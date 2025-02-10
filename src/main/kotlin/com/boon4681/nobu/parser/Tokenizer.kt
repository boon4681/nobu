package com.boon4681.com.boon4681.nobu.parser

fun isLetter(c: Char?): Boolean {
    return c?.isLetter() == true || c == '_' || c?.isLetterOrDigit() == true
}

fun isSpace(c: Char?): Boolean {
    return c == ' ' || c == '\t'
}

class Tokenizer {
    private var scanner: Scanner
    private var tokens: ArrayDeque<Token> = ArrayDeque()

    constructor(source: String) : this(Scanner(source))

    constructor(source: Scanner) {
        this.scanner = source
        this.tokens.addLast(this.readToken())
    }

    public fun get(): Token {
        return this.tokens[0]
    }

    public fun pos(): Position {
        return this.tokens[0].position
    }

    public fun type(type: Any): Boolean {
        return this.tokens[0].type(type)
    }

    public fun next() {
        if (this.tokens[0] is Token.Lexer && (this.tokens[0] as Token.Lexer).token == Lexers.EOF) {
            return;
        }
        this.tokens.removeFirst()
        if (this.tokens.size == 0) {
            this.tokens.addLast(this.readToken())
        }
    }

    public fun expect(type: Any) {
        if (!this.get().type(type)) {
            throw error("unexpected token ${this.pos()} ${this.get()} expecting ${type}")
        }
    }

    private fun readToken(): Token {
        var space = false;
        while (true) {
            val position = this.scanner.position
            if (this.scanner.eof) {
                return Token.Lexer(Lexers.EOF, position)
            }
            if (isSpace(this.scanner.char)) {
                this.scanner.next()
                space = true
                continue
            }
            when (this.scanner.char) {
                '\n', '\r' -> {
                    this.skipEmptyLines()
                    return Token.Literal(Literals.NEWLINE, position, this.scanner.char.toString(), space)
                }

                '=' -> {
                    this.scanner.next()
                    if (this.scanner.char == '=') {
                        this.scanner.next()
                        return Token.Operator(Operators.EQUAL, position, space)
                    }
                    return Token.Operator(Operators.ASSIGN, position, space)
                }

                '>' -> {

                }

                '(' -> {
                    this.scanner.next()
                    return Token.Operator(Operators.PARENT_LEFT, position, space)
                }

                ')' -> {
                    this.scanner.next()
                    return Token.Operator(Operators.PARENT_RIGHT, position, space)
                }

                '{' -> {
                    this.scanner.next()
                    return Token.Operator(Operators.CURL_LEFT, position, space)
                }

                '}' -> {
                    this.scanner.next()
                    return Token.Operator(Operators.CURL_RIGHT, position, space)
                }

                ',' -> {
                    this.scanner.next()
                    return Token.Operator(Operators.COMMA, position, space)
                }

                '+' -> {
                    this.scanner.next()
                    if (this.scanner.char == '=') {
                        this.scanner.next()
                        return Token.Operator(Operators.ADD_ASSIGN, position, space)
                    }
                    return Token.Operator(Operators.ADD, position, space)
                }

                '-' -> {
                    this.scanner.next()
                    if (this.scanner.char == '=') {
                        this.scanner.next()
                        return Token.Operator(Operators.SUB_ASSIGN, position, space)
                    }
                    return Token.Operator(Operators.SUB, position, space)
                }

                '*' -> {
                    this.scanner.next()
                    if (this.scanner.char == '=') {
                        this.scanner.next()
                        return Token.Operator(Operators.MUL_ASSIGN, position, space)
                    }
                    return Token.Operator(Operators.MUL, position, space)
                }

                '/' -> {
                    this.scanner.next()
                    if (this.scanner.char == '=') {
                        this.scanner.next()
                        return Token.Operator(Operators.DIV_ASSIGN, position, space)
                    }
                    return Token.Operator(Operators.DIV, position, space)
                }

                '%' -> {
                    this.scanner.next()
                    if (this.scanner.char == '=') {
                        this.scanner.next()
                        return Token.Operator(Operators.MOD_ASSIGN, position, space)
                    }
                    return Token.Operator(Operators.MOD, position, space)
                }

                '!' -> {
                    this.scanner.next()
                    return Token.Operator(Operators.NOT, position, space)
                }

                '"', '\'' -> {
                    return this.readStringToken(space)
                }

                '.' -> {
                    this.scanner.next()
                    if (this.scanner.char == '.') {
                        this.scanner.next()
                        return Token.Operator(Operators.RANGE, position, space)
                    }
                    return Token.Operator(Operators.DOT, position, space)
                }

                ';' -> {
                    this.scanner.next()
                    return Token.Literal(Literals.SEMICOLON, position, ";", space)
                }

                else -> {
                    val numberToken = this.readDigitsToken(space)
                    if (numberToken != null) return numberToken

                    val wordToken = this.readWordToken(space)
                    if (wordToken != null) return wordToken

                    throw error("invalid character '${this.scanner.char}'")
                }
            }
        }
    }

