package uk.co.purpleeagle.util

operator fun <E> Set<E>.get(index: Int): E = this.elementAt(index)
operator fun <E> Set<E>.set(index: Int, element: E): Set<E> {
    val list = toMutableList()
    list[index] = element
    return list.toSet()
}