package com.mousescrewstudio.trailsofwonder.ui.components

import android.content.Context
import android.widget.Toast
import com.mousescrewstudio.trailsofwonder.MainActivity

// Fonction générique pour afficher un message d'erreur
fun showErrorDialog(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