    private fun readStringToken(spaceLeft: Boolean): Token {
        val mark = this.scanner.char
        var str = ""
        this.scanner.next()
        val position = this.scanner.position
        var escape = false
        while (true) {
            if (this.scanner.eof) throw error("unexpected EOF")
            if (!escape) {
                if (this.scanner.char == mark) {
                    this.scanner.next()
                    break
                }
                if (this.scanner.char == '\\') {
                    escape = true
                }
                str += this.scanner.char
                this.scanner.next()
                continue
            } else {
                str += this.scanner.char
                this.scanner.next()
                escape = false
                continue
            }
            break
        }
        return Token.Literal(Literals.STRING, position, str, spaceLeft)
    }

    private fun readDigitsToken(spaceLeft: Boolean): Token? {
        var word = ""
        var dot = false
        var leadingZERO = false

        val position = this.scanner.position
        while (!this.scanner.eof) {
            if (this.scanner.char in '0'..'9') {
                if (leadingZERO) throw error("Uncaught SyntaxError: Unexpected number")
                leadingZERO = word.isEmpty() && this.scanner.char == '0'
                word += this.scanner.char
                this.scanner.next()
                continue
            }
            if (this.scanner.char == '.') {
                if (dot) throw error("Uncaught SyntaxError: Unexpected number")
                leadingZERO = false
                dot = true
                word += "."
                this.scanner.next()
                continue
            }
            break
        }
        if (word.isEmpty()) return null
        return Token.Literal(Literals.NUMBER, position, word, spaceLeft)
    }

    private fun readWordToken(spaceLeft: Boolean): Token? {
        var word = ""
        val position = this.scanner.position
        while (!this.scanner.eof && isLetter(this.scanner.char)) {
            word += this.scanner.char
            this.scanner.next()
        }
        if (word.isEmpty()) return null;
        return when (word) {
            "let" -> {
                Token.Keyword(Keywords.LET, position, spaceLeft)
            }

            "const" -> {
                Token.Keyword(Keywords.CONST, position, spaceLeft)
            }

            "fun" -> {
                Token.Keyword(Keywords.FUNCTION, position, spaceLeft)
            }

            "if" -> {
                Token.Keyword(Keywords.IF, position, spaceLeft)
            }

            "else" -> {
                Token.Keyword(Keywords.ELSE, position, spaceLeft)
            }

            "loop" -> {
                Token.Keyword(Keywords.LOOP, position, spaceLeft)
            }

            "break" -> {
                Token.Keyword(Keywords.BREAK, position, spaceLeft)
            }

            "true" -> {
                Token.Literal(Literals.TRUE, position, "true", spaceLeft)
            }

            "false" -> {
                Token.Literal(Literals.FALSE, position, "false", spaceLeft)
            }

            else -> {
                Token.Literal(Literals.IDENTIFIER, position, word, spaceLeft)
            }
        }
    }

    private fun skipEmptyLines() {
        while (!this.scanner.eof) {
            when (this.scanner.char) {
                '\n', '\r' -> {
                    this.scanner.next()
                    continue
                }
            }
            break
        }
    }
}