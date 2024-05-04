package com.dicoding.asclepius.data.response

import com.google.gson.annotations.SerializedName

data class NewsResponse(

    @field:SerializedName("totalResults")
    val totalResults: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("articles")
    val articles: List<ArticlesItem?>? = null,
)

data class ArticlesItem(

    @field:SerializedName("urlToImage")
    val urlToImage: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("title")
    val title: String? = null
)
