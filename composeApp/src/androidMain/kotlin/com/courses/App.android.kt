package com.courses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.koin.android.ext.koin.androidLogger
import org.koin.android.ext.koin.androidContext

class AppActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		enableEdgeToEdge()
		
		val appContext = applicationContext
		
		setContent {
			App(
				koinDeclaration = {
					androidLogger()
					androidContext(appContext)
				}
			)
		}
	}
}

@Preview
@Composable
fun AppPreview() {
	val context = LocalContext.current
	
	App(
		koinDeclaration = {
			androidLogger()
			androidContext(context)
		}
	)
}
