package ir.saltech.puyakhan.ui.view.activity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.App
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.util.LockedDirection
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.theme.Symbols
import ir.saltech.puyakhan.ui.view.component.compose.MemorySafety
import ir.saltech.puyakhan.ui.view.component.compose.OtpCodeCard
import ir.saltech.puyakhan.ui.view.component.compose.PermissionAlert
import ir.saltech.puyakhan.ui.view.component.manager.CLIPBOARD_OTP_CODE
import ir.saltech.puyakhan.ui.view.component.manager.OtpManager.Companion.getCodeList
import ir.saltech.puyakhan.ui.view.model.OtpCodesVM
import ir.saltech.puyakhan.ui.view.page.SettingsView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


internal const val OVERLAY_PERMISSIONS_REQUEST_CODE = 3093

internal const val NOTIFY_OTP_CHANNEL_ID = "ir.saltech.puyakhan.OTP_SMS_CODES"
internal const val NOTIFY_SERVICE_CHANNEL_ID = "ir.saltech.puyakhan.BACKGROUND_SERVICES"

internal lateinit var activity: ComponentActivity
internal lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
internal var memoryLeaked: Boolean = false

class MainActivity : ComponentActivity() {
	private val permissions =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
			arrayOf(
				android.Manifest.permission.READ_SMS,
				android.Manifest.permission.RECEIVE_SMS,
				android.Manifest.permission.POST_NOTIFICATIONS
			)
		else
			arrayOf(
				android.Manifest.permission.READ_SMS,
				android.Manifest.permission.RECEIVE_SMS
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
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			createOtpNotifyChannel()
			createServicesNotifyChannel()
		}
		startProgram()
	}

	@SuppressLint("NewApi")
	private fun startProgram() {
		setContent {
			PuyaKhanTheme {
				// A surface container using the 'background' color from the theme
				LockedDirection {
					Surface(
						modifier = Modifier.fillMaxSize(),
						color = MaterialTheme.colorScheme.background
					) {
						val appSettings = App.getSettings(this)
						if (checkAppPermissions()) {
							if (appSettings.disclaimerAccepted) {
								PuyaKhanApp()
							} else {
								DisclaimerAcceptation {
									appSettings.disclaimerAccepted = true
									App.setSettings(this, appSettings)
									startProgram()
								}
							}
						} else {
							RequestPermission()
						}
					}
				}
			}
		}
	}

	@Composable
	fun DisclaimerAcceptation(
		dismissible: Boolean = false,
		onConfirm: () -> Unit
	) {
		var dismiss by remember { mutableStateOf(false) }
		if (!dismiss) {
			AlertDialog(
				icon = {
					Icon(
						imageVector = Symbols.Default.Disclaimer,
						contentDescription = "Disclaimer Dialog"
					)
				},
				onDismissRequest = {
					dismiss = dismissible
				},
				title = {
					Text(
						text = "سلب مسئولیت",
						style = MaterialTheme.typography.headlineSmall.copy(textDirection = TextDirection.ContentOrRtl)
					)
				},
				text = {
					Text(
						text = stringResource(R.string.disclaimer_text),
						style = MaterialTheme.typography.bodyLarge.copy(
							textDirection = TextDirection.ContentOrRtl,
							textAlign = TextAlign.Justify
						)
					)
				},
				confirmButton = {
					TextButton(onClick = onConfirm) {
						Text(text = "پذیرش و ادامه")
					}
				}
			)
		}
	}

	@RequiresApi(Build.VERSION_CODES.M)
	@Composable
	fun RequestPermission() {
		when {
			needsAppPermissionsRational() ->
				PermissionAlert(
					"نیازمند مجوز دسترسی",
					"پویا خوان برای بررسی کد های رمز پویای دریافتی از طرف دستگاه شما، نیازمند دسترسی به پیام های شماست.",
					onConfirm = {
						requestAppPermissions()
					}
				)

			else -> requestAppPermissions()
		}
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

	private fun checkAppPermissions(): Boolean {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			for (permission in permissions) {
				if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) return false
			}
		}
		return true
	}

	private fun needsAppPermissionsRational(): Boolean {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			for (permission in permissions) {
				if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_SMS)) return true
			}
		}
		return false
	}

	private fun requestAppPermissions() {
		permissionLauncher.launch(permissions)
	}
}

