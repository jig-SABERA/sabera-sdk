package jp.jig.sabera.app.docs.snippets

import app.jigglass.glass.CommandManager

/** ページごとの使い方のコード例。 */
internal object PageSnippets {

    fun teleprompter(commandManager: CommandManager) {
        // #snippet pages.teleprompter
        commandManager.enterTeleprompterPage()
        commandManager.sendTeleprompterStatus(
            status = CommandManager.TeleprompterStatus.READY,
            mode = CommandManager.TeleprompterMode.TELEPROMPT,
        )
        commandManager.sendTeleprompterContent("読み上げる原稿", percent = 0)
        // #endsnippet
    }

    fun teleprompterPlay(commandManager: CommandManager, lines: List<String>) {
        // #snippet pages.teleprompter-play
        commandManager.sendTeleprompterStatus(
            status = CommandManager.TeleprompterStatus.STARTED,
            mode = CommandManager.TeleprompterMode.TELEPROMPT,
        )
        lines.forEachIndexed { index, line ->
            // 全文を送り直さず、読み上げた分だけ流す
            commandManager.sendTeleprompterLine(
                text = line,
                percent = (index + 1) * 100 / lines.size,
            )
            commandManager.sendTeleprompterTime("00:%02d".format(index))
        }
        commandManager.clearInscriptionText()
        // #endsnippet
    }

    fun translate(commandManager: CommandManager, sentences: List<String>) {
        // #snippet pages.translate
        commandManager.enterTranslatePage()
        commandManager.sendTranslateLanguage(source = "ENG", target = "JPN")
        sentences.forEach { sentence ->
            // 送るたび全文が置き換わる
            commandManager.sendTranslateContent(sentence)
        }
        commandManager.clearInscriptionText()
        // #endsnippet
    }

    fun aiChat(commandManager: CommandManager, question: String, answer: String) {
        // #snippet pages.ai-chat
        // フォントを先に確定させるため、開く前に言語を送る
        commandManager.sendAiChatLanguage("JPN")
        commandManager.enterAiChatPage()

        commandManager.sendAiChatSenderText(sender = CommandManager.AiChatSender.USER, text = question)
        commandManager.sendAiChatSenderStatus(
            sender = CommandManager.AiChatSender.AI,
            status = CommandManager.AiChatStatus.GENERATING,
        )
        commandManager.sendAiChatSenderText(sender = CommandManager.AiChatSender.AI, text = answer)
        commandManager.sendAiChatStatus(CommandManager.AiChatStatus.COMPLETE)
        // #endsnippet
    }

    fun aiChatClear(commandManager: CommandManager) {
        // #snippet pages.ai-chat-clear
        commandManager.clearAiChat()
        // #endsnippet
    }

    fun emptyScreen(commandManager: CommandManager) {
        // #snippet pages.empty-screen
        commandManager.enterEmptyScreenPage()
        commandManager.sendEmptyScreenContent("好きな文字列をそのまま出せる")
        // #endsnippet
    }

    fun imageDisplay(commandManager: CommandManager, grayscale: ByteArray) {
        // #snippet pages.image-display
        commandManager.enterImageDisplayPage()
        // 196x196 を超えるとファームが弾いて何も出ない
        commandManager.sendImage(width = 196, height = 196, grayscale = grayscale)
        // #endsnippet
    }

    fun layout(commandManager: CommandManager) {
        // #snippet pages.layout
        // 開く操作は要らない。モードを送るとその場で切り替わる
        commandManager.sendLayout(
            mode = CommandManager.LayoutMode.QUAD,
            texts = mapOf(0 to "左上", 1 to "右上", 2 to "左下", 3 to "右下"),
        )
        // 分割はそのまま、右下だけ書き換える
        commandManager.sendLayoutTexts(mapOf(3 to "書き換え"))
        // 空文字でその領域を消す
        commandManager.sendLayoutTexts(mapOf(3 to ""))
        commandManager.closeLayout()
        // #endsnippet
    }

    fun canvas(commandManager: CommandManager, photo: ByteArray) {
        // #snippet pages.canvas
        commandManager.sendCanvas(
            listOf(
                CommandManager.CanvasElement(id = 0, x = 16, y = 8, width = 240, height = 40, text = "見出し"),
            ),
        )
        // 画像はテキストの背面に置かれる
        commandManager.sendCanvasImage(id = 0, x = 16, y = 84, width = 192, height = 192, grayscale = photo)
        // 他の要素を残して id 1 だけ足す
        commandManager.sendCanvasElements(
            listOf(
                CommandManager.CanvasElement(id = 1, x = 16, y = 300, width = 240, height = 40, text = "キャプション"),
            ),
        )
        commandManager.closeCanvas()
        // #endsnippet
    }

    fun navigation(commandManager: CommandManager, map: ByteArray, courseDegrees: Double) {
        // #snippet pages.navigation
        commandManager.enterNavigationPage()
        commandManager.sendNaviLanguage("JPN")
        commandManager.sendNaviStatus(CommandManager.NaviStatus.START)
        commandManager.sendNavi(
            maneuverIcon = CommandManager.ManeuverIcon.TURN_LEFT,
            instructionText = "交差点を左折",
            distanceText = "300m",
            estimatedArrivalText = "12:34",
            timeAndDistanceText = "10分 / 1.2km",
            bitmapWidth = 128,
            bitmapHeight = 128,
            grayscale = map,
        )
        // 方位のドリフト補正。案内中は数秒おきに送る
        commandManager.sendNaviCourse(courseDegrees)
        // 着いたら到着画面に切り替える
        commandManager.sendNaviStatus(CommandManager.NaviStatus.ARRIVED)
        // #endsnippet
    }

    fun angleAdjustment(commandManager: CommandManager) {
        // #snippet pages.angle-adjustment
        commandManager.enterGlassAngleAdjustmentPage()
        commandManager.sendWakeupTiltThreshold(degrees = 20)
        // #endsnippet
    }

    fun adjust(commandManager: CommandManager) {
        // #snippet pages.adjust
        commandManager.sendAdjust(
            status = CommandManager.AdjustStatus.SHOW,
            imageType = CommandManager.AdjustImageType.HOME,
        )
        commandManager.sendAdjust(
            status = CommandManager.AdjustStatus.CLOSE,
            imageType = CommandManager.AdjustImageType.HOME,
        )
        // #endsnippet
    }

    fun home(commandManager: CommandManager) {
        // #snippet pages.home
        // 開いていたページを閉じて表示内容を捨てる
        commandManager.enterHomePage()
        // #endsnippet
    }
}
