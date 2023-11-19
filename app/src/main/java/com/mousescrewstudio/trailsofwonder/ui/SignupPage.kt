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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType.Companion.Password
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.mousescrewstudio.trailsofwonder.ui.components.showErrorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupPage(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
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
                text = "Inscription",
                style = typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            var username by remember { mutableStateOf(TextFieldValue()) }
            var email by remember { mutableStateOf(TextFieldValue()) }
            var password by remember { mutableStateOf(TextFieldValue()) }
            var isPasswordVisible by remember { mutableStateOf(false) }
            val context = LocalContext.current

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nom d'utilisateur") },
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
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
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
                        // Handle sign up action
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

            Button(
                onClick = {
                    createAccount(username.text, email.text, password.text, onSignUpSuccess, context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("S'inscrire")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("Connexion")
            }
        }
    }
}

private fun createAccount(
    username: String,
    email: String,
    password: String,
    onSignUpSuccess: () -> Unit,
    context: Context
) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Utilisateur créé avec succès
                // MaJ du profil avec le username
                val user = auth.currentUser

                //println("UID = ${user?.uid}")
                if (user != null) {
                    addUsername(user.uid)
                }
                else println("Problème lors de l'ajout de l'UID dans la collection d'username")

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { updateTask ->
                        if(updateTask.isSuccessful) {
                            onSignUpSuccess.invoke()
                        } else {
                            val exception = updateTask.exception
                            if (exception != null) {
                                handleSignUpError(exception, context)
                            }
                        }
                    }
            } else {
                val exception = task.exception
                if (exception != null) {
                    handleSignUpError(exception, context)
                }
            }
        }
}

// Fonction pour ajouter l'username à la liste dans Firebase
fun addUsername(uid: String) {
    val acces = FirebaseFirestore.getInstance().collection("username").document("UsernameList")
    val newData = hashMapOf( uid to uid )

    acces
        .update(newData as Map<String, Any>)
        .addOnSuccessListener {
            println("Username ajouté avec succès.")
        }
        .addOnFailureListener {
            println("Erreur lors de l'ajout de l'username")
        }
}

// Méthode pour gérer les erreurs lors de la création du compte
private fun handleSignUpError(exception: Exception, context: Context) {
    when (exception) {
        is FirebaseAuthWeakPasswordException -> {
            // Mot de passe faible
            showErrorDialog("Le mot de passe est trop faible.", context)
        }
        is FirebaseAuthInvalidCredentialsException -> {
            // E-mail invalide
            showErrorDialog("L'adresse e-mail est invalide.", context)
        }
        is FirebaseAuthUserCollisionException -> {
            // L'utilisateur avec cette adresse e-mail existe déjà
            showErrorDialog("Un compte avec cette adresse e-mail existe déjà.", context)
        }
        else -> {
            // Gérer d'autres erreurs
            showErrorDialog("Une erreur s'est produite lors de la création du compte.", context)
        }
    }
}