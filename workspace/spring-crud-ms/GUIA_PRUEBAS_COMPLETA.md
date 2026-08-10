# 🚀 GUÍA DE PRUEBAS - SPRING BOOT CRUD MICROSERVICE

## Requisitos Previos
- ✅ Java 17 (ya instalado en tu sistema)
- ✅ Maven (usado para compilar)
- ✅ MongoDB en puerto 27017 (o usa el servidor embebido de pruebas)
- ✅ Navegador web (Chrome, Firefox, Edge, etc.)

---

## 📋 MÉTODO 1: INTERFAZ GRÁFICA (GUI) - RECOMENDADO

La forma más fácil y visual de hacer pruebas.

### Paso 1: Compilar (solo una vez)

```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
mvn clean package -q
```

Esperado:
```
BUILD SUCCESS
Total time: ~60 segundos
```

### Paso 2: Iniciar la Aplicación

**Opción A - PowerShell (recomendado):**
```powershell
# Ejecutar el script automático
C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms\INICIA_APLICACION.ps1
```

**Opción B - CMD:**
```cmd
C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms\INICIA_APLICACION.bat
```

**Opción C - Línea de comandos manual:**
```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

### Paso 3: Esperar a que se inicie

Deberías ver:
```
...
2026-08-10T10:XX:XX... INFO ... Tomcat started on port(s): 8080 (http)
2026-08-10T10:XX:XX... INFO ... SpringCrudMsApplication started in X.XXX seconds
```

### Paso 4: Abrir la Interfaz en el Navegador

Tu navegador debería abrirse automáticamente en:
```
http://localhost:8080
```

Si no se abre automáticamente, manualmente escribe en la barra de direcciones:
```
http://localhost:8080
```

### Paso 5: Usar la Interfaz

Verás un dashboard con las siguientes opciones:

#### ➕ CREAR PRODUCTO

1. **Llenar el formulario:**
   - Nombre: `Laptop Dell XPS 13`
   - Descripción: `Laptop ultraportátil`
   - Precio: `1299.99`
   - Stock: `5`
   - Categoría: `Electronics`

2. **Hacer clic en "💾 Guardar"**

3. **Resultado esperado:**
   - ✅ Mensaje de éxito
   - 📝 Producto aparece en la tabla
   - 🔢 Total de productos se incrementa

#### 📋 VER LISTA DE PRODUCTOS

- Los productos aparecen automáticamente en la tabla
- Se actualiza cada 5 segundos
- Muestra: Nombre, Precio, Stock, Acciones

#### ✏️ EDITAR PRODUCTO

1. Haz clic en "✏️ Editar" en la fila del producto
2. El formulario se llena automáticamente
3. Modifica los valores (ej: cambiar precio de 1299.99 a 1199.99)
4. Haz clic en "💾 Guardar"
5. ✅ Producto actualizado en la tabla

#### 🔍 VER DETALLES

1. Haz clic en "👁️ Ver" en la fila del producto
2. Se abre una modal con todos los detalles:
   - ID
   - Nombre completo
   - Descripción
   - Precio
   - Stock
   - Categoría
   - Fecha de creación
   - Última actualización

#### 🗑️ ELIMINAR PRODUCTO

1. Haz clic en "🗑️ Eliminar" en la fila del producto
2. Confirmación: "¿Estás seguro?"
3. Haz clic en "Sí"
4. ✅ Producto eliminado de la tabla

#### 🔄 ACTUALIZACIÓN PARCIAL (PATCH)

Para actualizar solo precio y/o stock:

1. Haz clic en "✏️ Editar"
2. Modifica SOLO el precio o SOLO el stock
3. Deja los otros campos vacíos/sin cambios
4. Haz clic en "💾 Guardar"
5. ✅ Se actualiza solo los campos modificados

#### ℹ️ VER ESTADO DEL SERVIDOR

En la esquina superior derecha:
- **Estado: ✅ Online** - Servidor conectado
- **Puerto: 8080** - Puerto de la API
- **Botón Refrescar** - Actualiza manualmente la lista

---

## 📊 MÉTODO 2: USAR POSTMAN (API DIRECTO)

Para pruebas más técnicas de la API.

### Paso 1: Descargar Postman

Descarga desde: https://www.postman.com/downloads/

### Paso 2: Importar la Colección

1. Abre Postman
2. Click en "Import"
3. Selecciona el archivo: `Postman_Collection.json`
4. Se importarán todas las pruebas

### Paso 3: Usar las Pruebas

La colección incluye:

#### 1. CREAR PRODUCTOS
- `1.1 - Crear Laptop (Éxito)` → POST con datos válidos
- `1.2 - Crear Smartphone` → POST con otro producto
- `1.3 - Crear sin nombre` → POST sin campo obligatorio (400)
- `1.4 - Crear con precio negativo` → POST con validación fallida (400)

#### 2. OBTENER PRODUCTOS
- `2.1 - Listar todos` → GET /api/v1/products
- `2.2 - Listar con paginación` → GET con ?page=0&size=10
- `2.3 - Obtener por ID (Éxito)` → GET por ID válido
- `2.4 - Obtener por ID (No existe)` → GET por ID inválido (404)

#### 3. ACTUALIZAR PRODUCTOS
- `3.1 - Actualizar completo` → PUT con todos los campos
- `3.2 - Actualizar parcial` → PATCH con solo precio/stock
- `3.3 - Actualizar no existente` → PUT por ID inválido (404)

#### 4. ELIMINAR PRODUCTOS
- `4.1 - Eliminar existente` → DELETE por ID válido
- `4.2 - Eliminar no existente` → DELETE por ID inválido (404)

#### 5. SWAGGER
- `5.1 - OpenAPI JSON` → GET /api-docs
- `5.2 - Swagger UI` → Abre /swagger-ui.html

### Paso 4: Ejecutar Pruebas

Para cada request:
1. Haz clic en "Send"
2. Observa la respuesta
3. Verifica el status code esperado

---

## 🤖 MÉTODO 3: PRUEBAS AUTOMATIZADAS

Para ejecutar todas las pruebas de una vez.

### Opción A: Maven (tests automáticos)

```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
mvn test
```

Resultado esperado:
```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
```

### Opción B: Script de Pruebas

```powershell
# Si existe el script
C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms\pruebas_automatizadas.bat
```

---

## ✅ CASOS DE PRUEBA RECOMENDADOS

### PRUEBA 1: Flujo Completo CRUD

1. **CREATE**: Crear un nuevo producto
   - Nombre: `iPad Pro 12.9"`
   - Precio: `1099.99`
   - Stock: `3`
   - Categoría: `Electronics`

