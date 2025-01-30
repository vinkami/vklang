import vklang.Script
import vklang.interpret.SymbolTable

fun main() {
    val script = Script("src/test.vk")
    val st = SymbolTable({ s: String -> println(s)})
    val err = script.interpret(st)
    if (err != null) {
        println(err)
        println(script.parse())
    }
}