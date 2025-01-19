package uk.co.purpleeagle.models

class Stack<E> (
    val capacity: Int? = null
): Collection<E> {
    private val stack = mutableListOf<E>()
    override val size: Int
        get() = stack.size

    override fun toString(): String {
        return stack.toString()
    }

    override fun contains(element: E): Boolean = stack.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = stack.containsAll(elements)

    override fun isEmpty(): Boolean = stack.isEmpty()

    fun isFull(): Boolean = stack.size == capacity

    override fun iterator(): Iterator<E> = stack.reversed().iterator()

    fun push(element: E) : Boolean {
        if (size == capacity) return false
        return stack.add(element)
    }
    fun pop(): E = stack.removeLast()
    fun popOrNull(): E? = stack.removeLastOrNull()
    fun peek(): E = stack.last()
    fun peekOrNull(): E? = stack.lastOrNull()

    fun fill(list: List<E>) {
        stack.addAll(list)
    }
}

fun <E> stackOf(vararg elements: E): Stack<E> {
    val stack = Stack<E>()
    stack.fill(elements.asList())
    return stack
}