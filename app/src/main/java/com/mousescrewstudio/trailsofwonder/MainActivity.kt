package com.mousescrewstudio.trailsofwonder

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import com.mousescrewstudio.trailsofwonder.ui.theme.TrailsOfWonderTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            TrailsOfWonderTheme {
                TrailsOfWonderApp()
            }
        }
    }


}

