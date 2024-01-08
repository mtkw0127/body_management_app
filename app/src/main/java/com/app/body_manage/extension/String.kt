package com.app.body_manage.extension

fun String.toYYYYMMDD(): String {
    val stringBuilder = StringBuilder()
    if (4 <= this.length) {
        stringBuilder.append(substring(0, 4))
        stringBuilder.append("年")
    } else {
        stringBuilder.append(substring(0, lastIndex + 1))
    }
    if (5 <= this.length) {
        if (this.length == 5) {
            stringBuilder.append(substring(4, 5))
        }
        if (6 <= this.length) {
            stringBuilder.append(substring(4, 6))
            stringBuilder.append("月")
        }
    }
    if (this.length in 7..8) {
        stringBuilder.append(substring(6, lastIndex + 1))
        stringBuilder.append("日")
    }
    return stringBuilder.toString()
}

fun String.withPercent() = "$this %"

// ひらがなをカナに変換
fun String.toKana(): String {
    return this.map {
        if (it.code in 0x3041..0x3093) {
            it + 0x60
        } else {
            it
        }
    }.joinToString("")
}

fun String.toJp(): String {
    return this.map {
        if (it.code in 0x30A1..0x30F6) {
            it - 0x60
        } else {
            it
        }
    }.joinToString("")
}
