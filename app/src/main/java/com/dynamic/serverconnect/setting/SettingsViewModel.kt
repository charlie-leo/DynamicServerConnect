package com.dynamic.serverconnect.setting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepo: SettingsRepo): ViewModel() {



    override fun onCleared() {
        super.onCleared()
    }
}