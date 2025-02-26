package com.example.apprestaurante

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SaleList(private val context: Activity, internal val sales: List<Sale>) :
    ArrayAdapter<Sale>(context, R.layout.layout_list_sales, sales) {

    override fun getView(position: Int, convertView: View?, paernt: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_list_sales, null, true)

        val textCode = listViewItem.findViewById(R.id.textCode) as TextView
        val textDate = listViewItem.findViewById(R.id.textDate) as TextView
        val textClient = listViewItem.findViewById(R.id.textClient) as TextView
        val textDni = listViewItem.findViewById(R.id.textDni) as TextView
        val textProduct = listViewItem.findViewById(R.id.textProduct) as TextView
        val textQuantity = listViewItem.findViewById(R.id.textQuantity) as TextView
        val textDiscount = listViewItem.findViewById(R.id.textDiscount) as TextView
        val textTotal = listViewItem.findViewById(R.id.textTotal) as TextView

        val sale = sales[position]
        textCode.text = "Venta: ${sale.code}"
        textDate.text = "Fecha: ${sale.date}"
        textClient.text = "Cliente: ${sale.client}"
        textDni.text = "DNI: ${sale.dni}"
        textProduct.text = "Plato: ${sale.productDescription}"
        textQuantity.text = "Cantidad: ${sale.quantity.toString()}"
        textDiscount.text = "Descuento: " + if (sale.discount) "Sí" else "No"
        textTotal.text = "Total: S/. ${sale.total}"

        return listViewItem
    }
}