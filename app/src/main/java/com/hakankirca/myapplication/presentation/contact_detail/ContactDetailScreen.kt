package com.hakankirca.myapplication.presentation.contact_detail

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.provider.ContactsContract
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hakankirca.myapplication.R
import com.hakankirca.myapplication.presentation.add_edit_contact.AddEditContactViewModel
import com.hakankirca.myapplication.presentation.add_edit_contact.OperationResult
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contactId: String,
    onNavigateUp: () -> Unit,
    viewModel: AddEditContactViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val operationState = viewModel.operationState.value
    val isEditMode = viewModel.isEditMode.value

    var showMenu by remember { mutableStateOf(false) }
    var showSuccessAnim by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.done))

    LaunchedEffect(operationState) {
        when (operationState) {
            OperationResult.SAVED -> {
                showSuccessAnim = true
                delay(2000) // Sabit 2 Saniye (Animasyonun tamamlanması için güvenli süre)
                showSuccessAnim = false
                viewModel.resetOperationState()
            }
            OperationResult.DELETED -> {
                Toast.makeText(context, "Kişi Silindi!", Toast.LENGTH_SHORT).show()
                onNavigateUp()
                viewModel.resetOperationState()
            }
            OperationResult.ERROR -> {
                Toast.makeText(context, "Bir hata oluştu!", Toast.LENGTH_SHORT).show()
                viewModel.resetOperationState()
            }
            OperationResult.VALIDATION_ERROR -> {
                Toast.makeText(context, "Lütfen ad ve soyad alanlarını doldurunuz.", Toast.LENGTH_LONG).show()
                viewModel.resetOperationState()
            }
            else -> Unit
        }
    }

    val imageUrl = viewModel.profileImageUrl.value
    
    val defaultColor = MaterialTheme.colorScheme.primary
    var dominantColor by remember { mutableStateOf(defaultColor) }
    
    var textColor by remember { mutableStateOf(Color.White) }

    LaunchedEffect(imageUrl) {
        if (imageUrl.isNotBlank()) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val bitmap = (result.drawable as BitmapDrawable).bitmap
                Palette.from(bitmap).generate { palette ->
                    val swatch = palette?.darkVibrantSwatch
                        ?: palette?.vibrantSwatch
                        ?: palette?.mutedSwatch
                        ?: palette?.dominantSwatch

                    if (swatch != null) {
                        dominantColor = Color(swatch.rgb)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { if (!showSuccessAnim) onNavigateUp() }, 
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp
        ) {
            Scaffold(
                containerColor = Color.Transparent, 
                topBar = {
                    TopAppBar(
                        title = { 
                            Text(
                                text = if (isEditMode) "Düzenleniyor..." else "Kişi Detayı", 
                                color = textColor
                            ) 
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateUp, enabled = !showSuccessAnim) {
                                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Kapat", tint = textColor)
                            }
                        },
                        actions = {
                            if (isEditMode) {
                                IconButton(onClick = { viewModel.saveContact() }, enabled = !showSuccessAnim) {
                                    Icon(Icons.Default.Check, contentDescription = "Kaydet", tint = textColor)
                                }
                            } else {
                                IconButton(onClick = { showMenu = !showMenu }, enabled = !showSuccessAnim) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Seçenekler", tint = textColor)
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Düzenle") },
                                        onClick = {
                                            showMenu = false
                                            viewModel.toggleEditMode()
                                        },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Kişiyi Sil") },
                                        onClick = {
                                            showMenu = false
                                            viewModel.deleteContact()
                                        },
                                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(dominantColor, MaterialTheme.colorScheme.surface)
                            )
                        )
                        .padding(padding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Profil Resmi",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f)),
                                contentScale = ContentScale.Crop
                            )
                            
                            if (isEditMode) {
                                IconButton(
                                    onClick = { viewModel.generateRandomImage() },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        .size(32.dp),
                                    enabled = !showSuccessAnim
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Rastgele", tint = Color.White, modifier = Modifier.padding(4.dp))
                                }
                            }
                        }

                        if (isEditMode) {
                            ContactInfoTextField(
                                value = viewModel.profileImageUrl.value,
                                onValueChange = { viewModel.profileImageUrl.value = it },
                                label = "Fotoğraf Linki",
                                isEditMode = true
                            )
                        }

                        ContactInfoTextField(
                            value = viewModel.firstName.value,
                            onValueChange = { viewModel.firstName.value = it },
                            label = "Ad",
                            isEditMode = isEditMode
                        )

                        ContactInfoTextField(
                            value = viewModel.lastName.value,
                            onValueChange = { viewModel.lastName.value = it },
                            label = "Soyad",
                            isEditMode = isEditMode
                        )

                        ContactInfoTextField(
                            value = viewModel.phoneNumber.value,
                            onValueChange = { viewModel.phoneNumber.value = it },
                            label = "Telefon",
                            isEditMode = isEditMode,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (!isEditMode) {
                            OutlinedButton(
                                onClick = {
                                    val name = "${viewModel.firstName.value} ${viewModel.lastName.value}"
                                    val phone = viewModel.phoneNumber.value
                                    val intent = Intent(Intent.ACTION_INSERT).apply {
                                        type = ContactsContract.Contacts.CONTENT_TYPE
                                        putExtra(ContactsContract.Intents.Insert.NAME, name)
                                        putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                                        putExtra(ContactsContract.Intents.Insert.NOTES, "Profil: ${viewModel.profileImageUrl.value}")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Rehber açılamadı", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                                border = androidx.compose.foundation.BorderStroke(1.dp, textColor),
                                enabled = !showSuccessAnim
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cihaz Rehberine Kaydet")
                            }
                        }
                    }
                    if (viewModel.isLoading.value) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }

        if (showSuccessAnim) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)) 
                    .clickable(enabled = false) {}, 
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    isPlaying = true,
                    restartOnPlay = false,
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}

@Composable
fun ContactInfoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isEditMode: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        enabled = isEditMode,
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            disabledContainerColor = Color.Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = Color.Transparent,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}