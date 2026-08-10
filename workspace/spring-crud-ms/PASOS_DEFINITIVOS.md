# 🔧 INSTRUCCIONES DEFINITIVAS - GUARDAR PRODUCTOS

## ⚠️ IMPORTANTE

He realizado **actualizaciones importantes** a la aplicación. DEBES seguir estos pasos exactamente para que funcione:

---

## 📋 PASOS A SEGUIR

### PASO 1: Abre PowerShell NUEVA
- No uses la que tenías antes
- Presiona **Windows + R**
- Escribe: `powershell`
- Presiona Enter

### PASO 2: Navega a la carpeta del proyecto

```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
```

Verifica que estás en la carpeta correcta. Deberías ver archivos como `pom.xml`, `EJECUTAR_AHORA.ps1`, etc.

### PASO 3: Ejecuta el script automático

```powershell
.\EJECUTAR_AHORA.ps1
```

**Esto hará:**
1. Limpia archivos viejos
2. Recompila la aplicación (~2 minutos)
3. Inicia el servidor automáticamente
4. Abre el navegador en `http://localhost:8080`

### PASO 4: Espera a ver en la consola

```
Tomcat started on port(s): 8080 (http)
SpringCrudMsApplication started in X.XXX seconds
```

### PASO 5: En el navegador

1. Deberías ver la interfaz gráfica
2. En la esquina superior derecha, verifica que dice: **✅ Online**
3. Si dice ❌ Offline, espera 5 segundos y recarga con **Ctrl+F5**

### PASO 6: Prueba guardar un producto

1. **Llena el formulario:**
   - Nombre: `iPhone 15`
   - Descripción: `Smartphone Apple`
   - Precio: `999.99`
   - Stock: `10`
   - Categoría: `Electronics`

2. **Haz clic en "💾 Guardar"**

3. **Resultado esperado:**
   - Mensaje verde: ✅ **Producto creado correctamente**
   - Producto aparece en la tabla
   - Formulario se vacía

---

## 🐛 SI SIGUE SIN FUNCIONAR

### Opción A: Ver errores en consola del navegador

1. En navegador, presiona **F12**
2. Ve a pestaña **"Console"**
3. Haz clic en "Guardar"
4. **Copia TODO lo que ves en rojo**
5. **Comparte ese mensaje conmigo**

### Opción B: Probar directamente desde PowerShell

En la MISMA terminal donde corre la aplicación, abre OTRA terminal y ejecuta:

```powershell
$body = @{
    name = "Test desde PowerShell"
    description = "Prueba"
    price = 99.99
    stock = 5
    category = "Electronics"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body -Verbose
```

**¿Qué resultado ves?**

---

## 📝 CAMBIOS QUE HICE

Para que funcione correctamente, actualicé:

1. **CorsConfig.java** - Permite comunicación navegador ↔ API
2. **HealthController.java** - Endpoint para verificar que servidor funciona
3. **index.html** - Mejor manejo de errores y logging
4. **application.properties** - Configuración de MongoDB embebido

---

## ✅ CHECKLIST FINAL

- [ ] Abrí PowerShell NUEVA
- [ ] Navigué a la carpeta correcta
- [ ] Ejecuté: `.\EJECUTAR_AHORA.ps1`
- [ ] Esperé a que termine (2 minutos)
- [ ] Vi "Tomcat started on port 8080"
- [ ] Se abrió navegador automáticamente
- [ ] Veo la interfaz gráfica
- [ ] El servidor muestra ✅ Online
- [ ] Llené el formulario
- [ ] Hice clic en "Guardar"
- [ ] Vi el mensaje de éxito
- [ ] El producto aparece en la tabla

---

## 🎯 SI ALGO FALLA

**Error: No encuentra el script**
- Verifica que estás en la carpeta correcta
- El archivo debe ser: `C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms\EJECUTAR_AHORA.ps1`

**Error: "Tomcat no inicia"**
- Espera 10 segundos
- Si no inicia, presiona Ctrl+C y vuelve a ejecutar el script

**Error: "Puerto 8080 en uso"**
- Cierra aplicaciones que usen el puerto 8080
- O cambia el puerto: edita `src/main/resources/application.properties`

**Error: "Producto no se guarda pero no veo error"**
- Presiona F12 en navegador
- Comparte lo que veas en la Console (pestaña roja)

---

## 🎉 RESULTADO ESPERADO

Después de seguir estos pasos:

✅ Puedes crear productos
✅ Ves confirmación inmediata
✅ Los productos aparecen en tabla
✅ Puedes editar productos
✅ Puedes eliminar productos
✅ Todo sin errores

---

## 📞 NECESITO TU AYUDA

Si sigue sin funcionar, por favor dime:

1. **Qué ves en la consola del navegador (F12 → Console)**
2. **Si el servidor muestra ✅ Online o ❌ Offline**
3. **El mensaje de error exacto que aparece**
4. **Resultado de ejecutar el comando PowerShell anterior**

Con esa información puedo resolver el problema específico.

---

**¡Inténtalo ahora! 🚀**

Ejecuta:
```powershell
.\EJECUTAR_AHORA.ps1
```
