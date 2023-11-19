package com.mousescrewstudio.trailsofwonder

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.mousescrewstudio.trailsofwonder.Destinations.FORGOT_PASSWORD_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.HUNT_CREATION_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.HUNT_JOIN_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.INDICES_RECAP_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.HUNT_SUMMARY
import com.mousescrewstudio.trailsofwonder.Destinations.LOGIN_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.NEW_INDICE_CONFIG_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.NEW_INDICE_POSITION_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.NEW_PASSWORD_PAGE
import com.mousescrewstudio.trailsofwonder.Destinations.PROFILE_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.SETTINGS_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.SIGNUP_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.VERIFY_EMAIL_CODE_PAGE
import com.mousescrewstudio.trailsofwonder.Destinations.WELCOME_ROUTE
import com.mousescrewstudio.trailsofwonder.ui.ForgotPasswordPage
import com.mousescrewstudio.trailsofwonder.ui.WelcomePage
import com.mousescrewstudio.trailsofwonder.ui.HuntCreationPage
import com.mousescrewstudio.trailsofwonder.ui.HuntJoinPage
import com.mousescrewstudio.trailsofwonder.ui.HuntSummary
import com.mousescrewstudio.trailsofwonder.ui.IndicesRecapPage
import com.mousescrewstudio.trailsofwonder.ui.LoginPage
import com.mousescrewstudio.trailsofwonder.ui.NewIndiceConfigPage
import com.mousescrewstudio.trailsofwonder.ui.NewIndicePositionPage
import com.mousescrewstudio.trailsofwonder.ui.NewPasswordPage
import com.mousescrewstudio.trailsofwonder.ui.ProfilePage
import com.mousescrewstudio.trailsofwonder.ui.SettingsPage
import com.mousescrewstudio.trailsofwonder.ui.SignupPage
import com.mousescrewstudio.trailsofwonder.ui.VerifyEmailCodePage

