package com.example.expenso

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Expense : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var etAmount: EditText
    private lateinit var etTitle: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLocation: Button
    private lateinit var dbRef: DatabaseReference
    private lateinit var selectedCategory: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)
        etAmount=findViewById(R.id.amountE)
        etTitle=findViewById(R.id.titleE)
        btnSave=findViewById(R.id.saveButtonE)
        btnLocation=findViewById(R.id.locationButton)

        dbRef= FirebaseDatabase.getInstance().getReference("Income")
        btnSave.setOnClickListener {
            saveIncome()
        }
        btnLocation.setOnClickListener{
            getCurrentLocation()
        }

        val categories = listOf("Select Category", "Food & Dining", "Transportation", "Health & Fitness", "Shopping", "Other")
        val spinner = findViewById<Spinner>(R.id.categorySpinner)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
    private fun getCurrentLocation() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    Toast.makeText(this, "Location fetched successfully: Lat: $latitude, Long: $longitude", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"Not able to fetch the location",Toast.LENGTH_LONG).show()
                }
            }).addOnFailureListener { err ->
            // Show Toast for failure in fetching location
            Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_LONG).show()
        }
    }
    private fun saveIncome(){
        val amount=etAmount.text.toString()
        val title=etTitle.text.toString()
        if(amount.isEmpty()){
            etAmount.error="Please Enter Amount"
        }
        if(title.isEmpty()){
            etTitle.error="Please Enter Title"
        }
        if (selectedCategory == "Select Category") {
            Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionID = dbRef.push().key!!
        val transaction = InsertionModel(amount,title,selectedCategory,latitude,longitude) //object of data class

        dbRef.child(transactionID).setValue(transaction)
            .addOnCompleteListener {
                Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }
}