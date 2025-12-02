package ir.saltech.puyakhan.ui.view.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.ApplicationLoader
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.service.KeepAliveService
import ir.saltech.puyakhan.data.service.LocalServerService
import ir.saltech.puyakhan.data.util.XiaomiUtilities
import ir.saltech.puyakhan.data.util.grantXiaomiPermissions
import ir.saltech.puyakhan.data.util.startKeepAliveService
import ir.saltech.puyakhan.data.util.startLocalServerService
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.view.component.compose.LockedDirection
import ir.saltech.puyakhan.ui.view.component.compose.PermissionRationale
import ir.saltech.puyakhan.ui.view.page.LocalServerView
import ir.saltech.puyakhan.ui.view.page.MainView
import ir.saltech.puyakhan.ui.view.page.SettingsView
import kotlin.system.exitProcess


internal const val OVERLAY_PERMISSIONS_REQUEST_CODE = 3093
internal const val NOTIFY_OTP_CHANNEL_ID = "ir.saltech.puyakhan.OTP_SMS_CODES"
internal const val NOTIFY_SERVICE_CHANNEL_ID = "ir.saltech.puyakhan.BACKGROUND_SERVICES"

internal lateinit var activity: ComponentActivity
internal lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
internal lateinit var appInfoLauncher: ActivityResultLauncher<Intent>

internal val keepAliveServiceIntent = Intent(
	ApplicationLoader.applicationContext,
	KeepAliveService::class.java
)

internal val localServerServiceIntent = Intent(
    ApplicationLoader.applicationContext,
    LocalServerService::class.java
)

internal class MainActivity : ComponentActivity() {
	private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
		Manifest.permission.RECEIVE_SMS, Manifest.permission.POST_NOTIFICATIONS
	)
	else arrayOf(
		Manifest.permission.RECEIVE_SMS
	)

	init {
		activity = this
		permissionLauncher =
			registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
				if (it.values.all { granted -> granted }) {
					Toast.makeText(
						this,
						getString(R.string.xiaomi_back_service_permissions), Toast.LENGTH_SHORT
					).show()
					grantXiaomiPermissions(this)
					startProgram()
				} else {
					Toast.makeText(this,
						getString(R.string.app_permissions_needed), Toast.LENGTH_LONG).show()
					exitProcess(-1)
				}
			}
		appInfoLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
				Log.i("PuyaKhanActivity", "App Info Intent -> Launched")
			}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		window.setFlags(
			WindowManager.LayoutParams.FLAG_SECURE,
			WindowManager.LayoutParams.FLAG_SECURE
		)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			createOtpNotifyChannel()
			createServicesNotifyChannel()
		}
		try {
            startLocalServerService()
			stopService(keepAliveServiceIntent)
		} catch (_: Exception) {
		}
		with(NotificationManagerCompat.from(this)) {
			cancelAll()
		}
		startProgram()
	}

	private fun startProgram() {
		setContent {
			PuyaKhanTheme {
				LockedDirection {
					Surface(
						modifier = Modifier.fillMaxSize(),
						color = MaterialTheme.colorScheme.background
					) {
						if (checkAppPermissions()) {
							Launcher()
						} else {
							RequestPermission()
						}
					}
				}
			}
		}
	}

	@Composable
	private fun RequestPermission() {
		when {
			needsAppPermissionsRational() -> PermissionRationale(
				stringResource(R.string.app_permission_title),
				stringResource(R.string.app_permission_message),
				onConfirm = {
					appInfoLauncher.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
						setData(Uri.fromParts("package", activity.packageName, null))
					})
				})

			else -> requestAppPermissions()
		}
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun createOtpNotifyChannel() {
		val name = getString(R.string.otp_notify_channel_title)
		val descriptionText = getString(R.string.otp_notify_channel_desc)
		val importance = NotificationManager.IMPORTANCE_HIGH
		val channel = NotificationChannel(NOTIFY_OTP_CHANNEL_ID, name, importance).apply {
			description = descriptionText
			lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
		}
		with(NotificationManagerCompat.from(this)) {
			createNotificationChannel(channel)
		}
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun createServicesNotifyChannel() {
		val name = getString(R.string.overlay_window_alert_title)
		val descriptionText = getString(R.string.overlay_window_alert_subtitle)
		val importance = NotificationManager.IMPORTANCE_LOW
		val channel = NotificationChannel(NOTIFY_SERVICE_CHANNEL_ID, name, importance).apply {
			description = descriptionText
			lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
		}
		with(NotificationManagerCompat.from(this)) {
			createNotificationChannel(channel)
		}
	}

	private fun checkAppPermissions(): Boolean {
		for (permission in permissions) {
			if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) return false
		}
		return true
	}

	private fun needsAppPermissionsRational(): Boolean {
		for (permission in permissions) {
			if (shouldShowRequestPermissionRationale(permission)) return true
		}
		return false
	}

	private fun requestAppPermissions() {
		permissionLauncher.launch(permissions)
	}

	override fun onResume() {
		super.onResume()
		ApplicationLoader.isActivityLaunched = true
		try {
			stopService(keepAliveServiceIntent)
		} catch (_: Exception) {
		}
		if (!(XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_BOOT_COMPLETED) &&
					XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_SERVICE_FOREGROUND))) {
			grantXiaomiPermissions(this)
		}
	}

	override fun onPause() {
		super.onPause()
		ApplicationLoader.isActivityLaunched = false
		Log.i("TAG", "On Keep Alive Service wanted -> main activity")
        ApplicationLoader.applicationContext.startKeepAliveService()
	}
}

@Composable
private fun Launcher() {
	var page by remember {
		mutableStateOf(App.Page.Main)
	}
	AnimatedVisibility(visible = page == App.Page.Main) {
		MainView { page = it }
	}
	AnimatedVisibility(visible = page == App.Page.Settings) {
		SettingsView { page = it }
	}
    AnimatedVisibility(visible = page == App.Page.LocalServer) {
        LocalServerView { page = it }
    }
}


@Preview(showBackground = true)
@Composable
private fun OverallPreview() {
	PuyaKhanTheme {
		Launcher()
	}
}