2. **READ**: Ver que aparece en la lista

3. **UPDATE**: Cambiar el precio a `999.99`

4. **PATCH**: Cambiar solo el stock a `10`

5. **DELETE**: Eliminar el producto

### PRUEBA 2: Validaciones

1. Intentar crear sin nombre → Debe fallar (400)
2. Intentar crear con precio negativo → Debe fallar (400)
3. Intentar crear con nombre > 100 caracteres → Debe fallar (400)

### PRUEBA 3: Manejo de Errores

1. Intentar obtener producto ID inexistente → 404
2. Intentar actualizar producto ID inexistente → 404
3. Intentar eliminar producto ID inexistente → 404

### PRUEBA 4: Paginación

1. Crear 15 productos
2. Obtener productos con `?page=0&size=10`
3. Obtener productos con `?page=1&size=10`
4. Verificar que se devuelven correctamente

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### El navegador no abre automáticamente
→ Abre manualmente: http://localhost:8080

### Error: "No se encontró el JAR compilado"
→ Ejecuta primero: `mvn clean package -q`

### Error: "Puerto 8080 ya está en uso"
→ Cierra la aplicación anterior o usa otro puerto:
```
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar --server.port=8081
```

### MongoDB no conecta
→ Asegúrate que MongoDB está corriendo en localhost:27017
→ O usa Flapdoodle (embebido) que se inicia automáticamente

### La GUI no carga
→ Recarga la página: Ctrl+F5
→ Verifica que la consola no muestre errores
→ Comprueba que: http://localhost:8080/api/v1/products responde

---

## 📝 NOTAS IMPORTANTES

- La aplicación se inicia en puerto **8080**
- MongoDB se conecta a `mongodb://localhost:27017/products_db`
- Los tests incluyen **17 pruebas unitarias e integración**
- Cobertura de código: **>80%** en service y controller
- Checkstyle: **0 violaciones** (Google Java Style)

---

## 🎯 ENDPOINTS DISPONIBLES

```
POST   /api/v1/products          → Crear producto (201)
GET    /api/v1/products          → Listar productos (200)
GET    /api/v1/products/{id}     → Obtener por ID (200)
PUT    /api/v1/products/{id}     → Actualizar completo (200)
PATCH  /api/v1/products/{id}     → Actualizar parcial (200)
DELETE /api/v1/products/{id}     → Eliminar (204)

GET    /swagger-ui.html          → Interfaz Swagger (UI)
GET    /api-docs                 → OpenAPI JSON
GET    /                         → GUI Dashboard
```

---

## 📞 SOPORTE

Si encuentras problemas:
1. Revisa los logs en la consola
2. Verifica que el puerto 8080 esté disponible
3. Asegúrate que MongoDB está corriendo
4. Reinicia la aplicación

¡Happy Testing! 🎉

---

**Generado por OpenCode Agent - Fecha: 2026-08-10**
