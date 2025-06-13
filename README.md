# SplitPay - Sistema de Gestión de Gastos Compartidos

Este proyecto permite gestionar de forma sencilla los gastos compartidos entre grupos de personas, proporcionando funcionalidades de seguimiento, facturación, distribución de deudas y más.

## Tabla de Contenidos
- [Descripción del Proyecto](#descripción-del-proyecto)
- [Características Principales](#Características-Principales)
- [Requisitos del Sistema](#Requisitos-del-Sistema)
- [Configuración](#Configuración)
- [Uso](#Uso)
- [Estructura del Código](#Estructura-del-Código)

## Descripción del Proyecto

SplitPay es una aplicación diseñada para facilitar el seguimiento y la gestión de gastos compartidos entre grupos de personas. 
La aplicación permite a los usuarios crear grupos, registrar facturas, distribuir deudas entre miembros, realizar transacciones y visualizar el historial de actividades. 
SplitPay está desarrollada en Java y utiliza una base de datos Oracle para almacenar la información.

## Características Principales

- **Autenticación de Usuarios**: Los usuarios pueden registrarse e iniciar sesión con su correo electrónico y contraseña.
- **Gestión de Grupos**: Creación y administración de grupos, incluyendo la asignación de roles como "Líder" o "Miembro".
- **Registro de Facturas**: Los usuarios pueden registrar facturas asociadas a un grupo, incluyendo detalles como monto, descripción y ubicación.
- **Distribución de Deudas**: La aplicación calcula automáticamente las deudas entre miembros del grupo.
- **Transacciones**: Los usuarios pueden realizar transacciones para liquidar deudas.
- **Notificaciones**: Envío de notificaciones sobre nuevas facturas, deudas y transacciones.
- **Visualización de Datos**: Consultas para visualizar balances, historial de transacciones y estados de deudas.

## Requisitos del Sistema

- **Java JDK**: Versión 8 o superior.
- **Oracle Database**: Configurada con las tablas y relaciones especificadas en el script Bases de datos proyecto 3.sql.
- **Bibliotecas**:
  -ojdbc11.jar para la conexión con Oracle.
  -Bibliotecas de JavaFX para la interfaz gráfica (si se utiliza).

## Configuración

1. **Base de Datos**:

- Ejecutar el script SQL proporcionado **(Bases de datos proyecto 3.sql)** para crear las tablas y relaciones necesarias.

- Asegurarse de que la base de datos esté accesible y las credenciales sean correctas **(modificar en Constante.java si es necesario)**.

2. **Ejecución**:

- Compilar y ejecutar la clase principal PROYECTO3.java.

- Asegurarse de que todas las dependencias estén correctamente configuradas en el proyecto.

## Uso

1. **Inicio de Sesión**:

- Ingresar el correo electrónico y contraseña registrados en la base de datos.
- Ejemplo: juan.perez@example.com con contraseña pass1234.

2. **Menú Principal**:

- **Ver Grupos y Deudas**: Muestra los grupos a los que pertenece el usuario y sus deudas pendientes.
- **Crear Factura**: Permite registrar una nueva factura asociada a un grupo.
- **Crear Grupo**: Crea un nuevo grupo y asigna miembros.
- **Realizar Transacción**: Permite liquidar deudas entre miembros del grupo.
- **Administrar Grupo (solo líderes)**: Opciones para agregar miembros, asignar líderes o disolver deudas.

3. **Consultas**:

- Las consultas SQL predefinidas permiten visualizar información detallada, como balances, historial de transacciones y notificaciones.

## Estructura del Código

- **PROYECTO3.java**: Clase principal que gestiona la autenticación y el menú de la aplicación.

- **Constante.java**: Almacena las credenciales de conexión a la base de datos.

- **Métodos Auxiliares**:

   - **autenticar**: Verifica las credenciales del usuario.
   - **crearFacturaMenu**: Registra una nueva factura.
   - **transaccion**: Realiza una transacción entre usuarios.
   - **adminGrupo**: Funcionalidades exclusivas para líderes de grupo.


