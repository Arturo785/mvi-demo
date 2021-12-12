package com.codingwithmitch.mviexample.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codingwithmitch.mviexample.R
import com.codingwithmitch.mviexample.model.BlogPost
import com.codingwithmitch.mviexample.model.User
import com.codingwithmitch.mviexample.ui.DataStateListener
import com.codingwithmitch.mviexample.ui.main.state.MainStateEvent
import com.codingwithmitch.mviexample.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.Exception

class MainFragment : Fragment(),
    MainRecyclerAdapter.Interaction {


    private lateinit var viewModel: MainViewModel

    lateinit var dataStateHandler: DataStateListener

    lateinit var mainRecyclerAdapter: MainRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = activity?.run {
            ViewModelProvider(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        subscribeObservers()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecorator)

            mainRecyclerAdapter = MainRecyclerAdapter(this@MainFragment)
            adapter = mainRecyclerAdapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // we initialize our interface
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("$context must implement DataStateListener")
        }

    }

    private fun subscribeObservers() {
        // triggers the observer of viewState
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            println("DEBUG: DataState: ${dataState}")

            // this manages the loading and errors and mainActivity implements it
            dataStateHandler.onDataStateChange(dataState)

            dataState.data?.let { event ->
                // we only set the fields if new data
                event.getContentIfNotHandled()?.let { mainViewState ->

                    mainViewState.blogPosts?.let {
                        // set BlogPosts data
                        viewModel.setBlogListData(it)
                    }

                    mainViewState.user?.let {
                        // set User data
                        viewModel.setUser(it)
                    }

                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.blogPosts?.let {
                // set BlogPosts to RecyclerView
                println("DEBUG: Setting blog posts to RecyclerView: ${viewState.blogPosts}")
                mainRecyclerAdapter.submitList(it)
            }

            viewState.user?.let {
                // set User data to widgets
                println("DEBUG: Setting User data: ${viewState.user}")
                setUserProperties(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_get_blogs -> triggerGetBlogsEvent()

            R.id.action_get_user -> triggerGetUserEvent()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun triggerGetUserEvent() {
        viewModel.setStateEvent(MainStateEvent.GetUserEvent("1"))
    }

    private fun triggerGetBlogsEvent() {
        viewModel.setStateEvent(MainStateEvent.GetBlogPostsEvent())
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        println("DEBUG: CLICKED ${position}")
        println("DEBUG: CLICKED ${item}")
    }

    fun setUserProperties(user: User) {
        email.text = user.email
        username.text = user.username

        view?.let {
            Glide.with(it.context)
                .load(user.image)
                .into(image)
        }

    }
}
