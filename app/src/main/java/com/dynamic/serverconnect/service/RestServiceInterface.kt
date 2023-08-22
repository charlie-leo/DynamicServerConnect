package com.dynamic.serverconnect.service

import com.dynamic.serverconnect.login.model.LoginResponse
import com.dynamic.serverconnect.scenarios.model.ScenarioResponse
import com.google.gson.JsonPrimitive
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.Objects

/**
 * Created by Charles Raj I on 17/08/23.
 * @author Charles Raj I
 */
interface RestServiceInterface {

    @GET("/json/exec?sql=exec%20_YPR_AS_GETLIST%20@sparam=%27sales%27,%20@sparam2=%27%27,%20@hostname=%27DEMO8%27,%20@device=%27iPhone%27,%20@app=%27iSAP%20Scanner%27,%20@whsCode=%27%27")
    suspend fun getScenariosCall(@Query("dbname") dbname: String): Response<ScenarioResponse>

    @GET("json/count")
    suspend fun loginCall(@Query("dbname") dbname: String, @Query("sql") sql: String): Response<LoginResponse>

    @GET
    suspend fun loginWithUrlCall(@Url loginUrl: String): Response<LoginResponse>

    @GET("{url}")
    suspend fun getApi(@Path("url") url: String): Response<ResponseBody>

    @POST("{url}")
    suspend fun postApi(@Path("url") url: String): Response<ResponseBody>
}