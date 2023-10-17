package com.example.moviebookingalarm.service.network.repository

import com.example.moviebookingalarm.service.network.InoxService
import retrofit2.Response
import javax.inject.Inject

class InoxApiRepository @Inject constructor(private val inoxService: InoxService) {
    suspend fun getMoviesList(): Response<String> {
        return inoxService.getMoviesList();
    }
    suspend fun getScheduledMoviesList(showDate: String, cityId: Int): Response<String> {
        return inoxService.getScheduledMoviesList(showDate, cityId);
    }
}