package com.example.fakeapi.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.example.fakeapi.MyApp.Companion.instance
import com.example.fakeapi.databinding.ActivityMainBinding
import com.example.fakeapi.retrofit.Post
import com.example.fakeapi.ui.PostsListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fakeapi.R


private const val POSTS = "POSTS"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var posts: ArrayList<Post>
    private lateinit var listAdapter: PostsListAdapter

    private var userId = 12

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                data?.getStringExtra(BODY)?.let { body ->
                    data.getStringExtra(TITLE)?.let { title ->
                        createPostInDB(title, body)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listAdapter = PostsListAdapter {
            deletePostFromDB(it)
        }

        binding.posts.apply {
            adapter = listAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }

        getPosts()

        binding.refresh.setOnClickListener {
            fetchPostsFromAPI()
        }

        binding.addPost.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            resultLauncher.launch(intent)
        }

    }

    private suspend fun showCode(code: Int) = withContext(Dispatchers.Main) {
        Snackbar.make(binding.root, code.toString(), Snackbar.LENGTH_SHORT).show()
    }


    private fun deletePostFromDB(post: Post) {
        lifecycle.coroutineScope.launch {
            instance.db.postsDao().delete(post)
            posts.remove(post)
            withContext(Dispatchers.Main) {
                listAdapter.submitList(posts.toList())
            }
        }
    }

    private fun createPostInDB(title: String, body: String) =
        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            val newPost = Post(userId, posts.first().id + 1, title, body)
            instance.db.postsDao().insert(newPost)
            posts.add(0, newPost)
            listAdapter.submitList(posts.toList())
        }
    


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(POSTS, posts)
    }

    private suspend fun getAllPostsFromApi(): List<Post> {
        return try {
            val resp = instance.postService.listPosts()
            showCode(resp.code())
            resp.body()!!.reversed()
        } catch (e: Exception) {
            Snackbar.make(binding.root, R.string.trouble_network, Snackbar.LENGTH_SHORT)
                .show()
            posts
        }
    }

    private fun getPosts() {
        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            posts = ArrayList(instance.db.postsDao().getAll())
            listAdapter.submitList(posts.toList())
        }
    }

    private fun fetchPostsFromAPI() {
        lifecycle.coroutineScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                binding.indeterminateBar.visibility = View.VISIBLE
                binding.addPost.visibility = View.GONE
                binding.posts.visibility = View.GONE
            }

            posts = ArrayList(getAllPostsFromApi())
            instance.db.postsDao().insertAll(*posts.toTypedArray())

            withContext(Dispatchers.Main) {
                listAdapter.submitList(posts.toList())
                binding.indeterminateBar.visibility = View.GONE
                binding.addPost.visibility = View.VISIBLE
                binding.posts.visibility = View.VISIBLE
                binding.posts.layoutManager?.scrollToPosition(0)
            }
        }
    }

}
