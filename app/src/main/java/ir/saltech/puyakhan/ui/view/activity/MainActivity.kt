package ir.saltech.puyakhan.ui.view.activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.ui.theme.PermissionNeeded
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.view.components.compose.SettingsView
import ir.saltech.puyakhan.ui.view.components.manager.OtpManager.Companion.getCodeList
import ir.saltech.puyakhan.ui.view.components.manager.getDateTime
import kotlin.system.exitProcess


private const val SMS_PERMISSIONS_REQUEST_CODE = 3093

internal const val NOTIFY_OTP_CHANNEL_ID = "ir.saltech.puyakhan.OTP_SMS_CODES"
internal const val NOTIFY_SERVICE_CHANNEL_ID = "ir.saltech.puyakhan.BACKGROUND_SERVICES"

class MainActivity : ComponentActivity() {
	// TODO: At the publish moment, uncomment this line and use it instead of onRequestPermissionsResult
	// private var requestPermissionLauncher: ActivityResultLauncher<String>

	init {
		// requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
		//				isGranted: Boolean ->
		//			if (isGranted) startProgram() else exitProcess(-1)
		//	}
	}

	private fun startProgram() {
		setContent {
			PuyaKhanTheme {
				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
						(checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
								|| checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
					) {
						RequestPermission()
					} else {
						PuyaKhanApp()
					}
				}
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		grantScreenOverlayPermission()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			createOtpNotifyChannel()
			createServicesNotifyChannel()
		}
		val appSettings = App.getSettings(this)
		disableBatteryLimitations()
		startProgram()
	}

	@Deprecated(
		"Deprecated but for live edit it's ok",
		replaceWith = ReplaceWith("requestPermissionLauncher.launch(android.Manifest.permission.READ_SMS)")
	)
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == SMS_PERMISSIONS_REQUEST_CODE) {
			val isGranted = (grantResults.isNotEmpty() &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED)
			if (isGranted) startProgram() else exitProcess(-1)
		}
	}

	@RequiresApi(Build.VERSION_CODES.M)
	@Composable
	fun RequestPermission(launcher: ActivityResultLauncher<String>? = null) {
		when {
			shouldShowRequestPermissionRationale(android.Manifest.permission.READ_SMS) -> PermissionAlert()
			// else -> launcher.launch(android.Manifest.permission.READ_SMS)
			else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				requestPermissions(
					arrayOf(
						android.Manifest.permission.READ_SMS,
						android.Manifest.permission.RECEIVE_SMS,
						android.Manifest.permission.POST_NOTIFICATIONS
					),
					SMS_PERMISSIONS_REQUEST_CODE
				)
			} else {
				requestPermissions(
					arrayOf(
						android.Manifest.permission.READ_SMS,
						android.Manifest.permission.RECEIVE_SMS
					),
					SMS_PERMISSIONS_REQUEST_CODE
				)
			}
		}
	}

	@RequiresApi(Build.VERSION_CODES.M)
	@Composable
	fun PermissionAlert() {
		AlertDialog(
			icon = {
				Icon(
					imageVector = PermissionNeeded(),
					contentDescription = "Permission Needed Icon"
				)
			},
			onDismissRequest = {
				// Do nothing
			},
			title = { Text(text = "Permission Required") },
			text = { Text(text = "This app requires access to your SMS messages.") },
			confirmButton = {
				TextButton(onClick = {
					requestPermissions(
						arrayOf(android.Manifest.permission.READ_SMS),
						SMS_PERMISSIONS_REQUEST_CODE
					)
				}) {
					Text(text = "OK")
				}
			},
		)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun createOtpNotifyChannel() {
		val name = "اعلان رمز یکبار مصرف"
		val descriptionText = "هنگامی که رمز یکبار مصرف دریافت شد، اعلان آن نمایش داده می شود."
		val importance = NotificationManager.IMPORTANCE_HIGH
		val channel = NotificationChannel(NOTIFY_OTP_CHANNEL_ID, name, importance).apply {
			description = descriptionText
			lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
		}
		(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
			.createNotificationChannel(channel)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun createServicesNotifyChannel() {
		val name = "نمایش رمز یکبار مصرف"
		val descriptionText =
			"هنگامی که رمز یکبار مصرف دریافت شد، اعلان آن به صورت پنجره، نمایش داده می شود."
		val importance = NotificationManager.IMPORTANCE_LOW
		val channel = NotificationChannel(NOTIFY_SERVICE_CHANNEL_ID, name, importance).apply {
			description = descriptionText
			lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
		}
		(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
			.createNotificationChannel(channel)
	}

	private fun grantScreenOverlayPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (!Settings.canDrawOverlays(this)) {
				startActivityForResult(
					Intent(
						Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
						Uri.parse("package:$packageName")
					), 9583
				)
			}
		}
	}

	@SuppressLint("BatteryLife")
	private fun disableBatteryLimitations() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val intent = Intent()
			val packageName = packageName
			val pm = getSystemService(POWER_SERVICE) as PowerManager
			if (!pm.isIgnoringBatteryOptimizations(packageName)) {
				intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
				intent.setData(Uri.parse("package:$packageName"))
				startActivity(intent)
			}
		}
	}
}

@Composable
fun PuyaKhanApp() {
	Scaffold {
		//PuyaKhanView(it)
		SettingsView()
	}
}

@Composable
fun PuyaKhanView(contentPadding: PaddingValues = PaddingValues(0.dp)) {
	val clipboardManager = LocalClipboardManager.current
	val context = LocalContext.current
	val codeList = getCodeList(context)
	Column(modifier = Modifier.safeDrawingPadding()) {
		Text(text = "Showing ${codeList.size} sms")
		LazyColumn(modifier = Modifier.weight(1f), contentPadding = contentPadding) {
			items(codeList) { (otp, bank, expire) ->
				Text("$otp - From $bank", modifier = Modifier.selectable(true) {
					clipboardManager.setText(AnnotatedString(otp))
					Toast.makeText(
						context,
						context.getString(R.string.otp_copied_to_clipboard),
						Toast.LENGTH_SHORT
					).show()
				})
				Text("")
				Text("sent on based ${expire}")
				Text("and now date is: ${getDateTime(System.currentTimeMillis())}")
				Text("-----------------------------------")
			}
		}
		TextField(
			value = "",
			onValueChange = { },
			placeholder = { Text("Placeholder") },
			modifier = Modifier
				.fillMaxWidth()
		)
	}
}

@Preview(showBackground = true)
@Composable
fun OverallPreview() {
	PuyaKhanTheme {
		PuyaKhanApp()
	}
}
