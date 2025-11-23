# KMP Shared Architecture

## Overview

This project uses Kotlin Multiplatform (KMP) to share business logic between Android and iOS while keeping platform-specific code (Amplify SDK calls, UI) separate.

## Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                     Platform Layer                          │
│  ┌──────────────────────┐      ┌──────────────────────┐   │
│  │  Android (Compose)   │      │    iOS (SwiftUI)     │   │
│  │  - MainActivity      │      │    - ContentView     │   │
│  │  - Compose UI        │      │    - SwiftUI Views   │   │
│  └──────────┬───────────┘      └───────────┬──────────┘   │
└─────────────┼──────────────────────────────┼──────────────┘
              │                              │
┌─────────────┼──────────────────────────────┼──────────────┐
│             │   Shared ViewModels          │              │
│  ┌──────────┴──────────┐      ┌────────────┴──────────┐  │
│  │ SharedMainViewModel │      │SharedContactsViewModel│  │
│  │  - User state       │      │  - Contacts state     │  │
│  │  - Loading state    │      │  - UI state           │  │
│  └──────────┬──────────┘      └────────────┬──────────┘  │
└─────────────┼──────────────────────────────┼──────────────┘
              │                              │
┌─────────────┼──────────────────────────────┼──────────────┐
│             │   Repository Interfaces      │              │
│  ┌──────────┴──────────┐      ┌────────────┴──────────┐  │
│  │  ContactsRepository │      │   LocalRepository     │  │
│  │  - fetchContacts()  │      │   - getUserId()       │  │
│  │  - createContact()  │      │   - setUserData()     │  │
│  └─────────────────────┘      └───────────────────────┘  │
└──────────────────────────────────────────────────────────┘
              │                              │
┌─────────────┼──────────────────────────────┼──────────────┐
│             │Platform Implementations      │              │
│  ┌──────────┴──────────────────┐   ┌──────┴────────────┐ │
│  │ Android (Amplify Android)   │   │ iOS (Amplify Swift)│
│  │ - SharedContactsRepoImpl    │   │ - (To be created) │ │
│  │ - SharedLocalRepoImpl       │   │                   │ │
│  │ - Uses Amplify Android SDK  │   │ - Uses Amplify    │ │
│  └─────────────────────────────┘   │   Swift SDK       │ │
└────────────────────────────────────┴───────────────────────┘
```

## What's Shared (in `/shared` module)

### Domain Models (`com.cerqa.domain.model`)
- `User.kt` - User data model
- `Contact.kt` - Contact models (StandardContact, ReceivedContactInvite, SentContactInvite)
- `NetworkResponse.kt` - Network response wrapper
- `Result.kt` - Various result types
- `LocalContact.kt` - Device contact models

### Repository Interfaces (`com.cerqa.domain.repository`)
- `ContactsRepository` - Interface for contact operations
- `UserRepository` - Interface for user operations
- `LocalRepository` - Interface for local storage

### ViewModels (`com.cerqa.viewmodels`)
- `SharedMainViewModel` - Main app state management
- `SharedContactsViewModel` - Contacts list management

## Platform-Specific Code

### Android (`/app` module)

**Repository Implementations** (`com.mccartycarclub.repository.shared`)
- `SharedContactsRepositoryImpl` - Wraps existing Android `ContactsRepository`
- `SharedLocalRepositoryImpl` - Wraps existing Android `LocalRepository`

**DI Modules** (`com.mccartycarclub.di`)
- `SharedRepositoryModule` - Provides shared repository implementations
- `SharedViewModelModule` - Provides shared ViewModels

**Model Mapping**
- Extension functions convert between Android models and shared models
- Android models use Android-specific types (Uri, Moshi annotations)
- Shared models use KMP-compatible types (String, kotlinx.serialization)

### iOS (`/cerqaiOS`)

**To be implemented:**
- Swift repository implementations using Amplify Swift SDK
- XCFramework integration
- SwiftUI integration with shared ViewModels

## Key Benefits

1. **Shared Business Logic**: ViewModels and domain models are written once, used on both platforms
2. **Platform-Specific SDKs**: Each platform uses its native Amplify SDK (Amplify Android / Amplify Swift)
3. **Type-Safe**: All models are strongly typed with Kotlin/Swift
4. **Testable**: Shared code can be tested once for both platforms
5. **Maintainable**: Changes to business logic only need to be made in one place

## Dependencies

### Shared Module
- `kotlinx-coroutines-core` - Coroutines support
- `kotlinx-serialization-json` - JSON serialization
- `kotlinx-datetime` - Date/time handling
- `androidx.lifecycle` - ViewModel support
- `compose.runtime` - State management

### Android App
- `project(":shared")` - Shared KMP module
- Amplify Android SDK
- Hilt for DI
- Jetpack Compose

### iOS App (To be added)
- Shared.xcframework - Compiled shared module
- Amplify Swift SDK
- SwiftUI

## Next Steps

1. ✅ Create shared domain models
2. ✅ Create shared repository interfaces
3. ✅ Create shared ViewModels
4. ✅ Create Android repository adapters
5. ✅ Add Hilt DI modules
6. 🔄 Update MainActivity to use SharedMainViewModel
7. ⏳ Build XCFramework for iOS
8. ⏳ Create iOS repository implementations in Swift
9. ⏳ Integrate shared ViewModels in iOS

## Usage Example

### Android (Compose)
```kotlin
@Composable
fun MyScreen(viewModel: SharedMainViewModel = hiltViewModel()) {
    val userId by viewModel.userId.collectAsState()

    // UI code using shared state
    Text("User ID: $userId")
}
```

### iOS (SwiftUI) - To be implemented
```swift
struct MyView: View {
    @StateObject var viewModel = SharedMainViewModel(...)

    var body: some View {
        Text("User ID: \\(viewModel.userId)")
    }
}
```
