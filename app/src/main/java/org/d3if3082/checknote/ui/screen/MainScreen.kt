package org.d3if3082.checknote.ui.screen

import MainViewModel
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.d3if3082.checknote.R
import org.d3if3082.checknote.model.Notes
import org.d3if3082.checknote.navigation.Screen
import org.d3if3082.checknote.ui.theme.CheckNoteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val notes by viewModel.data.observeAsState(emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(
                        onClick = {
                            shareList(context, notes)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Info.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Add.route) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
    ) { padding ->
        ScreenContent(Modifier.padding(padding))
    }
}


@Composable
fun ScreenContent(modifier: Modifier, viewModel: MainViewModel = viewModel()) {
    val data by viewModel.data.observeAsState(emptyList())
    if (viewModel.isDataEmpty()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.note),
                contentDescription = null,
                modifier = modifier.size(250.dp)
            )
            Text(
                text = stringResource(id = R.string.list_kosong),
            )
        }
    }
    else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 78.dp)
        ) {
            items(data) { note ->
                ListItem(notes = note)
            }
        }
    }
}

@Composable
fun ListItem(notes: Notes) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = notes.judul,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = notes.desc,
            overflow = TextOverflow.Ellipsis
        )
    }
    Divider()
}


fun shareList(context: Context, list: List<Notes>) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"

    val data = StringBuilder()
    data.append(R.string.share).append("\n\n")
    for (note in list) {
        data.append(note.judul).append("\n")
        data.append(note.desc).append("\n")
    }

    intent.putExtra(Intent.EXTRA_TEXT, data.toString())
    context.startActivity(Intent.createChooser(intent, R.string.share_with.toString()))
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ScreenPreview() {
    CheckNoteTheme {
        MainScreen(rememberNavController())
    }
}