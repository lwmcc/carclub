//
// ContentView.swift
// cerqaiOS
//
// Main view with bottom navigation - mirrors Android StartScreen
//

import SwiftUI

struct ContentView: View {
    @State private var selectedTab: Tab = .home

    var body: some View {
        TabView(selection: $selectedTab) {
            // Home Tab
            HomeView()
                .tabItem {
                    Label("Home", systemImage: "house")
                }
                .tag(Tab.home)

            // Chat Tab
            ChatView()
                .tabItem {
                    Label("Chat", systemImage: "message")
                }
                .tag(Tab.chat)

            // Notifications/Inbox Tab
            NotificationsView()
                .tabItem {
                    Label("Notifications", systemImage: "bell")
                }
                .tag(Tab.notifications)
        }
        .accentColor(.blue) // Tab selection color
    }
}

enum Tab {
    case home
    case chat
    case notifications
}

