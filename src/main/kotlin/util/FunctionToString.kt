package uk.co.purpleeagle.util

import uk.co.purpleeagle.mathtokeniser.Operations

fun printOutAllFunctions() {
    for (i in Operations.hashMap){
        println("${i.key} : ${i.value.function}")
    }
}