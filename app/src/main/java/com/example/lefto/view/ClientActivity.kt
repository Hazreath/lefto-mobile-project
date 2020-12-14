package com.example.lefto.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.lefto.R
import com.example.lefto.ViewModel.ClientActivityViewModel
import com.example.lefto.data.GoogleMapDTO
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.example.lefto.utils.GeneralUtils
import com.example.lefto.utils.GoogleMapsUtils
import com.example.lefto.utils.LocationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_restaurant.*
import kotlinx.android.synthetic.main.activity_restaurant.btn_disconnect
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability


//GoogleMap.OnMarkerClickListener
class ClientActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var PREFERENCES : SharedPreferences


    companion object {
        const val ACCESS_FINE_LOCATION_RQ = 500
        const val TAG = "Lefto-MapsActivity"
        lateinit var DAO : FirebaseUtils
        var restauFetched = false
    }

    private lateinit var mMap: GoogleMap
    private var restaurants: ArrayList<RestaurantItem>? = null

    // Second click on marker gestion
    var lastClickedMarkerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DAO = FirebaseUtils(this, Firebase.firestore)
        val model = ViewModelProviders.of(this).get(ClientActivityViewModel::class.java)

        setContentView(R.layout.activity_maps)

        PREFERENCES = getSharedPreferences(
            getString(R.string.pref_filename),
            Context.MODE_PRIVATE
        )
        // UNCOMMENT TO GET THE FIREBASE CLOUD MESSAGING TOKEN, DO NOT USE IT IN PRODUCTION
//        getFbMessagingToken()
//        checkGooglePlayServices()

        btn_disconnect.setOnClickListener {
            restauFetched = false
            val intent = Intent(this, LoginActivity::class.java);
            startActivity(intent)
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java);
            startActivity(intent)
        }
        btn_reload.setOnClickListener {

            reloadMap()
        }
        GlobalScope.launch {
            suspend {
                restaurants = model.getRestaurantList()
                while(!restauFetched); // TODO better if i have some time
                displayMap()
            }.invoke()
        }
    }

    // DEBUG : Google play services not working ? (FIXED)
    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error with google play services")
            false
        } else {
            // 3
            Log.i(TAG, "Google play services OK")
            true
        }
    }

    private fun getFbMessagingToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                // 2
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // 3
                val token = task.result?.token

                // 4
                val msg = "my token: " + token
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
            })
    }
    private fun reloadMap() {
        mMap.clear()
        locationWork()
        onMapReady(mMap)
    }

    private fun displayMap() {
        runOnUiThread {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)

        }
    }

    private fun checkForPermissions(permission: String, name: String, requestCode: Int) {
        // In previous version the user is asked when he installed the app
        // so we don't ask at runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                    locationWork()
                }
                // Explain the user why he should gives us the permission
                shouldShowRequestPermissionRationale(permission) -> showDialog(
                    permission,
                    name,
                    requestCode
                )

                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        moveTaskToBack(false)
    }

    override fun onResume() {
        super.onResume()
        PREFERENCES = getSharedPreferences(
            getString(R.string.pref_filename),
            Context.MODE_PRIVATE
        )
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
                Toast.makeText(
                    applicationContext,
                    "$name permission already granted",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        when (requestCode) {
            ACCESS_FINE_LOCATION_RQ -> innerCheck("access fine location")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SettingsActivity.SETTINGS_REQUEST_CODE) {
            // Reload the map to take new preferences in consideration
            Log.d(TAG,"SETTINGS UPDATED")
            this.recreate()
        }
    }
    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Permission to access your $name is required to use this app.")
            setTitle("Permission required")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(
                    this@ClientActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
        }

        val dialog = builder.create()
        dialog.show()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//        Log.d(TAG,"onMapReady")
//        Log.d(TAG,"$restaurants")
        // Update your app to properly request permissions from the user when first started.
        checkForPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            "phone fine location",
            ACCESS_FINE_LOCATION_RQ
        )

        restaurants?.let {
            it.forEach { r ->
                val position = LatLng(
                    r.latitude,
                    r.longitude
                )

                // Custom color : vegan -> green, halal -> red
                // regular : yellow
                var snippet = ""
                var color = BitmapDescriptorFactory.HUE_YELLOW
                var prefOnlyHalal = PREFERENCES.getBoolean("onlyHalal",false)
                var prefOnlyVegan = PREFERENCES.getBoolean("onlyVegan",false)
                var displayRestaurant = (r.halal && prefOnlyHalal) || (r.vegan && prefOnlyVegan) ||
                        (!prefOnlyHalal && !prefOnlyVegan)
                // filter by preferences
                if (displayRestaurant) {
                    if (r.halal && r.vegan) {
                        color = BitmapDescriptorFactory.HUE_VIOLET
                        snippet += "(halal,vegan)"
                    } else if (r.halal) {
                        color = BitmapDescriptorFactory.HUE_RED
                        snippet += "(halal)"
                    } else if (r.vegan) {
                        color = BitmapDescriptorFactory.HUE_GREEN
                        snippet += "(vegan)"
                    }

                    mMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(r.name)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(color))
                    ).tag = r.id

                }
            }

            mMap.setOnMarkerClickListener( GoogleMap.OnMarkerClickListener() {
                var marker = it
                onClickMarker(marker)
            })

            // Reset second click feature when click map
            mMap.setOnMapClickListener {
                lastClickedMarkerId = ""
            }
        }
    }

    private fun onClickMarker(marker : Marker) : Boolean {
        val id = marker.tag.toString()

        if (id == "user") {
            lastClickedMarkerId = ""
            return false
        }

        // First click : show help toast & title
        if ( id != lastClickedMarkerId) {
            lastClickedMarkerId = id
            marker.showInfoWindow()
            GeneralUtils.showToast(this, "Click again to show available leftovers")
        } else {

            lastClickedMarkerId = ""

            // Forward intent to SeeLeftoversActivity along with restaurant ID
            val intent = Intent(this, SeeLeftoversActivity::class.java)
            intent.putExtra("restaurant_id", id)
            intent.putExtra("restaurant_name", marker.title.toString())
            startActivity(intent)
        }

        return marker.tag.toString() == lastClickedMarkerId
    }

    private fun locationWork() {
        val mode = "walking"

        val googleMapsUtils = GoogleMapsUtils()

        val delta = LatLng(58.385254, 26.725064)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delta, 12F))

        val helper = LocationUtils(applicationContext)

        var prefWIFILocation = PREFERENCES.getBoolean("WIFILocation",false)
//        Log.d(TAG,"LOCATION WIFI : $prefWIFILocation")
        var location : Location?
        if (prefWIFILocation) {
            location = helper.getCurrentLocationUsingNetwork()
        } else {
            location = helper.getCurrentLocationUsingGPS()
        }


        Log.i(TAG, "try to find location $location")

        location?.apply {
            Log.i(TAG, location.latitude.toString())

            val latIng = LatLng(location.latitude, location.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))

                    .position(latIng)
                    .title("You are here")
            ).tag = "user"

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

            Log.d("mxmx", " data from google map: $data")
            val result =  ArrayList<List<LatLng>>()

            val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
            val path =  ArrayList<LatLng>()

            val duration = respObj.routes[0].legs[0].duration.text
            val distance = respObj.routes[0].legs[0].distance.text

            runOnUiThread {
                mMap.addMarker(
                    MarkerOptions().position(delta).title("Delta Centre")
                        .snippet("Travel time: $duration distance: $distance")
                )
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
