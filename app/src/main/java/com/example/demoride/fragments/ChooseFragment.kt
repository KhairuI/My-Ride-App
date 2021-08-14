package com.example.demoride.fragments

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.demoride.R
import com.example.demoride.adapter.PlaceAdapter
import com.example.demoride.api.RetrofitInstance
import com.example.demoride.databinding.FragmentChooseBinding
import com.example.demoride.model.ModelPlaceList
import com.example.demoride.model.ModelPlaceName
import com.example.demoride.utils.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChooseFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnCameraIdleListener,GoogleMap.OnCameraMoveListener,
         GoogleMap.OnCameraMoveStartedListener,PlaceAdapter.OnPlaceClickListener{
    private lateinit var binding: FragmentChooseBinding
    private lateinit var mMap:GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var city=""
    private var latitude=0.0
    private var longitude=0.0
    private lateinit var adapter: PlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentChooseBinding.inflate(layoutInflater)
        val mapFragment= childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnConfirmDestination.setOnClickListener {

            goToMapFragment()

        }
        binding.edtChooseDestination.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(s.isNullOrEmpty()){
                    binding.placeRecycle.visibility= View.GONE
                }
                else{
                    binding.placeRecycle.visibility= View.VISIBLE
                    placeListApiCall(makePlaceListQuery(s ,"map_api_key(need paid api key in this case)"))

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        setAdapter()
        return binding.root
    }

    private fun placeListApiCall(query: String) {

        lifecycleScope.launchWhenCreated {
            val response= try {
                RetrofitInstance.api.getPlaceList(query)
            }catch (e:IOException){
                Log.d("api_error", "$e")
                return@launchWhenCreated

            }catch (e:HttpException){
                Log.d("api_error", "$e")
                return@launchWhenCreated
            }

            if(response.isSuccessful && response.body() != null){

                val body= response.body()!!.string()
                if(body.isNotBlank()){
                    val jsonObject = JSONObject(body)
                    val placeListModel = Gson().fromJson(jsonObject.toString(), ModelPlaceList::class.java)
                    val locationList= placeListModel.predictions
                    Log.d("mymsg", "list: ${locationList?.get(0)?.placeId}")
                    adapter.clear(true)
                    adapter.addAll(locationList!!,true)
                }


            }

        }

    }

    private fun setAdapter() {
        Log.d("mymsg", "adapter call")
        binding.placeRecycle.setHasFixedSize(true)
        adapter= PlaceAdapter(this)
        binding.placeRecycle.adapter= adapter
    }
    override fun onPlaceClick(placeID: String?, placeName: String?) {
        binding.txtDestinationLocation.text= placeName
        binding.edtChooseDestination.setText(placeName)
        binding.placeRecycle.visibility= View.GONE
        city= placeName!!
        placeApiCall(makePlaceQuery(placeID!!,"map_api_key(need paid api key in this case)"))
    }

    private fun placeApiCall(makePlaceQuery: String) {

        lifecycleScope.launchWhenCreated {
            val response= try {
                RetrofitInstance.api.getPlace(makePlaceQuery)
            }catch (e:IOException){
                Log.d("api_error", "$e")
                return@launchWhenCreated

            }catch (e:HttpException){
                Log.d("api_error", "$e")
                return@launchWhenCreated
            }

            if(response.isSuccessful && response.body() != null){
                val body= response.body()!!.string()
                if(body.isNotBlank()){
                    val jsonObject = JSONObject(body)
                    val modelPlaceName= Gson().fromJson(jsonObject.toString(), ModelPlaceName::class.java)
                    val result= modelPlaceName.result
                    val geometry= result?.geometry
                    val location= geometry?.location
                    latitude= location?.lat!!
                    longitude= location.lng!!
                    mMap.setOnCameraMoveListener(null)
                    val latLan= LatLng(latitude, longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLan,15f))

                }
            }
        }
    }

    private fun makePlaceListQuery(s: CharSequence, key: String): String {
        return "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=${s}&key=${key}"
    }

    private fun makePlaceQuery(placeId:String, key: String): String {
        return "https://maps.googleapis.com/maps/api/place/details/json?placeid=${placeId}&key=${key}"
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap= googleMap
        mMap.setOnCameraIdleListener(this)
        mMap.setOnCameraMoveStartedListener(this)
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
                    Log.d("mymap", "setMap: ${lastLocation.latitude}")
                    val currentLatLang= LatLng(location.latitude, location.longitude)
                    val address= getCityName(lastLocation)
                    city= address?.get(0)?.getAddressLine(0).toString()
                    binding.txtDestinationLocation.text= city
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang,15f))
                }
            }
        }
    }

    private fun getCityName(location: Location): MutableList<Address>? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 5)
    }

    override fun onCameraIdle() {
        Log.d("mymsg", "onCameraIdle: is working")
        val cameraPosition= mMap.cameraPosition
        val location= Location(LocationManager.GPS_PROVIDER)
        location.apply {
            latitude= cameraPosition.target.latitude
            longitude= cameraPosition.target.longitude
        }
        // set lat/lang for global variable
        latitude= location.latitude
        longitude= location.longitude

        val address= getCityName(location)
        city= address?.get(0)?.getAddressLine(0).toString()
        binding.txtDestinationLocation.text= city
        mMap.setOnCameraMoveListener(this)
    }

    override fun onCameraMove() {
        //Log.d("mymsg", "onCameraMove: is working")
    }

    override fun onCameraMoveStarted(reason: Int) {
        Log.d("mymsg", "onCameraMoveStarted: is working")
        if(reason== GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
            Log.d("mymsg", "onCameraMoveStarted: The user gestured on the map.")
            mMap.setOnCameraMoveStartedListener(this)
        }
    }


}

