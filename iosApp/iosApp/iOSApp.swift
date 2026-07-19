import SwiftUI
#if canImport(FirebaseCore)
import FirebaseCore
#endif

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(NotificationAppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
