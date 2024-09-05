package com.example.photogallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.photogallery.api.model.PagedGallery.GalleryItem
import com.example.photogallery.databinding.GalleryItemHolderBinding

class GalleryAdapter(
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
        holder.bind(galleryItem)
    }

    class GalleryItemViewHolder(val binding: GalleryItemHolderBinding) : ViewHolder(binding.root) {
        fun bind(item: GalleryItem) {
            binding.titleView.text = item.title
        }
    }
}