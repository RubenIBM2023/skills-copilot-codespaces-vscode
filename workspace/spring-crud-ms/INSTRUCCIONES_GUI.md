# 🎨 GUÍA DE INTERFAZ GRÁFICA PERSONALIZADA

## Archivo: `index.html` - Dashboard CRUD de Productos

---

## ✨ Características

✅ **Interfaz moderna y responsiva**  
✅ **Formulario para crear productos**  
✅ **Tabla dinámica para listar productos**  
✅ **Edición de productos en línea**  
✅ **Eliminación con confirmación**  
✅ **Estadísticas en tiempo real**  
✅ **Validaciones de entrada**  
✅ **Mensajes de error y éxito**  
✅ **Verificación de conexión al servidor**  
✅ **Diseño degradado y moderno**  

---

## 🚀 Cómo Usar

### Paso 1: Asegurate que el servidor está ejecutándose

```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

Espera a ver:
```
Tomcat started on port(s): 8080
```

### Paso 2: Abre el archivo HTML

**Opción A - Doble click:**
```
Navega a: workspace/spring-crud-ms/index.html
Haz doble click
```

**Opción B - Navegador:**
```
Abre: http://localhost:8080 (si está configurado como servidor)
O arrastra y suelta el archivo index.html al navegador
```

**Opción C - Servidor local (Recomendado):**
```powershell
# En otra terminal, navega a la carpeta del proyecto
cd workspace/spring-crud-ms

# Usa Python para servir la carpeta
python -m http.server 8000

# Abre en navegador: http://localhost:8000
```

### Paso 3: ¡Comienza a usar!

---

## 📋 Funcionalidades Principales

### 1️⃣ Crear Producto

1. En el panel izquierdo "Crear / Editar Producto"
2. Rellena los campos:
   - **Nombre** (requerido, máx 100 caracteres)
   - **Descripción** (opcional, máx 500 caracteres)
   - **Precio** (requerido, ≥ 0)
   - **Stock** (requerido, ≥ 0)
   - **Categoría** (requerido)
3. Haz clic en "💾 Guardar Producto"
4. Verás un mensaje de éxito ✅

**Ejemplo:**
```
Nombre: Laptop Dell XPS 13
Descripción: Laptop ultraportátil con SSD 512GB
Precio: 1299.99
Stock: 5
Categoría: Electronics
```

### 2️⃣ Ver Lista de Productos

- La tabla se actualiza automáticamente
- Muestra:
  - Nombre del producto
  - Descripción
  - Precio (en verde)
  - Stock (con código de color)
  - Categoría (con badge)

**Interpretación del color de Stock:**
- 🟢 Verde (Alto): > 20 unidades
- 🟡 Amarillo (Medio): 5-20 unidades
- 🔴 Rojo (Bajo): < 5 unidades

### 3️⃣ Editar Producto

1. En la tabla, haz clic en "✏️ Editar"
2. El formulario se llena automáticamente
3. Modifica los campos que desees
4. Haz clic en "💾 Guardar Producto"
5. Verás confirmación ✅

### 4️⃣ Eliminar Producto

1. En la tabla, haz clic en "🗑️ Eliminar"
2. Se abre un modal pidiendo confirmación
3. Haz clic en "🗑️ Sí, Eliminar"
4. El producto se elimina ✅

### 5️⃣ Ver Estadísticas

En el panel derecho "Información":
- **Estado del servidor**: ✅ Online / ❌ Offline
- **Total de productos**: Cantidad
- **Stock total**: Unidades
- **Valor total**: Precio × Stock de todos

Se actualiza automáticamente cada 5 segundos.

---

## 🎨 Diseño Visual

### Colores
- 🟣 Púrpura: Tema principal (botones, encabezados)
- 🟢 Verde: Precios, stock alto
- 🔵 Azul: Enlaces, información
- 🔴 Rojo: Alertas, eliminación

### Responsivo
- ✅ Funciona en desktop
- ✅ Funciona en tablet
- ✅ Funciona en móvil

### Animaciones
- Fade-in para mensajes
- Slide-up para modales
- Hover effects en botones
- Transiciones suaves

---

## ⚠️ Validaciones

### Campos Requeridos
- Nombre (no puede estar vacío)
- Precio (no puede ser negativo)
- Stock (no puede ser negativo)
- Categoría (debe seleccionar)

### Límites
- Nombre: máximo 100 caracteres
- Descripción: máximo 500 caracteres
- Precio: decimales con 2 dígitos
- Stock: números enteros

### Respuestas del Servidor
- **201**: Producto creado ✅
- **200**: Producto actualizado ✅
- **204**: Producto eliminado ✅
- **400**: Validación fallida ❌
- **404**: Producto no encontrado ❌

---

## 💬 Mensajes

### Mensajes de Éxito (Verde)
```
✅ Producto creado correctamente
✅ Producto actualizado correctamente
✅ Producto eliminado correctamente
```

### Mensajes de Error (Rojo)
```
❌ No se puede conectar al servidor
❌ Error al cargar productos
❌ Error en la operación
```

### Mensajes de Información (Azul)
```
📝 Producto cargado para editar
⚠️ No se puede conectar al servidor
```

---

## 🔄 Flujo de Trabajo Típico

### Crear 3 Productos

1. **Crear Laptop:**
   - Nombre: Laptop Dell XPS 13
   - Descripción: Laptop ultraportátil
   - Precio: 1299.99
   - Stock: 5
   - Categoría: Electronics
   - Guardar ✅

2. **Crear Smartphone:**
   - Nombre: iPhone 15 Pro
   - Descripción: Smartphone flagship
   - Precio: 999.99
   - Stock: 15
   - Categoría: Electronics
   - Guardar ✅

3. **Crear T-Shirt:**
   - Nombre: T-Shirt Básica
   - Descripción: Camiseta de algodón
   - Precio: 24.99
   - Stock: 100
   - Categoría: Clothing
   - Guardar ✅

### Ver Lista

- La tabla muestra los 3 productos
- Estadísticas se actualizan:
  - Total: 3 productos
  - Stock: 120 unidades
  - Valor: $33,324.65

### Editar Uno

- Click en "✏️ Editar" en Laptop
- Cambiar precio a 1199.99
- Guardar ✅
- Tabla se actualiza
- Valor total cambia

### Eliminar Uno

- Click en "🗑️ Eliminar" en T-Shirt
- Confirmar eliminación
- Modal se cierra
- Tabla ahora muestra 2 productos

---

## 🛠️ Troubleshooting

### "❌ No se puede conectar al servidor"

**Causas:**
- Servidor no está ejecutándose
- Puerto 8080 está en uso
- Firewall bloquea la conexión

**Solución:**
```powershell
# Verificar que el servidor esté ejecutándose
# Terminal 1:
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar

