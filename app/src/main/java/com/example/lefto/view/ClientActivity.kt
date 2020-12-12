package com.example.lefto.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.lefto.R
import com.example.lefto.ViewModel.MapsActivityViewModel
import com.example.lefto.data.GoogleMapDTO
import com.example.lefto.model.LeftOverItem
import com.example.lefto.utils.GoogleMapsUtils
import com.example.lefto.utils.LocationUtils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.android.gms.maps.model.PolylineOptions


//GoogleMap.OnMarkerClickListener
class ClientActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val ACCESS_FINE_LOCATION_RQ = 500
        const val TAG = "Lefto-MapsActivity"
    }

    private lateinit var mMap: GoogleMap
    private var leftovers: MutableList<LeftOverItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val model = ViewModelProviders.of(this).get(MapsActivityViewModel::class.java)
//        val restaurants = model.getRestaurantList()

        leftovers = model.getLeftOverList()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun checkForPermissions(permission: String, name: String, requestCode: Int) {
        // In previous version the user is asked when he installed the app
        // so we don't ask at runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_LONG).show()

                    // Do what to need to do, permission granted so you can.
//                    registerCallReceiver()
                    locationWork()
                }
                // Explain the user why he should gives us the permission
                shouldShowRequestPermissionRationale(permission) -> showDialog(permission, name, requestCode)

                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_LONG).show()
            } else {
                // Already accepted.
                locationWork()
//                registerCallReceiver()
                Toast.makeText(applicationContext, "$name permission already granted", Toast.LENGTH_LONG).show()
            }
        }

        when (requestCode) {
            ACCESS_FINE_LOCATION_RQ -> innerCheck("access fine location")
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Permission to access your $name is required to use this app.")
            setTitle("Permission required")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(this@ClientActivity, arrayOf(permission), requestCode)
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Update your app to properly request permissions from the user when first started.
        checkForPermissions(Manifest.permission.ACCESS_FINE_LOCATION, "phone fine location", ACCESS_FINE_LOCATION_RQ)

        leftovers?.let {
            it.forEach { leftover ->
                val position = LatLng(leftover.restaurantItem.latitude, leftover.restaurantItem.longitude)

                mMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(leftover.restaurantItem.name)
                )
            }
        }

//        mMap.setOnMarkerClickListener(this)
    }


    private fun locationWork() {
        val mode = "walking"

        val googleMapsUtils = GoogleMapsUtils()

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        val delta = LatLng(58.385254, 26.725064)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delta, 12F))

        val helper = LocationUtils(applicationContext)
        val location = helper.getCurrenLocationUsingNetwork()

        Log.i(TAG, "try to find location")

        location?.apply {
            Log.i(TAG, location.latitude.toString())

            val latIng = LatLng(location.latitude, location.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(latIng)
                    .title("You are here")
            )

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latIng, 12F))

            val apiKey = resources.getString(R.string.google_maps_key)

            val url = googleMapsUtils.getDirectionURL(mode, latIng, delta, apiKey)
//            getDirection(url, delta)
        }
    }

    private fun getDirection(url: String, delta: LatLng) {
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()

            Log.d("mxmx" , " data from google map: $data")
            val result =  ArrayList<List<LatLng>>()

            val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
            val path =  ArrayList<LatLng>()

            val duration = respObj.routes[0].legs[0].duration.text
            val distance = respObj.routes[0].legs[0].distance.text

            runOnUiThread {
                mMap.addMarker(MarkerOptions().position(delta).title("Delta Centre").snippet("Travel time: $duration distance: $distance"))
            }

            for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                path.addAll(GoogleMapsUtils.decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
            }

            result.add(path)

            runOnUiThread {
                val polyline = PolylineOptions()
                for (i in result.indices){
                    polyline.addAll(result[i])
                    polyline.width(10f)
                    polyline.color(Color.BLUE)
                    polyline.geodesic(true)
                }

                mMap.addPolyline(polyline)
            }
        }
    }
}
