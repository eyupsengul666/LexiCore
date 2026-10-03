package com.dunyadanuzak.lexicore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunyadanuzak.lexicore.data.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val input: String = "",
    val results: Map<Int, List<String>> = emptyMap(),
    val hasError: Boolean = false
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
                .debounce(200)
                .flatMapLatest { repository.getWords(it) }
                .collect { result ->
                    _uiState.update { state ->
                        state.copy(results = result.getOrDefault(emptyMap()), hasError = result.isFailure)
                    }
                }
        }
    }

    fun onInputChange(newInput: String) {
        _uiState.update { it.copy(input = newInput) }
    }
}
