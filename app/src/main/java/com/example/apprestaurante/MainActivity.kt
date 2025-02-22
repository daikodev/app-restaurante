package com.example.apprestaurante

import android.content.Intent
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
import java.io.StringReader

class MainActivity : AppCompatActivity() {

    lateinit var txtNombre: EditText
    lateinit var txtContrasena: EditText

    lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtNombre = findViewById(R.id.txtNombre)
        txtContrasena = findViewById(R.id.txtContrasena)

        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            loginUsuario()
        }
    }

    private fun loginUsuario() {
        val nombre = txtNombre.text.toString().trim()
        val contrasena = txtContrasena.text.toString().trim()

        // Volley
        val stringRequest = object : StringRequest(Request.Method.POST, EndPoints.URL_LOGIN,
            Response.Listener<String> { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val idUsuario = jsonObject.getInt("idUsuario")
                    val nombreUsuario = jsonObject.getString("nombre")

                    Toast.makeText(this, "Bienvenido, $nombreUsuario", Toast.LENGTH_SHORT).show()

                    // Llamar al HomeActivity
                    val llamar = Intent(this, HomeActivity::class.java)
                    startActivity(llamar)
                    finish()

                } catch (e: JSONException) {
                    e.printStackTrace()
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
                params.put("nombre", nombre)
                params.put("contrasena", contrasena)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}