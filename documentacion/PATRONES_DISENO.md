# Patrones de Diseño Detallados - Proyecto HTFsystem

Este documento contiene las descripciones detalladas de los patrones de diseño utilizados en el proyecto Android de gestión de sistemas HTF.

## Modelos de Datos del Proyecto

### Member (Miembro)
```kotlin
data class Member(
    val id: Int, // id_miembro
    val nombre: String, // nombres + " " + apellido_paterno + " " + apellido_materno
    val email: String,
    val telefono: String?,
    val fechaRegistro: String
)
```

### Assignment (Asignación Activa)
```kotlin
data class Assignment(
    val id: Int, // id_asignacion
    val idMiembro: Int,
    val idProductoDigital: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val activa: Boolean,
    val cancelada: Boolean
)
```

### Fine (Recargo Cobrado)
```kotlin
data class Fine(
    val id: Int, // id_recargo
    val idMiembro: Int,
    val monto: Double,
    val pagada: Boolean,
    val fechaCreacion: String // fecha_registro
)
```

## Singleton
También llamado: Instancia única

**Propósito**  
Singleton es un patrón de diseño creacional que nos permite asegurarnos de que una clase tenga una única instancia, a la vez que proporciona un punto de acceso global a dicha instancia.

**Patrón Singleton**

**Problema**  
El patrón Singleton resuelve dos problemas al mismo tiempo, vulnerando el Principio de responsabilidad única:

- Garantizar que una clase tenga una única instancia. ¿Por qué querría alguien controlar cuántas instancias tiene una clase? El motivo más habitual es controlar el acceso a algún recurso compartido, por ejemplo, una base de datos o un archivo.
- Funciona así: imagina que has creado un objeto y al cabo de un tiempo decides crear otro nuevo. En lugar de recibir un objeto nuevo, obtendrás el que ya habías creado.
- Ten en cuenta que este comportamiento es imposible de implementar con un constructor normal, ya que una llamada al constructor siempre debe devolver un nuevo objeto por diseño.

El acceso global a un objeto  
Puede ser que los clientes ni siquiera se den cuenta de que trabajan con el mismo objeto todo el tiempo.

Proporcionar un punto de acceso global a dicha instancia. ¿Recuerdas esas variables globales que utilizaste (bueno, sí, fui yo) para almacenar objetos esenciales? Aunque son muy útiles, también son poco seguras, ya que cualquier código podría sobrescribir el contenido de esas variables y descomponer la aplicación.

Al igual que una variable global, el patrón Singleton nos permite acceder a un objeto desde cualquier parte del programa. No obstante, también evita que otro código sobreescriba esa instancia.

Este problema tiene otra cara: no queremos que el código que resuelve el primer problema se encuentre disperso por todo el programa. Es mucho más conveniente tenerlo dentro de una clase, sobre todo si el resto del código ya depende de ella.

Hoy en día el patrón Singleton se ha popularizado tanto que la gente suele llamar singleton a cualquier patrón, incluso si solo resuelve uno de los problemas antes mencionados.

**Solución**  
Todas las implementaciones del patrón Singleton tienen estos dos pasos en común:

- Hacer privado el constructor por defecto para evitar que otros objetos utilicen el operador new con la clase Singleton.
- Crear un método de creación estático que actúe como constructor. Tras bambalinas, este método invoca al constructor privado para crear un objeto y lo guarda en un campo estático. Las siguientes llamadas a este método devuelven el objeto almacenado en caché.
- Si tu código tiene acceso a la clase Singleton, podrá invocar su método estático. De esta manera, cada vez que se invoque este método, siempre se devolverá el mismo objeto.

**Analogía en el mundo real**  
El gobierno es un ejemplo excelente del patrón Singleton. Un país sólo puede tener un gobierno oficial. Independientemente de las identidades personales de los individuos que forman el gobierno, el título "Gobierno de X" es un punto de acceso global que identifica al grupo de personas a cargo.

**Estructura**  
La estructura del patrón Singleton  
La clase Singleton declara el método estático obtenerInstancia que devuelve la misma instancia de su propia clase.

El constructor del Singleton debe ocultarse del código cliente. La llamada al método obtenerInstancia debe ser la única manera de obtener el objeto de Singleton.

**Pseudocódigo**  
En este ejemplo, la clase de conexión de la base de datos actúa como Singleton. Esta clase no tiene un constructor público, por lo que la única manera de obtener su objeto es invocando el método obtenerInstancia. Este método almacena en caché el primer objeto creado y lo devuelve en todas las llamadas siguientes.

// La clase Base de datos define el método `obtenerInstancia` 
// que permite a los clientes acceder a la misma instancia de
// una conexión de la base de datos a través del programa.
class Database is
    // El campo para almacenar la instancia singleton debe
    // declararse estático.
    private static field instance: Database

    // El constructor del singleton siempre debe ser privado
    // para evitar llamadas de construcción directas con el
    // operador `new`.
    private constructor Database() is
        // Algún código de inicialización, como la propia
        // conexión al servidor de una base de datos.
        // ...

    // El método estático que controla el acceso a la instancia
    // singleton.
    public static method getInstance() is
        if (Database.instance == null) then
            acquireThreadLock() and then
                // Garantiza que la instancia aún no se ha
                // inicializado por otro hilo mientras ésta ha
                // estado esperando el desbloqueo.
                if (Database.instance == null) then
                    Database.instance = new Database()
        return Database.instance

    // Por último, cualquier singleton debe definir cierta
    // lógica de negocio que pueda ejecutarse en su instancia.
    public method query(sql) is
        // Por ejemplo, todas las consultas a la base de datos
        // de una aplicación pasan por este método. Por lo
        // tanto, aquí puedes colocar lógica de regularización
        // (throttling) o de envío a la memoria caché.
        // ...

