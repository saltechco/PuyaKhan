package ir.saltech.puyakhan.ui.view.component.compose

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.saltech.puyakhan.R
import ir.saltech.puyakhan.data.model.OtpCode
import ir.saltech.puyakhan.data.util.MAX_OTP_SMS_EXPIRATION_TIME
import ir.saltech.puyakhan.data.util.div
import ir.saltech.puyakhan.data.util.past
import ir.saltech.puyakhan.data.util.shareSelectedOtpCode
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme
import ir.saltech.puyakhan.ui.theme.Symbols
import ir.saltech.puyakhan.ui.view.activity.copySelectedCode

internal object SegmentedButtonOrder {
	val First = RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
	val Middle = RoundedCornerShape(0.dp)
	val Last = RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
}

@Composable
internal fun PermissionAlert(
	title: String, text: String, onConfirm: () -> Unit, dismissible: Boolean = false,
) {
	var dismiss by remember { mutableStateOf(false) }
	if (!dismiss) {
		AlertDialog(icon = {
			Icon(
				imageVector = Symbols.Default.PermissionNeeded,
				contentDescription = "Permission Needed Icon"
			)
		}, onDismissRequest = {
			dismiss = dismissible
		}, title = {
			Text(
				text = title,
				style = MaterialTheme.typography.headlineSmall.copy(textDirection = TextDirection.ContentOrRtl)
			)
		}, text = {
			Text(
				text = text, style = MaterialTheme.typography.bodyLarge.copy(
					textDirection = TextDirection.ContentOrRtl, textAlign = TextAlign.Justify
				)
			)
		}, confirmButton = {
			TextButton(onClick = onConfirm) {
				Text(text = stringResource(R.string.permission_accept_button))
			}
		})
	}
}


@Composable
internal fun MemorySafety(
	showMessage: Boolean = true, content: @Composable () -> Unit,
) {
	val context = LocalContext.current
	val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
	if (!activityManager.isLowRamDevice) content()
	else if (showMessage) Toast.makeText(
		context, stringResource(R.string.memory_leaked_error), Toast.LENGTH_SHORT
	).show()
	else Log.e("MEMORY_SAFETY", "Memory Leaked Detected! Some features have disabled.")
}

@Composable
internal fun OpenReferenceButton(title: String, contentDescription: String?, onClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 4.dp)
			.clip(MaterialTheme.shapes.large)
			.background(MaterialTheme.colorScheme.surfaceVariant)
			.selectable(
				selected = false, onClick = onClick
			),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(8.dp)
				.align(Alignment.CenterVertically)
		) {
			Icon(
				imageVector = Symbols.Default.NewTab,
				contentDescription = contentDescription,
				modifier = Modifier
					.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
					.size(24.dp)
					.align(Alignment.CenterVertically)
			)
			Spacer(modifier = Modifier.width(16.dp))
			Text(
				text = title,
				textAlign = TextAlign.End,
				modifier = Modifier
					.padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
					.weight(1f)
					.align(Alignment.CenterVertically)
			)
		}
	}
}

@Composable
internal fun MinimalHelpText(text: String) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 13.dp, vertical = 8.dp)
	) {
		Text(
			text = text, style = MaterialTheme.typography.labelMedium.copy(
				textAlign = TextAlign.Justify,
				textDirection = TextDirection.ContentOrRtl,
				color = MaterialTheme.colorScheme.outline
			), modifier = Modifier
				.align(Alignment.CenterVertically)
				.fillMaxWidth(0.9f)
		)
		Spacer(modifier = Modifier.width(8.dp))
		Column {
			Spacer(modifier = Modifier.height(4.dp))
			Icon(
				modifier = Modifier.size(18.dp),
				imageVector = Symbols.Default.Info,
				contentDescription = "A Help Text",
				tint = MaterialTheme.colorScheme.outline
			)
		}
	}
}

@Composable
internal fun LockedDirection(
	direction: LayoutDirection = LayoutDirection.Ltr, content: @Composable () -> Unit,
) {
	CompositionLocalProvider(LocalLayoutDirection provides direction) {
		content()
	}
}

