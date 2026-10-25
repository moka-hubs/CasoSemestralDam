# Racks Tracker

Aplicación Android desarrollada con **Kotlin**, **Jetpack Compose**, **MVVM** y **Room** para llevar el control de racks informáticos por sede, sala y estado.

## Objetivo
Gestionar el inventario de racks, su ocupación, ubicación y asignación por usuario, con control de roles (Administrador / Usuario).

## Tecnologías utilizadas
- [Kotlin](https://kotlinlang.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [ViewModel + StateFlow](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Material 3](https://m3.material.io/)
- [Coil](https://coil-kt.github.io/coil/compose/)

## Funcionalidades
- Inicio de sesión / Registro de usuarios
- Persistencia de sesión con Room
- Listado de "Mis racks" y "Todos los racks"
- Agregar nuevo rack
- Ver detalle de rack y cambio de estado (solo Admin o dueño)
- Perfil de usuario con edición de datos
- Cierre de sesión con modal de confirmación
- Persistencia de racks, usuarios y sesión con **Room**

## Usuarios de prueba
| Correo | Contraseña | Rol |
| --- | --- | --- |
| `admin@racks.com` | `admin1234` | Administrador |
| `ana@racks.com` | `ana12345` | Usuario |

## Arquitectura
Se utiliza **MVVM (Model - View - ViewModel)** con las siguientes capas:

- `data/model` -> Entidades y modelos
- `data/local` -> Room (Entity, DAO, Database, Converters)
- `data/repository` -> Repositorios
- `data/session` -> Gestión de sesión
- `ui/screens` -> Pantallas Compose
- `ui/viewmodel` -> ViewModels
- `ui/components` -> Componentes reutilizables
- `ui/navigation` -> Navegación

## Instalación y ejecución
1. Clonar el repositorio
2. Abrir con Android Studio
3. Sincronizar Gradle
4. Ejecutar en emulador o dispositivo físico

## Integrantes
- Marcelo Dervis
- Joaquin Fuenzalida
- Paulo Riveros

## Fecha
Octubre 2026
