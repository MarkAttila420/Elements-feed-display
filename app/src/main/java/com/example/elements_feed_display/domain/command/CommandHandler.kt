package com.example.elements_feed_display.domain.command

import com.example.elements_feed_display.data.repository.FeedRepository

class CommandHandler(private val feedRepository: FeedRepository) {
    private var state: State = State.IDLE

    fun handleCommand(command: Command) {
        when(state){
            State.IDLE -> when(command){
                Command.Start -> transition(State.RUNNING) { feedRepository.start() }
                else -> invalid(command)
            }
            State.RUNNING -> when(command){
                Command.Pause -> transition(State.PAUSED) { feedRepository.pause() }
                Command.Stop -> transition(State.STOPPED) { feedRepository.stop() }
                else -> invalid(command)
            }
            State.PAUSED -> when(command){
                Command.Resume -> transition(State.RUNNING) { feedRepository.resume() }
                Command.Stop -> transition(State.STOPPED) { feedRepository.stop() }
                else -> invalid(command)
            }
            State.STOPPED -> throw CommandException("No commands allowed after stopped.")
        }
    }

    private fun transition(newState: State, action: () -> Unit) {
        action()
        state = newState
    }

    private fun invalid(command: Command): Nothing =
        throw CommandException("Invalid command: $command cannot be used in state $state")
}