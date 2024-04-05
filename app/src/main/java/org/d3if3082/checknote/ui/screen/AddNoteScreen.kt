package org.d3if3082.checknote.ui.screen

import MainViewModel
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.d3if3082.checknote.R
import org.d3if3082.checknote.model.Notes
import org.d3if3082.checknote.ui.theme.CheckNoteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    var judul by remember { mutableStateOf("") }
    var judulError by remember { mutableStateOf(false) }
    var deskripsi by remember { mutableStateOf("") }
    var deskripsiError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surfaceTint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = stringResource(id = R.string.tambah_note))
                }
            )
        }
    ) {
            padding ->
        Text(
            text = stringResource(id = R.string.info_tambah),
            modifier = Modifier
                .padding(padding)
                .padding(20.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 120.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        OutlinedTextField(
            value = judul,
            onValueChange = {judul = it},
            label = { Text(text = stringResource(id = R.string.judul))},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        OutlinedTextField(
            value = deskripsi,
            onValueChange = {deskripsi = it},
            label = { Text(text = stringResource(id = R.string.deskripsi))},
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
        Button(
            onClick = {
                judulError = (judul == "")
                deskripsiError = (deskripsi == "")
                if (judulError || deskripsiError) {
                    val error = context.getString(R.string.tambah_error)
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.addNote(Notes(judul, deskripsi))
                navController.popBackStack()
                judul = ""
                deskripsi = ""
                val pesan = context.getString(R.string.tambah_click)
                Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(top = 6.dp, start = 20.dp),
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.tambah))
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddNoteScreenPreview() {
    CheckNoteTheme {
        AddNoteScreen(rememberNavController())
    }
}