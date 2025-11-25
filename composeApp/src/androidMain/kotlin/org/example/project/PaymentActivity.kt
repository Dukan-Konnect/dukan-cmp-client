package org.example.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.example.project.core.config.RazorpayConfig
import org.json.JSONObject

class PaymentActivity: Activity(), PaymentResultWithDataListener, ExternalWalletListener {

    companion object {
        const val EXTRA_ORDER_ID = "order_id"
        const val EXTRA_AMOUNT = "amount"
        const val EXTRA_PHONE_NUMBER = "phone_number"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        * To ensure faster loading of the Checkout form,
        * call this method as early as possible in your checkout flow
        * */
        Checkout.preload(applicationContext)
        val co = Checkout()
        // Set Razorpay Key ID
        co.setKeyID(RazorpayConfig.KEY_ID)
        startPayment(co)

    }
    override fun onPaymentSuccess(razorpayPaymentId: String?, data: PaymentData?) {
        try {
            // Return payment ID to calling activity
            val resultIntent = Intent().apply {
                putExtra("payment_id", razorpayPaymentId)
                putExtra("signature", data?.signature)
            }
            setResult(RESULT_OK, resultIntent)

            Toast.makeText(
                this,
                "Payment successful: $razorpayPaymentId",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
        finish()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            Toast.makeText(
                this,
                "Payment failed: $p1",
                Toast.LENGTH_LONG
            ).show()
            setResult(RESULT_CANCELED)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
        finish()
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        try {
            Toast.makeText(
                this,
                "Wallet selected: $p0",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun startPayment(co : Checkout){
        val activity = this
        try {
            // Get order details from intent
            val orderId = intent.getStringExtra(EXTRA_ORDER_ID) ?: run {
                Toast.makeText(this, "Order ID is missing", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            val amount = intent.getLongExtra(EXTRA_AMOUNT, 0)
            val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER) ?: ""

            val options = JSONObject()
            options.put("name","Dukaan Konnect")
            options.put("description","Service Booking Payment")
            //You can omit the image option to fetch the image from the Dashboard
            options.put("image","http://example.com/image/rzp.jpg")
            options.put("theme.color", "#6C4DFF")
            options.put("currency","INR")
            options.put("order_id", orderId)
            options.put("amount", amount.toString()) //pass amount in currency subunits (paise)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            val prefill = JSONObject()
            if (phoneNumber.isNotEmpty()) {
                prefill.put("contact","+91$phoneNumber")
            }

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }
    //......
}