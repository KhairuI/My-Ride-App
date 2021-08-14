package com.example.demoride.fragments

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demoride.R
import com.example.demoride.databinding.FragmentDestinationBinding
import com.example.demoride.utils.PermissionUtils
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DestinationFragment : Fragment(),OnMapReadyCallback {
    private lateinit var binding: FragmentDestinationBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var city=""
    private var currentCity=""
    private var latitude=0.0
    private var longitude=0.0
    private lateinit var autocompleteSupportFragment:AutocompleteSupportFragment
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentDestinationBinding.inflate(layoutInflater)
        init()
        binding.btnConfirmDestination.setOnClickListener {
            if(!(city=="")){
                goToMapFragment()
            }

        }

        return binding.root
    }

    private fun goToMapFragment() {
        val bundle= Bundle()
        val latLng= LatLng(latitude,longitude)
        bundle.putParcelable("latlng",latLng)
        bundle.putString("name",city)
        val mapFragment= MapFragment()
        mapFragment.arguments= bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in_right_animation,
                R.anim.slide_out_left_animation,
                R.anim.slide_in_left_animation,
                R.anim.slide_out_right_animation
            )?.disallowAddToBackStack()?.replace(R.id.fragment_container,mapFragment)?.commit()

    }

    private fun init() {
        Places.initialize(requireContext(),"map_api_key(need paid api key in this case)")

        autocompleteSupportFragment= childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteSupportFragment.setPlaceFields(mutableListOf(
            com.google.android.libraries.places.api.model.Place.Field.ID,
            com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
            com.google.android.libraries.places.api.model.Place.Field.LAT_LNG,
            com.google.android.libraries.places.api.model.Place.Field.NAME))
        autocompleteSupportFragment.setOnPlaceSelectedListener(object :PlaceSelectionListener{

            override fun onPlaceSelected(place: com.google.android.libraries.places.api.model.Place) {
                Log.d("mymsg", "onPlaceSelected: Address: ${place.address} \nID: " +
                        "${place.id} \nName: ${place.name} \nlatLng: ${place.latLng}")
                val currentLatLang= place.latLng
                latitude= place.latLng!!.latitude
                longitude= place.latLng!!.longitude
                city= place.address.toString()
                marker.remove()
                addMarker(currentLatLang!!)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang,15f))
            }

            override fun onError(e: Status) {
                Log.d("mymsg", "onError: ${e.statusMessage}")
            }

        })

        val mapFragment= childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap= googleMap
        mMap.uiSettings.isMyLocationButtonEnabled= false
        setMap()
    }

    private fun setMap() {
        if(PermissionUtils.checkPermission(requireContext())){
            mMap.isMyLocationEnabled= true
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val location= it.result
                if(location != null){
                    lastLocation= location
                    // set lat/lang for global variable
                    latitude= lastLocation.latitude
                    longitude= lastLocation.longitude
                    currentCity= getCityName(location)!![0].getAddressLine(0).toString()
                    setRestrictCountry(location)
                    Log.d("mymap", "setMap: ${lastLocation.latitude}")
                    val currentLatLang= LatLng(location.latitude, location.longitude)
                    addMarker(currentLatLang)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang,15f))
                }
            }
        }
    }

    private fun getCityName(location: Location): MutableList<Address>? {
        val geocoder = Geocoder(context, Locale.getDefault())
        Log.d("mymsg", "getCityName: ${ geocoder.getFromLocation(location.latitude, location.longitude, 5)}")
        return geocoder.getFromLocation(location.latitude, location.longitude, 5)
    }

    private fun setRestrictCountry(location: Location) {
        val address= getCityName(location)
        Log.d("mymsg", "getLocality: ${address?.get(0)?.locality}")
        autocompleteSupportFragment.setCountry(address?.get(0)?.countryCode)
    }

    private fun addMarker(currentLatLang: LatLng) {
        marker= mMap.addMarker(MarkerOptions().position(currentLatLang))
    }


}