# DOCUMENTACIÓN FINAL DEL PROYECTO INTERMODULAR

## FotoChef — Aplicación Android de Gestión Culinaria con Inteligencia Artificial

**Ciclo Formativo:** Desarrollo de Aplicaciones Multiplataforma (DAM) — 2º Curso  
**Módulo:** Proyecto Intermodular  
**Curso académico:** 2025–2026  
**Autor:** Jaime  
**Fecha de entrega:** Mayo 2026  

---

## Abstract (English)

FotoChef is a native Android application developed in Kotlin using Jetpack Compose and Material Design 3. Its main purpose is to assist users in their daily cooking experience by leveraging artificial intelligence. The application allows users to scan their available ingredients using the device camera through Google ML Kit's on-device image labeling technology, and then automatically generates personalized recipe suggestions using the Groq API (Llama 3.3 70B large language model). The system follows the MVVM (Model-View-ViewModel) architectural pattern with manual dependency injection, and employs a hybrid data persistence strategy combining a local Room SQLite database with Firebase Firestore cloud synchronization for user-specific data such as favorites and shopping lists. Authentication is handled through Firebase Auth, supporting both email/password and Google Sign-In via the modern Credential Manager API. Additional features include a step-by-step cooking mode with an integrated AI chat assistant, a smart shopping list with calendar scheduling, dietary preference management (vegan, gluten-free), bilingual support (Spanish/Catalan), and dark/light theme switching. The application targets Android 7.0+ (API 24) and is built with AGP 9.1.0 and Kotlin 2.2.10.

**Keywords:** Android, Kotlin, Jetpack Compose, Artificial Intelligence, ML Kit, Groq, Llama 3, Firebase, Room, MVVM, Recipe Management, Image Recognition.

---

## Resumen (Castellano)

FotoChef es una aplicación Android nativa desarrollada en Kotlin con Jetpack Compose y Material Design 3. Su objetivo principal es asistir a los usuarios en su experiencia culinaria diaria aprovechando la inteligencia artificial. La aplicación permite escanear los ingredientes disponibles utilizando la cámara del dispositivo mediante la tecnología de etiquetado de imágenes on-device de Google ML Kit, y genera automáticamente sugerencias de recetas personalizadas utilizando la API de Groq (modelo de lenguaje Llama 3.3 70B). El sistema sigue el patrón arquitectónico MVVM (Model-View-ViewModel) con inyección de dependencias manual, y emplea una estrategia híbrida de persistencia de datos que combina una base de datos local Room (SQLite) con sincronización en la nube mediante Firebase Firestore para datos específicos del usuario como favoritos y listas de la compra. La autenticación se gestiona a través de Firebase Auth, soportando tanto email/contraseña como Google Sign-In mediante la API moderna Credential Manager. Entre las funcionalidades adicionales se incluyen un modo de cocina paso a paso con asistente IA por chat integrado, una lista de la compra inteligente con programación por calendario, gestión de preferencias dietéticas (vegano, sin gluten), soporte bilingüe (español/catalán) y cambio entre tema oscuro y claro. La aplicación está dirigida a Android 7.0+ (API 24) y se ha construido con AGP 9.1.0 y Kotlin 2.2.10.

**Palabras clave:** Android, Kotlin, Jetpack Compose, Inteligencia Artificial, ML Kit, Groq, Llama 3, Firebase, Room, MVVM, Gestión de Recetas, Reconocimiento de Imágenes.

---

## Resum (Valencià)

