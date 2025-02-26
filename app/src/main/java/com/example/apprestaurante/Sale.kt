package com.example.apprestaurante

import java.util.*

class Sale(
    val id: Int,
    val code: String,
    val dni: String,
    val date: String,
    val client: String,
    val productDescription: String,
    val quantity: Int,
    val total: Double,
    val discount: Boolean,
    val status: Boolean
) {
}