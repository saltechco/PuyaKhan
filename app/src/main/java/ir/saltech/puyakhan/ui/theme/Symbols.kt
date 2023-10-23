package ir.saltech.puyakhan.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal object Symbols {
	object Default {
		val Copy: ImageVector
			@Composable get() = rememberContentCopy()
		val Notify: ImageVector
			@Composable get() = rememberNotificationImportant()
		val Window: ImageVector
			@Composable get() = rememberSelectWindow()
		val Disclaimer: ImageVector
			@Composable get() = rememberBalance()
		val NewTab: ImageVector
			@Composable get() = rememberOpenInNew()
		val Info: ImageVector
			@Composable get() = rememberInfo()
		val Settings: ImageVector
			@Composable get() = rememberSettings()
		val Back: ImageVector
			@Composable get() = rememberArrowBack()
		val PermissionNeeded: ImageVector
			@Composable get() = rememberPermissionNeeded()
		val Rate: ImageVector
			@Composable get() = rememberStarRate()
		val Home: ImageVector
			@Composable get() = rememberHome()
	}

	object Filled {
		val Info: ImageVector
			@Composable get() = rememberFilledInfo()
		val Rate: ImageVector
			@Composable get() = rememberFilledStarRate()
		val Home: ImageVector
			@Composable get() = rememberFilledHome()
	}
}

