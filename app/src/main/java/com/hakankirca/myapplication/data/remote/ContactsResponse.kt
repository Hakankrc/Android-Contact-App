package com.hakankirca.myapplication.data.remote

import com.hakankirca.myapplication.domain.model.Contact

// En dıştaki ana kutu
data class ContactsResponse(
    val data: DataWrapper, // data artık bir liste değil, bir "Wrapper" (Paket)
    val success: Boolean? = null,
    val messages: List<String>? = null,
    val status: Int? = null
)

// data'nın içindeki ara kutu
data class DataWrapper(
    val users: List<Contact> // İşte aradığımız liste burada! Adı 'users'
)