package com.example.fakeapi.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fakeapi.R
import com.example.fakeapi.databinding.ActivityCreatePostBinding
import com.google.android.material.snackbar.Snackbar

const val TITLE = "TITLE"
const val BODY = "BODY"

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createButton.setOnClickListener {
            if (binding.body.text!!.isNotEmpty() && binding.title.text!!.isNotEmpty()) {
                val int = Intent()
                int.putExtra(TITLE, binding.title.text.toString())
                int.putExtra(BODY, binding.body.text.toString())
                setResult(RESULT_OK, int)
                finish()
            } else {
                Snackbar.make(binding.root, R.string.fill_all_field, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.body.setText(savedInstanceState.getString(BODY))
        binding.title.setText(savedInstanceState.getString(TITLE))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TITLE, binding.title.text.toString())
        outState.putString(BODY, binding.body.text.toString())
    }
}