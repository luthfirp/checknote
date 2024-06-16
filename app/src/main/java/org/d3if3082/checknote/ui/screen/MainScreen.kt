package org.d3if3082.checknote.ui.screen

import android.content.Context
import android.content.res.Configuration
import android.credentials.GetCredentialException
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if3082.checknote.BuildConfig
import org.d3if3082.checknote.R
import org.d3if3082.checknote.model.Notes
import org.d3if3082.checknote.model.User
import org.d3if3082.checknote.network.ApiStatus
import org.d3if3082.checknote.ui.theme.CheckNoteTheme
//import org.d3if3082.checknote.ui.components.AddNotesDialog
//import org.d3if3082.checknote.ui.components.HapusNotesDialog
//import org.d3if3082.checknote.ui.components.ProfileDialog
//import org.d3if3082.checknote.ui.screen.signin.PlusJakartaSans
import org.d3if3082.checknote.util.SettingsDataStore
import org.d3if3082.checknote.util.UserDataStore

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userDataStore = UserDataStore(context)
    val user by userDataStore.userFlow.collectAsState(User())

    val settingsDataStore = SettingsDataStore(LocalContext.current)
    val showList by settingsDataStore.layoutFlow.collectAsState(true)


    val viewModel: MainViewModel = viewModel()
    val errorMessage by remember { viewModel.errorMessage }

    var showProfileDialog by remember { mutableStateOf(false) }
    var showAddNotesDialog by remember { mutableStateOf(false) }

    var showHapusNotesDialog by remember { mutableStateOf(false) }
    var hapusIdTemp by remember { mutableStateOf("") }

    var croppedImageUri: Uri? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        result.uriContent?.let { uri ->
            croppedImageUri = uri
            showAddNotesDialog = true
//            Log.d("Crop Image", "Cropped image URI: $croppedImageUri")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, userDataStore) }
                        } else {
//                            Log.d("SIGN-IN", "User: $user")
                            showProfileDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(id = R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                },
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            settingsDataStore.saveLayout(!showList)
                        }
                    }) {
                        Icon(
                            painter = painterResource(
                                id =
                                if (showList) R.drawable.baseline_grid_view_24
                                else R.drawable.baseline_view_list_24
                            ), contentDescription = stringResource(
                                id =
                                if (showList) R.string.grid
                                else R.string.list
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

//                    IconButton(
//                        onClick = {
//                            navController.navigate(Screen.About.route)
//                        }) {
//                        Icon(
//                            imageVector = Icons.Outlined.Info,
//                            contentDescription = stringResource(id = R.string.about_app),
//                            tint = MaterialTheme.colorScheme.primary,
//                        )
//                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val options = CropImageContractOptions(
                    null, CropImageOptions(
                        imageSourceIncludeGallery = true,
                        imageSourceIncludeCamera = true,
                        fixAspectRatio = true
                    )
                )
                launcher.launch(options)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.tambah_note)
                )
            }
        }
    ) { padding ->
        ScreenContent(
            viewModel = viewModel,
            errorMessage,
            user.email,
            Modifier.padding(padding),
            showList
        ) { id ->
            hapusIdTemp = id
            showHapusNotesDialog = true
        }

        if (showProfileDialog) {
            ProfileDialog(
                user = user,
                onDismissRequest = { showProfileDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, userDataStore) }
                showProfileDialog = false
            }
        }

        if (showAddNotesDialog) {
            TambahNotesDialog(
                uri = croppedImageUri,
                onDismissRequest = { showAddNotesDialog = false }) { judul, desc, kategori ->
                viewModel.saveData(user.email, judul, desc, kategori, croppedImageUri!!, context)
                showAddNotesDialog = false
            }
        }

        if (showHapusNotesDialog) {
            HapusDialog(
                onDismissRequest = { showHapusNotesDialog = false }) {
                viewModel.deleteNotesById(hapusIdTemp, context)
                showHapusNotesDialog = false
            }
        }

        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

}

@Composable
fun ScreenContent(
    viewModel: MainViewModel,
    errorMessage: String?,
    userId: String,
    modifier: Modifier,
    showList: Boolean,
    onHapusClick: (String) -> Unit
) {
//    val categories by viewModel.data.collectAsState()
    val notes by viewModel.data.collectAsState(emptyMap())
    val status by viewModel.status.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.retrieveData()
    }

    when (status) {
        ApiStatus.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.memuat))
//                CircularProgressIndicator()
            }
        }

        ApiStatus.SUCCESS -> {
            if (showList) {
                Column(
                    modifier = Modifier.padding(1.dp)
                ) {
                    if (notes.keys.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.list_kosong),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            modifier = modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 84.dp)
                        ) {
                            items(notes.entries.toList()) { (key, notes) ->
                                if (notes.userId == userId && userId != "") {
                                    ListItem(
                                        viewModel = viewModel,
                                        notes = notes,
                                        mine = true
                                    ) {
                                        onHapusClick(key)
                                    }
                                } else if (notes.userId.isEmpty() || notes.userId == "") {
                                    ListItem(notes = notes, viewModel = viewModel) {
                                    }
                                }
                            }
                        }
                    }

                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (notes.keys.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.list_kosong),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyVerticalGrid(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
//                        Log.d("NotesScreen", "Key: $categories")
                            items(notes.entries.toList()) { (key, notes) ->
                                if (notes.userId == userId) {
                                    GridItem(
                                        viewModel = viewModel,
                                        notes = notes,
                                        mine = true
                                    ) {
                                        onHapusClick(key)
                                    }
                                } else if (notes.userId.isEmpty() || notes.userId == "") {
                                    GridItem(notes = notes, viewModel = viewModel) {
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }

        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.error))
                Text(text = errorMessage ?: "Unknown Error")
                Button(
                    onClick = { viewModel.retrieveData() },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.coba_lagi))
                }
            }
        }
    }
}

@Composable
fun ListItem(
    notes: Notes,
    mine: Boolean = false,
    viewModel: MainViewModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = notes.judul,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = notes.kategori,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
//                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notes.desc,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.weight(.9f),
                )
                if (mine) {
                    IconButton(
                        modifier = Modifier.weight(.1f),
                        onClick = { onClick() })
                    {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.hapus)
                        )
                    }
                }

            }
        }
    }
    Divider()
}


@Composable
fun GridItem(
    notes: Notes,
    mine: Boolean = false,
    viewModel: MainViewModel,
    onClick: () -> Unit
) {
    val urlsGambar by viewModel.urlsGambar.observeAsState(initial = emptyMap())
    val urlGambar = urlsGambar[notes.image] ?: ""

    LaunchedEffect(notes.image) {
        Log.d("LaunchedEffect", "Loading image URL for: ${notes.image}")
        viewModel.loadImageUrl(notes.image)
    }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(
                width = 1.dp,
                color = if (mine) Color.LightGray else MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (urlGambar.isNotEmpty()) {
                Log.d("ImageLoading", "Image URL is not empty: $urlGambar")
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(urlGambar)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(id = R.string.gambar),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.broken_img),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                )
            } else {
                Log.d("ImageLoading", "Image URL is empty")
                Text(text = stringResource(id = R.string.memuat))
            }
            Text(
                text = notes.judul,
                fontWeight = FontWeight.Bold,
                color = if (mine) Color.LightGray else MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = notes.desc,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = notes.kategori,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.message}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenPreview() {
    CheckNoteTheme {
        MainScreen(rememberNavController())
    }
}