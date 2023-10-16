package com.example.moviebookingalarm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.moviebookingalarm.ui.theme.MovieBookingAlarmTheme
import com.example.moviebookingalarm.viewmodel.MovieViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieModel: MovieViewModel = MovieViewModel(applicationContext);
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC), 0)
        }
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }
        setContent {
            MovieBookingAlarmTheme {
                // A surface container using the 'background' color from the theme
                App(movieModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(movieModel: MovieViewModel) {

    val movieName by movieModel.movieNameL.observeAsState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painterResource(id = R.drawable.inox_icon),
                contentDescription = "App Logo",
                modifier = Modifier.padding(16.dp))
            OutlinedTextField(value = movieName ?: "",
                onValueChange = {
                    movieModel.updateMovieNameL(it)
                },
                label = { Text(text = "Movie Name") })
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    movieModel.resetMovie();
                    movieModel.updateMovieNameL("");
                    throw RuntimeException("Killing the app to stop notification")
                }) {
                    Text(text = "Reset")
                }
                Button(
                    enabled = (movieName?.length ?: 0) > 0,
                    onClick = { movieModel.updateMovie(movieName?:"") }
                ) {
                    Text(text = "Set Alarm")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MovieBookingAlarmTheme {
        App(MovieViewModel(LocalContext.current))
    }
}

fun getDefaultRingtone(context: Context): Uri? {
    return RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE)
}