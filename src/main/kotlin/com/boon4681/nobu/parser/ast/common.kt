package com.boon4681.com.boon4681.nobu.parser.ast

import com.boon4681.com.boon4681.nobu.parser.Literals
import com.boon4681.com.boon4681.nobu.parser.Location
import com.boon4681.com.boon4681.nobu.parser.Token
import com.boon4681.com.boon4681.nobu.parser.Tokenizer

fun parseType(t: Tokenizer): Type {
    val start = t.pos()
    t.expect(Literals.IDENTIFIER)
    val token = t.get() as Token.Literal
    t.next()
    return NameType(
        Location(start, t.pos()),
        token.text
    )
}