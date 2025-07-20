@file:OptIn(ExperimentalMaterial3Api::class)

package ir.saltech.puyakhan.ui.view.page

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toUri
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.App.PresentMethod
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.theme.Symbols
import ir.saltech.puyakhan.ui.view.activity.OVERLAY_PERMISSIONS_REQUEST_CODE
import ir.saltech.puyakhan.ui.view.activity.activity
import ir.saltech.puyakhan.ui.view.activity.permissionLauncher
import ir.saltech.puyakhan.ui.view.component.compose.MinimalHelpText
import ir.saltech.puyakhan.ui.view.component.compose.OpenReferenceButton
import ir.saltech.puyakhan.ui.view.component.compose.SegmentedButtonOrder

@Composable
internal fun SettingsView(onPageChanged: (App.Page) -> Unit) {
	BackHandler() {
		onPageChanged(App.Page.Main)
	}
	Scaffold(topBar = { SettingsTopBar { onPageChanged(it) } }) {
		SettingsContent(it)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onPageChanged: (App.Page) -> Unit) {
	CenterAlignedTopAppBar(title = {
		Text(
			stringResource(R.string.settings_view_title),
			style = MaterialTheme.typography.displayMedium
		)
	}, navigationIcon = {
		Row {
			Spacer(modifier = Modifier.width(16.dp))
			Icon(
				modifier = Modifier
					.size(26.dp)
					.align(Alignment.Bottom)
					.clickable { onPageChanged(App.Page.Main) },
				imageVector = Symbols.Default.Back,
				contentDescription = stringResource(R.string.back_to_the_main_page_cd)
			)
			Spacer(modifier = Modifier.width(16.dp))
		}
	})
}

@Composable
private fun SettingsContent(paddingValues: PaddingValues = PaddingValues(0.dp)) {
	val context = LocalContext.current
	val appSettings = App.getSettings(context)
	LazyColumn(
		modifier = Modifier
			.padding(paddingValues)
			.scrollable(
				state = rememberScrollableState { delta ->
					delta
				}, orientation = Orientation.Vertical
			)
	) {
		items(1) {
			MethodSelection(context, appSettings)
			Spacer(modifier = Modifier.height(4.dp))
			ExpireTimeSelection(context, appSettings)
			Spacer(modifier = Modifier.height(44.dp))
			Text(
				stringResource(R.string.referenced_settings),
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
			if (Build.MANUFACTURER == "Xiaomi") XiaomiUsingWithCaution()
			SomeUsefulHelps()
			Spacer(modifier = Modifier.height(16.dp))
		}
	}
}

@Composable
private fun SomeUsefulHelps() {
	MinimalHelpText(text = stringResource(R.string.disable_battery_limitations_help_text))
	MinimalHelpText(text = stringResource(R.string.disabled_buttons_help_text))
}

@Composable
private fun XiaomiUsingWithCaution() {
	MinimalHelpText(text = stringResource(R.string.xiaomi_device_assertion))
}

@Composable
private fun GrantWindowOverlayPermission(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		OpenReferenceButton(
			title = stringResource(R.string.overlay_window_permission_button),
			contentDescription = stringResource(R.string.overlay_window_permission_cd)
		) {
			grantScreenOverlayPermission(context)
		}
	}
}

@Composable
private fun GrantNotificationPermission(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		OpenReferenceButton(
			title = stringResource(R.string.notification_permission_request_button),
			contentDescription = stringResource(R.string.notification_permission_request_cd)
		) {
			grantNotificationPermission(context)
		}
	}
}

@Composable
private fun AllowBatteryOptimization(context: Context) {
	OpenReferenceButton(
		title = stringResource(R.string.disable_battery_limitations_button),
		contentDescription = stringResource(R.string.disable_battery_limitations_cd)
	) {
		disableBatteryLimitations(context)
	}
}