class Application is
    method main() is
        Database foo = Database.getInstance()
        foo.query("SELECT ...")
        // ...
        Database bar = Database.getInstance()
        bar.query("SELECT ...")
        // La variable `bar` contendrá el mismo objeto que la
        // variable `foo`.

**Aplicabilidad**  
Utiliza el patrón Singleton cuando una clase de tu programa tan solo deba tener una instancia disponible para todos los clientes; por ejemplo, un único objeto de base de datos compartido por distintas partes del programa.

El patrón Singleton deshabilita el resto de las maneras de crear objetos de una clase, excepto el método especial de creación. Este método crea un nuevo objeto, o bien devuelve uno existente si ya ha sido creado.

Utiliza el patrón Singleton cuando necesites un control más estricto de las variables globales.

Al contrario que las variables globales, el patrón Singleton garantiza que haya una única instancia de una clase. A excepción de la propia clase Singleton, nada puede sustituir la instancia en caché.

Ten en cuenta que siempre podrás ajustar esta limitación y permitir la creación de cierto número de instancias Singleton. La única parte del código que requiere cambios es el cuerpo del método getInstance.

**Cómo implementarlo**  
- Añade un campo estático privado a la clase para almacenar la instancia Singleton.
- Declara un método de creación estático público para obtener la instancia Singleton.
- Implementa una inicialización diferida dentro del método estático. Debe crear un nuevo objeto en su primera llamada y colocarlo dentro del campo estático. El método deberá devolver siempre esa instancia en todas las llamadas siguientes.
- Declara el constructor de clase como privado. El método estático de la clase seguirá siendo capaz de invocar al constructor, pero no a los otros objetos.
- Repasa el código cliente y sustituye todas las llamadas directas al constructor de la instancia Singleton por llamadas a su método de creación estático.

**Pros y contras**  
- ✅ Puedes tener la certeza de que una clase tiene una única instancia.
- ✅ Obtienes un punto de acceso global a dicha instancia.
- ✅ El objeto Singleton solo se inicializa cuando se requiere por primera vez.
- ❌ Vulnera el Principio de responsabilidad única. El patrón resuelve dos problemas al mismo tiempo.
- ❌ El patrón Singleton puede enmascarar un mal diseño, por ejemplo, cuando los componentes del programa saben demasiado los unos sobre los otros.
- ❌ El patrón requiere de un tratamiento especial en un entorno con múltiples hilos de ejecución, para que varios hilos no creen un objeto Singleton varias veces.
- ❌ Puede resultar complicado realizar la prueba unitaria del código cliente del Singleton porque muchos frameworks de prueba dependen de la herencia a la hora de crear objetos simulados (mock objects). Debido a que la clase Singleton es privada y en la mayoría de los lenguajes resulta imposible sobrescribir métodos estáticos, tendrás que pensar en una manera original de simular el Singleton. O, simplemente, no escribas las pruebas. O no utilices el patrón Singleton.

**Relaciones con otros patrones**  
- Una clase fachada a menudo puede transformarse en una Singleton, ya que un único objeto fachada es suficiente en la mayoría de los casos.
- Flyweight podría asemejarse a Singleton si de algún modo pudieras reducir todos los estados compartidos de los objetos a un único objeto flyweight. Pero existen dos diferencias fundamentales entre estos patrones:
  - Solo debe haber una instancia Singleton, mientras que una clase Flyweight puede tener varias instancias con distintos estados intrínsecos.
  - El objeto Singleton puede ser mutable. Los objetos flyweight son inmutables.
- Los patrones Abstract Factory, Builder y Prototype pueden todos ellos implementarse como Singletons.

## Implementación en Android/Kotlin para Singleton
En Android/Kotlin, se implementa con 'object' para thread-safety automática. Para el cliente de Supabase, crea un objeto global:  
```kotlin
object SupabaseClient {
    val instance = createSupabaseClient(
        supabaseUrl = "URL",
        supabaseKey = "KEY"
    ) {
        install(Postgrest)
        install(Auth)
    }
}
```
Ventajas: Acceso global sin sincronización manual. Desventajas: Dificulta pruebas unitarias y puede violar principios de diseño. Usado para recursos compartidos como clientes de red o bases de datos.

## Factory Method
También llamado: Método fábrica, Constructor virtual

**Propósito**  
Factory Method es un patrón de diseño creacional que proporciona una interfaz para crear objetos en una superclase, mientras permite a las subclases alterar el tipo de objetos que se crearán.

**Patrón Factory Method**

