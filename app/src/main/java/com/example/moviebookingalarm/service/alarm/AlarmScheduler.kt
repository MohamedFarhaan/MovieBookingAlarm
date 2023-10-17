package com.example.moviebookingalarm.service.alarm

import com.example.moviebookingalarm.db.entity.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}