# 🎨 Interfaz Gráfica Personalizada - CRUD Dashboard

## Archivo: `index.html`

Una interfaz web moderna y completa para gestionar productos con operaciones CRUD.

---

## ⚡ Inicio Rápido (2 minutos)

### 1. Asegúrate que el servidor está ejecutándose
```powershell
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

### 2. Abre el archivo HTML
```
Opción A: Doble click en index.html
Opción B: Arrastra y suelta a navegador
Opción C: http://localhost:8000 (si usas servidor local)
```

### 3. ¡Comienza a usar!
- Crea un producto en el formulario
- Ver la tabla
- Edita o elimina

---

## 📋 Funcionalidades

| Función | Acción |
|---------|--------|
| ➕ **Crear** | Rellena formulario y haz clic en "Guardar" |
| 📋 **Listar** | Tabla muestra todos los productos |
| ✏️ **Editar** | Haz clic en "Editar" y modifica |
| 🗑️ **Eliminar** | Haz clic en "Eliminar" y confirma |
| 📊 **Estadísticas** | Ve totales en tiempo real |

---

## 🎨 Características

✅ **Diseño moderno** - Gradiente púrpura  
✅ **Responsivo** - Desktop, tablet, móvil  
✅ **Validaciones** - Campos requeridos  
✅ **Mensajes** - Error, éxito, información  
✅ **Estadísticas** - Total, stock, valor  
✅ **Modal de confirmación** - Para eliminar  
✅ **Color por stock** - Verde/Amarillo/Rojo  
✅ **Actualización automática** - Cada 5 segundos  

---

## 📖 Documentación Completa

Abre: `INSTRUCCIONES_GUI.md`

---

## 🚀 Ejemplo de Uso

### Crear Producto
```
1. Nombre: Laptop Dell
2. Descripción: Laptop de 13"
3. Precio: 1299.99
4. Stock: 5
5. Categoría: Electronics
6. Click en "Guardar"
```

### Editar Producto
```
1. En la tabla, haz clic en "✏️ Editar"
2. Modifica el precio (ej: 999.99)
3. Click en "Guardar"
```

### Eliminar Producto
```
1. En la tabla, haz clic en "🗑️ Eliminar"
2. Confirma en el modal
3. Producto eliminado
```

---

## ⚠️ Requisitos

- ✅ Servidor Spring Boot ejecutándose (puerto 8080)
- ✅ MongoDB conectado
- ✅ Navegador moderno (Chrome, Firefox, Edge, Safari)

---

## 🆘 Si algo falla

```
❌ "No se puede conectar"
→ Verifica que el servidor esté ejecutándose

❌ "Tabla vacía"
→ Haz clic en "🔄 Actualizar Lista"

❌ "Error en validación"
→ Revisa los campos marcados en rojo

❌ "Modal no cierra"
→ Presiona ESC o haz clic en Cancelar
```

---

**Para más detalles:** Lee `INSTRUCCIONES_GUI.md`

**¡Listo! Disfruta usando la interfaz! 🎉**
