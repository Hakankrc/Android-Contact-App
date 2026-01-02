package com.hakankirca.myapplication.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hakankirca.myapplication.domain.model.Contact
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DraggableContactItem(
    contact: Contact,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // Butonların toplam genişliği
    val actionWidth = 160.dp // Biraz daha geniş tuttum, rahat basılsın
    val actionWidthPx = with(density) { actionWidth.toPx() }

    val offsetX = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp) // Listede elemanlar arası boşluk
    ) {
        // --- ARKA PLAN (BUTONLAR) ---
        // Butonlar yuvarlatılmış köşeli kartın arkasında duracağı için
        // görsel olarak hoş durması adına bir container içinde
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .width(actionWidth)
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp), // Ana kartın margin'i ile uyumlu olsun
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DÜZENLE
            FilledIconButton(
                onClick = {
                    scope.launch { offsetX.animateTo(0f) }
                    onEditClick(contact.id ?: "")
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.8f) // Kartın tamamını kaplamasın, ortalı dursun
                    .padding(end = 4.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = MaterialTheme.colorScheme.onSecondary)
            }

            // SİL
            FilledIconButton(
                onClick = {
                    scope.launch { offsetX.animateTo(0f) }
                    onDeleteClick(contact.id ?: "")
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.8f)
                    .padding(start = 4.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Sil", tint = MaterialTheme.colorScheme.onError)
            }
        }

        // --- ÖN PLAN (KİŞİ KARTI) ---
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .fillMaxWidth()
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            val target = (offsetX.value + delta).coerceIn(-actionWidthPx, 0f)
                            offsetX.snapTo(target)
                        }
                    },
                    onDragStopped = {
                        scope.launch {
                            if (offsetX.value < -actionWidthPx / 2) {
                                offsetX.animateTo(-actionWidthPx)
                            } else {
                                offsetX.animateTo(0f)
                            }
                        }
                    }
                )
        ) {
            content()
        }
    }
}