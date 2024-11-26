package com.courses

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.courses.client.HttpClient
import com.courses.client.result.NetworkResult
import com.courses.client.extension.request
import com.courses.client.request.HttpMethod
import com.courses.di.NetworkModule
import courses.composeapp.generated.resources.*
import com.courses.theme.AppTheme
import com.courses.theme.LocalThemeIsDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

import kotlinx.serialization.Serializable

@Serializable
data class ExampleResponse(
	val userId: Int,
	val id: Int,
	val title: String,
	val body: String,
)

suspend fun test() {
	val networkModule = NetworkModule(baseUrl = "jsonplaceholder.typicode.com")
	
	val httpClient: HttpClient = networkModule.networkClient
	
	val result: NetworkResult<ExampleResponse> = httpClient.request {
		url = "/posts/1"
		method = HttpMethod.GET
	}
	
	// Обработка результата
	when (result) {
		is NetworkResult.Success -> {
			val users = result.data
			println("Список пользователей: $users")
		}
		
		is NetworkResult.Error -> {
			println("Ошибка: ${result.error}")
			println("Сообщение: ${result.message}")
		}
	}
}


@Composable
internal fun App() = AppTheme {
	val scope = rememberCoroutineScope()
	Column(
		modifier = Modifier
			.fillMaxSize()
			.windowInsetsPadding(WindowInsets.safeDrawing)
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = stringResource(Res.string.cyclone),
			fontFamily = FontFamily(Font(Res.font.IndieFlower_Regular)),
			style = MaterialTheme.typography.displayLarge
		)
		
		var isRotating by remember { mutableStateOf(false) }
		
		val rotate = remember { Animatable(0f) }
		val target = 360f
		if (isRotating) {
			LaunchedEffect(Unit) {
				while (isActive) {
					val remaining = (target - rotate.value) / target
					rotate.animateTo(
						target,
						animationSpec = tween((1_000 * remaining).toInt(), easing = LinearEasing)
					)
					rotate.snapTo(0f)
				}
			}
		}
		
		Image(
			modifier = Modifier
				.size(250.dp)
				.padding(16.dp)
				.run { rotate(rotate.value) },
			imageVector = vectorResource(Res.drawable.ic_cyclone),
			colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
			contentDescription = null
		)
		
		ElevatedButton(
			modifier = Modifier
				.padding(horizontal = 8.dp, vertical = 4.dp)
				.widthIn(min = 200.dp),
			onClick = { isRotating = !isRotating },
			content = {
				Icon(vectorResource(Res.drawable.ic_rotate_right), contentDescription = null)
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text(
					stringResource(if (isRotating) Res.string.stop else Res.string.run)
				)
			}
		)
		
		var isDark by LocalThemeIsDark.current
		val icon = remember(isDark) {
			if (isDark) Res.drawable.ic_light_mode
			else Res.drawable.ic_dark_mode
		}
		
		ElevatedButton(
			modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
			onClick = { isDark = !isDark },
			content = {
				Icon(vectorResource(icon), contentDescription = null)
				Spacer(Modifier.size(ButtonDefaults.IconSpacing))
				Text(stringResource(Res.string.theme))
			}
		)
		
		TextButton(
			modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
			onClick = {
				scope.launch(Dispatchers.Default) {
					test()
				}
			},
		) {
			Text("test request")
		}
	}
}


