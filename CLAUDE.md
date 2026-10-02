# Gestión Distribuidora

Aplicación de escritorio en Java 21 + JavaFX 21 (LTS) con Maven y SQLite.

## Comandos

- Ejecutar la aplicación: `mvn javafx:run`
- Compilar: `mvn compile`
- Pruebas: `mvn test`

## Estructura

- `com.distribuidora.modelo`: entidades (p. ej. `Producto`)
- `com.distribuidora.dao`: acceso a datos (`ConexionDB` y los DAO)
- `com.distribuidora.controlador`: controllers de JavaFX
- `src/main/resources/com/distribuidora/vista`: archivos FXML

## Convenciones

### Idioma
- Comentarios, mensajes de error y textos de la interfaz siempre en español.

### Acceso a datos
- Usar el patrón DAO para todo el acceso a datos, igual que en los demás proyectos del autor: un DAO por entidad, que crea su tabla si no existe y expone métodos como `insertar`, `consultarPorId` y `listar`.
- Las conexiones se obtienen de `ConexionDB` y se cierran con try-with-resources. Usar siempre `PreparedStatement` con parámetros, nunca SQL concatenado.
- La base de datos se llama `distribuidora.db` y debe estar en `.gitignore`.

### Pantallas
- Cada pantalla se separa en un archivo FXML (vista) y una clase Controller (lógica).
- Nunca mezclar diseño visual con lógica de negocio: el FXML no contiene lógica, y el controller no construye la interfaz ni contiene reglas de negocio ni SQL; delega en clases de servicio o en los DAO.

### Tipos de datos
- Usar `BigDecimal` para precios e importes, nunca `double` ni `float`.

### Pruebas
- Toda nueva funcionalidad debe incluir pruebas unitarias donde sea posible.
- La lógica de negocio y los DAO sí deben tener pruebas. Los controllers de JavaFX no es necesario probarlos.

### Commits
- Antes de hacer commit, ejecutar `mvn test` y confirmar que todas las pruebas pasan. Si alguna falla, no hacer commit: corregir primero.
- Después de completar cada tarea, hacer commit automáticamente con un mensaje descriptivo en español, sin esperar confirmación adicional.
