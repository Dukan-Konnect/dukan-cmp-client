# Razorpay Integration Guide

## Configuration

### Setting up Razorpay Keys

1. **Get your API Keys:**
   - Login to [Razorpay Dashboard](https://dashboard.razorpay.com/)
   - Go to Settings > API Keys
   - Generate Test/Live keys

2. **Configure in the app:**
   - Open `composeApp/src/commonMain/kotlin/org/example/project/core/config/RazorpayConfig.kt`
   - Replace `KEY_ID` and `KEY_SECRET` with your actual keys:
   
   ```kotlin
   object RazorpayConfig {
       const val KEY_ID = "rzp_test_YOUR_KEY_ID"
       const val KEY_SECRET = "YOUR_SECRET_KEY"
   }
   ```

   ⚠️ **Security Note:** In production, never hardcode secret keys in the app. Move order creation to your backend server.

### Testing Payments

**Test Mode:**
- Use test keys (prefix: `rzp_test_`)
- Test cards: [Razorpay Test Cards](https://razorpay.com/docs/payments/payments/test-card-upi-details/)
- No real money is charged

**Test Card Numbers:**
- Success: `4111 1111 1111 1111`
- CVV: Any 3 digits
- Expiry: Any future date

### Payment Flow

1. User adds items to cart
2. Fills address and selects time slot
3. Clicks "Pay" button
4. App creates Razorpay order via API
5. PaymentActivity opens with Razorpay checkout
6. User completes payment
7. Payment success/failure is returned to the app

### API Endpoints

**Create Order:**
```bash
POST https://api.razorpay.com/v1/orders
Authorization: Basic <base64(key_id:key_secret)>
Content-Type: application/json

{
  "amount": 50000,  // in paise (₹500.00)
  "currency": "INR",
  "receipt": "rcpt_123456"
}
```

## Production Recommendations

1. **Backend Integration:**
   - Move order creation to your backend
   - Never expose `KEY_SECRET` in the client app
   - Verify payment signatures on the server

2. **Security:**
   - Use environment-specific configurations
   - Implement proper error handling
   - Add payment logging and monitoring

3. **User Experience:**
   - Handle payment failures gracefully
   - Show clear error messages
   - Provide retry options

## Troubleshooting

**Common Issues:**

1. **"Invalid Key" Error:**
   - Check if KEY_ID is correct
   - Ensure you're using test keys in test mode

2. **Order Creation Failed:**
   - Verify KEY_SECRET is correct
   - Check network connectivity
   - Review API response for error details

3. **Payment Window Not Opening:**
   - Ensure Razorpay SDK is properly initialized
   - Check if order_id is valid
   - Verify app permissions

## Resources

- [Razorpay Documentation](https://razorpay.com/docs/)
- [Android SDK Integration](https://razorpay.com/docs/payments/payment-gateway/android-integration/)
- [API Reference](https://razorpay.com/docs/api/)

