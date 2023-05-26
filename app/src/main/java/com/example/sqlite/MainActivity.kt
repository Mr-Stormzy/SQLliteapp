package com.example.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    lateinit var edtName:EditText
    lateinit var edtEmail:EditText
    lateinit var edtIDNumber:EditText
    lateinit var btnSave:Button
    lateinit var btnView:Button
    lateinit var btnDelete:Button
    lateinit var db:SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edtName = findViewById(R.id.edttxtname)
        edtEmail = findViewById(R.id.edttxtemail)
        edtIDNumber = findViewById(R.id.edttxtid)
        btnSave = findViewById(R.id.savebtn)
        btnView = findViewById(R.id.viewbtn)
        btnDelete = findViewById(R.id.deletebtn)

        //Create a database
        db = openOrCreateDatabase("emobilisdb",
            Context.MODE_PRIVATE, null)

        //Create a table inside the database
        db.execSQL("CREATE TABLE IF NOT EXISTS users(name VARCHAR, " +
                "email VARCHAR, id_number VARCHAR)")

        btnSave.setOnClickListener {
            var name = edtName.text.toString()
            var email = edtEmail.text.toString()
            var idnum = edtIDNumber.text.toString()

            //Check if the user is submitting empty fields
            if (name.isEmpty()) {
                edtName.setError("Please fill out this input")
                edtName.requestFocus()
            } else if (email.isEmpty()) {
                edtEmail.setError("Please fill this input")
                edtName.requestFocus()
            } else if (email.isEmpty()) {
                edtIDNumber.setError("Please fill this input")
                edtIDNumber.requestFocus()
            } else {
                //Proceed to save data
                db.execSQL("INSERT INTO users VALUES($name, $email, $idnum)")
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                edtName.setText(null)
                edtEmail.setText(null)
                edtIDNumber.setText(null)
            }
        }

        btnView.setOnClickListener {
            //Use cursor to select all the users
            var cursor = db.rawQuery("SELECT * FROM users", null)

            //Check if there's any record in the db
            if (cursor.count == 0){
                displayUsers("NO RECORDS", "Sorry, no data")
            }else{
                // Use a string buffer to append records from the db
                var buffer = StringBuffer()
                while (cursor.moveToNext()){
                    var retrievedName = cursor.getString(0)
                    var retrievedEmail = cursor.getString(1)
                    var retrievedIDNum = cursor.getString(2)
                    buffer.append(retrievedName+"\n")
                    buffer.append(retrievedEmail+"\n")
                    buffer.append(retrievedIDNum+"\n\n")
                }
                displayUsers("USERS", buffer.toString())
            }
        }
        btnDelete.setOnClickListener {
            //Recieve the ID of the user to be deleted
            var idNumber = edtIDNumber.text.toString()
            //Check if the ID no. recieved is empty
            if (idNumber.isEmpty()){
                edtIDNumber.setError("Please fill this input")
                edtIDNumber.requestFocus()
            }else{
                //Proceed to delete
                //Use cursor to select the user with the id
                var cursor = db.rawQuery(
                    "SELECT * FROM users WHERE id_number=$idNumber",
                    null)
                //Check if the user with the provided id exists
                if (cursor.count == 0){
                    displayUsers("NO USER","Sorry, no data")
                }else{
                    //Delete the user
                    db.execSQL("DELETE FROM users WHERE id_number=$idNumber")
                    displayUsers("SUCCESS", "User deleted!")
                    edtIDNumber.setText(null)
                }
            }
        }
    }
    fun displayUsers(title: String, message: String){
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("Close", null)
        alertDialog.create().show()
    }
}