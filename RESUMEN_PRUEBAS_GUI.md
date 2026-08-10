# 📊 RESUMEN EJECUTIVO - Pruebas con GUI

## REQ-2026-08-08-001-spring-crud-ms: Microservicio CRUD de Productos

---

## ✨ Lo que se ha preparado para ti

### 📚 Documentos Incluidos

1. **GUIA_PRUEBAS_GUI.md** (Este repositorio raíz)
   - Guía completa de 300+ líneas
   - Todos los endpoints documentados
   - Casos de prueba con JSON de ejemplo
   - Troubleshooting

2. **README_PRUEBAS_GUI.md** (En la carpeta del proyecto)
   - Tutorial paso a paso
   - Instrucciones por herramienta
   - Checklist de validación

3. **CASOS_PRUEBA_DETALLADOS.md** (Raíz)
   - Especificación de cada caso de prueba
   - Matriz de cobertura
   - Validaciones por módulo

4. **Postman_Collection.json** (Carpeta del proyecto)
   - 20+ requests preconfigurados
   - Ejemplos listos para usar
   - Importable en Postman

### 🛠️ Herramientas Configuradas

1. **Script Automatizado:** `pruebas_automatizadas.bat`
   - Ejecuta todas las pruebas
   - Genera reporte en log

2. **Swagger UI Integrada**
   - Acceso directo sin herramientas
   - Documentación interactiva

3. **Ejemplos JSON Listos**
   - Para cada endpoint
   - Validaciones incluidas

---

## 🚀 Inicio Rápido (5 minutos)

