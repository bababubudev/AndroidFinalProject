package com.example.mobilefinalproject.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
              .background(
                MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(
                  topStart = 10.dp,
                  topEnd = 10.dp,
                  bottomStart = 5.dp,
                  bottomEnd = 5.dp
                ),
              )
              .focusRequester(focusRequester),
          )

          Spacer(modifier = Modifier.width(10.dp))

          IconButton(
            enabled = query.isNotEmpty(),
            onClick = {
              onExecuteSearch()
              openDialog = false
            },
            modifier = Modifier.weight(1f).height(100.dp).padding(top = 5.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.Search,
              contentDescription = "Search",
              modifier = Modifier.clip(CircleShape)
              .background(
                MaterialTheme.colorScheme.background,
                shape = CircleShape,
              )
              .border(2.dp, MaterialTheme.colorScheme.onSecondary, CircleShape)
              .padding(15.dp)
            )
          }
        }
      }
    }
  }

}