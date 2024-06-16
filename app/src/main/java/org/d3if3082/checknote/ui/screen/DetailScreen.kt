package org.d3if3082.checknote.ui.screen

//import android.content.res.Configuration
//import android.widget.Toast
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.selection.selectable
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material.icons.outlined.Check
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.RadioButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.semantics.Role
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.KeyboardCapitalization
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import org.d3if3082.checknote.R
//import org.d3if3082.checknote.ui.theme.CheckNoteTheme
//import org.d3if3082.checknote.util.ViewModelFactory
//
//const val KEY_ID_NOTES = "idNotes"
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DetailScreen(navController: NavController, id: Long? = null) {
//    val context = LocalContext.current
//    val viewModel: MainViewModel = viewModel()
//    val pilihanKategori = listOf(
//        "Catatan",
//        "Tugas",
//        "Belanja",
//        "Quotes",
//    )
//    var judul by remember { mutableStateOf("") }
//    var desc by remember { mutableStateOf("") }
//    var kategori by remember { mutableStateOf("") }
//    var showDialog by remember { mutableStateOf(false) }
//
//    LaunchedEffect(true) {
//        if (id == null) return@LaunchedEffect
//        val data = viewModel.getNotes(id) ?: return@LaunchedEffect
//        judul = data.judul
//        desc = data.desc
//        kategori = data.kategori
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                       Icon(
//                           imageVector = Icons.Filled.ArrowBack,
//                           contentDescription = stringResource(id = R.string.kembali),
//                           tint = MaterialTheme.colorScheme.primary
//                       )
//                    }
//                },
//                title = {
//                    if (id == null)
//                        Text(text = stringResource(id = R.string.tambah_note))
//                    else
//                        Text(text = stringResource(id = R.string.edit_catatan))
//                },
//                colors = TopAppBarDefaults.mediumTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
//                ),
//                actions = {
//                    IconButton(onClick = {
//                        if (judul == "" || desc == "" || kategori == "") {
//                            Toast.makeText(context, R.string.invalid, Toast.LENGTH_SHORT).show()
//                            return@IconButton
//                        }
//                        if (id == null) {
//                            viewModel.insert(judul, desc, kategori)
//                        } else {
//                            viewModel.update(id, judul, desc, kategori)
//                        }
//                        navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Outlined.Check,
//                            contentDescription = stringResource(id = R.string.simpan),
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                    if (id != null) {
//                        DeleteAction { showDialog = true }
//                        DisplayAlertDialog(
//                            openDialog = showDialog,
//                            onDismissRequest = { showDialog = false }) {
//                            showDialog = false
//                            viewModel.delete(id)
//                            navController.popBackStack()
//                        }
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        FormDaftar(
//            judul = judul,
//            onJudulChange = {judul = it},
//            desc = desc,
//            onDescChange = {desc = it},
//            pilihanKategori = pilihanKategori,
//            kategori = kategori,
//            onKategoriSelected = {kategori = it},
//            modifier = Modifier.padding(padding)
//        )
//    }
//}
//
//@Composable
//fun FormDaftar(
//    judul: String, onJudulChange: (String) -> Unit,
//    desc: String, onDescChange: (String) -> Unit,
//    pilihanKategori: List<String>,
//    kategori: String,
//    onKategoriSelected: (String) -> Unit,
//    modifier: Modifier
//) {
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        OutlinedTextField(
//            value = judul,
//            onValueChange ={ onJudulChange(it) },
//            label = {Text (text = stringResource(R.string.judul)) },
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(
//                capitalization = KeyboardCapitalization.Words,
//                imeAction = ImeAction.Next
//        ),
//            modifier = Modifier.fillMaxWidth()
//        )
//        OutlinedTextField(
//            value = desc,
//            onValueChange ={ onDescChange(it) },
//            label = { Text(text = stringResource(R.string.deskripsi)) },
//            keyboardOptions = KeyboardOptions(
//                capitalization = KeyboardCapitalization.Words,
//                imeAction = ImeAction.Next
//            ),
//            modifier = Modifier.fillMaxWidth()
//        )
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
//                .padding(16.dp, 0.dp),
//        ) {
//            pilihanKategori.forEachIndexed { index, opsi ->
//                ChoiceButton(
//                    label = opsi,
//                    isSelected = kategori == opsi,
//                    modifier = Modifier
//                        .selectable(
//                            selected = kategori == opsi,
//                            onClick = { onKategoriSelected(pilihanKategori[index]) },
//                            role = Role.RadioButton
//                        )
//                        .padding(0.dp, 16.dp)
//                )
//            }
//        }
//
//    }
//}
//
//@Composable
//fun ChoiceButton(label: String, isSelected: Boolean, modifier: Modifier) {
//    Row(
//        modifier = modifier,
//    ) {
//        RadioButton(selected = isSelected, onClick = null)
//        Text(
//            text = label,
//            style = MaterialTheme.typography.bodyLarge,
//            modifier = Modifier.padding(start = 8.dp)
//        )
//    }
//}
//
//@Composable
//fun DeleteAction(delete: () -> Unit) {
//    var expanded by remember { mutableStateOf(false) }
//    IconButton(onClick = { expanded = true}) {
//        Icon(
//            imageVector = Icons.Filled.MoreVert,
//            contentDescription = stringResource(id = R.string.lainnya),
//            tint = MaterialTheme.colorScheme.primary
//        )
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            DropdownMenuItem(
//                text = { Text(text = stringResource(id = R.string.hapus)) },
//                onClick = {
//                    expanded = false
//                    delete()
//                }
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
//@Composable
//fun DetailscreenPreview() {
//    CheckNoteTheme {
//        DetailScreen(rememberNavController())
//    }
//}