package vklang.interpret

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class BuiltinTest {
    @Test
    fun testPrint() {
        val (output, err) = run("""
            print("Hello, world!")
            print(5)
            print(5.5)
            print(true)
            print([1, 2, 3])
        """.trimIndent())
        assertNull(err)
        assertEquals("Hello, world!\n5.0\n5.5\ntrue\n[1.0, 2.0, 3.0]\n", output)
    }

    @Test
    fun testType() {
        val (output, err) = run("""
            class CustomType {}
            print(type(5))
            print(type("Hello, world!"))
            print(type(true))
            print(type([1, 2, 3]))
            print(type([1=2, "x"=4]))
            print(type(type))
            print(type(CustomType))
            print(type(CustomType()))
        """.trimIndent())
        assertNull(err)
        assertEquals("Number\nString\nBoolean\nList\nDict\nFunction\nClass\nCustomType\n", output)
    }
}