**Problema**  
Imagina que estás creando una aplicación de gestión logística. La primera versión de tu aplicación sólo es capaz de manejar el transporte en camión, por lo que la mayor parte de tu código se encuentra dentro de la clase Camión.

Al cabo de un tiempo, tu aplicación se vuelve bastante popular. Cada día recibes decenas de peticiones de empresas de transporte marítimo para que incorpores la logística por mar a la aplicación.

Añadir una nueva clase de transporte al programa provoca un problema  
Añadir una nueva clase al programa no es tan sencillo si el resto del código ya está acoplado a clases existentes.

Estupendo, ¿verdad? Pero, ¿qué pasa con el código? En este momento, la mayor parte de tu código está acoplado a la clase Camión. Para añadir barcos a la aplicación habría que hacer cambios en toda la base del código. Además, si más tarde decides añadir otro tipo de transporte a la aplicación, probablemente tendrás que volver a hacer todos estos cambios.

Al final acabarás con un código bastante sucio, plagado de condicionales que cambian el comportamiento de la aplicación dependiendo de la clase de los objetos de transporte.

**Solución**  
El patrón Factory Method sugiere que, en lugar de llamar al operador new para construir objetos directamente, se invoque a un método fábrica especial. No te preocupes: los objetos se siguen creando a través del operador new, pero se invocan desde el método fábrica. Los objetos devueltos por el método fábrica a menudo se denominan productos.

La estructura de las clases creadoras  
Las subclases pueden alterar la clase de los objetos devueltos por el método fábrica.

A simple vista, puede parecer que este cambio no tiene sentido, ya que tan solo hemos cambiado el lugar desde donde invocamos al constructor. Sin embargo, piensa en esto: ahora puedes sobrescribir el método fábrica en una subclase y cambiar la clase de los productos creados por el método.

No obstante, hay una pequeña limitación: las subclases sólo pueden devolver productos de distintos tipos si dichos productos tienen una clase base o interfaz común. Además, el método fábrica en la clase base debe tener su tipo de retorno declarado como dicha interfaz.

La estructura de la jerarquía de productos  
Todos los productos deben seguir la misma interfaz.

Por ejemplo, tanto la clase Camión como la clase Barco deben implementar la interfaz Transporte, que declara un método llamado entrega. Cada clase implementa este método de forma diferente: los camiones entregan su carga por tierra, mientras que los barcos lo hacen por mar. El método fábrica dentro de la clase LogísticaTerrestre devuelve objetos de tipo camión, mientras que el método fábrica de la clase LogísticaMarítima devuelve barcos.

La estructura del código tras aplicar el patrón Factory Method  
Siempre y cuando todas las clases de producto implementen una interfaz común, podrás pasar sus objetos al código cliente sin descomponerlo.

El código que utiliza el método fábrica (a menudo denominado código cliente) no encuentra diferencias entre los productos devueltos por varias subclases, y trata a todos los productos como la clase abstracta Transporte. El cliente sabe que todos los objetos de transporte deben tener el método entrega, pero no necesita saber cómo funciona exactamente.

**Estructura**  
La estructura del patrón Factory Method  
El Producto declara la interfaz, que es común a todos los objetos que puede producir la clase creadora y sus subclases.

Los Productos Concretos son distintas implementaciones de la interfaz de producto.

La clase Creadora declara el método fábrica que devuelve nuevos objetos de producto. Es importante que el tipo de retorno de este método coincida con la interfaz de producto.

Puedes declarar el patrón Factory Method como abstracto para forzar a todas las subclases a implementar sus propias versiones del método. Como alternativa, el método fábrica base puede devolver algún tipo de producto por defecto.

Observa que, a pesar de su nombre, la creación de producto no es la principal responsabilidad de la clase creadora. Normalmente, esta clase cuenta con alguna lógica de negocios central relacionada con los productos. El patrón Factory Method ayuda a desacoplar esta lógica de las clases concretas de producto. Aquí tienes una analogía: una gran empresa de desarrollo de software puede contar con un departamento de formación de programadores. Sin embargo, la principal función de la empresa sigue siendo escribir código, no preparar programadores.

Los Creadores Concretos sobrescriben el Factory Method base, de modo que devuelva un tipo diferente de producto.

Observa que el método fábrica no tiene que crear nuevas instancias todo el tiempo. También puede devolver objetos existentes de una memoria caché, una agrupación de objetos, u otra fuente.

**Pseudocódigo**  
Este ejemplo ilustra cómo puede utilizarse el patrón Factory Method para crear elementos de interfaz de usuario (UI) multiplataforma sin acoplar el código cliente a clases UI concretas.

Ejemplo de la estructura del patrón Factory Method  
Ejemplo del diálogo multiplataforma.

La clase base de diálogo utiliza distintos elementos UI para representar su ventana. En varios sistemas operativos, estos elementos pueden tener aspectos diferentes, pero su comportamiento debe ser consistente. Un botón en Windows sigue siendo un botón en Linux.

Cuando entra en juego el patrón Factory Method no hace falta reescribir la lógica del diálogo para cada sistema operativo. Si declaramos un patrón Factory Method que produce botones dentro de la clase base de diálogo, más tarde podremos crear una subclase de diálogo que devuelva botones al estilo de Windows desde el Factory Method. Entonces la subclase hereda la mayor parte del código del diálogo de la clase base, pero, gracias al Factory Method, puede representar botones al estilo de Windows en pantalla.

