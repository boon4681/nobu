package com.boon4681

import com.boon4681.com.boon4681.nobu.parser.Tokenizer
import com.boon4681.com.boon4681.nobu.parser.ast.parse

fun main() {
    val x = Tokenizer(
        """
let d_0 = a.t(10,
5)
d_0 += (0 == 0)

print("HI")

if (1 == 0){

} else if(1 == 1){

} else {
    print("JIOO")
}
""".trimIndent()
    )
    for (i in parse(x)) {
        println(i)
    }
//    while (true) {
//        val token = x.get()
//        if (token is Token.Lexer && token.token == Lexers.EOF) {
//            break
//        }
//        println(token)
//        x.next()
//    }
}