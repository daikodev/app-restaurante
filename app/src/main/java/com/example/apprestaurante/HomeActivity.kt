package com.example.apprestaurante

import android.annotation.SuppressLint
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.ubicacion -> Toast.makeText(this, "Consultas, llámanos al 942097670", Toast.LENGTH_SHORT).show()
            R.id.nosotros -> Toast.makeText(this, "Consultas, llámanos al 942097670", Toast.LENGTH_SHORT).show()
            R.id.cerrar -> Toast.makeText(this, "Consultas, llámanos al 942097670", Toast.LENGTH_SHORT).show()
            R.id.verProductos -> Toast.makeText(this, "Consultas, llámanos al 942097670", Toast.LENGTH_SHORT).show()
            R.id.verVentas -> Toast.makeText(this, "Consultas, llámanos al 942097670", Toast.LENGTH_SHORT).show()
            R.id.productos -> Toast.makeText(this, "Consultas, llámanos al 942097670", Toast.LENGTH_SHORT).show()
            R.id.ventas -> Toast.makeText(this, "Consultas, llámanos al 942097670", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}