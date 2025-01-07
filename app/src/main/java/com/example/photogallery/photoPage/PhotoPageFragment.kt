package com.example.photogallery.photoPage

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.photogallery.VisibleFragment
import com.example.photogallery.databinding.FragmentPhotoPageBinding

class PhotoPageFragment : VisibleFragment() {

    private lateinit var uri: Uri
    private lateinit var binding: FragmentPhotoPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.max = 100

        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100) {
                        binding.progressBar.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar.progress = newProgress
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    (requireActivity() as AppCompatActivity).supportActionBar?.subtitle = title
                }
            }

            loadUrl(uri.toString())
        }
    }

    companion object {
        private const val ARG_URI = "ARG_URI"

        fun newInstance(uri: Uri?): PhotoPageFragment {
            return PhotoPageFragment().apply {
                arguments = bundleOf(
                    ARG_URI to uri
                )
            }
        }
    }
}