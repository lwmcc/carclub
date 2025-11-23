//
// KoinHelper.swift
// cerqaiOS
//
// Helper to initialize Kotlin Multiplatform Koin DI
//

import Foundation
import shared

class KoinHelper {
    static func initialize() {
        // Initialize Koin from KMP shared module
        KoinHelperKt.doInitKoin()
    }
}