Para que este patrón funcione, la clase base de diálogo debe funcionar con botones abstractos, es decir, una clase base o una interfaz que sigan todos los botones concretos. De este modo, el código sigue siendo funcional, independientemente del tipo de botones con el que trabaje.

Por supuesto, también se puede aplicar este sistema a otros elementos UI. Sin embargo, con cada nuevo método de fábrica que añadas al diálogo, más te acercarás al patrón Abstract Factory. No temas, más adelante hablaremos sobre este patrón.

// La clase creadora declara el método fábrica que debe devolver  
// un objeto de una clase de producto. Normalmente, las  
// subclases de la creadora proporcionan la implementación de  
// este método.  
class Dialog is  
    // La creadora también puede proporcionar cierta  
    // implementación por defecto del método fábrica.  
    abstract method createButton():Button  

    // Observa que, a pesar de su nombre, la principal  
    // responsabilidad de la creadora no es crear productos.  
    // Normalmente contiene cierta lógica de negocio que depende  
    // de los objetos de producto devueltos por el método  
    // fábrica. Las subclases pueden cambiar indirectamente esa  
    // lógica de negocio sobrescribiendo el método fábrica y  
    // devolviendo desde él un tipo diferente de producto.  
    method render() is  
        // Invoca el método fábrica para crear un objeto de  
        // producto.  
        Button okButton = createButton()  
        // Ahora utiliza el producto.  
        okButton.onClick(closeDialog)  
        okButton.render()  


// Los creadores concretos sobrescriben el método fábrica para  
// cambiar el tipo de producto resultante.  
class WindowsDialog extends Dialog is  
    method createButton():Button is  
        return new WindowsButton()  

class WebDialog extends Dialog is  
    method createButton():Button is  
        return new HTMLButton()  


// La interfaz de producto declara las operaciones que todos los  
// productos concretos deben implementar.  
interface Button is  
    method render()  
    method onClick(f)  

// Los productos concretos proporcionan varias implementaciones  
// de la interfaz de producto.  

class WindowsButton implements Button is  
    method render(a, b) is  
        // Representa un botón en estilo Windows.  
    method onClick(f) is  
        // Vincula un evento clic de OS nativo.  

class HTMLButton implements Button is  
    method render(a, b) is  
        // Devuelve una representación HTML de un botón.  
    method onClick(f) is  
        // Vincula un evento clic de navegador web.  

class Application is  
    field dialog: Dialog  

    // La aplicación elige un tipo de creador dependiendo de la  
    // configuración actual o los ajustes del entorno.  
    method initialize() is  
        config = readApplicationConfigFile()  

        if (config.OS == "Windows") then  
            dialog = new WindowsDialog()  
        else if (config.OS == "Web") then  
            dialog = new WebDialog()  
        else  
            throw new Exception("Error! Unknown operating system.")  

    // El código cliente funciona con una instancia de un  
    // creador concreto, aunque a través de su interfaz base.  
    // Siempre y cuando el cliente siga funcionando con el  
    // creador a través de la interfaz base, puedes pasarle  
    // cualquier subclase del creador.  
    method main() is  
        this.initialize()  
        dialog.render()

**Aplicabilidad**  
Utiliza el Método Fábrica cuando no conozcas de antemano las dependencias y los tipos exactos de los objetos con los que deba funcionar tu código.

El patrón Factory Method separa el código de construcción de producto del código que hace uso del producto. Por ello, es más fácil extender el código de construcción de producto de forma independiente al resto del código.

Por ejemplo, para añadir un nuevo tipo de producto a la aplicación, sólo tendrás que crear una nueva subclase creadora y sobrescribir el Factory Method que contiene.

Utiliza el Factory Method cuando quieras ofrecer a los usuarios de tu biblioteca o framework, una forma de extender sus componentes internos.

La herencia es probablemente la forma más sencilla de extender el comportamiento por defecto de una biblioteca o un framework. Pero, ¿cómo reconoce el framework si debe utilizar tu subclase en lugar de un componente estándar?

La solución es reducir el código que construye componentes en todo el framework a un único patrón Factory Method y permitir que cualquiera sobrescriba este método además de extender el propio componente.

Veamos cómo funcionaría. Imagina que escribes una aplicación utilizando un framework de UI de código abierto. Tu aplicación debe tener botones redondos, pero el framework sólo proporciona botones cuadrados. Extiendes la clase estándar Botón con una maravillosa subclase BotónRedondo, pero ahora tienes que decirle al clase principal FrameworkUI que utilice la nueva subclase de botón en lugar de la clase por defecto.

Para conseguirlo, creamos una subclase UIConBotonesRedondos a partir de una clase base del framework y sobrescribimos su método createBotón. Si bien este método devuelve objetos Botón en la clase base, haces que tu subclase devuelva objetos BotónRedondo. Ahora, utiliza la clase UIConBotonesRedondos en lugar de FrameworkUI. ¡Eso es todo!

