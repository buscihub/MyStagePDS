# MyStage 🎭

**MyStage** is a comprehensive desktop application developed in JavaFX, designed to empower artists in managing their professional profiles, portfolios, and career documents. It provides a centralized hub where artists can create dedicated "rooms" (*stanze*) to showcase their work, handle secure document storage, and manage their career progression.

This project was developed with a strong focus on software engineering principles, employing a structured layered architecture and robust data management.

## ✨ Key Features

*   **Robust Authentication System**: 
    *   Standard login/registration with Two-Factor Authentication (2FA) via email (OTP).
    *   Mocked SPID (Sistema Pubblico di Identità Digitale) integration for secure third-party login flows.
    *   Password recovery system.
*   **Profile & Career Management**: Artists can curate their personal details, stage names, and track their years of career and professional milestones.
*   **Virtual Rooms (*Stanze*)**: Users can create customized, shareable portfolio spaces to present their work to scouters or the public via direct links.
*   **Document Management**: Securely upload, organize, and manage professional documents associated with the artist's profile or specific rooms.
*   **Guest/Scouter View**: Allows unauthenticated users (e.g., talent scouters) to view an artist's room via a direct shareable link.

## 🛠 Technologies & Architecture

*   **Language**: Java 21
*   **GUI Framework**: JavaFX
*   **Database**: SQLite (via `sqlite-jdbc`)
*   **Build Tool**: Maven
*   **Other Libraries**: 
    *   `jakarta.mail` (for email/OTP functionalities)
    *   `Gson` (for JSON serialization)
*   **Architecture**: The application rigorously follows the **BCE (Boundary-Control-Entity)** architectural pattern, ensuring a clean separation of concerns between the user interface, the business logic, and the data model.

## 📂 Project Structure

*   `pkgMain`: Application entry point and JavaFX Router initialization.
*   `pkgBoundary`: Interfaces with external systems, UI views, and the Database layer.
*   `pkgControl`: Contains the core business logic and controllers for the UI (e.g., `AuthCtrl`, `GestioneProfiloCtrl`).
*   `pkgEntity`: Domain models and data structures (`ArtistaEntity`, `StanzaEntity`, etc.).
*   `pkgServer` & `pkgTextmessage`: Utilities for backend interactions, HTTP handling, and UI message dialogs.
*   `pkgUtility`: Helper classes including the JavaFX `Router`, `UserSession` management, and `EmailSender`.

## 🚀 Getting Started

### Prerequisites
*   Java Development Kit (JDK) 21 or higher.
*   Maven.

### Build and Run
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mystage.git
   ```
2. Navigate to the project directory:
   ```bash
   cd mystage/MyStagePDS
   ```
3. Compile and build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn javafx:run
   ```

*(Note: The `database.db` is intentionally excluded from the repository. The application will initialize a new schema or you might need to run initial migrations depending on your local setup).*

## 📄 Documentation
The repository includes comprehensive software engineering documentation detailing the system design:
*   **RAD** (Requirements Analysis Document)
*   **ODD** (Object Design Document)
*   **SDD** (System Design Document)

---
*Developed as a project to demonstrate proficiency in Java desktop development, software architecture, and secure data handling.*
