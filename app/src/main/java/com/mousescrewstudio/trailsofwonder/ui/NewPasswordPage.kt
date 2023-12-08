package com.mousescrewstudio.trailsofwonder.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType.Companion.Password
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.mousescrewstudio.trailsofwonder.utils.showErrorDialog

// PAGE OBSOLETE PERMETTANT DE CONFIGURER UN NOUVEAU MOT DE PASSE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordPage(
    onResetPassword: () -> Unit
) {
    var password by remember { mutableStateOf(TextFieldValue()) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val typography = MaterialTheme.typography
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        /*Image(
            painter = painterResource(id = R.drawable.ic_lock),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )*/

        //Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Nouveau mot de passe",
            style = typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("New Password") },
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
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    resetPassword(password.text, onResetPassword, context)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                resetPassword(password.text, onResetPassword, context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Valider")
        }
    }
}

fun resetPassword(newPassword: String, onSuccess: () -> Unit, context: Context) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke()
                } else {
                    val exception = task.exception
                    if (exception != null) {
                        handleErrorResetPassword(exception, context)
                    }
                }
            }
    }
}

// Méthode pour gérer les erreurs lors de la création du compte
private fun handleErrorResetPassword(exception: Exception, context: Context) {
    when (exception) {
        is FirebaseAuthWeakPasswordException -> {
            // Mot de passe faible
            showErrorDialog("Le mot de passe est trop faible.", context)
        }
        is FirebaseAuthInvalidCredentialsException -> {
            showErrorDialog("Le mot de passe est invalide.", context)
        }
        else -> {
            // Gérer d'autres erreurs
            showErrorDialog("Une erreur s'est produite lors de la réinitialisation du mot de passe.", context)
        }
    }
}