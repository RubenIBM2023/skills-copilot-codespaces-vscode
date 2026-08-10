# 📖 ÍNDICE MAESTRO - Pruebas con Interfaz Gráfica

**REQ-2026-08-08-001-spring-crud-ms: Microservicio CRUD de Productos**

---

## 📚 Documentación Disponible

### 🎯 Punto de Partida (Comienza aquí)

1. **RESUMEN_PRUEBAS_GUI.md** ← **COMIENZA AQUÍ**
   - Descripción general
   - Lo que se ha preparado
   - Inicio rápido (5 minutos)
   - Verificación rápida

### 🚀 Guías Prácticas

2. **workspace/spring-crud-ms/README_PRUEBAS_GUI.md**
   - Tutorial paso a paso
   - Instrucciones por herramienta
   - Preguntas frecuentes
   - Troubleshooting

3. **GUIA_PRUEBAS_GUI.md**
   - Guía completa (300+ líneas)
   - Todos los endpoints documentados
   - Casos de prueba con JSON de ejemplo
   - Validaciones por módulo
   - Checklist final

### 📊 Especificaciones Técnicas

4. **CASOS_PRUEBA_DETALLADOS.md**
   - Especificación de cada caso de prueba
   - Matriz de cobertura
   - Validaciones por módulo (ProductController, ProductService, etc.)
   - 46+ casos de prueba documentados

5. **DIAGRAMA_FLUJOS.md**
   - Arquitectura del sistema (ASCII diagrams)
   - Flujo de operaciones CRUD
   - Manejo de excepciones
   - Ciclo de vida de requests
   - Matriz de pruebas

### 🛠️ Herramientas Listas para Usar

6. **workspace/spring-crud-ms/Postman_Collection.json**
   - 20+ requests preconfigurados
   - Ejemplos listos para copiar/pegar
   - Importable en Postman
   - Todos los endpoints incluidos

7. **workspace/spring-crud-ms/pruebas_automatizadas.bat**
   - Script Windows automatizado
   - Ejecuta 11+ pruebas
   - Genera reporte en log

---

## 🗺️ Mapa de Navegación

```
┌─ INICIO RÁPIDO
│  └─ RESUMEN_PRUEBAS_GUI.md
│
├─ ELEGIR HERRAMIENTA
│  ├─ Opción 1: Swagger UI (Browser, más fácil)
│  ├─ Opción 2: Postman (Más potente)
│  ├─ Opción 3: Insomnia (Ligero)
│  └─ Opción 4: Script (Automatizado)
│
├─ TUTORIALES PASO A PASO
│  └─ README_PRUEBAS_GUI.md
│
├─ DETALLES TÉCNICOS
│  ├─ GUIA_PRUEBAS_GUI.md (endpoints, JSON)
│  ├─ CASOS_PRUEBA_DETALLADOS.md (especificación)
│  └─ DIAGRAMA_FLUJOS.md (arquitectura)
│
├─ RECURSOS
│  ├─ Postman_Collection.json (descarga aquí)
│  └─ pruebas_automatizadas.bat (ejecuta aquí)
│
└─ VALIDACIÓN
   ├─ Checklist en README_PRUEBAS_GUI.md
   ├─ Matriz de pruebas en CASOS_PRUEBA_DETALLADOS.md
   └─ Verification en RESUMEN_PRUEBAS_GUI.md
```

---

## 📋 Por Nivel de Experiencia

### 👶 Principiante (Primera vez)
**Tiempo recomendado:** 1 hora

1. Leer: **RESUMEN_PRUEBAS_GUI.md** (5 min)
2. Leer: **README_PRUEBAS_GUI.md** - Sección Swagger UI (10 min)
3. Abrir: `http://localhost:8080/swagger-ui.html`
4. Hacer: Primera prueba POST (5 min)
5. Hacer: Probar resto de endpoints (30 min)
6. Leer: **GUIA_PRUEBAS_GUI.md** - Casos de validación (10 min)

### 🎓 Intermedio (Conoce REST)
**Tiempo recomendado:** 1.5 horas

1. Leer: **RESUMEN_PRUEBAS_GUI.md** (5 min)
2. Leer: **README_PRUEBAS_GUI.md** - Sección Postman (15 min)
3. Importar: **Postman_Collection.json** en Postman
4. Ejecutar: Flujo completo de pruebas (30 min)
5. Leer: **CASOS_PRUEBA_DETALLADOS.md** (20 min)
6. Crear: 2-3 colecciones personalizadas en Postman (25 min)

### 🏆 Avanzado (Experto)
**Tiempo recomendado:** 45 minutos

1. Leer: **CASOS_PRUEBA_DETALLADOS.md** (10 min)
2. Leer: **DIAGRAMA_FLUJOS.md** (10 min)
3. Ejecutar: **pruebas_automatizadas.bat** (2 min)
4. Crear: Tests automáticos en Postman con variables (20 min)
5. Validar: Cobertura de pruebas vs especificación (3 min)

