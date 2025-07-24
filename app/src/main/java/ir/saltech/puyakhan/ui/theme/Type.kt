package ir.saltech.puyakhan.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ir.saltech.puyakhan.R


val Vazir = FontFamily(
	Font(R.font.vazir_regular, FontWeight.Normal),
	Font(R.font.vazir_light, FontWeight.Light)
)

// Set of Material typography styles to start with
val Typography = Typography(
	headlineMedium = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 28.sp,
		lineHeight = 36.sp,
		letterSpacing = 2.sp
	),
	headlineSmall = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 26.sp,
		lineHeight = 32.sp,
		letterSpacing = 2.sp
	),
	displayMedium = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 24.sp,
		lineHeight = 30.sp,
		letterSpacing = 0.5.sp
	),
	displaySmall = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 22.sp,
		lineHeight = 26.sp,
		letterSpacing = 0.5.sp
	),
	bodyLarge = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 20.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.5.sp
	),
	bodyMedium = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 18.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.5.sp
	),
	bodySmall = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		lineHeight = 18.sp,
		letterSpacing = 0.5.sp
	),
	labelLarge = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 14.sp,
		lineHeight = 18.sp,
		letterSpacing = 0.5.sp
	),
	labelMedium = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Light,
		fontSize = 12.sp,
		lineHeight = 18.sp,
		letterSpacing = 0.5.sp
	),
	labelSmall = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Light,
		fontSize = 10.sp,
		lineHeight = 14.sp,
		letterSpacing = 0.5.sp
	)
)
