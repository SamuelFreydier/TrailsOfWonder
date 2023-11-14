package com.mousescrewstudio.trailsofwonder

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mousescrewstudio.trailsofwonder.Destinations.HUNT_CREATION_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.HUNT_JOIN_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.LOGIN_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.PROFILE_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.WELCOME_ROUTE
import com.mousescrewstudio.trailsofwonder.ui.WelcomePage
import com.mousescrewstudio.trailsofwonder.ui.HuntCreationPage
import com.mousescrewstudio.trailsofwonder.ui.HuntJoinPage
import com.mousescrewstudio.trailsofwonder.ui.LoginPage
import com.mousescrewstudio.trailsofwonder.ui.ProfilePage

object Destinations {
    const val WELCOME_ROUTE = "welcome"
    const val LOGIN_ROUTE = "login"
    const val HUNT_CREATION_ROUTE = "hunt-creation"
    const val HUNT_JOIN_ROUTE = "hunt-join"
    const val PROFILE_ROUTE = "profile"
}

sealed class Screen(val route: String) {
    object Welcome: Screen(WELCOME_ROUTE)
    object Login: Screen(LOGIN_ROUTE)
    object HuntCreation: Screen(HUNT_CREATION_ROUTE)
    object HuntJoin: Screen(HUNT_JOIN_ROUTE)
    object Profile: Screen(PROFILE_ROUTE)
}

@Composable
fun TrailsOfWonderApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = WELCOME_ROUTE,
    screenItems: List<Screen> = listOf(
        Screen.Welcome,
        Screen.Login,
        Screen.HuntCreation,
        Screen.HuntJoin,
        Screen.Profile
    )
) {
    // Barre de navigation du bas
    /*Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screenItems.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text()},
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { /*TODO*/ }

                    )
                }
            }
        }
    ) {

    }*/

    // Navigation
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(WELCOME_ROUTE) { WelcomePage(
            onNavigateToHuntCreation = { navController.navigate(HUNT_CREATION_ROUTE) },
            onNavigateToHuntJoin = { navController.navigate(HUNT_JOIN_ROUTE) },
            onNavigateToProfile = { navController.navigate(PROFILE_ROUTE) },
            onNavigateToLogin = { navController.navigate(LOGIN_ROUTE) }
        ) }
        composable(LOGIN_ROUTE) { LoginPage() }
        composable(HUNT_CREATION_ROUTE) { HuntCreationPage() }
        composable(HUNT_JOIN_ROUTE) { HuntJoinPage() }
        composable(PROFILE_ROUTE) { ProfilePage() }
    }
}
