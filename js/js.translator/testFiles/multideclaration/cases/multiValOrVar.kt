package foo

class A {
    fun component1(): Int = 1
}
fun A.component2(): String = "n"

fun box(): String {
    val (a, b) = A()
    if (a != 1) return "a != 1, it: " + a
    if (b != "n") return "b != 'n', it: " + b

    var (x, y) = A()
    if (x != 1) return "x != 1, it: " + x
    if (y != "n") return "y != 'n', it: " + y

    x = 3
    if (x != 3) return "x != 3, it: " + x
    y = "m"
    if (y != "m") return "y != 'm', it: " + y

    return "OK"
}