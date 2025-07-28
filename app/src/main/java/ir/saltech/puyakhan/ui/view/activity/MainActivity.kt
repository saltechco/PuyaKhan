package ir.saltech.puyakhan.ui.view.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.ApplicationLoader
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.model.OtpSms
import ir.saltech.puyakhan.data.util.MAX_OTP_SMS_EXPIRATION_TIME
import ir.saltech.puyakhan.data.util.OtpProcessor
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.theme.Symbols
import ir.saltech.puyakhan.ui.view.component.compose.LockedDirection
import ir.saltech.puyakhan.ui.view.component.compose.OtpCodeCard
import ir.saltech.puyakhan.ui.view.component.compose.PermissionAlert
import ir.saltech.puyakhan.ui.view.model.OtpCodesVM
import ir.saltech.puyakhan.ui.view.page.SettingsView
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.system.exitProcess


internal const val OVERLAY_PERMISSIONS_REQUEST_CODE = 3093
internal const val NOTIFY_OTP_CHANNEL_ID = "ir.saltech.puyakhan.OTP_SMS_CODES"
internal const val NOTIFY_SERVICE_CHANNEL_ID = "ir.saltech.puyakhan.BACKGROUND_SERVICES"

internal lateinit var activity: ComponentActivity
internal lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

private const val INIT_TIME_DELAY = 1500
private const val CODE_TIME_DELAY = 150L

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
					startProgram()
				} else {
					exitProcess(-1)
				}
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
							PuyaKhanApp()
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
			needsAppPermissionsRational() -> PermissionAlert(
				stringResource(R.string.app_permission_title),
				stringResource(R.string.app_permission_message),
				onConfirm = {
					requestAppPermissions()
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
	}

	override fun onPause() {
		super.onPause()
		ApplicationLoader.isActivityLaunched = false
	}
}

@Composable
private fun PuyaKhanApp() {
	var page by remember {
		mutableStateOf(App.Page.Main)
	}
	AnimatedVisibility(visible = page == App.Page.Main) {
		PuyaKhanView { page = it }
	}
	AnimatedVisibility(visible = page == App.Page.Settings) {
		SettingsView { page = it }
	}
}

@Composable
private fun PuyaKhanView(otpCodesVM: OtpCodesVM = viewModel(), onPageChanged: (App.Page) -> Unit) {
	val codeList by otpCodesVM.otpCodes.collectAsState()

	LaunchedEffect(otpCodesVM.initProgressShow) {
		coroutineScope {
			launch {
				delay(INIT_TIME_DELAY + (codeList.size * CODE_TIME_DELAY))
				otpCodesVM.initProgressShow = false
			}
		}
	}

	Scaffold(
		topBar = {
			PuyaKhanTopBar(onPageChanged = { onPageChanged(it) })
		},
	) { contentPadding ->
		PuyaKhanContent(codeList, otpCodesVM.initProgressShow, contentPadding)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PuyaKhanTopBar(
	onPageChanged: (App.Page) -> Unit,
) {
	CenterAlignedTopAppBar(title = {
		Text(
			stringResource(id = R.string.app_name), style = MaterialTheme.typography.displayMedium
		)
	}, actions = {
		Spacer(modifier = Modifier.width(16.dp))
		IconButton(onClick = {
			onPageChanged(App.Page.Settings)
		}) {
			Icon(
				modifier = Modifier.size(26.dp),
				imageVector = Symbols.Default.Settings,
				contentDescription = stringResource(R.string.app_settings_cd)
			)
		}
		Spacer(modifier = Modifier.width(16.dp))
	})
}

@Composable
private fun PuyaKhanContent(
	codeList: MutableList<OtpCode>, showProgress: Boolean,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {
	val context = LocalContext.current
	val codesListState = rememberLazyStaggeredGridState()

	AnimatedVisibility(
		visible = showProgress,
		enter = fadeIn(),
		exit = fadeOut()
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(contentPadding),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			CircularProgressIndicator(modifier = Modifier.size(32.dp))
		}
	}
	AnimatedVisibility(
		visible = codeList.isEmpty() && !showProgress,
		enter = fadeIn(),
		exit = fadeOut()
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(contentPadding),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				stringResource(R.string.empty_code_list),
				style = MaterialTheme.typography.labelLarge.copy(
					color = MaterialTheme.colorScheme.outline,
					textDirection = TextDirection.ContentOrRtl
				),
				textAlign = TextAlign.Center
			)
		}
	}
	AnimatedVisibility(
		visible = codeList.isNotEmpty() && !showProgress,
		enter = fadeIn(),
		exit = fadeOut()
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(contentPadding),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Top
		) {
			Text(
				modifier = Modifier
					.padding(top = 24.dp)
					.scale(0.95f),
				text = stringResource(R.string.list_otp_codes_title),
				style = MaterialTheme.typography.bodyMedium.copy(
					color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold
				),
				textAlign = TextAlign.Center,
				maxLines = 1
			)
			AnimatedContent(codesListState) { state ->
				LazyVerticalStaggeredGrid(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 4.dp, bottom = 8.dp)
						.padding(horizontal = 16.dp),
					columns = StaggeredGridCells.Adaptive(145.dp),
					contentPadding = PaddingValues(8.dp),
					state = state,
					reverseLayout = true,
					horizontalArrangement = Arrangement.Absolute.SpaceAround
				) {
					itemsIndexed(codeList) { index, _ ->
						OtpCodeCard(context, codeList, index)
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun OverallPreview() {
	PuyaKhanTheme {
		PuyaKhanApp()
	}
}
