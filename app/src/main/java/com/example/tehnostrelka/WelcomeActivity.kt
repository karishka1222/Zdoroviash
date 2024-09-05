package com.example.tehnostrelka

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tehnostrelka.databinding.ActivityMainBinding
import com.example.tehnostrelka.databinding.ActivityWelcomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

const val API_KEY = "7798dd7ba9fa4a03916211627231804"
class WelcomeActivity : AppCompatActivity() {

    private lateinit var timer: CountDownTimer


    //weather
    private lateinit var fLocationClient: FusedLocationProviderClient
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: ActivityWelcomeBinding

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkPermission()
        var txt = binding.text
        var cnt: Int = 0
        timer = object : CountDownTimer(10_000, 200) {
            override fun onTick(remaining: Long) {
                if (cnt == 0) {
                    txt.text = "П"
                } else if (cnt == 1) {
                    txt.text = "Пр"
                } else if (cnt == 2) {
                    txt.text = "При"
                } else if (cnt == 3) {
                    txt.text = "Прив"
                } else if (cnt == 4) {
                    txt.text = "Приве"
                } else if (cnt == 5) {
                    txt.text = "Привет"
                } else if (cnt == 6) {
                    txt.text = "Привет,"
                } else if (cnt == 7) {
                    txt.text = "Привет, я"
                } else if (cnt == 8) {
                    txt.text = "Привет, я З"
                } else if (cnt == 9) {
                    txt.text = "Привет, я Зд"
                } else if (cnt == 10) {
                    txt.text = "Привет, я Здо"
                } else if (cnt == 11) {
                    txt.text = "Привет, я Здор"
                } else if (cnt == 12) {
                    txt.text = "Привет, я Здоро"
                } else if (cnt == 13) {
                    txt.text = "Привет, я Здоров"
                } else if (cnt == 14) {
                    txt.text = "Привет, я Здоровя"
                } else if (cnt == 15) {
                    txt.text = "Привет, я Здоровяш"
                } else {
                    txt.text = "Привет, я Здоровяш!"

                }
                cnt++
            }

            override fun onFinish() {
                txt.text = "Привет, я Здоровяш!"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        timer.start()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    private fun getLocation(){
        val ct = CancellationTokenSource()
        if(ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )!= PackageManager.PERMISSION_GRANTED
        ){
            return
        }
        fLocationClient
            .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,ct.token)
            .addOnCompleteListener {
                getResult("${it.result.latitude},${it.result.longitude}")
            }
    }

    private fun permissionListener(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
            if(it==true){
                getLocation()
            }
        }
    }

    fun isPermitionsGranted(p:String): Boolean{
        return ContextCompat.checkSelfPermission(
            this,p) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermission(){
        if(!isPermitionsGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            getLocation()
        }
    }

    private fun getResult(name:String){
        val url = "https://api.weatherapi.com/v1/current.json"+
                "?key=$API_KEY&q=$name&aqi=no"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,{response->
                val obj = JSONObject(response)
                Log.d("myLog","$obj")
                TOfill(obj)
            },
            {

            }
        )
        queue.add(stringRequest)
    }

    private fun TOfill(it: JSONObject) {
        val tvTemp = it.getJSONObject("current").getString("temp_c").toFloat().toInt().toString()
        val tvCity = it.getJSONObject("location").getString("name")
        val tvcon = it.getJSONObject("current").getJSONObject("condition").getString("text")
//        if (tvCity == "Nizhny Novgorod" || tvCity == "Nizhniy Novgorod") {
//            val city = "Нижний Новгород"
//        } else {
//            val city = tvCity
//        }
//        if (tvcon == "Sunny") {
//            val con = "Ясно"
//        } else if (tvcon == "cloudy" || tvcon == "overcast" || tvcon == "Mainly cloudy" || tvcon == "Partly cloudy") {
//            val con = "Пасмурно"
//        } else if (tvcon == "Rain" || tvcon == "Light drizzle" || tvcon == "Heathy rain") {
//            val con = "Осадки"
//        }
//        val weather = mapOf(
//            "city" to tvCity,
//            "temp" to tvTemp,
//            "cond" to tvcon
//        )
        if (tvcon == "Sunny"||tvcon == "Clear") {
            db.collection("Советы").document("Бадминтон").get().addOnSuccessListener {
                if(it != null){
                    val adv = it.data?.get("Ясно").toString()
                    val intent = Intent(this, SunActivity::class.java)
                    intent.putExtra("city",tvCity)
                    intent.putExtra("temp",tvTemp)
                    intent.putExtra("cond",tvcon)
                    intent.putExtra("adv",adv)
                    startActivity(intent)
                }
            }

        } else if (tvcon == "Cloudy" || tvcon == "Overcast" || tvcon == "Mainly cloudy" || tvcon == "Partly cloudy") {
            db.collection("Советы").document("Бадминтон").get().addOnSuccessListener {
                if(it != null){
                    val adv = it.data?.get("Пасмурно").toString()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("city",tvCity)
                    intent.putExtra("temp",tvTemp)
                    intent.putExtra("cond",tvcon)
                    intent.putExtra("adv",adv)
                    startActivity(intent)
                }
            }
        } else if (tvcon == "Rain" || tvcon == "Light drizzle" || tvcon == "Heathy rain") {
            db.collection("Советы").document("Бадминтон").get().addOnSuccessListener {
                if(it != null){
                    val adv = it.data?.get("Осадки").toString()
                    val intent = Intent(this, RainActivity::class.java)
                    intent.putExtra("city",tvCity)
                    intent.putExtra("temp",tvTemp)
                    intent.putExtra("cond",tvcon)
                    intent.putExtra("adv",adv)
                    startActivity(intent)
                }
            }
        }

    }



}