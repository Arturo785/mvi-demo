package com.codingwithmitch.mviexample.ui

import com.codingwithmitch.mviexample.util.DataState

interface DataStateListener {

    // receives any data state
    fun onDataStateChange(dataState: DataState<*>?)
}