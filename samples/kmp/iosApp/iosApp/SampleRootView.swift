import SampleShared
import SwiftUI

struct SampleRootView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        SampleAppViewControllerKt.SampleAppViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
