package com.boon4681.com.boon4681.nobu.parser.ast

import com.boon4681.com.boon4681.nobu.parser.*

private data class BindingPower(val lbp: Int?, val rbp: Int?)

fun parseReference(t: Tokenizer): Reference {
    val start = t.pos()
    t.expect(Literals.IDENTIFIER)
    val identToken = t.get() as Token.Literal
    t.next()
    val ident = Ident(
        Location(start, t.pos()),
        identToken.text
    )
    if (t.type(Literals.NEWLINE)) {
        t.next()
    }
    val props: ArrayList<Ident> = ArrayList()
    while (!t.type(Lexers.EOF)) {
        if (t.type(Operators.DOT)) {
            t.next()
            if (t.type(Literals.NEWLINE)) {
                t.next()
            }
            t.expect(Literals.IDENTIFIER)
            val token = t.get() as Token.Literal
            t.next()
            props.add(
                Ident(
                    Location(token.position, t.pos()),
                    token.text
                )
            )
            continue
        }
        break
    }
    return Reference(
        Location(start, t.pos()),
        ident,
        props
    )
}

public fun parseAtom(t: Tokenizer): Expression {
    val start = t.pos()
    when (val token = t.get()) {
        is Token.Keyword -> TODO()

        is Token.Literal -> {
            when (token.token) {
                Literals.IDENTIFIER -> {
                    return parseReference(t)
                }

                Literals.NUMBER -> {
                    val value = token.text.toDouble()
                    t.next()
                    return Num(Location(start, t.pos()), value)
                }

                Literals.STRING -> {
                    val value = token.text
                    t.next()
                    return Str(Location(start, t.pos()), value)
                }

                Literals.TRUE, Literals.FALSE -> {
                    val value = token.type(Literals.TRUE)
                    t.next()
                    return Bool(Location(start, t.pos()), value)
                }

                else -> {
                    throw error("unexpected token")
                }
            }
        }

        is Token.Operator -> {
            when (token.token) {
                Operators.PARENT_LEFT -> {
                    return parseParenthesis(t)
                }

                else -> {}
            }
        }

        else -> {}
    }
    throw error("unexpected token ${t.get()}")
}

private fun parseParenthesis(t: Tokenizer): Expression {
    val start = t.pos()
    t.expect(Operators.PARENT_LEFT)
    t.next()
    if (t.get().type(Literals.NEWLINE)) {
        t.next()
    }
    var commaRight = false
    val value: ArrayList<Expression> = ArrayList()
    while (!t.get().type(Operators.PARENT_RIGHT)) {
        commaRight = false
        value.add(parse(t, 0))
        val token = t.get()
        if (token.type(Literals.NEWLINE)) {
            t.next()
            continue
        }
        if (token.type(Operators.COMMA)) {
            commaRight = true
            t.next()
            if (t.get().type(Literals.NEWLINE)) {
                t.next()
            }
            continue
        }
        if (token.type(Operators.PARENT_RIGHT)) {
            break
        }
        if (token.type(Lexers.EOF)) {
            throw error("unexpected token")
        }
        throw error("separator expected ${t.pos()}")
    }
    t.expect(Operators.PARENT_RIGHT)
    t.next()
    return when (value.size) {
        1 -> {
            if (commaRight) throw error("unexpected token at ${t.pos()}")
            value[0]
        }

        2 -> {
            Vec2(
                Location(start, t.pos()),
                value
            )
        }

        3 -> {
            Vec3(
                Location(start, t.pos()),
                value
            )
        }

        else -> {
            throw error("unexpected token")
        }
    }
}

private fun parseCall(t: Tokenizer, left: Expression): Expression {
    val start = t.pos()
    t.expect(Operators.PARENT_LEFT)
    t.next()
    if (t.get().type(Literals.NEWLINE)) {
        t.next()
    }
    val value: ArrayList<Expression> = ArrayList()
    while (!t.get().type(Operators.PARENT_RIGHT)) {
        value.add(parse(t, 0))
        val token = t.get()
        if (token.type(Literals.NEWLINE)) {
            t.next()
            continue
        }
        if (token.type(Operators.COMMA)) {
            t.next()
            if (t.get().type(Literals.NEWLINE)) {
                t.next()
            }
            continue
        }
        if (token.type(Operators.PARENT_RIGHT)) {
            break
        }
        if (token.type(Lexers.EOF)) {
            throw error("unexpected token")
        }
        throw error("separator expected")
    }
    t.expect(Operators.PARENT_RIGHT)
    t.next()
    return Call(
        Location(start, t.pos()),
        left,
        value
    )
}

