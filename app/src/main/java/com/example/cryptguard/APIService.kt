package com.example.cryptguard

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface APIService {
    @Multipart
    @POST("/api/login")
    suspend fun login(@PartMap requestBody: HashMap<String?, RequestBody?>): Response<ResponseBody>

    @Multipart
    @POST("/api/register")
    suspend fun register(@PartMap requestBody: HashMap<String?, RequestBody?>): Response<ResponseBody>

    @Multipart
    @POST("/api/request-login")
    suspend fun requestLogin(@PartMap requestBody: HashMap<String?, RequestBody?>): Response<ResponseBody>

    @Multipart
    @POST("/api/database")
    suspend fun uploadDB(@Header ("Authorization") token: String,  @PartMap requestBody: HashMap<String?, RequestBody?>): Response<ResponseBody>

    @GET("/api/database")
    suspend fun downloadDB(@Header ("Authorization") token: String): Response<ResponseBody>
}
