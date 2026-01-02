package com.hakankirca.myapplication.presentation.contacts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf 
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankirca.myapplication.data.local.SearchHistoryManager
import com.hakankirca.myapplication.domain.model.Contact
import com.hakankirca.myapplication.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val searchHistoryManager: SearchHistoryManager
) : ViewModel() {

    private val _state = mutableStateOf(ContactsState())
    val state: State<ContactsState> = _state

    private var allContacts = listOf<Contact>()

    private val _deviceNumbers = mutableStateListOf<String>()

    private val _searchHistory = mutableStateOf<List<String>>(emptyList())
    val searchHistory: State<List<String>> = _searchHistory

    init {
        getContacts()
        getSearchHistory()
    }

    fun onEvent(event: ContactsEvent) {
        when (event) {
            is ContactsEvent.LoadContacts -> getContacts()
            is ContactsEvent.DeleteContact -> deleteContact(event.id)
            is ContactsEvent.OnSearchQueryChange -> {
                _state.value = _state.value.copy(searchQuery = event.query)
                filterContacts(event.query)
            }
        }
    }

    fun loadDeviceContacts(context: android.content.Context) {
        viewModelScope.launch {
            try {
                val numbers = repository.getDevicePhoneNumbers(context)
                _deviceNumbers.clear()
                val cleanedNumbers = numbers.map { it.replace(Regex("[^0-9]"), "") }
                _deviceNumbers.addAll(cleanedNumbers)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isContactInDevice(contactPhone: String): Boolean {
        val cleanApiPhone = contactPhone.replace(Regex("[^0-9]"), "")
        if (cleanApiPhone.isBlank()) return false
        if (cleanApiPhone.length < 7) return _deviceNumbers.contains(cleanApiPhone)
        val lastSevenDigits = cleanApiPhone.takeLast(7)
        return _deviceNumbers.any { it.endsWith(lastSevenDigits) }
    }

    private fun getSearchHistory() {
        searchHistoryManager.searchHistory.onEach { historySet ->
            _searchHistory.value = historySet.toList().reversed()
        }.launchIn(viewModelScope)
    }

    fun addToSearchHistory(query: String) {
        viewModelScope.launch {
            searchHistoryManager.saveSearchQuery(query)
        }
    }

    fun removeFromSearchHistory(query: String) {
        viewModelScope.launch {
            searchHistoryManager.removeSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryManager.clearSearchHistory()
        }
    }

    private fun getContacts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = "")
            try {
                val result = repository.getContacts()
                allContacts = result.sortedBy { it.firstName }
                val query = _state.value.searchQuery
                val listToShow = if (query.isEmpty()) allContacts else filterList(query)
                _state.value = _state.value.copy(contacts = listToShow, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Hata", isLoading = false)
            }
        }
    }

    private fun filterContacts(query: String) {
        val filtered = filterList(query)
        _state.value = _state.value.copy(contacts = filtered)
    }

    private fun filterList(query: String): List<Contact> {
        val cleanQuery = query.trim().lowercase()
        return allContacts.filter { contact ->
            val fullName = "${contact.firstName} ${contact.lastName}".lowercase()
            val reverseName = "${contact.lastName} ${contact.firstName}".lowercase()
            val phone = contact.phoneNumber.replace(Regex("[^0-9]"), "")
            fullName.contains(cleanQuery) || reverseName.contains(cleanQuery) || phone.contains(cleanQuery)
        }
    }

    private fun deleteContact(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteContact(id)
                getContacts()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Silinemedi: ${e.message}")
            }
        }
    }
}