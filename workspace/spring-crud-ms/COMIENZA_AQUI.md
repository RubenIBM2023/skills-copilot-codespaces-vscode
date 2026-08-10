# 🚀 SPRING BOOT CRUD MICROSERVICE - PRUEBAS

## 📌 Inicio Rápido (2 PASOS)

### Paso 1: Compila (primera vez - toma ~60 segundos)
```powershell
cd "workspace\spring-crud-ms"
mvn clean package -q
```

### Paso 2: Inicia y Prueba (elige una opción)

#### Opción A: Script Automático (RECOMENDADO)
```powershell
# PowerShell
.\INICIA_APLICACION.ps1

# O CMD
INICIA_APLICACION.bat
```

#### Opción B: Línea de comandos
```powershell
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

Espera a ver:
```
Tomcat started on port(s): 8080 (http)
```

### Paso 3: Abre en navegador
```
http://localhost:8080
```

---

## 📚 DOCUMENTACIÓN DE PRUEBAS

- **[GUIA_PRUEBAS_COMPLETA.md](./GUIA_PRUEBAS_COMPLETA.md)** - Guía completa con todos los métodos
- **[PRUEBAS_RAPIDAS.md](./PRUEBAS_RAPIDAS.md)** - Pruebas rápidas con curl y ejemplos
- **[Postman_Collection.json](./Postman_Collection.json)** - Importa en Postman para pruebas API

---

## 🎯 TRES FORMAS DE PROBAR

### 1️⃣ GUI DASHBOARD (Interfaz Gráfica)
**La más fácil**
- Crear, ver, editar, eliminar productos
- Interfaz visual completa
- Solo abre `http://localhost:8080`

### 2️⃣ POSTMAN (API Directo)
**La más técnica**
- Importa `Postman_Collection.json`
- Pruebas de validación
- Casos de error

### 3️⃣ CURL / PowerShell (Terminal)
**La más rápida**
- Prueba directamente desde PowerShell
- Ejemplos en `PRUEBAS_RAPIDAS.md`

---

## ✅ VERIFICATION COMMANDS

```powershell
# Todos los tests pasan
mvn test

# Sin violaciones de estilo
mvn checkstyle:check

# Compila sin errores
mvn compile

# Build completo
mvn clean package
```

---

## 🔗 URLS DISPONIBLES

| URL | Descripción |
|-----|-------------|
| `http://localhost:8080` | 🎨 GUI Dashboard |
| `http://localhost:8080/api/v1/products` | 📝 API REST |
| `http://localhost:8080/swagger-ui.html` | 📘 Swagger UI |
| `http://localhost:8080/api-docs` | 📄 OpenAPI JSON |

---

## 📊 ENDPOINTS API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/products` | Crear producto |
| GET | `/api/v1/products` | Listar productos |
| GET | `/api/v1/products/{id}` | Obtener por ID |
| PUT | `/api/v1/products/{id}` | Actualizar completo |
| PATCH | `/api/v1/products/{id}` | Actualizar parcial |
| DELETE | `/api/v1/products/{id}` | Eliminar |

---

## 🧪 EJEMPLO DE PRUEBA RÁPIDA

Crear un producto desde PowerShell:

```powershell
$body = @{
    name = "iPhone 15"
    price = 999.99
    stock = 10
    category = "Electronics"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

---

## 🐛 PROBLEMAS COMUNES

| Problema | Solución |
|----------|----------|
| "Puerto 8080 en uso" | Cambia: `--server.port=8081` |
| "No se encuentra JAR" | Ejecuta: `mvn clean package -q` |
| "MongoDB no conecta" | Usa Flapdoodle (embebido, automático) |
| "GUI no carga" | Recarga: Ctrl+F5 |

---

## 📝 NOTAS

- ✅ 17 tests automáticos
- ✅ Cobertura >80%
- ✅ 0 violaciones Checkstyle
- ✅ MongoDB embebido para testing
- ✅ Swagger/OpenAPI integrado

---

**¿Necesitas ayuda? Lee [GUIA_PRUEBAS_COMPLETA.md](./GUIA_PRUEBAS_COMPLETA.md)**

---

Generado por OpenCode Agent - 2026-08-10
