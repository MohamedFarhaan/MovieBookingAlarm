package com.example.moviebookingalarm

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.moviebookingalarm.constants.INOX_CITY
import com.example.moviebookingalarm.ui.theme.MovieBookingAlarmTheme
import com.example.moviebookingalarm.viewmodel.MovieViewModel
import java.util.Calendar


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val movieModel: MovieViewModel = MovieViewModel(applicationContext, alarmManager);
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC), 0)
        }
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }
        var notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("101", "Inox Movie Booking Alarm", importance)
            channel.setSound(null, null)
            // Register the channel with the system.

            notificationManager.createNotificationChannel(channel)
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
    val cityName by movieModel.cityL.observeAsState()
    val showDateName by movieModel.showDateL.observeAsState()
    val locationName by movieModel.locationL.observeAsState()
    var cityDropDownExpanded by remember { mutableStateOf(false) }

    var datePickerDialog: DatePickerDialog = DatePickerDialog(LocalContext.current, {
            _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
        movieModel.updateShowDateL("${String.format("%0" + 2 + "d", (mMonth+1).toInt())}/${String.format("%0" + 2 + "d", mDayOfMonth.toInt())}/$mYear")
    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONDAY), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

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
            Text(text = "BOOKING ALARM",
                modifier = Modifier.padding(16.dp),
                letterSpacing = 5.sp,
                fontSize = 12.sp)
            OutlinedTextField(value = movieName ?: "",
                onValueChange = {
                    movieModel.updateMovieNameL(it)
                },
                label = { Text(text = "Movie Name") })
            OutlinedTextField(value = cityName ?: "",
                readOnly = true,
                onValueChange = {
                    movieModel.updateCityL(it.trim().lowercase())
                },
                label = { Text(text = "City") },
                leadingIcon = { IconButton(onClick = { cityDropDownExpanded = true }) {
                    Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = "Location")
                } })
            if (cityDropDownExpanded) {
                DropdownMenu(
                    expanded = cityDropDownExpanded,
                    onDismissRequest = { cityDropDownExpanded = false }
                ) {
                    INOX_CITY.values.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.trim().lowercase()) },
                            onClick = {
                                movieModel.updateCityL(item.trim().lowercase())
                                cityDropDownExpanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = showDateName?:"",
                onValueChange = { movieModel.updateShowDateL(it) },
                label = { Text(text = "Date(MM/DD/YYYY)") },
                leadingIcon = { IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date picker")
                } }
            )
            OutlinedTextField(value = locationName ?: "",
                onValueChange = {
                    movieModel.updateLocationL(it.trim().lowercase())
                },
                label = { Text(text = "Inox Theatre Venue name (Optional)", fontSize = 12.sp) })
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    movieModel.resetMovie();
                    movieModel.updateMovieNameL("");
                    movieModel.updateCityL("");
                    movieModel.updateShowDateL("");
                    movieModel.updateLocationL("");
//                    throw RuntimeException("Killing the app to stop notification")
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val packageName = movieModel.context.packageName
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
                    movieModel.context.startActivity(intent)
                }) {
                    Text(text = "Reset")
                }
                Button(
                    enabled = (movieName?.length ?: 0) > 0 && (cityName?.length ?: 0) > 0 && (showDateName?.length ?: 0) > 0,
                    onClick = { movieModel.updateMovie(movieName?:"", cityName?:"", showDateName?:"", if(locationName!!.trim().length >0) locationName!!.trim() else null) }
                ) {
                    Text(text = "Set Alarm")
                }
            }
        }
    }
}