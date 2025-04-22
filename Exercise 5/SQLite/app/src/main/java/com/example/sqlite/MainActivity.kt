package com.example.sqlite

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view. View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var srollno: EditText
    private lateinit var sname: EditText
    private lateinit var scgpa: EditText
    private lateinit var insert: Button
    private lateinit var update: Button
    private lateinit var delete: Button
    private lateinit var view: Button
    private lateinit var viewAll: Button

    private lateinit var db: SQLiteDatabase
    private lateinit var c: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        srollno = findViewById(R.id.editRollno)
        sname = findViewById(R.id.editName)
        scgpa = findViewById(R.id.editCGPA)
        insert = findViewById(R.id.btnInsert)
        update = findViewById(R.id.btnUpdate)
        delete = findViewById(R.id.btnDelete)
        view = findViewById(R.id.btnView)
        viewAll = findViewById(R.id.btnViewAll)

        db = openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS student(rollno VARCHAR,name VARCHAR, marks VARCHAR);")
    }

    override fun onClick(v: View?) {
        if (v == insert) {
            Toast.makeText(this@MainActivity, "Welcome", Toast.LENGTH_LONG).show()

            if (srollno.text.toString().isEmpty() || sname.text.toString().isEmpty()) {
                showMessage("Error", "Please enter all values")
                return
            }
            db.execSQL("INSERT INTO student VALUES('" + srollno.text.toString() + "','" + sname.text.toString() + "','" + scgpa.text.toString() + "')")
            showMessage("Success", "Record added")
            clearText()
        }
        if (v == update) {
            if (srollno.text.toString().isEmpty()) {
                showMessage("Error", "Please enter roll no.")
                return
            }
            this.c = db.rawQuery(
                "SELECT * FROM student WHERE rollno='" + srollno.text.toString() + "'",
                null
            )
            if (this.c.moveToFirst()) {
                db.execSQL("UPDATE student SET name='" + sname.text.toString() + "', marks='" + scgpa.text.toString() + "' WHERE rollno='" + srollno.text.toString() + "'")
                showMessage("Success", "Record updated")
            } else {
                showMessage("Error", "Invalid roll no.")
            }
            clearText()
            this.c.close()
        }
        if (v == delete) {
            if (srollno.text.toString().isEmpty()) {
                showMessage("Error", "Please enter roll no.")
                return
            }
            this.c = db.rawQuery(
                "SELECT * FROM student WHERE rollno='" + srollno.text.toString() + "'",
                null
            )
            if (this.c.moveToFirst()) {
                db.execSQL("DELETE FROM student WHERE rollno='" + srollno.text.toString() + "'")
                showMessage("Success", "Record deleted")
            } else {
                showMessage("Error", "Invalid roll no.")
            }
            clearText()
            this.c.close()
        }
        if (v == view) {
            if (srollno.text.toString().isEmpty()) {
                showMessage("Error", "Please enter roll no.")
                return
            }
            this.c = db.rawQuery(
                "SELECT * FROM student WHERE rollno='" + srollno.text.toString() + "'",
                null
            )
            if (this.c.moveToFirst()) {
                sname.setText(this.c.getString(1))
                scgpa.setText(this.c.getString(2))
            } else {
                showMessage("Error", "Invalid roll no.")
            }
            this.c.close()
        }
        if (v == viewAll) {
            this.c = db.rawQuery("SELECT * FROM student", null)
            if (this.c.count == 0) {
                showMessage("Error", "No records found")
                return
            }
            val buffer = StringBuffer()
            while (this.c.moveToNext()) {
                buffer.append("Roll No: " + this.c.getString(0).trimIndent() + "\n")
                buffer.append("Name: " + this.c.getString(1).trimIndent() + "\n")
                buffer.append("CGPA: " + this.c.getString(2).trimIndent() + "\n\n")
            }
            showMessage("Student Details", buffer.toString())
        }
    }

    private fun showMessage(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.show()
    }

    private fun clearText() {
        srollno.setText("")
        sname.setText("")
        scgpa.setText("")
    }
}
