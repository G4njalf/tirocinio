package com.example.myapplication.ui.contract_creation

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentContractCreationBinding
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.Marker


class ContractCreationFragment : Fragment() {

    private var _binding: FragmentContractCreationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var lat = 0.0
    private var lng = 0.0


    @SuppressLint("ClickableViewAccessibility")
    private fun mapSetup(){
        Configuration.getInstance().load(
            context,
            requireContext().getSharedPreferences("default_prefs",0)
        )

        val map = binding.mapView
        map.setMultiTouchControls(true)

        var marker: Marker? = null
        var selectedLocation: GeoPoint? = null

        val mapController = map.controller
        mapController.setZoom(15.0)
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        locationOverlay.enableMyLocation()
        locationOverlay.runOnFirstFix {
            activity?.runOnUiThread {
                val myLocation = locationOverlay.myLocation
                Log.d("ContractCreation",myLocation.toString()+ "asdasdsa")
                if (myLocation != null) {
                    val startPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
                    lat = myLocation.latitude
                    lng = myLocation.longitude
                    mapController.setCenter(startPoint)

                    marker = Marker(map)
                    marker!!.position = startPoint
                    marker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker!!.title = "Posizione attuale"
                    map.overlays.add(marker)
                    map.invalidate()

                    selectedLocation = startPoint
                }
            }
        }
        map.overlays.add(locationOverlay)

        // Gestione click sulla mappa
        val eventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                marker?.let {
                    it.position = p
                    it.title = "Nuova posizione"
                    map.invalidate()
                } ?: run {
                    marker = Marker(map).apply {
                        position = p
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Nuova posizione"
                    }
                    map.overlays.add(marker)
                    map.invalidate()
                }
                lat = p.latitude
                lng = p.longitude
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                TODO("Not yet implemented")
            }
        }
        val mapEventsOverlay = MapEventsOverlay(eventsReceiver)
        map.overlays.add(mapEventsOverlay)
    }


    private fun hasLocationPermission(): Boolean {
        return androidx.core.app.ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            mapSetup()
        } else {
            Toast.makeText(requireContext(), "Permesso posizione negato", Toast.LENGTH_SHORT).show()
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val contractCreationViewModel =
            ViewModelProvider(this)[ContractCreationViewModel::class.java]

        _binding = FragmentContractCreationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!hasLocationPermission()) {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        else{
            mapSetup()
        }

        binding.progressBarCreateContract.visibility = View.GONE

        contractCreationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarCreateContract.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        contractCreationViewModel.contractCreated.observe(viewLifecycleOwner) {
            contractCreated -> if (contractCreated) Toast.makeText(context,"Contract created successfully!",Toast.LENGTH_LONG).show()
        }

        binding.buttonCreateContract.setOnClickListener {
            val insuredAddress = binding.editTextEnsurer.text.toString()
            val premio = binding.editTextPremio.text.toString().toUIntOrNull() ?: 0u
            val topic = if(binding.radioHum.isChecked){
                "Humidity"
            }
            else{
                "Temperature"
            }
            val latInt = (lat * 10000).toUInt()
            val lngInt = (lng * 10000).toUInt()
            contractCreationViewModel.createContract(insuredAddress,premio,topic,latInt,lngInt)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}