package com.example.demoride.fragments

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.demoride.R
import com.example.demoride.api.RetrofitInstance
import com.example.demoride.databinding.FragmentRouteBinding
import com.example.demoride.model.Leg
import com.example.demoride.model.ModelRoute
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.*


class RouteFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    private lateinit var binding: FragmentRouteBinding
    private lateinit var mMap:GoogleMap
    private lateinit var origin:LatLng
    private lateinit var destination:LatLng
    private  var latLngs = mutableListOf<LatLng>()
    private var paths= mutableListOf<LatLng>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentRouteBinding.inflate(layoutInflater)
        getValue()
        val mapFragment= childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    private fun getValue() {
        origin= arguments?.getParcelable<LatLng>("origin")!!
        destination= arguments?.getParcelable<LatLng>("destination")!!
    }

    override fun onMapReady(googleMap : GoogleMap) {
        mMap= googleMap
        mMap.uiSettings.isZoomControlsEnabled= false
        mMap.uiSettings.isMyLocationButtonEnabled= false
        mMap.setOnMarkerClickListener(this)
        setMap()
    }

    private fun setMap() {
        val originStr="${origin.latitude},${origin.longitude}"
        val destinationStr="${destination.latitude},${destination.longitude}"

        val originAddress= getCityName(origin)
        mMap.addMarker(MarkerOptions().position(origin).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_location))
            .snippet(originAddress).title("Pick up"))

        val destinationAddress= getCityName(destination)
        mMap.addMarker(MarkerOptions().position(destination).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red))
            .snippet(destinationAddress).title("Destination"))

       routeApiCall(makeRouteQuery(originStr,destinationStr,"map_api_key(need paid api key in this case)"))


    }

    private fun routeApiCall(makeRouteQuery: String) {
        lifecycleScope.launchWhenCreated {
            val response= try {
                RetrofitInstance.api.getRoute(makeRouteQuery)
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
                    val modelRoute= Gson().fromJson(jsonObject.toString(), ModelRoute::class.java)
                    getDirection(modelRoute)
                }
            }
        }

    }

    private fun getDirection(modelRoute: ModelRoute?) {
        if(modelRoute != null){
            val routeList= modelRoute.routes
            if(routeList.isNullOrEmpty()){
                Snackbar.make(requireView(),"No route found",Snackbar.LENGTH_SHORT).show()
            }
            else{
                val routeItem= routeList[0]
                val legList= routeItem.legs
                val legItem= legList?.get(0)
                val stepList= legItem?.steps
                setData(legItem)
                latLngs.clear()
                val iterator = stepList?.listIterator()
                for(i in iterator!!){
                    val points= i.polyline?.points
                    latLngs.addAll(PolyUtil.decode(points))
                }
                paths = latLngs
                drawPoly(paths)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(legItem: Leg?) {

        val distance= legItem?.distance?.value
        val duration= legItem?.duration?.value
        val distanceKM= distance?.div(1000.00)
        Log.d("mymsg", "setData: distanceKM= ${String.format("%.2f",distanceKM)}")
        binding.txtDistance.text= "${String.format("%.2f",distanceKM)} KM"

        val durationMin= duration?.div(60)
        binding.txtTime.text= "$durationMin Min"
    }

    private fun drawPoly(paths: MutableList<LatLng>) {
        val polyLine= mMap.addPolyline(PolylineOptions().addAll(paths))
        polyLine.apply {
            endCap= RoundCap()
            width= 12f
            color= ContextCompat.getColor(requireContext(),R.color.purple_500)
            jointType= JointType.ROUND
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin,15f))
    }



    private fun makeRouteQuery(origin:String,destination:String,key:String):String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin}&destination=${destination}&mode=DRIVING&key=${key}&language=en"
    }

    private fun getCityName(latLng: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address= geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5)
        return address?.get(0)?.getAddressLine(0).toString()
    }

    override fun onMarkerClick(p0: Marker)= false
}



