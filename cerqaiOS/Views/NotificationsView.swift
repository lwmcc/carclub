//
// NotificationsView.swift
// cerqaiOS
//
// Notifications screen - mirrors Android NotificationScreen
//

import SwiftUI

struct NotificationsView: View {
    var body: some View {
        NavigationStack {
            ContentUnavailableView(
                "No Notifications",
                systemImage: "bell.fill",
                description: Text("You're all caught up!")
            )
            .navigationTitle("Notifications")
        }
    }
}

