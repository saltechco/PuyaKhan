@file:OptIn(ExperimentalMaterial3Api::class)

package ir.saltech.puyakhan.ui.view.page

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.App.PresentMethod
import ir.saltech.puyakhan.data.util.LockedDirection
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.theme.Symbols
import ir.saltech.puyakhan.ui.view.activity.OVERLAY_PERMISSIONS_REQUEST_CODE
import ir.saltech.puyakhan.ui.view.activity.activity
import ir.saltech.puyakhan.ui.view.activity.permissionLauncher
import ir.saltech.puyakhan.ui.view.component.compose.MinimalHelpText
import ir.saltech.puyakhan.ui.view.component.compose.OpenReferenceButton
import ir.saltech.puyakhan.ui.view.component.compose.SegmentedButtonOrder

@Composable
fun SettingsView(onPageChanged: (App.Page) -> Unit) {
	BackHandler(true) {
		onPageChanged(App.Page.Main)
	}
	Scaffold(topBar = { SettingsTopBar() { onPageChanged(it) } }) {
		SettingsContent(it)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(onPageChanged: (App.Page) -> Unit) {
	CenterAlignedTopAppBar(
		title = {
			Text(
				"تنظیمات",
				style = MaterialTheme.typography.displayMedium
			)
		},
		navigationIcon = {
			Row {
				Spacer(modifier = Modifier.width(16.dp))
				Icon(
					modifier = Modifier
						.size(26.dp)
						.align(Alignment.Bottom)
						.clickable { onPageChanged(App.Page.Main) },
					imageVector = Symbols.Back,
					contentDescription = "Back to the main page"
				)
				Spacer(modifier = Modifier.width(16.dp))
			}
		}
	)
}

@Composable
fun SettingsContent(paddingValues: PaddingValues = PaddingValues(0.dp)) {
	val context = LocalContext.current
	val appSettings = App.getSettings(context)
	LazyColumn(
		modifier = Modifier
			.padding(paddingValues)
			.scrollable(
				state = rememberScrollableState { delta ->
					delta
				},
				orientation = Orientation.Vertical
			)
	) {
		items(1) {
			MethodSelection(context, appSettings)
			Spacer(modifier = Modifier.height(4.dp))
			ExpireTimeSelection(context, appSettings)
			Spacer(modifier = Modifier.height(44.dp))
			Text(
				"تنظیمات ارجاعی",
				style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.ContentOrRtl),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 26.dp)
					.alpha(0.75f)
			)
			Spacer(modifier = Modifier.height(5.dp))
			GrantNotificationPermission(context)
			GrantWindowOverlayPermission(context)
			AllowBatteryOptimization(context)
			Spacer(modifier = Modifier.height(16.dp))
			if (Build.MANUFACTURER == "Xiaomi")
				XiaomiUsingWithCaution()
			SomeUsefulHelps()
			Spacer(modifier = Modifier.height(16.dp))
		}
	}
}

@Composable
fun SomeUsefulHelps() {
	MinimalHelpText(text = "زمانی از غیرفعال کردن محدودیت\u200cهای باتری استفاده کنید که برنامه به خوبی در همه مواقع کار نمی کند.\nسعی کنید حتی الامکان از این کار پرهیز کنید!")
	// Spacer(modifier = Modifier.height(8.dp))
	MinimalHelpText(text = "در صورت غیر فعال بودن بعضی از گزینه های نحوه نمایش، مجوز های آنها را اعطا کنید تا فعال شوند.")
}

@Composable
fun XiaomiUsingWithCaution() {
	MinimalHelpText(text = "شما از دستگاه شیائومی استفاده می کنید که ممکن است استفاده از ویژگی کپی کردن رمز پویا به صورت مستقیم، به درستی کار نکند.")
}

@Composable
fun GrantWindowOverlayPermission(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		OpenReferenceButton(
			title = "اعطای مجوز نمایش پنجره",
			contentDescription = "Grant Show Small Window permission"
		) {
			grantScreenOverlayPermission(context)
		}
	}
}

@Composable
fun GrantNotificationPermission(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		OpenReferenceButton(
			title = "اعطای مجوز دسترسی به اعلان",
			contentDescription = "Grant Post notification permission"
		) {
			grantNotificationPermission(context)
		}
	}
}

@Composable
fun AllowBatteryOptimization(context: Context) {
	OpenReferenceButton(
		title = "غیرفعال کردن محدودیت\u200Cهای باتری",
		contentDescription = "Disable Battery Limitations"
	) {
		disableBatteryLimitations(context)
	}
}

