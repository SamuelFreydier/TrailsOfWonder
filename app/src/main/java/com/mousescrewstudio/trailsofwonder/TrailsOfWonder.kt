package com.mousescrewstudio.trailsofwonder

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mousescrewstudio.trailsofwonder.Destinations.FORGOT_PASSWORD_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.HUNT_CREATION_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.HUNT_JOIN_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.LOGIN_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.PROFILE_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.SIGNUP_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.WELCOME_ROUTE
import com.mousescrewstudio.trailsofwonder.ui.ForgotPasswordPage
import com.mousescrewstudio.trailsofwonder.ui.WelcomePage
import com.mousescrewstudio.trailsofwonder.ui.HuntCreationPage
import com.mousescrewstudio.trailsofwonder.ui.HuntJoinPage
import com.mousescrewstudio.trailsofwonder.ui.LoginPage
import com.mousescrewstudio.trailsofwonder.ui.ProfilePage
import com.mousescrewstudio.trailsofwonder.ui.SignupPage

object Destinations {
    const val WELCOME_ROUTE = "welcome"
    const val LOGIN_ROUTE = "login"
    const val SIGNUP_ROUTE = "signup"
    const val FORGOT_PASSWORD_ROUTE = "forgotpassword"
    const val HUNT_CREATION_ROUTE = "hunt-creation"
    const val HUNT_JOIN_ROUTE = "hunt-join"
    const val PROFILE_ROUTE = "profile"
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, val imageVector: ImageVector) {
    object Welcome: Screen(WELCOME_ROUTE, R.string.welcome, Icons.Filled.Home)
    object HuntCreation: Screen(HUNT_CREATION_ROUTE, R.string.create, Icons.Filled.AddLocationAlt)
    object Profile: Screen(PROFILE_ROUTE, R.string.profile, Icons.Filled.AccountCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailsOfWonderApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = WELCOME_ROUTE,
    screenItems: List<Screen> = listOf(
        Screen.Welcome,
        Screen.HuntCreation,
        Screen.Profile
    )
) {
    // Barre de navigation du bas
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screenItems.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.imageVector, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId))},
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Navigation
        NavHost(
            modifier = modifier.padding(innerPadding),
            navController = navController,
            startDestination = startDestination
        ) {
            composable(WELCOME_ROUTE) { WelcomePage(
                onNavigateToHuntCreation = { navController.navigate(HUNT_CREATION_ROUTE) },
                onNavigateToHuntJoin = { navController.navigate(HUNT_JOIN_ROUTE) },
                onNavigateToProfile = { navController.navigate(PROFILE_ROUTE) },
                onNavigateToLogin = { navController.navigate(LOGIN_ROUTE) }
            ) }
            composable(LOGIN_ROUTE) { LoginPage(
                onNavigateToForgotPassword = { navController.navigate(FORGOT_PASSWORD_ROUTE) },
                onNavigateToSignup = { navController.navigate(SIGNUP_ROUTE) }
            ) }
            composable(FORGOT_PASSWORD_ROUTE) { ForgotPasswordPage(
                onNavigateToLogin = { navController.navigate(LOGIN_ROUTE) }
            ) }
            composable(SIGNUP_ROUTE) { SignupPage(
                onNavigateToLogin = { navController.navigate(LOGIN_ROUTE) }
            ) }
            composable(HUNT_CREATION_ROUTE) { HuntCreationPage() }
            composable(HUNT_JOIN_ROUTE) { HuntJoinPage() }
            composable(PROFILE_ROUTE) { ProfilePage() }
        }
    }


}
