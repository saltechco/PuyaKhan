package ir.saltech.puyakhan.ui.view.page

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.puyakhan.App
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.theme.Symbols
import ir.saltech.puyakhan.ui.view.component.compose.OtpCodeCard
import ir.saltech.puyakhan.ui.view.model.MainViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val INIT_TIME_DELAY = 1500
private const val CODE_TIME_DELAY = 150L

@Composable
internal fun MainView(mainViewModel: MainViewModel = viewModel(), onPageChanged: (App.Page) -> Unit) {
    val codeList by mainViewModel.otpCodes.collectAsState()

    LaunchedEffect(mainViewModel.initProgressShow) {
        coroutineScope {
            launch {
                delay(INIT_TIME_DELAY + (codeList.size * CODE_TIME_DELAY))
                mainViewModel.initProgressShow = false
            }
        }
    }

    Scaffold(
        topBar = {
            PuyaKhanTopBar(onPageChanged = { onPageChanged(it) })
        },
    ) { contentPadding ->
        PuyaKhanContent(codeList, mainViewModel.initProgressShow, contentPadding)
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
        IconButton(modifier = Modifier.padding(horizontal = 16.dp), onClick = {
            onPageChanged(App.Page.Settings)
        }) {
            Icon(
                modifier = Modifier.size(26.dp),
                imageVector = Symbols.Default.Settings,
                contentDescription = stringResource(R.string.app_settings_cd)
            )
        }
    }, navigationIcon = {
        IconButton(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 2.dp), onClick = {
            onPageChanged(App.Page.LocalServer)
        }) {
            Icon(
                modifier = Modifier.size(23.dp),
                imageVector = Icons.Rounded.Devices,
                contentDescription = stringResource(R.string.local_server_view_title)
            )
        }
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
        visible = showProgress, enter = fadeIn(), exit = fadeOut()
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
        visible = codeList.isEmpty() && !showProgress, enter = fadeIn(), exit = fadeOut()
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
        visible = codeList.isNotEmpty() && !showProgress, enter = fadeIn(), exit = fadeOut()
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
private fun MainViewPreview() {
    PuyaKhanTheme {
        MainView { }
    }
}
