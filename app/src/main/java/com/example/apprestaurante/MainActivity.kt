package com.example.apprestaurante

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var txtName: EditText
    lateinit var txtPassword: EditText

    lateinit var btnLogin: Button
    lateinit var btnClose: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtName = findViewById(R.id.txtName)
        txtPassword = findViewById(R.id.txtPassword)

        btnLogin = findViewById(R.id.btnLogin)
        btnClose = findViewById(R.id.btnClose)

        btnLogin.setOnClickListener {
            loginUser()
        }

        btnClose.setOnClickListener {
            close()
        }
    }

    // Logear usuario
    private fun loginUser() {
        if (validate()) {
            val name = txtName.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            val stringRequest = object : StringRequest(Request.Method.POST, EndPoints.URL_LOGIN,
                Response.Listener<String> { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val username = jsonObject.getString("name")
                        Toast.makeText(this, "Bienvenido, $username", Toast.LENGTH_SHORT).show()

                        // Llamar al HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(volleyError: VolleyError) {
                        if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 401) {
                            Toast.makeText(
                                applicationContext,
                                "Credenciales incorrectas",
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
                    params.put("name", name)
                    params.put("password", password)
                    return params
                }
            }
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }

    // Validación para campos vacíos
    fun validate(): Boolean {
        var answer = true
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("Error")
            .setMessage("Por favor, complete todos los campos")

        val dialog: AlertDialog = builder.create()

        if (txtName.text.toString().trim().isEmpty()) {
            answer = false
            dialog.show()
            txtName.requestFocus()
        }

        if (txtPassword.text.toString().trim().isEmpty()) {
            answer = false
            dialog.show()
            txtPassword.requestFocus()
        }

        return answer
    }

    // Cerrar la aplicación
    fun close() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("¿Desea salir de la aplicación?")
            .setPositiveButton("Sí") { dialog, which ->
                Toast.makeText(
                    applicationContext, android.R.string.yes,
                    Toast.LENGTH_SHORT
                ).show()

                finishAffinity()
            }
            .setNegativeButton("No") { dialog, which ->
                Toast.makeText(
                    applicationContext, android.R.string.no,
                    Toast.LENGTH_SHORT
                ).show()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}