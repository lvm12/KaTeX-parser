package uk.co.purpleeagle.util

fun <T> MutableList<T>.removeAtIndexes(indexes: List<Int>): MutableList<T> {
    val copy = this
    indexes.reversed().forEach {
        copy.removeAt(it)
    }
    return copy
}

fun <T> MutableList<T>.removeAtIndexes(vararg indexes: Int): MutableList<T> = removeAtIndexes(indexes.toList())
fun <T> MutableList<T>.removeAtIndexes(indexes: IntRange) : MutableList<T> = removeAtIndexes(indexes.toList())