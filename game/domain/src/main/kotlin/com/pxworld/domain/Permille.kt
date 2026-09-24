package com.pxworld.domain

const val PERMILLE: Int = 1000

fun Int.scaledBy(permille: Int): Int = (this.toLong() * permille / PERMILLE).toInt()
