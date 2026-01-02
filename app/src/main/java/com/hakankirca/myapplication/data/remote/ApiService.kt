package com.hakankirca.myapplication.data.remote

import com.hakankirca.myapplication.domain.model.Contact
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.hakankirca.myapplication.domain.model.CreateContactRequest
interface ApiService {
    // Adresi bulduk: api/User/GetAll
    @GET("api/User/GetAll")
    suspend fun getAllContacts(): ContactsResponse  // Eskiden List<Contact> idi

    @POST("api/User")
    suspend fun createContact(@Body request: CreateContactRequest)

    @DELETE("api/User/{id}")
    suspend fun deleteContact(@Path("id") id: String): com.hakankirca.myapplication.data.remote.SingleContactResponse

    @PUT("api/User/{id}")
    suspend fun updateContact(
        @Path("id") id: String,
        @Body request: com.hakankirca.myapplication.domain.model.CreateContactRequest
    ): com.hakankirca.myapplication.data.remote.SingleContactResponse // Cevabı da karşılıyoruz

    @GET("api/User/{id}")
    suspend fun getContactById(@Path("id") id: String): SingleContactResponse
}