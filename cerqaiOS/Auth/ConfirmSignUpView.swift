//
// ConfirmSignUpView.swift
// cerqaiOS
//
// Confirmation code screen for sign up
//

import SwiftUI

struct ConfirmSignUpView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    @Environment(\.dismiss) private var dismiss

    let username: String
    @State private var confirmationCode = ""

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Header
                VStack(spacing: 8) {
                    Image(systemName: "envelope.badge.fill")
                        .font(.system(size: 60))
                        .foregroundColor(.blue)

                    Text("Check Your Email")
                        .font(.title)
                        .fontWeight(.bold)

                    Text("We sent a confirmation code to your email")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
                .padding(.top, 40)
                .padding(.bottom, 20)

                // Confirmation Code Field
                VStack(alignment: .leading, spacing: 8) {
                    Text("Confirmation Code")
                        .font(.subheadline)
                        .fontWeight(.medium)

                    TextField("Enter 6-digit code", text: $confirmationCode)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .keyboardType(.numberPad)
                        .multilineTextAlignment(.center)
                        .font(.title2)
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

                // Confirm Button
                Button(action: {
                    Task {
                        await authManager.confirmSignUp(
                            username: username,
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
                        Text("Confirm")
                            .fontWeight(.semibold)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(confirmationCode.count == 6 ? Color.blue : Color.gray)
                .foregroundColor(.white)
                .cornerRadius(12)
                .disabled(confirmationCode.count != 6 || authManager.isLoading)

                // Resend Code
                Button(action: {
                    // TODO: Implement resend code
                }) {
                    Text("Resend Code")
                        .font(.subheadline)
                        .foregroundColor(.blue)
                }
                .padding(.top, 8)
            }
            .padding(.horizontal, 24)
        }
        .navigationTitle("Confirm Sign Up")
        .navigationBarTitleDisplayMode(.inline)
    }
}

