package com.example.apprestaurante

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ViewSalesActivity : AppCompatActivity() {

    private var listView: ListView? = null
    private var saleList: MutableList<Sale>? = null
    private var totalSales: Int = 0

    lateinit var txtCode: EditText
    lateinit var btnSearch: Button
    lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_sales)

        listView = findViewById(R.id.listViewSales) as ListView
        saleList = mutableListOf<Sale>()

        txtCode = findViewById(R.id.txtCode)
        btnSearch = findViewById(R.id.btnSearch)
        btnClear = findViewById(R.id.btnClear)

        loadSales()

        btnSearch.setOnClickListener {
            val code = txtCode.text.toString().trim()
            searchSaleByCode(code)

        }

        btnClear.setOnClickListener {
            if (saleList!!.size != totalSales) {
                loadSales()
            }
            txtCode.setText("")
        }
    }

    private fun loadSales() {
        saleList!!.clear()

        val stringRequest = StringRequest(
            Request.Method.GET,
            EndPoints.URL_LIST_SALES,
            Response.Listener<String> { response ->
                try {
                    val array = org.json.JSONArray(response)

                    if (array.length() == 0) {
                        Toast.makeText(this, "No hay ventas registrados", Toast.LENGTH_SHORT).show()
                    }

                    for (i in 0 until array.length()) {
                        val objectSale = array.getJSONObject(i)

                        // Sacar description de product
                        val objectProduct = objectSale.getJSONObject("product")
                        val productDescription = objectProduct.getString("description")

                        val sale = Sale(
                            objectSale.getInt("id"),
                            objectSale.getString("code"),
                            objectSale.getString("dni"),
                            objectSale.getString("date"),
                            objectSale.getString("client"),
                            productDescription,
                            objectSale.getInt("quantity"),
                            objectSale.getDouble("total"),
                            objectSale.getBoolean("discount"),
                            objectSale.getBoolean("status")
                        )

                        saleList!!.add(sale)
                    }

                    val adapter = SaleList(this@ViewSalesActivity, saleList!!)
                    listView!!.adapter = adapter

                    totalSales = saleList!!.size

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            Response.ErrorListener { volleyError ->
                Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show()
            })

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun searchSaleByCode(code: String) {
        if (validate()) {
            val stringRequest = StringRequest(
                Request.Method.GET,
                EndPoints.URL_FIND_SALE + code,
                Response.Listener<String> { response ->
                    try {
                        if (response.isEmpty() || response == "null") {
                            Toast.makeText(this, "Venta no encontrada", Toast.LENGTH_SHORT).show()
                            return@Listener
                        }

                        val objectSale = JSONObject(response)

                        saleList!!.clear()

                        // Sacar description de product
                        val objectProduct = objectSale.getJSONObject("product")
                        val productDescription = objectProduct.getString("description")

                        val sale = Sale(
                            objectSale.getInt("id"),
                            objectSale.getString("code"),
                            objectSale.getString("dni"),
                            objectSale.getString("date"),
                            objectSale.getString("client"),
                            productDescription,
                            objectSale.getInt("quantity"),
                            objectSale.getDouble("total"),
                            objectSale.getBoolean("discount"),
                            objectSale.getBoolean("status")
                        )

                        saleList!!.add(sale)
                        Toast.makeText(this, "Venta encontrada", Toast.LENGTH_SHORT).show()

                        val adapter = SaleList(this@ViewSalesActivity, saleList!!)
                        listView!!.adapter = adapter

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                Response.ErrorListener { volleyError ->
                    Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG)
                        .show()
                })

            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }
    }

    fun validate(): Boolean {
        var answer = true
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("Error")
            .setMessage("Por favor, ingrese un código para buscar")

        val dialog: AlertDialog = builder.create()

        if (txtCode.text.toString().trim().isEmpty()) {
            answer = false
            dialog.show()
            txtCode.requestFocus()
        }

        return answer
    }

    // Menú para regresar al HomeActivity
    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_back, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true

        return super.onCreateOptionsMenu(menu)
    }

    fun back() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.back -> back()
        }
        return super.onOptionsItemSelected(item)
    }
}