### Paso 1: Ejecutar Servidor
```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

### Paso 2: Abrir Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### Paso 3: Hacer Primera Prueba
- Click en POST /api/v1/products
- Click en "Try it out"
- Pega datos y haz "Execute"
- ¡Status 201 = Éxito!

---

## 📊 Cobertura de Pruebas

### Endpoints Documentados (6/6 = 100%)

```
✅ POST   /api/v1/products          → Crear
✅ GET    /api/v1/products          → Listar
✅ GET    /api/v1/products/{id}     → Obtener
✅ PUT    /api/v1/products/{id}     → Actualizar
✅ PATCH  /api/v1/products/{id}     → Parcial
✅ DELETE /api/v1/products/{id}     → Eliminar
```

### Validaciones Incluidas (9/9 = 100%)

```
✅ Nombre requerido
✅ Nombre máx 100 chars
✅ Descripción máx 500 chars
✅ Precio ≥ 0
✅ Stock ≥ 0
✅ Categoría requerida
✅ Tipo de dato price (BigDecimal)
✅ Tipo de dato stock (Integer)
✅ Paginación con page y size
```

### Errores Manejados (5/5 = 100%)

```
✅ 201 Created          → Creación exitosa
✅ 200 OK               → Lectura/Actualización exitosa
✅ 204 No Content       → Eliminación exitosa
✅ 400 Bad Request      → Validación fallida
✅ 404 Not Found        → Producto no existe
```

### Módulos Testeable (5/5 = 100%)

```
✅ ProductController     (6 endpoints)
✅ ProductService       (CRUD + mapeo)
✅ ProductRepository    (MongoDB queries)
✅ GlobalExceptionHandler (Manejo de errores)
✅ DTOs + Entidades    (Validaciones + mapeos)
```

---

## 🎯 Herramientas Recomendadas

### Para Principiantes: Swagger UI ⭐⭐⭐⭐⭐
- ✅ No requiere instalación
- ✅ Acceso inmediato
- ✅ Documentación integrada
- ❌ Funcionalidad limitada

```
Acceso: http://localhost:8080/swagger-ui.html
```

### Para Profesionales: Postman ⭐⭐⭐⭐⭐
- ✅ Colección incluida
- ✅ Tests automáticos
- ✅ Historial y variables
- ❌ Requiere instalación

```
Importar: Postman_Collection.json
```

### Para Rápido: Script Automatizado ⭐⭐⭐⭐
- ✅ Ejecutar todo de una vez
- ✅ Genera reporte
- ✅ 11+ casos de prueba
- ❌ Menos flexible

```
Ejecutar: .\pruebas_automatizadas.bat
```

---

## 📋 Flujo de Pruebas Sugerido

### Sesión 1: Crear y Listar (20 min)
```
1. POST /api/v1/products → Crear Laptop
2. POST /api/v1/products → Crear Smartphone
3. POST /api/v1/products → Crear T-Shirt
4. GET  /api/v1/products → Listar todos
5. GET  /api/v1/products?page=0&size=10 → Listar con paginación
```

### Sesión 2: Obtener y Actualizar (20 min)
```
1. GET    /api/v1/products/{id} → Obtener producto
2. PUT    /api/v1/products/{id} → Actualizar completo
3. PATCH  /api/v1/products/{id} → Actualizar solo precio
4. PATCH  /api/v1/products/{id} → Actualizar solo stock
5. GET    /api/v1/products/{id} → Verificar cambios
```

### Sesión 3: Validaciones (20 min)
```
1. POST con nombre vacío → Esperar 400
2. POST con precio negativo → Esperar 400
3. POST con stock negativo → Esperar 400
4. POST con nombre > 100 chars → Esperar 400
5. POST con descripción > 500 chars → Esperar 400
```

### Sesión 4: Errores y Limpieza (20 min)
```
1. GET  /api/v1/products/invalid → Esperar 404
2. DELETE /api/v1/products/{id} → Eliminar
3. GET  /api/v1/products/{id} → Verificar 404
4. DELETE /api/v1/products/{id} → Eliminar otro
5. GET  /api/v1/products → Verificar lista reducida
```

---

## 🔍 Verificación por Módulo

### ✅ Módulo: ProductController
- [ ] Todos los 6 endpoints responden
- [ ] Status codes correctos
- [ ] Validaciones en lugar

### ✅ Módulo: ProductService
- [ ] CRUD completo funciona
- [ ] DTOs se mapean correctamente
- [ ] Lógica de negocio se ejecuta

### ✅ Módulo: ProductRepository
- [ ] Datos se persisten en MongoDB
- [ ] Queries funcionan correctamente
- [ ] Relaciones se mantienen

### ✅ Módulo: GlobalExceptionHandler
- [ ] Errores retornan JSON correcto
- [ ] Status codes apropiados
- [ ] Mensajes son descriptivos

### ✅ Módulo: DTOs + Entidades
- [ ] Validaciones funcionan
- [ ] Timestamps se auto-gestionan
- [ ] Tipos de dato correctos

---

## 📈 Métricas de Éxito

Para considerar las pruebas **exitosas**, verifica:

| Métrica | Esperado | Tu Resultado |
|---------|----------|--------------|
| Endpoints funcionales | 6/6 | ___/6 |
| Validaciones activas | 9/9 | ___/9 |
| Códigos HTTP correctos | 5/5 | ___/5 |
| Errores manejados | 5/5 | ___/5 |
| JSON bien formado | 100% | ___% |
| Datos persistidos | Sí | ___ |
| Timestamps auto-gestionados | Sí | ___ |

---

## 🆘 Ayuda Rápida

### "¿No veo respuesta?"
→ Verificar que el servidor está ejecutándose
→ Ver logs en consola

### "¿Falla validación?"
→ Revisar valores de entrada
→ Consultar casos de prueba en documento

### "¿MongoDB no conecta?"
→ Ejecutar `net start MongoDB` (Admin)
→ O usar MongoDB integrado (Flapdoodle)

### "¿Status 500?"
→ Revisar logs del servidor
→ Verificar application.properties
→ Recompilar proyecto

---

## 📞 Próximos Pasos

1. **Completar pruebas básicas** (Este documento)
2. **Ejecutar tests unitarios:**
   ```bash
   mvn test
   ```
3. **Verificar cobertura:**
   ```bash
   mvn jacoco:report
   open target/site/jacoco/index.html
   ```
4. **Pasar validaciones:**
   ```bash
   mvn checkstyle:check
   mvn compile
   ```

---

## 📊 Archivos Incluidos

```
workspace/spring-crud-ms/
├── README_PRUEBAS_GUI.md          ← Tutorial paso a paso
├── Postman_Collection.json        ← 20+ requests preconfigurados
├── pruebas_automatizadas.bat      ← Script de pruebas
├── pom.xml                         ← Configuración Maven
├── src/
│   ├── main/
│   │   ├── java/.../controller/ProductController.java
│   │   ├── java/.../service/ProductService.java
│   │   ├── java/.../repository/ProductRepository.java
│   │   ├── java/.../exception/GlobalExceptionHandler.java
│   │   ├── java/.../model/Product.java
│   │   ├── java/.../dto/*.java
│   │   └── resources/application.properties
│   └── test/
│       └── java/.../test/*.java
├── target/
│   └── spring-crud-ms-1.0.0-SNAPSHOT.jar
└── ...

../
├── GUIA_PRUEBAS_GUI.md            ← Guía completa (300+ líneas)
├── CASOS_PRUEBA_DETALLADOS.md     ← Especificación de cada caso
└── README_PRUEBAS_GUI.md          ← README principal
```

---

## 🎓 Lo que Aprenderás

Al completar estas pruebas, habrás validado:

✅ **Arquitectura:** Capas MVC funcionando  
✅ **REST API:** Todos los verbos HTTP  
✅ **Validaciones:** Bean Validation en acción  
✅ **Persistencia:** MongoDB almacenando datos  
✅ **Manejo de errores:** Excepciones capturadas  
✅ **Documentación:** OpenAPI/Swagger  
✅ **Pruebas GUI:** Verificación manual e integrada  

---

## ✨ Resumen

Tienes **3 formas** de probar:

1. **Swagger UI** → Más fácil (navegador)
2. **Postman** → Más potente (colección incluida)
3. **Script** → Más rápido (todo de una vez)

Elige la que prefieras y **¡comienza a probar!**

---

**Fecha:** 2026-08-08  
**Estado:** ✅ LISTO PARA USAR  
**Versión:** 1.0.0

---

### 🎯 Tu siguiente paso:
```
1. Abre http://localhost:8080/swagger-ui.html
2. Haz clic en POST /api/v1/products
3. Haz clic en "Try it out"
4. ¡Crea tu primer producto!
```
