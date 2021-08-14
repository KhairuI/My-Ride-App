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
import androidx.lifecycle.lifecycleScope
import com.example.demoride.R
import com.example.demoride.api.RetrofitInstance
import com.example.demoride.databinding.FragmentShoppingBinding
import com.example.demoride.model.ModelNearbyPlace
import com.example.demoride.model.ResultR
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
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ShoppingFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    private lateinit var binding: FragmentShoppingBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentShoppingBinding.inflate(layoutInflater)
        val mapFragment= childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap= p0
        mMap.uiSettings.isMyLocationButtonEnabled= true
        setMap()
    }

    private fun setMap() {
        if(PermissionUtils.checkPermission(requireContext())){
            mMap.isMyLocationEnabled= true
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val location= it.result
                if(location != null){

                    val currentLatLang= LatLng(location.latitude, location.longitude)
                    addMarker(currentLatLang)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang,15f))
                    shoppingApiCall(makeQuery(location))
                }
            }
        }
    }

    private fun shoppingApiCall(url: String) {
        Log.d("mymsg", "RestaurantApiCall: $url")
        lifecycleScope.launchWhenCreated {
            val response= try {
                RetrofitInstance.api.getNearbyPlace(url)
            }catch (e: IOException){
                Log.d("api_error", "$e")
                return@launchWhenCreated

            }catch (e: HttpException){
                Log.d("api_error", "$e")
                return@launchWhenCreated
            }

            if(response.isSuccessful && response.body() != null){
                val body= response.body()!!.string()
                if(body.isNotBlank()){
                    val jsonObject = JSONObject(body)
                    val modelRestaurant= Gson().fromJson(jsonObject.toString(), ModelNearbyPlace::class.java)
                    val result= modelRestaurant.results
                    setShopping(result)
                }
            }
        }

    }

    private fun setShopping(result: List<ResultR>?) {
        val iterator = result?.listIterator()
        for(i in iterator!!){
            val lat= i.geometry?.location?.lat
            val lng= i.geometry?.location?.lng
            val latLng= LatLng(lat!!,lng!!)
            mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.shopping_icon)).snippet(i.vicinity)
                .title(i.name))
        }
    }

    private fun makeQuery(location: Location):String{
        val place= "${location.latitude},${location.longitude}"
        return "maps/api/place/nearbysearch/json?location=${place}&radius=5000&type=shopping_mall&key=map_api_key(need paid api key in this case)"
    }

    private fun addMarker(currentLatLang: LatLng) {
        val address= getCityName(currentLatLang)
        val city= address?.get(0)?.getAddressLine(0).toString()
        val markerOption= MarkerOptions().position(currentLatLang).title(city)
        mMap.addMarker(markerOption)
    }

    private fun getCityName(latLng: LatLng): MutableList<Address>? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5)
    }

    override fun onMarkerClick(p0: Marker)= false

}