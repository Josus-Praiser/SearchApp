package com.josus.myapplication

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.josus.myapplication.util.ConnectionManager
import kotlinx.android.synthetic.main.grid_image.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.HashMap

class MainActivity : AppCompatActivity() {

    /** Google Api URL  */
    private val apiUrl = "https://www.googleapis.com/customsearch/v1"

    /** Google API key */
    private val key = "&key=AIzaSyCxU1BKS1WW6nFgofiSFYM4qJaBLYqf3IM"

    /** Google ID  */
    private val cx = "&cx=3a361edcf72e87cca"

    /** Get images link  */
    private val searchType = "&searchType=image"

    /** Pagination  */
    var start = 0
    var end =50

    lateinit var gridView: RecyclerView
    var imgList: MutableList<String> = ArrayList()
    lateinit var progresDialog: ProgressDialog
    lateinit var message: TextView
    var searchQuery: String? = null
    lateinit var searchBar: SearchView
    var columnCount = 2
    lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBar = findViewById(R.id.search_bar)
        gridView = findViewById(R.id.gridView)
        message = findViewById(R.id.message)
        progresDialog = ProgressDialog(this@MainActivity)

        progresDialog!!.setMessage("Loading")

        gridView.layoutManager =  GridLayoutManager(this, columnCount)

        gridView.adapter =  ImageAdapter(this@MainActivity,imgList)

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                searchQuery = query
                if(ConnectionManager().checkConnectivity(this@MainActivity)){
                    fetchImage()}
                else {
                    val dialog= AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Internet Connection not Found!")
                    dialog.setMessage("Connect to the internet")
                    dialog.setPositiveButton("OK")
                    {text, listener ->  }

                    dialog.setNegativeButton("Cancel")
                    {text,listener ->   }

                    dialog.create()
                    dialog.show()

                }
                if (query.isEmpty()) return false
                    fetchImage()
                    return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText
                return false
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menu.add(Menu.NONE, 1, Menu.NONE, "2")
        menu.add(Menu.NONE, 2, Menu.NONE, "3")
        menu.add(Menu.NONE, 3, Menu.NONE, "4")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            1 -> {
                columnCount = 2
                gridView.layoutManager =  GridLayoutManager(this, 2)
                true
            }

            2 -> {
                gridView.layoutManager =  GridLayoutManager(this, 3)
                true
            }
            3 -> {
                gridView.layoutManager =  GridLayoutManager(this, 4)
                true
            }
            else -> false
        }
    }


    fun fetchImage() {
        if (searchQuery == null || searchQuery!!.isEmpty()) return
        val url: String =
            (apiUrl + "?q=" + searchQuery!!.trim { it <= ' ' } + key + cx + searchType
                    + "&" + start + end)
        println(
            """
                $url
                """.trimIndent()
        )
        setLoading(true)
        PlaceTask().execute(url)
    }

    fun setLoading(value: Boolean) {
        if (value && !progresDialog.isShowing) {
            progresDialog.show()
            return
        }
        if (progresDialog.isShowing) progresDialog.dismiss()
    }

    inner class PlaceTask : AsyncTask<String?, Int?, String?>() {

        override fun doInBackground(vararg params: String?): String? {
            var data: String? = null
            try {
                data = params[0]?.let { downloadURL(it) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            print("TEST"+data)
            return data
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            ParserTask().execute(s)
        }
    }

    fun downloadURL(string: String): String {
        val url = URL(string)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()
        val stream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(stream))
        val builder = StringBuilder()
        var line: String? = ""
        while (reader.readLine().also { line = it } != null) {
            builder.append(line)
        }
        val data = builder.toString()
        reader.close()
        return data
    }

    inner class ParserTask :AsyncTask<String?, Int?, List<HashMap<String, String>>?>(){

        override fun doInBackground(vararg params: String?): List<HashMap<String, String>>? {
            val jsonParser = JsonParser()
            var mapList: List<HashMap<String, String>>? = null
            var `object`: JSONObject?=null
            try {
                `object` = JSONObject(params[0])
                mapList = jsonParser.parseResult(`object`)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return mapList
        }

    override fun onPostExecute(hashMaps: List<HashMap<String, String>>?) {
        super.onPostExecute(hashMaps)
        imgList.clear()
        for (i in hashMaps!!.indices) {
            try {
                hashMaps[i]["link"]?.let { imgList.add(it) }
            }catch (e :Exception){
                println(e)
            }
            println(hashMaps[i]["link"])
        }
        updateUI()
        searchBar.isIconified = true
        setLoading(false)
    }
}

    fun updateUI() {
        if (imgList.size > 0) {
            gridView.visibility = View.VISIBLE
            message.visibility = View.GONE
        } else {
            gridView.visibility = View.GONE
            message.visibility = View.VISIBLE
            message.text = "No Images Found"
        }
    }

fun done(view: View) {
    if(ConnectionManager().checkConnectivity(this@MainActivity)){
    fetchImage()}
    else{
        val dialog= AlertDialog.Builder(this@MainActivity)
        dialog.setTitle("Internet Connection not Found!")
        dialog.setMessage("Connect to the internet")
        dialog.setPositiveButton("OK")
        {text, listener ->  }

        dialog.setNegativeButton("Cancel")
        {text,listener ->   }

        dialog.create()
        dialog.show()

    }
}

}