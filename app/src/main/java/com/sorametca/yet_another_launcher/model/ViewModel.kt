package com.sorametca.yet_another_launcher.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sorametca.yet_another_launcher.fragments.SortTypes

class ViewModel : ViewModel() {

    private val _sortType =
        MutableLiveData<SortTypes>(SortTypes.ALPHABETICAL_ASC)

    val sortType: LiveData<SortTypes> = _sortType

    fun setSortType(sortType: SortTypes) {
        _sortType.value = sortType
    }
}