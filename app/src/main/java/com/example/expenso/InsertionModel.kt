package com.example.expenso

data class InsertionModel (
    var amount: String? = null,
    var title: String? = null,
    val category: String?=null,
    val latitude: Double,
    val longitude: Double,
    var key: String? = null
)
{
    // Firebase needs a no-argument constructor, so you should ensure default values.
    constructor() : this("", "", "", 0.0, 0.0)  // No-argument constructor
}