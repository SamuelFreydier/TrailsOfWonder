package com.mousescrewstudio.trailsofwonder.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mousescrewstudio.trailsofwonder.utils.showErrorDialog

// Page d'oubli de mot de passe
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordPage(
    onNavigateToLogin: () -> Unit,
    onCodeSentSucess: () -> Unit
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
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
            )*/

            //Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Mot de passe oublié",
                style = typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            var email by remember { mutableStateOf(TextFieldValue()) }
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

            Button(
                onClick = {
                    sendPasswordResetEmail(email.text, context, onCodeSentSucess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Réinitialiser le mot de passe")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { onNavigateToLogin() }) {
                Text("Annuler")
            }
        }
    }
}

// Fonction permettant d'envoyer l'email de réinitialisation de mdp
fun sendPasswordResetEmail(
    email: String,
    context: Context,
    onCodeSentSucess: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onCodeSentSucess()
            } else {
                // La réinitialisation du mot de passe a échoué
                val exception = task.exception
                if (exception != null) {
                    handleResetPasswordError(exception, context)
                }
            }
        }
}

// Méthode pour gérer les erreurs lors de l'envoi d'un e-mail de réinitialisation de mot de passe
fun handleResetPasswordError(exception: Exception, context: Context) {
    when (exception) {
        is FirebaseAuthInvalidUserException -> {
            // L'utilisateur avec cette adresse e-mail n'existe pas
            showErrorDialog("Aucun utilisateur avec cette adresse e-mail n'a été trouvé.", context)
        }
        else -> {
            // Gérer d'autres erreurs
            showErrorDialog("Une erreur s'est produite lors de l'envoi de l'e-mail de réinitialisation.", context)
        }
    }
}