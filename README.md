# Proyecto XMPP
Realizado con la libreria Smack para kotlin/java.

El proyecto esta pensado para un server de ejabberd.

### Configuraciones que se personalizaron con ejaberd fueron:
- Los usuarios todos estan en un rooster compartido para agregar a alguien a contactos se <br> escoge dentro de ese
roster compartido.
- No hay timeout de conección ni de registro.
- Solo el admin puede ingresar al server web

Para correrlo se corre en IntelliJ ya que el gradle importa smack.

El archivo que contiene la configuracion es el .yml para ejabberd.
Para correrlo no se necesita nada mas que instalar ejabberd y configurarle su dominio localhost.<br>
```kotlin
XMPPTCPConnectionConfiguration
    .builder()
    .setXmppDomain("localhost") //<----Localhost
    .setHost("127.0.0.1")//<----Localhost
```