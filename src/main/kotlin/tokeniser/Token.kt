package uk.co.purpleeagle.tokeniser

data class Token(
    val tokenType: TokenType,
    val lexeme: String,
    val literal: Any?,
){
    override fun toString() = "$tokenType $lexeme $literal"

    companion object {
        fun replacedToken() = Token(TokenType.REPLACED, "", null)
    }
}