Utiliza el Factory Method cuando quieras ahorrar recursos del sistema mediante la reutilización de objetos existentes en lugar de reconstruirlos cada vez.

A menudo experimentas esta necesidad cuando trabajas con objetos grandes y que consumen muchos recursos, como conexiones de bases de datos, sistemas de archivos y recursos de red.

Pensemos en lo que hay que hacer para reutilizar un objeto existente:

Primero, debemos crear un almacenamiento para llevar un registro de todos los objetos creados.
Cuando alguien necesite un objeto, el programa deberá buscar un objeto disponible dentro de ese agrupamiento.
… y devolverlo al código cliente.
Si no hay objetos disponibles, el programa deberá crear uno nuevo (y añadirlo al agrupamiento).
¡Eso es mucho código! Y hay que ponerlo todo en un mismo sitio para no contaminar el programa con código duplicado.

Es probable que el lugar más evidente y cómodo para colocar este código sea el constructor de la clase cuyos objetos intentamos reutilizar. Sin embargo, un constructor siempre debe devolver nuevos objetos por definición. No puede devolver instancias existentes.

Por lo tanto, necesitas un método regular capaz de crear nuevos objetos, además de reutilizar los existentes. Eso suena bastante a lo que hace un patrón Factory Method.

**Cómo implementarlo**  
- Haz que todos los productos sigan la misma interfaz. Esta interfaz deberá declarar métodos que tengan sentido en todos los productos.
- Añade un patrón Factory Method vacío dentro de la clase creadora. El tipo de retorno del método deberá coincidir con la interfaz común de los productos.
- Encuentra todas las referencias a constructores de producto en el código de la clase creadora. Una a una, sustitúyelas por invocaciones al Factory Method, mientras extraes el código de creación de productos para colocarlo dentro del Factory Method.
- Puede ser que tengas que añadir un parámetro temporal al Factory Method para controlar el tipo de producto devuelto.
- A estas alturas, es posible que el aspecto del código del Factory Method luzca bastante desagradable. Puede ser que tenga un operador switch largo que elige qué clase de producto instanciar. Pero, no te preocupes, lo arreglaremos enseguida.
- Ahora, crea un grupo de subclases creadoras para cada tipo de producto enumerado en el Factory Method. Sobrescribe el Factory Method en las subclases y extrae las partes adecuadas de código constructor del método base.
- Si hay demasiados tipos de producto y no tiene sentido crear subclases para todos ellos, puedes reutilizar el parámetro de control de la clase base en las subclases.
- Por ejemplo, imagina que tienes la siguiente jerarquía de clases: la clase base Correo con las subclases CorreoAéreo y CorreoTerrestre y la clase Transporte con Avión, Camión y Tren. La clase CorreoAéreo sólo utiliza objetos Avión, pero CorreoTerrestre puede funcionar tanto con objetos Camión, como con objetos Tren. Puedes crear una nueva subclase (digamos, CorreoFerroviario) que gestione ambos casos, pero hay otra opción. El código cliente puede pasar un argumento al Factory Method de la clase CorreoTerrestre para controlar el producto que quiere recibir.
- Si, tras todas las extracciones, el Factory Method base queda vacío, puedes hacerlo abstracto. Si queda algo dentro, puedes convertirlo en un comportamiento por defecto del método.

**Pros y contras**  
- ✅ Evitas un acoplamiento fuerte entre el creador y los productos concretos.
- ✅ Principio de responsabilidad única. Puedes mover el código de creación de producto a un lugar del programa, haciendo que el código sea más fácil de mantener.
- ✅ Principio de abierto/cerrado. Puedes incorporar nuevos tipos de productos en el programa sin descomponer el código cliente existente.
- ❌ Puede ser que el código se complique, ya que debes incorporar una multitud de nuevas subclases para implementar el patrón. La situación ideal sería introducir el patrón en una jerarquía existente de clases creadoras.

**Relaciones con otros patrones**  
- Muchos diseños empiezan utilizando el Factory Method (menos complicado y más personalizable mediante las subclases) y evolucionan hacia Abstract Factory, Prototype, o Builder (más flexibles, pero más complicados).
- La clase del Abstract Factory a menudo se basa en un grupo de métodos de fábrica, pero también puedes utilizar Prototype para escribir los métodos de estas clases.
- Puedes utilizar el patrón Factory Method junto con el Iterator para permitir que las subclases de la colección devuelvan distintos tipos de iteradores que sean compatibles con las colecciones.
- Prototype no se basa en la herencia, por lo que no presenta sus inconvenientes. No obstante, Prototype requiere de una inicialización complicada del objeto clonado. Factory Method se basa en la herencia, pero no requiere de un paso de inicialización.
- Factory Method es una especialización del Template Method. Al mismo tiempo, un Factory Method puede servir como paso de un gran Template Method.

## Strategy

**Strategy (Comportamiento) - Para algoritmos intercambiables, ej. diferentes fuentes de datos**

Encapsula algoritmos en interfaces intercambiables. Ejemplo para fuentes de datos:  

```kotlin
interface DataSource {
    fun fetchData(): List<Item>
}

class LocalDataSource : DataSource { ... }
class RemoteDataSource : DataSource { ... }

class DataManager(private var strategy: DataSource) {
    fun setStrategy(strategy: DataSource) { this.strategy = strategy }
    fun getData() = strategy.fetchData()
}
```

