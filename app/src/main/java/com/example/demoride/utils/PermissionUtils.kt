package com.example.demoride.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PermissionUtils() {

    companion object{

       // val PERMISSIONS= arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
       const val ACCESS_FINE_LOCATION= Manifest.permission.ACCESS_FINE_LOCATION
       const val ACCESS_COARSE_LOCATION= Manifest.permission.ACCESS_COARSE_LOCATION
        const val PERMISSION=1


        // check permission
        fun checkPermission(context: Context?):Boolean{
            if((context?.let { ContextCompat.checkSelfPermission(it, ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED) &&
                (context.let { ContextCompat.checkSelfPermission(it, ACCESS_COARSE_LOCATION) } == PackageManager.PERMISSION_GRANTED) ){
                return true
            }
            return false

        }

       /* // request permission
        fun requestPermission(context: Context){
            ActivityCompat.requestPermissions(context as Activity, arrayOf(ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION), PERMISSION)
        }*/

        fun isGpsEnable(context: Context):Boolean{
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        fun enableGps(context: Context){

            val mSettingsClient= LocationServices.getSettingsClient(context)
            var mLocationSettingsRequest: LocationSettingsRequest?= null
            val mLocationRequest= LocationRequest.create()
            mLocationRequest.apply {
                priority= LocationRequest.PRIORITY_HIGH_ACCURACY
                interval= (30 * 1000)
                fastestInterval= (5 * 1000)
            }

            if(mLocationRequest != null){
                val builder: LocationSettingsRequest.Builder= LocationSettingsRequest.Builder()
                builder.addLocationRequest(mLocationRequest)
                mLocationSettingsRequest= builder.build()
            }

            //
            mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener {

                Log.d("mymsg", "enableGps: Already enable")
            }.addOnFailureListener {
                if((it as com.google.android.gms.common.api.ApiException).statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){

                    try {
                        val resolvableApiException= it as ResolvableApiException
                        resolvableApiException.startResolutionForResult(context as Activity,101)
                    }catch (e:Exception){
                        Log.d("mymsg", "enableGps: Unable to start")
                    }

                }
                else{

                    if((it as com.google.android.gms.common.api.ApiException).statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE){
                        Log.d("mymsg", "enableGps: An error occured")
                    }
                }
            }
        }

    }
}

