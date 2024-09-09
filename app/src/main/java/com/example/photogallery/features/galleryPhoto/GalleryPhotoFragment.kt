package com.example.photogallery.features.galleryPhoto

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.photogallery.R
import com.example.photogallery.api.ThumbnailDownloader
import com.example.photogallery.api.model.galleryMetadataRequest.GalleryItem
import com.example.photogallery.databinding.FragmentGalleryPhotoBinding
import com.example.photogallery.databinding.GalleryItemHolderBinding
import kotlin.math.max
import kotlin.math.min


private const val SPAN_SIZE = 300
private const val DEFAULT_SPAN_COUNT = 3
private const val PRELOAD_SIZE = 10

class GalleryPhotoFragment : Fragment() {

    private val viewModel: GalleryPhotoFragmentViewModel by viewModels()
    private lateinit var binding: FragmentGalleryPhotoBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var thumbnailDownloader: ThumbnailDownloader<GalleryAdapter.GalleryItemViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(
            this,
            responseHandler
        )
        { holder, bitmap ->
            if (bitmap == null)
                return@ThumbnailDownloader
            val drawable = bitmap.toDrawable(resources)
            holder.bindDrawable(drawable)
        }
    }

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

            viewModel.photosMetadata.observe(viewLifecycleOwner) { pagedGallery ->
                if (pagedGallery != null) {
                    adapter = GalleryAdapter(pagedGallery.thumbnails)

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
        thumbnailDownloader.clearQueue()
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

            thumbnailDownloader.queueThumbnailForShow(holder, galleryItem.url)

            val listForPreload = galleryItems.subList(
                max(position - PRELOAD_SIZE, 0),
                min(position + PRELOAD_SIZE, galleryItems.size - 1)
            )
                .map { it.url }
            thumbnailDownloader.queueThumbnailsForPreload(listForPreload)
        }

        inner class GalleryItemViewHolder(binding: GalleryItemHolderBinding) :
            ViewHolder(binding.root) {
            val bindDrawable: (drawable: Drawable) -> Unit =
                binding.photoImageView::setImageDrawable
        }
    }
}