package com.example.mypowerfulandroidapp.ui

interface DataStateChangeListener {
    fun onDataStateChange(dataState: DataState<*>?)
    fun expandAppBar()
    fun hideSoftKeyboard()
    fun isStoragePermissionGranted(): Boolean
}