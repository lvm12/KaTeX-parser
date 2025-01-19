package uk.co.purpleeagle.util

private var called = ""

fun List<Any>.log(name: String, index: Int? = null) {
    if (called != name) println("Logging ${this::class.simpleName} : $name")
    if (index != null)println("Checking index $index")
    if (index != null) println("${take(index).space()}||\t${this[index]}\t||${takeLast(size-index-1).space()}")
    else println(this)
}


fun List<Any>.space() : String{
    return try {
        var result = get(0).toString()

        for (i in 1..size - 1) {
            result += "\t${get(i)}"
        }
        result
    }catch (_: Exception){
        ""
    }
}