package com.wibisa.dicodingstoryapp.ui.explore

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.wibisa.dicodingstoryapp.R
import com.wibisa.dicodingstoryapp.core.data.remote.response.Story
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import com.wibisa.dicodingstoryapp.core.util.indonesiaLocation
import com.wibisa.dicodingstoryapp.core.util.showShortSnackBar
import com.wibisa.dicodingstoryapp.databinding.CardForMapExploreBinding
import com.wibisa.dicodingstoryapp.databinding.FragmentExploreBinding
import com.wibisa.dicodingstoryapp.viewmodel.ExploreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExploreFragment : Fragment(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private lateinit var binding: FragmentExploreBinding
    private lateinit var map: GoogleMap
    private val viewModel: ExploreViewModel by viewModels()
    private val mainNavController: NavController? by lazy { view?.findNavController() }
    private val latLngBoundsBuilder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMapToolbarEnabled = true

        CameraUpdateFactory.newLatLngZoom(indonesiaLocation, 4f)

        observeUserPreferencesForGetStories()

        observeStoriesUiState()

        setMapStyle()

        map.setInfoWindowAdapter(this)
        map.setOnInfoWindowClickListener {
            val story = it.tag as Story
            val destination = ExploreFragmentDirections.actionExploreToStoryDetails(story)
            mainNavController?.navigate(destination)
        }
    }

    private fun setMapStyle() {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success)
                Log.e("ExploreFragment", "Style parsing failed")
        } catch (e: Resources.NotFoundException) {
            Log.e("ExploreFragment", "Cannot find style, Error: ", e)
        }
    }

    private fun addManyStoryMarkerOnMap(stories: List<Story>) {
        stories.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            map.addMarker(MarkerOptions().position(latLng))?.tag = story
            latLngBoundsBuilder.include(latLng)
        }

        val bounds = latLngBoundsBuilder.build()
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                200
            )
        )
    }

    private fun observeUserPreferencesForGetStories() {
        viewModel.userPreferences.observe(viewLifecycleOwner) {
            getStories(it)
        }
    }

    private fun getStories(userPreferences: UserPreferences) {
        viewModel.getAllStories(userPreferences)
    }

    private fun observeStoriesUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.storiesUiState.collect { ui ->
                    when (ui) {
                        is ApiResult.Success -> {
                            binding.loadingIndicator.hide()
                            addManyStoryMarkerOnMap(ui.data)
                            viewModel.getAllStoriesCompleted()
                        }
                        is ApiResult.Loading -> {
                            binding.loadingIndicator.show()
                        }
                        is ApiResult.Error -> {
                            binding.loadingIndicator.hide()
                            binding.exploreContainer.showShortSnackBar(ui.message)
                            viewModel.getAllStoriesCompleted()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val bindingToolTips =
            CardForMapExploreBinding.inflate(LayoutInflater.from(requireContext()))

        val story = marker.tag as Story

        bindingToolTips.tvName.text = story.name
        bindingToolTips.tvDescription.text = story.description
        return bindingToolTips.root
    }

}