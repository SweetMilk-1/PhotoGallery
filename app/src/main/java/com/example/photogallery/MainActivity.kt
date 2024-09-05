package com.example.photogallery

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photogallery.databinding.ActivityMainBinding
import com.example.photogallery.features.galleryPhoto.GalleryAdapter
import com.example.photogallery.features.galleryPhoto.GalleryPhotoFragment
import com.example.photogallery.features.galleryPhoto.GalleryPhotoFragmentViewModel

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
}