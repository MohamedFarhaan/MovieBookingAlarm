package com.example.moviebookingalarm.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviebookingalarm.db.DatabaseInit
import com.example.moviebookingalarm.db.entity.AlarmItem
import com.example.moviebookingalarm.db.entity.Movie
import com.example.moviebookingalarm.service.InoxService
import com.example.moviebookingalarm.service.alarm.AlarmSchedulerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(val context: Context, val alarmManager: AlarmManager): ViewModel() {

    lateinit var db: DatabaseInit;
    lateinit var inoxService: InoxService;
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
//    val serviceIntent = Intent(context, ApiService::class.java)
//    val intervalMillis = 10 * 1000 // 10 seconds
//    val triggerAtMillis = System.currentTimeMillis() + intervalMillis
//    val intent = Intent(context, AlarmReceiver::class.java)
//    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

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
                _cityL.value = movie.city ?: ""
                _showDateL.value = movie.showDate ?: ""
                _locationL.value = movie.location ?: ""
            }
        }
    }

    fun updateMovie(movieName: String, cit: String, showDat: String, loc: String? = "inox") {
        val movie = Movie(id = 0, movieName = movieName, city = cit, showDate = showDat, location = loc ?: "inox")
        viewModelScope.launch {
            db.movieDao().deleteAllMovies()
            db.movieDao().insertMovie(movie)
            populateMovie()
//            context.startService(serviceIntent)
            alarmItem?.let (AlarmSchedulerImpl(context)::schedule)
        }
    }

    fun resetMovie() {
        viewModelScope.launch {
            db.movieDao().deleteAllMovies()
            populateMovie()
//            context.stopService(serviceIntent)
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
}