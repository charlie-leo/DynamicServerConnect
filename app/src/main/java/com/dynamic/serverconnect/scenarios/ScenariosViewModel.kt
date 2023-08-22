package com.dynamic.serverconnect.scenarios

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynamic.serverconnect.scenarios.model.ScenarioResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScenariosViewModel @Inject constructor(
    private val application: Application,
    private val scenariosRepo: ScenariosRepo,

) : ViewModel() {
    val responseUpdate: MutableLiveData<ScenarioResponse> = MutableLiveData<ScenarioResponse>()

    init {
        fetchScenarios()
    }

    private fun fetchScenarios() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                scenariosRepo.getScenarios(responseUpdate)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}