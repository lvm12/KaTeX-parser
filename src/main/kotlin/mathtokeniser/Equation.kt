package uk.co.purpleeagle.mathtokeniser

data class Equation(
    val lhs: MutableList<MathToken>,
    val rhs: MutableList<MathToken>
)
