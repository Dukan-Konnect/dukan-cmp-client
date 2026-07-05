import UIKit
import UserNotifications
import ComposeApp

#if canImport(FirebaseCore)
import FirebaseCore
#endif

#if canImport(FirebaseMessaging)
import FirebaseMessaging
#endif

final class NotificationAppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    private let notificationBridge = IosFirebaseNotificationBridge()
    private let razorpayCoordinator = RazorpayPaymentCoordinator.shared

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        print("[AppDelegate] didFinishLaunchingWithOptions")
        KoinInitializerKt.initializeKoin(additionalModules: [PlatformModuleKt.platformModule])
        UNUserNotificationCenter.current().delegate = self

        #if canImport(FirebaseCore)
        if FirebaseApp.app() == nil {
            print("[AppDelegate] configuring Firebase")
            FirebaseApp.configure()
        }
        #endif

        #if canImport(FirebaseMessaging)
        print("[AppDelegate] setting Firebase Messaging delegate")
        Messaging.messaging().delegate = self
        #endif

        return true
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        print("[AppDelegate] didBecomeActive")
        refreshRemoteNotificationRegistration(application: application)
    }

    func application(
        _ application: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        print("[AppDelegate] openURL called url=\(url.absoluteString) options=\(options)")
        let handled = razorpayCoordinator.handleIncomingURL(url)
        print("[AppDelegate] openURL handled=\(handled)")
        return handled
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        print("[AppDelegate] didRegisterForRemoteNotificationsWithDeviceToken")
        #if canImport(FirebaseMessaging)
        Messaging.messaging().apnsToken = deviceToken
        #endif
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        print("[AppDelegate] Failed to register for remote notifications: \(error.localizedDescription)")
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        print("[AppDelegate] willPresent notification=\(notification.request.content.userInfo)")
        completionHandler([.banner, .list, .sound])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        print("[AppDelegate] didReceive notification response userInfo=\(response.notification.request.content.userInfo)")
        completionHandler()
    }
}

private extension NotificationAppDelegate {
    func refreshRemoteNotificationRegistration(application: UIApplication) {
        print("[AppDelegate] refreshing remote notification registration")
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            let status = settings.authorizationStatus
            let isAuthorized = status == .authorized || status == .provisional || status == .ephemeral
            print("[AppDelegate] notification authorizationStatus=\(status.rawValue) isAuthorized=\(isAuthorized)")
            if isAuthorized {
                print("[AppDelegate] calling registerForRemoteNotifications")
                application.registerForRemoteNotifications()
            }
        }
    }
}

#if canImport(FirebaseMessaging)
extension NotificationAppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("[AppDelegate] didReceiveRegistrationToken token=\(fcmToken ?? "nil")")
        guard let fcmToken, !fcmToken.isEmpty else { return }
        notificationBridge.syncRegistrationToken(token: fcmToken)
    }
}
#endif
