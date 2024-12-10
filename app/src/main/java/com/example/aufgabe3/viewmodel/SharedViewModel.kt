package com.example.aufgabe3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aufgabe3.model.BookingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel: ViewModel() {
    private val _bookingsEntries = MutableStateFlow<List<BookingEntry>>(emptyList())
    val bookingsEntries: StateFlow<List<BookingEntry>> = _bookingsEntries


    /**
     * Fügt einen neuen Buchungseintrag zur Liste hinzu.
     *
     * @param entry Der neue Buchungseintrag, der zur Liste hinzugefügt werden soll.
     */
    fun addBookingEntry(entry: BookingEntry){
        // TODO create a new booking entry and save it
        _bookingsEntries.value = _bookingsEntries.value + entry
    }

    /**
     * Entfernt einen Buchungseintrag aus der Liste.
     *
     * @param entry Der Buchungseintrag, der aus der Liste entfernt werden soll.
     */
    fun deleteBookingEntry(entry: BookingEntry){
        // TODO delete a new booking entry
        _bookingsEntries.value = _bookingsEntries.value - entry
    }
}