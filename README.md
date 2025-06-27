# 🧪 Pollab - Mobile App

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-orange)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-blue)
![Architecture-MVVM](https://img.shields.io/badge/Architecture-MVVM-green)
![License](https://img.shields.io/badge/License-MIT-green)

**About Pollab**

**Pollab** (short for *Poll Laboratory*) is a modern, open-source polling platform designed to
simplify how users collect opinions and make decisions. Built with **Django REST Framework**
and **PostgreSQL**, it powers both web and mobile clients with a robust API.

Main features include:
- 🧠 Clean and responsive UI
- ⚙️ Reactive forms with validation
- 🕒 Real-time vote tracking and countdown support
- 📢 Toast notifications for a smooth UX

Whether you're testing ideas, collecting feedback, or running interactive demos — Pollab lets
you create, vote, and visualize results in seconds.
> **Join. Experiment. Transform.**

---

## Interface

<p align="center">
  <img src="readme_assets/mobile_create.png" alt="Splashscreen" width="30%"/>
  <img src="readme_assets/mobile_polllist.png" alt="Enquetes Abertas" width="30%"/>
  <img src="readme_assets/mobile_polldetails.png" alt="Detalhe da Enquete" width="30%"/>
</p>

---

## 🛠️ Architecture & Technologies

| Layer       | Technology                                    |
|-------------|-----------------------------------------------|
| **IDE**     | Android Studio (recommended) or IntelliJ IDEA |
| **UI**      | Jetpack Compose                               |
| **Pattern** | MVVM (Model-View-ViewModel)                   |
| **Network** | Retrofit                                      |
| **Async**   | Kotlin Coroutines                             |
| **State**   | StateFlow · SharedFlow                        |
| **Routing** | Navigation Compose                            |

### Why These Choices?

- **Jetpack Compose**: A modern, declarative UI toolkit — replaces XML layouts.
- **MVVM**: Separates UI from business logic, recommended by Google.
- **Retrofit**: Clean and type-safe HTTP client.
- **Coroutines**: Simplifies async tasks, avoiding UI blocking.

---

## ⚙️ Setup


### 0. Prerequisites

Make sure you have the following installed:

- [Android Studio](https://developer.android.com/studio) or [IntelliJ IDEA](https://www.jetbrains.com/idea/download/?section=windows) 
- [Java Development Kit (JDK) > 17](https://adoptium.net/en-GB/)
- [Git](https://git-scm.com/)
- Backend API running locall (see: [pollab-backend](https://github.com/igormahall/pollab-backend))
  > Must be accessible from emulator via `http://10.0.2.2:8000`


### 1. Clone the Repository

```bash
git clone https://github.com/igormahall/pollab-mobile.git
cd pollab-mobile
```

### 2. Open in Android Studio
- Launch **Android Studio** or **IntelliJ IDEA**
- Select **Open Project** → choose the project folder
- Let **Gradle** sync all dependencies

### 3. Run the Django Backend
- Ensure the REST API is up and running locally:
  ```bash
  python manage.py runserver
  ```
> ⚠️ The app expects the backend to be accessible at http://10.0.2.2:8000/ for emulator compatibility.


### 4. Launch the App
- Start an Android emulator or connect a physical device
- Press **Run 'app'** ▶️ in Android Studio

---

## 🧱 Implementation Breakdown

### 🧩 1. Network & Data Layer

- **Data Models**  
  - Kotlin `data class` objects mirror the JSON schema from the Django API.


- **API Service (Retrofit)**  
  - Interface `ApiService` with annotations like `@GET`, `@POST`, `@Path`, `@Body`


- **Localhost Access**  
  - Use `http://10.0.2.2:8000/` in the emulator )maps to host´s localhost)


- **Permissions**  
  - In `AndroidManifest.xml`:
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <application android:usesCleartextTraffic="true" ... /> 
    ```
  
---

### ⚙️ 2. Logic Layer (Repository & ViewModels)

- **Repository Pattern**  
  - `PollRepository` abstracts data fetching from the UI layer.

- **ViewModels** (one per screen):
  - `PollListViewModel`
  - `PollDetailViewModel`
  - `PollFormViewModel`

- **State Management**
  - `StateFlow` : Exposes reactive UI state (loading, success, error).
  - `SharedFlow` : Emits one-time events (snackbars, errors).

- **Concurrency & Safety**  
  Uses `withContext(Dispatchers.IO)` inside `viewModelScope` to avoid blocking the UI thread

---

### 🎨 3. Presentation Layer (Jetpack Compose UI)

- **Navegation**  
  - Handled via `Navigation Compose` in `AppNavigation.kt`:
  ```kotlin
  navController.navigate("/enquetes/{pollId}")
  ```

- **Composables**
  - `PollListScreen`
  - `PollDetailScreen`
  - `PollFormScreen`


- **Modern UI Components**  
  - `LazyColumn` : Efficient scrolling lists
  - `Card` : For individual pools
  - `OutlinedTextField` : Pool forms
  - `remember { mutableStateOf(...) }` : local UI state

---

### 4. UX Enhancements

- **Visual Feedback**
  - Loading snippers
  - Snackbar messages for success or failure
  - Disables buttons async operations

- **Winner Highlighting**
  - The option with most votes is visually emphasized

- **User simulation**
  - A participant name field simulates multiple users and enforces **1 vote per user rule**

---

## 🤝 Contributing

We welcome contributions! If you'd like to suggest improvements, report a bug,
or propose new features, feel free to open an issue or submit a pull request.

---

## 📜 License

Distributed under the **MIT License**.
