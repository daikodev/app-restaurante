package com.example.apprestaurante

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ProductList(private val context: Activity, internal val products: List<Product>) :
    ArrayAdapter<Product>(context, R.layout.layout_list_products, products) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_list_products, null, true)

        val textDescription = listViewItem.findViewById(R.id.textDescription) as TextView
        val textCode = listViewItem.findViewById(R.id.textCode) as TextView
        val textUnit = listViewItem.findViewById(R.id.textUnit) as TextView
        val textPrice = listViewItem.findViewById(R.id.textPrice) as TextView
        val textStock = listViewItem.findViewById(R.id.textStock) as TextView

        val product = products[position]
        textDescription.text = "Plato: ${product.description}"
        textCode.text = "Código: ${product.code}"
        textUnit.text = "Medida: ${product.unitOfMeasure}"
        textPrice.text = "Precio: ${product.price}"
        textStock.text = "Stock: ${product.stock}"

        return listViewItem
    }
}