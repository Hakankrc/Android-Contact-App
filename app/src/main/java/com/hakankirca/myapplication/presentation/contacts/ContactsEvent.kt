package com.hakankirca.myapplication.presentation.contacts

sealed interface ContactsEvent {
    object LoadContacts : ContactsEvent // Sayfa açılınca yükle
    data class DeleteContact(val id: String) : ContactsEvent // Sola kaydırıp silince
    data class OnSearchQueryChange(val query: String) : ContactsEvent // Arama yapınca
}