FotoChef és una aplicació Android nativa desenvolupada en Kotlin amb Jetpack Compose i Material Design 3. El seu objectiu principal és assistir els usuaris en la seua experiència culinària diària aprofitant la intel·ligència artificial. L'aplicació permet escanejar els ingredients disponibles utilitzant la càmera del dispositiu mitjançant la tecnologia d'etiquetatge d'imatges on-device de Google ML Kit, i genera automàticament suggeriments de receptes personalitzades utilitzant l'API de Groq (model de llenguatge Llama 3.3 70B). El sistema segueix el patró arquitectònic MVVM (Model-View-ViewModel) amb injecció de dependències manual, i empra una estratègia híbrida de persistència de dades que combina una base de dades local Room (SQLite) amb sincronització al núvol mitjançant Firebase Firestore per a dades específiques de l'usuari com ara favorits i llistes de la compra. L'autenticació es gestiona a través de Firebase Auth, suportant tant email/contrasenya com Google Sign-In mitjançant l'API moderna Credential Manager. Entre les funcionalitats addicionals s'inclouen un mode de cuina pas a pas amb assistent IA per xat integrat, una llista de la compra intel·ligent amb programació per calendari, gestió de preferències dietètiques (vegà, sense gluten), suport bilingüe (espanyol/català) i canvi entre tema fosc i clar. L'aplicació està dirigida a Android 7.0+ (API 24) i s'ha construït amb AGP 9.1.0 i Kotlin 2.2.10.

**Paraules clau:** Android, Kotlin, Jetpack Compose, Intel·ligència Artificial, ML Kit, Groq, Llama 3, Firebase, Room, MVVM, Gestió de Receptes, Reconeixement d'Imatges.

---

## 1. INFORMACIÓN GENERAL

| Campo | Valor |
|-------|-------|
| **Nombre** | FotoChef |
| **Package** | com.example.fotochef |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 36 |
| **Versión** | 1.0 (versionCode 1) |
| **Lenguaje** | Kotlin 2.2.10 |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Arquitectura** | MVVM (Model-View-ViewModel) |
| **Inyección de dependencias** | Manual (AppContainer) |

---

## 2. DESCRIPCIÓN DEL PROYECTO

FotoChef es una aplicación Android de gestión culinaria inteligente que permite a los usuarios:

1. **Escanear ingredientes** mediante la cámara del dispositivo usando ML Kit (detección on-device)
2. **Generar recetas con IA** basadas en los ingredientes detectados, usando Groq API (Llama 3.3 70B)
3. **Gestionar un catálogo de recetas** con filtros avanzados por dificultad, tiempo, dieta vegana y sin gluten
4. **Mantener una lista de la compra** sincronizada en la nube con Firebase Firestore
5. **Marcar recetas favoritas** sincronizadas en Firebase Firestore
6. **Modo cocina paso a paso** con asistente de IA integrado para resolver dudas en tiempo real
7. **Personalizar preferencias** dietéticas, idioma (ES/CA) y tema oscuro/claro

---

## 3. STACK TECNOLÓGICO

### 3.1 Dependencias Principales

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **Jetpack Compose BOM** | 2024.09.00 | Framework UI declarativo |
| **Material 3** | (BOM) | Componentes y temas UI |
| **Navigation Compose** | 2.9.0 | Navegación entre pantallas |
| **Room** | 2.7.1 | Base de datos local SQLite |
| **DataStore Preferences** | 1.1.7 | Preferencias del usuario |
| **CameraX** | 1.4.2 | Captura de fotos |
| **ML Kit Image Labeling** | 17.0.9 | Detección de ingredientes on-device |
| **Firebase Auth** | BOM 33.0.0 | Autenticación (email + Google) |
| **Firebase Firestore** | BOM 33.0.0 | Base de datos en la nube |
| **Retrofit** | 2.11.0 | Cliente HTTP para API de IA |
| **OkHttp** | 4.12.0 | HTTP client + logging |
| **Gson** | 2.12.1 | Serialización/deserialización JSON |
| **Coil** | 2.7.0 | Carga de imágenes en Compose |
| **Credential Manager** | 1.2.2 | Google Sign-In moderno |
| **KSP** | 2.2.10-2.0.2 | Procesador de anotaciones Room |

### 3.2 APIs Externas

