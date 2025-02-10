package com.boon4681.com.boon4681.nobu.parser

data class Position(val line: Int, val column: Int) {
    override fun toString(): String {
        return "${this.line}:${this.column}"
    }
}

data class Location(val start: Position, val end: Position)

class Scanner() {
    var firstPage: Int = 0
    var lastPage: Int = 0
    var page: Int = 0
    var index: Int = 0
    var line: Int = 0
    var column: Int = 0
    var char: Char? = null;
    private var pages: HashMap<Int, String> = HashMap()

    public val position: Position get() = Position(this.line + 1, this.column + 1)

    constructor(source: String) : this() {
        this.firstPage = 0
        this.lastPage = 0
        this.pages[0] = source
        this.moveNext()
    }

    public val eof: Boolean get() = index >= this.pages[page]!!.length

    public fun next() {
        if (!this.eof && this.char == '\n') {
            this.line += 1
            this.column = 0
        } else {
            this.column += 1
        }
        this.incIndex()
        this.moveNext()
    }

    public fun prev() {
        this.decIndex()
        this.movePrev()
    }

    private val isEndOfPage: Boolean get() = this.index >= this.pages[this.page]!!.length

    private fun moveNext() {
        this.load()
        while (true) {
            if (!this.eof && this.char == '\r') {
                this.incIndex()
                this.load()
                continue
            }
            break
        }
    }

    private fun movePrev() {
        this.load()
        while (true) {
            if (!this.eof && this.char == '\r') {
                this.decIndex()
                this.load()
                continue
            }
            break
        }
    }

    private fun incIndex() {
        if (!this.isEndOfPage) {
            this.index += 1
        } else if (this.page < this.lastPage) {
            this.page += 1
            this.index = 0
        }
    }

    private fun decIndex() {
        if (this.index > 0) {
            this.index -= 1
        } else if (this.page > this.firstPage) {
            this.page -= 1
            this.index = this.pages[this.page]!!.length - 1
        }
    }

    private fun load() {
        if (this.eof) {
            this.char = null
        } else {
            this.char = this.pages[this.page]?.get(this.index)
        }
    }
}