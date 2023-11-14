package com.mousescrewstudio.trailsofwonder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mousescrewstudio.trailsofwonder.ui.theme.TrailsOfWonderTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrailsOfWonderTheme {
                TrailsOfWonderApp()
            }
        }
    }
}

