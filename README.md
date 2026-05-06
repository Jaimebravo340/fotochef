# FotoChef 🍳

FotoChef es una aplicación Android diseñada para ayudarte a cocinar con lo que tienes a mano. Utiliza Inteligencia Artificial para detectar ingredientes mediante la cámara y te sugiere las mejores recetas basadas en esos ingredientes y tus preferencias dietéticas.

## 🚀 Funcionalidades Principales

- **Detección IA:** Escaneo de ingredientes mediante la cámara (ML Kit / Google Vision).
- **Recetario Inteligente:** Listado de recetas que coinciden con tus ingredientes.
- **Filtros Avanzados:** Filtrado por dificultad, tiempo, dieta vegana y sin gluten.
- **Favoritos:** Guarda tus recetas preferidas localmente.
- **Persistencia Local:** Todo el recetario e ingredientes se gestionan mediante Room Database para funcionamiento offline.

---

## 🛠️ Detalles de Implementación (Sprints)

Para ver los detalles técnicos de cada fase del proyecto, consulta la documentación específica:

- **Sprint 2:** [Estructura Base y Navegación](docs/S2_Documentacion_Implementacion_Jaime_bravo.md)
- **Sprint 3:** [Cámara y Flujo de Captura](docs/S3_Documentacion_Implementacion_Camara_Deteccion.md)
- **Sprint 4:** [IA de Detección de Ingredientes (ML Kit)](docs/S4_Documentacion_Implementacion_IA_Deteccion_Ingredientes.md)
- **Sprint 5:** [Recetas, Filtros y Persistencia Room](docs/S5_Documentacion_Implementacion_Recetas_Filtros.md)
- **Sprint 6:** [Favoritos y Lista de la Compra](docs/walkthrough_s6.md)

---

## 🏗️ Arquitectura Técnica

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Base de Datos:** Room
- **IA:** Google ML Kit (Vision Labeling)
- **Patrón:** MVVM (Model-View-ViewModel)
- **DI:** Manual Dependency Injection (AppContainer)

---

## ⚙️ Configuración del Proyecto

1. Clona el repositorio.
2. Abre el proyecto en Android Studio.
3. El proyecto utiliza un `AppContainer` para la inyección de dependencias, asegúrate de que `FotoChefApplication` esté configurada en el `AndroidManifest.xml`.
4. Las recetas se precargan desde `MockDataRepository` a la base de datos Room en el primer inicio.

---

Desarrollado como proyecto intermodular para 2º DAM.
