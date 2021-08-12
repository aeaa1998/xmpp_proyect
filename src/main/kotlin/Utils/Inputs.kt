package Utils

import java.nio.charset.Charset

fun readLineInt(): Int {
    var input: Int
    while (true){
        try {
            input = Integer.valueOf(readLine())
            break
        }catch (e: Exception){
            print("Ingrese un numero valido")
        }
    }
    return input
}

fun readString(error: String = "Es un campo obligatorio"): String {
    val i = readLine()
    i?.let {
        return i
    }
    print(error)
    return readString(error)
}

fun readString(text:String, error: String = "Es un campo obligatorio"): String {
    println(text)
    val i = readLine()
    i?.let {
        return i
    }
    print(error)
    return readString(text, error)
}
