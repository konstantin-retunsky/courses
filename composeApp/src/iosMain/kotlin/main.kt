import androidx.compose.ui.window.ComposeUIViewController
import com.courses.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
