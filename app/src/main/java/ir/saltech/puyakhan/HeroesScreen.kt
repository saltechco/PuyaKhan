package ir.saltech.puyakhan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.saltech.puyakhan.model.Hero
import ir.saltech.puyakhan.model.HeroesRepository
import ir.saltech.puyakhan.ui.theme.PuyaKhanTheme

@Composable
fun HeroCard(hero: Hero, modifier: Modifier = Modifier) {
	Card(
		shape = RoundedCornerShape(16.dp),
		elevation = CardDefaults.cardElevation(2.dp),
		modifier = modifier,
	) {
		Row(
			horizontalArrangement = Arrangement.Center, modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth()
				.sizeIn(minHeight = 72.dp)
		) {
			Column(
				modifier = Modifier
					.weight(1f, true)
			) {
				Text(
					text = stringResource(id = hero.nameRes),
					style = MaterialTheme.typography.displaySmall
				)
				Text(
					text = stringResource(id = hero.descriptionRes),
					style = MaterialTheme.typography.bodyLarge
				)
			}
			Spacer(modifier = Modifier.width(16.dp))
			Box(
				modifier = Modifier
					.size(72.dp)
					.clip(RoundedCornerShape(8.dp))
			) {
				Image(
					painter = painterResource(id = hero.imageRes),
					contentDescription = null,
					alignment = Alignment.TopCenter,
					contentScale = ContentScale.FillWidth
				)
			}
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroesView(
	hr: HeroesRepository = HeroesRepository,
	contentPadding: PaddingValues = PaddingValues(0.dp)
) {

	LazyColumn (contentPadding = contentPadding) {
		items(hr.heroes) { hero ->
			HeroCard(
				hero,
				modifier = Modifier
					.padding(horizontal = 16.dp, vertical = 8.dp)
					.animateItemPlacement()

			)
		}
	}
}


@Preview(showBackground = true)
@Composable
fun HeroCardPreview() {
	PuyaKhanTheme {
		HeroCard(
			hero = Hero(R.string.hero1, R.string.description1, R.drawable.android_superhero1)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun HeroesViewPreview() {
	PuyaKhanTheme {
		HeroesView()
	}
}
