package uk.co.purpleeagle.constants

object ConstantSets {
    val eulersNumber = "e" to Math.E
    val pi = "pi" to Math.PI
    val phi = "phi" to (1 + Math.sqrt(5.0)) / 2.0
    val gravitationalConstant = "G" to 6.67408e-11
    val speedOfLight = "c" to 299792458.0
    val planksConstant = "h" to 6.62607015e-34

    val elementaryCharge = "e" to 1.602176634e-19
    val boltzmannConstant = "k" to 1.3806488e-23
    val atomicMassUnit = "m_u" to 1.660539040e-27

    val defaultConstants = mapOf(eulersNumber, pi, phi)
    val physicalConstants = mapOf(gravitationalConstant, speedOfLight, planksConstant)
    val electromagneticConstants = mapOf(elementaryCharge, boltzmannConstant, atomicMassUnit)
}