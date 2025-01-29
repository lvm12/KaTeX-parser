package uk.co.purpleeagle.algorithms

import uk.co.purpleeagle.mathtokeniser.Equation

abstract class Algorithm(
    private var equation: Equation
) {
    abstract fun solve(): Double
}