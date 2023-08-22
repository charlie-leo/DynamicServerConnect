package com.dynamic.serverconnect.login

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynamic.serverconnect.scenarios.ScenariosActivity
import com.dynamic.serverconnect.setting.SettingsActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Charles Raj I on 18/08/23
 * @project DynamicServerConnect
 * @author Charles Raj
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {

    var userName: String = ""
    var password: String = ""
    val errorML: MutableLiveData<String> = MutableLiveData<String>()

    fun loginClick(view: View) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loginRepo.loginBtnClick(userName,password,errorML)
            }
        }
    }

    fun settingsClick(view: View) {
        val intent: Intent = Intent(view.context, SettingsActivity::class.java)
        view.context.startActivity(intent)
    }

    fun qrScannerClick(view: View){

    }

}