package com.example.apprestaurante

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import org.json.JSONException
import org.json.JSONObject

class RegisterProductsActivity : AppCompatActivity() {
    lateinit var txtCodeProduct: EditText
    lateinit var txtDescription: EditText
    lateinit var rbUnitOfMeasure: RadioGroup
    lateinit var rbFamily: RadioButton
    lateinit var rbPersonal: RadioButton
    lateinit var txtPriceUnit: EditText
    lateinit var txtStock: EditText

    lateinit var btnAdd: Button
    lateinit var btnSearchCode: Button
    lateinit var btnUpdate: Button
    lateinit var btnDelete: Button
    lateinit var btnViewProducts: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_products)

        txtCodeProduct = findViewById(R.id.txtCodeProduct)
        txtDescription = findViewById(R.id.txtDescription)
        rbUnitOfMeasure = findViewById(R.id.rbUnitOfMeasure)
        rbFamily = findViewById(R.id.rbFamily)
        rbPersonal = findViewById(R.id.rbPersonal)
        txtPriceUnit = findViewById(R.id.txtPriceUnit)
        txtStock = findViewById(R.id.txtStock)

        btnAdd = findViewById(R.id.btnAdd)
        btnSearchCode = findViewById(R.id.btnSearchCode)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        btnViewProducts = findViewById(R.id.btnViewProducts)

        btnAdd.setOnClickListener {
            saveProducts()
        }

        btnSearchCode.setOnClickListener {
            val code = txtCodeProduct.text.toString().trim()
            searchProductByCode(code)
        }

        btnUpdate.setOnClickListener {
            updateProducts()
        }

        btnDelete.setOnClickListener {
            deleteProducts()
        }

        btnViewProducts.setOnClickListener {
            val intent = Intent(this, ViewProductsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveProducts() {
        if (validate()) {
            val codeProduct = txtCodeProduct.text.toString().trim()
            val description = txtDescription.text.toString().trim()
            val priceUnit = txtPriceUnit.text.toString().trim()
            val stock = txtStock.text.toString().trim()
            val unitOfMeasure = when {
                rbFamily.isChecked -> "Familiar"
                rbPersonal.isChecked -> "Personal"
                else -> ""
            }

            if (!validateOthers(codeProduct, priceUnit, stock)) return

            if (unitOfMeasure.isEmpty()) {
                Toast.makeText(this, "Seleccione una unidad de medida", Toast.LENGTH_SHORT).show()
                return
            }

            val stringRequest = object : StringRequest(
                Request.Method.POST, EndPoints.URL_SAVE_PRODUCT,
                Response.Listener<String> { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val cod = jsonObject.getString("code")

                        Toast.makeText(
                            this,
                            "El producto $cod fue agregado exitosamente",
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
                        if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 409) {
                            Toast.makeText(
                                applicationContext,
                                "El código ingresado ya existe",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                volleyError.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("code", codeProduct)
                    params.put("description", description)
                    params.put("unitOfMeasure", unitOfMeasure)
                    params.put("price", priceUnit)
                    params.put("stock", stock)
                    params.put("status", "true")
                    return params
                }
            }

            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }

    private var productId: Int? = null // Almacenar el ID del producto

    private fun searchProductByCode(code: String) {
        if (validateCode()) {
            val stringRequest = StringRequest(
                Request.Method.GET,
                EndPoints.URL_FIND_PRODUCT + code,
                Response.Listener<String> { response ->
                    try {
                        val array = org.json.JSONArray(response)

                        if (array.length() == 0) {
                            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT)
                                .show()
                            return@Listener
                        }

                        val objectProduct = array.getJSONObject(0)

                        // Llenar inputs con información del producto
                        productId = objectProduct.getInt("id")
                        txtCodeProduct.setText(objectProduct.getString("code"))
                        txtDescription.setText(objectProduct.getString("description"))
                        txtPriceUnit.setText(objectProduct.getDouble("price").toString())
                        txtStock.setText(objectProduct.getInt("stock").toString())

                        val unitOfMeasure = objectProduct.getString("unitOfMeasure")
                        when (unitOfMeasure) {
                            "Familiar" -> rbFamily.isChecked = true
                            "Personal" -> rbPersonal.isChecked = true
                        }

                        Toast.makeText(this, "Producto encontrado", Toast.LENGTH_SHORT).show()

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

    private fun updateProducts() {
        if (productId == null) {
            Toast.makeText(this, "Por favor, busque un producto primero", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val codeProduct = txtCodeProduct.text.toString().trim()
        val description = txtDescription.text.toString().trim()
        val priceUnit = txtPriceUnit.text.toString().trim()
        val stock = txtStock.text.toString().trim()
        val unitOfMeasure = when {
            rbFamily.isChecked -> "Familiar"
            rbPersonal.isChecked -> "Personal"
            else -> ""
        }

        if (!validateOthers(codeProduct, priceUnit, stock)) return

        if (unitOfMeasure.isEmpty()) {
            Toast.makeText(this, "Seleccione una unidad de medida", Toast.LENGTH_SHORT).show()
            return
        }

        val stringRequest = object : StringRequest(
            Request.Method.PATCH, EndPoints.URL_UPDATE_PRODUCT + productId,
            Response.Listener<String> { response ->
                try {
                    Toast.makeText(
                        this,
                        "El producto $codeProduct fue actualizado exitosamente",
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
                    if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 409) {
                        Toast.makeText(
                            applicationContext,
                            "El código ingresado ya existe",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            volleyError.message,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("code", codeProduct)
                params.put("description", description)
                params.put("unitOfMeasure", unitOfMeasure)
                params.put("price", priceUnit)
                params.put("stock", stock)
                params.put("status", "true")
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun deleteProducts() {
        if (productId == null) {
            Toast.makeText(this, "Por favor, busque un producto primero", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val codeProduct = txtCodeProduct.text.toString().trim()

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Está seguro de que desea eliminar el producto $codeProduct?")
            .setPositiveButton("Sí") { _, _ ->
                val stringRequest = object : StringRequest(
                    Request.Method.PATCH, EndPoints.URL_DELETE_PRODUCT + productId,
                    Response.Listener<String> { response ->
                        try {
                            Toast.makeText(
                                this,
                                "El producto $codeProduct fue eliminado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            new()

                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this,
                                "Error al procesar la respuesta",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    },
                    object : Response.ErrorListener {
                        override fun onErrorResponse(volleyError: VolleyError) {
                            Toast.makeText(
                                applicationContext,
                                volleyError.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }) {
                }

                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }

            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun new() {
        txtCodeProduct.setText("")
        txtDescription.setText("")
        rbUnitOfMeasure.clearCheck()
        txtPriceUnit.setText("")
        txtStock.setText("")

        txtCodeProduct.requestFocus()
    }

    fun validate(): Boolean {
        var answer = true
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("Error")
            .setMessage("Por favor, complete todos los campos")

        val dialog: AlertDialog = builder.create()

        if (txtDescription.text.toString().trim().isEmpty()) {
            answer = false
            dialog.show()
            txtDescription.requestFocus()
        }

        if (txtPriceUnit.text.toString().trim().isEmpty()) {
            answer = false
            dialog.show()
            txtPriceUnit.requestFocus()
        }

        if (txtStock.text.toString().trim().isEmpty()) {
            answer = false
            dialog.show()
            txtStock.requestFocus()
        }

        if (txtCodeProduct.text.toString().trim().isEmpty()) {
            answer = false
            dialog.show()
            txtCodeProduct.requestFocus()
        }

        return answer
    }

    fun validateCode(): Boolean {
        var answer = true
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("Error")
            .setMessage("Por favor, ingrese un código para buscar")

        val dialog: AlertDialog = builder.create()

        if (txtCodeProduct.text.toString().trim().isEmpty()) {
            answer = false
            dialog.show()
            txtCodeProduct.requestFocus()
        }

        return answer
    }

    fun validateOthers(codeProduct: String, priceUnit: String, stock: String): Boolean {
        val codeVal = Regex("^P\\d{5}$")
        if (!codeProduct.matches(codeVal)) {
            Toast.makeText(this, "El formato del código debe ser 'P00001'", Toast.LENGTH_SHORT)
                .show()
            txtCodeProduct.requestFocus()
            return false
        }

        val priceVal = priceUnit.toDoubleOrNull()
        if (priceVal == null || priceVal <= 0) {
            Toast.makeText(this, "Ingrese un precio válido", Toast.LENGTH_SHORT).show()
            txtPriceUnit.requestFocus()
            return false
        }

        val stockVal = stock.toDoubleOrNull()
        if (stockVal == null || stockVal <= 0) {
            Toast.makeText(this, "Ingrese un stock válido", Toast.LENGTH_SHORT).show()
            txtStock.requestFocus()
            return false
        }

        return true
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