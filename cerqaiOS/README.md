# Car Club iOS App - Setup Guide

## Overview
This is a complete SwiftUI implementation of the Car Club app that mirrors the Android Compose UI, with full authentication and KMP integration.

## Project Structure

```
cerqaiOS/
├── Auth/                           # Authentication screens
│   ├── AuthenticationManager.swift # Amplify auth state management
│   ├── SignInView.swift           # Login screen
│   ├── SignUpView.swift           # Registration screen
│   ├── ConfirmSignUpView.swift    # Email confirmation
│   └── PasswordResetView.swift    # Password reset flow
├── Views/                          # Main app screens
│   ├── HomeView.swift             # Home dashboard
│   ├── ChatView.swift             # Chat list
│   ├── NotificationsView.swift    # Notifications/Inbox
│   ├── ContactsView.swift         # Contacts list
│   ├── GroupsView.swift           # Groups list
│   ├── ContactsSearchView.swift   # Search contacts
│   └── GroupsAddView.swift        # Create new group
├── ViewModels/                     # Business logic
│   ├── ChatViewModel.swift        # Chat management
│   └── ContactsViewModel.swift    # Contact management
├── KMP/                            # Kotlin Multiplatform integration
│   ├── KoinHelper.swift           # Koin DI initialization
│   └── ViewModelHelper.swift      # KMP ViewModel wrappers
├── Models/                         # Data models
│   └── Models.swift               # Contact, Chat, Group, User models
├── ContentView.swift               # Main TabView with bottom navigation
├── RootView.swift                  # Authentication wrapper
├── cerqaiOSApp.swift              # App entry point
└── AppDelegate.swift               # Amplify configuration

```

## Features Implemented

### ✅ Authentication (Amplify Auth)
- Sign In with username/password
- Sign Up with email confirmation
- Password reset flow
- Session management
- Auto sign-in after registration

### ✅ Bottom Navigation
- **Home** - Main dashboard with quick access to Contacts & Groups
- **Chat** - Conversation list with unread counts
- **Notifications** - Inbox for notifications

### ✅ Contextual Top Navigation
- Home screen: Contacts, Groups icons
- Contacts screen: Search icon
- Groups screen: Add icon

### ✅ Screen Views
- HomeView - Dashboard
- ChatView - List of conversations
- NotificationsView - Inbox
- ContactsView - Contact list
- GroupsView - Group list
- ContactsSearchView - Search and invite
- GroupsAddView - Create group with member selection

### ✅ KMP Integration
- Koin dependency injection setup
- Shared ViewModel integration
- Platform-specific implementations

## Setup Instructions

### 1. Add Files to Xcode Project

1. Open `cerqaiOS.xcodeproj` in Xcode
2. Create folder groups:
   - Right-click project → New Group → "Auth"
   - Repeat for: Views, ViewModels, KMP, Models
3. Add all `.swift` files to their respective groups
4. Ensure files are added to the target

### 2. Configure Amplify

Add `amplifyconfiguration.json` and `awsconfiguration.json` to your project:

1. Copy these files from your Android app or AWS Amplify console
2. Add to Xcode project
3. Ensure they're in the app bundle

### 3. Update Info.plist

Add SceneDelegate support if using UIKit integration:

```xml
<key>UIApplicationSceneManifest</key>
<dict>
    <key>UIApplicationSupportsMultipleScenes</key>
    <false/>
    <key>UISceneConfigurations</key>
    <dict>
        <key>UIWindowSceneSessionRoleApplication</key>
        <array>
            <dict>
                <key>UISceneConfigurationName</key>
                <string>Default</string>
                <key>UISceneDelegateClassName</key>
                <string>$(PRODUCT_MODULE_NAME).SceneDelegate</string>
            </dict>
        </array>
    </dict>
</dict>
```

### 4. Add Dependencies

In your `Package.swift` or Xcode SPM:

```swift
dependencies: [
    .package(url: "https://github.com/aws-amplify/amplify-swift", from: "2.0.0")
]
```

Add frameworks to your target:
- Amplify
- AWSCognitoAuthPlugin
- AWSAPIPlugin

### 5. Link KMP Shared Module

1. Build the shared KMP framework:
   ```bash
   cd ../shared
   ./gradlew :shared:assembleXCFramework
   ```

2. In Xcode, add the framework:
   - Target → General → Frameworks, Libraries, and Embedded Content
   - Click "+" → Add Other → Add Files
   - Navigate to `shared/build/XCFrameworks/debug/shared.xcframework`

### 6. Build and Run

1. Select a simulator or device
2. Build (⌘B)
3. Run (⌘R)

## Connecting to Backend

### Fetch Real Contacts from Amplify

Update `ContactsViewModel.swift`:

```swift
func fetchContacts() {
    isLoading = true

    Task {
        do {
            // Get current user
            let user = try await Amplify.Auth.getCurrentUser()

            // Query UserContact table
            let request = GraphQLRequest<String>(
                document: """
                    query GetUserContacts($userId: ID!) {
                        getUser(id: $userId) {
                            contacts {
                                items {
                                    user {
                                        userId
                                        userName
                                        phone
                                        avatarUri
                                    }
                                }
                            }
                        }
                    }
                """,
                variables: ["userId": user.userId],
                responseType: String.self
            )

            let result = try await Amplify.API.query(request: request)

            // Parse and update contacts
            // ... decode JSON and update self.contacts

        } catch {
            errorMessage = error.localizedDescription
        }

        isLoading = false
    }
}
```

### Fetch Chats

Update `ChatViewModel.swift` similarly to fetch from your backend.

## Architecture

### MVVM Pattern
- **Views**: SwiftUI views (passive, declarative)
- **ViewModels**: Business logic, state management
- **Models**: Data structures
- **Services**: AuthenticationManager, KMP integration

### State Management
- `@StateObject` for ViewModel ownership
- `@Published` for reactive updates
- `@EnvironmentObject` for dependency injection
- Combine framework for async operations

### Navigation
- `NavigationStack` for hierarchical navigation
- `TabView` for bottom tabs
- `NavigationLink` for push navigation
- Sheet modals for secondary flows

## Next Steps

1. **Implement GraphQL Queries**: Connect ViewModels to Amplify API
2. **Real-time Updates**: Add Amplify DataStore subscriptions
3. **Push Notifications**: Integrate FCM/APNs
4. **Image Handling**: Add avatar upload/display
5. **Offline Support**: Implement local caching
6. **Error Handling**: Add retry logic and user feedback
7. **Testing**: Write unit and UI tests
8. **CI/CD**: Set up automated builds

## Troubleshooting

### Build Errors

**"Cannot find 'shared' in scope"**
- Ensure KMP framework is linked
- Check Framework Search Paths in Build Settings

**"Amplify not configured"**
- Verify `amplifyconfiguration.json` is in bundle
- Check Amplify initialization in `cerqaiOSApp.swift`

**"Koin initialization failed"**
- Ensure shared module exports Koin classes
- Check `initKoin()` is called in KoinHelper

### Runtime Errors

**Authentication fails**
- Verify AWS Cognito credentials
- Check network connectivity
- Confirm user pool configuration

**Empty data**
- Check GraphQL queries
- Verify backend permissions
- Test API endpoints

## Resources

- [Amplify Swift Documentation](https://docs.amplify.aws/swift/)
- [SwiftUI Documentation](https://developer.apple.com/documentation/swiftui)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)

## Support

For issues or questions, see:
- Android implementation: `app/src/main/java/com/mccartycarclub/`
- Shared KMP code: `shared/src/`
