package com.example.apprestaurante

import android.annotation.SuppressLint
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

class ViewProductsActivity : AppCompatActivity() {

    private var listView: ListView? = null
    private var productList: MutableList<Product>? = null
    private var totalProducts: Int = 0

    lateinit var txtCode: EditText
    lateinit var btnSearch: Button
    lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_products)

        listView = findViewById(R.id.listViewProducts) as ListView
        productList = mutableListOf<Product>()

        txtCode = findViewById(R.id.txtCode)
        btnSearch = findViewById(R.id.btnSearch)
        btnClear = findViewById(R.id.btnClear)

        loadProducts()

        btnSearch.setOnClickListener {
            val code = txtCode.text.toString().trim()

            if (code.isNotEmpty()) {
                searchProductByCode(code)
            } else {
                Toast.makeText(this, "Por favor, ingrese un código para buscar", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnClear.setOnClickListener {
            if (productList!!.size != totalProducts) {
                loadProducts()
            }
            txtCode.setText("")
        }
    }

    private fun loadProducts() {
        productList!!.clear()

        val stringRequest = StringRequest(
            Request.Method.GET,
            EndPoints.URL_LIST_PRODUCTS,
            Response.Listener<String> { response ->
                try {
                    val array = org.json.JSONArray(response)

                    for (i in 0 until array.length()) {
                        val objectProduct = array.getJSONObject(i)
                        val product = Product(
                            objectProduct.getInt("id"),
                            objectProduct.getString("code"),
                            objectProduct.getString("description"),
                            objectProduct.getString("unitOfMeasure"),
                            objectProduct.getDouble("price"),
                            objectProduct.getInt("stock"),
                            objectProduct.getBoolean("status"),
                        )

                        productList!!.add(product)
                    }

                    val adapter = ProductList(this@ViewProductsActivity, productList!!)
                    listView!!.adapter = adapter

                    totalProducts = productList!!.size

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

    private fun searchProductByCode(code: String) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            EndPoints.URL_FIND_PRODUCT + code,
            Response.Listener<String> { response ->
                try {
                    val array = org.json.JSONArray(response)

                    if (array.length() == 0) {
                        Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                        return@Listener
                    }

                    productList!!.clear() // Limpiar la lista de productos

                    for (i in 0 until array.length()) {
                        val objectProduct = array.getJSONObject(i)
                        val product = Product(
                            objectProduct.getInt("id"),
                            objectProduct.getString("code"),
                            objectProduct.getString("description"),
                            objectProduct.getString("unitOfMeasure"),
                            objectProduct.getDouble("price"),
                            objectProduct.getInt("stock"),
                            objectProduct.getBoolean("status"),
                        )

                        productList!!.add(product)
                        Toast.makeText(this, "Producto encontrado", Toast.LENGTH_SHORT).show()
                    }

                    val adapter = ProductList(this@ViewProductsActivity, productList!!)
                    listView!!.adapter = adapter

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