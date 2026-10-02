# Gestión Distribuidora

Aplicación de escritorio en Java 21 + JavaFX 21 (LTS) con Maven y SQLite.

## Comandos

- Ejecutar la aplicación: `mvn javafx:run`
- Compilar: `mvn compile`
- Pruebas: `mvn test`

## Estructura

- `com.distribuidora.modelo`: entidades (`Producto`, `Proveedor`)
- `com.distribuidora.dao`: acceso a datos (`ConexionDB` y los DAO)
- `com.distribuidora.servicio`: lógica de negocio y validaciones (lanzan `ValidacionException` con mensajes para el usuario)
- `com.distribuidora.controlador`: controllers de JavaFX (`PrincipalController` gestiona las pestañas)
- `src/main/resources/com/distribuidora/vista`: archivos FXML y `estilos.css`. `principal.fxml` es la ventana principal: un `TabPane` que incluye cada formulario con `fx:include`.

## Convenciones

### Idioma
- Comentarios, mensajes de error y textos de la interfaz siempre en español.

### Acceso a datos
- Usar el patrón DAO para todo el acceso a datos, igual que en los demás proyectos del autor: un DAO por entidad, que crea su tabla si no existe y expone métodos como `insertar`, `consultarPorId` y `listar`.
- Las conexiones se obtienen de `ConexionDB` y se cierran con try-with-resources. Usar siempre `PreparedStatement` con parámetros, nunca SQL concatenado.
- La base de datos se llama `distribuidora.db` y debe estar en `.gitignore`.
- `ConexionDB` activa las claves foráneas (`PRAGMA foreign_keys = ON`) y registra la función `minusculas()`. Para comparar textos sin distinguir mayúsculas usar `minusculas(columna) = minusculas(?)`, no `LOWER()` ni `COLLATE NOCASE`, que no convierten letras acentuadas ni la ñ.
- Si se cambia una tabla que ya existe, el DAO debe migrarla en `crearTablaSiNoExiste` (p. ej. comprobar con `PRAGMA table_info` y hacer `ALTER TABLE`) sin perder datos, porque los usuarios ya tienen un `distribuidora.db`. Añadir una prueba que parta del esquema antiguo.

### Pantallas
- Cada pantalla se separa en un archivo FXML (vista) y una clase Controller (lógica).
- Nunca mezclar diseño visual con lógica de negocio: el FXML no contiene lógica, y el controller no construye la interfaz ni contiene reglas de negocio ni SQL; delega en clases de servicio o en los DAO.
- Los estilos van en `estilos.css` (clases `titulo-formulario`, `mensaje-exito`, `mensaje-error`), no en línea. Los mensajes de confirmación y error se muestran con `Mensajes.mostrarExito` / `Mensajes.mostrarError`.

### Tipos de datos
- Usar `BigDecimal` para precios e importes, nunca `double` ni `float`.

### Pruebas
- Toda nueva funcionalidad debe incluir pruebas unitarias donde sea posible.
- La lógica de negocio y los DAO sí deben tener pruebas. Los controllers de JavaFX no es necesario probarlos.

### Commits
- Antes de hacer commit, ejecutar `mvn test` y confirmar que todas las pruebas pasan. Si alguna falla, no hacer commit: corregir primero.
- Después de completar cada tarea, hacer commit automáticamente con un mensaje descriptivo en español, sin esperar confirmación adicional.