@Composable
fun PuyaKhanApp() {
	var page by remember {
		mutableStateOf(App.Page.Main)
	}
	AnimatedVisibility(visible = page == App.Page.Main) {
		PuyaKhanView(page) { page = it }
	}
	AnimatedVisibility(visible = page == App.Page.Settings) {
		SettingsView { page = it }
	}
}

@Composable
fun PuyaKhanView(page: App.Page, onPageChanged: (App.Page) -> Unit) {
	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
	val scope = rememberCoroutineScope()
	BackHandler(drawerState.isOpen) {
		closeDrawer(scope, drawerState)
	}
	DismissibleNavigationDrawer(
		drawerState = drawerState,
		drawerContent = {
			ModalDrawerSheet {
				Spacer(modifier = Modifier.fillMaxHeight(0.45f))
				Text(
					"¯\\_( ͡° ͜ʖ ͡°)_/¯",
					style = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
					modifier = Modifier
						.fillMaxWidth()
				)
//				Text(
//					"هنوز ایده\u200cای نداریم!",
//					style = MaterialTheme.typography.displaySmall.copy(
//						textAlign = TextAlign.Center,
//						textDirection = TextDirection.ContentOrRtl,
//						fontSize = 21.sp,
//						fontWeight = FontWeight.Light
//					),
//					modifier = Modifier
//						.fillMaxWidth()
//				)

				//PublishMenu(page, onPageChanged, scope, drawerState)
			}
		}
	) {
		Scaffold(
			topBar = {
				PuyaKhanTopBar(
					drawerState,
					onPageChanged = { onPageChanged(it) },
					onNavigated = {
						scope.launch {
							drawerState.apply {
								if (isClosed) open() else close()
							}
						}
					}
				)
			},
		) {
			PuyaKhanContent(it)
		}
	}
}

@Deprecated("Currently, There is no any idea for customizing this menu.")
@Composable
private fun PublishMenu(
	page: App.Page,
	onPageChanged: (App.Page) -> Unit,
	scope: CoroutineScope,
	drawerState: DrawerState
) {
	Spacer(modifier = Modifier.height(24.dp))
	NavigationDrawerItem(
		label = { Text(text = "صفحه اصلی") },
		icon = {
			Icon(
				if (page == App.Page.Main) Symbols.Filled.Home else Symbols.Default.Home,
				contentDescription = "Home",
				modifier = Modifier.size(36.dp)
			)
		},
		selected = page == App.Page.Main,
		onClick = { onPageChanged(App.Page.Main); closeDrawer(scope, drawerState) }
	)
	Spacer(modifier = Modifier.height(8.dp))
	NavigationDrawerItem(
		label = { Text(text = "درباره ما") },
		icon = {
			Icon(
				if (page == App.Page.Info) Symbols.Filled.Info else Symbols.Default.Info,
				contentDescription = "Info",
				modifier = Modifier.size(36.dp)
			)
		},
		selected = page == App.Page.Info,
		onClick = { onPageChanged(App.Page.Info); closeDrawer(scope, drawerState) }
	)
	Spacer(modifier = Modifier.height(8.dp))
	NavigationDrawerItem(
		label = { Text(text = "حمایت از ما") },
		icon = {
			Icon(
				if (page == App.Page.RateUs) Symbols.Filled.Rate else Symbols.Default.Rate,
				contentDescription = "Rate us",
				modifier = Modifier.size(36.dp)
			)
		},
		selected = page == App.Page.RateUs,
		onClick = { onPageChanged(App.Page.RateUs); closeDrawer(scope, drawerState) }
	)
	Spacer(modifier = Modifier.height(24.dp))
}