@Composable
private fun ExpireTimeSelection(context: Context, appSettings: App.Settings) {
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
						"0", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
					)
				},
				singleLine = true,
				textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
				shape = RoundedCornerShape(15.dp),
				modifier = Modifier.weight(1.2f),
				prefix = {
					Text(
						stringResource(R.string.code_expiration_time_unit),
						modifier = Modifier.padding(end = 8.dp)
					)
				})
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				stringResource(R.string.code_expiration_time_title),
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
private fun MethodSelection(context: Context, appSettings: App.Settings) {
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
				text = stringResource(R.string.otp_code_present_method),
				style = MaterialTheme.typography.bodyLarge,
				textAlign = TextAlign.Center
			)
			Spacer(modifier = Modifier.height(16.dp))
			MultiChoiceSegmentedButtonRow(modifier = Modifier.align(Alignment.CenterHorizontally)) {
				SegmentedButton(
					checked = preferredMethod.contains(PresentMethod.Otp.COPY),
					onCheckedChange = {
						preferredMethod =
							if (it) preferredMethod.plus(PresentMethod.Otp.COPY) else preferredMethod.minus(
								PresentMethod.Otp.COPY
							)
						updatePreferredMethods(appSettings, preferredMethod, context)
					},
					shape = SegmentedButtonOrder.First,
					//icon = { Icon(imageVector = Symbols.CopyCode, contentDescription = "Copy code method") },
				) {
					Text(text = stringResource(R.string.otp_copy_method))
				}
				SegmentedButton(
					enabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) (checkSelfPermission(
						context, android.Manifest.permission.POST_NOTIFICATIONS
					) == PackageManager.PERMISSION_GRANTED) else true,
					checked = preferredMethod.contains(PresentMethod.Otp.NOTIFY),
					onCheckedChange = {
						preferredMethod =
							if (it) preferredMethod.plus(PresentMethod.Otp.NOTIFY) else preferredMethod.minus(
								PresentMethod.Otp.NOTIFY
							)
						updatePreferredMethods(appSettings, preferredMethod, context)
					},
					shape = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) SegmentedButtonOrder.Middle else SegmentedButtonOrder.Last
				) {
					Text(text = stringResource(R.string.otp_notify_method))
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					SegmentedButton(
						enabled = Settings.canDrawOverlays(context),
						checked = preferredMethod.contains(PresentMethod.Otp.SELECT),
						onCheckedChange = {
							preferredMethod =
								if (it) preferredMethod.plus(PresentMethod.Otp.SELECT) else preferredMethod.minus(
									PresentMethod.Otp.SELECT
								)
							updatePreferredMethods(appSettings, preferredMethod, context)
						},
						shape = SegmentedButtonOrder.Last,
					) {
						Text(text = stringResource(R.string.otp_show_method))
					}
				}
			}
		}
	}
}

private fun updatePreferredMethods(
	appSettings: App.Settings, preferredMethod: Set<String>, context: Context,
) {
	appSettings.presentMethods = preferredMethod
	App.setSettings(context, appSettings)
}

private fun grantScreenOverlayPermission(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		if (!Settings.canDrawOverlays(context)) {
			startActivityForResult(
				activity, Intent(
					Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					("package:" + context.applicationContext.packageName).toUri()
				), OVERLAY_PERMISSIONS_REQUEST_CODE, null
			)

		} else {
			Toast.makeText(
				context, context.getString(R.string.permission_granted_recently), Toast.LENGTH_SHORT
			).show()
		}
	}
}

private fun grantNotificationPermission(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		if (checkSelfPermission(
				context, android.Manifest.permission.POST_NOTIFICATIONS
			) != PackageManager.PERMISSION_GRANTED
		) {
			permissionLauncher.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))
		} else {
			Toast.makeText(
				context, context.getString(R.string.permission_granted_recently), Toast.LENGTH_SHORT
			).show()
		}
	}
}

@SuppressLint("BatteryLife")
private fun disableBatteryLimitations(context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		val intent = Intent()
		val packageName = context.packageName
		val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
		if (!pm.isIgnoringBatteryOptimizations(packageName)) {
			intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
			intent.setData("package:$packageName".toUri())
			context.startActivity(intent, null)
		} else {
			Toast.makeText(
				context,
				context.getString(R.string.battery_optimization_disabled),
				Toast.LENGTH_SHORT
			).show()
		}
	} else {
		Toast.makeText(
			context, context.getString(R.string.device_doesnt_support), Toast.LENGTH_SHORT
		).show()
	}
}

@Preview(
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SettingsPreview() {
	PuyaKhanTheme {
		SettingsView {}
	}
}
