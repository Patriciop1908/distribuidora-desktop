# Gestión Distribuidora

Aplicación de escritorio para que una tienda o distribuidora registre sus **productos** y **proveedores**. Está hecha con **Java 21 y JavaFX 21**, y guarda los datos en **SQLite**.

![Java](https://img.shields.io/badge/Java-21-orange) ![JavaFX](https://img.shields.io/badge/JavaFX-21-blue) ![SQLite](https://img.shields.io/badge/SQLite-3-blue) ![Tests](https://img.shields.io/badge/tests-JUnit-success)

<!-- Añade aquí una captura de la aplicación: ![Pantalla principal](docs/captura.png) -->

## Funcionalidades

- **Productos:** registrar nombre, precio, stock y el proveedor que lo suministra, elegido de una lista desplegable.
- **Proveedores:** registrar nombre, persona de contacto, teléfono y email.
- **Validaciones con mensajes claros para el usuario:** campos obligatorios, precio y stock no negativos, teléfono y email válidos, y sin nombres duplicados.
- **Los nombres no distinguen mayúsculas, ni siquiera con tildes o ñ:** "Café" y "CAFÉ" se reconocen como el mismo producto.
- **Interfaz con pestañas** (Productos / Proveedores) y mensajes de confirmación y de error.

## Tecnologías

| Área | Herramientas |
|---|---|
| Lenguaje | Java 21 (LTS) |
| Interfaz gráfica | JavaFX 21 + FXML + CSS |
| Persistencia | SQLite con JDBC y el patrón DAO |
| Pruebas | JUnit 5 |
| Construcción | Maven (`javafx-maven-plugin`) |

## Arquitectura

Cada pantalla separa el **diseño** (archivo FXML) de la **lógica** (controller), y las reglas de negocio viven en una capa de servicios aparte:

```
Vista (FXML + CSS)  →  Controller  →  Servicio  →  DAO  →  SQLite
                       (eventos de    (validaciones  (JDBC,
                        la interfaz)   y reglas)      SQL)
```

```
src/main/java/com/distribuidora/
├── modelo/        Entidades: Producto, Proveedor
├── dao/           Acceso a datos: ConexionDB, ProductoDAO, ProveedorDAO
├── servicio/      Lógica de negocio y validaciones
└── controlador/   Controllers de JavaFX
src/main/resources/com/distribuidora/vista/
├── principal.fxml       Ventana principal con pestañas
├── producto-form.fxml   Formulario de productos
├── proveedor-form.fxml  Formulario de proveedores
└── estilos.css
```

Algunas decisiones de diseño:
- **Consultas siempre con `PreparedStatement`**, nunca con SQL concatenado, para evitar inyección SQL.
- **Precios con `BigDecimal`** para no perder céntimos por redondeo.
- **Relación producto → proveedor con clave foránea** (`PRAGMA foreign_keys = ON`).
- **Migraciones sin pérdida de datos:** si cambia una tabla, el DAO actualiza la base de datos existente del usuario en lugar de recrearla.

## Cómo ejecutarlo

Requisitos: **JDK 21** y **Maven**.

```bash
git clone https://github.com/Patriciop1908/distribuidora-desktop.git
cd distribuidora-desktop
mvn javafx:run
```

La base de datos `distribuidora.db` se crea automáticamente la primera vez que se abre la aplicación.

## Pruebas

```bash
mvn test
```

Hay pruebas unitarias de los **DAO** (inserción, consultas, la relación con proveedores y la migración desde un esquema antiguo) y de los **servicios** (todas las validaciones de datos).

## Próximos pasos

- Tablas con el listado de productos y proveedores, con búsqueda y filtros.
- Edición y borrado de registros.
- Versión web y móvil a partir de la misma lógica de negocio.

## Autor

**Gustavo Patricio Pérez Chávez** · Estudiante del Máster en Ingeniería Informática (UCM)
[LinkedIn](https://www.linkedin.com/in/gustavo-patricio-perez-chavez-4a16603a9) · [GitHub](https://github.com/Patriciop1908)
