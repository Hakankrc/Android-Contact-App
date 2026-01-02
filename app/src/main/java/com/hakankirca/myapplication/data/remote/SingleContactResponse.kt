package com.hakankirca.myapplication.data.remote

import com.hakankirca.myapplication.domain.model.Contact

// Tekil kişi isteğinde gelen cevap kutusu
data class SingleContactResponse(
    val data: Contact, // Dikkat: Burada 'users' listesi yok, direkt 'Contact' nesnesi var.
    val success: Boolean? = null,
    val message: String? = null
)