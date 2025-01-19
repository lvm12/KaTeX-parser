package uk.co.purpleeagle.util

fun <T> MutableList<T>.removeAtIndexes(indexes: List<Int>): MutableList<T> {
    val copy = this
    indexes.reversed().forEach {
        copy.removeAt(it)
    }
    return copy
}