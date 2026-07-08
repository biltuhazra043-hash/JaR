# JARVIS AI - Personal Android Voice Assistant

> A premium futuristic AI assistant inspired by Iron Man's JARVIS. Built with Kotlin + Jetpack Compose.

## Overview

JARVIS AI is NOT a chatbot — it's your personal AI operating system inside Android. It works through voice, text, and automation. You own the AI completely. No branding from ChatGPT, Gemini, Claude, or OpenAI appears anywhere.

## Features

### 🎤 Voice System
- Always-on wake word detection ("Hey Jarvis")
- Natural voice output with streaming speech
- Multi-language support: English, Hindi, Bengali
- Automatic language detection
- Interruptible speech with context awareness
- Continuous conversation with memory

### 🤖 AI Provider Configuration
- **OpenRouter** — Access GPT-4, Claude, Gemini, Mistral, DeepSeek, and more
- **Ollama** — Run local models (Llama3, Qwen, Phi, Mistral, Gemma, DeepSeek)
- **Custom APIs** — Any OpenAI-compatible endpoint
- Per-provider settings: temperature, top-p, max tokens, system prompt
- Streaming support for real-time responses
- Test connection functionality

### 🎨 Iron Man UI
- Premium dark theme: Black, Gunmetal, Red, Blue Glow
- Animated Arc Reactor on home screen
- Glassmorphism effects
- Voice waveform visualization
- Iron Man mask with glowing eyes
- Smooth 120 FPS animations
- Particle effects

### 📱 Device Control
- Open/close apps
- Flashlight, WiFi, Bluetooth toggles
- Volume and brightness control
- Silent mode / DND
- Alarms, calendar, SMS, phone calls
- Clipboard, battery info, storage, RAM
- Media control (play/pause/next/previous)
- Screenshots and navigation
- File search and management

### 🔄 Automation & Routines
- Morning routine (weather, news, calendar)
- Night routine (DND, summary)
- Location-based triggers
- Battery/charging routines
- Custom automation with JSON config

### 💾 Memory System
- Persistent memory across conversations
- User preferences and nicknames
- Semantic search
- Categories: general, preference, routine, note, task
- Export/import memory

### 📨 Telegram Integration
- Remote control via Telegram bot
- Send commands to Jarvis from anywhere
- Receive notifications and screenshots
- AI-powered responses to Telegram messages

### 🔒 Security
- Biometric lock (fingerprint/face)
- PIN lock
- Encrypted API keys
- Encrypted database (Room)
- Encrypted preferences

### 📊 Dashboard
- Weather widget
- Battery, RAM, Storage, CPU stats
- Upcoming events and reminders
- Recent conversations

## Architecture

```
MVVM + Repository Pattern
├── Data Layer
│   ├── Room Database (encrypted)
│   ├── DataStore Preferences
│   ├── Retrofit + OkHttp (API)
│   └── Repository Pattern
├── Domain Layer
│   ├── Use Cases
│   └── Domain Models
├── Presentation Layer (Jetpack Compose)
│   ├── Home Screen
│   ├── Chat Screen
│   ├── Dashboard
│   ├── Settings
│   ├── AI Provider
│   ├── Automation
│   ├── Memory
│   ├── Files
│   └── Security
└── Services
    ├── Voice Service (foreground)
    ├── Accessibility Service
    ├── Notification Listener
    └── Telegram Bot Service
```

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **DI:** Hilt (Dagger)
- **Database:** Room (encrypted)
- **Network:** Retrofit + OkHttp + SSE streaming
- **Async:** Kotlin Coroutines + Flow
- **Preferences:** DataStore
- **Animations:** Compose Animation + Lottie
- **Security:** EncryptedSharedPreferences, Biometric API
- **Background:** WorkManager + Foreground Services

## Requirements

- Android 10+ (API 29+)
- Target: API 35
- Min SDK: 29

## Permissions

- Microphone (voice input)
- Accessibility (device control)
- Notification Access (smart notifications)
- Overlay (always-on display)
- Storage (file management)
- Bluetooth
- Location (optional, for automation)
- Phone / SMS (optional)
- Calendar / Contacts
- Camera

## Building

```bash
./gradlew assembleDebug
```

## Project Structure

```
app/src/main/java/com/jarvis/ai/
├── JarvisApp.kt          # Application class with Hilt
├── di/                    # Dependency Injection
├── data/
│   ├── db/               # Room entities, DAOs, database
│   ├── preferences/      # DataStore preferences
│   ├── remote/           # API client (OpenAI-compatible)
│   └── repository/       # Data repositories
├── domain/
│   ├── model/            # Domain models
│   └── usecase/          # Use cases
├── service/              # Voice, Accessibility, Notification services
├── device/               # Device control utilities
├── telegram/             # Telegram bot integration
├── receiver/             # Boot receiver, Widget provider
├── ui/
│   ├── theme/            # Iron Man dark theme
│   ├── navigation/       # Navigation graph
│   ├── components/       # Arc Reactor, Mask, Waveform components
│   ├── home/             # Home screen with mask animation
│   ├── chat/             # Chat UI with streaming
│   ├── dashboard/        # System stats dashboard
│   ├── settings/         # Settings screens
│   ├── provider/         # AI Provider configuration
│   ├── automation/       # Routines and automation
│   ├── memory/           # Memory management
│   ├── files/            # File manager
│   └── security/         # Biometric/PIN lock
└── util/                 # Constants, extensions
```

## License

Private project. All rights reserved.
