package com.hakankirca.myapplication.presentation.contacts

import com.hakankirca.myapplication.domain.model.Contact

data class ContactsState(
    val isLoading: Boolean = false,       // Yükleniyor dönüyor mu?
    val contacts: List<Contact> = emptyList(), // Kişi listesi
    val error: String = "",               // Hata mesajı var mı?
    val searchQuery: String = ""          // Arama çubuğunda ne yazıyor?
)