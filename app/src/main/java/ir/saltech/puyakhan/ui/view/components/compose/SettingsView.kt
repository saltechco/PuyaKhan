package ir.saltech.puyakhan.ui.view.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme

@Composable
fun SettingsView() {
	Scaffold(topBar = { SettingsTopBar() }) {
		SettingsContent(it)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
	CenterAlignedTopAppBar(title = { Text("تنظیمات") })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(paddingValues: PaddingValues) {
	Column(
		modifier = Modifier
			.padding(paddingValues)
	) {
		val context = LocalContext.current
		val appSettings = App.getSettings(context)
		Column(
			modifier =
			Modifier
				.padding(8.dp)
				.clip(
					RoundedCornerShape(
						topEnd = 20.dp, topStart = 20.dp,
						bottomEnd = 5.dp, bottomStart = 5.dp
					)
				)
				.background(MaterialTheme.colorScheme.primaryContainer)
		) {
			Column(modifier = Modifier.padding(8.dp)) {
				Text(text = "نحوه نمایش رمز پویا", style = MaterialTheme.typography.bodyLarge)
				SingleChoiceSegmentedButtonRow {
					SegmentedButton(
						selected = false,
						onClick = { /*TODO*/ },
						shape = RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
					) {
						Text(text = "کپی")
					}
					SegmentedButton(
						selected = false,
						onClick = { /*TODO*/ },
						shape = RoundedCornerShape(0.dp)
					) {
						Text(text = "اعلان")
					}
					SegmentedButton(
						selected = false,
						onClick = { /*TODO*/ },
						shape = RoundedCornerShape(0.dp)
					) {
						Text(text = "نمایش")
					}
					SegmentedButton(
						selected = false,
						onClick = { /*TODO*/ },
						shape = RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
					) {
						Text(text = "اعلان،کپی")
					}
				}
			}
		}
	}
}


@Preview(showBackground = true)
@Composable
fun SettingPreview() {
	PuyaKhanTheme {
		SettingsView()
	}
}
