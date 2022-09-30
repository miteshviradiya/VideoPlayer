package com.silverorange.videoplayer.webservice

import com.silverorange.videoplayer.utils.Constant
import retrofit2.Call
import retrofit2.http.*


interface GeneralService {
    //used to call videos list api
    @GET(Constant.videos)
    fun getVideos(): Call<String>
}