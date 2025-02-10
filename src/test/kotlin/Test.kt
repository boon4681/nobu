import com.boon4681.com.boon4681.nobu.parser.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class tokenizerTests {
    @Test
    fun kywTrue() {
        val t = Tokenizer("true")
        assertEquals(Token.Keyword(Keywords.TRUE, Position(1, 1), false), t.get())
    }

    @Test
    fun kywFalse() {
        val t = Tokenizer("false")
        assertEquals(Token.Keyword(Keywords.FALSE, Position(1, 1), false), t.get())
    }

    @Test
    fun oprDot() {
        val t = Tokenizer(".")
        assertEquals(Token.Operator(Operators.DOT, Position(1, 1), false), t.get())
    }

    @Test
    fun oprRange() {
        val t = Tokenizer("..")
        assertEquals(Token.Operator(Operators.RANGE, Position(1, 1), false), t.get())
    }

    @Test
    fun testAssignNumber() {
        val t = Tokenizer("let a = 0")
        assertEquals(Token.Keyword(Keywords.LET, Position(1, 1), false), t.get())
        t.next()
        assertEquals(Token.Literal(Literals.IDENTIFIER, Position(1, 5), "a", true), t.get())
        t.next()
    }
}