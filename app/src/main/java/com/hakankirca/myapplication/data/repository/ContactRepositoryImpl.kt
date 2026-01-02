package com.hakankirca.myapplication.data.repository

import com.hakankirca.myapplication.data.remote.ApiService
import com.hakankirca.myapplication.domain.model.Contact
import com.hakankirca.myapplication.domain.model.CreateContactRequest
import com.hakankirca.myapplication.domain.repository.ContactRepository
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ContactRepository {

    override suspend fun getContacts(): List<Contact> {
        return api.getAllContacts().data.users
    }

    // Parametre tipi değişti: Contact -> CreateContactRequest
    override suspend fun createContact(request: CreateContactRequest) {
        api.createContact(request) // Burası böyle kalmalı
    }

    override suspend fun deleteContact(id: String) {
        api.deleteContact(id)
    }

    override suspend fun getContactById(id: String): Contact? {
        return try {
            // Artık .users.firstOrNull() yok! Direkt .data diyoruz.
            api.getContactById(id).data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateContact(id: String, request: CreateContactRequest) {
        // API'deki yeni metoda yönlendiriyoruz
        api.updateContact(id, request)
    }

    override suspend fun getDevicePhoneNumbers(context: android.content.Context): List<String> {
        val numbers = mutableListOf<String>()
        // Sadece numaraları çekiyoruz, isimler lazım değil (Performans için)
        val cursor = context.contentResolver.query(
            android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER), // Sadece numara sütunu
            null, null, null
        )

        cursor?.use {
            val index = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val rawNumber = it.getString(index) ?: ""
                // Numarayı temizle (Boşluk, parantez, tire hepsini sil) -> "0555 123-45" -> "055512345"
                val cleanNumber = rawNumber.replace(Regex("[^0-9]"), "")
                numbers.add(cleanNumber)
            }
        }
        return numbers
    }
}