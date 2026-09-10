import SampleShared
import PhotosUI
import AVFoundation
import UIKit
import UniformTypeIdentifiers

final class SampleMediaPickerImpl: NSObject, SampleMediaPicker, PHPickerViewControllerDelegate {
    private enum Selection {
        case image(Int32, (GrayscaleImage) -> Void)
        case video(Bool, ([String]) -> Void)
    }

    private final class Request {
        let selection: Selection
        let onError: (String) -> Void

        init(selection: Selection, onError: @escaping (String) -> Void) {
            self.selection = selection
            self.onError = onError
        }
    }

    private enum Output {
        case image(Int, Int, [UInt8])
        case video([String])
    }

    private struct Failure: LocalizedError {
        let message: String
        var errorDescription: String? { message }
    }

    private var activeRequest: Request?
    private var presentedPicker: PHPickerViewController?

    func pickImage(
        maxSize: Int32,
        onSuccess: @escaping (GrayscaleImage) -> Void,
        onError: @escaping (String) -> Void
    ) {
        start(.image(maxSize, onSuccess), onError: onError)
    }

    func pickVideo(
        invert: Bool,
        onSuccess: @escaping ([String]) -> Void,
        onError: @escaping (String) -> Void
    ) {
        start(.video(invert, onSuccess), onError: onError)
    }

