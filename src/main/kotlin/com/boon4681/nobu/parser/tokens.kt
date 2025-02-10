package com.boon4681.com.boon4681.nobu.parser

sealed class Token(
    open val position: Position,
    open val spaceLeft: Boolean
) {
    data class Keyword(
        val token: Keywords,
        override val position: Position,
        override val spaceLeft: Boolean
    ) : Token(position, spaceLeft)

    data class Literal(
        val token: Literals,
        override val position: Position,
        val text: String,
        override val spaceLeft: Boolean
    ) : Token(position, spaceLeft)

    data class Operator(
        val token: Operators,
        override val position: Position,
        override val spaceLeft: Boolean
    ) : Token(position, spaceLeft)

    data class Lexer(
        val token: Lexers,
        override val position: Position
    ) : Token(position, false)

    fun type(tokenType: Any): Boolean {
        return when (this) {
            is Keyword -> token == tokenType
            is Literal -> token == tokenType
            is Operator -> token == tokenType
            is Lexer -> token == tokenType
        }
    }
}

enum class Lexers {
    EOF
}

enum class Keywords(val key: String) {
    LET("let"),
    CONST("const"),
    IF("if"),
    ELSE("else"),
    LOOP("loop"),
    BREAK("break"),
    FUNCTION("fun");
}

enum class Operators(val operand: String) {
    ASSIGN("="),
    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),
    DIV_ASSIGN("/="),
    MUL_ASSIGN("*="),
    MOD_ASSIGN("%="),
    EQUAL("=="),
    NOT_EQUAL("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_EQUAL(">="),
    LESS_THAN_EQUAL("<="),
    PARENT_LEFT("("),
    PARENT_RIGHT(")"),
    CURL_LEFT("{"),
    CURL_RIGHT("}"),
    COMMA(","),
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    NOT("!"),
    DOT("."),
    RANGE(".."),
    COLON(":"),
}

enum class Literals {
    NEWLINE,
    SEMICOLON,
    IDENTIFIER,
    NUMBER,
    STRING,
    TRUE,
    FALSE
}