# Terminal 2:
# Abre la GUI
```

### "No veo la tabla vacía al inicio"

**Esperado:**
- Cuando abres por primera vez, dice "No hay productos aún"
- Después de crear uno, aparece la tabla

### "Los cambios no se guardan"

**Verificar:**
1. ¿Ves el mensaje verde "✅ Producto creado"?
2. ¿El servidor dice "201" o "200"?
3. ¿Aparece el producto en la tabla?

Si no aparece en la tabla:
- Haz clic en "🔄 Actualizar Lista"
- Recarga la página (F5)

### "Obtengo error 400"

**Causas posibles:**
- Nombre vacío
- Precio o stock negativo
- Nombre > 100 caracteres
- Categoría no seleccionada

**Solución:**
- Revisa los campos en rojo
- Sigue las validaciones mostradas

### "Modal de confirmación no cierra"

**Solución:**
- Haz clic en "❌ Cancelar"
- O presiona ESC en el teclado

---

## 🎯 Casos de Uso

### Caso 1: Gestionar Inventario

1. Crear 5 productos diferentes
2. Ver estadísticas (total, stock, valor)
3. Actualizar stock cuando baja
4. Eliminar productos descontinuados

### Caso 2: Probar Validaciones

1. Intentar crear sin nombre (error)
2. Intentar crear con precio negativo (error)
3. Crear con nombre > 100 caracteres (error)
4. Crear válido (éxito)

### Caso 3: Editar en Lote

1. Crear 3 productos
2. Editar cada uno (cambiar precio)
3. Verificar cambios en tabla
4. Ver valor total actualizado

---

## 📱 Acceso Móvil

La interfaz es **responsiva** y funciona en:
- ✅ Desktop (navegador desktop)
- ✅ Tablet (iPad, tablets Android)
- ✅ Móvil (iPhone, Android)

En móvil:
- Botones más grandes para tocar
- Tabla se adapta
- Formulario de una columna

---

## 🔄 Actualización Automática

- **Estadísticas**: Se actualizan cada 5 segundos
- **Estado del servidor**: Se verifica al cargar
- **Lista de productos**: Se carga al abrir
- **Clic en "🔄 Actualizar Lista"**: Recarga manual

---

## ⌨️ Atajos de Teclado

| Tecla | Acción |
|-------|--------|
| ESC | Cierra modales |
| Ctrl+A | Selecciona todo |
| Tab | Navega entre campos |
| Enter | Envía formulario |

---

## 📊 API Endpoints Usados

La GUI llama a estos endpoints:

```
POST   /api/v1/products           → Crear
GET    /api/v1/products           → Listar
GET    /api/v1/products/{id}      → Obtener uno
PUT    /api/v1/products/{id}      → Actualizar
DELETE /api/v1/products/{id}      → Eliminar
```

---

## 🔒 Seguridad

⚠️ **Importante:**
- Esta GUI es solo para desarrollo local
- No uses en producción sin HTTPS
- No almacena datos en el navegador
- Todos los datos van al servidor

---

## 📞 Soporte

Si algo falla:

1. **Revisa la consola del navegador:**
   - F12 → Pestaña "Console"
   - Mira los mensajes de error

2. **Verifica el servidor:**
   - ¿Dice "Tomcat started on port(s): 8080"?
   - ¿MongoDB está ejecutándose?

3. **Recarga la página:**
   - F5 o Ctrl+R
   - Prueba de nuevo

4. **Revisa logs:**
   - Mira la consola donde ejecutas Java
   - Busca líneas rojas de error

---

## 📚 Documentación Relacionada

- [GUIA_PRUEBAS_GUI.md](../GUIA_PRUEBAS_GUI.md) - Todos los endpoints
- [QUICK_START.md](../QUICK_START.md) - Inicio rápido
- [README_PRUEBAS_GUI.md](./README_PRUEBAS_GUI.md) - Tutorial completo

---

**Archivo:** `index.html`  
**Fecha:** 2026-08-08  
**Versión:** 1.0.0  
**Estado:** ✅ Completado

---

## 🎉 ¡Listo!

**Abre:** `index.html` en tu navegador y comienza a probar! 🚀