    private func start(_ selection: Selection, onError: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            guard self.activeRequest == nil else {
                onError("A media selection is already in progress")
                return
            }
            if case .image(let maxSize, _) = selection, maxSize <= 0 {
                onError("Image maximum size must be positive")
                return
            }
            let windows = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .filter { $0.activationState == .foregroundActive }
                .flatMap { $0.windows }
            guard var presenter = windows.first(where: { $0.isKeyWindow })?.rootViewController else {
                onError("No foreground window is available")
                return
            }
            while true {
                if let presented = presenter.presentedViewController {
                    presenter = presented
                } else if let navigation = presenter as? UINavigationController,
                          let visible = navigation.visibleViewController {
                    presenter = visible
                } else if let tabs = presenter as? UITabBarController,
                          let selected = tabs.selectedViewController {
                    presenter = selected
                } else {
                    break
                }
            }
            guard presenter.viewIfLoaded?.window != nil,
                  !presenter.isBeingDismissed, !presenter.isBeingPresented,
                  presenter.transitionCoordinator == nil else {
                onError("The foreground view is not ready to present a media picker")
                return
            }

            var configuration = PHPickerConfiguration()
            configuration.selectionLimit = 1
            configuration.preferredAssetRepresentationMode = .current
            switch selection {
            case .image: configuration.filter = .images
            case .video: configuration.filter = .videos
            }
            let request = Request(selection: selection, onError: onError)
            let picker = PHPickerViewController(configuration: configuration)
            picker.delegate = self
            // Full-screen presentation leaves cancellation to the picker's delegate.
            picker.modalPresentationStyle = .fullScreen
            self.activeRequest = request
            self.presentedPicker = picker
            presenter.present(picker, animated: true)
            if picker.presentingViewController == nil {
                self.finish(request, .failure(Failure(message: "Could not present the media picker")))
            }
        }
    }

    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        DispatchQueue.main.async {
            guard self.presentedPicker === picker, let request = self.activeRequest else { return }
            self.presentedPicker = nil
            picker.dismiss(animated: true) {
                guard let provider = results.first?.itemProvider else {
                    self.finish(request, .failure(Failure(message: "Media selection was cancelled")))
                    return
                }
                switch request.selection {
                case .image(let maxSize, _):
                    self.loadImage(provider, maxSize: maxSize, request: request)
                case .video(let invert, _):
                    self.loadVideo(provider, invert: invert, request: request)
                }
            }
        }
    }

    private func loadImage(_ provider: NSItemProvider, maxSize: Int32, request: Request) {
        guard provider.canLoadObject(ofClass: UIImage.self) else {
            finish(request, .failure(Failure(message: "The selected item is not a supported image")))
            return
        }
        provider.loadObject(ofClass: UIImage.self) { object, error in
            guard error == nil, let image = object as? UIImage else {
                self.finish(request, .failure(error ?? Failure(message: "Could not load the image")))
                return
            }
            DispatchQueue.global(qos: .userInitiated).async {
                let result: Result<Output, Error> = Result {
                    try autoreleasepool {
                        var size = image.size.applying(CGAffineTransform(scaleX: image.scale, y: image.scale))
                        if let source = image.cgImage {
                            switch image.imageOrientation {
                            case .left, .leftMirrored, .right, .rightMirrored:
                                size = CGSize(width: source.height, height: source.width)
                            default:
                                size = CGSize(width: source.width, height: source.height)
                            }
                        }
                        guard size.width.isFinite, size.height.isFinite,
                              size.width > 0, size.height > 0 else {
                            throw Failure(message: "The image has invalid dimensions")
                        }
                        let scale = min(1, CGFloat(maxSize) / max(size.width, size.height))
                        let width = max(1, Int((size.width * scale).rounded(.down)))
                        let height = max(1, Int((size.height * scale).rounded(.down)))
                        guard width <= Int(Int32.max) / height else {
                            throw Failure(message: "The image is too large")
                        }
                        return .image(width, height, try Self.grayscale(image, width: width, height: height))
                    }
                }
                self.finish(request, result)
            }
        }
    }

    private func loadVideo(_ provider: NSItemProvider, invert: Bool, request: Request) {
        let type = UTType.movie.identifier
        guard provider.hasItemConformingToTypeIdentifier(type) else {
            finish(request, .failure(Failure(message: "The selected item is not a supported video")))
            return
        }
        provider.loadFileRepresentation(forTypeIdentifier: type) { url, error in
            guard error == nil, let url = url else {
                self.finish(request, .failure(error ?? Failure(message: "Could not load the video")))
                return
            }
            let ownedURL = FileManager.default.temporaryDirectory
                .appendingPathComponent(UUID().uuidString)
                .appendingPathExtension(url.pathExtension.isEmpty ? "mov" : url.pathExtension)
            do {
                // The provider's URL expires as soon as this callback returns.
                try FileManager.default.copyItem(at: url, to: ownedURL)
            } catch {
                try? FileManager.default.removeItem(at: ownedURL)
                self.finish(request, .failure(error))
                return
            }
            Task.detached(priority: .userInitiated) {
                let asset = AVURLAsset(url: ownedURL)
                let generator = AVAssetImageGenerator(asset: asset)
                defer {
                    generator.cancelAllCGImageGeneration()
                    asset.cancelLoading()
                    try? FileManager.default.removeItem(at: ownedURL)
                }
                do {
                    let duration = try await asset.load(.duration)
                    guard duration.seconds.isFinite, duration.seconds > 0,
                          !(try await asset.loadTracks(withMediaType: .video)).isEmpty else {
                        throw Failure(message: "The video has no decodable duration or video track")
                    }
                    generator.appliesPreferredTrackTransform = true
                    generator.requestedTimeToleranceBefore = .zero
                    generator.requestedTimeToleranceAfter = .zero
                    let ramp = Array(" .:-=+*#%@")
                    var frames: [String] = []
                    for index in 0..<200 {
                        let time = CMTime(value: Int64(index), timescale: 10)
                        guard CMTimeCompare(time, duration) < 0 else { break }
                        let frame = try await generator.image(at: time)
                        let text = try autoreleasepool {
                            let pixels = try Self.grayscale(UIImage(cgImage: frame.image), width: 20, height: 8)
                            return (0..<8).map { row -> String in
                                var characters = (0..<20).map { column -> Character in
                                    let gray = Int(pixels[row * 20 + column])
                                    let value = invert ? 255 - gray : gray
                                    return ramp[value * (ramp.count - 1) / 255]
                                }
                                while characters.last == " " { characters.removeLast() }
                                return String(characters)
                            }.joined(separator: "\n")
                        }
                        frames.append(text)
                    }
                    guard !frames.isEmpty else {
                        throw Failure(message: "Could not decode any video frames")
                    }
                    await self.finish(request, .success(.video(frames)))
                } catch {
                    await self.finish(request, .failure(error))
                }
            }
        }
    }

    nonisolated private static func grayscale(_ image: UIImage, width: Int, height: Int) throws -> [UInt8] {
        let format = UIGraphicsImageRendererFormat()
        format.scale = 1
        format.opaque = true
        format.preferredRange = .standard
        let size = CGSize(width: width, height: height)
        let normalized = UIGraphicsImageRenderer(size: size, format: format).image { renderer in
            UIColor.black.setFill()
            renderer.fill(CGRect(origin: .zero, size: size))
            image.draw(in: CGRect(origin: .zero, size: size))
        }
        guard let source = normalized.cgImage else {
            throw Failure(message: "Could not render the image")
        }
        var rgba = [UInt8](repeating: 0, count: width * height * 4)
        try rgba.withUnsafeMutableBytes { bytes in
            guard let context = CGContext(
                data: bytes.baseAddress,
                width: width,
                height: height,
                bitsPerComponent: 8,
                bytesPerRow: width * 4,
                space: CGColorSpace(name: CGColorSpace.sRGB)!,
                bitmapInfo: CGBitmapInfo.byteOrder32Big.rawValue | CGImageAlphaInfo.premultipliedLast.rawValue
            ) else {
                throw Failure(message: "Could not allocate image pixels")
            }
            context.draw(source, in: CGRect(origin: .zero, size: size))
        }
        return stride(from: 0, to: rgba.count, by: 4).map { index -> UInt8 in
            let red = 0.299 * Double(rgba[index])
            let green = 0.587 * Double(rgba[index + 1])
            let blue = 0.114 * Double(rgba[index + 2])
            return UInt8((red + green + blue).rounded())
        }
    }

    private func finish(_ request: Request, _ result: Result<Output, Error>) {
        DispatchQueue.main.async {
            guard self.activeRequest === request else { return }
            self.activeRequest = nil
            self.presentedPicker = nil
            switch (request.selection, result) {
            case (_, .failure(let error)):
                request.onError(error.localizedDescription)
            case (.image(_, let onSuccess), .success(.image(let width, let height, let pixels))):
                let bytes = KotlinByteArray(size: Int32(pixels.count))
                for (index, pixel) in pixels.enumerated() {
                    bytes.set(index: Int32(index), value: Int8(bitPattern: pixel))
                }
                onSuccess(GrayscaleImage(width: Int32(width), height: Int32(height), pixels: bytes))
            case (.video(_, let onSuccess), .success(.video(let frames))):
                onSuccess(frames)
            default:
                request.onError("Unexpected media conversion result")
            }
        }
    }
}