@Composable
internal fun OtpCodeCard(
	context: Context, codeList: MutableList<OtpCode>, position: Int,
) {
	var showActions by remember { mutableStateOf(false) }
	var code by remember { mutableStateOf(codeList[position]) }

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		enabled = code.expirationTime past code.elapsedTime > 0,
		shape = MaterialTheme.shapes.medium,
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
		colors = CardColors(
			containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
			contentColor = MaterialTheme.colorScheme.surfaceTint,
			disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
			disabledContentColor = MaterialTheme.colorScheme.outline
		),
		onClick = {
			showActions = !showActions
		}
	) {
		Box {
			Column(
				modifier = Modifier.fillMaxWidth()
			) {
				Spacer(modifier = Modifier.height(13.dp))
				AnimatedVisibility(
					code.expirationTime past code.elapsedTime > 0,
					enter = fadeIn(),
					exit = fadeOut()
				) {
					RemainingTime(code.expirationTime past code.elapsedTime, code.expirationTime)
				}
				Column(
					modifier = Modifier
						.padding(16.dp)
						.wrapContentHeight(Alignment.Top)
				) {
					Text(
						code.otp,
						style = MaterialTheme.typography.headlineMedium,
						modifier = Modifier.clickable(code.expirationTime past code.elapsedTime > 0) {
							copySelectedCode(
								context,
								code.otp
							)
						})
					Spacer(modifier = Modifier.height(6.dp))
					AnimatedVisibility(code.price != null) {
						Text(
							modifier = Modifier.padding(bottom = 4.dp),
							text = context.getString(R.string.otp_code_with_price, code.price!!),
							style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outline)
						)
					}
					AnimatedVisibility(code.bank != null) {
						Text(
							text = context.getString(R.string.otp_code_from_bank, code.bank!!),
							style = MaterialTheme.typography.labelLarge.copy(
								color = MaterialTheme.colorScheme.outline,
								fontStyle = FontStyle.Italic
							)
						)
					}
				}
				AnimatedVisibility(visible = showActions) {
					HorizontalDivider(modifier = Modifier.fillMaxWidth())
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(8.dp)
					) {
						Spacer(modifier = Modifier.weight(1f, true))
						OutlinedButton(onClick = { shareSelectedOtpCode(context, code) }) {
							Text(
								stringResource(R.string.otp_card_share),
								style = MaterialTheme.typography.labelSmall
							)
						}
						Spacer(modifier = Modifier.width(8.dp))
						Button(onClick = { copySelectedCode(context, code.otp) }) {
							Text(
								stringResource(R.string.otp_card_copy),
								style = MaterialTheme.typography.labelSmall
							)
						}
						Spacer(modifier = Modifier.width(8.dp))
					}
				}
			}
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.align(Alignment.Center),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				AnimatedVisibility(
					code.expirationTime past code.elapsedTime <= 0,
					enter = fadeIn() + scaleIn(initialScale = 1.1f),
					exit = fadeOut()
				) {
					LaunchedEffect(showActions) {
						showActions = false
					}
					OutlinedCard(
						modifier = Modifier
							.rotate(-14f)
							.scale(1.4f)
							.alpha(0.6f),
						shape = RoundedCornerShape(8.dp),
						border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
						colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent)
					) {
						Text(
							modifier = Modifier.padding(8.dp),
							text = context.getString(R.string.otp_code_expired_2),
							style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
						)
					}
				}
			}
		}
	}
}

@Composable
private fun RemainingTime(
	remainingTime: Long, originTime: Long,
) {
	Row {
		Spacer(modifier = Modifier.width(13.dp))
		Box(modifier = Modifier.size(28.dp)) {
			CircularProgressIndicator(
				progress = { remainingTime div originTime },
				strokeWidth = 2.25.dp,
			)
			Icon(
				painter = painterResource(id = R.drawable.otp_code_expiration_remaining_time),
				contentDescription = stringResource(R.string.code_expiration_time_cd),
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.padding(4.75.dp),
			)
		}
		Spacer(modifier = Modifier.width(5.dp))
		Text(
			printTime(remainingTime), style = MaterialTheme.typography.bodyLarge.copy(
				fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 18.sp
			), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Bottom)
		)
	}
}

internal fun printTime(t: Long): String {
	val minutes = t / 60000
	val seconds = (t % 60000) / 1000
	fun twoDigit(number: Long) = if (number < 10) "0" else ""
	return "${twoDigit(minutes)}$minutes:${twoDigit(seconds)}$seconds"
}


@Preview(
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun OtpCardPreview() {
	val context = LocalContext.current
	val otpCode = OtpCode(
		id = 0,
		"4729912",
		"صادرات ایران",
		"1,222,222",
		1697436005137,
		expirationTime = MAX_OTP_SMS_EXPIRATION_TIME,
		elapsedTime = MAX_OTP_SMS_EXPIRATION_TIME
	)
	PuyaKhanTheme {
		OtpCodeCard(context, mutableListOf(otpCode), 0)
	}
}
