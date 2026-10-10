# CitaYa

Plataforma SaaS para barberías y salones (República Dominicana): citas, CRM,
finanzas, inventario y WhatsApp. App **Android nativa (Kotlin + Jetpack Compose)**.

## Requisitos para ejecutarla

- **Android Studio** (recomendado) o **Firebase Studio / AI Studio**.
  Android Studio ya incluye el JDK y el Android SDK.
- Un **emulador** o un **teléfono Android** conectado.

## Cómo verla en Android Studio

1. `File > Open...` y selecciona la **carpeta raíz de este proyecto** (la que tiene
   `settings.gradle.kts`).
2. Espera a que termine el **Gradle Sync** (la primera vez descarga dependencias).
3. Elige un emulador o tu teléfono en la barra superior y pulsa **Run ▶**.
   La app abre en el **Menú de inicio** (Cliente / Negocio).

> El `gradlew` (Gradle wrapper) ya está incluido. Si usas la terminal:
> `./gradlew assembleDebug`

## ✅ `google-services.json` (ya incluido)

El repo ya trae el `google-services.json` **real** de tu proyecto
**`citaya-eab2c`** (paquete `com.aistudio.citaya.rdzxp`), ubicado en
`app/google-services.json`. No tienes que descargar nada más.

El plugin de Google Services está aplicado en `app/build.gradle.kts`
(`id("com.google.gms.google-services")`) y las dependencias de Firebase usan el
**BoM** (`implementation(platform(libs.firebase.bom))`), así que no se
especifican versiones por librería.

### Falta solo esto en Firebase Console
1. **Firestore Database → Crear base de datos.** La app usa la base `(default)`
   (definida en `app/src/main/res/values/firebase_applet_config.xml`).
2. **Authentication → activar proveedor Google.** Para el login con Google hay
   que registrar la **huella SHA-1** de tu app (Configuración del proyecto →
   Agregar huella digital). El **Modo Cliente** (sin login) ya funciona sin esto.

## Pagos (opcional)

Stripe está integrado, pero necesita un backend desplegado:

```
cd functions
npm install
firebase functions:secrets:set STRIPE_SECRET_KEY     # pega tu sk_... / rk_... aquí
firebase functions:secrets:set STRIPE_WEBHOOK_SECRET  # secreto del webhook
firebase deploy --only functions
```

Luego copia la URL del deploy en `STRIPE_BACKEND_URL` (archivo `.env.example`) y
crea el webhook en Stripe apuntando a `.../stripeWebhook`.

La clave **pública** (`pk_...`) va en `.env` / `.env.example`
(`STRIPE_PUBLISHABLE_KEY`). La clave **secreta** NUNCA va en la app ni en el repo.

## Planes

| Plan | Precio (RD$/mes) | Incluye |
|------|------------------|---------|
| Gratis (Básico) | 0 | Hasta 20 citas al mes |
| Pro | 1,000 | Citas ilimitadas |
| VIP | 2,500 | Todo ilimitado + venta cruzada |

## Estructura

- `app/src/main/java/com/example/` — código de la app
  - `ui/screens/` — pantallas (Menú, Auth, Panel, Dashboard, CRM, Inventario, Operaciones, Modo Cliente, Registro)
  - `ui/components/` — componentes y diálogos (incluye `PaywallDialog` con los 3 planes)
  - `data/` — repositorio, servicios de Firebase y Stripe
  - `model/` — modelos y `PlanType`
- `firestore.rules` — reglas de seguridad (aislamiento por negocio)
- `functions/` — backend de pagos (Stripe)
- `firebase.json` — configuración de Firebase
