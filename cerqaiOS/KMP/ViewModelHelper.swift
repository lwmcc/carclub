//
// ViewModelHelper.swift
// cerqaiOS
//
// Helper to access KMP ViewModels from Swift
//

import Foundation
import shared
import Combine

@MainActor
class MainViewModelWrapper: ObservableObject {
    private let viewModel: MainViewModel

    init() {
        // Get MainViewModel from Koin
        self.viewModel = KoinPlatformKt.getKoin().get(objCClass: MainViewModel.self) as! MainViewModel
    }

    func setUserData(userId: String, userName: String) {
        viewModel.setUserData(userId: userId, userName: userName)
    }

    func getUserData() {
        viewModel.getUserData()
    }
}

// Extension to make it easier to work with KMP code
extension KoinApplication {
    func get<T: AnyObject>(type: T.Type) -> T {
        return koin.get(objCClass: type) as! T
    }
}
