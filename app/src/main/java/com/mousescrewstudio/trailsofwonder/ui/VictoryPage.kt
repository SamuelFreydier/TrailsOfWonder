package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.mousescrewstudio.trailsofwonder.ui.database.OngoingHunt
import com.mousescrewstudio.trailsofwonder.ui.database.getOngoingHunt

// Page de victoire après complétion totale d'une chasse
@Composable
fun VictoryPage(
    retourMenu: () -> Unit,
    huntId: String
) {

    var ongoing : OngoingHunt


    var text by remember { mutableStateOf("En cours de calcul...") }

    LaunchedEffect(huntId) {
        getOngoingHunt(
            huntId = huntId,
            onSuccess = {
                ongoing = it
                text = "Vous avez mis"
                val comparaison = Timestamp.now().toDate().time - ongoing.startDate.toDate().time
                val hours = (comparaison / (60 * 60 * 1000))
                if(hours.toInt() != 0)
                    text += " ${hours}H"
                val minutes = (comparaison % (60 * 60 * 1000)) / (60 * 1000)
                if(minutes.toInt() != 0)
                    text += " ${minutes}m"
                val seconds = (comparaison % (60 * 1000)) / 1000
                if(seconds.toInt() != 0)
                    text += " ${seconds}s"
                text += " à la finir"
            },
            onFailure = { exception ->
                println("Erreur lors de la récupération de la chasse : $exception")
            }
        )
    }



  Column(
      modifier = Modifier
          .padding(13.dp)
  ) {
      Text(text = "Bravo, la chasse est fini !",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.primary)

      Spacer(modifier = Modifier.height(16.dp))

      Text(text = text)

      Spacer(modifier = Modifier.height(30.dp))

      Button(
          onClick = { retourMenu() },
          modifier = Modifier
              .fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(
              backgroundColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.primary)
      ) {
          Text(text = "Retour Menu")
      }
  }
}