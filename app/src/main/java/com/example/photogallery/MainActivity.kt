package com.example.photogallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photogallery.features.galleryPhoto.GalleryPhotoFragment

//git test
private const val LOG_TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var galleryPhotoFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (galleryPhotoFragment == null) {
            galleryPhotoFragment = GalleryPhotoFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, galleryPhotoFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        fun newIntent(context: Context) :Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}