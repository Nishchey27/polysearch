# 🌍 PolySearch: AI-Powered Multilingual Search Proxy

**PolySearch** is a smart search engine proxy that breaks down language barriers. It allows users to search the global web in their native language and receive fully translated results, giving them access to information that was previously hidden behind the "**language wall**."

This project was built for the **Lingo.dev hackathon**, demonstrating a full-stack, polyglot microservice architecture.

## Problem: The "Language-Walled" Internet

The internet is dominated by English-language content. This creates an "information inequality" where users searching in other languages may miss out on the best, most relevant resources simply because they aren't in their native tongue.

* **Before PolySearch:** A user searches in Hindi. They only get results from the small "Hindi internet."
* **After PolySearch:** A user searches in Hindi. PolySearch translates the query, searches the *entire* global internet, and translates the top results *back* to Hindi.

<img width="500" height="500" alt="Gemini_Generated_Image_j02mwbj02mwbj02m" src="https://github.com/user-attachments/assets/7a249698-0d0a-4a21-8365-650dec2bd57f" />

---

## ✨ Key Features

* **Multilingual UI (Static i18n):** The entire user interface (buttons, slogans, titles) is translated based on the user's preferred language, powered by **Lingo.dev CLI**-generated JSON files.
* **Accessibility:** Includes a **Speech-to-Text (STT)** feature integrated directly into the search bar for hands-free, multilingual input.
* **Multilingual Querying:** Search in any language (e.g., Hindi, Spanish, etc.).
* **Real-Time Translation:** The top search results (titles and snippets) are translated back to your original language.
* **High-Performance Batching:** Optimized to translate all 20 results (title + snippet) in a single API call, reducing latency from ~60 seconds to **~3-5 seconds**.
* **Microservice Architecture:** A robust **Spring Boot** backend orchestrates a **Node.js** "sidecar" proxy to handle specialized API communication.

---

## 🛠️ Tech Stack & Architecture

This project uses a polyglot microservice architecture to solve a complex integration challenge.

* **Backend:** **Java 21** & **Spring Boot**
    * Handles all core logic, API orchestration, and serving the frontend.
    * Uses **Thymeleaf** for server-side HTML rendering.
* **Frontend:** **htmx** + **Tailwind CSS**
    * `htmx` handles all server interactions (AJAX) for a fast, modern UI without a single line of client-side JavaScript.
    * `Tailwind CSS` provides a professional, "glassmorphism" design.
* **Translation Service:** **Lingo.dev SDK & CLI**
    * **SDK:** Used for dynamic, real-time language detection and translation of search results.
    * **CLI:** Used to manage and generate static JSON files for UI localization.
* **Search Service:** **Serper.dev**
    * Provides Google-quality search results via a REST API.
* **Translation Proxy (Sidecar):** **Node.js + Express**
    * **Why?** The Lingo.dev SDK uses gRPC-web, which is not directly compatible with Spring's standard `RestClient`.
    * **Solution:** A lightweight **Node.js** "sidecar" proxy was built. This proxy uses the official Lingo.dev SDK (JavaScript) and exposes a simple REST API.
    * The Spring Boot app makes a fast, local REST call to this proxy, which then handles the complex gRPC-web communication. This is a standard pattern for integrating incompatible technologies.

<img width="700" height="700" alt="diagram-export-11-16-2025-1_39_13-AM" src="https://github.com/user-attachments/assets/96d42c32-b995-45aa-ab59-caaeec7c4877" />

---

## 🚀 How to Run Locally

This project has two parts: the Java Spring Boot server and the Node.js proxy. **Both must be running.**

### 1. Prerequisites

* **Java JDK 21**
* **Node.js 18** or newer
* An API key from **Serper.dev**
* An API key from **Lingo.dev**

### 2. Configure Your Secrets

1.  **Backend (Java):** Create a file at `src/main/resources/application-secrets.properties` and add your Serper key:
    ```properties
    serper.api.key=YOUR_SERPER_API_KEY_HERE
    serper.api.url=[https://google.serper.dev/search](https://google.serper.dev/search)
    ```

2.  **Lingo.dev Keys (Root & Proxy):**
    * Create a file at the project **root** named **`.env`** for the CLI.
    * Create a file at **`lingo-proxy/.env`** for the Node.js server.
    * **Both files must contain:**
    ```
    LINGO_API_KEY=YOUR_LINGO_API_KEY_HERE
    ```

### 3. Setup Static UI Translations (CLI)

Before running the app, you must generate the initial UI translation files.

1.  Create the master English file at **`i18n/en.json`** (contains all UI keys like `header.title`).
2.  In the project root, run the CLI:
    ```bash
    npx lingo.dev@latest run
    ```
    This will generate the target files (e.g., `es.json`, `hi.json`).

### 4. Run the Application

You can run both servers manually or use the included VS Code `launch.json` to run them with one click (F5).

**A) The Manual Way (Two Terminals)**

**Terminal 1: Start the Lingo.dev Proxy**
```bash
# Navigate to the proxy folder
cd lingo-proxy

# Install dependencies (only once)
npm install

# Start the server
node server.js
```
**Terminal 2: Start the Spring Boot App**
```# In the project's root 'backend' folder
./mvnw spring-boot:run

... Tomcat started on port(s): 8080 (http) ...
```
### 5. Open the App
```Your PolySearch app is now running! Open your browser and go to:

http://localhost:8080
```
**⚡ API Endpoint (Advanced)**
While the main application is the htmx web UI, the Spring Boot backend also exposes a raw JSON API. You can test this directly using a tool like Postman, Insomnia, or cURL.

```Method: POST URL:
http://localhost:8080/api/v1/search
```
Example JSON Body (for Postman)
```Set your request type to POST and the body to raw -> JSON:
{
  "query": "मुफ्त जावा कोर्स"
}
```
Example Success Response (JSON)
```PolySearch will detect the language, translate the query, search, and translate the results back, returning a clean JSON array:
[
  {
    "title": "जावा ट्यूटोरियल: जावा प्रोग्रामिंग सीखें",
    "snippet": "मुफ्त कोर्स! प्रोग्रामर्स के लिए जावा। अनुभवी प्रोग्रामर्स के लिए बनाया गया यह कोर्स...",
    "url": "[https://www.codecademy.com/learn/learn-java](https://www.codecademy.com/learn/learn-java)"
  },
  {
    "title": "बिल्कुल नौसिखियों के लिए सर्वश्रेष्ठ मुफ्त जावा कोर्स : r/learnjava",
    "snippet": "हम हेलसिंकी विश्वविद्यालय से MOOC की सिफारिश करते हैं। यह एक पाठ्य कोर्स है...",
    "url": "[https://www.reddit.com/r/learnjava/comments/](https://www.reddit.com/r/learnjava/comments/)..."
  }
]
```
**AI assitance disclosure :**
I used AI tools for help with certain parts of the project- mainly for clarifying concepts, and generating code snippets for the technology i was learning for the first time. All AI generated suggestions were reviewed, tested, and integrated by me. the core idea architecture logic final implementaion are my own.
