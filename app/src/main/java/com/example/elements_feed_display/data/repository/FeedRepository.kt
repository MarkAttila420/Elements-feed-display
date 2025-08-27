package com.example.elements_feed_display.data.repository

import com.example.elements_feed_display.data.api.FeedApiService
import com.example.elements_feed_display.data.model.ListItem
import com.example.elements_feed_display.domain.command.Command
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FeedRepository(
    private val feedApiService: FeedApiService = FeedApiService()
){
    private val _feeds = MutableStateFlow<List<ListItem>>(emptyList())
    val feeds: StateFlow<List<ListItem>> get() = _feeds
    private val pendingFeeds = mutableListOf<ListItem>()

    private var counter = 0
    private var pollingJob: Job? = null

    private var isPaused = false

    fun start(){
        if(pollingJob?.isActive == true) return

        addCommandToList(Command.Start)

        pollingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive){
                fetchNext()
                delay(5000)
            }
        }
    }

    fun stop() {
        addCommandToList(Command.Stop)
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
            println("Error fetching feed: ${err.message}")
        }
    }

    private fun addCommandToList(command: Command) {
        val currentFeeds = _feeds.value.toMutableList()
        currentFeeds.add(ListItem.Command(command.toString()))
        _feeds.value = currentFeeds
    }
}