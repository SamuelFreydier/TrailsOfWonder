package com.mousescrewstudio.trailsofwonder

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.mousescrewstudio.trailsofwonder.Destinations.HUNT_SUMMARY_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.LOGIN_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.NEW_INDICE_CONFIG_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.NEW_INDICE_POSITION_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.NEW_PASSWORD_PAGE
import com.mousescrewstudio.trailsofwonder.Destinations.PROFILE_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.SETTINGS_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.SIGNUP_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.VERIFY_EMAIL_CODE_PAGE
import com.mousescrewstudio.trailsofwonder.Destinations.WELCOME_ROUTE
import com.mousescrewstudio.trailsofwonder.Destinations.CHAT_PAGE
import com.mousescrewstudio.trailsofwonder.Destinations.TEAM_CREATION_PAGE
import com.mousescrewstudio.trailsofwonder.Destinations.HUNT_ONGOING
import com.mousescrewstudio.trailsofwonder.Destinations.VICTORY_PAGE
import com.mousescrewstudio.trailsofwonder.ui.ForgotPasswordPage
import com.mousescrewstudio.trailsofwonder.ui.WelcomePage
import com.mousescrewstudio.trailsofwonder.ui.HuntCreationPage
import com.mousescrewstudio.trailsofwonder.ui.HuntJoinPage
import com.mousescrewstudio.trailsofwonder.ui.HuntSummaryPage
import com.mousescrewstudio.trailsofwonder.ui.IndicesRecapPage
import com.mousescrewstudio.trailsofwonder.ui.LoginPage
import com.mousescrewstudio.trailsofwonder.ui.NewIndiceConfigPage
import com.mousescrewstudio.trailsofwonder.ui.NewIndicePositionPage
import com.mousescrewstudio.trailsofwonder.ui.NewPasswordPage
import com.mousescrewstudio.trailsofwonder.ui.ProfilePage
import com.mousescrewstudio.trailsofwonder.ui.SettingsPage
import com.mousescrewstudio.trailsofwonder.ui.SignupPage
import com.mousescrewstudio.trailsofwonder.ui.VerifyEmailCodePage
import com.mousescrewstudio.trailsofwonder.ui.ChatPage
import com.mousescrewstudio.trailsofwonder.ui.TeamCreationPage
import com.mousescrewstudio.trailsofwonder.ui.HuntOngoing
import com.mousescrewstudio.trailsofwonder.ui.VictoryPage

