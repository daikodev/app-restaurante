package com.example.apprestaurante

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true

        return super.onCreateOptionsMenu(menu)
    }

    fun callMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun callRestaurant() {
        val intent = Intent(this, RestaurantActivity::class.java)
        startActivity(intent)
        finish()
    }

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

    fun callViewProducts() {
        val intent = Intent(this, ViewProductsActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun callViewSales() {
        val intent = Intent(this, ViewSalesActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun registerProducts() {
        val intent = Intent(this, RegisterProductsActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun registerSales() {
        val intent = Intent(this, RegisterSalesActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.locate -> callMap()

            R.id.restaurant -> callRestaurant()

            R.id.close -> close()

            R.id.viewProducts -> callViewProducts()

            R.id.viewSales -> callViewSales()

            R.id.products -> registerProducts()

            R.id.sales -> registerSales()
        }
        return super.onOptionsItemSelected(item)
    }
}