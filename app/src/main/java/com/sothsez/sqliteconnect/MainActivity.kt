package com.sothsez.sqliteconnect

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sothsez.sqliteconnect.databinding.ActivityMainBinding
import com.sothsez.sqliteconnect.model.TitleAdapter
import com.sothsez.sqliteconnect.model.TitleData
import com.sothsez.sqliteconnect.model.db.FeedEntry
import com.sothsez.sqliteconnect.model.db.FeedReaderDbHelper

class MainActivity : AppCompatActivity(), TitleAdapter.TitleInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: FeedReaderDbHelper
    private lateinit var datasList: ArrayList<TitleData>

    private var idSelect: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dbHelper = FeedReaderDbHelper(this)
        readDatabase()

        binding.recyclerName.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter =  TitleAdapter(datasList, this@MainActivity)
        }


        binding.btnCreate.setOnClickListener {
            insertItem()
            updateRecyclerView()
        }

        binding.btnUpdate.setOnClickListener {
            updateItem()
            updateRecyclerView()
        }

        binding.btnSearch.setOnClickListener {
            searchDatabase()
            updateRecyclerView()
        }

        binding.btnDelete.setOnClickListener {
            deleteItem()
            updateRecyclerView()
        }


    }

    private fun insertItem() {
        if (!binding.etTitle.text.equals("")) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(FeedEntry.COLUMN_NAME_TITLE, binding.etTitle.text.toString())
                put(FeedEntry.COLUMN_NAME_SUBTITLE, binding.etSubtitle.text.toString())
            }
            val newRowId = db?.insert(FeedEntry.TABLE_NAME, null, values)
            db.close()

            updateRecyclerView()
            Toast.makeText(this, "Insert data complete.", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "Input data should fill in title.", Toast.LENGTH_SHORT).show()
            binding.etTitle.requestFocus()
        }
    }

    private fun readDatabase() {
        val db = dbHelper.readableDatabase

        val sortOrder = "${FeedEntry.COLUMN_NAME_TITLE} ASC"

        val cursor = db.query(
            FeedEntry.TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        datasList = ArrayList<TitleData>()

        with(cursor) {
            while (moveToNext()) {
                val item = TitleData(
                    getLong(getColumnIndexOrThrow(BaseColumns._ID)),
                    getString(getColumnIndex("title")),
                    getString(getColumnIndex("subtitle"))
                )
                datasList.add(item)
            }
        }
        cursor.close()
        db.close()
    }

    private fun searchDatabase() {
        if (!binding.etTitle.text.equals("")) {

            val db = dbHelper.readableDatabase

            val selector = "${FeedEntry.COLUMN_NAME_TITLE} = " + binding.etTitle.text.toString()

            val cursor = db.query(
                FeedEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selector,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )

            datasList = ArrayList<TitleData>()

            with(cursor) {
                while (moveToNext()) {
                    val item = TitleData(
                        getLong(getColumnIndexOrThrow(BaseColumns._ID)),
                        getString(getColumnIndex("title")),
                        getString(getColumnIndex("subtitle"))
                    )
                    datasList.add(item)
                }
            }
            cursor.close()
            db.close()
        }
    }

    private fun updateItem() {
        if (!binding.etTitle.text.equals("")) {

            val db = dbHelper.writableDatabase

            val values = ContentValues().apply {
                put(FeedEntry.COLUMN_NAME_TITLE, binding.etTitle.text.toString())
                put(FeedEntry.COLUMN_NAME_SUBTITLE, binding.etSubtitle.text.toString())
            }

            val selection = "${BaseColumns._ID} = \'" + idSelect + "\'"
            val count = db.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                null
            )

            Toast.makeText(this, "Update data complete.", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "Input data should fill in title. ", Toast.LENGTH_SHORT).show()
            binding.etTitle.requestFocus()
        }
    }

    private fun deleteItem() {
        if (!idSelect.equals(-1)) {

            val db = dbHelper.writableDatabase

            val selection = "${BaseColumns._ID} = \'" + idSelect + "\'"
            val deletedRows = db.delete(FeedEntry.TABLE_NAME, selection, null)
            db.close()

            updateRecyclerView()
            idSelect = -1
            Toast.makeText(this, "Delete data complete.", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "Please select item.", Toast.LENGTH_SHORT).show()
            binding.etTitle.requestFocus()
        }
    }

    private fun updateRecyclerView() {
        val recyclerViewTop =
            (binding.recyclerName.layoutManager as LinearLayoutManager)?.findFirstVisibleItemPosition()

        readDatabase()
        binding.recyclerName.adapter = TitleAdapter(datasList, this)
        binding.recyclerName.scrollToPosition(recyclerViewTop)
    }

    override fun onSelectTitleItem(title: String, subtitle: String, id: Long) {
        idSelect = id
        binding.etTitle.text = title.toEditable()
        binding.etSubtitle.text = subtitle.toEditable()
        Toast.makeText(this, "Select at item : "+idSelect, Toast.LENGTH_SHORT).show()

    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}