Ventajas: Intercambio en runtime sin cambios en cliente. Desventajas: Puede aumentar complejidad si estrategias son simples.

## Repository

**Repository (No en catálogo GoF, pero especificado) - Para abstracción de acceso a datos**

Abstrae data sources (Room, Retrofit) en MVVM. Ejemplo:  

```kotlin
class ItemRepository(private val api: ApiService, private val dao: ItemDao) {
    suspend fun getItems(): List<Item> {
        return try {
            val remote = api.getItems()
            dao.insertAll(remote)
            remote
        } catch (e: Exception) {
            dao.getAll()
        }
    }
}
```

Ventajas: Centraliza lógica de datos, facilita testing. Desventajas: Capa extra puede añadir complejidad. Complementa MVVM separando data access de UI.

## Observer
También llamado: Observador, Publicación-Suscripción, Modelo-patrón, Event-Subscriber, Listener

**Propósito**  
Observer es un patrón de diseño de comportamiento que te permite definir un mecanismo de suscripción para notificar a varios objetos sobre cualquier evento que le suceda al objeto que están observando.

**Patrón de diseño Observer**

**Problema**  
Imagina que tienes dos tipos de objetos: un objeto Cliente y un objeto Tienda. El cliente está muy interesado en una marca particular de producto (digamos, un nuevo modelo de iPhone) que estará disponible en la tienda muy pronto.

El cliente puede visitar la tienda cada día para comprobar la disponibilidad del producto. Pero, mientras el producto está en camino, la mayoría de estos viajes serán en vano.

Visita a la tienda vs. envío de spam  
Visita a la tienda vs. envío de spam

Por otro lado, la tienda podría enviar cientos de correos (lo cual se podría considerar spam) a todos los clientes cada vez que hay un nuevo producto disponible. Esto ahorraría a los clientes los interminables viajes a la tienda, pero, al mismo tiempo, molestaría a otros clientes que no están interesados en los nuevos productos.

Parece que nos encontramos ante un conflicto. O el cliente pierde tiempo comprobando la disponibilidad del producto, o bien la tienda desperdicia recursos notificando a los clientes equivocados.

**Solución**  
El objeto que tiene un estado interesante suele denominarse sujeto, pero, como también va a notificar a otros objetos los cambios en su estado, le llamaremos notificador (en ocasiones también llamado publicador). El resto de los objetos que quieren conocer los cambios en el estado del notificador, se denominan suscriptores.

El patrón Observer sugiere que añadas un mecanismo de suscripción a la clase notificadora para que los objetos individuales puedan suscribirse o cancelar su suscripción a un flujo de eventos que proviene de esa notificadora. ¡No temas! No es tan complicado como parece. En realidad, este mecanismo consiste en: 1) un campo matriz para almacenar una lista de referencias a objetos suscriptores y 2) varios métodos públicos que permiten añadir suscriptores y eliminarlos de esa lista.

Mecanismo de suscripción  
Un mecanismo de suscripción permite a los objetos individuales suscribirse a notificaciones de eventos.

Ahora, cuando le sucede un evento importante al notificador, recorre sus suscriptores y llama al método de notificación específico de sus objetos.

Las aplicaciones reales pueden tener decenas de clases suscriptoras diferentes interesadas en seguir los eventos de la misma clase notificadora. No querrás acoplar la notificadora a todas esas clases. Además, puede que no conozcas algunas de ellas de antemano si se supone que otras personas pueden utilizar tu clase notificadora.

Por eso es fundamental que todos los suscriptores implementen la misma interfaz y que el notificador únicamente se comunique con ellos a través de esa interfaz. Esta interfaz debe declarar el método de notificación junto con un grupo de parámetros que el notificador puede utilizar para pasar cierta información contextual con la notificación.

Métodos de notificación  
El notificador notifica a los suscriptores invocando el método de notificación específico de sus objetos.

Si tu aplicación tiene varios tipos diferentes de notificadores y quieres hacer a tus suscriptores compatibles con todos ellos, puedes ir más allá y hacer que todos los notificadores sigan la misma interfaz. Esta interfaz sólo tendrá que describir algunos métodos de suscripción. La interfaz permitirá a los suscriptores observar los estados de los notificadores sin acoplarse a sus clases concretas.

**Analogía en el mundo real**  
Suscripciones a revistas y periódicos  
Suscripciones a revistas y periódicos.

Si te suscribes a un periódico o una revista, ya no necesitarás ir a la tienda a comprobar si el siguiente número está disponible. En lugar de eso, el notificador envía nuevos números directamente a tu buzón justo después de la publicación, o incluso antes.

El notificador mantiene una lista de suscriptores y sabe qué revistas les interesan. Los suscriptores pueden abandonar la lista en cualquier momento si quieren que el notificador deje de enviarles nuevos números.

**Estructura**  
Estructura del patrón de diseño Observer  
El Notificador envía eventos de interés a otros objetos. Esos eventos ocurren cuando el notificador cambia su estado o ejecuta algunos comportamientos. Los notificadores contienen una infraestructura de suscripción que permite a nuevos y antiguos suscriptores abandonar la lista.