object Destinations {
    const val WELCOME_ROUTE = "welcome"
    const val LOGIN_ROUTE = "login"
    const val SIGNUP_ROUTE = "signup"
    const val FORGOT_PASSWORD_ROUTE = "forgotpassword"
    const val VERIFY_EMAIL_CODE_PAGE = "verifyemailcode"
    const val NEW_PASSWORD_PAGE = "newpassword"
    const val HUNT_CREATION_ROUTE = "hunt-creation"
    const val HUNT_JOIN_ROUTE = "hunt-join"
    const val HUNT_SUMMARY = "hunt-summary"
    const val PROFILE_ROUTE = "profile"
    const val SETTINGS_ROUTE = "settings"
    const val INDICES_RECAP_ROUTE = "indices-recap"
    const val NEW_INDICE_POSITION_ROUTE = "new-indice-position"
    const val NEW_INDICE_CONFIG_ROUTE = "new-indice-config"
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, val imageVector: ImageVector) {
    object Welcome: Screen(WELCOME_ROUTE, R.string.welcome, Icons.Filled.Home)
    object HuntCreation: Screen(HUNT_CREATION_ROUTE, R.string.create, Icons.Filled.AddLocationAlt)
    object Profile: Screen(PROFILE_ROUTE, R.string.profile, Icons.Filled.AccountCircle)
    object Login: Screen(LOGIN_ROUTE, R.string.login, Icons.Filled.Login)
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
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    when (navBackStackEntry?.destination?.route) {
        WELCOME_ROUTE -> { bottomBarState.value = true }
        LOGIN_ROUTE -> { bottomBarState.value = false }
        SIGNUP_ROUTE -> {bottomBarState.value = false}
        FORGOT_PASSWORD_ROUTE -> { bottomBarState.value = false}
        PROFILE_ROUTE -> { bottomBarState.value = true }
        HUNT_CREATION_ROUTE -> { bottomBarState.value = true }
        HUNT_JOIN_ROUTE -> { bottomBarState.value = true }
        HUNT_SUMMARY -> { bottomBarState.value = true }
        SETTINGS_ROUTE -> { bottomBarState.value = false }
        "$INDICES_RECAP_ROUTE/{huntId}" -> { bottomBarState.value = false }
        "$NEW_INDICE_POSITION_ROUTE/{huntId}" -> { bottomBarState.value = false }
        "$NEW_INDICE_CONFIG_ROUTE/{huntId}/{latitude}/{longitude}" -> { bottomBarState.value = false }
    }

    // Barre de navigation du bas
    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarState.value,
                enter = slideInVertically(initialOffsetY = {it}),
                exit = slideOutVertically(targetOffsetY = {it})
            ) {
                BottomNavigation() {
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
                onNavigateToSignup = { navController.navigate(SIGNUP_ROUTE) },
                onLoginSuccess = { navController.navigate(WELCOME_ROUTE) }
            ) }
            composable(FORGOT_PASSWORD_ROUTE) { ForgotPasswordPage(
                onNavigateToLogin = { navController.navigate(LOGIN_ROUTE) },
                onCodeSentSucess = { navController.navigate(LOGIN_ROUTE) }
            ) }
            composable(SIGNUP_ROUTE) { SignupPage(
                onNavigateToLogin = { navController.navigate(LOGIN_ROUTE) },
                onSignUpSuccess = { navController.navigate(WELCOME_ROUTE) }
            ) }
            // NE SERT A RIEN LA DEMARCHE SE FAIT PAR EMAIL
            composable(VERIFY_EMAIL_CODE_PAGE) { VerifyEmailCodePage(
                onVerifyCode = { navController.navigate(NEW_PASSWORD_PAGE) },
                onResendCode = { navController.navigate(FORGOT_PASSWORD_ROUTE) }
            ) }
            // IDEM
            composable(NEW_PASSWORD_PAGE) { NewPasswordPage(
                onResetPassword = {navController.navigate(LOGIN_ROUTE) }
            ) }
            composable(HUNT_CREATION_ROUTE) { HuntCreationPage(
                onDeleteClick = {},
                onPublishClick = {},
                onSaveClick = { navController.navigate(HUNT_CREATION_ROUTE) },
                onIndicesClick = { huntId ->
                    navController.navigate("$INDICES_RECAP_ROUTE/$huntId") }
            ) }
            composable(HUNT_JOIN_ROUTE) { HuntJoinPage (
                navController = navController
            ) }
            /*composable(HUNT_SUMMARY) { HuntSummary(

            ) }*/
            composable(
                "HuntSummary/{huntID}",
                arguments = listOf(
                    navArgument("huntID") {type = NavType.StringType })
            ) { backStackEntry ->
                val huntID = backStackEntry.arguments?.getString("huntID")
                if(huntID != null) HuntSummary(huntID)
            }
            composable(PROFILE_ROUTE) {
                ProfilePage(
                    username = FirebaseAuth.getInstance().currentUser?.displayName.toString(),
                    onSettingsClick = { navController.navigate(SETTINGS_ROUTE) },
                    onEditTreasureHuntClick = {}
                )
            }
            composable(SETTINGS_ROUTE) {
                SettingsPage(
                    onLogoutClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    onBackClick = { navController.navigate(PROFILE_ROUTE) }
                )
            }
            composable(
                route = "$INDICES_RECAP_ROUTE/{huntId}",
                arguments = listOf(
                    navArgument("huntId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val arguments = backStackEntry?.arguments
                val huntId = arguments?.getString("huntId")
                if (huntId != null) {
                    IndicesRecapPage(
                        huntId = huntId,
                        onBackClick = { navController.navigate(HUNT_CREATION_ROUTE) },
                        onAddIndexClick = { navController.navigate("$NEW_INDICE_POSITION_ROUTE/$huntId") }
                    )
                }
            }

            composable(
                route = "$NEW_INDICE_POSITION_ROUTE/{huntId}",
                arguments = listOf(
                    navArgument("huntId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val arguments = backStackEntry?.arguments
                val huntId = arguments?.getString("huntId")
                if (huntId != null) {
                    NewIndicePositionPage(
                        huntId = huntId,
                        onPositionValidated = { latitude, longitude ->
                            navController.navigate("$NEW_INDICE_CONFIG_ROUTE/$huntId/$latitude/$longitude")
                        },
                        onBackClick = {navController.navigate("$INDICES_RECAP_ROUTE/$huntId")}
                    )
                }
            }
            composable(
                route = "$NEW_INDICE_CONFIG_ROUTE/{huntId}/{latitude}/{longitude}",
                arguments = listOf(
                    navArgument("huntId") { type = NavType.StringType },
                    navArgument("latitude") { type = NavType.FloatType },
                    navArgument("longitude") { type = NavType.FloatType}
                )
            ) { backStackEntry ->
                val arguments = backStackEntry?.arguments
                val huntId = arguments?.getString("huntId")
                val latitude = arguments?.getFloat("latitude")
                val longitude = arguments?.getFloat("longitude")

                if (huntId != null && latitude != null && longitude != null) {
                    NewIndiceConfigPage(
                        huntId = huntId,
                        latitude = latitude,
                        longitude = longitude,
                        onIndiceConfigured = { indice ->
                            navController.navigate("$INDICES_RECAP_ROUTE/$huntId")
                        },
                        onBackClick = { navController.navigate("$NEW_INDICE_POSITION_ROUTE/$huntId")}
                    )
                }
            }
        }
    }
}