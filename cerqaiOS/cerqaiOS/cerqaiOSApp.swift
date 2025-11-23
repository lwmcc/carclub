//
// cerqaiOSApp.swift
// cerqaiOS
//
// SwiftUI App entry point - matches Android MainActivity
//

import SwiftUI
// TODO: Add Amplify via SPM
// import Amplify
// import AWSCognitoAuthPlugin
// import AWSAPIPlugin

@main
struct cerqaiOSApp: App {

    init() {
        // configureAmplify()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }

    /*
    private func configureAmplify() {
        do {
            try Amplify.add(plugin: AWSCognitoAuthPlugin())
            try Amplify.add(plugin: AWSAPIPlugin())
            try Amplify.configure()
            print("Amplify configured")
        } catch {
            print("Amplify configuration failed: \(error)")
        }
    }
    */
}
