package com.hakankirca.myapplication.domain.model

data class Contact(
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profileImageUrl: String? = null
)