private fun isPrefix(token: Token): BindingPower? {
    if (token.type(Operators.SUB)) return BindingPower(14, null)
    if (token.type(Operators.NOT)) return BindingPower(14, null)
    if (token.type(Operators.RANGE)) return BindingPower(14, null)
    return null
}

private fun isPostfix(token: Token): BindingPower? {
    if (token.type(Operators.PARENT_LEFT)) return BindingPower(20, null)
    return null
}

private fun isInfix(token: Token): BindingPower? {
    if (token.type(Operators.DOT)) return BindingPower(18, 19)
    if (token.type(Operators.DIV)) return BindingPower(12, 13)
    if (token.type(Operators.MUL)) return BindingPower(12, 13)
    if (token.type(Operators.MOD)) return BindingPower(12, 13)

    if (token.type(Operators.ADD)) return BindingPower(10, 11)
    if (token.type(Operators.SUB)) return BindingPower(10, 11)

    if (token.type(Operators.LESS_THAN)) return BindingPower(8, 9)
    if (token.type(Operators.LESS_THAN_EQUAL)) return BindingPower(8, 9)
    if (token.type(Operators.GREATER_THAN)) return BindingPower(8, 9)
    if (token.type(Operators.GREATER_THAN_EQUAL)) return BindingPower(8, 9)

    if (token.type(Operators.EQUAL)) return BindingPower(6, 7)
    if (token.type(Operators.NOT_EQUAL)) return BindingPower(6, 7)
    return null
}

private fun parsePrefix(t: Tokenizer, minBp: Int): Expression {
    val start = t.pos()
    val token = t.get()
    t.next()
    val expr: Expression = parse(t, minBp)
    val end = expr.loc.end
    if (token.type(Operators.ADD)) {
        if (expr is Num) {
            return Num(Location(start, end), expr.value)
        }
        return Plus(Location(start, end), expr as Expression)
    }
    if (token.type(Operators.SUB)) {
        if (expr is Num) {
            return Num(Location(start, end), -1 * expr.value)
        }
        return Minus(Location(start, end), expr as Expression)
    }
    if (token.type(Operators.NOT)) {
        return Not(Location(start, end), expr as Expression)
    }
    throw error("unexpected token")
}

private fun parsePostfix(t: Tokenizer, left: Expression): Expression {
    val start = t.pos()
    val token = t.get()
    if (token.type(Operators.PARENT_LEFT)) {
        return parseCall(t, left)
    }
    TODO()
}

private fun parseInfix(t: Tokenizer, left: Expression, bp: Int): Expression {
    val start = t.pos()
    val token = t.get()
    t.next()
    if (token.type(Operators.DOT)) {
        t.expect(Literals.IDENTIFIER)
        val name = (t.get() as Token.Literal).text
        t.next()
        return IdentProp(
            Location(start, t.pos()),
            left,
            name
        )
    }
    val right = parse(t, bp)
    val end = t.pos()
    val op = token as Token.Operator
    when (op.token) {
        Operators.ADD -> {
            return Add(
                Location(start, end),
                left,
                right
            )
        }

        Operators.SUB -> {
            return Sub(
                Location(start, end),
                left,
                right
            )
        }

        Operators.MUL -> {
            return Mul(
                Location(start, end),
                left,
                right
            )
        }

        Operators.DIV -> {
            return Div(
                Location(start, end),
                left,
                right
            )
        }

        Operators.MOD -> {
            return Mod(
                Location(start, end),
                left,
                right
            )
        }

        Operators.EQUAL -> {
            return Eq(
                Location(start, end),
                left,
                right
            )
        }

        else -> {
            throw error("unexpected error")
        }
    }
}

private fun parse(t: Tokenizer, bp: Int): Expression {
    var token = t.get()
    var left: Expression
    val prefix = isPrefix(token)
    if (prefix != null) {
        left = parsePrefix(t, prefix.lbp!!)
    } else {
        left = parseAtom(t)
    }
    while (true) {
        val postfix = isPostfix(t.get())
        if (postfix != null) {
            if (postfix.lbp!! < bp) {
                break
            }
            if (t.get().type(Operators.PARENT_LEFT) && t.get().spaceLeft) {
                //
            } else {
                left = parsePostfix(t, left);
                continue;
            }
        }

        val infix = isInfix(t.get())
        if (infix != null) {
            if (infix.lbp!! < bp) {
                break
            }
            left = parseInfix(t, left, infix.rbp!!)
            continue
        }
        break
    }
    return left
}

fun parseExpr(t: Tokenizer): Expression {
    return parse(t, 0)
}