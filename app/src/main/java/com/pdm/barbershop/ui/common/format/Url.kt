package com.pdm.barbershop.ui.common.format

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * URL encoding helpers to safely build query strings and route arguments.
 * - encodeUrlComponent: encodes a single component (query value or path segment)
 * - toEncodedQueryString: builds a sorted, encoded query string from a map
 */
fun String.encodeUrlComponent(): String =
    URLEncoder.encode(this, StandardCharsets.UTF_8.toString())

fun Map<String, String?>.toEncodedQueryString(): String =
    entries
        .filter { it.value != null }
        .sortedBy { it.key }
        .joinToString(separator = "&") { (k, v) ->
            "${k.encodeUrlComponent()}=${v!!.encodeUrlComponent()}"
        }

