package com.lunayoung.mapsee

import android.content.Context
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.lunayoung.mapsee.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.view_custom_info_window.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationSource, BottomSheet.BottomSheetListener {

    val TAG = MainActivity::class.java.simpleName
    lateinit var naverMap : NaverMap
    lateinit var locationOverlay : LocationOverlay
    private var locationSource : FusedLocationSource? = null
    val bottomSheet = BottomSheet()
    var caseNumber:String = ""


    private class InfoWindowAdapter(private val context: Context) : InfoWindow.ViewAdapter() {
        private var rootView: View? = null
        private var icon: ImageView? = null
        private var text: TextView? = null
        private var et_caseNumber: EditText? = null

        override fun getView(infoWindow: InfoWindow): View {
            val view = rootView?:View.inflate(context, R.layout.view_custom_info_window, null).also{rootView = it}
            val icon = icon ?: view.findViewById<ImageView>(R.id.icon).also { icon = it }
            val text = text ?: view.findViewById<TextView>(R.id.text).also { text = it }
            val et_caseNumber = et_caseNumber ?: view.findViewById<EditText>(R.id.et_caseNumber).also{et_caseNumber = it}

            val marker = infoWindow.marker
            if(marker != null) {
                icon.setImageResource(R.drawable.ic_place_black_24dp)
                text.text = marker.tag as String? //이거 뭐지
            }
            else {
                icon.setImageResource(R.drawable.ic_my_location_black_24dp)
                text.text = context.getString(
                    R.string.format_coord, infoWindow.position.latitude, infoWindow.position.longitude)
            }
            return view
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager = supportFragmentManager
        val mapFragment = fragmentManager.findFragmentById(R.id.main_map) as MapFragment ?
                ?: MapFragment.newInstance().also {
                    fragmentManager.beginTransaction().add(R.id.main_map ,it).commit()
        }

        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
/*
        fab.setOnClickListener{
            val bottomSheet = BottomSheet()
            bottomSheet.show(supportFragmentManager, "BottomSheet")
        }*/

    }


    override fun onSaveButtonClicked() {
        Log.i(TAG, "제목: "+bottomSheet.etTitle.text)
        Log.i(TAG, "내용: "+bottomSheet.etDescription.text)

        //todo
        /*
        * 저장하고 나면
        * 해당 내용을 마커 윈도에 뿌려준다
        *
        * */
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        //location tracking
        naverMap.locationTrackingMode = LocationTrackingMode.Follow


        //사용자 인터페이스
        val uiSettings = naverMap.uiSettings
        uiSettings.isCompassEnabled = true
        uiSettings.isLocationButtonEnabled = true

        locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        locationOverlay.position = LatLng(37.338215, 127.102746)

        handleLongClickEvent(naverMap)
    }

    fun handleLongClickEvent(naverMap: NaverMap){

        val infoWindow = InfoWindow().apply {
            anchor = PointF(0f, 1f)
            offsetX = resources.getDimensionPixelSize(R.dimen.custom_info_window_offset_x)
            offsetY = resources.getDimensionPixelSize(R.dimen.custom_info_window_offset_y)
            adapter = InfoWindowAdapter(this@MainActivity)
            setOnClickListener {
                close()
                bottomSheet.dismiss()
                true
            }
        }

        //위치 롱클릭 시 마커 생성
        naverMap.setOnMapLongClickListener { _, coord ->
            Marker().apply {
                position = LatLng(coord.latitude, coord.longitude)

                setOnClickListener {
                    //infoWindow.open(this, Align.Center)
                    bottomSheet.show(supportFragmentManager, "BTS")
                    true
                }
                map = naverMap

                /*
                * 마커 클릭 시
                * 1) 저장된 정보가 있다 -> 보여준다
                * 2) 저장된 정보가 없다 -> 입력한다
                * => 바텀시트 2개 필요
                * 일단은 입력하는 거 1개만 만든다
                * */
            }
        }
    }


    override fun activate(p0: LocationSource.OnLocationChangedListener) {

    }

    override fun deactivate() {

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onDestroy() {
        super.onDestroy()
        locationSource = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

}
