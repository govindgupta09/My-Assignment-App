package com.govi.myassignmentapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.govi.myassignmentapp.R
import com.govi.myassignmentapp.databinding.ActivityDescriptionBinding
import com.govi.myassignmentapp.models.Product
import java.util.Timer
import java.util.TimerTask

class DescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDescriptionBinding
    private lateinit var context: Context
    private lateinit var product: Product

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = applicationContext

        product = intent.getSerializableExtra("Product") as Product

        setupActionBar()

        binding.tvDescr.text = product.title
        binding.descrBrand.text = product.brand
        binding.descrCategory.text = "("+product.category+")"
        binding.descrDiscountPercentage.text = product.discountPercentage.toString()+"% off"
        binding.descrPrice.text = "$"+product.price.toString()+" USD"

        binding.descrStock.text = "Only "+product.stock.toString()+" items available"
        binding.descrStock.setTextColor(ContextCompat.getColor(context, R.color.stockTextColor))

        binding.tvDescr.text = product.description

        binding.descrRatings.text = product.rating.toString()+" ratings"
        binding.descrRatings.setTextColor(ContextCompat.getColor(context, R.color.ratingTextColor))

        binding.descrRatingBar.rating = product.rating.toFloat()

        // Set the rating change listener (optional, remove if not needed)
        binding.descrRatingBar.setOnRatingBarChangeListener { _, _, _ ->
        }

        val timer = Timer()
        // Schedule the task to run every second
        var i=0
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread(Runnable {
                    if(i<product.images.size){
                        Glide.with(context).load(product.images.get(i)).into(binding.ivDescr)
                        i++
                    }else{
                        i=0
                    }
                })
            }
        }, 0, 1000)

    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarDescriptionActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
            actionBar.title = product.title
        }

        binding.toolbarDescriptionActivity.setNavigationOnClickListener { onBackPressed() }
    }
}