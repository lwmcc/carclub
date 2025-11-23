//
// SignUpView.swift
// cerqaiOS
//
// Sign up screen - mirrors Android Authenticator SignUp
//

import SwiftUI

struct SignUpView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    @Environment(\.dismiss) private var dismiss

    @State private var username = ""
    @State private var email = ""
    @State private var phoneNumber = ""
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var showConfirmation = false
    @State private var passwordsMatch = true

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Header
                VStack(spacing: 8) {
                    Text("Create Account")
                        .font(.largeTitle)
                        .fontWeight(.bold)

                    Text("Sign up to get started")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.top, 20)
                .padding(.bottom, 10)

                // Sign Up Form
                VStack(spacing: 16) {
                    // Username
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Username *")
                            .font(.subheadline)
                            .fontWeight(.medium)

                        TextField("Choose a username", text: $username)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .textInputAutocapitalization(.never)
                            .autocorrectionDisabled()
                    }

                    // Email
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Email *")
                            .font(.subheadline)
                            .fontWeight(.medium)

                        TextField("Enter your email", text: $email)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .textInputAutocapitalization(.never)
                            .autocorrectionDisabled()
                            .keyboardType(.emailAddress)
                    }

                    // Phone Number (Optional)
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Phone Number (Optional)")
                            .font(.subheadline)
                            .fontWeight(.medium)

                        TextField("+1234567890", text: $phoneNumber)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .keyboardType(.phonePad)
                    }

                    // Password
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Password *")
                            .font(.subheadline)
                            .fontWeight(.medium)

                        SecureField("Create a password", text: $password)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }

                    // Confirm Password
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Confirm Password *")
                            .font(.subheadline)
                            .fontWeight(.medium)

                        SecureField("Confirm your password", text: $confirmPassword)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .onChange(of: confirmPassword) { _, newValue in
                                passwordsMatch = password == newValue
                            }

                        if !passwordsMatch && !confirmPassword.isEmpty {
                            Text("Passwords do not match")
                                .font(.caption)
                                .foregroundColor(.red)
                        }
                    }

                    // Password requirements
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Password must contain:")
                            .font(.caption)
                            .foregroundColor(.secondary)

                        HStack(spacing: 4) {
                            Image(systemName: password.count >= 8 ? "checkmark.circle.fill" : "circle")
                                .foregroundColor(password.count >= 8 ? .green : .secondary)
                                .font(.caption)
                            Text("At least 8 characters")
                                .font(.caption)
                                .foregroundColor(.secondary)
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

                // Sign Up Button
                Button(action: {
                    Task {
                        await authManager.signUp(
                            username: username,
                            password: password,
                            email: email,
                            phoneNumber: phoneNumber.isEmpty ? nil : phoneNumber
                        )

                        if authManager.errorMessage == nil {
                            showConfirmation = true
                        }
                    }
                }) {
                    if authManager.isLoading {
                        ProgressView()
                            .tint(.white)
                    } else {
                        Text("Create Account")
                            .fontWeight(.semibold)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(isFormValid ? Color.blue : Color.gray)
                .foregroundColor(.white)
                .cornerRadius(12)
                .disabled(!isFormValid || authManager.isLoading)

                // Sign In Link
                HStack {
                    Text("Already have an account?")
                        .foregroundColor(.secondary)

                    Button(action: {
                        dismiss()
                    }) {
                        Text("Sign In")
                            .fontWeight(.semibold)
                            .foregroundColor(.blue)
                    }
                }
                .padding(.top, 8)
            }
            .padding(.horizontal, 24)
        }
        .navigationTitle("Sign Up")
        .navigationBarTitleDisplayMode(.inline)
        .navigationDestination(isPresented: $showConfirmation) {
            ConfirmSignUpView(username: username)
        }
    }

    var isFormValid: Bool {
        !username.isEmpty &&
        !email.isEmpty &&
        !password.isEmpty &&
        !confirmPassword.isEmpty &&
        passwordsMatch &&
        password.count >= 8
    }
}