Cuando sucede un nuevo evento, el notificador recorre la lista de suscripción e invoca el método de notificación declarado en la interfaz suscriptora en cada objeto suscriptor.

La interfaz Suscriptora declara la interfaz de notificación. En la mayoría de los casos, consiste en un único método actualizar. El método puede tener varios parámetros que permitan al notificador pasar algunos detalles del evento junto a la actualización.

Los Suscriptores Concretos realizan algunas acciones en respuesta a las notificaciones emitidas por el notificador. Todas estas clases deben implementar la misma interfaz de forma que el notificador no esté acoplado a clases concretas.

Normalmente, los suscriptores necesitan cierta información contextual para manejar correctamente la actualización. Por este motivo, a menudo los notificadores pasan cierta información de contexto como argumentos del método de notificación. El notificador puede pasarse a sí mismo como argumento, dejando que los suscriptores extraigan la información necesaria directamente.

El Cliente crea objetos tipo notificador y suscriptor por separado y después registra a los suscriptores para las actualizaciones del notificador.

**Pseudocódigo**  
En este ejemplo, el patrón Observer permite al objeto editor de texto notificar a otros objetos tipo servicio sobre los cambios en su estado.

Ejemplo de estructura del patrón Observer  
Notificar a objetos sobre eventos que suceden a otros objetos.

La lista de suscriptores se compila dinámicamente: los objetos pueden empezar o parar de escuchar notificaciones durante el tiempo de ejecución, dependiendo del comportamiento que desees para tu aplicación.

En esta implementación, la clase editora no mantiene la lista de suscripción por sí misma. Delega este trabajo al objeto ayudante especial dedicado justo a eso. Puedes actualizar ese objeto para que sirva como despachador centralizado de eventos, dejando que cualquier objeto actúe como notificador.

Añadir nuevos suscriptores al programa no requiere cambios en clases notificadoras existentes, siempre y cuando trabajen con todos los suscriptores a través de la misma interfaz.

// La clase notificadora base incluye código de gestión de  
// suscripciones y métodos de notificación.  
class EventManager is  
    private field listeners: hash map of event types and listeners  

    method subscribe(eventType, listener) is  
        listeners.add(eventType, listener)  

    method unsubscribe(eventType, listener) is  
        listeners.remove(eventType, listener)  

    method notify(eventType, data) is  
        foreach (listener in listeners.of(eventType)) do  
            listener.update(data)  

// El notificador concreto contiene lógica de negocio real, de  
// interés para algunos suscriptores. Podemos derivar esta clase  
// de la notificadora base, pero esto no siempre es posible en  
// el mundo real porque puede que la notificadora concreta sea  
// ya una subclase. En este caso, puedes modificar la lógica de  
// la suscripción con composición, como hicimos aquí.  
class Editor is  
    public field events: EventManager  
    private field file: File  

    constructor Editor() is  
        events = new EventManager()  

    // Los métodos de la lógica de negocio pueden notificar los  
    // cambios a los suscriptores.  
    method openFile(path) is  
        this.file = new File(path)  
        events.notify("open", file.name)  

    method saveFile() is  
        file.write()  
        events.notify("save", file.name)  

    // ...  


// Aquí está la interfaz suscriptora. Si tu lenguaje de  
// programación soporta tipos funcionales, puedes sustituir toda  
// la jerarquía suscriptora por un grupo de funciones.  

interface EventListener is  
    method update(filename)  

// Los suscriptores concretos reaccionan a las actualizaciones  
// emitidas por el notificador al que están unidos.  
class LoggingListener implements EventListener is  
    private field log: File  
    private field message: string  

    constructor LoggingListener(log_filename, message) is  
        this.log = new File(log_filename)  
        this.message = message  

    method update(filename) is  
        log.write(replace('%s',filename,message))  

class EmailAlertsListener implements EventListener is  
    private field email: string  
    private field message: string  

    constructor EmailAlertsListener(email, message) is  
        this.email = email  
        this.message = message  

    method update(filename) is  
        system.email(email, replace('%s',filename,message))  

// Una aplicación puede configurar notificadores y suscriptores  
// durante el tiempo de ejecución.  
class Application is  
    method config() is  
        editor = new Editor()  

        logger = new LoggingListener(  
            "/path/to/log.txt",  
            "Someone has opened the file: %s")  
        editor.events.subscribe("open", logger)  

        emailAlerts = new EmailAlertsListener(  
            "admin@example.com",  
            "Someone has changed the file: %s")  
        editor.events.subscribe("save", emailAlerts)

**Aplicabilidad**  
Utiliza el patrón Observer cuando los cambios en el estado de un objeto puedan necesitar cambiar otros objetos y el grupo de objetos sea desconocido de antemano o cambie dinámicamente.

Puedes experimentar este problema a menudo al trabajar con clases de la interfaz gráfica de usuario. Por ejemplo, si creaste clases personalizadas de botón y quieres permitir al cliente colgar código cliente de tus botones para que se active cuando un usuario pulse un botón.

