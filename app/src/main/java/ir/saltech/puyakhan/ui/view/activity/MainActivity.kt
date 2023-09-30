package ir.saltech.puyakhan.ui.view.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import ir.saltech.puyakhan.ui.manager.OtpSmsManager.Companion.getOtpFromSms
import ir.saltech.puyakhan.ui.manager.OtpSmsManager.Companion.getSmsList
import ir.saltech.puyakhan.ui.manager.getDateTime
import ir.saltech.puyakhan.ui.theme.PermissionNeeded
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import kotlin.system.exitProcess

private const val SMS_PERMISSION_REQUEST_CODE = 3093

lateinit var activity: ComponentActivity

internal const val NOTIFY_CHANNEL_ID = "otp_sms_codes"

class MainActivity : ComponentActivity() {
	// TODO: At the publish moment, uncomment this line and use it instead of onRequestPermissionsResult
	// private var requestPermissionLauncher: ActivityResultLauncher<String>

	init {
		activity = this
		// requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
		//				isGranted: Boolean ->
		//			if (isGranted) startProgram() else exitProcess(-1)
		//	}
	}

	private fun startProgram() {
		createNotificationChannel(this)
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
		if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
			val isGranted = (grantResults.isNotEmpty() &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED)
			if (isGranted) startProgram() else exitProcess(-1)
		}
	}

	private fun createNotificationChannel(context: Context) {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is not in the Support Library.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = "رمز های یکبار مصرف"
			val descriptionText = "دریافت رمز های یکبار مصرف"
			val importance = NotificationManager.IMPORTANCE_HIGH
			val channel = NotificationChannel(NOTIFY_CHANNEL_ID, name, importance).apply {
				description = descriptionText
				lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
			}
			try {
				// Register the channel with the system.
				val notificationManager: NotificationManager =
					context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
				notificationManager.createNotificationChannel(channel)
			} catch (e: Exception) {
				Log.e("SmsReceiver", "Exception smsReceiver: $e")
			}
		}
	}
}

@Composable
fun RequestPermission(launcher: ActivityResultLauncher<String>? = null) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
		(activity.checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
				|| activity.checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
	) {
		when {
			activity.shouldShowRequestPermissionRationale(android.Manifest.permission.READ_SMS) -> PermissionAlert()
			// else -> launcher.launch(android.Manifest.permission.READ_SMS)
			else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				activity.requestPermissions(
					arrayOf(
						android.Manifest.permission.READ_SMS,
						android.Manifest.permission.RECEIVE_SMS,
						android.Manifest.permission.POST_NOTIFICATIONS
					),
					SMS_PERMISSION_REQUEST_CODE
				)
			} else {
				activity.requestPermissions(
					arrayOf(
						android.Manifest.permission.READ_SMS,
						android.Manifest.permission.RECEIVE_SMS
					),
					SMS_PERMISSION_REQUEST_CODE
				)
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PermissionAlert(launcher: ActivityResultLauncher<String>? = null) {
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
				activity.requestPermissions(
					arrayOf(android.Manifest.permission.READ_SMS),
					SMS_PERMISSION_REQUEST_CODE
				)
			}) {
				Text(text = "OK")
			}
		},
	)
}

@Composable
fun PuyaKhanApp() {
	Scaffold {
		PuyaKhanView(it)
	}
}

@Composable
fun PuyaKhanView(contentPadding: PaddingValues = PaddingValues(0.dp)) {
	val clipboardManager = LocalClipboardManager.current
	val smsList = getSmsList()
	Column(modifier = Modifier.safeDrawingPadding()) {
		Text(text = "Showing ${smsList.size} sms")
		LazyColumn(modifier = Modifier.weight(1f), contentPadding = contentPadding) {
			items(smsList) { sms ->
				val (otp, bank) = getOtpFromSms(sms, true)!!
				Text("$otp - From $bank", modifier = Modifier.selectable(true) {
					clipboardManager.setText(AnnotatedString(otp))
					Toast.makeText(activity, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
				})
				Text("")
				Text("sent on based ${sms.date}")
				Text("and now date is: ${getDateTime(System.currentTimeMillis().toString())}")
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
