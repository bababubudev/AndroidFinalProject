package com.example.mobilefinalproject.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.Dialog
import com.example.mobilefinalproject.R

@Composable
fun PopupSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  onExecuteSearch: () -> Unit,
  onDismiss: () -> Unit
) {
  var openDialog by remember { mutableStateOf(true) }
  val focusRequester by remember { mutableStateOf(FocusRequester()) }

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  if (openDialog) {
    Dialog(
      onDismissRequest = {
        openDialog = false
        onDismiss()
      }
    ) {
      Surface(
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
      ) {
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text(stringResource(R.string.search)) },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { onExecuteSearch() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier
              .weight(4f)
              .background(MaterialTheme.colorScheme.background)
              .focusRequester(focusRequester)
          )

          IconButton(
            onClick = {
              onExecuteSearch()
              openDialog = false
            },
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
            )
          }
        }
      }
    }
  }

}