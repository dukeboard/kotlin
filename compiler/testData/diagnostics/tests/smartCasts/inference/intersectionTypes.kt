package a

fun <T> id(t: T): T = t

fun <T> two(u: T, <!UNUSED_PARAMETER!>v<!>: T): T = u

fun <T> three(<!UNUSED_PARAMETER!>a<!>: T, <!UNUSED_PARAMETER!>b<!>: T, c: T): T = c

trait A
trait B: A
trait C: A

fun test(a: A, b: B, c: C) {
    if (a is B && a is C) {
        val d: C = id(a)
        val e: Any = id(a)
        val f = id(a)
        val g = two(a, b)
        g: B
        g: A

        val h: Any = two(a, b)

        val k = three(a, b, c)
        k: A
        <!TYPE_MISMATCH!>k<!>: B
        val l: Int = <!TYPE_INFERENCE_EXPECTED_TYPE_MISMATCH!>three<!>(a, b, c)
        
        use(d, e, f, g, h, k, l)
    }
}

fun <T> foo(t: T, <!UNUSED_PARAMETER!>l<!>: MutableList<T>): T = t

fun testErrorMessages(a: A, ml: MutableList<String>) {
    if (a is B && a is C) {
        <!TYPE_INFERENCE_CONFLICTING_SUBSTITUTIONS!>foo<!>(a, ml)
    }

    if(a is C) {
        <!TYPE_INFERENCE_CONFLICTING_SUBSTITUTIONS!>foo<!>(a, ml)
    }
}

fun rr(s: String?) {
    if (s != null) {
        val l = arrayListOf("", s)
        l: MutableList<String>
        <!TYPE_MISMATCH!>l<!>: MutableList<String?>
    }
}

//from library
fun arrayListOf<T>(vararg <!UNUSED_PARAMETER!>values<!>: T): MutableList<T> = throw Exception()

fun use(vararg a: Any) = a