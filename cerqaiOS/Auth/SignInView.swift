//
// SignInView.swift
// cerqaiOS
//
// Sign in screen - mirrors Android Authenticator SignIn
//

import SwiftUI

struct SignInView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    @State private var username = ""
    @State private var password = ""
    @State private var showSignUp = false
    @State private var showPasswordReset = false

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 24) {
                    // Logo/Header
                    VStack(spacing: 8) {
                        Image(systemName: "car.fill")
                            .font(.system(size: 60))
                            .foregroundColor(.blue)

                        Text("Car Club")
                            .font(.largeTitle)
                            .fontWeight(.bold)

                        Text("Sign in to your account")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .padding(.top, 40)
                    .padding(.bottom, 20)

                    // Sign In Form
                    VStack(spacing: 16) {
                        // Username field
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Username")
                                .font(.subheadline)
                                .fontWeight(.medium)

                            TextField("Enter your username", text: $username)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                                .textInputAutocapitalization(.never)
                                .autocorrectionDisabled()
                        }

                        // Password field
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Password")
                                .font(.subheadline)
                                .fontWeight(.medium)

                            SecureField("Enter your password", text: $password)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                        }

                        // Forgot password
                        HStack {
                            Spacer()
                            Button(action: {
                                showPasswordReset = true
                            }) {
                                Text("Forgot Password?")
                                    .font(.subheadline)
                                    .foregroundColor(.blue)
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

                    // Sign In Button
                    Button(action: {
                        Task {
                            await authManager.signIn(username: username, password: password)
                        }
                    }) {
                        if authManager.isLoading {
                            ProgressView()
                                .tint(.white)
                        } else {
                            Text("Sign In")
                                .fontWeight(.semibold)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(username.isEmpty || password.isEmpty ? Color.gray : Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(12)
                    .disabled(username.isEmpty || password.isEmpty || authManager.isLoading)

                    // Sign Up Link
                    HStack {
                        Text("Don't have an account?")
                            .foregroundColor(.secondary)

                        Button(action: {
                            showSignUp = true
                        }) {
                            Text("Sign Up")
                                .fontWeight(.semibold)
                                .foregroundColor(.blue)
                        }
                    }
                    .padding(.top, 8)
                }
                .padding(.horizontal, 24)
            }
            .navigationDestination(isPresented: $showSignUp) {
                SignUpView()
            }
            .navigationDestination(isPresented: $showPasswordReset) {
                PasswordResetView()
            }
        }
    }
}

