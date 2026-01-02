package com.hakankirca.myapplication.presentation.add_edit_contact

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
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
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hakankirca.myapplication.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContactScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditContactViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val operationState = viewModel.operationState.value

    var showSuccessAnim by remember { mutableStateOf(false) }

    LaunchedEffect(operationState) {
        when (operationState) {
            OperationResult.SAVED -> {
                showSuccessAnim = true
                delay(2000)
                showSuccessAnim = false
                onNavigateBack()
                viewModel.resetOperationState()
            }
            OperationResult.VALIDATION_ERROR -> {
                Toast.makeText(context, "Lütfen tüm alanları doldurunuz.", Toast.LENGTH_LONG).show()
                viewModel.resetOperationState()
            }
            OperationResult.ERROR -> {
                Toast.makeText(context, "Bir hata oluştu!", Toast.LENGTH_SHORT).show()
                viewModel.resetOperationState()
            }
            else -> Unit
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.done))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { if (!showSuccessAnim) onNavigateBack() },
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
                    CenterAlignedTopAppBar(
                        title = { Text("Yeni Kişi Ekle") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack, enabled = !showSuccessAnim) {
                                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Kapat")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                floatingActionButton = {
                    if (!showSuccessAnim) {
                        FloatingActionButton(
                            onClick = { viewModel.saveContact() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (viewModel.isLoading.value) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Kaydet")
                            }
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        AsyncImage(
                            model = viewModel.profileImageUrl.value,
                            contentDescription = "Profil Resmi",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
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

                    OutlinedTextField(
                        value = viewModel.profileImageUrl.value,
                        onValueChange = { viewModel.profileImageUrl.value = it },
                        label = { Text("Fotoğraf Linki") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("https://...") },
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.firstName.value,
                        onValueChange = { viewModel.firstName.value = it },
                        label = { Text("Ad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.lastName.value,
                        onValueChange = { viewModel.lastName.value = it },
                        label = { Text("Soyad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.phoneNumber.value,
                        onValueChange = { viewModel.phoneNumber.value = it },
                        label = { Text("Telefon Numarası") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp)
                    )
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