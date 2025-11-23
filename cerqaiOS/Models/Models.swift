//
// Models.swift
// cerqaiOS
//
// Swift data models matching Android/KMP models
//

import Foundation

// MARK: - Contact Models

struct Contact: Identifiable, Codable {
    let id: String
    let userName: String?
    let phoneNumber: String?
    let userId: String?
    let avatarUri: String?

    var displayName: String {
        userName ?? phoneNumber ?? "Unknown"
    }
}

// MARK: - Chat Models

struct Chat: Identifiable, Codable {
    let id: String
    let chatId: String?
    let userName: String?
    let avatarUri: String?
    let userId: String?
    let lastMessage: String?
    let timestamp: Date?
    let unreadCount: Int?

    var displayName: String {
        userName ?? "Unknown"
    }
}

struct Message: Identifiable, Codable {
    let id: String
    let messageId: String?
    let content: String
    let senderId: String
    let createdAt: Date
    let isFromMe: Bool
}

// MARK: - Group Models

struct Group: Identifiable, Codable {
    let id: String
    let groupId: String?
    let groupName: String?
    let groupAvatarUri: String?
    let memberCount: Int?
    let lastActivity: Date?

    var displayName: String {
        groupName ?? "Unnamed Group"
    }
}

// MARK: - User Models

struct UserData: Codable {
    let userId: String
    let userName: String?
    let email: String?
    let phoneNumber: String?
    let firstName: String?
    let lastName: String?
    let avatarUri: String?

    var displayName: String {
        if let first = firstName, let last = lastName {
            return "\(first) \(last)"
        }
        return userName ?? email ?? "User"
    }
}

// MARK: - Channel Models

struct ChannelsItem: Identifiable, Codable {
    let id: String
    let receiverId: String?
    let userName: String?
    let avatarUri: String?
}