---

## 🎯 Búsqueda Rápida

### "Quiero..."

#### "...probar un endpoint específico"
→ **GUIA_PRUEBAS_GUI.md** - Módulo 1: ProductController

#### "...entender la arquitectura"
→ **DIAGRAMA_FLUJOS.md** - Sección Arquitectura del Sistema

#### "...ver todos los casos de prueba"
→ **CASOS_PRUEBA_DETALLADOS.md** - Matriz de cobertura

#### "...usando Postman"
→ **README_PRUEBAS_GUI.md** - Opción 2: Postman (Recomendado)

#### "...sin instalar nada"
→ **README_PRUEBAS_GUI.md** - Opción 1: Swagger UI

#### "...solucionar un problema"
→ **README_PRUEBAS_GUI.md** - Troubleshooting

#### "...ver ejemplos JSON"
→ **GUIA_PRUEBAS_GUI.md** - Módulo 1, Casos de Prueba

#### "...hacer pruebas automáticas"
→ **README_PRUEBAS_GUI.md** - Opción 4: Script

---

## 📊 Estructura de Documentos

```
Raíz del Repositorio
├── RESUMEN_PRUEBAS_GUI.md ⭐ COMIENZA AQUÍ
├── GUIA_PRUEBAS_GUI.md (referencia completa)
├── CASOS_PRUEBA_DETALLADOS.md (especificación)
├── DIAGRAMA_FLUJOS.md (arquitectura)
├── INDICE_MAESTRO.md ← TÚ ESTÁS AQUÍ
│
└── workspace/spring-crud-ms/
    ├── README_PRUEBAS_GUI.md (tutorial práctico)
    ├── Postman_Collection.json (20+ requests)
    ├── pruebas_automatizadas.bat (script)
    ├── pom.xml
    ├── src/
    │   ├── main/java/
    │   │   ├── .../controller/ProductController.java
    │   │   ├── .../service/ProductService.java
    │   │   ├── .../repository/ProductRepository.java
    │   │   ├── .../exception/GlobalExceptionHandler.java
    │   │   ├── .../model/Product.java
    │   │   └── .../dto/
    │   │       ├── ProductRequestDTO.java
    │   │       ├── ProductResponseDTO.java
    │   │       ├── ProductPatchDTO.java
    │   │       └── ErrorResponseDTO.java
    │   └── test/java/
    │       ├── .../test/ProductServiceTest.java
    │       └── .../test/ProductControllerTest.java
    ├── target/
    │   └── spring-crud-ms-1.0.0-SNAPSHOT.jar
    └── server.log
```

---

## 📱 Métodos de Prueba Comparados

| Aspecto | Swagger UI | Postman | Insomnia | Script |
|---------|-----------|---------|----------|--------|
| **Instalación** | No (Web) | Necesaria | Necesaria | No |
| **Facilidad** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ |
| **Potencia** | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Documentación** | ✅ Integrada | ✅ Colección | ⭐ Manual | ⭐ Código |
| **Tests Auto.** | ❌ No | ✅ Sí | ✅ Sí | ✅ Sí |
| **Variables** | ❌ No | ✅ Sí | ✅ Sí | ⭐ Limited |
| **Historial** | ⭐ Browser | ✅ Sí | ✅ Sí | ⭐ Log |
| **Reutilizable** | ❌ No | ✅ Sí | ✅ Sí | ✅ Sí |
| **Curva Aprend.** | Muy rápida | Rápida | Media | Media |

**Recomendado para principiantes:** Swagger UI  
**Recomendado para profesionales:** Postman  
**Recomendado para automatización:** Script

---

## ✅ Checklist de Lectura

- [ ] Leí RESUMEN_PRUEBAS_GUI.md
- [ ] Entiendo las 3 opciones de herramientas
- [ ] Sé dónde encontrar ejemplos JSON
- [ ] Tengo README_PRUEBAS_GUI.md como referencia
- [ ] Descargué Postman_Collection.json
- [ ] Conozco los endpoints CRUD completos
- [ ] Entiendo validaciones y errores
- [ ] Sé cómo ejecutar el script

---

## 🔗 Enlaces Directos

### En Este Repositorio
- [RESUMEN_PRUEBAS_GUI.md](RESUMEN_PRUEBAS_GUI.md) ⭐
- [GUIA_PRUEBAS_GUI.md](GUIA_PRUEBAS_GUI.md)
- [CASOS_PRUEBA_DETALLADOS.md](CASOS_PRUEBA_DETALLADOS.md)
- [DIAGRAMA_FLUJOS.md](DIAGRAMA_FLUJOS.md)

### En Proyecto Spring
- [README_PRUEBAS_GUI.md](workspace/spring-crud-ms/README_PRUEBAS_GUI.md)
- [Postman_Collection.json](workspace/spring-crud-ms/Postman_Collection.json)
- [pruebas_automatizadas.bat](workspace/spring-crud-ms/pruebas_automatizadas.bat)

