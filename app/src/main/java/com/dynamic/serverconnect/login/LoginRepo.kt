package com.dynamic.serverconnect.login

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.dynamic.serverconnect.SessionManager
import com.dynamic.serverconnect.StaticVariable
import com.dynamic.serverconnect.login.model.LoginResponse
import com.dynamic.serverconnect.scenarios.ScenariosActivity
import com.dynamic.serverconnect.service.RestServiceInterface
import javax.inject.Inject

/**
 * Created by Charles Raj I on 18/08/23
 * @project DynamicServerConnect
 * @author Charles Raj
 */
class LoginRepo @Inject constructor(
    private val application: Application,
    private val restServiceInterface: RestServiceInterface,
    private val sessionManager: SessionManager
) {

    suspend fun loginBtnClick(userName: String, password: String, errorML: MutableLiveData<String>){

        val baseUrl = sessionManager.getString(StaticVariable.BASE_URL)
        val database = sessionManager.getString(StaticVariable.DATABASE)
        val loginUrl = "$baseUrl/json/count?dbname=$database&sql=exec _YPR_AS_EXISTUSER @sparam='${userName.trim()}',@sparam2='${password.trim()}',@hostname='DEMO8',@app='iSAP Scanner',@version='2.0.4'"

        val response = restServiceInterface.loginWithUrlCall(loginUrl.trim())
        if (response.isSuccessful) {
            val valid = response.body().takeIf {
                val loginResponse: LoginResponse = it as LoginResponse

                return@takeIf loginResponse.table?.get(0)?.userCount.equals("1")
            }
            if (valid != null){
                val intent: Intent = Intent(application, ScenariosActivity::class.java)
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                application.startActivity(intent)
            }else{
                errorML.postValue("Invalid Credentials")
            }
        } else{
            errorML.postValue(response.message())
        }

    }

}