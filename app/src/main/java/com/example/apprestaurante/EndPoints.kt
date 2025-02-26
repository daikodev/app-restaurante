package com.example.apprestaurante

object EndPoints {
    private val URL_ROOT = "http://192.168.10.100:8080/"

    // EndPoints de Login

    val URL_LOGIN = URL_ROOT + "restaurante/login"

    // EndPoints de Productos

    val URL_LIST_PRODUCTS = URL_ROOT + "products/all"

    val URL_FIND_PRODUCT = URL_ROOT + "products/code/"

    val URL_SAVE_PRODUCT = URL_ROOT + "products/save"

    val URL_UPDATE_PRODUCT = URL_ROOT + "products/update/"

    val URL_DELETE_PRODUCT = URL_ROOT + "products/delete/"

    // EndPoints de Ventas

    val URL_LIST_SALES = URL_ROOT + "sales/all"

    val URL_FIND_SALE = URL_ROOT + "sales/"

    val URL_SAVE_SALE = URL_ROOT + "sales/save"

    val URL_DELETE_SALE = URL_ROOT + "sales/delete/"
}