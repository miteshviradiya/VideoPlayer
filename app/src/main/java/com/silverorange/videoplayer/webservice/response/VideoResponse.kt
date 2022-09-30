package com.silverorange.videoplayer.webservice.response
import com.google.gson.annotations.SerializedName

class Video {
    // Video object use to retrieve data from string response
    @SerializedName("id")
    var id: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("hlsURL")
    var hlsURL: String? = null

    @SerializedName("fullURL")
    var fullURL: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("publishedAt")
    var publishedAt: String? = null

    @SerializedName("author")
    var author: Author? = null
}

class Author {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("name")
    var name: String? = null
}