private fun closeDrawer(
	scope: CoroutineScope,
	drawerState: DrawerState
) {
	scope.launch {
		drawerState.apply { close() }
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuyaKhanTopBar(
	drawerState: DrawerState,
	onPageChanged: (App.Page) -> Unit,
	onNavigated: () -> Unit
) {
	CenterAlignedTopAppBar(
		title = {
			Text(
				stringResource(id = R.string.app_name),
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
						.clickable { onNavigated() },
					imageVector = if (drawerState.isClosed) Icons.Rounded.Menu else Icons.Rounded.Close,
					contentDescription = "Show more items"
				)
				Spacer(modifier = Modifier.width(16.dp))
			}
		},
		actions = {
			Spacer(modifier = Modifier.width(16.dp))
			Icon(
				modifier = Modifier
					.size(26.dp)
					.align(Alignment.Bottom)
					.clickable { onPageChanged(App.Page.Settings) },
				imageVector = Symbols.Default.Settings,
				contentDescription = "App Settings"
			)
			Spacer(modifier = Modifier.width(16.dp))
		}
	)
}

@Composable
fun PuyaKhanContent(
	contentPadding: PaddingValues = PaddingValues(0.dp),
	otpCodesVM: OtpCodesVM = viewModel()
) {
	val context = LocalContext.current
	val appSettings = App.getSettings(context)
	val codeList by otpCodesVM.otpCodes.observeAsState(getCodeList(context, appSettings))

	MemorySafety {
		RefreshSmsList(otpCodesVM, context, appSettings)
	}

	AnimatedVisibility(visible = codeList.isEmpty()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(contentPadding)
		) {
			Spacer(modifier = Modifier.fillMaxHeight(0.5f))
			Text(
				stringResource(R.string.empty_code_list),
				style = MaterialTheme.typography.labelLarge.copy(
					color = MaterialTheme.colorScheme.outline,
					textDirection = TextDirection.ContentOrRtl
				),
				modifier = Modifier
					.fillMaxHeight(0.5f)
					.align(Alignment.CenterHorizontally)
			)
		}
	}
	AnimatedVisibility(visible = codeList.isNotEmpty()) {
		Column {
			Text(text = "Showing ${codeList.size} sms")
			LazyColumn(modifier = Modifier.weight(1f), contentPadding = contentPadding) {
				items(codeList) { code ->
					OtpCodeCard(context, appSettings, code) {
						otpCodesVM.onOtpCodesChanged(codeList.minus(code))
					}
				}
			}
		}
	}
}

@Composable
private fun RefreshSmsList(
	otpCodesVM: OtpCodesVM,
	context: Context, appSettings: App.Settings
) {
	SideEffect {
		object : CountDownTimer(10000000000, 3000) {

			override fun onTick(millisUntilFinished: Long) {
				otpCodesVM.onOtpCodesChanged(getCodeList(context, appSettings))
			}

			override fun onFinish() {
				Log.i("TAG", "Sms Code Live Checker has been ended!")
				this.start()
			}
		}.start()
	}
}

fun copySelectedCode(context: Context, code: OtpCode) {
	val clipboardManager =
		context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
	clipboardManager.setPrimaryClip(
		ClipData(ClipData.newPlainText(CLIPBOARD_OTP_CODE, code.otp))
	)
	Toast.makeText(
		context,
		context.getString(R.string.otp_copied_to_clipboard), Toast.LENGTH_SHORT
	).show()
}


fun shareSelectedCode(context: Context, code: OtpCode) {
	val shareIntent = Intent(Intent.ACTION_SEND)
	shareIntent.type = "text/plain"
	shareIntent.putExtra(
		Intent.EXTRA_TEXT,
		context.getString(R.string.share_otp_code_text, code.bank ?: "نامشخص", code.otp)
	)
	context.startActivity(
		Intent.createChooser(
			shareIntent,
			context.getString(R.string.send_otp_to)
		).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	)
}

@Preview(showBackground = true)
@Composable
fun OverallPreview() {
	PuyaKhanTheme {
		PuyaKhanApp()
	}
}
