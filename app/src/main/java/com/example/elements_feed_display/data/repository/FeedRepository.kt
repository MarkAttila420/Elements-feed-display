package com.example.elements_feed_display.data.repository

import com.example.elements_feed_display.data.api.FeedApiService
import com.example.elements_feed_display.data.model.ListItem
import com.example.elements_feed_display.domain.command.Command
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FeedRepository(
    private val feedApiService: FeedApiService = FeedApiService()
){
    private val _feeds = MutableStateFlow<List<ListItem>>(emptyList())
    val feeds: StateFlow<List<ListItem>> get() = _feeds
    private val pendingFeeds = mutableListOf<ListItem>()

    private val _errors = MutableSharedFlow<Throwable>()
    val errors: SharedFlow<Throwable> = _errors

    private var counter = 0
    private var pollingJob: Job? = null

    private var isPaused = false

    fun start(){
        if(pollingJob?.isActive == true) return
        addCommandToList(Command.Start)

        pollingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive){
                var success = false
                var attempts = 0

                while (!success && attempts <3){
                    try {
                        fetchNext()
                        delay(5000)
                        success=true
                    }catch (exception: Exception){
                        attempts++
                        if(attempts>=3){
                            _errors.emit(exception)
                            stop(false)
                        }
                        delay(1000)
                    }
                }
            }
        }
    }

    fun stop(addCommandToList: Boolean = true) {
        if(addCommandToList) addCommandToList(Command.Stop)
        pollingJob?.cancel()
        pollingJob = null
        counter = 0
        pendingFeeds.clear()
    }

    fun pause(){
        addCommandToList(Command.Pause)
        isPaused = true
    }

    fun resume(){
        addCommandToList(Command.Resume)
        isPaused = false
        if (pendingFeeds.isNotEmpty()) {
            val newList = _feeds.value.toMutableList().apply {
                addAll(pendingFeeds)
            }
            _feeds.value = newList
            pendingFeeds.clear()
        }
    }

    private suspend fun fetchNext(){
        val res = feedApiService.getFeedById(counter)
        res.onSuccess { feed ->
            counter++

            if(isPaused){
                pendingFeeds.add(feed)
            } else{
                val currentFeeds = _feeds.value.toMutableList()
                currentFeeds.add(feed)
                _feeds.value = currentFeeds
            }
        }.onFailure { err ->
            throw err
        }
    }

    private fun addCommandToList(command: Command) {
        val currentFeeds = _feeds.value.toMutableList()
        currentFeeds.add(ListItem.Command(command.toString()))
        _feeds.value = currentFeeds
    }
}