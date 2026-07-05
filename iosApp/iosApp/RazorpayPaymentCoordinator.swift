import Foundation
import UIKit
import ComposeApp

#if canImport(Razorpay)
import Razorpay
#endif

final class RazorpayPaymentCoordinator: NSObject {
    static let shared = RazorpayPaymentCoordinator()

    private override init() {
        super.init()
        paymentLog("init: registering payment request observer")
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handlePresentRazorpay(_:)),
            name: Notification.Name("DukaanKonnectPresentRazorpay"),
            object: nil
        )
    }

    @objc private func handlePresentRazorpay(_ notification: Notification) {
        paymentLog("handlePresentRazorpay: received notification userInfo=\(notification.userInfo ?? [:])")
        guard
            let userInfo = notification.userInfo,
            let requestId = userInfo["requestId"] as? String,
            let orderId = userInfo["orderId"] as? String,
            let amountValue = userInfo["amount"]
        else {
            paymentLog("handlePresentRazorpay: missing request/order/amount, posting failure")
            postResult(success: false, requestId: notification.userInfo?["requestId"] as? String)
            return
        }

        let amount = (amountValue as? NSNumber)?.int64Value ?? (amountValue as? Int64 ?? 0)
        guard amount > 0 else {
            paymentLog("handlePresentRazorpay: invalid amount=\(amount), posting failure requestId=\(requestId)")
            postResult(success: false, requestId: requestId)
            return
        }

        let phoneNumber = userInfo["phoneNumber"] as? String
        paymentLog("handlePresentRazorpay: opening checkout requestId=\(requestId) orderId=\(orderId) amount=\(amount) phone=\(phoneNumber ?? "")")
        presentCheckout(orderId: orderId, amount: amount, phoneNumber: phoneNumber, requestId: requestId)
    }

    private func presentCheckout(orderId: String, amount: Int64, phoneNumber: String?, requestId: String) {
        #if canImport(Razorpay)
        paymentLog("presentCheckout: creating RazorpayCheckout with key id length=\(RazorpayConfig.shared.KEY_ID.count)")
        let checkout = RazorpayCheckout.initWithKey(RazorpayConfig.shared.KEY_ID, andDelegateWithData: self)

        var options: [String: Any] = [
            "order_id": orderId,
            "amount": NSNumber(value: amount),
            "currency": "INR",
            "name": "DukaanKonnect",
            "description": "Service booking payment"
        ]

        if let phoneNumber, !phoneNumber.isEmpty {
            options["prefill"] = [
                "contact": phoneNumber
            ]
        }

        currentRequestId = requestId
        currentCheckout = checkout
        paymentLog("presentCheckout: calling open(options) requestId=\(requestId) optionsKeys=\(Array(options.keys))")
        DispatchQueue.main.async {
            paymentLog("presentCheckout: open(options) on main thread requestId=\(requestId)")
            checkout.open(options)
        }
        #else
        paymentLog("presentCheckout: Razorpay SDK not available in the iOS target")
        postResult(success: false, requestId: requestId)
        #endif
    }

    @discardableResult
    func handleIncomingURL(_ url: URL) -> Bool {
        #if canImport(Razorpay)
        paymentLog("handleIncomingURL: url=\(url.absoluteString)")
        guard let currentCheckout else {
            paymentLog("handleIncomingURL: no currentCheckout available")
            return false
        }
        let handled = currentCheckout.handleRedirection(url.absoluteString)
        paymentLog("handleIncomingURL: handleRedirection returned \(handled)")
        return handled
        #else
        paymentLog("handleIncomingURL: Razorpay SDK unavailable")
        return false
        #endif
    }

    private var currentRequestId: String?

    #if canImport(Razorpay)
    private var currentCheckout: RazorpayCheckout?
    #endif

    private func postResult(success: Bool, requestId: String?) {
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }

            paymentLog("postResult: success=\(success) requestId=\(requestId ?? "nil")")
            var userInfo: [String: Any] = [
                "success": success
            ]
            if let requestId {
                userInfo["requestId"] = requestId
            }

            paymentLog("postResult: posting notification userInfo=\(userInfo)")
            NotificationCenter.default.post(
                name: Notification.Name("DukaanKonnectRazorpayResult"),
                object: nil,
                userInfo: userInfo
            )

            self.currentRequestId = nil
            #if canImport(Razorpay)
            self.currentCheckout = nil
            #endif
            paymentLog("postResult: cleared currentRequestId/currentCheckout")
        }
    }
}

#if canImport(Razorpay)
extension RazorpayPaymentCoordinator: RazorpayPaymentCompletionProtocolWithData {
    func onPaymentError(_ code: Int32, description str: String, andData response: [AnyHashable : Any]?) {
        paymentLog("onPaymentError: code=\(code) description=\(str) response=\(response ?? [:]) requestId=\(currentRequestId ?? "nil")")
        postResult(success: false, requestId: currentRequestId)
    }

    func onPaymentSuccess(_ payment_id: String, andData response: [AnyHashable : Any]?) {
        paymentLog("onPaymentSuccess: paymentId=\(payment_id) response=\(response ?? [:]) requestId=\(currentRequestId ?? "nil")")
        postResult(success: true, requestId: currentRequestId)
    }
}
#endif

private func paymentLog(_ message: String) {
    print("[RazorpayBridge] \(message)")
}
