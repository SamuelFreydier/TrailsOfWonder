package com.mousescrewstudio.trailsofwonder

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mousescrewstudio.trailsofwonder.ui.theme.TrailsOfWonderTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            TrailsOfWonderTheme {
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val startRoute = if(currentUser == null) Screen.Login else Screen.Welcome
                TrailsOfWonderApp(startDestination = startRoute.route)
            }
        }
    }


}

