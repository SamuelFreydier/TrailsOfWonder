package com.mousescrewstudio.trailsofwonder.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mousescrewstudio.trailsofwonder.R
import com.mousescrewstudio.trailsofwonder.ui.components.showErrorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailCodePage(
    onVerifyCode: () -> Unit,
    onResendCode: () -> Unit
) {
    var code by remember { mutableStateOf(TextFieldValue()) }
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
            text = "Validez le code envoyé par email",
            style = typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Code") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    verifyCode(code.text, onVerifyCode, context)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onResendCode) {
                Text("Renvoyer le code")
            }
        }

        Button(
            onClick = {
                verifyCode(code.text, onVerifyCode, context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Vérifier")
        }
    }
}

fun verifyCode(code: String, onSuccess: () -> Unit, context: Context) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    if (user != null) {
        val credential = auth.currentUser?.email?.let { EmailAuthProvider.getCredential(it, code) }
        user.reauthenticate(credential!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke()
                } else {
                    val exception = task.exception
                    if (exception != null) {
                        handleVerifyEmailCodeError(exception, context)
                    }
                }
            }
    }
}

// Méthode pour gérer les erreurs lors de la connexion
private fun handleVerifyEmailCodeError(exception: Exception, context: Context) {
    when (exception) {
        else -> {
            // Gérer d'autres erreurs
            showErrorDialog("Code invalide.", context)
        }
    }
}