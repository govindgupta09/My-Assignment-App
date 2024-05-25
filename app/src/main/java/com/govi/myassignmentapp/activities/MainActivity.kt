package com.govi.myassignmentapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.govi.myassignmentapp.R
import com.govi.myassignmentapp.adapter.MyAdapter
import com.govi.myassignmentapp.api.APIService
import com.govi.myassignmentapp.databinding.ActivityMainBinding
import com.govi.myassignmentapp.firebase.FirestoreClass
import com.govi.myassignmentapp.models.MyData
import com.govi.myassignmentapp.models.Product
import com.govi.myassignmentapp.models.User
import io.supercharge.shimmerlayout.ShimmerLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mUserName: String
    lateinit var shimmerLayout: ShimmerLayout
    private lateinit var dataList: ArrayList<Product>
    private lateinit var myAdapter: MyAdapter
    lateinit var recyclerView: RecyclerView

    /**
     * A companion object to declare the constants.
     */
    companion object {
        //A unique code for starting the activity for result
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shimmerLayout = findViewById(R.id.shimmer_layout)
        recyclerView = findViewById(R.id.recyclerView)

        setupActionBar()

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        binding.navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this@MainActivity,true)

        /**
         * Here, implementation of shimmer effect.
         */
        shimmerLayout.startShimmerAnimation()

        /**
         * Initialize the dataList
         */
        dataList = ArrayList()

        /**
         * Here setting up recycler view in android layout.
         */
        recyclerView.layoutManager = LinearLayoutManager(this)

        /**
         * Here, set up our adapter with an empty list of data.
         */
        myAdapter = MyAdapter(this, dataList){}

        /**
         *Below code is responsible for managing the data and creating views for the items in the RecyclerView.
         */
        recyclerView.adapter = myAdapter

        /**
         * Calling getMyData() to fetch data from API
         */
        getMyData()

    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_main_activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar.setTitle("Items")

        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {

                startActivityForResult(
                    Intent(this@MainActivity, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            FirestoreClass().loadUserData(this@MainActivity)
        }else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    /**
     * A function to get the current user details from firebase.
     */
    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {

        hideProgressDialog()

        mUserName = user.name

        // The instance of the header view of the navigation view.
        val headerView = binding.navView.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)

        // Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(navUserImage) // the view in which the image will be loaded.

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        navUsername.text = user.name
    }

    /**
     * Here, making a network request to fetch data from a provided API
     */
    private fun getMyData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)

        val retrofitData = retrofit.fetchData()

        retrofitData.enqueue(object: Callback<MyData> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<MyData>, response: Response<MyData>) {
                val responseBody = response.body()
                Log.d("MyResponse",response.body().toString());
                Log.d("MyResponse",response.toString());
                if(responseBody != null){
                    dataList.addAll(responseBody.products)
                    myAdapter.notifyDataSetChanged()
                    shimmerLayout.isInvisible = true
                    recyclerView.isVisible = true
                    Log.d("MyResponse","Data successfully fetched inside responseBody");
                }
            }

            override fun onFailure(call: Call<MyData>, t: Throwable) {
                Log.d("MyResponse","onFailure"+t.message)
                Toast.makeText(this@MainActivity,"onFailure"+t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

}