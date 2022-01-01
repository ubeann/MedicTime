package com.medictime.ui.headquarter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.medictime.R
import com.medictime.databinding.ActivityHeadquarterBinding

class HeadquarterActivity : AppCompatActivity() {
    private var _binding: ActivityHeadquarterBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHeadquarterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            val location = LatLng(-7.268843, 112.783621)
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Universitas Airlangga - Kampus C")
            )
            mMap.setMinZoomPreference(16.0f)
            mMap.setMaxZoomPreference(20.0f)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}