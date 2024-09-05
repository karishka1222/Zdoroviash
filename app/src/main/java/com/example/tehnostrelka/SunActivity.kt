package com.example.tehnostrelka

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.tehnostrelka.databinding.ActivityMainBinding
import com.example.tehnostrelka.databinding.ActivitySunBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.util.*

class SunActivity : AppCompatActivity() {

    lateinit var btn1: ImageButton
    lateinit var btn2: ImageButton
    lateinit var pan_front: FrameLayout
    lateinit var pan_back: FrameLayout
    lateinit var left_btn: ImageButton
    lateinit var right_btn: ImageButton

    //weather

    private lateinit var binding: ActivitySunBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val city = intent.extras?.getString("city")
        val temp = intent.extras?.getString("temp")
        val con = intent.extras?.getString("cond")
        val adv = intent.extras?.getString("adv")
        binding.tempa.text = temp
        binding.adv.text = adv

        if (city == "Nizhny Novgorod" || city == "Nizhniy Novgorod") {
            binding.city.text = "Нижний Новгород"
        } else {
            binding.city.text = city
        }
        if (con == "Sunny"||con == "Clear") {
            binding.prognos.text = "Ясно"
        } else if (con == "Cloudy" || con == "Overcast" || con == "Mainly cloudy" || con == "Partly cloudy") {
            binding.prognos.text  = "Пасмурно"
        } else if (con == "Rain" || con == "Light drizzle" || con == "Heathy rain") {
            binding.prognos.text = "Осадки"
        }
        val eq1 = findViewById<TextView>(R.id.eq)
        db.collection("Бадминтон").document("Ясно")
            .get().addOnSuccessListener {
                if (it != null) {
                    var eq = ""
                    for(i in 1..6){
                        if(it.data?.get("$i").toString()!=""){
                            eq+="●"+"  "+it.data?.get("$i").toString()+"\n"+"\n"
                        }
                    }
                    eq1.text =eq
                }}




        pan_front = binding.front //findViewById(R.id.front)
        pan_back = binding.back //findViewById(R.id.back)
        btn1 = findViewById(R.id.btn1)
        btn1.setOnClickListener {
            flipCard(this, pan_back, pan_front)
            btn1.visibility = View.INVISIBLE
            btn2.visibility = View.VISIBLE
        }

        btn2 = findViewById(R.id.btn2)
        btn2.setOnClickListener {
            flipCard(this, pan_front, pan_back)
            btn2.visibility = View.INVISIBLE
            btn1.visibility = View.VISIBLE
        }

        left_btn = findViewById(R.id.arr_left)
        left_btn.setOnClickListener{
            val intent = Intent(this, LizhiSunActivity::class.java)
            db.collection("Советы").document("Лыжи").get().addOnSuccessListener {
                if(it != null){
                    val adv = it.data?.get("Ясно").toString()
                    intent.putExtra("city",city)
                    intent.putExtra("temp",temp)
                    intent.putExtra("cond",con)
                    intent.putExtra("adv",adv)
                    startActivity(intent)
                }
            }
        }

        right_btn = findViewById(R.id.arr_right)
        right_btn.setOnClickListener{
            val intent = Intent(this, LizhiSunActivity::class.java)
            db.collection("Советы").document("Лыжи").get().addOnSuccessListener {
                if(it != null){
                    val adv = it.data?.get("Ясно").toString()
                    intent.putExtra("city",city)
                    intent.putExtra("temp",temp)
                    intent.putExtra("cond",con)
                    intent.putExtra("adv",adv)
                    startActivity(intent)
                }
        }
        }

    }

    fun flipCard(context: Context, visibleView: View, inVisibleView: View) {
        try {
            visibleView.visibility
            val scale = context.resources.displayMetrics.density
            val cameraDist = 8000 * scale
            visibleView.cameraDistance = cameraDist
            inVisibleView.cameraDistance = cameraDist
            val flipOutAnimatorSet =
                AnimatorInflater.loadAnimator(
                    context,
                    R.animator.flip_out
                ) as AnimatorSet
            flipOutAnimatorSet.setTarget(inVisibleView)
            val flipInAnimationSet =
                AnimatorInflater.loadAnimator(
                    context,
                    R.animator.flip_in
                ) as AnimatorSet
            flipInAnimationSet.setTarget(visibleView)
            flipOutAnimatorSet.start()
            flipInAnimationSet.start()
            flipOutAnimatorSet.doOnEnd {
                inVisibleView.visibility
            }
        } catch (e: Exception) {
            //LogHandledException(e)
        }
    }


}