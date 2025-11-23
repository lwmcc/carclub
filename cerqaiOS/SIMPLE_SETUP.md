# Simple Car Club iOS - Setup Guide

## What's Included

✅ **Amplify Authentication** - Same flow as Android Compose Authenticator
✅ **Bottom Navigation** - Home, Chat, Notifications tabs
✅ **Same Look** - Matches Android MainActivity layout

❌ No Flutter integration (add later)
❌ No ViewModels (add later)
❌ No backend calls (add later)

## Quick Setup (3 Steps)

### 1. Add Amplify Authenticator Package

In Xcode:
- **File** → **Add Package Dependencies...**
- URL: `https://github.com/aws-amplify/amplify-ui-swift-authenticator`
- Version: `2.0.0` or later
- Add **Authenticator** to target

### 2. Add Amplify Configuration

Copy from Android:
```bash
cp app/src/main/res/raw/amplifyconfiguration.json cerqaiOS/
```

Add to Xcode project (ensure "Copy items if needed" is checked)

### 3. Build & Run

```bash
open cerqaiOS.xcodeproj
# Press ⌘R to run
```

## What You Get

### Sign In/Up Flow
```
Launch App
    ↓
Amplify Authenticator (Sign In/Up screens)
    ↓
After Login → ContentView with tabs
```

### Bottom Navigation
- **Home** - Welcome screen with app info
- **Chat** - Placeholder (will connect to Flutter later)
- **Notifications** - Placeholder

## File Structure (Simplified)

```
cerqaiOS/
├── cerqaiOSApp.swift          # App entry, Amplify config
├── RootView.swift             # Authenticator wrapper
├── ContentView.swift          # Bottom tab navigation
└── Views/
    ├── HomeView.swift         # Home tab content
    ├── ChatView.swift         # Chat tab placeholder
    └── NotificationsView.swift # Notifications placeholder
```

## Comparison with Android

### Android (MainActivity.kt)
```kotlin
Authenticator { state ->
    StartScreen(mainViewModel, state = state, ...)
}
```

### iOS (RootView.swift)
```swift
Authenticator { state in
    ContentView() // Same TabView structure
}
```

## What to Add Next

1. **Flutter Integration** - Connect chat functionality
2. **Backend Calls** - Fetch real data from Amplify
3. **ViewModels** - Add business logic
4. **KMP Shared Code** - Connect to shared module

## Troubleshooting

**Build fails**: Ensure Authenticator package is added
**Config error**: Verify amplifyconfiguration.json is in bundle
**Auth fails**: Check AWS Cognito user pool settings

That's it! Simple, clean, and ready for you to build on. 🚀
