package com.example.apprestaurante

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.view.menu.MenuBuilder
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RegisterSalesActivity : AppCompatActivity() {
    lateinit var txtCodeSale: EditText
    lateinit var txtDni: EditText
    lateinit var txtDate: EditText
    lateinit var txtClient: EditText

    lateinit var lblPriceUnit: TextView
    lateinit var txtQuantity: EditText
    lateinit var chDiscount: CheckBox
    lateinit var lblSubTotal: TextView
    lateinit var lblTotal: TextView
    lateinit var cboListProducts: Spinner
    lateinit var btnSave: Button
    lateinit var btnDeleteSale: Button
    lateinit var btnSearchCodeSale: Button
    lateinit var btnViewSales: Button
    lateinit var btnNew: Button

    var productsSpinner = mutableListOf<String>()
    var productsPrices = mutableListOf<Double>()
    var productsIds = mutableListOf<Int>()
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_sales)

        txtCodeSale = findViewById(R.id.txtCodeSale)
        txtDni = findViewById(R.id.txtDni)
        txtDate = findViewById(R.id.txtDate)
        txtClient = findViewById(R.id.txtClient)

        lblPriceUnit = findViewById(R.id.lblPriceUnit)
        txtQuantity = findViewById(R.id.txtQuantity)
        chDiscount = findViewById(R.id.chDiscount)
        lblSubTotal = findViewById(R.id.lblSubTotal)
        lblTotal = findViewById(R.id.lblTotal)

        cboListProducts = findViewById(R.id.cboListProducts)
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, productsSpinner)
        cboListProducts.setAdapter(adapter)

        btnSave = findViewById(R.id.btnSave)
        btnDeleteSale = findViewById(R.id.btnDeleteSale)
        btnSearchCodeSale = findViewById(R.id.btnSearchCodeSale)
        btnViewSales = findViewById(R.id.btnViewSales)
        btnNew = findViewById(R.id.btnNew)

        btnSave.setOnClickListener {
            saveSale()
        }

        btnDeleteSale.setOnClickListener {
            deleteSale()
        }

        btnSearchCodeSale.setOnClickListener {
            val code = txtCodeSale.text.toString().trim()

            if (code.isNotEmpty()) {
                searchSalesByCode(code)
            } else {
                Toast.makeText(this, "Por favor, ingrese un código para buscar", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnViewSales.setOnClickListener {
            val intent = Intent(this, ViewSalesActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnNew.setOnClickListener {
            new()
        }

        loadProductsForSpinner()

        cboListProducts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                lblPriceUnit.text = "S/ ${productsPrices[position]}"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                lblPriceUnit.text = ""
            }
        }
    }

    private fun loadProductsForSpinner() {
        val stringRequest = StringRequest(
            Request.Method.GET,
            EndPoints.URL_LIST_PRODUCTS,
            Response.Listener<String> { response ->
                try {
                    val array = JSONArray(response)

                    productsSpinner.clear()
                    productsPrices.clear()
                    productsIds.clear()

                    productsSpinner.add("Seleccionar un plato")
                    productsPrices.add(0.00)
                    productsIds.add(-1)

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

                        productsSpinner.add("${product.description} - ${product.unitOfMeasure}")
                        productsPrices.add(product.price)
                        productsIds.add(product.id)
                    }

                    adapter.notifyDataSetChanged()
                    cboListProducts.setSelection(0)

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

    private fun saveSale() {
        val codeSale = txtCodeSale.text.toString().trim()
        val dni = txtDni.text.toString().trim()
        val date = txtDate.text.toString().trim()
        val client = txtClient.text.toString().trim()

        val selectedProductIndex = cboListProducts.selectedItemPosition
        val selectedProductId = productsIds[selectedProductIndex]
        var priceUnit = productsPrices[selectedProductIndex]

        val quantity = txtQuantity.text.toString().trim().toInt()
        val dsct = chDiscount.isChecked

        if (chDiscount.isChecked) {
            priceUnit *= 0.90
        }

        val subTotal = priceUnit * quantity
        val igv = subTotal * 0.18
        val total = subTotal + igv

        lblSubTotal.text = "Subtotal: S/ %.2f".format(subTotal)
        lblTotal.text = "Total: S/ %.2f".format(total)

        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_SAVE_SALE,
            Response.Listener<String> { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val cod = jsonObject.getString("code")

                    Toast.makeText(
                        this,
                        "La venta $cod fue agregado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    new()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG)
                        .show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("code", codeSale)
                params.put("dni", dni)
                params.put("date", date)
                params.put("client", client)
                params.put("productId", selectedProductId.toString())
                params.put("quantity", quantity.toString())
                params.put("total", total.toString())
                params.put("discount", dsct.toString())
                params.put("status", "true")
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private var saleId: Int? = null // Almacenar el ID de la venta

    private fun searchSalesByCode(code: String) {
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

                    saleId = objectSale.getInt("id")
                    txtCodeSale.setText(objectSale.getString("code"))
                    txtDni.setText(objectSale.getString("dni"))
                    txtDate.setText(objectSale.getString("date"))
                    txtClient.setText(objectSale.getString("client"))

                    val product = objectSale.getJSONObject("product")
                    val productId = product.getInt("id")
                    val position = productsIds.indexOf(productId)
                    if (position != -1) {
                        cboListProducts.setSelection(position)
                        lblPriceUnit.text = "S/ ${productsPrices[position]}"
                    }

                    txtQuantity.setText(objectSale.getString("quantity"))

                    val dsct = objectSale.getBoolean("discount")
                    chDiscount.isChecked = dsct

                    // Calcular precio unitario y total
                    var priceUnit = productsPrices.getOrElse(position) {0.0}
                    val quantity = objectSale.getInt("quantity")

                    if (dsct) {
                        priceUnit *= 0.90
                    }

                    val subTotal = priceUnit * quantity
                    val igv = subTotal * 0.18
                    val total = subTotal + igv

                    lblSubTotal.text = "Subtotal: S/ %.2f".format(subTotal)
                    lblTotal.text = "Total: S/ %.2f".format(total)

                    Toast.makeText(this, "Venta encontrada", Toast.LENGTH_SHORT).show()

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

    private fun deleteSale() {
        if (saleId == null) {
            Toast.makeText(this, "Por favor, busque una venta primero", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val code = txtCodeSale.text.toString().trim()

        val stringRequest = object : StringRequest(
            Request.Method.PATCH, EndPoints.URL_DELETE_SALE + saleId,
            Response.Listener<String> { response ->
                try {
                    Toast.makeText(
                        this,
                        "La venta $code fue eliminada exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    new()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG)
                        .show()
                }
            }) {
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun new() {
        txtCodeSale.setText("")
        txtDni.setText("")
        txtDate.setText("")
        txtClient.setText("")
        cboListProducts.setSelection(0)
        txtQuantity.setText("")
        chDiscount.isChecked = false
        lblPriceUnit.text = "S/ 0.0"
        lblSubTotal.text = "Subtotal: S/. 0.00"
        lblTotal.text = "Total: S/. 0.00"

        txtCodeSale.requestFocus()
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