### En Servidor (Cuando esté ejecutándose)
- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [OpenAPI JSON](http://localhost:8080/api-docs)

---

## 📞 Ayuda por Tema

### Temas Generales
| Tema | Documento |
|------|-----------|
| ¿Qué hacer? | RESUMEN_PRUEBAS_GUI.md |
| ¿Cómo hacerlo? | README_PRUEBAS_GUI.md |
| ¿Qué esperar? | GUIA_PRUEBAS_GUI.md |
| ¿Por qué pasa? | CASOS_PRUEBA_DETALLADOS.md |
| ¿Cómo fluye? | DIAGRAMA_FLUJOS.md |

### Por Herramienta
| Herramienta | Documento |
|-------------|-----------|
| Swagger UI | README_PRUEBAS_GUI.md - Opción 1 |
| Postman | README_PRUEBAS_GUI.md - Opción 2 |
| Insomnia | README_PRUEBAS_GUI.md - Opción 3 |
| Script | README_PRUEBAS_GUI.md - Opción 4 |

### Por Módulo
| Módulo | Documento |
|--------|-----------|
| ProductController | GUIA_PRUEBAS_GUI.md - Módulo 1 |
| ProductService | CASOS_PRUEBA_DETALLADOS.md - Módulo 2 |
| ProductRepository | CASOS_PRUEBA_DETALLADOS.md - Módulo 3 |
| GlobalExceptionHandler | GUIA_PRUEBAS_GUI.md - Módulo 4 |
| Validaciones | GUIA_PRUEBAS_GUI.md - Módulo 3 |

---

## 🎓 Flujo de Aprendizaje Sugerido

```
Día 1: Introducción
├─ Leer: RESUMEN_PRUEBAS_GUI.md (30 min)
├─ Ver: Swagger UI en navegador (10 min)
└─ Hacer: Primera prueba POST (15 min)

Día 2: Fundamentos
├─ Leer: README_PRUEBAS_GUI.md (45 min)
├─ Instalar: Postman (10 min)
├─ Importar: Postman_Collection.json (5 min)
└─ Hacer: CRUD completo en Postman (45 min)

Día 3: Validaciones
├─ Leer: GUIA_PRUEBAS_GUI.md (45 min)
├─ Hacer: Pruebas de validación (30 min)
└─ Verificar: Errores 400 y 404 (20 min)

Día 4: Profundidad
├─ Leer: CASOS_PRUEBA_DETALLADOS.md (60 min)
├─ Leer: DIAGRAMA_FLUJOS.md (30 min)
└─ Hacer: Pruebas complejas (30 min)

Día 5: Automatización
├─ Ejecutar: pruebas_automatizadas.bat (2 min)
├─ Crear: Tests en Postman (30 min)
└─ Validar: Cobertura total (20 min)
```

---

## 💡 Tips Útiles

1. **Guarda el ID del primer producto** para usarlo en todas las pruebas subsecuentes
2. **Usa las variables de Postman** para no repetir URLs
3. **Verifica los timestamps** para asegurar que se actualizan correctamente
4. **Prueba primero lo bueno** luego lo malo (validaciones)
5. **Revisa los logs del servidor** cuando algo falle
6. **Mantén MongoDB ejecutándose** antes de iniciar pruebas
7. **Usa el script para regresión** cuando termines cambios

---

## 🎯 Objetivos Completados

Después de usar esta guía, habrás:

✅ Comprendido la arquitectura del microservicio  
✅ Probado todos los 6 endpoints CRUD  
✅ Validado todas las 9 validaciones  
✅ Verificado el manejo de 5 tipos de error  
✅ Explorado 5 herramientas diferentes  
✅ Creado tus propias colecciones de pruebas  
✅ Automatizado pruebas de regresión  

---

## 📞 Soporte

¿No encuentras lo que buscas?

1. **Busca en este índice** usando Ctrl+F
2. **Consulta README_PRUEBAS_GUI.md** - Sección FAQ
3. **Revisa DIAGRAMA_FLUJOS.md** si necesitas entender arquitectura
4. **Verifica los logs** si algo falla

---

## 📈 Próximos Pasos

1. ✅ Lee este índice
2. ✅ Elige **RESUMEN_PRUEBAS_GUI.md**
3. ✅ Ejecuta servidor
4. ✅ Abre Swagger UI
5. ✅ Haz primera prueba
6. ✅ ¡Continúa explorando!

---

**Documento:** INDICE_MAESTRO.md  
**Versión:** 1.0.0  
**Actualizado:** 2026-08-08  
**Estado:** ✅ Completado

---

## 🚀 ¿Listo para Comenzar?

**[>>> VE A RESUMEN_PRUEBAS_GUI.md <<<](RESUMEN_PRUEBAS_GUI.md)**

o

**Abre directamente el navegador:** `http://localhost:8080/swagger-ui.html`
