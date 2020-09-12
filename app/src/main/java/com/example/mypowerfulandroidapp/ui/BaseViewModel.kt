package com.example.mypowerfulandroidapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<StateEvent, ViewState> : ViewModel() {
    protected val _StateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    protected val _ViewState: MutableLiveData<ViewState> = MutableLiveData()

    val viewState: LiveData<ViewState>
        get() = _ViewState

    val dataState: LiveData<DataState<ViewState>> = Transformations
        .switchMap(_StateEvent) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun setStatEvent(event: StateEvent) {
        _StateEvent.value = event
    }

    fun setViewState(viewState: ViewState) {
        _ViewState.value = viewState
    }

    fun getCurrentViewStateOrNew(): ViewState {
        return viewState.value?.let {
            it
        } ?: initNewViewState()
    }

    abstract fun initNewViewState(): ViewState

    abstract fun handleStateEvent(stateEvent: StateEvent): LiveData<DataState<ViewState>>
}