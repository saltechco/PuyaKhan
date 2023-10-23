package ir.saltech.puyakhan.ui.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.view.component.manager.OtpManager

class BackgroundActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			DoCopyTask()
			finishAffinity()
		}
	}
}

@Composable
fun DoCopyTask() {
	val context = LocalContext.current
	val appSettings = App.getSettings(context)
	val code = OtpManager.getCodeList(context, appSettings).first()
	copySelectedCode(context, code)
}

@Preview(showBackground = true)
@Composable
fun BackgroundPreview() {
	PuyaKhanTheme {
		DoCopyTask()
	}
}