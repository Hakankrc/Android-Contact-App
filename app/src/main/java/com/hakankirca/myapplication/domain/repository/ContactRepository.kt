package com.hakankirca.myapplication.domain.repository

import com.hakankirca.myapplication.domain.model.Contact
import com.hakankirca.myapplication.domain.model.CreateContactRequest

interface ContactRepository {
    suspend fun getContacts(): List<Contact>
    suspend fun createContact(contact: com.hakankirca.myapplication.domain.model.CreateContactRequest)
    suspend fun deleteContact(id: String)

    suspend fun getContactById(id: String): Contact?
    suspend fun updateContact(id: String, request: CreateContactRequest)
    suspend fun getDevicePhoneNumbers(context: android.content.Context): List<String>
}