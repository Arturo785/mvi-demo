package com.codingwithmitch.mviexample.ui.main.state


// the actions that can be performed in the UI and triggered by the user
sealed class MainStateEvent {

    class GetBlogPostsEvent() : MainStateEvent()

    data class GetUserEvent(
        val userId: String
    ) : MainStateEvent()

    class None : MainStateEvent()


}