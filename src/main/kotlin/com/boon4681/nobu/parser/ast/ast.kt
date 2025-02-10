package com.boon4681.com.boon4681.nobu.parser.ast

import com.boon4681.com.boon4681.nobu.parser.Location

abstract class Node(open val type: String) {
    abstract val loc: Location
}

sealed class Statement(type: String) : Node(type) {}

data class DeclareStatement(
    override val loc: Location,
    val name: String,
    val mut: Boolean,
    val defType: Type?,
    val expr: Expression?
) : Statement("declare_stmt")

data class BlockStatement(
    override val loc: Location,
    val stmt: ArrayList<Node>
) : Statement("block_stmt")

data class ConditionStatement(
    override val loc: Location,
    val cond: Expression,
    val block: ArrayList<Node>
) : Statement("condition_stmt")

data class IfStatement(
    override val loc: Location,
    val cond: Expression,
    val then: BlockStatement,
    val elif: ArrayList<ConditionStatement>,
    val el: BlockStatement?
) : Statement("if_stmt")

data class AssignStatement(
    override val loc: Location,
    val dest: Expression,
    val value: Expression
) : Statement("assign_stmt")

data class AddAssignStatement(
    override val loc: Location,
    val dest: Expression,
    val value: Expression
) : Statement("add_assign_stmt")

data class SubAssignStatement(
    override val loc: Location,
    val dest: Expression,
    val value: Expression
) : Statement("sub_assign_stmt")

data class MulAssignStatement(
    override val loc: Location,
    val dest: Expression,
    val value: Expression,
) : Statement("mul_assign_stmt")

data class DivAssignStatement(
    override val loc: Location,
    val dest: Expression,
    val value: Expression,
) : Statement("div_assign_stmt")

data class ModAssignStatement(
    override val loc: Location,
    val dest: Expression,
    val value: Expression
) : Statement("mod_assign_stmt")

sealed class Type(type: String) : Node(type) {}

data class NameType(
    override val loc: Location,
    val name: String
) : Type("nameType")

sealed class Expression(type: String) : Node(type) {}

data class Call(
    override val loc: Location,
    val target: Expression,
    val args: ArrayList<Expression>
) : Expression("call") {}

data class Reference(
    override val loc: Location,
    val ident: Ident,
    val props: ArrayList<Ident>
) : Expression("ref") {}

data class Vec2(
    override val loc: Location,
    val value: ArrayList<Expression>
) : Expression("vec2") {}

data class Vec3(
    override val loc: Location,
    val value: ArrayList<Expression>
) : Expression("vec3") {}

data class IdentProp(
    override val loc: Location,
    val target: Expression,
    val name: String
) : Expression("ident_prop") {}

data class Ident(
    override val loc: Location,
    val value: String
) : Expression("ident") {}

data class Bool(
    override val loc: Location,
    val value: Boolean
) : Expression("bool") {}

data class Str(
    override val loc: Location,
    val value: String
) : Expression("str") {}

data class Num(
    override val loc: Location,
    val value: Double
) : Expression("num") {}

data class Plus(
    override val loc: Location,
    val expr: Expression
) : Expression("plus") {}

data class Minus(
    override val loc: Location,
    val expr: Expression
) : Expression("minus") {}

data class Add(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("add") {}

data class Sub(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("sub") {}

data class Mul(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("mul") {}

data class Div(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("div") {}

data class Mod(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("mod") {}

data class Not(
    override val loc: Location,
    val expr: Expression
) : Expression("not") {}

data class Eq(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("eg") {}

data class NEq(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("neg") {}

data class Gt(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("Gt") {}

data class GtEq(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("GtEq") {}

data class Lt(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("Lt") {}

data class LtEq(
    override val loc: Location,
    val left: Expression,
    val right: Expression
) : Expression("LtEq") {}