package ir.saltech.puyakhan

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import ir.saltech.puyakhan.model.OtpSms
import ir.saltech.puyakhan.ui.theme.PermissionNeeded
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

private const val MAX_OTP_LENGTH = 9

lateinit var activity: ComponentActivity

class MainActivity : ComponentActivity() {
	private lateinit var analytics: FirebaseAnalytics
	// TODO: At the publish moment, uncomment this line and use it instead of onRequestPermissionsResult
	//private var requestPermissionLauncher: ActivityResultLauncher<String>

	init {
		activity = this
		// requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
		//				isGranted: Boolean ->
		//			if (isGranted) startProgram() else exitProcess(-1)
		//	}
	}

	private fun startProgram() {
		analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
			param(FirebaseAnalytics.Param.ITEM_ID, "Test")
			param(FirebaseAnalytics.Param.ITEM_NAME, "A Test")
			param(FirebaseAnalytics.Param.CONTENT_TYPE, "text")
		}
		setContent {
			PuyaKhanTheme {
				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
						checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
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
		analytics = Firebase.analytics
		startProgram()
	}

	@Deprecated("Deprecated but for live edit it's ok", replaceWith = ReplaceWith("requestPermissionLauncher.launch(android.Manifest.permission.READ_SMS)"))
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == 3093) {
			val isGranted = (grantResults.isNotEmpty() &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED)
			if (isGranted) startProgram() else exitProcess(-1)
		}
	}
}

@Composable
fun RequestPermission(launcher: ActivityResultLauncher<String>? = null) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
		activity.checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
	) {
		when {
			activity.shouldShowRequestPermissionRationale(android.Manifest.permission.READ_SMS) -> PermissionAlert()
			// else -> launcher.launch(android.Manifest.permission.READ_SMS)
			else -> activity.requestPermissions(arrayOf(android.Manifest.permission.READ_SMS), 3093)
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
					3093
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
//	Box (modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)) {
//
//	}
}

@Composable
fun PuyaKhanView(contentPadding: PaddingValues = PaddingValues(0.dp)) {
	val clipboardManager = LocalClipboardManager.current
	val smsList = getSmsList()
	Column (modifier = Modifier.safeDrawingPadding()) {
		Text(text="Showing ${smsList.size} sms")
		LazyColumn(modifier =  Modifier.weight(1f)) {
			items(smsList) { sms ->
				var otp by remember { mutableStateOf("") }
				val smsBody = sms.body.split("\n").reversed()
				for (line in smsBody) {
					if (line.contains("رمز") || line.contains("پویا")) {
						if (line.contains(":")) {
							val splits = line.split(":")
							otp = splits[splits.size - 1].trim()
							if (otp.length > 10 || !otp.isDigitsOnly()) otp = ""
							break
						} else {
							if (line.contains(" ")) {
								val splits = line.split(" ")
								otp = splits[splits.size - 1].trim()
								if (otp.length > 10 || !otp.isDigitsOnly()) otp = ""
								break
							} else {
								otp = line.trim()
								if (otp.length > 10 || !otp.isDigitsOnly()) otp = ""
								break
							}
						}
					}
				}
				Text("$otp", modifier = Modifier.selectable(true) {
					clipboardManager.setText(AnnotatedString("$otp"))
					Toast.makeText(activity, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
				})
				Text("")
				Text("sent on based ${sms.date}")
				Text("and now date is: ${getDateTime(System.currentTimeMillis().toString())}")
				Text("-----------------------------------")
			}
		}
		TextField(value = "", onValueChange = {  }, placeholder = { Text("Placeholder") }, modifier = Modifier
			.fillMaxWidth())
	}
}

fun getSmsList(): List<OtpSms> {
	val otpSmsList = mutableListOf<OtpSms>()
	val resolver = activity.contentResolver
	val cursor = resolver.query(
		android.provider.Telephony.Sms.Inbox.CONTENT_URI,
		arrayOf("body", "date"),
		"((body like \"%بلو%\") or (body like \"%بانک%\")) and ((body like \"%رمز%\") or (body like \"%پویا%\")) and (body like \"%مبلغ%\") and (not body like \"%کارمزد%\") ",
		null,
		null
	)
	cursor.use { c ->
		while (c!!.moveToNext()) {
			otpSmsList += OtpSms(
				c.getString(c.getColumnIndexOrThrow("body")),
				getDateTime(c.getString(c.getColumnIndexOrThrow("date")))
			)
		}
	}
	return otpSmsList
}

@Composable
fun HeroesApp() {
	Scaffold(modifier = Modifier.fillMaxSize(), topBar = { HeroesAppTopBar() }) {
		HeroesView(contentPadding = it)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroesAppTopBar(modifier: Modifier = Modifier) {
	CenterAlignedTopAppBar(
		title = {
			Text(
				text = stringResource(R.string.app_name),
				style = MaterialTheme.typography.displayLarge,
			)
		},
		modifier = modifier
	)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	PuyaKhanTheme {
		HeroesApp()
	}
}

private fun getDateTime(s: String): String {
	return try {
		val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH)
		val netDate = Date(s.toLong())
		sdf.format(netDate)
	} catch (e: Exception) {
		e.toString()
	}
}
