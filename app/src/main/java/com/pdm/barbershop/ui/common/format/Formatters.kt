package com.pdm.barbershop.ui.common.format

import java.text.NumberFormat
import java.util.Locale

fun Double.toBRL(): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)

