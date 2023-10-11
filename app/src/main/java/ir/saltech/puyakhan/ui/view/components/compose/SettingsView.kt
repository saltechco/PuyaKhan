@file:OptIn(ExperimentalMaterial3Api::class)

package ir.saltech.puyakhan.ui.view.components.compose

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.PresentMethod
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme

@Composable
fun SettingsView() {
	Scaffold(topBar = { SettingsTopBar() }) {
		SettingsContent(it)
	}
}

@Composable
fun SettingsTopBar() {
	CenterAlignedTopAppBar(title = { Text("تنظیمات") })
}

@Composable
fun SettingsContent(paddingValues: PaddingValues) {
	Column(
		modifier = Modifier
			.padding(paddingValues)
	) {
		val context = LocalContext.current
		val appSettings = App.getSettings(context)
		MethodSelection(context, appSettings)
		ExpireTimeSelection(context, appSettings)
	}
}

@Composable
fun ExpireTimeSelection(context: Context, appSettings: App.Settings) {
	var expireTime by remember {
		mutableLongStateOf(appSettings.expireTime)
	}
	Column(
		modifier =
		Modifier
			.padding(8.dp)
			.clip(
				RoundedCornerShape(
					topEnd = 5.dp, topStart = 5.dp,
					bottomEnd = 15.dp, bottomStart = 15.dp
				)
			)
			.background(MaterialTheme.colorScheme.surfaceBright)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight()
		) {
			Text("مدت انقضای رمز")
			Spacer(modifier = Modifier.width(16.dp))
			OutlinedTextField(
				value = (expireTime / 1_000).toString(),
				onValueChange = { time ->
					if (time.toInt() in 1..180) {
						expireTime = time.toLong() * 1000
						appSettings.expireTime = expireTime
						App.setSettings(context, appSettings)
					}
				},
				placeholder = { Text("بر حسب ثانیه") }
			)
		}
	}
}

@Composable
fun MethodSelection(context: Context, appSettings: App.Settings) {
	var preferredMethod by remember {
		mutableIntStateOf(appSettings.presentMethod)
	}
	Column(
		modifier =
		Modifier
			.padding(8.dp)
			.clip(
				RoundedCornerShape(
					topEnd = 15.dp, topStart = 15.dp,
					bottomEnd = 5.dp, bottomStart = 5.dp
				)
			)
			.background(MaterialTheme.colorScheme.surfaceBright)
	) {
		Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
			Text(
				modifier = Modifier.fillMaxWidth(),
				text = "نحوه نمایش رمز پویا",
				style = MaterialTheme.typography.bodyLarge,
				textAlign = TextAlign.Center
			)
			Spacer(modifier = Modifier.height(8.dp))
			SingleChoiceSegmentedButtonRow {
				SegmentedButton(
					selected = preferredMethod == PresentMethod.Otp.Copy,
					onClick = {
						preferredMethod = PresentMethod.Otp.Copy
						appSettings.presentMethod = PresentMethod.Otp.Copy
						App.setSettings(context, appSettings)
					},
					shape = SegmentedButtonPosition.First
				) {
					Text(text = "کپی")
				}
				SegmentedButton(
					selected = preferredMethod == PresentMethod.Otp.Notify,
					onClick = {
						preferredMethod = PresentMethod.Otp.Notify
						appSettings.presentMethod = PresentMethod.Otp.Notify
						App.setSettings(context, appSettings)
					},
					shape = SegmentedButtonPosition.Middle
				) {
					Text(text = "اعلان")
				}
				SegmentedButton(
					selected = preferredMethod == PresentMethod.Otp.Select,
					onClick = {
						preferredMethod = PresentMethod.Otp.Select
						appSettings.presentMethod = PresentMethod.Otp.Select
						App.setSettings(context, appSettings)
					},
					shape = SegmentedButtonPosition.Middle
				) {
					Text(text = "نمایش")
				}
				SegmentedButton(
					selected = preferredMethod == PresentMethod.Otp.CopyNotify,
					onClick = {
						preferredMethod = PresentMethod.Otp.CopyNotify
						appSettings.presentMethod = PresentMethod.Otp.CopyNotify
						App.setSettings(context, appSettings)
					},
					shape = SegmentedButtonPosition.Last
				) {
					Text(text = "اعلان،کپی")
				}
			}
		}
	}
}

internal object SegmentedButtonPosition {
	val First = RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
	val Middle = RoundedCornerShape(0.dp)
	val Last = RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
}

@Preview(
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun SettingsPreview() {
	PuyaKhanTheme {
		SettingsView()
	}
}
