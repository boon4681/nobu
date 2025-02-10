package com.boon4681.com.boon4681.nobu.parser.ast

import com.boon4681.com.boon4681.nobu.parser.*

fun parseStatement(t: Tokenizer): Node {
    val start = t.pos()
    if (t.type(Keywords.LET) || t.type(Keywords.CONST)) {
        return parseLet(t)
    }
    if (t.type(Keywords.IF)) {
        return parseIf(t)
    }
    val expr = parseExpr(t)
    return parseAssign(t, expr) ?: return expr
}

fun parseIf(t: Tokenizer): IfStatement {
    val start = t.pos()
    t.expect(Keywords.IF)
    t.next()
    val cond = parseExpr(t)
    val block = parseBlockStatement(t)

    while (t.type(Literals.NEWLINE)) {
        t.next()
    }
    val elseif = ArrayList<ConditionStatement>()
    var el: BlockStatement? = null
    while (t.type(Keywords.ELSE)) {
        t.next()
        while (t.type(Literals.NEWLINE)) {
            t.next()
        }
        if (t.type(Keywords.IF)) {
            t.next()
            val elseStart = t.pos()
            val elseCond = parseExpr(t)
            val elseThen = parseBlock(t)
            elseif.add(
                ConditionStatement(
                    Location(elseStart, t.pos()),
                    elseCond,
                    elseThen
                )
            )
            continue
        }
        el = parseBlockStatement(t)
        break
    }
    return IfStatement(
        Location(start, t.pos()),
        cond,
        block,
        elseif,
        el
    )
}

fun parseBlock(t: Tokenizer): ArrayList<Node> {
    t.expect(Operators.CURL_LEFT)
    t.next()
    while (t.type(Literals.NEWLINE)) {
        t.next()
    }
    val stmt = ArrayList<Node>()
    while (!t.type(Operators.CURL_RIGHT)) {
        stmt.add(parseStatement(t))

        if (t.type(Operators.CURL_RIGHT)) {
            break
        }
        if (t.type(Literals.NEWLINE)) {
            while (t.type(Literals.NEWLINE)) {
                t.next()
            }
            continue
        }
        if (t.type(Lexers.EOF)) {
            throw error("UnexpectedEOFError")
        }
        throw error("Multiple statements cannot be placed on a single line. ${t.pos()}")
    }
    t.expect(Operators.CURL_RIGHT)
    t.next()
    return stmt
}

fun parseBlockStatement(t: Tokenizer): BlockStatement {
    val start = t.pos()
    return BlockStatement(
        Location(start, t.pos()),
        parseBlock(t)
    )
}

fun parseBlockOrStatement(t: Tokenizer): Node {
    if (t.type(Operators.CURL_LEFT)) {
        return parseBlockStatement(t)
    }
    return parseStatement(t)
}

fun parseLet(t: Tokenizer): Statement {
    val start = t.pos()
    val mut = if (t.type(Keywords.LET)) {
        true
    } else if (t.type(Keywords.CONST)) {
        false
    } else {
        throw error("unexpected token ${t.pos()} ${t.get()}")
    }
    t.next()
    t.expect(Literals.IDENTIFIER)
    val name = (t.get() as Token.Literal).text
    t.next()
    var type: Type? = null
    if (t.type(Operators.COLON)) {
        t.next()
        type = parseType(t)
    }
    t.expect(Operators.ASSIGN)
    t.next()
    val expr = parseExpr(t)
    return DeclareStatement(
        Location(start, t.pos()),
        name,
        mut,
        type,
        expr
    )
}

fun parseAssign(t: Tokenizer, left: Expression): Statement? {
    val start = t.pos()
    if (t.type(Operators.ASSIGN)) {
        t.next()
        val expr = parseExpr(t)
        return AssignStatement(
            Location(start, t.pos()),
            left,
            expr
        )
    }
    if (t.type(Operators.ADD_ASSIGN)) {
        t.next()
        val expr = parseExpr(t)
        return AddAssignStatement(
            Location(start, t.pos()),
            left,
            expr
        )
    }
    if (t.type(Operators.SUB_ASSIGN)) {
        t.next()
        val expr = parseExpr(t)
        return SubAssignStatement(
            Location(start, t.pos()),
            left,
            expr
        )
    }
    if (t.type(Operators.MUL_ASSIGN)) {
        t.next()
        val expr = parseExpr(t)
        return MulAssignStatement(
            Location(start, t.pos()),
            left,
            expr
        )
    }
    if (t.type(Operators.DIV_ASSIGN)) {
        t.next()
        val expr = parseExpr(t)
        return DivAssignStatement(
            Location(start, t.pos()),
            left,
            expr
        )
    }
    if (t.type(Operators.MOD_ASSIGN)) {
        t.next()
        val expr = parseExpr(t)
        return ModAssignStatement(
            Location(start, t.pos()),
            left,
            expr
        )
    }
    return null
}