package com.example.moviebookingalarm.viewmodel

import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviebookingalarm.db.dao.MovieDao
import com.example.moviebookingalarm.db.entity.AlarmItem
import com.example.moviebookingalarm.db.entity.Movie
import com.example.moviebookingalarm.service.alarm.AlarmSchedulerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(val context: Context,
    val movieDao: MovieDao, val ringtone: Ringtone): ViewModel() {

    private var _movie = MutableStateFlow<Movie>(Movie(id = 0, movieName = null, city = "", showDate = ""))
    var movie: StateFlow<Movie> = _movie
    private var _movieNameL = MutableLiveData<String>("")
    var movieNameL: LiveData<String> = _movieNameL
    private var _cityL = MutableLiveData<String>("")
    var cityL: LiveData<String> = _cityL
    private var _showDateL = MutableLiveData<String>("")
    var showDateL: LiveData<String> = _showDateL
    private var _locationL = MutableLiveData<String>("")
    var locationL: LiveData<String> = _locationL
    var alarmItem: AlarmItem = AlarmItem(time = "123", message = "Heelow")

    init {
        viewModelScope.launch {
            populateMovie()
        }
    }



    fun populateMovie() {
        viewModelScope.launch {
            val movie = movieDao.getMovie();
            if (movie != null) {
                _movie.value = movie
                _movieNameL.value = movie.movieName ?: ""
                _cityL.value = movie.city ?: ""
                _showDateL.value = movie.showDate ?: ""
                _locationL.value = movie.location ?: ""
            }
        }
    }

    fun updateMovie(movieName: String, cit: String, showDat: String, loc: String? = "inox") {
        val movie = Movie(id = 0, movieName = movieName, city = cit, showDate = showDat, location = loc ?: "inox")
        viewModelScope.launch {
            movieDao.deleteAllMovies()
            movieDao.insertMovie(movie)
            populateMovie()
            alarmItem?.let (AlarmSchedulerImpl(context)::schedule)
        }
    }

    fun resetMovie() {
        viewModelScope.launch {
            movieDao.deleteAllMovies()
            populateMovie()
            alarmItem?.let (AlarmSchedulerImpl(context)::cancel)
        }
    }

    fun updateMovieNameL(movieText: String) {
        _movieNameL.value = movieText
    }

    fun updateCityL(ci: String) {
        _cityL.value = ci
    }

    fun updateShowDateL(showd: String) {
        _showDateL.value = showd
    }

    fun updateLocationL(loc: String) {
        _locationL.value = loc
    }

    fun isBatteryOptimizationEnabled(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return !powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun openBatteryOptimizationSettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
//        val uri: Uri = Uri.fromParts("package", context.packageName, null)
//        intent.data = uri
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}