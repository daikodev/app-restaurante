package com.example.apprestaurante

class Product(
    val id: Int,
    val code: String,
    val description: String,
    val unitOfMeasure: String,
    val price: Double,
    val stock: Int,
    val status: Boolean
) {
}