package com.example.demoride.fragments

import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.demoride.R
import com.example.demoride.activity.PlaceActivity
import com.example.demoride.databinding.FragmentMainBinding
import com.example.demoride.utils.PermissionUtils
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMainBinding.inflate(layoutInflater)
        binding.layoutRide.setOnClickListener {
           if(PermissionUtils.checkPermission(context)){
               Log.d("mymsg", "On Create: Permission Granted")
               if(PermissionUtils.isGpsEnable(requireContext())){
                   Log.d("mymsg", "On Create: GPS already enable")
                   goToMapFragment()
               }
               else{
                   PermissionUtils.enableGps(requireContext())
               }
           }
            else{
                requestMultiplePermissions.launch(
                    arrayOf(PermissionUtils.ACCESS_FINE_LOCATION,PermissionUtils.ACCESS_COARSE_LOCATION)
                )
           }
        }
        binding.txtCurrent.setOnClickListener {
            if(PermissionUtils.checkPermission(context)){
                Log.d("mymsg", "On Create: Permission Granted")
                if(PermissionUtils.isGpsEnable(requireContext())){
                    Log.d("mymsg", "On Create: GPS already enable")
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                        val location= it.result
                        if(location== null){
                            newLocation()
                        }
                        else{
                            showDialogue(location)
                        }
                    }

                }
                else{
                    PermissionUtils.enableGps(requireContext())
                }
            }
            else{
                requestMultiplePermissions.launch(
                    arrayOf(PermissionUtils.ACCESS_FINE_LOCATION,PermissionUtils.ACCESS_COARSE_LOCATION)
                )
            }
        }

        binding.layoutNear.setOnClickListener {

            if(PermissionUtils.checkPermission(context)){
                Log.d("mymsg", "On Create: Permission Granted")
                if(PermissionUtils.isGpsEnable(requireContext())){
                    Log.d("mymsg", "On Create: GPS already enable")
                    //goToPlaceFragment()
                    startActivity(Intent(context,PlaceActivity::class.java))
                }
                else{
                    PermissionUtils.enableGps(requireContext())
                }

            }

        }

        return binding.root
    }


    private fun newLocation(){
        locationRequest= LocationRequest.create().apply {
            interval = 0
            fastestInterval = 0
            priority= LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates= 2
        }
        if(PermissionUtils.checkPermission(context)){
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,locationCallBack,Looper.myLooper()
            )
        }
        else{
            requestMultiplePermissions.launch(
                arrayOf(PermissionUtils.ACCESS_FINE_LOCATION,PermissionUtils.ACCESS_COARSE_LOCATION)
            )
        }

    }

    private val locationCallBack = object :LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {

            val lastLocation= p0.lastLocation
            showDialogue(lastLocation)

        }
    }

    private fun showDialogue(lastLocation: Location?) {
        val address= getCityName(lastLocation!!)
        val builder= MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Current Location")
        builder.setMessage("Latitude: ${lastLocation.latitude} \nLongitude: ${lastLocation.longitude} \nAddress: ${
            address?.get(0)?.getAddressLine(0)
        } \nCountry: ${address?.get(0)?.countryName}")

        builder.setIcon(R.drawable.ic_location)
        builder.background = resources.getDrawable(R.drawable.dialogue_bg,null)
        builder.setNegativeButton("Close"){ dialogInterface: DialogInterface, i: Int ->

        }
        builder.show()
    }

    private fun getCityName(location: Location): MutableList<Address>? {
        var city="aa"
        val geocoder= Geocoder(context, Locale.getDefault())
        val address= geocoder.getFromLocation(location.latitude,location.longitude,5)
        Log.d("myadd", "getCityName: $address")
        city= "${address[0].getAddressLine(0)},${address[0].countryName}"
        Log.d("myadd", "get City and country: $city")
        return address
    }


    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permission->

        if(permission[PermissionUtils.ACCESS_FINE_LOCATION]==true && permission[PermissionUtils.ACCESS_COARSE_LOCATION] == true){
            Log.d("mymsg", "Request Check: Permission Granted")
        }
        else{
            Log.d("mymsg", "Request Check: Permission Not Granted")
        }

    }
    private fun goToMapFragment(){
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in_right_animation,
                R.anim.slide_out_left_animation,
                R.anim.slide_in_left_animation,
                R.anim.slide_out_right_animation
            )?.replace(R.id.fragment_container,MapFragment())?.addToBackStack(null)?.commit()
    }

}