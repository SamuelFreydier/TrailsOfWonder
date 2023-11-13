package com.example.trailsofwonder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trailsofwonder.Destinations.HUNT_CREATION_ROUTE
import com.example.trailsofwonder.Destinations.HUNT_JOIN_ROUTE
import com.example.trailsofwonder.Destinations.PROFILE_ROUTE
import com.example.trailsofwonder.Destinations.WELCOME_ROUTE
import com.example.trailsofwonder.ui.WelcomePage
import com.example.trailsofwonder.ui.HuntCreationPage
import com.example.trailsofwonder.ui.HuntJoinPage
import com.example.trailsofwonder.ui.ProfilePage

object Destinations {
    const val WELCOME_ROUTE = "welcome"
    const val HUNT_CREATION_ROUTE = "hunt-creation"
    const val HUNT_JOIN_ROUTE = "hunt-join"
    const val PROFILE_ROUTE = "profile"
}
@Composable
fun TrailsOfWonderNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = WELCOME_ROUTE
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(WELCOME_ROUTE) { WelcomePage(
            onNavigateToHuntCreation = { navController.navigate(HUNT_CREATION_ROUTE) },
            onNavigateToHuntJoin = { navController.navigate(HUNT_JOIN_ROUTE) },
            onNavigateToProfile = { navController.navigate(PROFILE_ROUTE) }
        ) }
        composable(HUNT_CREATION_ROUTE) { HuntCreationPage() }
        composable(HUNT_JOIN_ROUTE) { HuntJoinPage() }
        composable(PROFILE_ROUTE) { ProfilePage() }
    }
}
