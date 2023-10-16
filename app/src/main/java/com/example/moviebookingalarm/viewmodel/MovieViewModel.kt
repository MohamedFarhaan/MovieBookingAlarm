package com.example.moviebookingalarm.viewmodel

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviebookingalarm.db.DatabaseInit
import com.example.moviebookingalarm.db.entity.Movie
import com.example.moviebookingalarm.getDefaultRingtone
import com.example.moviebookingalarm.service.ApiService
import com.example.moviebookingalarm.service.InoxService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(val context: Context): ViewModel() {

    lateinit var db: DatabaseInit;
    lateinit var inoxService: InoxService;
    private var _movie = MutableStateFlow<Movie>(Movie(id = 0, movieName = null))
    var movie: StateFlow<Movie> = _movie
    private var _movieNameL = MutableLiveData<String>("")
    var movieNameL: LiveData<String> = _movieNameL
    val serviceIntent = Intent(context, ApiService::class.java)

    init {
        viewModelScope.launch {
            db = DatabaseInit.getDatabase(context = context);
            inoxService = InoxService.getInoxServiceInstance();
            populateMovie()
        }
    }



    fun populateMovie() {
        viewModelScope.launch {
            val movie = db.movieDao().getMovie();
            if (movie != null) {
                _movie.value = movie
                _movieNameL.value = movie.movieName ?: ""
            }
        }
    }

    fun updateMovie(movieName: String) {
        val movie = Movie(id = 0, movieName = movieName)
        viewModelScope.launch {
            db.movieDao().deleteAllMovies()
            db.movieDao().insertMovie(movie)
            populateMovie()
            context.startService(serviceIntent)
        }
    }

    fun resetMovie() {
        viewModelScope.launch {
            db.movieDao().deleteAllMovies()
            populateMovie()
            context.stopService(serviceIntent)
        }
    }

    fun updateMovieNameL(movieText: String) {
        _movieNameL.value = movieText
    }

}