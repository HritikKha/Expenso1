package com.example.expenso


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val user = Firebase.auth.currentUser
    private lateinit var expRecyclerView: RecyclerView
    private lateinit var expList: ArrayList<InsertionModel>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvIncome = findViewById<TextView>(R.id.tv1)
        val tvExpense = findViewById<TextView>(R.id.tv2)

        // Initialize RecyclerView
        expRecyclerView = findViewById(R.id.rvExp)
        expRecyclerView.layoutManager = LinearLayoutManager(this)
        expRecyclerView.setHasFixedSize(true)

        // Initialize list and adapter
        expList = arrayListOf<InsertionModel>()


        // On-click listeners
        tvIncome.setOnClickListener {
            startActivity(Intent(this, Insertion::class.java))
        }
        tvExpense.setOnClickListener {
            startActivity(Intent(this, Expense::class.java))
        }

        showUserName()
        getEmployeesData()
    }

    private fun showUserName() {
        user?.reload()
        val tvUserName: TextView =findViewById(R.id.tvUserName)
        val email = user!!.email
        val userName = user.displayName


        val name = if (userName == null || userName == ""){
            val splitValue = email?.split("@")
            splitValue?.get(0).toString()
        }else{
            userName
        }

        tvUserName.text = "Hello, ${name}!"
    }

    private fun getEmployeesData() {
        dbRef = FirebaseDatabase.getInstance().getReference("Income")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expList.clear()  // Clear the list
                if (snapshot.exists()) {
                    for (postSnap in snapshot.children) {
                        val curData = postSnap.getValue(InsertionModel::class.java)
                        curData?.key = postSnap.key // Assign the Firebase key to the model
                        expList.add(curData!!)
                    }

                    val mAdapter = expAdapter(expList)
                    expRecyclerView.adapter = mAdapter
                    mAdapter.notifyDataSetChanged()

                    mAdapter.setOnItemLongClickListener(object : expAdapter.onItemLongClickListener {
                        @SuppressLint("SuspiciousIndentation")
                        override fun onItemLongClick(position: Int) {
                            val dialogView = layoutInflater.inflate(R.layout.dialog_edit, null)

                            val selectedItem = expList[position]

                            // Find the EditText views in the dialog
                            val editAmount = dialogView.findViewById<EditText>(R.id.editAmount)
                            val editTitle = dialogView.findViewById<EditText>(R.id.editTitle)

                            editAmount.setText(selectedItem.amount.toString())
                            editTitle.setText(selectedItem.title)


                            val builder = AlertDialog.Builder(this@MainActivity)
                                .setTitle("Edit Item")
                                .setView(dialogView)
                                .setCancelable(false)

                            builder.setPositiveButton("Save") { dialog, _ ->
                                // Retrieve the edited data from the input fields
                                val updatedAmount = editAmount.text.toString()
                                val updatedTitle = editTitle.text.toString()
                                val updatedItem = selectedItem.copy(amount = updatedAmount, title = updatedTitle)

                                // Get the key for this item from the current selectedItem
                                val keyToUpdate = selectedItem.key
                                if (keyToUpdate != null) {
                                    val dbRef = FirebaseDatabase.getInstance().getReference("Income").child(keyToUpdate)

                                    // Update the item in Firebase
                                    dbRef.setValue(updatedItem)
                                        .addOnSuccessListener {
                                            // Update the local list and notify the adapter
                                            expList[position] = updatedItem
                                            mAdapter.notifyItemChanged(position)

                                            Toast.makeText(this@MainActivity, "Successfully Updated", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { error ->
                                            Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }

                                dialog.dismiss()
                            }

                            builder.setNegativeButton("Delete") { dialog, _ ->
                                val selectedItem = expList[position]
                                val keyToDelete = selectedItem.key // Get the key of the selected item

                                if (keyToDelete != null) {
                                    val dbRef = FirebaseDatabase.getInstance().getReference("Income").child(keyToDelete)
                                    dbRef.removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(this@MainActivity, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { error ->
                                            Toast.makeText(this@MainActivity, "Deleting Error ${error.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                dialog.dismiss()
                            }

                            builder.show()
                        }


                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
