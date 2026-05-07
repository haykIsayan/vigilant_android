package com.example.project_vig_la.vigilant.state

interface MapEvent {
    data class Message(val message: String) : MapEvent
}