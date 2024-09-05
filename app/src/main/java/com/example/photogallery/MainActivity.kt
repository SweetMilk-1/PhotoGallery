package com.example.photogallery

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photogallery.databinding.ActivityMainBinding

//git test 
private const val LOG_TAG = "MainActivity"

private const val SPAN_SIZE = 450
private const val DEFAULT_SPAN_COUNT = 3

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater).also { binding ->
            setContentView(binding.root)

            binding.galleryRecyclerView.apply {
                gridLayoutManager = GridLayoutManager(this@MainActivity, DEFAULT_SPAN_COUNT)
                binding.galleryRecyclerView.layoutManager = gridLayoutManager

                viewTreeObserver.addOnGlobalLayoutListener {
                    @Suppress("DEPRECATION")
                    gridLayoutManager.spanCount = display.width / SPAN_SIZE
                }

                viewModel.photosLiveData.observe(this@MainActivity) { pagedGallery ->
                    if (pagedGallery != null) {
                        adapter = GalleryAdapter(pagedGallery.galleryItems)

                        binding.nextButton.isEnabled = pagedGallery.page != pagedGallery.pages
                        binding.prevButton.isEnabled = pagedGallery.page != 1
                        binding.pageTextView.text = getString(R.string.pages, pagedGallery.page, pagedGallery.pages)

                    }
                }

                viewModel.isProgressBarVisible.observe(this@MainActivity) { isVisible ->
                    binding.galleryRecyclerView.isVisible = !isVisible
                    binding.bottomBar.isVisible = !isVisible
                    binding.progressBar.isVisible = isVisible
                }

                binding.prevButton.setOnClickListener {
                    viewModel.prevPage()
                }

                binding.nextButton.setOnClickListener {
                    viewModel.nextPage()
                }
            }
        }
    }
}