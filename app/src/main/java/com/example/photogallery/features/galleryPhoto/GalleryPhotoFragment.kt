package com.example.photogallery.features.galleryPhoto

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.photogallery.R
import com.example.photogallery.api.ThumbnailDownloader
import com.example.photogallery.api.model.PagedGallery.GalleryItem
import com.example.photogallery.databinding.FragmentGalleryPhotoBinding
import com.example.photogallery.databinding.GalleryItemHolderBinding


private const val SPAN_SIZE = 300
private const val DEFAULT_SPAN_COUNT = 3

class GalleryPhotoFragment : Fragment() {

    private val viewModel: GalleryPhotoFragmentViewModel by viewModels()
    private lateinit var binding: FragmentGalleryPhotoBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var thumbnailDownloader: ThumbnailDownloader<GalleryAdapter.GalleryItemViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { holder, bitmap ->
            if (bitmap == null)
                return@ThumbnailDownloader
            val drawable = bitmap.toDrawable(resources)
            holder.bindDrawable(drawable)
        }

        lifecycle.addObserver(
            thumbnailDownloader.fragmentLifecycleEventObserver
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycle.addObserver(
            thumbnailDownloader.viewLifecycleEventObserver
        )

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

                    binding.nextButton.visibility =
                        if (pagedGallery.page != pagedGallery.pages) View.VISIBLE else View.INVISIBLE
                    binding.prevButton.visibility =
                        if (pagedGallery.page != 1) View.VISIBLE else View.INVISIBLE
                    binding.pageTextView.text =
                        getString(R.string.pages, pagedGallery.page, pagedGallery.pages)

                }
            }

            viewModel.isProgressBarVisible.observe(viewLifecycleOwner) { isVisible ->
                binding.galleryRecyclerView.isVisible = !isVisible
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(
            thumbnailDownloader.viewLifecycleEventObserver
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(
            thumbnailDownloader.fragmentLifecycleEventObserver
        )
    }

    companion object {
        fun newInstance(): GalleryPhotoFragment {
            return GalleryPhotoFragment()
        }
    }

    inner class GalleryAdapter(
        val galleryItems: List<GalleryItem>
    ) : Adapter<GalleryAdapter.GalleryItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
            LayoutInflater.from(parent.context)
            val holderBinding =
                GalleryItemHolderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return GalleryItemViewHolder(holderBinding)
        }

        override fun getItemCount() = galleryItems.size

        override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
            val galleryItem = galleryItems[position]
            val placeholder = AppCompatResources
                .getDrawable(
                    requireContext(),
                    android.R.drawable.ic_menu_camera
                ) ?: ColorDrawable()
            holder.bindDrawable(placeholder)
            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }

        inner class GalleryItemViewHolder(binding: GalleryItemHolderBinding) :
            ViewHolder(binding.root) {
            val bindDrawable: (drawable: Drawable) -> Unit =
                binding.photoImageView::setImageDrawable
        }
    }
}