object Destinations {
    const val WELCOME_ROUTE = "welcome"
    const val LOGIN_ROUTE = "login"
    const val SIGNUP_ROUTE = "signup"
    const val FORGOT_PASSWORD_ROUTE = "forgotpassword"
    const val VERIFY_EMAIL_CODE_PAGE = "verifyemailcode"
    const val NEW_PASSWORD_PAGE = "newpassword"
    const val HUNT_CREATION_ROUTE = "hunt-creation"
    const val HUNT_JOIN_ROUTE = "hunt-join"
    const val HUNT_SUMMARY_ROUTE = "hunt-summary"
    const val PROFILE_ROUTE = "profile"
    const val SETTINGS_ROUTE = "settings"
    const val INDICES_RECAP_ROUTE = "indices-recap"
    const val NEW_INDICE_POSITION_ROUTE = "new-indice-position"
    const val NEW_INDICE_CONFIG_ROUTE = "new-indice-config"
    const val CHAT_PAGE = "chat-page"
    const val TEAM_CREATION_PAGE = "team-creation"
    const val HUNT_ONGOING = "hunt-ongoing"
    const val VICTORY_PAGE = "victory-page"
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, val imageVector: ImageVector) {
    object Welcome: Screen(WELCOME_ROUTE, R.string.welcome, Icons.Filled.Home)
    object HuntCreation: Screen(HUNT_CREATION_ROUTE, R.string.create, Icons.Filled.AddLocationAlt)
    object HuntJoin: Screen(HUNT_JOIN_ROUTE, R.string.welcome, Icons.Filled.Home)
    object Profile: Screen(PROFILE_ROUTE, R.string.profile, Icons.Filled.AccountCircle)
    object Login: Screen(LOGIN_ROUTE, R.string.login, Icons.Filled.Login)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailsOfWonderApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = WELCOME_ROUTE,
    screenItems: List<Screen> = listOf(
        Screen.HuntJoin,
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
        "$HUNT_CREATION_ROUTE/{huntId}" -> {bottomBarState.value = false}
        HUNT_JOIN_ROUTE -> { bottomBarState.value = true }
        "$HUNT_SUMMARY_ROUTE/{huntId}" -> { bottomBarState.value = false }
        SETTINGS_ROUTE -> { bottomBarState.value = false }
        "$INDICES_RECAP_ROUTE/{huntId}" -> { bottomBarState.value = false }
        "$NEW_INDICE_POSITION_ROUTE/{huntId}" -> { bottomBarState.value = false }
        "$NEW_INDICE_CONFIG_ROUTE/{huntId}/{latitude}/{longitude}" -> { bottomBarState.value = false }
        "$NEW_INDICE_CONFIG_ROUTE/{huntId}/{indiceId}" -> {bottomBarState.value = false}
        CHAT_PAGE -> { bottomBarState.value = true }
        TEAM_CREATION_PAGE -> { bottomBarState.value = false }
        HUNT_ONGOING -> { bottomBarState.value = true }
        VICTORY_PAGE -> { bottomBarState.value = true }
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
                onLoginSuccess = { navController.navigate(HUNT_JOIN_ROUTE) }
            ) }
            composable(FORGOT_PASSWORD_ROUTE) { ForgotPasswordPage(
                onNavigateToLogin = { navController.navigate(LOGIN_ROUTE) },
                onCodeSentSucess = { navController.navigate(LOGIN_ROUTE) }
            ) }
            composable(SIGNUP_ROUTE) { SignupPage(
                onNavigateToLogin = { navController.navigate(LOGIN_ROUTE) },
                onSignUpSuccess = { navController.navigate(HUNT_JOIN_ROUTE) }
            ) }
            // NE SERT A RIEN, LA DEMARCHE SE FAIT PAR EMAIL
            composable(VERIFY_EMAIL_CODE_PAGE) { VerifyEmailCodePage(
                onVerifyCode = { navController.navigate(NEW_PASSWORD_PAGE) },
                onResendCode = { navController.navigate(FORGOT_PASSWORD_ROUTE) }
            ) }
            // IDEM
            composable(NEW_PASSWORD_PAGE) { NewPasswordPage(
                onResetPassword = {navController.navigate(LOGIN_ROUTE) }
            ) }
            composable(HUNT_CREATION_ROUTE) { HuntCreationPage(
                editMode = false,
                onDeleteClick = { navController.navigate(PROFILE_ROUTE)},
                onPublishClick = { navController.navigate(PROFILE_ROUTE)},
                onSaveClick = { navController.navigate(PROFILE_ROUTE) },
                onIndicesClick = { huntId ->
                    navController.navigate("$INDICES_RECAP_ROUTE/$huntId") },
                onBackClick = { navController.navigate(PROFILE_ROUTE)}
            ) }
            composable(
                route = "$HUNT_CREATION_ROUTE/{huntId}",
                arguments = listOf(
                    navArgument("huntId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val arguments = backStackEntry?.arguments
                val huntId = arguments?.getString("huntId")

                if (huntId != null) {
                    HuntCreationPage(
                        huntId = huntId,
                        editMode = true,
                        onDeleteClick = { navController.navigate(PROFILE_ROUTE)},
                        onPublishClick = { navController.navigate(PROFILE_ROUTE)},
                        onSaveClick = { navController.navigate(PROFILE_ROUTE) },
                        onIndicesClick = { huntId ->
                            navController.navigate("$INDICES_RECAP_ROUTE/$huntId") },
                        onBackClick = {navController.navigate(PROFILE_ROUTE)}
                    )
                }

            }
            composable(HUNT_JOIN_ROUTE) { HuntJoinPage (
                onHuntClicked = {huntId ->
                    navController.navigate("$HUNT_SUMMARY_ROUTE/$huntId")
                },
                chatPage = {navController.navigate(CHAT_PAGE) }
            ) }
            composable(
                route = "$HUNT_SUMMARY_ROUTE/{huntId}",
                arguments = listOf(
                    navArgument("huntId") {type = NavType.StringType })
            ) { backStackEntry ->
                val huntId = backStackEntry.arguments?.getString("huntId")
                if(huntId != null) HuntSummaryPage(
                    huntId = huntId,
                    onHuntStart = {navController.navigate(TEAM_CREATION_PAGE) },
                    onBackClick = {navController.popBackStack()}

                /*val huntID = backStackEntry.arguments?.getString("huntID")
                if(huntID != null) HuntSummary(
                    huntID = huntID,
                    navController = navController*/
                )
            }
            composable(PROFILE_ROUTE) {
                ProfilePage(
                    username = FirebaseAuth.getInstance().currentUser?.displayName.toString(),
                    onSettingsClick = { navController.navigate(SETTINGS_ROUTE) },
                    onEditHuntClick = { huntId ->
                        navController.navigate("$HUNT_CREATION_ROUTE/$huntId")
                    }
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
                        onBackClick = { navController.navigate("$HUNT_CREATION_ROUTE/$huntId") },
                        onAddIndexClick = { navController.navigate("$NEW_INDICE_POSITION_ROUTE/$huntId") },
                        onEditIndiceClick = { indiceId ->
                            navController.navigate("$NEW_INDICE_CONFIG_ROUTE/$huntId/$indiceId")
                        }
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
                        editMode = false,
                        latitude = latitude,
                        longitude = longitude,
                        onIndiceConfigured = { navController.navigate("$INDICES_RECAP_ROUTE/$huntId") },
                        onBackClick = { navController.navigate("$NEW_INDICE_POSITION_ROUTE/$huntId")}
                    )
                }
            }
            composable(
                route = "$NEW_INDICE_CONFIG_ROUTE/{huntId}/{indiceId}",
                arguments = listOf(
                    navArgument("huntId") { type = NavType.StringType },
                    navArgument("indiceId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val arguments = backStackEntry?.arguments
                val huntId = arguments?.getString("huntId")
                val indiceId = arguments?.getString("indiceId")

                if (huntId != null && indiceId != null) {
                    NewIndiceConfigPage(
                        huntId = huntId,
                        indiceId = indiceId,
                        editMode = true,
                        onIndiceConfigured = { navController.navigate("$INDICES_RECAP_ROUTE/$huntId") },
                        onBackClick = { navController.navigate("$INDICES_RECAP_ROUTE/$huntId")}
                    )
                }
            }

            composable(
                "ChatPage/{receiverId}",
                arguments = listOf(
                    navArgument("receiverId") {type = NavType.StringType })
            ) { backStackEntry ->
                val receiverId = backStackEntry.arguments?.getString("receiverId")
                if(receiverId != null) ChatPage(receiverId)
            }

            composable(TEAM_CREATION_PAGE) { TeamCreationPage (
                onStartClick = {},
                onBackClick = {navController.popBackStack()}
            ) }
            /*composable(
                "TeamCreation/{huntID}",
                arguments = listOf(
                    navArgument("huntID") {type = NavType.StringType })
            ) { backStackEntry ->
                val huntID = backStackEntry.arguments?.getString("huntID")
                if(huntID != null) TeamCreation(navController, huntID)
            }*/
            composable(
                "HuntOngoing/{ID}",
                arguments = listOf(
                    navArgument("ID") {type = NavType.StringType })
            ) { backStackEntry ->
                val ID = backStackEntry.arguments?.getString("ID")
                if(ID != null) HuntOngoing(ID,
                    /*onClickVictory = { huntId ->
                        navController.navigate("$VICTORY_PAGE/$huntId")})*/
                    navController = navController)
            }

            composable(
                "VictoryPage/{huntId}",
                arguments = listOf(
                    navArgument("huntId") {type = NavType.StringType })
            ) { backStackEntry ->
                val huntId = backStackEntry.arguments?.getString("huntId")
                if(huntId != null) VictoryPage(
                    retourMenu = { navController.navigate(WELCOME_ROUTE)},
                    huntId)
            }

        }
    }
}