El patrón Observer permite que cualquier objeto que implemente la interfaz suscriptora pueda suscribirse a notificaciones de eventos en objetos notificadores. Puedes añadir el mecanismo de suscripción a tus botones, permitiendo a los clientes acoplar su código personalizado a través de clases suscriptoras personalizadas.

Utiliza el patrón cuando algunos objetos de tu aplicación deban observar a otros, pero sólo durante un tiempo limitado o en casos específicos.

La lista de suscripción es dinámica, por lo que los suscriptores pueden unirse o abandonar la lista cuando lo deseen.

**Cómo implementarlo**  
- Repasa tu lógica de negocio e intenta dividirla en dos partes: la funcionalidad central, independiente del resto de código, actuará como notificador; el resto se convertirá en un grupo de clases suscriptoras.
- Declara la interfaz suscriptora. Como mínimo, deberá declarar un único método actualizar.
- Declara la interfaz notificadora y describe un par de métodos para añadir y eliminar de la lista un objeto suscriptor. Recuerda que los notificadores deben trabajar con suscriptores únicamente a través de la interfaz suscriptora.
- Decide dónde colocar la lista de suscripción y la implementación de métodos de suscripción. Normalmente, este código tiene el mismo aspecto para todos los tipos de notificadores, por lo que el lugar obvio para colocarlo es en una clase abstracta derivada directamente de la interfaz notificadora. Los notificadores concretos extienden esa clase, heredando el comportamiento de suscripción.
- Sin embargo, si estás aplicando el patrón a una jerarquía de clases existentes, considera una solución basada en la composición: coloca la lógica de la suscripción en un objeto separado y haz que todos los notificadores reales la utilicen.
- Crea clases notificadoras concretas. Cada vez que suceda algo importante dentro de una notificadora, deberá notificar a todos sus suscriptores.
- Implementa los métodos de notificación de actualizaciones en clases suscriptoras concretas. La mayoría de las suscriptoras necesitarán cierta información de contexto sobre el evento, que puede pasarse como argumento del método de notificación.
- Pero hay otra opción. Al recibir una notificación, el suscriptor puede extraer la información directamente de ella. En este caso, el notificador debe pasarse a sí mismo a través del método de actualización. La opción menos flexible es vincular un notificador con el suscriptor de forma permanente a través del constructor.
- El cliente debe crear todos los suscriptores necesarios y registrarlos con los notificadores adecuados.

**Pros y contras**  
- ✅ Principio de abierto/cerrado. Puedes introducir nuevas clases suscriptoras sin tener que cambiar el código de la notificadora (y viceversa si hay una interfaz notificadora).
- ✅ Puedes establecer relaciones entre objetos durante el tiempo de ejecución.
- ❌ Los suscriptores son notificados en un orden aleatorio.

**Relaciones con otros patrones**  
- Chain of Responsibility, Command, Mediator y Observer abordan distintas formas de conectar emisores y receptores de solicitudes:
  - Chain of Responsibility pasa una solicitud secuencialmente a lo largo de una cadena dinámica de receptores potenciales hasta que uno de ellos la gestiona.
  - Command establece conexiones unidireccionales entre emisores y receptores.
  - Mediator elimina las conexiones directas entre emisores y receptores, forzándolos a comunicarse indirectamente a través de un objeto mediador.
  - Observer permite a los receptores suscribirse o darse de baja dinámicamente a la recepción de solicitudes.
- La diferencia entre Mediator y Observer a menudo resulta difusa. En la mayoría de los casos, puedes implementar uno de estos dos patrones; pero en ocasiones puedes aplicarlos ambos a la vez. Veamos cómo podemos hacerlo.
- La meta principal del patrón Mediator consiste en eliminar las dependencias mutuas entre un grupo de componentes del sistema. En su lugar, estos componentes se vuelven dependientes de un único objeto mediador. La meta del patrón Observer es establecer conexiones dinámicas de un único sentido entre objetos, donde algunos objetos actúan como subordinados de otros.
- Hay una implementación popular del patrón Mediator que se basa en el Observer. El objeto mediador juega el papel de notificador, y los componentes actúan como suscriptores que se suscriben o se dan de baja de los eventos del mediador. Cuando se implementa el Mediator de esta forma, puede asemejarse mucho al Observer.
- Cuando te sientas confundido, recuerda que puedes implementar el patrón Mediator de otras maneras. Por ejemplo, puedes vincular permanentemente todos los componentes al mismo objeto mediador. Esta implementación no se parece al Observer, pero aún así será una instancia del patrón Mediator.
- Ahora, imagina un programa en el que todos los componentes se hayan convertido en notificadores, permitiendo conexiones dinámicas entre sí. No hay un objeto mediador centralizado, tan solo un grupo distribuido de observadores.

## Implementación en Android/Kotlin para Observer
LiveData implementa Observer pattern lifecycle-aware: notifica solo cuando UI está activa. StateFlow para flows modernos:  
```kotlin
// En ViewModel
val data: StateFlow<List<Item>> = repository.getData().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

// En Fragment
viewModel.data.collect { updateUI(it) }
```
Ventajas: Reativo, evita memory leaks con LiveData. Desventajas: LiveData no soporta múltiples productores. StateFlow es más flexible pero requiere coroutines.

## Implementación en Android/Kotlin para Factory Method
