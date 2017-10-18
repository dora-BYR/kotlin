// WITH_RUNTIME

class Foo {
    lateinit var bar: String

    fun test(): Boolean {
        if (!::bar.isInitialized) {
            bar = "OK"
            return false
        }
        return true
    }
}

fun box(): String {
    val foo = Foo()
    if (foo.test()) return "Fail 1"
    return foo.bar
}
