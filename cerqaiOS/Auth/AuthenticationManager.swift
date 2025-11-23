//
// AuthenticationManager.swift
// cerqaiOS
//
// Manages authentication state with Amplify
//

import Foundation
// TODO: Add Amplify via SPM
// import Amplify
import Combine

@MainActor
class AuthenticationManager: ObservableObject {
    @Published var isSignedIn = false
    @Published var currentUser: AuthUser?
    @Published var errorMessage: String?
    @Published var isLoading = false

    init() {
        checkAuthStatus()
    }

    func checkAuthStatus() {
        Task {
            do {
                let session = try await Amplify.Auth.fetchAuthSession()
                isSignedIn = session.isSignedIn

                if isSignedIn {
                    currentUser = try await Amplify.Auth.getCurrentUser()
                }
            } catch {
                print("Failed to fetch auth session: \(error)")
                isSignedIn = false
            }
        }
    }

    func signIn(username: String, password: String) async {
        isLoading = true
        errorMessage = nil

        do {
            let signInResult = try await Amplify.Auth.signIn(
                username: username,
                password: password
            )

            if signInResult.isSignedIn {
                isSignedIn = true
                currentUser = try await Amplify.Auth.getCurrentUser()
            }
        } catch let error as AuthError {
            errorMessage = error.errorDescription
        } catch {
            errorMessage = "An unexpected error occurred"
        }

        isLoading = false
    }

    func signUp(username: String, password: String, email: String, phoneNumber: String?) async {
        isLoading = true
        errorMessage = nil

        do {
            var userAttributes = [
                AuthUserAttribute(.email, value: email)
            ]

            if let phone = phoneNumber, !phone.isEmpty {
                userAttributes.append(AuthUserAttribute(.phoneNumber, value: phone))
            }

            let signUpResult = try await Amplify.Auth.signUp(
                username: username,
                password: password,
                options: AuthSignUpRequest.Options(userAttributes: userAttributes)
            )

            // Handle confirmation if needed
            switch signUpResult.nextStep {
            case .confirmUser:
                // Will navigate to confirmation screen
                break
            case .done:
                // Auto sign in after successful signup
                await signIn(username: username, password: password)
            }
        } catch let error as AuthError {
            errorMessage = error.errorDescription
        } catch {
            errorMessage = "An unexpected error occurred"
        }

        isLoading = false
    }

    func confirmSignUp(username: String, confirmationCode: String) async {
        isLoading = true
        errorMessage = nil

        do {
            let confirmResult = try await Amplify.Auth.confirmSignUp(
                for: username,
                confirmationCode: confirmationCode
            )

            if confirmResult.isSignUpComplete {
                // Sign up confirmed, user can now sign in
            }
        } catch let error as AuthError {
            errorMessage = error.errorDescription
        } catch {
            errorMessage = "An unexpected error occurred"
        }

        isLoading = false
    }

    func resetPassword(username: String) async {
        isLoading = true
        errorMessage = nil

        do {
            let resetResult = try await Amplify.Auth.resetPassword(for: username)

            switch resetResult.nextStep {
            case .confirmResetPasswordWithCode:
                // Will navigate to confirm reset screen
                break
            case .done:
                break
            }
        } catch let error as AuthError {
            errorMessage = error.errorDescription
        } catch {
            errorMessage = "An unexpected error occurred"
        }

        isLoading = false
    }

    func confirmResetPassword(username: String, newPassword: String, confirmationCode: String) async {
        isLoading = true
        errorMessage = nil

        do {
            try await Amplify.Auth.confirmResetPassword(
                for: username,
                with: newPassword,
                confirmationCode: confirmationCode
            )

            // Password reset successful, user can now sign in
        } catch let error as AuthError {
            errorMessage = error.errorDescription
        } catch {
            errorMessage = "An unexpected error occurred"
        }

        isLoading = false
    }

    func signOut() async {
        do {
            _ = try await Amplify.Auth.signOut()
            isSignedIn = false
            currentUser = nil
        } catch {
            print("Sign out failed: \(error)")
        }
    }
}
