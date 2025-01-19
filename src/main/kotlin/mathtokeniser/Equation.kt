package uk.co.purpleeagle.mathtokeniser

data class Equation(
    val lhs: List<MathToken>,
    val rhs: List<MathToken>
)
