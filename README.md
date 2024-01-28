# Demonstration of web application redirected from TPM

Template/example for new [TPM](https://github.com/fjfjgg/TPM) web applications and HTTP tools.

# Descripción

Aplicación de intercambio de mensajes integrada con enseñanza virtual. Es un ejemplo de una integración de una aplicación web con [TPM](https://github.com/fjfjgg/TPM).

Soporta distintas salas de conversación, cada sala está identificada por el curso y 
el nombre del enlace. Si desea crear una nueva sala, simplemente edite el enlace que 
ha usado para acceder a la aplicación, copie los valores y cree un nuevo enlace con 
los mismos valores cambiando el nombre o curso.

Los mensajes se guardan en memoria, por lo que si se reinicia el servidor se pierden. Los mensajes se pueden borrar. El borrado de mensajes solo afecta a los usuarios que acceden posteriormente.

El nombre del usuario se obtiene de enseñanza virtual. También se obtiene el tipo de usuario
(profesor o estudiante). Ver ayuda y borrar los mensajes son acciones que solo puede realizar el profesor.

## Estado del proyecto

Funciona correctamente.

## Tecnologías usadas

* Java
* Servlets de Jakarta EE
* JSP

## Compilación

Se puede importar el proyecto con Eclipse y generar el war con él. 

Alternativamente también se puede usar `ant`. Modifique el fichero `build.xml` incluyendo las rutas adecuadas a las librerías de Tomcat. Después simplemente hay que ejecutar:

```shell
ant war
```

## Instalación

Debe instalar el `war` generado en un servidor de aplicaciones Jakarta EE 10. El servidor solo necesita implementar el *Jakarta EE Web Profile*, como por ejemplo [Tomcat 10.1](https://tomcat.apache.org/download-10.cgi).

### Autenticación entre TPM y esta aplicación web

Hay que elegir un usuario y contraseña para que TPM se autentique con la aplicación web.

Esto hay que configurarlo en la aplicación web y en TPM (secreto compartido).

#### Configuración en la aplicación web

En el servidor donde se ejecuta la aplicación web debe configurarse un usuario nuevo.

Si se usa Tomcat, hay que modificar `tomcat-users.xml`:

```
<role rolename="oauthclient"/>
<user username="clienttest" password="testclient" roles="oauthclient"/>
```
Puede modificar el nombre del cliente (en el ejemplo es `clienttest`) y la contraseña (en el ejemplo es `testclient`).

#### Configurar TPM

Al configurar la herramienta en TPM hay que seleccionar el tipo HTTP y en su configuración hay que generar una cabecera `Authentication`.

Convertir a base 64 la cadena "<usuario>:<clave>" (sustituyendo usuario y clave por los elegidos en la aplicación web) e incluir el valor obtenido en la configuración. Por ejemplo, usando los valores de ejemplo:
```json
  "headers": [
    {
      "key": "Authorization",
      "value": "Basic Y2xpZW50dGVzdDp0ZXN0Y2xpZW50",
      "literal": true
    }
    ],
```

En el fichero `tpm-cfg/config.json` está un ejemplo con la configuración completa.
En ese fichero debe modificar también donde aparece la cadena `<server>` por el nombre del servidor y puerto.

Además, en el formulario de configuración hay que establecer a `true` el parámetro `redirectMode` y agregar como argumentos extra: 

```
${ts.instructor ? "profesor" : "estudiante"},${ts.resourceLink.title}`
```

## Uso

1. Debe crear una nueva herramienta en TPM siguiendo los pasos anteriores y crear una clave/secreto.

2. Añada uno o varios enlaces LTI en su plataforma de enseñanza virtual con los datos anteriores.

3. Acceda al enlace LTI.

En el código fuente se suministra los ficheros `test.jsp` e `info.jsp` para hacer pruebas:

* `test.jsp`: permite crear una sesión sin necesidad de usar LTI. En un entorno de producción debería borrarse.

* `info.jsp`: muestra información de las salas existentes. Puede ser borrado también.

## Licencia

Distributed under the GNU GENERAL PUBLIC LICENSE Version 3. See `LICENSE.txt` for more information.

## Contacto

Francisco José Fernández Jiménez - [@fjfjes](ht) - fjfj @ us.es

Project Link: <https://github.com/fjfjgg/tpm>

## Referencias

- [TPM](https://github.com/fjfjgg/TPM)
