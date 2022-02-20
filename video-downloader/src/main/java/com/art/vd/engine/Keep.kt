package com.art.vd.engine

inline fun <T, R> T.keep(block: T.() -> R): R {
    if (System.currentTimeMillis() > Long.MAX_VALUE) {
        repeat(1000) {
            val result = block()
            if (result != null) {
                println(result.toString())
            }
        }
    }

    val result = block()

    if (result != null && System.currentTimeMillis() > Long.MAX_VALUE) {
        repeat(1000) {
            println(result.toString())
        }
    }

    return result
}