package com.codingwithmitch.mviexample.ui.main.state

import com.codingwithmitch.mviexample.model.BlogPost
import com.codingwithmitch.mviexample.model.User

// Represents how the view looks and what it displays in a wrapper containing
// Everything in the view
// In this case the MainActivity

data class MainViewState(
    var blogPosts: List<BlogPost>? = null,
    var user: User? = null
)