package com.hakankirca.myapplication.domain.model

data class CreateContactRequest(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profileImageUrl: String
)