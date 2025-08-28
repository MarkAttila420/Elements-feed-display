package com.example.elements_feed_display.ui.view_model

import androidx.lifecycle.ViewModel
import com.example.elements_feed_display.data.model.ListItem
import com.example.elements_feed_display.data.repository.FeedRepository
import com.example.elements_feed_display.domain.command.Command
import com.example.elements_feed_display.domain.command.CommandHandler
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class FeedViewModel(
    private val repository: FeedRepository = FeedRepository()
): ViewModel(){
    val feeds: StateFlow<List<ListItem>> = repository.feeds
    val errors: SharedFlow<Throwable> = repository.errors

    private val commandHandler = CommandHandler(repository)

    fun sendCommand(command: Command){
        commandHandler.handleCommand(command)
    }

}