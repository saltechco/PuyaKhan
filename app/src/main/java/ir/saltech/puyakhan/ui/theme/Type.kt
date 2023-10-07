package ir.saltech.puyakhan.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ir.saltech.puyakhan.R


val Vazir = FontFamily(
	Font(R.font.vazir_black, FontWeight.Black),
	Font(R.font.vazir_bold, FontWeight.Bold),
	Font(R.font.vazir_light, FontWeight.Light),
	Font(R.font.vazir_regular, FontWeight.Normal)
)

// Set of Material typography styles to start with
val Typography = Typography(
	bodyLarge = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.5.sp
	),
	displayLarge = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Normal,
		fontSize = 30.sp
	),
	displayMedium = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Bold,
		fontSize = 20.sp
	),
	displaySmall = TextStyle(
		fontFamily = Vazir,
		fontWeight = FontWeight.Bold,
		fontSize = 20.sp
	)
)