@Composable
private fun rememberPermissionNeeded(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "gpp_maybe",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(20f, 20.5f)
				quadToRelative(0.583f, 0f, 1f, -0.396f)
				quadToRelative(0.417f, -0.396f, 0.417f, -0.979f)
				verticalLineTo(13.25f)
				quadToRelative(0f, -0.542f, -0.417f, -0.937f)
				quadToRelative(-0.417f, -0.396f, -1f, -0.396f)
				reflectiveQuadToRelative(-0.979f, 0.396f)
				quadToRelative(-0.396f, 0.395f, -0.396f, 0.937f)
				verticalLineToRelative(5.875f)
				quadToRelative(0f, 0.583f, 0.396f, 0.979f)
				reflectiveQuadTo(20f, 20.5f)
				close()
				moveToRelative(0f, 5.792f)
				quadToRelative(0.667f, 0f, 1.125f, -0.438f)
				quadToRelative(0.458f, -0.437f, 0.458f, -1.146f)
				quadToRelative(0f, -0.666f, -0.437f, -1.125f)
				quadToRelative(-0.438f, -0.458f, -1.146f, -0.458f)
				quadToRelative(-0.667f, 0f, -1.125f, 0.458f)
				quadToRelative(-0.458f, 0.459f, -0.458f, 1.125f)
				quadToRelative(0f, 0.667f, 0.437f, 1.125f)
				quadToRelative(0.438f, 0.459f, 1.146f, 0.459f)
				close()
				moveToRelative(0f, 10.25f)
				quadToRelative(-0.208f, 0f, -0.396f, -0.042f)
				quadToRelative(-0.187f, -0.042f, -0.312f, -0.083f)
				quadToRelative(-5.584f, -1.667f, -9.125f, -6.813f)
				quadToRelative(-3.542f, -5.146f, -3.542f, -11.312f)
				verticalLineToRelative(-7.875f)
				quadToRelative(0f, -0.959f, 0.542f, -1.75f)
				quadToRelative(0.541f, -0.792f, 1.416f, -1.125f)
				lineToRelative(10.375f, -3.875f)
				quadTo(19.458f, 3.5f, 20f, 3.5f)
				reflectiveQuadToRelative(1.083f, 0.167f)
				lineToRelative(10.334f, 3.875f)
				quadToRelative(0.875f, 0.333f, 1.416f, 1.125f)
				quadToRelative(0.542f, 0.791f, 0.542f, 1.75f)
				verticalLineToRelative(7.875f)
				quadToRelative(0f, 6.166f, -3.542f, 11.312f)
				quadToRelative(-3.541f, 5.146f, -9.125f, 6.813f)
				quadToRelative(0.042f, 0f, -0.708f, 0.125f)
				close()
				moveToRelative(0f, -3f)
				quadToRelative(4.542f, -1.584f, 7.438f, -5.792f)
				quadToRelative(2.895f, -4.208f, 2.895f, -9.458f)
				verticalLineToRelative(-7.875f)
				lineTo(20f, 6.5f)
				lineTo(9.667f, 10.417f)
				verticalLineToRelative(7.875f)
				quadToRelative(0f, 5.25f, 2.895f, 9.458f)
				quadToRelative(2.896f, 4.208f, 7.438f, 5.792f)
				close()
				moveTo(20f, 20f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberContentCopy(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "content_copy",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(13.292f, 30.958f)
				quadToRelative(-1.084f, 0f, -1.875f, -0.77f)
				quadToRelative(-0.792f, -0.771f, -0.792f, -1.855f)
				verticalLineToRelative(-22f)
				quadToRelative(0f, -1.083f, 0.792f, -1.854f)
				quadToRelative(0.791f, -0.771f, 1.875f, -0.771f)
				horizontalLineToRelative(17.083f)
				quadToRelative(1.083f, 0f, 1.854f, 0.771f)
				quadTo(33f, 5.25f, 33f, 6.333f)
				verticalLineToRelative(22f)
				quadToRelative(0f, 1.084f, -0.771f, 1.855f)
				quadToRelative(-0.771f, 0.77f, -1.854f, 0.77f)
				close()
				moveToRelative(0f, -2.625f)
				horizontalLineToRelative(17.083f)
				verticalLineToRelative(-22f)
				horizontalLineTo(13.292f)
				verticalLineToRelative(22f)
				close()
				moveTo(8f, 36.25f)
				quadToRelative(-1.083f, 0f, -1.854f, -0.771f)
				quadToRelative(-0.771f, -0.771f, -0.771f, -1.854f)
				verticalLineTo(10.792f)
				quadToRelative(0f, -0.542f, 0.375f, -0.938f)
				quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
				quadToRelative(0.583f, 0f, 0.958f, 0.396f)
				reflectiveQuadToRelative(0.375f, 0.938f)
				verticalLineToRelative(22.833f)
				horizontalLineToRelative(17.625f)
				quadToRelative(0.5f, 0f, 0.896f, 0.375f)
				reflectiveQuadToRelative(0.396f, 0.917f)
				quadToRelative(0f, 0.583f, -0.396f, 0.958f)
				reflectiveQuadToRelative(-0.896f, 0.375f)
				close()
				moveToRelative(5.292f, -29.917f)
				verticalLineToRelative(22f)
				verticalLineToRelative(-22f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberNotificationImportant(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "notification_important",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(20f, 22.083f)
				quadToRelative(0.542f, 0f, 0.938f, -0.375f)
				quadToRelative(0.395f, -0.375f, 0.395f, -0.958f)
				verticalLineToRelative(-5.958f)
				quadToRelative(0f, -0.542f, -0.395f, -0.938f)
				quadToRelative(-0.396f, -0.396f, -0.938f, -0.396f)
				quadToRelative(-0.542f, 0f, -0.917f, 0.396f)
				reflectiveQuadToRelative(-0.375f, 0.938f)
				verticalLineToRelative(5.958f)
				quadToRelative(0f, 0.583f, 0.375f, 0.958f)
				reflectiveQuadToRelative(0.917f, 0.375f)
				close()
				moveToRelative(0f, 4.5f)
				quadToRelative(0.583f, 0f, 1f, -0.416f)
				quadToRelative(0.417f, -0.417f, 0.417f, -1f)
				quadToRelative(0f, -0.584f, -0.417f, -1f)
				quadToRelative(-0.417f, -0.417f, -1f, -0.417f)
				reflectiveQuadToRelative(-1f, 0.417f)
				quadToRelative(-0.417f, 0.416f, -0.417f, 1f)
				quadToRelative(0f, 0.583f, 0.417f, 1f)
				quadToRelative(0.417f, 0.416f, 1f, 0.416f)
				close()
				moveTo(8.167f, 31.625f)
				quadToRelative(-0.542f, 0f, -0.938f, -0.396f)
				quadToRelative(-0.396f, -0.396f, -0.396f, -0.937f)
				quadToRelative(0f, -0.542f, 0.396f, -0.938f)
				quadToRelative(0.396f, -0.396f, 0.938f, -0.396f)
				horizontalLineToRelative(2.125f)
				verticalLineTo(16.542f)
				quadToRelative(0f, -3.375f, 2.041f, -6.084f)
				quadToRelative(2.042f, -2.708f, 5.334f, -3.416f)
				verticalLineTo(5.875f)
				quadToRelative(0f, -1f, 0.666f, -1.625f)
				quadTo(19f, 3.625f, 20f, 3.625f)
				quadToRelative(0.958f, 0f, 1.646f, 0.625f)
				quadToRelative(0.687f, 0.625f, 0.687f, 1.625f)
				verticalLineToRelative(1.167f)
				quadToRelative(3.292f, 0.708f, 5.375f, 3.416f)
				quadToRelative(2.084f, 2.709f, 2.084f, 6.084f)
				verticalLineToRelative(12.416f)
				horizontalLineToRelative(2.041f)
				quadToRelative(0.542f, 0f, 0.938f, 0.396f)
				quadToRelative(0.396f, 0.396f, 0.396f, 0.938f)
				quadToRelative(0f, 0.541f, -0.396f, 0.937f)
				reflectiveQuadToRelative(-0.938f, 0.396f)
				close()
				moveTo(20f, 19.375f)
				close()
				moveToRelative(0f, 17.083f)
				quadToRelative(-1.333f, 0f, -2.312f, -0.958f)
				quadToRelative(-0.98f, -0.958f, -0.98f, -2.292f)
				horizontalLineToRelative(6.584f)
				quadToRelative(0f, 1.334f, -0.959f, 2.292f)
				quadToRelative(-0.958f, 0.958f, -2.333f, 0.958f)
				close()
				moveToRelative(-7.083f, -7.5f)
				horizontalLineToRelative(14.208f)
				verticalLineTo(16.583f)
				quadToRelative(0f, -3.041f, -2.063f, -5.104f)
				quadTo(23f, 9.417f, 20f, 9.417f)
				quadToRelative(-2.958f, 0f, -5.021f, 2.083f)
				quadToRelative(-2.062f, 2.083f, -2.062f, 5.042f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberSelectWindow(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "select_window",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(149.833f, -87.333f)
				quadToRelative(-25.625f, 0f, -44.479f, -18.854f)
				reflectiveQuadTo(86.5f, -150.666f)
				verticalLineToRelative(-374.638f)
				quadToRelative(0f, -25.134f, 18.854f, -44.165f)
				reflectiveQuadToRelative(44.479f, -19.031f)
				horizontalLineToRelative(104.834f)
				verticalLineToRelative(-221.334f)
				quadToRelative(0f, -25.27f, 18.854f, -44.302f)
				quadToRelative(18.854f, -19.031f, 44.479f, -19.031f)
				horizontalLineToRelative(492f)
				quadToRelative(25.625f, 0f, 44.479f, 19.031f)
				quadToRelative(18.854f, 19.032f, 18.854f, 44.165f)
				verticalLineToRelative(374.638f)
				quadToRelative(0f, 25.625f, -18.854f, 44.479f)
				reflectiveQuadTo(810f, -372f)
				horizontalLineTo(705.833f)
				verticalLineToRelative(221.334f)
				quadToRelative(0f, 25.625f, -19.031f, 44.479f)
				reflectiveQuadToRelative(-44.14f, 18.854f)
				horizontalLineTo(149.833f)
				close()
				moveToRelative(0f, -63.333f)
				horizontalLineTo(642.5f)
				verticalLineToRelative(-311.168f)
				horizontalLineTo(149.833f)
				verticalLineToRelative(311.168f)
				close()
				moveToRelative(556f, -284.667f)
				horizontalLineTo(810f)
				verticalLineToRelative(-311.168f)
				horizontalLineTo(318f)
				verticalLineTo(-588.5f)
				horizontalLineToRelative(313.417f)
				quadToRelative(31.552f, 0f, 52.984f, 21.466f)
				quadToRelative(21.432f, 21.466f, 21.432f, 53.284f)
				verticalLineToRelative(78.417f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberBalance(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "balance",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(4.708f, 34.917f)
				quadToRelative(-0.541f, 0f, -0.916f, -0.375f)
				reflectiveQuadToRelative(-0.375f, -0.917f)
				quadToRelative(0f, -0.583f, 0.375f, -0.958f)
				reflectiveQuadToRelative(0.916f, -0.375f)
				horizontalLineToRelative(14f)
				verticalLineTo(12.5f)
				quadToRelative(-1.125f, -0.375f, -1.958f, -1.208f)
				quadToRelative(-0.833f, -0.834f, -1.208f, -1.917f)
				horizontalLineTo(9.208f)
				lineToRelative(4.75f, 11.375f)
				quadToRelative(0.209f, 0.5f, 0.292f, 1.104f)
				quadToRelative(0.083f, 0.604f, -0.042f, 0.938f)
				quadToRelative(-0.416f, 1.5f, -1.916f, 2.5f)
				reflectiveQuadToRelative(-3.417f, 1f)
				quadToRelative(-1.917f, 0f, -3.396f, -1f)
				quadToRelative(-1.479f, -1f, -1.937f, -2.5f)
				quadToRelative(-0.084f, -0.334f, 0f, -0.938f)
				quadToRelative(0.083f, -0.604f, 0.25f, -1.104f)
				lineTo(8.583f, 9.375f)
				horizontalLineTo(6.375f)
				quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
				reflectiveQuadToRelative(-0.375f, -0.958f)
				quadToRelative(0f, -0.542f, 0.375f, -0.938f)
				quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
				horizontalLineToRelative(9.167f)
				quadToRelative(0.5f, -1.458f, 1.729f, -2.396f)
				quadTo(18.5f, 3.375f, 20f, 3.375f)
				reflectiveQuadToRelative(2.729f, 0.937f)
				quadToRelative(1.229f, 0.938f, 1.729f, 2.396f)
				horizontalLineToRelative(9.167f)
				quadToRelative(0.542f, 0f, 0.937f, 0.396f)
				quadToRelative(0.396f, 0.396f, 0.396f, 0.938f)
				quadToRelative(0f, 0.583f, -0.396f, 0.958f)
				quadToRelative(-0.395f, 0.375f, -0.937f, 0.375f)
				horizontalLineToRelative(-2.208f)
				lineToRelative(4.791f, 11.375f)
				quadToRelative(0.167f, 0.5f, 0.25f, 1.104f)
				quadToRelative(0.084f, 0.604f, 0f, 0.938f)
				quadToRelative(-0.458f, 1.5f, -1.937f, 2.5f)
				reflectiveQuadToRelative(-3.396f, 1f)
				quadToRelative(-1.917f, 0f, -3.417f, -1f)
				quadToRelative(-1.5f, -1f, -1.916f, -2.5f)
				quadToRelative(-0.125f, -0.334f, -0.042f, -0.938f)
				quadToRelative(0.083f, -0.604f, 0.292f, -1.104f)
				lineToRelative(4.75f, -11.375f)
				horizontalLineToRelative(-6.334f)
				quadToRelative(-0.375f, 1.083f, -1.208f, 1.917f)
				quadToRelative(-0.833f, 0.833f, -1.917f, 1.208f)
				verticalLineToRelative(19.792f)
				horizontalLineToRelative(13.959f)
				quadToRelative(0.541f, 0f, 0.937f, 0.375f)
				reflectiveQuadToRelative(0.396f, 0.958f)
				quadToRelative(0f, 0.542f, -0.396f, 0.917f)
				reflectiveQuadToRelative(-0.937f, 0.375f)
				close()
				moveToRelative(23.167f, -13.209f)
				horizontalLineToRelative(6.458f)
				lineToRelative(-3.208f, -7.75f)
				close()
				moveToRelative(-22.208f, 0f)
				horizontalLineToRelative(6.458f)
				lineToRelative(-3.25f, -7.75f)
				close()
				moveTo(20f, 10.042f)
				quadToRelative(0.833f, 0f, 1.438f, -0.584f)
				quadToRelative(0.604f, -0.583f, 0.604f, -1.416f)
				quadToRelative(0f, -0.834f, -0.604f, -1.417f)
				quadToRelative(-0.605f, -0.583f, -1.438f, -0.583f)
				reflectiveQuadToRelative(-1.417f, 0.583f)
				quadTo(18f, 7.208f, 18f, 8.042f)
				quadToRelative(0f, 0.833f, 0.583f, 1.416f)
				quadToRelative(0.584f, 0.584f, 1.417f, 0.584f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberOpenInNew(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "open_in_new",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(7.875f, 34.75f)
				quadToRelative(-1.042f, 0f, -1.833f, -0.792f)
				quadToRelative(-0.792f, -0.791f, -0.792f, -1.833f)
				verticalLineTo(7.875f)
				quadToRelative(0f, -1.042f, 0.792f, -1.833f)
				quadToRelative(0.791f, -0.792f, 1.833f, -0.792f)
				horizontalLineTo(18f)
				quadToRelative(0.542f, 0f, 0.938f, 0.396f)
				quadToRelative(0.395f, 0.396f, 0.395f, 0.937f)
				quadToRelative(0f, 0.542f, -0.395f, 0.917f)
				quadToRelative(-0.396f, 0.375f, -0.938f, 0.375f)
				horizontalLineTo(7.875f)
				verticalLineToRelative(24.25f)
				horizontalLineToRelative(24.25f)
				verticalLineTo(22f)
				quadToRelative(0f, -0.542f, 0.375f, -0.938f)
				quadToRelative(0.375f, -0.395f, 0.917f, -0.395f)
				quadToRelative(0.583f, 0f, 0.958f, 0.395f)
				quadToRelative(0.375f, 0.396f, 0.375f, 0.938f)
				verticalLineToRelative(10.125f)
				quadToRelative(0f, 1.042f, -0.792f, 1.833f)
				quadToRelative(-0.791f, 0.792f, -1.833f, 0.792f)
				close()
				moveToRelative(7.25f, -9.875f)
				quadToRelative(-0.375f, -0.375f, -0.375f, -0.917f)
				quadToRelative(0f, -0.541f, 0.375f, -0.916f)
				lineTo(30.292f, 7.875f)
				horizontalLineToRelative(-7f)
				quadToRelative(-0.542f, 0f, -0.938f, -0.375f)
				quadToRelative(-0.396f, -0.375f, -0.396f, -0.917f)
				quadToRelative(0f, -0.583f, 0.396f, -0.958f)
				reflectiveQuadToRelative(0.938f, -0.375f)
				horizontalLineToRelative(10.166f)
				quadToRelative(0.542f, 0f, 0.917f, 0.375f)
				reflectiveQuadToRelative(0.375f, 0.917f)
				verticalLineToRelative(10.166f)
				quadToRelative(0f, 0.542f, -0.396f, 0.938f)
				quadToRelative(-0.396f, 0.396f, -0.937f, 0.396f)
				quadToRelative(-0.542f, 0f, -0.917f, -0.396f)
				reflectiveQuadToRelative(-0.375f, -0.938f)
				verticalLineTo(9.75f)
				lineTo(16.958f, 24.917f)
				quadToRelative(-0.375f, 0.375f, -0.896f, 0.375f)
				quadToRelative(-0.52f, 0f, -0.937f, -0.417f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberInfo(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "info",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(20.083f, 28.208f)
				quadToRelative(0.584f, 0f, 0.959f, -0.396f)
				quadToRelative(0.375f, -0.395f, 0.375f, -0.937f)
				verticalLineToRelative(-7.25f)
				quadToRelative(0f, -0.5f, -0.396f, -0.875f)
				reflectiveQuadToRelative(-0.896f, -0.375f)
				quadToRelative(-0.583f, 0f, -0.958f, 0.375f)
				reflectiveQuadToRelative(-0.375f, 0.917f)
				verticalLineToRelative(7.25f)
				quadToRelative(0f, 0.541f, 0.396f, 0.916f)
				quadToRelative(0.395f, 0.375f, 0.895f, 0.375f)
				close()
				moveTo(20f, 15.292f)
				quadToRelative(0.583f, 0f, 1f, -0.396f)
				quadToRelative(0.417f, -0.396f, 0.417f, -1.021f)
				quadToRelative(0f, -0.583f, -0.417f, -1f)
				quadToRelative(-0.417f, -0.417f, -1f, -0.417f)
				quadToRelative(-0.625f, 0f, -1.021f, 0.417f)
				quadToRelative(-0.396f, 0.417f, -0.396f, 1f)
				quadToRelative(0f, 0.625f, 0.417f, 1.021f)
				quadToRelative(0.417f, 0.396f, 1f, 0.396f)
				close()
				moveToRelative(0f, 21.083f)
				quadToRelative(-3.458f, 0f, -6.458f, -1.25f)
				reflectiveQuadToRelative(-5.209f, -3.458f)
				quadToRelative(-2.208f, -2.209f, -3.458f, -5.209f)
				quadToRelative(-1.25f, -3f, -1.25f, -6.458f)
				reflectiveQuadToRelative(1.25f, -6.437f)
				quadToRelative(1.25f, -2.98f, 3.458f, -5.188f)
				quadToRelative(2.209f, -2.208f, 5.209f, -3.479f)
				quadToRelative(3f, -1.271f, 6.458f, -1.271f)
				reflectiveQuadToRelative(6.438f, 1.271f)
				quadToRelative(2.979f, 1.271f, 5.187f, 3.479f)
				reflectiveQuadToRelative(3.479f, 5.188f)
				quadToRelative(1.271f, 2.979f, 1.271f, 6.437f)
				reflectiveQuadToRelative(-1.271f, 6.458f)
				quadToRelative(-1.271f, 3f, -3.479f, 5.209f)
				quadToRelative(-2.208f, 2.208f, -5.187f, 3.458f)
				quadToRelative(-2.98f, 1.25f, -6.438f, 1.25f)
				close()
				moveTo(20f, 20f)
				close()
				moveToRelative(0f, 13.75f)
				quadToRelative(5.667f, 0f, 9.708f, -4.042f)
				quadTo(33.75f, 25.667f, 33.75f, 20f)
				reflectiveQuadToRelative(-4.042f, -9.708f)
				quadTo(25.667f, 6.25f, 20f, 6.25f)
				reflectiveQuadToRelative(-9.708f, 4.042f)
				quadTo(6.25f, 14.333f, 6.25f, 20f)
				reflectiveQuadToRelative(4.042f, 9.708f)
				quadTo(14.333f, 33.75f, 20f, 33.75f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberFilledInfo(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "info",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(20.083f, 28.208f)
				quadToRelative(0.584f, 0f, 0.959f, -0.396f)
				quadToRelative(0.375f, -0.395f, 0.375f, -0.937f)
				verticalLineToRelative(-7.25f)
				quadToRelative(0f, -0.5f, -0.396f, -0.875f)
				reflectiveQuadToRelative(-0.896f, -0.375f)
				quadToRelative(-0.583f, 0f, -0.958f, 0.375f)
				reflectiveQuadToRelative(-0.375f, 0.917f)
				verticalLineToRelative(7.25f)
				quadToRelative(0f, 0.541f, 0.396f, 0.916f)
				quadToRelative(0.395f, 0.375f, 0.895f, 0.375f)
				close()
				moveTo(20f, 15.292f)
				quadToRelative(0.583f, 0f, 1f, -0.396f)
				quadToRelative(0.417f, -0.396f, 0.417f, -1.021f)
				quadToRelative(0f, -0.583f, -0.417f, -1f)
				quadToRelative(-0.417f, -0.417f, -1f, -0.417f)
				quadToRelative(-0.625f, 0f, -1.021f, 0.417f)
				quadToRelative(-0.396f, 0.417f, -0.396f, 1f)
				quadToRelative(0f, 0.625f, 0.417f, 1.021f)
				quadToRelative(0.417f, 0.396f, 1f, 0.396f)
				close()
				moveToRelative(0f, 21.083f)
				quadToRelative(-3.458f, 0f, -6.458f, -1.25f)
				reflectiveQuadToRelative(-5.209f, -3.458f)
				quadToRelative(-2.208f, -2.209f, -3.458f, -5.209f)
				quadToRelative(-1.25f, -3f, -1.25f, -6.458f)
				reflectiveQuadToRelative(1.25f, -6.437f)
				quadToRelative(1.25f, -2.98f, 3.458f, -5.188f)
				quadToRelative(2.209f, -2.208f, 5.209f, -3.479f)
				quadToRelative(3f, -1.271f, 6.458f, -1.271f)
				reflectiveQuadToRelative(6.438f, 1.271f)
				quadToRelative(2.979f, 1.271f, 5.187f, 3.479f)
				reflectiveQuadToRelative(3.479f, 5.188f)
				quadToRelative(1.271f, 2.979f, 1.271f, 6.437f)
				reflectiveQuadToRelative(-1.271f, 6.458f)
				quadToRelative(-1.271f, 3f, -3.479f, 5.209f)
				quadToRelative(-2.208f, 2.208f, -5.187f, 3.458f)
				quadToRelative(-2.98f, 1.25f, -6.438f, 1.25f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberSettings(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "settings",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(22.792f, 36.375f)
				horizontalLineToRelative(-5.584f)
				quadToRelative(-0.5f, 0f, -0.875f, -0.313f)
				quadToRelative(-0.375f, -0.312f, -0.416f, -0.77f)
				lineToRelative(-0.625f, -4.084f)
				quadToRelative(-0.375f, -0.125f, -1.167f, -0.583f)
				quadToRelative(-0.792f, -0.458f, -1.708f, -1.042f)
				lineToRelative(-3.75f, 1.667f)
				quadToRelative(-0.5f, 0.208f, -0.979f, 0.042f)
				quadToRelative(-0.48f, -0.167f, -0.73f, -0.625f)
				lineTo(4.167f, 25.75f)
				quadToRelative(-0.25f, -0.417f, -0.125f, -0.917f)
				reflectiveQuadToRelative(0.5f, -0.791f)
				lineTo(8f, 21.5f)
				quadToRelative(-0.083f, -0.333f, -0.104f, -0.708f)
				quadToRelative(-0.021f, -0.375f, -0.021f, -0.792f)
				quadToRelative(0f, -0.333f, 0.021f, -0.75f)
				reflectiveQuadTo(8f, 18.417f)
				lineToRelative(-3.458f, -2.5f)
				quadToRelative(-0.375f, -0.292f, -0.521f, -0.771f)
				quadToRelative(-0.146f, -0.479f, 0.146f, -0.938f)
				lineToRelative(2.791f, -4.916f)
				quadToRelative(0.292f, -0.459f, 0.771f, -0.604f)
				quadToRelative(0.479f, -0.146f, 0.938f, 0.062f)
				lineToRelative(3.791f, 1.708f)
				quadTo(13f, 10f, 13.771f, 9.542f)
				quadToRelative(0.771f, -0.459f, 1.521f, -0.709f)
				lineToRelative(0.625f, -4.166f)
				quadToRelative(0.041f, -0.459f, 0.416f, -0.771f)
				quadToRelative(0.375f, -0.313f, 0.875f, -0.313f)
				horizontalLineToRelative(5.584f)
				quadToRelative(0.5f, 0f, 0.875f, 0.313f)
				quadToRelative(0.375f, 0.312f, 0.416f, 0.771f)
				lineToRelative(0.625f, 4.125f)
				quadToRelative(0.709f, 0.25f, 1.48f, 0.687f)
				quadToRelative(0.77f, 0.438f, 1.354f, 0.979f)
				lineToRelative(3.833f, -1.708f)
				quadToRelative(0.458f, -0.208f, 0.917f, -0.042f)
				quadToRelative(0.458f, 0.167f, 0.75f, 0.584f)
				lineToRelative(2.791f, 4.916f)
				quadToRelative(0.292f, 0.417f, 0.167f, 0.917f)
				quadToRelative(-0.125f, 0.5f, -0.542f, 0.792f)
				lineToRelative(-3.5f, 2.5f)
				quadToRelative(0.042f, 0.375f, 0.063f, 0.771f)
				quadToRelative(0.021f, 0.395f, 0.021f, 0.812f)
				quadToRelative(0f, 0.417f, -0.021f, 0.812f)
				quadToRelative(-0.021f, 0.396f, -0.063f, 0.73f)
				lineToRelative(3.459f, 2.5f)
				quadToRelative(0.416f, 0.291f, 0.541f, 0.791f)
				quadToRelative(0.125f, 0.5f, -0.125f, 0.917f)
				lineTo(33f, 30.708f)
				quadToRelative(-0.25f, 0.459f, -0.729f, 0.604f)
				quadToRelative(-0.479f, 0.146f, -0.938f, -0.062f)
				lineToRelative(-3.791f, -1.708f)
				quadToRelative(-0.542f, 0.458f, -1.271f, 0.896f)
				quadToRelative(-0.729f, 0.437f, -1.563f, 0.77f)
				lineToRelative(-0.625f, 4.084f)
				quadToRelative(-0.041f, 0.458f, -0.416f, 0.77f)
				quadToRelative(-0.375f, 0.313f, -0.875f, 0.313f)
				close()
				moveToRelative(-2.834f, -10.958f)
				quadToRelative(2.25f, 0f, 3.834f, -1.584f)
				quadTo(25.375f, 22.25f, 25.375f, 20f)
				reflectiveQuadToRelative(-1.583f, -3.833f)
				quadToRelative(-1.584f, -1.584f, -3.834f, -1.584f)
				reflectiveQuadToRelative(-3.833f, 1.584f)
				quadTo(14.542f, 17.75f, 14.542f, 20f)
				reflectiveQuadToRelative(1.583f, 3.833f)
				quadToRelative(1.583f, 1.584f, 3.833f, 1.584f)
				close()
				moveToRelative(0f, -2.625f)
				quadToRelative(-1.166f, 0f, -1.979f, -0.813f)
				quadToRelative(-0.812f, -0.812f, -0.812f, -1.979f)
				reflectiveQuadToRelative(0.812f, -1.979f)
				quadToRelative(0.813f, -0.813f, 1.979f, -0.813f)
				quadToRelative(1.167f, 0f, 1.98f, 0.813f)
				quadToRelative(0.812f, 0.812f, 0.812f, 1.979f)
				reflectiveQuadToRelative(-0.812f, 1.979f)
				quadToRelative(-0.813f, 0.813f, -1.98f, 0.813f)
				close()
				moveTo(20f, 19.958f)
				close()
				moveTo(18.25f, 33.75f)
				horizontalLineToRelative(3.5f)
				lineToRelative(0.583f, -4.583f)
				quadToRelative(1.334f, -0.375f, 2.479f, -1.021f)
				quadToRelative(1.146f, -0.646f, 2.105f, -1.646f)
				lineToRelative(4.333f, 1.875f)
				lineToRelative(1.625f, -2.917f)
				lineToRelative(-3.833f, -2.791f)
				quadToRelative(0.166f, -0.667f, 0.27f, -1.334f)
				quadToRelative(0.105f, -0.666f, 0.105f, -1.333f)
				quadToRelative(0f, -0.708f, -0.084f, -1.333f)
				quadToRelative(-0.083f, -0.625f, -0.291f, -1.334f)
				lineToRelative(3.833f, -2.833f)
				lineToRelative(-1.583f, -2.917f)
				lineToRelative(-4.375f, 1.875f)
				quadTo(26f, 12.5f, 24.833f, 11.792f)
				quadToRelative(-1.166f, -0.709f, -2.5f, -0.959f)
				lineToRelative(-0.541f, -4.625f)
				horizontalLineTo(18.25f)
				lineToRelative(-0.583f, 4.625f)
				quadToRelative(-1.417f, 0.334f, -2.521f, 0.959f)
				quadToRelative(-1.104f, 0.625f, -2.104f, 1.666f)
				lineTo(8.75f, 11.583f)
				lineTo(7.083f, 14.5f)
				lineToRelative(3.792f, 2.792f)
				quadToRelative(-0.167f, 0.708f, -0.271f, 1.354f)
				quadToRelative(-0.104f, 0.646f, -0.104f, 1.312f)
				quadToRelative(0f, 0.709f, 0.104f, 1.354f)
				quadToRelative(0.104f, 0.646f, 0.271f, 1.355f)
				lineToRelative(-3.792f, 2.791f)
				lineToRelative(1.667f, 2.917f)
				lineToRelative(4.292f, -1.833f)
				quadToRelative(1f, 1f, 2.146f, 1.646f)
				quadToRelative(1.145f, 0.645f, 2.479f, 0.979f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberArrowBack(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "arrow_back",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(18.542f, 32.208f)
				lineTo(7.25f, 20.917f)
				quadToRelative(-0.208f, -0.209f, -0.292f, -0.438f)
				quadToRelative(-0.083f, -0.229f, -0.083f, -0.479f)
				quadToRelative(0f, -0.25f, 0.083f, -0.479f)
				quadToRelative(0.084f, -0.229f, 0.292f, -0.438f)
				lineTo(18.583f, 7.75f)
				quadToRelative(0.375f, -0.333f, 0.896f, -0.333f)
				reflectiveQuadToRelative(0.938f, 0.375f)
				quadToRelative(0.375f, 0.416f, 0.375f, 0.937f)
				quadToRelative(0f, 0.521f, -0.375f, 0.938f)
				lineToRelative(-9.042f, 9f)
				horizontalLineToRelative(19.917f)
				quadToRelative(0.541f, 0f, 0.916f, 0.395f)
				quadToRelative(0.375f, 0.396f, 0.375f, 0.938f)
				quadToRelative(0f, 0.542f, -0.375f, 0.917f)
				reflectiveQuadToRelative(-0.916f, 0.375f)
				horizontalLineTo(11.375f)
				lineToRelative(9.083f, 9.083f)
				quadToRelative(0.334f, 0.375f, 0.334f, 0.896f)
				reflectiveQuadToRelative(-0.375f, 0.937f)
				quadToRelative(-0.417f, 0.375f, -0.938f, 0.375f)
				quadToRelative(-0.521f, 0f, -0.937f, -0.375f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberStarRate(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "star_rate",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(14.875f, 29.125f)
				lineTo(20f, 25.208f)
				lineToRelative(5.125f, 3.917f)
				lineToRelative(-2.042f, -6.458f)
				lineToRelative(4.75f, -3.167f)
				horizontalLineToRelative(-5.75f)
				lineTo(20f, 13.042f)
				lineTo(17.917f, 19.5f)
				horizontalLineToRelative(-5.75f)
				lineToRelative(4.75f, 3.167f)
				close()
				moveToRelative(5.125f, -0.5f)
				lineToRelative(-6.75f, 5.125f)
				quadToRelative(-0.375f, 0.333f, -0.792f, 0.292f)
				quadToRelative(-0.416f, -0.042f, -0.75f, -0.292f)
				quadToRelative(-0.333f, -0.208f, -0.5f, -0.583f)
				quadToRelative(-0.166f, -0.375f, 0f, -0.875f)
				lineToRelative(2.584f, -8.334f)
				lineToRelative(-6.625f, -4.791f)
				quadToRelative(-0.417f, -0.25f, -0.521f, -0.667f)
				quadToRelative(-0.104f, -0.417f, 0.021f, -0.792f)
				quadToRelative(0.125f, -0.375f, 0.437f, -0.646f)
				quadToRelative(0.313f, -0.27f, 0.813f, -0.27f)
				horizontalLineToRelative(8.208f)
				lineToRelative(2.625f, -8.709f)
				quadToRelative(0.125f, -0.5f, 0.5f, -0.729f)
				quadToRelative(0.375f, -0.229f, 0.75f, -0.229f)
				reflectiveQuadToRelative(0.75f, 0.229f)
				quadToRelative(0.375f, 0.229f, 0.5f, 0.729f)
				lineToRelative(2.625f, 8.709f)
				horizontalLineToRelative(8.208f)
				quadToRelative(0.5f, 0f, 0.813f, 0.27f)
				quadToRelative(0.312f, 0.271f, 0.437f, 0.646f)
				reflectiveQuadToRelative(0.021f, 0.792f)
				quadToRelative(-0.104f, 0.417f, -0.521f, 0.667f)
				lineToRelative(-6.625f, 4.791f)
				lineToRelative(2.584f, 8.334f)
				quadToRelative(0.166f, 0.5f, 0f, 0.875f)
				quadToRelative(-0.167f, 0.375f, -0.5f, 0.583f)
				quadToRelative(-0.334f, 0.25f, -0.75f, 0.292f)
				quadToRelative(-0.417f, 0.041f, -0.792f, -0.292f)
				close()
				moveToRelative(0f, -7.542f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberFilledStarRate(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "star_rate",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(20f, 28.625f)
				lineToRelative(-6.75f, 5.125f)
				quadToRelative(-0.375f, 0.333f, -0.792f, 0.292f)
				quadToRelative(-0.416f, -0.042f, -0.75f, -0.292f)
				quadToRelative(-0.333f, -0.208f, -0.5f, -0.583f)
				quadToRelative(-0.166f, -0.375f, 0f, -0.875f)
				lineToRelative(2.584f, -8.334f)
				lineToRelative(-6.625f, -4.791f)
				quadToRelative(-0.417f, -0.25f, -0.521f, -0.667f)
				quadToRelative(-0.104f, -0.417f, 0.021f, -0.792f)
				quadToRelative(0.125f, -0.375f, 0.437f, -0.646f)
				quadToRelative(0.313f, -0.27f, 0.813f, -0.27f)
				horizontalLineToRelative(8.208f)
				lineToRelative(2.625f, -8.709f)
				quadToRelative(0.125f, -0.5f, 0.5f, -0.729f)
				quadToRelative(0.375f, -0.229f, 0.75f, -0.229f)
				reflectiveQuadToRelative(0.75f, 0.229f)
				quadToRelative(0.375f, 0.229f, 0.5f, 0.729f)
				lineToRelative(2.625f, 8.709f)
				horizontalLineToRelative(8.208f)
				quadToRelative(0.5f, 0f, 0.813f, 0.27f)
				quadToRelative(0.312f, 0.271f, 0.437f, 0.646f)
				reflectiveQuadToRelative(0.021f, 0.792f)
				quadToRelative(-0.104f, 0.417f, -0.521f, 0.667f)
				lineToRelative(-6.625f, 4.791f)
				lineToRelative(2.584f, 8.334f)
				quadToRelative(0.166f, 0.5f, 0f, 0.875f)
				quadToRelative(-0.167f, 0.375f, -0.5f, 0.583f)
				quadToRelative(-0.334f, 0.25f, -0.75f, 0.292f)
				quadToRelative(-0.417f, 0.041f, -0.792f, -0.292f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberHome(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "home",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(9.542f, 32.125f)
				horizontalLineToRelative(5.75f)
				verticalLineToRelative(-10.25f)
				horizontalLineToRelative(9.416f)
				verticalLineToRelative(10.25f)
				horizontalLineToRelative(5.75f)
				verticalLineTo(16.417f)
				lineTo(20f, 8.583f)
				lineTo(9.542f, 16.417f)
				close()
				moveToRelative(0f, 2.625f)
				quadToRelative(-1.084f, 0f, -1.855f, -0.771f)
				quadToRelative(-0.77f, -0.771f, -0.77f, -1.854f)
				verticalLineTo(16.417f)
				quadToRelative(0f, -0.625f, 0.271f, -1.188f)
				quadToRelative(0.27f, -0.562f, 0.77f, -0.937f)
				lineToRelative(10.459f, -7.834f)
				quadToRelative(0.375f, -0.25f, 0.771f, -0.375f)
				quadToRelative(0.395f, -0.125f, 0.812f, -0.125f)
				quadToRelative(0.417f, 0f, 0.812f, 0.125f)
				quadToRelative(0.396f, 0.125f, 0.771f, 0.375f)
				lineToRelative(10.459f, 7.834f)
				quadToRelative(0.5f, 0.375f, 0.77f, 0.937f)
				quadToRelative(0.271f, 0.563f, 0.271f, 1.188f)
				verticalLineToRelative(15.708f)
				quadToRelative(0f, 1.083f, -0.771f, 1.854f)
				quadToRelative(-0.77f, 0.771f, -1.854f, 0.771f)
				horizontalLineToRelative(-8.375f)
				verticalLineTo(24.5f)
				horizontalLineToRelative(-4.166f)
				verticalLineToRelative(10.25f)
				close()
				moveTo(20f, 20.333f)
				close()
			}
		}.build()
	}
}

@Composable
private fun rememberFilledHome(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "home",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(9.542f, 34.75f)
				quadToRelative(-1.084f, 0f, -1.855f, -0.771f)
				quadToRelative(-0.77f, -0.771f, -0.77f, -1.854f)
				verticalLineTo(16.417f)
				quadToRelative(0f, -0.625f, 0.271f, -1.188f)
				quadToRelative(0.27f, -0.562f, 0.77f, -0.937f)
				lineToRelative(10.459f, -7.834f)
				quadToRelative(0.375f, -0.25f, 0.771f, -0.375f)
				quadToRelative(0.395f, -0.125f, 0.812f, -0.125f)
				quadToRelative(0.417f, 0f, 0.812f, 0.125f)
				quadToRelative(0.396f, 0.125f, 0.771f, 0.375f)
				lineToRelative(10.459f, 7.834f)
				quadToRelative(0.5f, 0.375f, 0.77f, 0.937f)
				quadToRelative(0.271f, 0.563f, 0.271f, 1.188f)
				verticalLineToRelative(15.708f)
				quadToRelative(0f, 1.083f, -0.771f, 1.854f)
				quadToRelative(-0.77f, 0.771f, -1.854f, 0.771f)
				horizontalLineToRelative(-7.041f)
				verticalLineTo(23.208f)
				horizontalLineToRelative(-6.792f)
				verticalLineTo(34.75f)
				close()
			}
		}.build()
	}
}
