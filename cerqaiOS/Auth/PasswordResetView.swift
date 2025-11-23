//
// PasswordResetView.swift
// cerqaiOS
//
// Password reset screen
//

import SwiftUI

struct PasswordResetView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    @Environment(\.dismiss) private var dismiss

    @State private var username = ""
    @State private var showConfirmation = false

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Header
                VStack(spacing: 8) {
                    Image(systemName: "lock.rotation")
                        .font(.system(size: 60))
                        .foregroundColor(.blue)

                    Text("Reset Password")
                        .font(.title)
                        .fontWeight(.bold)

                    Text("Enter your username to receive a reset code")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
                .padding(.top, 40)
                .padding(.bottom, 20)

                // Username Field
                VStack(alignment: .leading, spacing: 8) {
                    Text("Username")
                        .font(.subheadline)
                        .fontWeight(.medium)

                    TextField("Enter your username", text: $username)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                }

                // Error message
                if let errorMessage = authManager.errorMessage {
                    Text(errorMessage)
                        .font(.subheadline)
                        .foregroundColor(.red)
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.red.opacity(0.1))
                        .cornerRadius(8)
                }

                // Send Code Button
                Button(action: {
                    Task {
                        await authManager.resetPassword(username: username)

                        if authManager.errorMessage == nil {
                            showConfirmation = true
                        }
                    }
                }) {
                    if authManager.isLoading {
                        ProgressView()
                            .tint(.white)
                    } else {
                        Text("Send Reset Code")
                            .fontWeight(.semibold)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(username.isEmpty ? Color.gray : Color.blue)
                .foregroundColor(.white)
                .cornerRadius(12)
                .disabled(username.isEmpty || authManager.isLoading)

                // Back to Sign In
                Button(action: {
                    dismiss()
                }) {
                    Text("Back to Sign In")
                        .font(.subheadline)
                        .foregroundColor(.blue)
                }
                .padding(.top, 8)
            }
            .padding(.horizontal, 24)
        }
        .navigationTitle("Password Reset")
        .navigationBarTitleDisplayMode(.inline)
        .navigationDestination(isPresented: $showConfirmation) {
            ConfirmPasswordResetView(username: username)
        }
    }
}

struct ConfirmPasswordResetView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    @Environment(\.dismiss) private var dismiss

    let username: String
    @State private var confirmationCode = ""
    @State private var newPassword = ""
    @State private var confirmNewPassword = ""
    @State private var passwordsMatch = true

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Header
                VStack(spacing: 8) {
                    Text("Enter New Password")
                        .font(.title)
                        .fontWeight(.bold)

                    Text("Check your email for the reset code")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
                .padding(.top, 20)
                .padding(.bottom, 10)

                // Form
                VStack(spacing: 16) {
                    // Confirmation Code
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Confirmation Code")
                            .font(.subheadline)
                            .fontWeight(.medium)

                        TextField("Enter 6-digit code", text: $confirmationCode)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .keyboardType(.numberPad)
                    }

                    // New Password
                    VStack(alignment: .leading, spacing: 8) {
                        Text("New Password")
                            .font(.subheadline)
                            .fontWeight(.medium)

                        SecureField("Enter new password", text: $newPassword)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }

                    // Confirm New Password
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Confirm New Password")
                            .font(.subheadline)
                            .fontWeight(.medium)

                        SecureField("Confirm new password", text: $confirmNewPassword)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .onChange(of: confirmNewPassword) { _, newValue in
                                passwordsMatch = newPassword == newValue
                            }

                        if !passwordsMatch && !confirmNewPassword.isEmpty {
                            Text("Passwords do not match")
                                .font(.caption)
                                .foregroundColor(.red)
                        }
                    }
                }

                // Error message
                if let errorMessage = authManager.errorMessage {
                    Text(errorMessage)
                        .font(.subheadline)
                        .foregroundColor(.red)
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.red.opacity(0.1))
                        .cornerRadius(8)
                }

                // Reset Password Button
                Button(action: {
                    Task {
                        await authManager.confirmResetPassword(
                            username: username,
                            newPassword: newPassword,
                            confirmationCode: confirmationCode
                        )

                        if authManager.errorMessage == nil {
                            dismiss()
                        }
                    }
                }) {
                    if authManager.isLoading {
                        ProgressView()
                            .tint(.white)
                    } else {
                        Text("Reset Password")
                            .fontWeight(.semibold)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(isFormValid ? Color.blue : Color.gray)
                .foregroundColor(.white)
                .cornerRadius(12)
                .disabled(!isFormValid || authManager.isLoading)
            }
            .padding(.horizontal, 24)
        }
        .navigationTitle("Reset Password")
        .navigationBarTitleDisplayMode(.inline)
    }

    var isFormValid: Bool {
        confirmationCode.count == 6 &&
        !newPassword.isEmpty &&
        !confirmNewPassword.isEmpty &&
        passwordsMatch &&
        newPassword.count >= 8
    }
}

