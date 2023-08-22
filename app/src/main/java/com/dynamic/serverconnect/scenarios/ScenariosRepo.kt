package com.dynamic.serverconnect.scenarios

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dynamic.serverconnect.SessionManager
import com.dynamic.serverconnect.StaticVariable
import com.dynamic.serverconnect.scenarios.model.ScenarioResponse
import com.dynamic.serverconnect.service.RestServiceInterface
import java.lang.Exception
import javax.inject.Inject

/**
 * Created by Charles Raj I on 18/08/23
 * @project DynamicServerConnect
 * @author Charles Raj
 */
class ScenariosRepo @Inject constructor(
    private val restServiceInterface: RestServiceInterface,
    private val sessionManager: SessionManager
) {

    suspend fun getScenarios(responseUpdate: MutableLiveData<ScenarioResponse>) {
        try {
            val db = sessionManager.getString(StaticVariable.DATABASE)
            val item = restServiceInterface.getScenariosCall(db!!.trim())
            if (item.isSuccessful) {
                val data = item.body() as ScenarioResponse
                responseUpdate.postValue(data)
            }
        } catch (e: Exception){
            Log.d("TAG", "getBtn: " + e.message)
        }
    }

}