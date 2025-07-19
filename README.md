# 🔐 Authentication & Payment Card Validation App (Work in Progress)

This mobile application combines user authentication and payment card validation, built with **Kotlin**, **Jetpack Compose**, **MVVM**, and **Koin** for dependency injection. The goal is to deliver a modern, robust authentication flow using email/password and Google Sign-In, alongside a custom payment card validation feature, all following Google's best practices and clean architecture principles.

## 🛠️ Technologies Used

- **Kotlin**
- **Jetpack Compose** – declarative UI development
- **MVVM Architecture** – clear separation of concerns
- **Koin** – dependency injection
- **Firebase Authentication**
- **Google Sign-In**
- **Password Reset** (via Firebase Hosting)
- **Sentry** – real-time error monitoring and performance tracking

## 🚧 Project Status

This project is actively under development. Core authentication and card validation features are implemented and undergoing testing.

### Completed Features

- Full authentication flow (Sign Up, Sign In, Forgot Password)
- Firebase Authentication and Google Sign-In integration
- Real-time payment card validation with user feedback
- Navigation between screens using Jetpack Compose Navigation
- Sentry Integration: The app now features robust error and performance monitoring with Sentry. All crashes, exceptions, and ANRs are automatically captured, helping ensure reliability and fast issue resolution.
- Navigation Tracking in Jetpack Compose: While Sentry’s navigation tracking is automatic for traditional navigation, in Jetpack Compose we’ve implemented custom breadcrumbs to track user navigation between screens. This provides valuable context for debugging and user flow analysis.

## 📱 Screenshots

### Sign Up Screen

<img src="app/src/main/java/com/example/authapp/assets/screenshots/createaccount.png" width="300" alt="Sign Up">

<img src="app/src/main/java/com/example/authapp/assets/screenshots/googlesignup.png" width="300" alt="Sign Up with Google">

### Sign In Screen

<img src="app/src/main/java/com/example/authapp/assets/screenshots/signin.png.png" width="300" alt="Sign In">

<img src="app/src/main/java/com/example/authapp/assets/screenshots/signingoogle.png" width="300" alt="Sign In with Google">

### Home Screen

<img src="app/src/main/java/com/example/authapp/assets/screenshots/home.png" width="300" alt="Home">

---

## 📋 Roadmap

- [✅] Firebase Authentication integration
- [✅] Complete Forgot Password flow
- [  ] Unit and instrumentation tests

## 🤝 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests for improvements or suggestions.

## 📄 License

This project is licensed under the **MIT License**.

---

Built with ❤️ using Kotlin and Jetpack Compose.