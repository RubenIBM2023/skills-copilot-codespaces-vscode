# ⚡ QUICK START - 5 Minutos

**REQ-2026-08-08-001-spring-crud-ms**

---

## 🚀 En 5 pasos

### ✅ Paso 1: Asegúrate que MongoDB está ejecutándose
```powershell
mongosh
# Debe conectar. Si no, ejecuta: net start MongoDB (como Admin)
# Ctrl+C para salir
```

### ✅ Paso 2: Abre PowerShell y ve al proyecto
```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
```

### ✅ Paso 3: Inicia el servidor
```powershell
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

Espera a ver:
```
Tomcat started on port(s): 8080
```

### ✅ Paso 4: Abre Swagger UI en navegador
```
http://localhost:8080/swagger-ui.html
```

### ✅ Paso 5: Haz tu primera prueba

1. Haz clic en **POST /api/v1/products**
2. Haz clic en **"Try it out"**
3. Pega este JSON:

```json
{
  "name": "Laptop Dell XPS 13",
  "description": "Laptop ultraportátil",
  "price": 1299.99,
  "stock": 5,
  "category": "Electronics"
}
```

4. Haz clic en **"Execute"**
5. ¡Verás Status 201 = Éxito! ✅

---

## 📚 Próxima lectura

- **Más detalles:** [`RESUMEN_PRUEBAS_GUI.md`](RESUMEN_PRUEBAS_GUI.md)
- **Tutorial completo:** [`workspace/spring-crud-ms/README_PRUEBAS_GUI.md`](workspace/spring-crud-ms/README_PRUEBAS_GUI.md)
- **Todos los casos:** [`GUIA_PRUEBAS_GUI.md`](GUIA_PRUEBAS_GUI.md)

---

## 🎯 Pruebas Rápidas (Si todo funciona)

Desde Swagger UI, prueba estos en orden:

| # | Endpoint | Método | Esperar | ✅ |
|---|----------|--------|---------|-----|
| 1 | POST /api/v1/products | POST | 201 | - |
| 2 | GET /api/v1/products | GET | 200 Array | - |
| 3 | GET /api/v1/products/{id} | GET | 200 Data | - |
| 4 | PUT /api/v1/products/{id} | PUT | 200 Updated | - |
| 5 | PATCH /api/v1/products/{id} | PATCH | 200 Partial | - |
| 6 | DELETE /api/v1/products/{id} | DELETE | 204 Empty | - |

---

## 🆘 Si algo falla

```
❌ "Connection refused"
→ ¿MongoDB ejecutándose? (mongosh)
→ ¿Servidor en puerto 8080? (ver consola)

❌ "HTTP 500"
→ Revisar logs en consola del servidor

❌ "Timeout en Swagger"
→ Espera a que aparezca "Tomcat started on port(s): 8080"

❌ "ValidationException (400)"
→ Es correcto - valida los datos
→ Revisa el JSON de ejemplo
```

---

**¿Listo?** → **[Abre Swagger UI](http://localhost:8080/swagger-ui.html)**
