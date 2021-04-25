package ru.devivanov.widgetexample.Entity


import com.google.gson.annotations.SerializedName

data class DoggyResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)