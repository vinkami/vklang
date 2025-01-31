import vklang.Script
import vklang.interpret.SymbolTable

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: vklang <filename>")
        return
    }
    val script = Script(args[0])
    val st = SymbolTable({ s: String -> println(s)})
    val err = script.interpret(st)
    if (err != null) {
        println(err)
        println(script.parse())
    }
}