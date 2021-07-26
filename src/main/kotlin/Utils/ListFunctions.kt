package Utils

fun <R>List<R>.printMenu(){
    forEachIndexed {
        i, t ->
        println("${i+1} $t")
    }
}


fun <R>List<R>.getIndexOfOption(): Int {
    require(this.isNotEmpty())
    while (true){
        println("Seleccione el numero de una opcion valida:\n")
        printMenu()
        val i = readLineInt()
        if (i > 0 && i <= count()){
            return i-1
        }
    }
}

fun <R>List<R>.selectValidOption(): R {
    require(this.isNotEmpty())
    while (true){
        print("Seleccione el numero de una opcion valida:\n")
        printMenu()
        val i = readLineInt()
        if (i > 0 && i <= count()){
            return get(i-1)
        }
    }
}
