# Amplify Authenticator UI Setup for Swift

## Overview
Now using **Amplify Authenticator UI** for Swift to match your Android Compose Authenticator implementation.

## Installation

### Add Swift Package Dependency

1. **Open Xcode** → `cerqaiOS.xcodeproj`

2. **Add Package**:
   - File → Add Package Dependencies...
   - Enter URL: `https://github.com/aws-amplify/amplify-ui-swift-authenticator`
   - Version: Latest (2.x.x)
   - Click "Add Package"

3. **Select Products**:
   - ✅ Check **Authenticator**
   - Add to Target: cerqaiOS
   - Click "Add Package"

### Alternative: SPM Package.swift

If using SPM directly:

```swift
dependencies: [
    .package(
        url: "https://github.com/aws-amplify/amplify-ui-swift-authenticator",
        from: "2.0.0"
    )
]
```

## Code Comparison: Android vs iOS

### Android (Compose)

```kotlin
// MainActivity.kt
setContent {
    AppTheme {
        Authenticator(
            state = stateProvider.provide(),
            modifier = Modifier.fillMaxHeight(),
        ) { state ->
            mainViewModel.setLoggedInUserId(userId = state.user.userId)
            StartScreen(
                mainViewModel,
                state = state,
                // ... other params
            )
        }
    }
}
```

### iOS (SwiftUI) - Now Matching!

```swift
// RootView.swift
var body: some View {
    Authenticator { state in
        ContentView()
            .environmentObject(mainViewModel)
            .onAppear {
                if let userId = state.user.userId,
                   let username = state.user.username {
                    mainViewModel.setUserData(
                        userId: userId,
                        userName: username
                    )
                }
            }
    }
}
```

## Features Included

The Amplify Authenticator UI provides:

### ✅ **Automatic Screens**
- Sign In
- Sign Up
- Confirm Sign Up (email/SMS verification)
- Password Reset
- Confirm Password Reset
- MFA Setup
- TOTP Setup

### ✅ **Built-in Features**
- Form validation
- Error handling
- Loading states
- Accessibility
- Dark mode support
- Localization

### ✅ **State Management**
- Automatic auth state tracking
- Session management
- User attribute handling

## Configuration

### Basic Usage (Current)

```swift
Authenticator { state in
    // Your signed-in content
    ContentView()
}
```

### With Customization

```swift
Authenticator(
    // Add custom header
    headerContent: {
        VStack {
            Image(systemName: "car.fill")
                .font(.system(size: 60))
            Text("Car Club")
                .font(.largeTitle)
        }
    },

    // Add custom footer
    footerContent: {
        Text("Terms & Privacy Policy")
            .font(.caption)
    }
) { state in
    ContentView()
}
```

### With Custom Fields

```swift
Authenticator(
    signUpContent: { state in
        SignUpView(state: state) {
            // Add custom field
            TextField(
                "Phone Number",
                text: $state.phoneNumber
            )
            .textContentType(.telephoneNumber)
            .keyboardType(.phonePad)
        }
    }
) { state in
    ContentView()
}
```

## Authentication State

### Access User Info

```swift
Authenticator { state in
    let userId = state.user.userId
    let username = state.user.username
    let email = state.user.email

    ContentView()
        .onAppear {
            print("Signed in as: \(username)")
        }
}
```

### Sign Out

```swift
Button("Sign Out") {
    Task {
        await state.signOut()
    }
}
```

## Customization Options

### Theme Customization

```swift
Authenticator { state in
    ContentView()
}
.tint(.blue) // Custom accent color
```

### Field Configuration

Configure which fields are required:

```swift
Authenticator(
    signUpContent: { state in
        SignUpView(state: state) {
            // Require phone number
            TextField("Phone", text: $state.phoneNumber)
                .textContentType(.telephoneNumber)
        }
    }
) { state in
    ContentView()
}
```

## Migration from Custom Auth

If you want to keep custom auth screens for specific flows:

### Option 1: Use Authenticator Everywhere (Recommended)
Delete custom auth files:
- `Auth/SignInView.swift`
- `Auth/SignUpView.swift`
- `Auth/ConfirmSignUpView.swift`
- `Auth/PasswordResetView.swift`
- `Auth/AuthenticationManager.swift`

### Option 2: Hybrid Approach
Keep custom screens for special cases:

```swift
struct RootView: View {
    @State private var useCustomAuth = false

    var body: some View {
        if useCustomAuth {
            // Custom auth flow
            SignInView()
        } else {
            // Amplify Authenticator
            Authenticator { state in
                ContentView()
            }
        }
    }
}
```

## Troubleshooting

### "Cannot find 'Authenticator' in scope"

**Solution**: Add Authenticator package to your target
1. Xcode → Target → General → Frameworks
2. Ensure `Authenticator` is linked

### Auth configuration issues

**Verify amplifyconfiguration.json**:
```json
{
  "auth": {
    "plugins": {
      "awsCognitoAuthPlugin": {
        "UserAgent": "aws-amplify/cli",
        "Version": "1.0",
        "IdentityManager": {
          "Default": {}
        },
        "CredentialsProvider": {
          "CognitoIdentity": {
            "Default": {
              "PoolId": "YOUR_POOL_ID",
              "Region": "YOUR_REGION"
            }
          }
        },
        "CognitoUserPool": {
          "Default": {
            "PoolId": "YOUR_USER_POOL_ID",
            "AppClientId": "YOUR_APP_CLIENT_ID",
            "Region": "YOUR_REGION"
          }
        }
      }
    }
  }
}
```

### Sign up/in not working

**Check Cognito settings**:
- Username attributes (email, phone)
- Password policy
- Auto-confirm settings
- MFA requirements

## Complete Example

```swift
import SwiftUI
import Authenticator

@main
struct CarClubApp: App {
    init() {
        configureAmplify()
    }

    var body: some Scene {
        WindowGroup {
            Authenticator { state in
                TabView {
                    HomeView()
                        .tabItem {
                            Label("Home", systemImage: "house")
                        }

                    ChatView()
                        .tabItem {
                            Label("Chat", systemImage: "message")
                        }

                    NotificationsView()
                        .tabItem {
                            Label("Inbox", systemImage: "bell")
                        }
                }
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Sign Out") {
                            Task {
                                await state.signOut()
                            }
                        }
                    }
                }
            }
        }
    }

    private func configureAmplify() {
        // Amplify configuration
    }
}
```

## Resources

- [Amplify UI Authenticator Docs](https://ui.docs.amplify.aws/swift/connected-components/authenticator)
- [Amplify Swift Auth](https://docs.amplify.aws/swift/build-a-backend/auth/)
- [GitHub Repository](https://github.com/aws-amplify/amplify-ui-swift-authenticator)

## Summary

✅ Now matches Android Compose Authenticator exactly
✅ Less code to maintain
✅ Automatic validation & error handling
✅ Built-in theming & accessibility
✅ Same authentication flow across platforms
