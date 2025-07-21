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
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.service.SelectOtpService
import ir.saltech.puyakhan.data.util.div
import ir.saltech.puyakhan.data.util.minus
import ir.saltech.puyakhan.ui.view.component.adapter.OtpCodesViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val OTP_VIEWER_WINDOW = "OTP Viewer Window"

class SelectOtpWindow(private val context: Context) {
	private var wait: Int = 0
	private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
	private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
	private val layoutInflater =
		context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
	private val view = layoutInflater.inflate(R.layout.layout_window_select_otp, null)
	private lateinit var appSettings: App.Settings
	private var windowParams = WindowManager.LayoutParams(
		WindowManager.LayoutParams.WRAP_CONTENT,
		WindowManager.LayoutParams.WRAP_CONTENT,
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
		PixelFormat.TRANSLUCENT
	)
	private val otpCodes: MutableList<OtpCode>
		get() {
			val otpCodes = mutableStateListOf<OtpCode>()
			return otpCodes
		}

	init {
		setWindowParam()
		initValues()
		initViews()
		show()
	}

	private fun initValues() {
		loadAppSettings()
		// todo: then load new otp codes from OtpProcessor here.
	}

	private fun initViews() {
		view.findViewById<ImageButton>(R.id.close_otp_window).setOnClickListener { hide() }
		val otpCodesEmpty = view.findViewById<TextView>(R.id.otp_codes_empty)
		val otpCodesView = view.findViewById<RecyclerView>(R.id.otp_codes_view)
		val windowDragHandle = view.findViewById<CardView>(R.id.window_drag_handle)
		val windowParent = view.findViewById<ViewGroup>(R.id.select_otp_window_card)
		// todo: یه تابع hide بنویس برای حذف این پنجره و کد ها رو از خود کلاس بخون.
		if (otpCodes.isEmpty()) {
			otpCodesEmpty.visibility = View.VISIBLE
			otpCodesView.visibility = View.GONE
		} else {
			otpCodesEmpty.visibility = View.GONE
			otpCodesView.visibility = View.VISIBLE
		}
		setupOtpCodesViewer(otpCodes, otpCodesView)
		setupWindowDrag(windowDragHandle, windowParent)
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
						windowManager.updateViewLayout(parent, windowParams)
						appSettings.otpWindowPos =
							App.WindowPosition(windowParams.x, windowParams.y)
						saveAppSettings()
					} else {
						wait += 100
						Log.e("TAG", "Waiting for ... $wait")
					}
				}

				MotionEvent.ACTION_UP -> {
					handle.backgroundTintList = ContextCompat.getColorStateList(
						context, R.color.otpExpiredCardBackground
					)
					wait = 0
				}
			}
			true
		}
	}

	private fun setupOtpCodesViewer(otpCodes: List<OtpCode>, otpCodesView: RecyclerView) {
		val adapter = OtpCodesViewAdapter(otpCodes)
		otpCodesView.adapter = adapter
		otpCodesView.layoutManager =
			LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
	}

	private fun setWindowParam() {
		windowParams.title = "Select OTP Code"
		windowParams.gravity = Gravity.CENTER
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
			Log.e(OTP_VIEWER_WINDOW, e.toString())
		}
	}

	private fun hide() {
		try {
			(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(view)
			view.invalidate()
			(view.parent as ViewGroup).removeAllViews()
		} catch (e: Exception) {
			Log.e(OTP_VIEWER_WINDOW, e.toString())
		}
	}

	private fun loadAppSettings() {
		CoroutineScope(Dispatchers.IO).launch {
			appSettings = App.getSettings(context)
		}
	}

	private fun saveAppSettings() {
		CoroutineScope(Dispatchers.IO).launch {
			App.setSettings(context, appSettings)
		}
	}

	companion object {
		fun show(context: Context) {
			if (Settings.canDrawOverlays(context)) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					ContextCompat.startForegroundService(
						context, prepareIntentService(context)
					)
				} else {
					context.startService(prepareIntentService(context))
				}
			}
		}

		private fun prepareIntentService(context: Context): Intent =
			Intent(context, SelectOtpService::class.java)
	}
}
