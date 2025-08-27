package com.example.elements_feed_display.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elements_feed_display.R
import com.example.elements_feed_display.data.repository.FeedRepository
import com.example.elements_feed_display.domain.command.CommandException
import com.example.elements_feed_display.ui.feed.FeedAdapter
import com.example.elements_feed_display.ui.view_model.FeedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val repository = FeedRepository()
    private val viewModel = FeedViewModel(repository)

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeedAdapter
    private lateinit var editText: EditText
    private lateinit var sendButton: Button

    private var uiScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom + imeInsets.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        adapter = FeedAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        editText = findViewById(R.id.EtCommand)
        sendButton = findViewById(R.id.BtnCommandSend)

        sendButton.setOnClickListener { sendCommand() }


        //https://stackoverflow.com/questions/2004344/how-do-i-handle-imeoptions-done-button-click
        editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                sendCommand()
                true
            } else {
                false
            }
        }

        uiScope.launch {
            viewModel.feeds.collectLatest { feeds ->
                adapter.updateItems(feeds)
                recyclerView.scrollToPosition(feeds.size - 1)
            }
        }
    }

    private fun sendCommand(){
        try{
            val command = editText.text.toString()
            viewModel.sendCommand(enumValueOf(command))

            recyclerView.scrollToPosition(adapter.itemCount-1)

            editText.text.clear()
        }catch (commandException: CommandException){
            Toast.makeText(this, commandException.message, Toast.LENGTH_SHORT).show()
        }catch (_: IllegalArgumentException){
            Toast.makeText(this, "Invalid command.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}