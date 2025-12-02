package ir.saltech.puyakhan.ui.view.page

import android.content.ClipData
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.ApplicationLoader
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.service.LOCAL_SERVER_NET_PORT
import ir.saltech.puyakhan.data.service.LocalServerService
import ir.saltech.puyakhan.data.util.getWifiIpAddress
import ir.saltech.puyakhan.data.util.startLocalServerService
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.theme.Symbols
import ir.saltech.puyakhan.ui.view.model.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val NETWORK_CHECK_DELAY_MILLIS: Long = 3000

@Composable
fun LocalServerView(onPageChanged: (App.Page) -> Unit) {
    BackHandler {
        onPageChanged(App.Page.Main)
    }
    Scaffold(topBar = { LocalServerViewTopBar { onPageChanged(it) } }) {
        LocalServerViewContent(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocalServerViewTopBar(onPageChanged: (App.Page) -> Unit) {
    CenterAlignedTopAppBar(title = {
        Text(
            stringResource(R.string.local_server_view_title),
            style = MaterialTheme.typography.displayMedium
        )
    }, navigationIcon = {
        Row {
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = {
                onPageChanged(App.Page.Main)
            }) {
                Icon(
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.Bottom),
                    imageVector = Symbols.Default.Back,
                    contentDescription = stringResource(R.string.back_to_the_main_page_cd)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    })
}

@Composable
private fun LocalServerViewContent(
    paddingValues: PaddingValues = PaddingValues(0.dp), mainViewModel: MainViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current

    var isServerRunning: App.TripleStateSwitchStatus by remember {
        mutableStateOf(
            if (mainViewModel.appSettings?.runLocalServer ?: false) App.TripleStateSwitchStatus.On else App.TripleStateSwitchStatus.Off
        )
    }
    var ipAddress by remember { mutableStateOf("0.0.0.0") }
    var isWifiConnected by remember { mutableStateOf(false) }

    // آپدیت کردن IP و وضعیت وای‌فای به صورت دوره‌ای یا هنگام ورود
    LaunchedEffect(Unit) {
        while (true) {
            ipAddress = context.getWifiIpAddress()
            isWifiConnected = ipAddress != "0.0.0.0" && ipAddress.isNotEmpty()
            delay(NETWORK_CHECK_DELAY_MILLIS) // هر ۳ ثانیه وضعیت شبکه چک شود
        }
    }

    // کنترل سرویس (شروع/توقف)
    fun toggleServer(enable: Boolean) {
        isServerRunning = App.TripleStateSwitchStatus.Loading
        Log.i("TAG", "Save new run local server state .. $enable")
        ApplicationLoader.canRunLocalServer = enable
        mainViewModel.appSettings?.runLocalServer = enable
        mainViewModel.saveAppSettings()
        val intent = Intent(
            context, LocalServerService::class.java
        )
        scope.launch {
            delay(1000)
            if (enable) {
                context.startLocalServerService()
            } else {
                context.stopService(intent)
                Toast.makeText(
                    context, context.getString(R.string.local_server_stopped), Toast.LENGTH_SHORT
                ).show()
            }
            isServerRunning = if (enable) App.TripleStateSwitchStatus.On else App.TripleStateSwitchStatus.Off
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            ServerStatusCard(
                isRunning = isServerRunning == App.TripleStateSwitchStatus.On, onToggle = { toggleServer(it) })
        }

        item {
            if (isWifiConnected) {
                ConnectionInfoCard(
                    ip = ipAddress,
                    port = LOCAL_SERVER_NET_PORT.toString(),
                    onCopyClick = { address ->
                        scope.launch {
                            clipboardManager.setClipEntry(
                                ClipEntry(
                                    ClipData.newPlainText(
                                        "Server Ip Address", AnnotatedString(address).text
                                    )
                                )
                            )
                        }
                        Toast.makeText(context, "آدرس کپی شد", Toast.LENGTH_SHORT).show()
                    })
            } else {
                NoWifiWarningCard {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
            }
        }

        // بخش ۳: راهنما و دکمه تنظیمات وای‌فای
        item {
            WifiSettingsSection {
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// UI Components
// -----------------------------------------------------------------------------

@Composable
fun ServerStatusCard(isRunning: Boolean, onToggle: (Boolean) -> Unit) {
    val cardColor by animateColorAsState(
        targetValue = if (isRunning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        label = "cardColor"
    )

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Switch(
                checked = isRunning, onCheckedChange = onToggle, thumbContent = {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                })
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (isRunning) stringResource(R.string.local_server_on)
                    else stringResource(R.string.local_server_off),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold, textDirection = TextDirection.ContentOrRtl
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (isRunning) stringResource(R.string.local_server_ready) else stringResource(
                        R.string.local_server_start_communicate
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(textDirection = TextDirection.ContentOrRtl),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ConnectionInfoCard(ip: String, port: String, onCopyClick: (String) -> Unit) {
    val fullAddress = "$ip:$port"

    OutlinedCard(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(R.string.local_server_connection_info),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold, textDirection = TextDirection.ContentOrRtl
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.Lan,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.local_server_use_address_help),
                style = MaterialTheme.typography.bodySmall.copy(textDirection = TextDirection.ContentOrRtl),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                    .clickable { onCopyClick(fullAddress) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier
                            .scale(0.9f)
                            .offset(x = (-9).dp),
                        text = stringResource(R.string.local_server_ip_address_and_port),
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 4.sp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                    Text(
                        text = fullAddress,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        letterSpacing = 1.sp,
                        maxLines = 1
                    )
                }
                IconButton(onClick = { onCopyClick(fullAddress) }) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun NoWifiWarningCard(onSettingsClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.WifiOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "عدم اتصال به وای‌فای",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "برای ارسال اطلاعات به لپ‌تاپ، هر دو دستگاه باید به یک مودم (وای‌فای) متصل باشند.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            Button(
                onClick = onSettingsClick, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("روشن کردن وای‌فای")
            }
        }
    }
}

@Composable
fun WifiSettingsSection(onWifiClick: () -> Unit) {
    OutlinedButton(
        onClick = onWifiClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(stringResource(R.string.local_server_wifi_settings_title), maxLines = 1)
        Spacer(Modifier.width(8.dp))
        Icon(
            Icons.Rounded.Wifi,
            contentDescription = null,
            modifier = Modifier
                .size(22.dp)
                .padding(bottom = 2.dp)
        )
    }
    Spacer(Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.local_server_wifi_tip),
        style = MaterialTheme.typography.bodySmall.copy(
            textDirection = TextDirection.ContentOrRtl,
            textAlign = TextAlign.Justify,
            lineHeight = 24.sp
        ),
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .scale(0.95f)
    )
}

@Preview(showBackground = true)
@Composable
private fun LocalServerViewPreview() {
    PuyaKhanTheme {
        LocalServerView { }
    }
}
