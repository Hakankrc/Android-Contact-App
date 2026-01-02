package com.hakankirca.myapplication.presentation.add_edit_contact

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankirca.myapplication.domain.model.CreateContactRequest
import com.hakankirca.myapplication.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OperationResult {
    IDLE,
    SAVED,
    DELETED,
    ERROR,
    VALIDATION_ERROR
}

@HiltViewModel
class AddEditContactViewModel @Inject constructor(
    private val repository: ContactRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var phoneNumber = mutableStateOf("")

    var profileImageUrl = mutableStateOf("https://picsum.photos/200")

    private var currentContactId: String? = null

    private val _isEditMode = mutableStateOf(false)
    val isEditMode: State<Boolean> = _isEditMode

    private val _operationState = mutableStateOf(OperationResult.IDLE)
    val operationState: State<OperationResult> = _operationState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        savedStateHandle.get<String>("contactId")?.let { id ->
            if (id != "-1") {
                viewModelScope.launch {
                    repository.getContactById(id)?.let { contact ->
                        currentContactId = contact.id
                        firstName.value = contact.firstName
                        lastName.value = contact.lastName
                        phoneNumber.value = contact.phoneNumber
                        profileImageUrl.value = contact.profileImageUrl ?: "https://picsum.photos/200"
                    }
                }
            } else {
                _isEditMode.value = true
            }
        }
    }

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    fun generateRandomImage() {
        profileImageUrl.value = "https://picsum.photos/200?random=${System.currentTimeMillis()}"
    }

    fun saveContact() {
        val cleanName = firstName.value.trim()
        val cleanLast = lastName.value.trim()
        val cleanPhone = phoneNumber.value.trim()
        val cleanImage = profileImageUrl.value.trim()

        if (cleanName.isBlank() || cleanLast.isBlank() || cleanPhone.isBlank()) {
            _operationState.value = OperationResult.VALIDATION_ERROR
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = CreateContactRequest(
                    firstName = cleanName,
                    lastName = cleanLast,
                    phoneNumber = cleanPhone,
                    profileImageUrl = cleanImage
                )

                if (currentContactId == null) {
                    repository.createContact(request)
                } else {
                    repository.updateContact(currentContactId!!, request)
                }
                
                _operationState.value = OperationResult.SAVED
                _isEditMode.value = false 
            } catch (e: Exception) {
                e.printStackTrace()
                _operationState.value = OperationResult.ERROR
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteContact() {
        currentContactId?.let { id ->
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    repository.deleteContact(id)
                    _operationState.value = OperationResult.DELETED
                } catch (e: Exception) {
                    e.printStackTrace()
                    _operationState.value = OperationResult.ERROR
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun resetOperationState() {
        _operationState.value = OperationResult.IDLE
    }
}