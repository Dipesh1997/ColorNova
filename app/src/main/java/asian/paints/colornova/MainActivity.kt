package asian.paints.colornova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import asian.paints.colornova.data.SampleColorRepository
import asian.paints.colornova.ui.ColorNovaApp
import asian.paints.colornova.ui.theme.ColorNovaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SampleColorRepository.loadFromAssets(applicationContext)
        enableEdgeToEdge()
        setContent {
            ColorNovaApp()
        }
    }
}