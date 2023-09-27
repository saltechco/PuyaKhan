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


@Composable
fun PermissionNeeded(): ImageVector {
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
