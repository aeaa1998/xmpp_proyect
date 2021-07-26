import Auth.Auth
import Utils.getIndexOfOption
import Utils.readString
import Utils.selectValidOption

fun main(args: Array<String>) {



    val auth = Auth
    auth.createUser("lolos19980101", "123456")
    print("Done")
}


fun mainMenu(){
    val initialMenu = listOf("Registrar Cuenta", "Iniciar Sesión", "Eliminar cuenta", "Salir")
    var option: Int = initialMenu.getIndexOfOption()
    while (option != 3){
        when(option){
            0 -> {
                //Registrar cuenta
                val username = readString("El usuario es un campo obligatorio\n")
                val password = readString("La contraseña es un campo obligatorio\n")
                Auth.createUser(username, password)
            }
            1 -> {
                //Login
                val username = readString("El usuario es un campo obligatorio\n")
                val password = readString("La contraseña es un campo obligatorio\n")
                if (Auth.login(username = username, password, notify = true)){
                    //We are logged
                }
            }
            2 -> {
                println("Para poder borrar su usuario le pediremos que ingrese sus credenciales")
                val username = readString("El usuario es un campo obligatorio\n")
                val password = readString("La contraseña es un campo obligatorio\n")
                if (Auth.login(username = username, password, notify = false)){
                    //We are logged
                    Auth.deleteAccount()
                }
            }
        }
        option = initialMenu.getIndexOfOption()
    }
    print("Gracias por usar el programa")
}