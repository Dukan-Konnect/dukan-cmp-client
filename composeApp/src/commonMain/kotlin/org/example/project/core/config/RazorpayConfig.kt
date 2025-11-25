package org.example.project.core.config

/**
 * Configuration for Razorpay payment gateway.
 *
 * For security, replace these with your actual keys:
 * - Key ID: Get from Razorpay Dashboard > Settings > API Keys
 * - Key Secret: Keep this secure, never commit to version control
 *
 * For production, consider using:
 * - Environment variables
 * - Secure key storage (Android Keystore, iOS Keychain)
 * - Backend API to generate orders (recommended)
 */
object RazorpayConfig {
    // TODO: Replace with your Razorpay Test/Live keys


    // For production: Move order creation to backend
    // This exposes the secret key on client side which is not recommended
}

