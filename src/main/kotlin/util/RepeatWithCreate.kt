package uk.co.purpleeagle.util

fun <T> repeatWithCreate(
    times : Int,
    action : (Int) -> T
): List<T> {
    val result = mutableListOf<T>()

    repeat (times){
        result += action(it)
    }

    return result
}