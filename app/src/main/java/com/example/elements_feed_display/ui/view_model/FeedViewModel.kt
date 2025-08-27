package com.example.elements_feed_display.ui.view_model

import androidx.lifecycle.ViewModel
import com.example.elements_feed_display.data.model.ListItem
import com.example.elements_feed_display.data.repository.FeedRepository
import com.example.elements_feed_display.domain.command.Command
import com.example.elements_feed_display.domain.command.CommandHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: FeedRepository = FeedRepository()
): ViewModel(){
    val feeds: StateFlow<List<ListItem>> = repository.feeds

    private val commandHandler = CommandHandler(repository)

    fun sendCommand(command: Command){
        commandHandler.handleCommand(command)
    }

}