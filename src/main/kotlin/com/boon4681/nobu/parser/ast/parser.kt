package com.boon4681.com.boon4681.nobu.parser.ast

import com.boon4681.com.boon4681.nobu.parser.Lexers
import com.boon4681.com.boon4681.nobu.parser.Literals
import com.boon4681.com.boon4681.nobu.parser.Tokenizer

fun parse(t: Tokenizer): ArrayList<Node> {
    val nodes: ArrayList<Node> = ArrayList()
    while (t.type(Literals.NEWLINE)) {
        t.next()
    }
    while (!t.type(Lexers.EOF)) {
        nodes.add(parseStatement(t))
        if (t.type(Literals.NEWLINE) || t.type(Literals.SEMICOLON)) {
            while (t.type(Literals.NEWLINE) || t.type(Literals.SEMICOLON)) {
                t.next()
            }
        } else if (t.type(Lexers.EOF)) {
            break
        } else {
            throw error("Multiple statements cannot be placed on a single line. ${t.pos()}")
        }
    }
    return nodes
}