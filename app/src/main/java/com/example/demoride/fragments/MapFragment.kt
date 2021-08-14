package com.example.demoride.fragments

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demoride.R
import com.example.demoride.databinding.FragmentMapBinding
import com.example.demoride.model.ModelDriver
import com.example.demoride.utils.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MapFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mMap:GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation:Location
    private var city=""
    private var currentLat=0.0
    private var currentLng=0.0
    private var destinationLat=0.0
    private var destinationLng=0.0
    private lateinit var driverList: MutableList<ModelDriver>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMapBinding.inflate(layoutInflater)
        getValue()
        val mapFragment= childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnMyLocation.setOnClickListener {
            goToMyLocation()
        }
        binding.txtDestinationLocation.setOnClickListener {
            goToChooseFragment()
        }
        binding.btnConfirm.setOnClickListener {
            if(binding.txtDestinationLocation.text.toString()!="Choose Destination"){
                sentRoute()
            }
            else{
                Snackbar.make(requireView(),"Select your destination",Snackbar.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun sentRoute() {
        val bundle= Bundle()
        val origin= LatLng(currentLat,currentLng)
        val destination= LatLng(destinationLat,destinationLng)
        bundle.putParcelable("origin",origin)
        bundle.putParcelable("destination",destination)

        val routeFragment= RouteFragment()
        routeFragment.arguments= bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in_right_animation,
                R.anim.slide_out_left_animation,
                R.anim.slide_in_left_animation,
                R.anim.slide_out_right_animation
            )?.replace(R.id.fragment_container,routeFragment)?.addToBackStack(null)?.commit()
    }

    private fun getValue() {
        val latLng= arguments?.getParcelable<LatLng>("latlng")
        if(latLng==null){
            Log.d("mymsg", "getValue: Empty")
        }
        else{
            destinationLat= latLng.latitude
            destinationLng= latLng.longitude
        }
        val name= arguments?.getString("name")
        if(name.isNullOrBlank()){
            binding.txtDestinationLocation.text="Choose Destination"
        }
        else{
            binding.txtDestinationLocation.text= name
        }


    }

    private fun goToChooseFragment() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in_right_animation,
                R.anim.slide_out_left_animation,
                R.anim.slide_in_left_animation,
                R.anim.slide_out_right_animation
            )?.replace(R.id.fragment_container,ChooseFragment())?.addToBackStack(null)?.commit()
    }

    private fun goToMyLocation() {
        if(PermissionUtils.checkPermission(requireContext())){
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val location= it.result
                if(location != null){
                    lastLocation= location
                    Log.d("mymap", "setMap: ${lastLocation.latitude}")
                    val currentLatLang= LatLng(location.latitude, location.longitude)
                    addMarker(currentLatLang)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang,15f))
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap= googleMap
       // mMap.uiSettings.isZoomControlsEnabled= true
        mMap.uiSettings.isMyLocationButtonEnabled= false
        mMap.setOnMarkerClickListener(this)
        setMap()
    }

    private fun setMap() {

        if(PermissionUtils.checkPermission(requireContext())){
            mMap.isMyLocationEnabled= true
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val location= it.result
                if(location != null){
                    lastLocation= location
                    currentLat= location.latitude
                    currentLng= location.longitude
                    Log.d("mymap", "setMap: ${lastLocation.latitude}")
                    val currentLatLang= LatLng(location.latitude, location.longitude)
                    addMarker(currentLatLang)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang,15f))
                    setNearestDriver()
                }
            }
        }
    }

    private fun setNearestDriver() {
        makeDummyData()
        val iterator= driverList.listIterator()
        for(i in iterator){
            if(i.type=="Bike"){
                mMap.addMarker(MarkerOptions().position(i.latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
                    .title(i.address))
            }
            else{
                mMap.addMarker(MarkerOptions().position(i.latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi))
                    .title(i.address))
            }

        }

    }

    private fun makeDummyData() {
        // this data is static which i collect from around my location
        driverList= mutableListOf(ModelDriver("154 new polton Rd, Dhaka 1205", LatLng(23.7298129,90.383732),"Bike"))
        driverList.add(ModelDriver("Kataban Rd, Dhaka 1205", LatLng(23.7349005,90.3834316),"Taxi"))
        driverList.add(ModelDriver("Azimpur Rd, Dhaka 1205", LatLng(23.727812, 90.386097),"Taxi"))
        driverList.add(ModelDriver("Dhaka University Campus, Dhaka 1205", LatLng(23.733466, 90.392496),"Bike"))
    }

    private fun addMarker(currentLatLang: LatLng) {
        val address= getCityName(lastLocation)
        city= address?.get(0)?.getAddressLine(0).toString()
        binding.txtSelfLocation.text= city
        val markerOption= MarkerOptions().position(currentLatLang).title(city)
        mMap.addMarker(markerOption)
    }

    private fun getCityName(location: Location): MutableList<Address>? {
        val geocoder = Geocoder(context, Locale.getDefault())
        Log.d("mymsg", "getCityName: ${ geocoder.getFromLocation(location.latitude, location.longitude, 5)}")
        return geocoder.getFromLocation(location.latitude, location.longitude, 5)
    }

    override fun onMarkerClick(p0: Marker)= false
}