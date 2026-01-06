package com.dunyadanuzak.lexicore.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunyadanuzak.lexicore.data.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val input: String = "",
    val results: Map<Int, List<String>> = emptyMap(),
    val isInitializing: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState
                .map { it.input }
                .distinctUntilChanged()
                .debounce(300)
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(Result.success(emptyMap<Int, List<String>>()))
                    } else {
                        repository.getWords(query)
                    }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { 
                            _uiState.update { state -> state.copy(results = it, errorMessage = null) }
                        },
                        onFailure = { 
                            _uiState.update { state -> state.copy(results = emptyMap(), errorMessage = "Hata: ${it.localizedMessage}") }
                        }
                    )
                }
        }
    }

    fun initializeDatabase(context: Context) {
        viewModelScope.launch {
            repository.initializeDatabase(context)
            _uiState.update { it.copy(isInitializing = false) }
        }
    }

    fun onInputChange(newInput: String) {
        _uiState.update { it.copy(input = newInput) }
    }
}
