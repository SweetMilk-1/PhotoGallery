package com.example.photogallery.features.galleryPhoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photogallery.R
import com.example.photogallery.databinding.FragmentGalleryPhotoBinding


private const val SPAN_SIZE = 450
private const val DEFAULT_SPAN_COUNT = 3

class GalleryPhotoFragment : Fragment() {

    private lateinit var binding: FragmentGalleryPhotoBinding
    private val viewModel: GalleryPhotoFragmentViewModel by viewModels()
    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryPhotoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.galleryRecyclerView.apply {
            gridLayoutManager = GridLayoutManager(requireContext(), DEFAULT_SPAN_COUNT)
            binding.galleryRecyclerView.layoutManager = gridLayoutManager

            viewTreeObserver.addOnGlobalLayoutListener {
                @Suppress("DEPRECATION")
                gridLayoutManager.spanCount = display.width / SPAN_SIZE
            }

            viewModel.photosLiveData.observe(viewLifecycleOwner) { pagedGallery ->
                if (pagedGallery != null) {
                    adapter = GalleryAdapter(pagedGallery.galleryItems)

                    binding.nextButton.isEnabled = pagedGallery.page != pagedGallery.pages
                    binding.prevButton.isEnabled = pagedGallery.page != 1
                    binding.pageTextView.text =
                        getString(R.string.pages, pagedGallery.page, pagedGallery.pages)

                }
            }

            viewModel.isProgressBarVisible.observe(viewLifecycleOwner) { isVisible ->
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

    companion object {
        fun newInstance(): GalleryPhotoFragment {
            return GalleryPhotoFragment()
        }
    }
}