package com.codingwithmitch.mviexample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codingwithmitch.mviexample.model.BlogPost
import com.codingwithmitch.mviexample.model.User
import com.codingwithmitch.mviexample.repository.MainRepository
import com.codingwithmitch.mviexample.ui.main.state.MainStateEvent
import com.codingwithmitch.mviexample.ui.main.state.MainViewState
import com.codingwithmitch.mviexample.util.AbsentLiveData
import com.codingwithmitch.mviexample.util.DataState

class MainViewModel : ViewModel() {

    private val _stateEvent: MutableLiveData<MainStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()

    // handles the wrapper of the view
    // and handles the different models visible in the view
    val viewState: LiveData<MainViewState>
        get() = _viewState

    // handles the actions of the user from the view
    // triggers the different actions
    val dataState: LiveData<DataState<MainViewState>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(stateEvent)
            }
        }


    fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
        println("DEBUG: New StateEvent detected: $stateEvent")
        when (stateEvent) {

            is MainStateEvent.GetBlogPostsEvent -> {
                return MainRepository.getBlogPosts()
            }

            is MainStateEvent.GetUserEvent -> {
                return MainRepository.getUser(stateEvent.userId)
            }

            is MainStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    private fun getCurrentViewStateOrNew(): MainViewState {
        return viewState.value ?: MainViewState()
    }

    fun setBlogListData(blogPosts: List<BlogPost>) {
        val update = getCurrentViewStateOrNew()
        update.blogPosts = blogPosts
        _viewState.value = update
    }

    fun setUser(user: User) {
        val update = getCurrentViewStateOrNew()
        update.user = user
        _viewState.value = update
    }

    fun setStateEvent(event: MainStateEvent) {
        // triggers the switchMap
        _stateEvent.value = event
    }
}