@Composable
fun ExpireTimeSelection(context: Context, appSettings: App.Settings) {
	var expireTime by remember {
		mutableLongStateOf(appSettings.expireTime)
	}
	Column(
		modifier = Modifier
			.padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
			.clip(
				RoundedCornerShape(
					topEnd = 8.dp, topStart = 8.dp, bottomEnd = 25.dp, bottomStart = 25.dp
				)
			)
			.background(MaterialTheme.colorScheme.surfaceVariant)
	) {
		Row(
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth()
		) {
			OutlinedTextField(
				value = if (expireTime >= 1) (expireTime / 60_000).toString() else "",
				onValueChange = { time ->
					if (time.isEmpty()) {
						expireTime = 0
						return@OutlinedTextField
					}
					if (time.toIntOrNull() in 1..3) {
						expireTime = time.toLong() * 60_000
						appSettings.expireTime = expireTime
						App.setSettings(context, appSettings)
					}
				},
				placeholder = {
					Text(
						"0",
						textAlign = TextAlign.Center,
						modifier = Modifier.fillMaxWidth()
					)
				},
				singleLine = true,
				textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
				shape = RoundedCornerShape(15.dp),
				modifier = Modifier
					.weight(1.2f),
				prefix = { Text("دقیقه", modifier = Modifier.padding(end = 8.dp)) }
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				":مدت انقضای رمز",
				textAlign = TextAlign.Center,
				modifier = Modifier
					.weight(1f)
					.align(Alignment.CenterVertically)
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethodSelection(context: Context, appSettings: App.Settings) {
	var preferredMethod by remember {
		mutableStateOf(appSettings.presentMethods)
	}
	Column(
		modifier = Modifier
			.padding(start = 8.dp, end = 8.dp, top = 8.dp)
			.clip(
				RoundedCornerShape(
					topEnd = 25.dp, topStart = 25.dp, bottomEnd = 8.dp, bottomStart = 8.dp
				)
			)
			.background(MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
			Text(
				modifier = Modifier.fillMaxWidth(),
				text = "نحوه دسترسی به رمز پویا جدید",
				style = MaterialTheme.typography.bodyLarge,
				textAlign = TextAlign.Center
			)
			Spacer(modifier = Modifier.height(16.dp))
			MultiChoiceSegmentedButtonRow(modifier = Modifier.align(Alignment.CenterHorizontally)) {
				SegmentedButton(
					checked = preferredMethod.contains(PresentMethod.Otp.Copy),
					onCheckedChange = {
						preferredMethod =
							if (it) preferredMethod.plus(PresentMethod.Otp.Copy) else preferredMethod.minus(
								PresentMethod.Otp.Copy
							)
						updatePreferredMethods(appSettings, preferredMethod, context)
					},
					shape = SegmentedButtonOrder.First,
					//icon = { Icon(imageVector = Symbols.CopyCode, contentDescription = "Copy code method") },
				) {
					Text(text = "کپی")
				}
				SegmentedButton(
					enabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) (checkSelfPermission(
						context,
						android.Manifest.permission.POST_NOTIFICATIONS
					) == PackageManager.PERMISSION_GRANTED) else true,
					checked = preferredMethod.contains(PresentMethod.Otp.Notify),
					onCheckedChange = {
						preferredMethod =
							if (it) preferredMethod.plus(PresentMethod.Otp.Notify) else preferredMethod.minus(
								PresentMethod.Otp.Notify
							)
						updatePreferredMethods(appSettings, preferredMethod, context)
					},
					//icon = { Icon(imageVector = Symbols.NotifyCode, contentDescription = "Notification code method") },
					shape = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) SegmentedButtonOrder.Middle else SegmentedButtonOrder.Last
				) {
					Text(text = "اعلان")
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					SegmentedButton(
						enabled = Settings.canDrawOverlays(context),
						checked = preferredMethod.contains(PresentMethod.Otp.Select),
						onCheckedChange = {
							preferredMethod =
								if (it) preferredMethod.plus(PresentMethod.Otp.Select) else preferredMethod.minus(
									PresentMethod.Otp.Select
								)
							updatePreferredMethods(appSettings, preferredMethod, context)
						},
						shape = SegmentedButtonOrder.Last,
						//icon = { Icon(imageVector = Symbols.ShowCode, contentDescription = "Show a window for code method") },
					) {
						Text(text = "نمایش")
					}
				}
			}
		}
	}
}

private fun updatePreferredMethods(
	appSettings: App.Settings,
	preferredMethod: Set<String>,
	context: Context
) {
	appSettings.presentMethods = preferredMethod
	App.setSettings(context, appSettings)
}

private fun grantScreenOverlayPermission(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		if (!Settings.canDrawOverlays(context)) {
			startActivityForResult(
				activity,
				Intent(
					Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					Uri.parse("package:" + context.applicationContext.packageName)
				), OVERLAY_PERMISSIONS_REQUEST_CODE, null
			)

		} else {
			Toast.makeText(context, "این مجوز قبلاً اخذ گردیده است.", Toast.LENGTH_SHORT).show()
		}
	} else {
		Toast.makeText(
			context,
			"نیازی به اخذ این مجوز، برای دستگاه شما نمی باشد.",
			Toast.LENGTH_SHORT
		).show()
	}
}

private fun grantNotificationPermission(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		if (checkSelfPermission(
				context,
				android.Manifest.permission.POST_NOTIFICATIONS
			) != PackageManager.PERMISSION_GRANTED
		) {
			permissionLauncher.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))
		} else {
			Toast.makeText(context, "این مجوز قبلاً اخذ گردیده است.", Toast.LENGTH_SHORT).show()
		}
	} else {
		Toast.makeText(
			context,
			"نیازی به اخذ این مجوز، برای دستگاه شما نمی باشد.",
			Toast.LENGTH_SHORT
		).show()
	}
}

@SuppressLint("BatteryLife")
private fun disableBatteryLimitations(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		val intent = Intent()
		val packageName = context.packageName
		val pm = context.getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
		if (!pm.isIgnoringBatteryOptimizations(packageName)) {
			intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
			intent.setData(Uri.parse("package:$packageName"))
			startActivity(context, intent, null)
		} else {
			Toast.makeText(
				context,
				"محدودیت\u200Cهای باتری قبلاً خاموش شده اند!",
				Toast.LENGTH_SHORT
			).show()
		}
	} else {
		Toast.makeText(context, "این دستگاه از این قابلیت پشتیبانی نمی‌کند.", Toast.LENGTH_SHORT)
			.show()
	}
}

@Preview(
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun SettingsPreview() {
	PuyaKhanTheme {
		SettingsView {}
	}
}
