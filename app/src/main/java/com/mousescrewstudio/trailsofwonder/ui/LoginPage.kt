package com.mousescrewstudio.trailsofwonder.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType.Companion.Password
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mousescrewstudio.trailsofwonder.utils.showErrorDialog

// Page de connexion au compte
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            /*Image(
                painter = painterResource(id = R.drawable.),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
            )*/

            //Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Connexion",
                style = typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            var email by remember { mutableStateOf(TextFieldValue()) }
            var password by remember { mutableStateOf(TextFieldValue()) }
            var isPasswordVisible by remember { mutableStateOf(false) }
            val context = LocalContext.current

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            isPasswordVisible = !isPasswordVisible
                        }
                    ) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = Password),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Handle login action
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onNavigateToForgotPassword) {
                    Text("Mot de passe oublié ?")
                }
                TextButton(onClick = onNavigateToSignup) {
                    Text("Inscription")
                }
            }

            Button(
                onClick = {
                    signInWithEmailAndPassword(email.text, password.text, onLoginSuccess, context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Se connecter")
            }
        }
    }
}

private fun signInWithEmailAndPassword(
    email: String,
    password: String,
    onSignInSuccess: () -> Unit,
    context: Context
) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignInSuccess.invoke()
            } else {
                val exception = task.exception
                if (exception != null) {
                    handleLoginError(exception, context)
                }
            }
        }
}

// Méthode pour gérer les erreurs lors de la connexion
private fun handleLoginError(exception: Exception, context: Context) {
    when (exception) {
        is FirebaseAuthInvalidUserException -> {
            // L'utilisateur n'existe pas
            showErrorDialog("L'utilisateur n'existe pas.", context)
        }
        is FirebaseAuthInvalidCredentialsException -> {
            // Les informations d'identification sont incorrectes
            showErrorDialog("Les informations d'identification sont incorrectes.", context)
        }
        else -> {
            // Gérer d'autres erreurs
            showErrorDialog("Une erreur s'est produite lors de la connexion.", context)
        }
    }
}



