package ir.saltech.puyakhan.ui.view.window

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.service.SelectOtpService
import ir.saltech.puyakhan.data.util.MAX_OTP_SMS_EXPIRATION_TIME
import ir.saltech.puyakhan.data.util.div
import ir.saltech.puyakhan.data.util.minus
import ir.saltech.puyakhan.data.util.past
import ir.saltech.puyakhan.data.util.repeatWhile
import ir.saltech.puyakhan.data.util.runOnUiThread
import ir.saltech.puyakhan.ui.view.component.adapter.OtpCodesViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "OTP Viewer Window"

class SelectOtpWindow private constructor(
	private val context: Context,
	private val appSettings: App.Settings,
) {
	private var afterMove: Boolean = false
	private var wait: Int = 0
	private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
	private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
	@SuppressLint("InflateParams")
	private val view = LayoutInflater.from(context)
		.inflate(R.layout.layout_window_select_otp, null)
	private var windowParams = WindowManager.LayoutParams(
		WindowManager.LayoutParams.WRAP_CONTENT,
		WindowManager.LayoutParams.WRAP_CONTENT,
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
		PixelFormat.TRANSLUCENT
	)
	private val windowScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	private var otpCodes: MutableList<OtpCode> = mutableStateListOf()
	private var otpCodesView: RecyclerView? = null
	private var otpCodesViewAdapter: OtpCodesViewAdapter? = null
	private var otpCodesEmptyView: TextView? = null
	private var isClosedManual: Boolean = false

	init {
		setWindowParam()
		initViews()
		show()
	}

	private fun initViews() {
		val windowParent = view.findViewById<ViewGroup>(R.id.select_otp_window_card)
		val windowDragHandle = view.findViewById<CardView>(R.id.window_drag_handle)
		val closeButton = view.findViewById<ImageButton>(R.id.close_otp_window)
		otpCodesEmptyView = view.findViewById(R.id.otp_codes_empty)
		otpCodesView = view.findViewById(R.id.otp_codes_view)

		closeButton.setOnClickListener {
			isClosedManual = true
			hide(context, windowManager, view)
		}
		setupWindowDrag(windowDragHandle, windowParent)
	}

	private fun setEmptyView() {
		if (otpCodes.isEmpty()) {
			otpCodesEmptyView?.visibility = View.VISIBLE
			otpCodesView?.visibility = View.GONE
		} else {
			otpCodesEmptyView?.visibility = View.GONE
			otpCodesView?.visibility = View.VISIBLE
		}
	}

	private fun setupWindowLocation(view: View) {
		val windowPosition = appSettings.otpWindowPos
		if (windowPosition != null) {
			windowParams.x = windowPosition.x
			windowParams.y = windowPosition.y
			windowManager.updateViewLayout(view, windowParams)
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private fun setupWindowDrag(handle: View, parent: View) {
		handle.setOnTouchListener { _, e ->
			when (e.action) {
				MotionEvent.ACTION_MOVE -> {
					if (wait >= 1000) {
						if (wait == 1000) {
							vibrator.vibrate(50)
							handle.backgroundTintList = ContextCompat.getColorStateList(
								context, R.color.colorAccent
							)
							wait++
						}
						windowParams.x = (e.rawX - (parent.measuredWidth / 1.25.dp)).roundToInt()
						windowParams.y = (e.rawY - (parent.measuredHeight * 2.25.dp)).roundToInt()
						windowManager.updateViewLayout(view, windowParams)
						afterMove = true
					} else {
						wait += 100
					}
				}

				MotionEvent.ACTION_UP -> {
					if (afterMove) {
						appSettings.otpWindowPos =
							App.WindowPosition(windowParams.x, windowParams.y)
						saveAppSettings()
					}
					handle.backgroundTintList = ContextCompat.getColorStateList(
						context, R.color.otpExpiredCardBackground
					)
					wait = 0
				}
			}
			true
		}
	}

	private fun setOtpCodesAdapter(otpCodes: MutableList<OtpCode>) {
		otpCodesViewAdapter = OtpCodesViewAdapter(otpCodes.asReversed())
		otpCodesView?.layoutManager =
			LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
		otpCodesView?.adapter = otpCodesViewAdapter
		setEmptyView()
	}

	private fun setOtpCodeElapseTimer() {
		windowScope.launch {
			repeatWhile(isActive) {
				runOnUiThread {
					if (isClosedManual) {
						isClosedManual = false
						cancel("Window Closed manually; so Countdown must be canceled")
					}
					updateOtpCountdown {
						hide(context, windowManager, view)
						cancel("OtpCodes cleaned; so Countdown must be canceled")
					}
				}
				delay(1000)
			}
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun updateOtpCountdown(onCanceled: () -> Unit) {
		val currentTime = System.currentTimeMillis()
		otpCodes.apply {
			forEachIndexed { index, otp ->
				otp.elapsedTime = currentTime past otp.sentTime
			}
			if (all { code -> code.elapsedTime >= MAX_OTP_SMS_EXPIRATION_TIME }) {
				clear()
				onCanceled()
			}
		}
		if (otpCodesViewAdapter != null) {
			otpCodesViewAdapter!!.notifyDataSetChanged()
		}
	}

	private fun setWindowParam() {
		windowParams.title = context.getString(R.string.select_otp_code_window_title)
		windowParams.gravity = Gravity.CENTER
	}

	fun setOtpCodes(newOtpCodes: MutableList<OtpCode>) {
		otpCodes = newOtpCodes
		setOtpCodesAdapter(otpCodes)
		setOtpCodeElapseTimer()
	}

	private fun show() {
		try {
			if (view.windowToken == null) {
				if (view.parent == null) {
					try {
						windowManager.removeView(view)
					} catch (ex: Exception) {
						ex.printStackTrace()
					} catch (er: Error) {
						er.printStackTrace()
					} finally {
						windowManager.addView(view, windowParams)
						setupWindowLocation(view)
					}
				}
			}
		} catch (e: Exception) {
			Log.e(TAG, e.toString())
		}
	}

	private fun saveAppSettings() {
		CoroutineScope(Dispatchers.IO).launch {
			App.setSettings(context, appSettings)
		}
	}

	companion object {
		const val APP_SETTINGS_KEY = "app_settings"

		@Volatile @SuppressLint("StaticFieldLeak")
		private var instance: SelectOtpWindow? = null

		fun getInstance(context: Context, appSettings: App.Settings): SelectOtpWindow {
			if (instance == null) {
				synchronized(this) {
					if (instance == null)
						instance = SelectOtpWindow(context, appSettings)
				}
			}
			return instance!!
		}

		fun show(context: Context, appSettings: App.Settings) {
			if (Settings.canDrawOverlays(context)) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					ContextCompat.startForegroundService(
						context, prepareIntentService(context, appSettings)
					)
				} else {
					context.startService(prepareIntentService(context, appSettings))
				}
			}
		}

		private fun hide(context: Context, windowManager: WindowManager, view: View) {
			try {
				windowManager.removeView(view)
			} catch (e: Exception) {
				Log.e(TAG, e.toString())
			} finally {
				instance?.windowScope?.cancel()
				instance = null
				context.stopService(
					Intent(
						context, SelectOtpService::class.java
					)
				)
			}
		}

		private fun prepareIntentService(
			context: Context,
			appSettings: App.Settings,
		): Intent = Intent(context, SelectOtpService::class.java).apply {
			putExtra(APP_SETTINGS_KEY, appSettings)
		}

	}
}
