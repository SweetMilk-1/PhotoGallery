package com.example.photogallery.features.galleryPhoto

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
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
private const val PRELOAD_SIZE = 0

private const val LOG_TAG = "GalleryPhotoFragment"

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

        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_photo_gallery, menu)
                val searchItem = menu.findItem(R.id.app_bar_search)
                val searchView = searchItem.actionView as SearchView

                searchView.apply {
                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            Log.d(LOG_TAG, "QueryTextSubmit: $query")
                            hideKeyboard()
                            viewModel.setSearchText(query ?: "")
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            Log.d(LOG_TAG, "QueryTextChange: $newText")
                            return false
                        }
                    })

                    setOnSearchClickListener{
                        setQuery(viewModel.searchText, false)
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.app_bar_clear_search -> {
                        viewModel.setSearchText("")
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun hideKeyboard() {
        val view = requireActivity().findViewById<View>(android.R.id.content)
        if (view != null) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
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

            thumbnailDownloader.queueThumbnailForShow(holder, galleryItem.url!!)

            val listForPreload = galleryItems.subList(
                max(position - PRELOAD_SIZE, 0),
                min(position + PRELOAD_SIZE, galleryItems.size - 1)
            )
                .map { it.url!! }
            thumbnailDownloader.queueThumbnailsForPreload(listForPreload)
        }

        inner class GalleryItemViewHolder(binding: GalleryItemHolderBinding) :
            ViewHolder(binding.root) {
            val bindDrawable: (drawable: Drawable) -> Unit =
                binding.photoImageView::setImageDrawable
        }
    }
}