package ru.devivanov.widgetexample.remote

import retrofit2.http.GET
import ru.devivanov.widgetexample.Entity.DoggyResponse

interface DoggyApi {
    @GET("random")
    suspend fun getRandomDog(): DoggyResponse
}