| API | Modelo | Uso |
|-----|--------|-----|
| **Groq** | Llama 3.3 70B Versatile | Generación de recetas + Asistente de cocina |
| **ML Kit** | Image Labeling (on-device) | Detección de ingredientes en fotos |
| **Firebase Auth** | - | Login con email/contraseña y Google |
| **Firebase Firestore** | - | Sincronización de favoritos y lista de compra |

### 3.3 Tipografía Personalizada

- **Títulos/Headings**: Poppins (Regular, Medium, SemiBold, Bold)
- **Cuerpo de texto**: Nunito (Regular, Medium, SemiBold, Bold)

### 3.4 Paleta de Colores

**Tema Claro:**
- Primary: Orange500 (#FF6D00) — identidad culinaria cálida
- Secondary: Green500 (#2E7D32) — frescura de ingredientes
- Tertiary: Red500 (#D32F2F) — acentos (tomates/especias)
- Background: #FFFDF7 (crema suave)

**Tema Oscuro:**
- Primary: Orange300 (#FFB74D) — naranja adaptado a fondo oscuro
- Background: #1A1410 (marrón oscuro cálido)

---

## 4. ARQUITECTURA DEL PROYECTO

### 4.1 Estructura de Paquetes

`
com.example.fotochef/
├── AppContainer.kt              # Contenedor de inyección de dependencias manual
├── FotoChefApplication.kt       # Application class (inicializa AppContainer)
├── MainActivity.kt              # Activity única (tema, navegación, Scaffold)
│
├── data/
│   ├── local/
│   │   ├── dao/                 # Data Access Objects (Room)
│   │   │   ├── IngredientDao.kt
│   │   │   ├── IngredientDetectatDao.kt
│   │   │   ├── ReceptaDao.kt
│   │   │   └── ShoppingListDao.kt
│   │   ├── database/
│   │   │   └── FotoChefDatabase.kt   # Room Database (v5, 5 entidades)
│   │   └── entity/              # Entidades Room
│   │       ├── Ingredient.kt
│   │       ├── IngredientCompra.kt
│   │       ├── IngredientDetectat.kt
│   │       ├── Recepta.kt
│   │       └── ReceptaIngredient.kt
│   │
│   ├── preferences/
│   │   ├── PreferencesManager.kt         # DataStore: dieta, tema, idioma, privacidad
│   │   └── UserPreferencesRepository.kt  # DataStore: onboarding completado
│   │
│   ├── remote/
│   │   ├── AiRecipeService.kt      # Interfaz Retrofit → Groq API
│   │   ├── VisionApiService.kt     # Interfaz Retrofit → Vision (legacy)
│   │   └── dto/
│   │       ├── AiGeneratedRecipe.kt # DTO de receta generada por IA
│   │       ├── OpenAiDto.kt         # DTOs para request/response OpenAI-compatible
│   │       └── VisionDtos.kt        # DTOs de Google Vision (legacy)
│   │
│   └── repository/
│       ├── AiRecipeRepository.kt     # Generación de recetas con Groq + fallback
│       ├── AuthRepository.kt         # Firebase Auth (email + Google Sign-In)
│       ├── FallbackRecipes.kt        # 6 recetas offline de respaldo
│       ├── IngredientRepository.kt   # CRUD ingredientes + detección
│       ├── MockDataRepository.kt     # Datos mock para desarrollo
│       ├── ReceptaRepository.kt      # CRUD recetas (Room + Firestore)
│       ├── ShoppingListRepository.kt # Lista de compra (Firestore)
│       └── VisionRepository.kt       # ML Kit Image Labeling on-device
│
├── di/
│   └── AppModule.kt                  # Módulo legacy (no usado activamente)
│
└── ui/
    ├── ViewModelFactory.kt           # Factory centralizado de ViewModels
    ├── components/
    │   └── EmptyStateView.kt         # Vista de estado vacío reutilizable
    ├── navigation/
    │   ├── BottomNavigationBar.kt    # Barra de navegación inferior (5 tabs)
    │   ├── NavGraph.kt               # Grafo de navegación completo
    │   └── Screen.kt                 # Definición de rutas selladas
    ├── theme/
    │   ├── Color.kt                  # Paleta de colores completa
    │   ├── Theme.kt                  # Tema Material 3 (claro/oscuro)
    │   └── Type.kt                   # Tipografía (Poppins + Nunito)
    └── screens/
        ├── auth/                     # Login, Registro, AuthViewModel
        ├── favorites/                # Pantalla de favoritos
        ├── help/                     # Ayuda y Política de privacidad
        ├── home/                     # Pantalla principal (dashboard)
        ├── ingredients/              # Gestión de despensa
        ├── onboarding/               # Tutorial inicial (3 páginas)
        ├── recipes/                  # Catálogo, detalle, modo cocina, IA
        ├── scan/                     # Cámara y detección de ingredientes
        ├── settings/                 # Ajustes unificados (perfil+settings)
        └── shopping/                 # Lista de la compra
`

### 4.2 Patrón de Inyección de Dependencias

La app usa **inyección manual** mediante AppContainer (no Hilt, por incompatibilidad con AGP 9.x):

1. FotoChefApplication.onCreate() → crea AppContainer(context)
2. AppContainer instancia todos los DAOs, Repositories y Managers como lazy singletons
3. ViewModelFactory accede al AppContainer a través de CreationExtras.fotoChefApplication()
4. Cada ViewModel declara sus dependencias en su constructor

### 4.3 Flujo de Datos (MVVM)

`
[UI Composable] ←observa StateFlow/Flow← [ViewModel] ←llama→ [Repository] ←accede→ [DAO/API/Firebase]
`

---

## 5. BASE DE DATOS

### 5.1 Room Database: otochef_database (v5)

**5 entidades** con relaciones many-to-many:

#### Tabla eceptes (Recepta)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long (PK, auto) | ID único |
| nom | String | Nombre de la receta |
| descripcio | String | Descripción breve |
| tempsPreparacio | Int | Tiempo en minutos |
| dificultat | String | "Fàcil", "Mitjà", "Difícil" |
| imatgePath | String | Ruta/URL de imagen |
| esFavorita | Boolean | Marcada como favorita |
| passos | String (JSON) | Array JSON de pasos |
| categoria | String | "Primer", "Segon", "Postres" |
| esVegana | Boolean | Apta para veganos |
| esSenseGluten | Boolean | Sin gluten |
| calories | Int | Calorías por ración |
| proteines | Float | Proteínas (g) |
| carbs | Float | Carbohidratos (g) |
| greixos | Float | Grasas (g) |

#### Tabla ingredients (Ingredient)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long (PK, auto) | ID único |
| nom | String | Nombre del ingrediente |
| categoria | String | "Verdura", "Carne", "Lácteo"... |
| icona | String | Emoji representativo |
| isInPantry | Boolean | Si está en la despensa virtual |

#### Tabla ecepta_ingredient (ReceptaIngredient) — Tabla intermedia N:M
| Campo | Tipo | Descripción |
|-------|------|-------------|
| receptaId | Long (PK, FK→receptes) | ID de la receta |
| ingredientId | Long (PK, FK→ingredients) | ID del ingrediente |
| quantitat | String | Cantidad ("200g", "2 unidades") |

#### Tabla ingredients_detectats (IngredientDetectat)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long (PK, auto) | ID único |
| nom | String | Nombre detectado |
| confianca | Float | Score de confianza (0.0-1.0) |
| imatgePath | String | Ruta de la imagen fuente |
| timestamp | Long | Momento de detección |
| confirmat | Boolean | Validado manualmente |

#### Tabla ingredients_compra (IngredientCompra)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long (PK, auto) | ID único |
| nom | String | Nombre del ingrediente |
| quantitat | String | Cantidad ("500", "2") |
| unitat | String | Unidad ("g", "kg", "ud") |
| receptaId | Long? | Receta de origen (nullable) |
| isComprat | Boolean | Estado comprado/pendiente |
| createdAt | Long | Fecha de creación |
| scheduledDate | Long? | Fecha programada para compra |

### 5.2 Firebase Firestore (Nube)

Estructura de colecciones:
`
users/{userId}/
├── favorites/{recipeId}     → Objeto Recepta completo
└── shopping_list/{itemId}   → Objeto IngredientCompra completo
`

Sincronización en tiempo real mediante ddSnapshotListener + callbackFlow.

---

## 6. PANTALLAS Y NAVEGACIÓN

### 6.1 Barra de Navegación Inferior (5 tabs)

| Tab | Icono | Ruta | Pantalla |
|-----|-------|------|----------|
| Home | 🏠 | home | Dashboard principal |
| Recipes | 📖 | ecipes | Catálogo de recetas |
| Compra | 🛒 | shopping_list | Lista de la compra |
| Favorites | ❤️ | avorites | Recetas favoritas |
| Perfil | 👤 | profile | Ajustes unificados |

### 6.2 Mapa Completo de Pantallas (15 rutas)

| Ruta | Pantalla | Descripción |
|------|----------|-------------|
| onboarding | OnboardingScreen | Tutorial inicial (3 slides) |
| login | LoginScreen | Login con email o Google |
| egister | RegisterScreen | Registro con email |
| home | HomeScreen | Dashboard: receta destacada, acciones rápidas, favoritos |
| camera | CameraScreen | Captura de foto con CameraX |
| detection | DetectionScreen | Resultados de detección ML Kit |
| ecipes | RecipesScreen | Catálogo con filtros + panel IA |
| ecipe_detail/{recipeId} | RecipeDetailScreen | Detalle de receta con macros |
| cooking_mode/{name}/{steps} | CookingModeScreen | Modo cocina paso a paso + asistente IA |
| ingredients | IngredientsScreen | Gestión de despensa virtual |
| shopping_list | ShoppingListScreen | Lista de compra con calendario |
| avorites | FavoritesScreen | Recetas marcadas como favoritas |
| profile | SettingsScreen | Ajustes: dieta, tema, idioma, logout |
| help | HelpScreen | Pantalla de ayuda |
| privacy | PrivacyScreen | Política de privacidad |

### 6.3 Flujo de Navegación

`
Onboarding → Login ↔ Register
                ↓
              Home ──→ Camera → Detection → Recipes
                │                              ↓
                ├──→ Recipes ──────→ RecipeDetail → CookingMode
                ├──→ ShoppingList
                ├──→ Favorites ───→ RecipeDetail
                └──→ Profile (Settings) ──→ Help / Privacy
                                          ──→ Logout → Login
`

---

## 7. VIEWMODELS (11 total)

| ViewModel | Dependencias | Responsabilidad |
|-----------|-------------|-----------------|
| **HomeViewModel** | ReceptaRepository | Receta destacada aleatoria, favoritos recientes, nombre usuario |
| **RecipesViewModel** | ReceptaRepo, PrefsManager, AiRecipeRepo, IngredientRepo | Filtros, búsqueda, generación IA, despensa |
| **RecipeDetailViewModel** | ReceptaRepo, ShoppingListRepo | Detalle, toggle favorito, añadir a lista compra |
| **FavoritesViewModel** | ReceptaRepository | Lista de favoritos, toggle |
| **ScanFlowViewModel** | VisionRepo, IngredientRepo | Flujo cámara→detección→guardado |
| **SettingsViewModel** | PreferencesManager, ReceptaRepo | Dieta, tema, idioma, borrar datos |
| **AuthViewModel** | AuthRepository | Login/registro email, Google Sign-In, estado auth |
| **ShoppingListViewModel** | ShoppingListRepository | CRUD lista compra, filtro por fecha, toggle estado |
| **IngredientsViewModel** | IngredientRepository | Búsqueda ingredientes, toggle despensa |
| **CookingAssistantViewModel** | AiRecipeRepository | Chat IA en modo cocina |

---

## 8. FUNCIONALIDADES DETALLADAS

### 8.1 Escaneo de Ingredientes (ML Kit On-Device)
1. El usuario abre la **cámara** (CameraX)
2. Captura una foto de sus ingredientes
3. VisionRepository procesa la imagen con **ML Kit Image Labeling** (sin API key, on-device)
4. Threshold de confianza: **0.5** mínimo
5. Los resultados se guardan en Room (ingredients_detectats)
6. Se muestran en DetectionScreen con barra de confianza y chips editables
7. Los ingredientes confirmados se pasan a RecipesScreen para buscar/generar recetas

### 8.2 Generación de Recetas con IA (Groq API)
1. El usuario tiene ingredientes detectados o en su despensa
2. Pulsa "Generar recetas IA" en el panel expandible de RecipesScreen
3. AiRecipeRepository envía un prompt estructurado a **Groq API (Llama 3.3 70B)**
4. El prompt incluye: ingredientes, filtros dietéticos, recetas a excluir
5. La respuesta JSON se parsea a List<AiGeneratedRecipe>
6. Si hay error (rate limit 429, sin conexión), se muestran **FallbackRecipes** (6 recetas offline)
7. Las recetas IA se pueden guardar como favoritas o ver su detalle

### 8.3 Asistente de Cocina con IA
1. Desde RecipeDetailScreen, el usuario pulsa "Empezar a cocinar"
2. Se abre CookingModeScreen con los pasos numerados
3. En la parte inferior, hay un **chat con IA** (CookingAssistantSheet)
4. El usuario puede preguntar dudas sobre la receta en tiempo real
5. CookingAssistantViewModel envía la pregunta + contexto de la receta a Groq

### 8.4 Sistema de Favoritos (Firestore)
- Los favoritos se sincronizan en **Firebase Firestore** por usuario
- Colección: users/{uid}/favorites/{recipeId}
- Se escuchan cambios en tiempo real con ddSnapshotListener
- Al guardar una receta IA como favorita, se crea el registro local + cloud

### 8.5 Lista de la Compra (Firestore)
- Colección: users/{uid}/shopping_list/{itemId}
- Funcionalidades: añadir manual, añadir desde receta, marcar comprado, eliminar
- Calendario horizontal para filtrar por fecha programada
- Pestañas: Pendientes / Comprados
- Sincronización en tiempo real entre dispositivos

### 8.6 Autenticación (Firebase Auth)
- **Email/Password**: registro y login con validación
- **Google Sign-In**: mediante Credential Manager API moderna
- Onboarding de 3 páginas antes del primer login
- Estado de sesión persistente

### 8.7 Preferencias del Usuario (DataStore)
| Preferencia | Clave | Tipo | Default |
|------------|-------|------|---------|
| Vegano | is_vegan | Boolean | false |
| Sin gluten | is_gluten_free | Boolean | false |
| Modo oscuro | dark_mode | Boolean | false |
| Idioma | language | String | "es" |
| Privacidad aceptada | privacy_accepted | Boolean | false |
| Onboarding completado | onboarding_completed | Boolean | false |

Idiomas soportados: **Español (es)** y **Catalán (ca)** — archivos alues-es/strings.xml y alues-ca/strings.xml.

---

## 9. CONFIGURACIÓN Y CLAVES API

En local.properties (no versionado):
`properties
GEMINI_API_KEY=<clave_gemini>
GROQ_API_KEY=<clave_groq>
`

Ambas se exponen vía BuildConfig:
- BuildConfig.GEMINI_API_KEY — legacy, no usado activamente
- BuildConfig.GROQ_API_KEY — usado por AiRecipeRepository

Firebase se configura mediante google-services.json en pp/.

---

## 10. PERMISOS ANDROID

| Permiso | Uso |
|---------|-----|
| INTERNET | APIs remotas (Groq, Firebase) |
| CAMERA | Captura de fotos con CameraX |

uses-feature camera declarada como equired="false".

---

## 11. DATOS MOCK Y FALLBACK

### MockDataRepository
- 15 ingredientes predefinidos con emojis
- 6 recetas completas (con pasos JSON y relaciones N:M)
- 5 ingredientes detectados mock

### FallbackRecipes
- 6 recetas de respaldo para cuando la IA no está disponible
- Filtros por dieta (vegana, sin gluten, sin lactosa)
- Se seleccionan aleatoriamente y excluyendo nombres ya mostrados

---

## 12. CONTEO DE ARCHIVOS

| Categoría | Archivos |
|-----------|----------|
| Entidades Room | 5 |
| DAOs | 4 |
| Repositories | 8 |
| ViewModels | 11 |
| Screens (Composables) | 15+ |
| Navegación | 3 |
| Tema | 3 |
| DTOs | 3 |
| Preferences | 2 |
| Componentes reutilizables | 1 |
| **Total archivos Kotlin** | **~64** |

---

## 13. CONCLUSIONES

### 13.1 Objetivos Alcanzados

El desarrollo de FotoChef ha permitido alcanzar satisfactoriamente todos los objetivos planteados inicialmente:

1. **Integración de Inteligencia Artificial**: Se ha logrado integrar dos sistemas de IA de manera efectiva: ML Kit para la detección on-device de ingredientes mediante la cámara (sin necesidad de conexión a internet para este paso), y Groq API (Llama 3.3 70B) para la generación de recetas personalizadas y el asistente de cocina en tiempo real. El sistema de fallback con recetas predefinidas garantiza que la funcionalidad principal nunca falle, incluso sin conexión o con límites de tasa de la API.

2. **Arquitectura Moderna y Escalable**: La aplicación sigue el patrón MVVM con Jetpack Compose, lo que ha resultado en un código declarativo, reactivo y fácil de mantener. La separación clara entre capas (UI → ViewModel → Repository → Data Source) facilita la extensibilidad del proyecto.

3. **Persistencia Híbrida (Local + Cloud)**: La estrategia de combinar Room (SQLite local) para datos del catálogo con Firebase Firestore para datos del usuario (favoritos y lista de compra) permite una sincronización en tiempo real entre dispositivos manteniendo la rapidez de acceso local.

4. **Experiencia de Usuario Completa**: Se ha implementado un flujo completo desde el escaneo de ingredientes hasta el seguimiento paso a paso de una receta con asistente IA, pasando por la gestión de listas de compra con calendario, sistema de favoritos y personalización de preferencias dietéticas y visuales.

5. **Autenticación Segura**: La implementación de Firebase Auth con soporte para email/contraseña y Google Sign-In mediante la API moderna Credential Manager garantiza un acceso seguro y cómodo para el usuario.

### 13.2 Dificultades Encontradas

- **Incompatibilidad con Hilt**: La versión de AGP 9.x utilizada no es compatible con Hilt (Dagger), lo que obligó a implementar un sistema de inyección de dependencias manual mediante `AppContainer`. Aunque funcional, esta solución requiere más código boilerplate.
- **Rate Limiting de APIs**: Los servicios de IA gratuitos (Groq) tienen límites de tasa que pueden afectar a la experiencia del usuario. Se solucionó implementando un sistema de recetas de fallback que garantiza la funcionalidad incluso con errores 429.
- **Migración a Firestore**: La transición de una base de datos puramente local (Room) a un sistema híbrido con Firestore requirió refactorizar significativamente los repositorios y la gestión de estados reactivos con `callbackFlow`.

### 13.3 Posibles Mejoras Futuras

- **Detección de ingredientes mejorada**: Entrenar un modelo ML Kit personalizado específico para ingredientes alimentarios, mejorando la precisión de la detección.
- **Imágenes de recetas**: Integrar generación de imágenes con IA o conexión a APIs de imágenes de comida para enriquecer visualmente las recetas.
- **Modo offline completo**: Implementar sincronización bidireccional con caché local de Firestore para uso sin conexión.
- **Notificaciones push**: Recordatorios de lista de compra y sugerencias de recetas basadas en ingredientes próximos a caducar.
- **Compartir recetas**: Funcionalidad social para compartir recetas entre usuarios de la aplicación.
- **Planificador semanal de menús**: Generar un menú semanal equilibrado basado en las preferencias dietéticas del usuario.

### 13.4 Valoración Personal

El desarrollo de FotoChef ha supuesto un reto técnico completo que ha permitido aplicar de forma práctica los conocimientos adquiridos a lo largo del ciclo formativo DAM: desde el diseño de interfaces modernas con Jetpack Compose hasta la integración con servicios cloud (Firebase) y APIs de inteligencia artificial (Groq, ML Kit). El resultado es una aplicación funcional, visualmente atractiva y con un alto componente de innovación que demuestra las posibilidades actuales del desarrollo Android con Kotlin.

---

## 14. ANEXOS

### Anexo A: Esquema Entidad-Relación de la Base de Datos

El esquema en formato DBML para su visualización en [dbdiagram.io](https://dbdiagram.io/d) se encuentra en el archivo:

📄 **`dbdiagram_schema.dbml`** (incluido en la raíz del proyecto)

Para generar el diagrama ER:
1. Abrir [dbdiagram.io/d](https://dbdiagram.io/d)
2. Pegar el contenido del archivo `.dbml`
3. El diagrama se genera automáticamente

### Anexo B: Estructura de Ficheros del Proyecto

El proyecto contiene **~64 archivos Kotlin** organizados en la siguiente estructura de paquetes:

- `com.example.fotochef` — Clases raíz (Application, Activity, AppContainer)
- `com.example.fotochef.data.local` — Entidades, DAOs y base de datos Room
- `com.example.fotochef.data.remote` — Servicios Retrofit y DTOs
- `com.example.fotochef.data.repository` — Capa de repositorio (8 repositorios)
- `com.example.fotochef.data.preferences` — DataStore Preferences
- `com.example.fotochef.ui.navigation` — Grafo de navegación y rutas
- `com.example.fotochef.ui.screens` — 10 paquetes de pantallas
- `com.example.fotochef.ui.theme` — Tema Material 3 personalizado
- `com.example.fotochef.ui.components` — Componentes reutilizables

### Anexo C: Instrucciones de Compilación y Ejecución

**Prerrequisitos:**
- Android Studio Ladybug (2024.x) o superior
- JDK 17+
- SDK de Android con API 36 instalada

**Configuración:**
1. Clonar el repositorio desde GitHub
2. Crear el archivo `local.properties` en la raíz con las claves API:
   ```
   GROQ_API_KEY=<tu_clave_de_groq>
   GEMINI_API_KEY=<tu_clave_de_gemini>
   ```
3. Colocar el archivo `google-services.json` de Firebase en `app/`
4. Sincronizar el proyecto con Gradle

**Compilación:**
```bash
./gradlew assembleDebug     # APK de debug
./gradlew assembleRelease   # APK de release
```

**Ejecución:**
- Conectar un dispositivo Android (API 24+) o usar un emulador
- Ejecutar desde Android Studio con `Run > Run 'app'`

### Anexo D: APIs y Servicios Externos

| Servicio | URL | Tipo de Acceso |
|----------|-----|----------------|
| Groq API | `https://api.groq.com/` | API Key (Bearer Token) |
| Firebase Auth | Console Firebase | google-services.json |
| Firebase Firestore | Console Firebase | google-services.json |
| ML Kit Image Labeling | On-device (sin API) | Incluido en el SDK |

### Anexo E: Repositorio del Código Fuente

📦 **Código fuente en GitHub:** *(añadir enlace al repositorio)*

El repositorio incluye:
- Código fuente completo del proyecto Android
- Archivo `dbdiagram_schema.dbml` con el esquema de la base de datos
- Documentación técnica (`DOCUMENTACION_COMPLETA_FOTOCHEF.md`)
- APK compilada en `app/build/outputs/apk/`

---

*Documento generado para la entrega del Proyecto Intermodular — DAM 2º Curso — 2025/2026*


