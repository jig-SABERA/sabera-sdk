#!/usr/bin/env python3
"""docs/api 配下の API リファレンス雛形を生成する。

シグネチャは SDK 0.4.0 の公開 API に合わせたもの。
SDK のバージョンを上げてメソッドが増減したら SPEC を更新して再実行する。

既存ファイルは上書きしない（人間が書いた本文を守るため）。--force で上書き。
"""

import argparse
import re
from pathlib import Path

DOCS = Path(__file__).resolve().parent.parent / "docs"
API_TITLE = "API リファレンス"

def m(name, sig, params=(), returns="Unit", related=(), note=None, summary=None):
    """1メソッド分のページ定義。

    params は (名前, 型) か (名前, 型, 説明) のタプル。
    summary を書いたページは執筆中の警告を出さない。
    """
    return {
        "name": name,
        "sig": sig,
        "params": [(p + ("",))[:3] if len(p) == 2 else p for p in params],
        "returns": returns,
        "related": list(related),
        "note": note,
        "summary": summary,
    }


SPEC = [
    {
        "dir": "glasses-sdk",
        "title": "GlassesSDK",
        "nav_order": 1,
        "summary": "SDK 全体の初期設定を行うシングルトン。`Application.onCreate()` で呼ぶ。",
        "methods": [
            m(
                "setLogger",
                "fun setLogger(sink: (tag: String, message: String) -> Unit)",
                [("sink", "(String, String) -> Unit")],
            ),
            m("setProd", "fun setProd(isProd: Boolean)", [("isProd", "Boolean")]),
            m(
                "setDevicePersistence",
                "fun setDevicePersistence(persistence: SdkDevicePersistence)",
                [("persistence", "SdkDevicePersistence")],
            ),
        ],
    },
    {
        "dir": "glass-manager",
        "title": "GlassManager",
        "nav_order": 2,
        "summary": "デバイスの探索・接続・切断を担う。`getGlassManager(context)` で取得する。",
        "methods": [
            m(
                "getGlassManager",
                "fun getGlassManager(context: Context): GlassManager",
                [("context", "Context")],
                "GlassManager",
            ),
            m("connectedDevice", "val connectedDevice: StateFlow<GlassClient?>", returns="StateFlow<GlassClient?>"),
            m("lastConnectedDevice", "val lastConnectedDevice: GlassClient?", returns="GlassClient?"),
            m("hasLastConnectedDevice", "val hasLastConnectedDevice: Boolean", returns="Boolean"),
            m("selectionDialogPresented", "val selectionDialogPresented: SharedFlow<Unit>", returns="SharedFlow<Unit>"),
            m(
                "externalDisplayNameChanged",
                "val externalDisplayNameChanged: SharedFlow<String?>",
                returns="SharedFlow<String?>",
            ),
            m(
                "showAutomaticSelectionDialog",
                "suspend fun showAutomaticSelectionDialog(context: Context): GlassClient?",
                [("context", "Context")],
                "GlassClient?",
                ["connect", "createClientFromDeviceID"],
            ),
            m("connect", "suspend fun connect(glassClient: GlassClient)", [("glassClient", "GlassClient")],
              related=["disconnect", "showAutomaticSelectionDialog"]),
            m("disconnect", "suspend fun disconnect(glassClient: GlassClient)", [("glassClient", "GlassClient")],
              related=["disconnectAndClearBond"]),
            m("disconnectAndClearBond", "suspend fun disconnectAndClearBond(glassClient: GlassClient)",
              [("glassClient", "GlassClient")], related=["disconnect"]),
            m(
                "createClientFromDeviceID",
                "fun createClientFromDeviceID(deviceId: String): GlassClient?",
                [("deviceId", "String")],
                "GlassClient?",
                ["connect"],
            ),
            m("onDeviceDisappear", "fun onDeviceDisappear(address: String)", [("address", "String")]),
            m("registerDeviceFromIntent", "fun registerDeviceFromIntent(data: Intent)", [("data", "Intent")]),
            m("showDisconnectSheetIOS", "suspend fun showDisconnectSheetIOS(): Boolean", returns="Boolean"),
            m("showRenameAccessorySheetIOS", "suspend fun showRenameAccessorySheetIOS(): Boolean", returns="Boolean"),
        ],
    },
    {
        "dir": "glass-client",
        "title": "GlassClient",
        "nav_order": 3,
        "summary": "接続済みの1台を表す。`GlassManager.connectedDevice` から得る。",
        "methods": [
            m("connected", "val connected: StateFlow<Boolean>", returns="StateFlow<Boolean>",
              summary="グラスとつながっている間 true。切断で false に戻る。"
                      "送信メソッドは未接続だと黙って捨てられるので、通知の転送のように"
                      "取りこぼしたくないものはこれを見てから送る。"),
            m("deviceName", "val deviceName: String?", returns="String?"),
            m("deviceIdentifier", "val deviceIdentifier: String?", returns="String?"),
            m("isConnectionValid", "fun isConnectionValid(): Boolean", returns="Boolean"),
            m("createCommandManager", "fun createCommandManager(): CommandManager", returns="CommandManager"),
            m("micChannel", "val micChannel: StateFlow<Int?>\nfun setMicChannel(channel: Int?)",
              [("channel", "Int?")], returns="StateFlow<Int?>",
              note="getter と setter で型が揃っていないため、Kotlin からは読みが `val`、"
                   "書きが `setMicChannel()` に見える。"),
            m("sendCommand", "suspend fun sendCommand(command: ByteArray)", [("command", "ByteArray")],
              related=["sendCommandList"]),
            m("sendCommandList", "suspend fun sendCommandList(command: List<ByteArray>)",
              [("command", "List<ByteArray>")], related=["sendCommand"]),
            m("sendText", "suspend fun sendText(page: Byte, content: String)",
              [("page", "Byte"), ("content", "String")]),
            m("cancelPendingPackets", "suspend fun cancelPendingPackets()"),
            m("reboot", "suspend fun reboot()"),
        ],
    },
    {
        "dir": "command-manager",
        "title": "CommandManager",
        "nav_order": 4,
        "summary": "グラス側の画面遷移・コンテンツ送信・イベント購読。`GlassClient.createCommandManager()` で作る。",
        "methods": [
            m("connected", "val connected: StateFlow<Boolean>", returns="StateFlow<Boolean>",
              summary="グラスとつながっている間 true。GlassClient.connected と同じ状態を返す。"
                      "送信メソッドは未接続だと黙って捨てられるので、通知の転送のように"
                      "取りこぼしたくないものはこれを見てから送る。"),
            m("gestureEvents", "val gestureEvents: SharedFlow<GestureType>", returns="SharedFlow<GestureType>",
              summary="グラスのタッチ操作が流れる。シングルタップ・ダブルタップ・長押しの3種。"
                      "どのページを開いていても届くので、アプリが背面にいる間の操作も拾える。"),
            m("imuData", "val imuData: SharedFlow<CommandManager.ImuData>",
              returns="SharedFlow<CommandManager.ImuData>",
              summary="6DoF のセンサー値。startImuData を呼ぶまで何も流れない。"
                      "1サンプルは加速度[mg]・角速度[dps]・ピッチとヨー[度]と、"
                      "AR起動からの経過時間[ms]を持つ。並べ替えや間隔の計算は受信時刻ではなく"
                      "この経過時間を使う。送信キューが詰まるとグラス側がサンプルを捨てるため、"
                      "指定した周期どおりには届かない。",
              related=["startImuData", "stopImuData"]),
            m("imuDataStarted", "val imuDataStarted: StateFlow<Boolean>",
              returns="StateFlow<Boolean>",
              summary="6DoF が流れている間 true。開始・停止の応答で切り替わる。",
              related=["startImuData"]),
            m("micAudio", "val micAudio: SharedFlow<ByteArray>",
              returns="SharedFlow<ByteArray>",
              summary="グラスのマイク音声。PCM16 のリトルエンディアン、16kHz モノラル。"
                      "startMicStreaming を呼ぶまで何も流れない。デバイスの世代で音声の形式が"
                      "変わる（Ogg Opus か record stream）が、判別とデコードは SDK 側で行うため"
                      "利用側は PCM だけ受け取ればよい。グラスの録音は小さいため、SDK が3倍に"
                      "持ち上げてから流す。購読が遅れると古いデータから捨てるので、"
                      "録音として貯めるなら受け取り側でバッファする。",
              related=["startMicStreaming", "stopMicStreaming", "micStreaming"]),
            m("micStreaming", "val micStreaming: StateFlow<Boolean>",
              returns="StateFlow<Boolean>",
              summary="マイクが流れている間 true。",
              related=["startMicStreaming"]),
            m("enterHomePage", "fun enterHomePage()",
              summary="グラスをホーム画面に戻す。開いていたページは閉じ、表示していた内容は破棄される。"
                      "機能を止めるときの後片付けに使う。"),
            m("enterTeleprompterPage", "fun enterTeleprompterPage()",
              summary="テレプロンプトページを開く。原稿は sendTeleprompterContent で送る。"
                      "開く前に送った原稿は表示されない。",
              related=["sendTeleprompterContent"]),
            m("sendTeleprompterContent",
              "fun sendTeleprompterContent(content: String)\n"
              "fun sendTeleprompterContent(content: String, percent: Int)",
              [("content", "String", "表示する原稿"),
               ("percent", "Int", "スクロールバーの位置（0..100）")],
              summary="テレプロンプトに原稿を送る。200バイトを超える分は分割して送られる。"
                      "`percent` つきの overload はスクロールバーの位置も一緒に送る。",
              related=["enterTeleprompterPage", "sendTeleprompterLine"]),

            m("sendAIContent", "fun sendAIContent(content: String)", [("content", "String")]),
            m("enterTranslatePage", "fun enterTranslatePage()",
              summary="翻訳ページを開く。開いたあとに sendTranslateLanguage で言語ペアを送り、"
                      "sendTranslateContent で本文を送る、の順で使う。",
              related=["sendTranslateContent", "sendTranslateLanguage"]),
            m("sendTranslateContent", "fun sendTranslateContent(content: String)",
              [("content", "String", "表示する訳文。長い分は分割して送られる")],
              summary="翻訳ページに訳文を送る。enterTranslatePage で開いてから呼ぶ。"
                      "送るたび表示は置き換わる。消すときは clearInscriptionText を使う。",
              related=["enterTranslatePage", "sendTranslateLanguage"]),
            m("sendTranslateLanguage", "fun sendTranslateLanguage(source: String, target: String)",
              [("source", "String", "翻訳元の言語コード。\"en\" など"),
               ("target", "String", "翻訳先の言語コード。\"ja\" など")],
              summary="翻訳ページに出す言語ラベルを切り替える。本文を送る前に呼ぶ。"
                      "画面遷移は起こらない。",
              related=["sendTranslateContent"]),
            m("sendMeeting", "fun sendMeeting(meetingType: Byte, text: String, percent: Int)",
              [("meetingType", "Byte"), ("text", "String"), ("percent", "Int")]),
            m("enterAiChatPage", "fun enterAiChatPage()",
              summary="AI アシスタントページを開く。吹き出しは sendAiChatSenderText で送る。"
                      "本文のフォントが言語で変わるため、開く前に sendAiChatLanguage を送っておく。",
              related=["sendAiChatSenderText", "sendAiChatLanguage"]),
            m("sendAiChatSender", "fun sendAiChatSender(sender: CommandManager.AiChatSender)",
              [("sender", "CommandManager.AiChatSender", "吹き出しの主体。`USER` か `AI`")],
              summary="次に送る本文の吹き出しをどちら側にするかを切り替える。"
                      "本文と一度に送る sendAiChatSenderText の方が確実。",
              related=["sendAiChatSenderText"]),
            m("sendAiChatText", "fun sendAiChatText(text: String)",
              [("text", "String", "表示する本文")],
              summary="AI アシスタントページに本文だけを送る。"
                      "どちらの吹き出しに出すかも指定できる sendAiChatSenderText の方が確実。",
              related=["enterAiChatPage", "sendAiChatSenderText"]),
            m("sendAiChatStatus", "fun sendAiChatStatus(status: CommandManager.AiChatStatus)",
              [("status", "CommandManager.AiChatStatus", "`GENERATING` か `COMPLETE`")],
              summary="AI 応答の生成状態を送る。`COMPLETE` を送るまでグラスは生成中の表示を続ける。",
              related=["sendAiChatSenderStatus"]),
            m("sendAiChatSenderText",
              "fun sendAiChatSenderText(\n"
              "    sender: CommandManager.AiChatSender,\n"
              "    text: String,\n"
              "    model: CommandManager.AiChatModel? = null,\n"
              ")",
              [("sender", "CommandManager.AiChatSender", "吹き出しの主体"),
               ("text", "String", "表示する本文"),
               ("model", "CommandManager.AiChatModel?", "`AI` のときに表示する生成モデル")],
              summary="吹き出しの主体と本文をまとめて送る。質問は `USER`、回答は `AI` で送る。"
                      "1パケットに収まらない長文は sendAiChatText で続きを流す。",
              related=["sendAiChatText"]),
            m("sendAiChatSenderStatus",
              "fun sendAiChatSenderStatus(\n"
              "    sender: CommandManager.AiChatSender,\n"
              "    status: CommandManager.AiChatStatus,\n"
              "    model: CommandManager.AiChatModel? = null,\n"
              ")",
              [("sender", "CommandManager.AiChatSender", "吹き出しの主体"),
               ("status", "CommandManager.AiChatStatus", "生成中か完了か"),
               ("model", "CommandManager.AiChatModel?", "`AI` のときに表示する生成モデル")],
              summary="吹き出しの主体と生成状態をまとめて送る。"
                      "回答を流し始める前に `AI` と `GENERATING` を送る。",
              related=["sendAiChatText"]),
            m("openGlassMic", "fun openGlassMic()",
              summary="グラスのマイクを開く。開いている間、音声は "
                      "`GlassClient.addAudioDataEventListener` に届く。"
                      "マイクのチャンネルは接続中のデバイスに合わせて SDK 側で決まる。",
              related=["closeGlassMic"]),
            m("closeGlassMic", "fun closeGlassMic()",
              summary="グラスのマイクを閉じる。openGlassMic と対で呼ぶ。"
                      "閉じ忘れるとグラスは録音を続けるので、画面を離れるときに必ず呼ぶ。",
              related=["openGlassMic"]),
            m("startMicStreaming", "fun startMicStreaming()",
              summary="マイクを開いて、届いた音声をデコードしながら micAudio に流す。"
                      "すでに流れているときは開き直す。生の Opus を自分で扱いたい場合は"
                      "openGlassMic と GlassClient.addAudioDataEventListener を使う。",
              related=["micAudio", "stopMicStreaming"]),
            m("stopMicStreaming", "fun stopMicStreaming()",
              summary="マイクを閉じて micAudio を止める。デコーダも解放する。",
              related=["micAudio", "startMicStreaming"]),
            m("sendMessage", "fun sendMessage(name: String, title: String, time: Long, text: String)",
              [("name", "String", "通知を出したアプリの名前"),
               ("title", "String", "送信者名など、通知の見出し"),
               ("time", "Long", "通知が届いた時刻。エポックミリ秒"),
               ("text", "String", "本文")],
              summary="スマホに届いた通知をグラスに転送する。件数の表示は syncNotificationCount が別にある。",
              related=["syncNotificationCount"]),
            m("syncNotificationCount", "fun syncNotificationCount(count: Int)",
              [("count", "Int", "未読の件数")],
              summary="未読通知の件数をグラスに知らせる。ホームの通知バッジに反映される。"
                      "未接続だと捨てられるので connected を見てから送る。",
              related=["sendMessage", "connected"]),
            m("sendDebugPhoneName", "fun sendDebugPhoneName(phoneName: String)",
              [("phoneName", "String", "スマホ本体の Bluetooth 名")],
              summary="開発用。接続元のスマホ名をグラスに送り、グラス側のデバッグ表示で"
                      "どの端末とつながっているか分かるようにする。"),
            m("addGlassPowerEventListener", "fun addGlassPowerEventListener(listener: () -> Unit)",
              [("listener", "() -> Unit")], related=["removeGlassPowerEventListener"]),
            m("removeGlassPowerEventListener", "fun removeGlassPowerEventListener(listener: () -> Unit)",
              [("listener", "() -> Unit")], related=["addGlassPowerEventListener"]),
            m("addRemoteControllerEventListener",
              "fun addRemoteControllerEventListener(listener: CommandManager.RemoteControlListener)",
              [("listener", "CommandManager.RemoteControlListener")],
              related=["removeRemoteControllerEventListener"]),
            m("removeRemoteControllerEventListener",
              "fun removeRemoteControllerEventListener(listener: CommandManager.RemoteControlListener)",
              [("listener", "CommandManager.RemoteControlListener")],
              related=["addRemoteControllerEventListener"]),
            m("parseResponse", "fun parseResponse(value: ByteArray)",
              [("value", "ByteArray", "グラスから届いたパケット")],
              summary="グラスから届いたパケットを解析する。"
                      "requestSystemStatus や requestSettingSync の応答を自前で受けるときに使う。"
                      "ジェスチャーや6DoFのように専用の Flow があるものは、そちらを購読すればよい。",
              related=["requestSystemStatus", "requestSettingSync"]),

            # ここから下は新ファーム向けのコマンド
            m("enterEmptyScreenPage", "fun enterEmptyScreenPage()",
              summary="汎用テキスト表示ページを開く。本文は sendEmptyScreenContent で送る。",
              related=["sendEmptyScreenContent", "sendEmptyScreenStatus"]),
            m("enterImageDisplayPage", "fun enterImageDisplayPage()",
              summary="画像表示ページを開く。技適マークの表示に使っている画面で、"
                      "画像は sendImage で送る。",
              related=["sendImage"]),
            m("sendLayout",
              "fun sendLayout(\n"
              "    mode: CommandManager.LayoutMode,\n"
              "    texts: Map<Int, String> = emptyMap(),\n"
              ")",
              [("mode", "CommandManager.LayoutMode",
                "`FULL` / `TOP_BOTTOM` / `LEFT_RIGHT` / `QUAD`"),
               ("texts", "Map<Int, String>", "領域番号ごとの表示テキスト")],
              summary="分割レイアウトを開いて、分割と初期テキストを送る。送るだけで画面が切り替わるので、"
                      "先にページを開く必要はない。モードを送るとレイアウトは作り直され、全領域の"
                      "テキストが消えたうえで texts が反映される。領域番号は分割ごとに意味が変わり、"
                      "`TOP_BOTTOM` なら 0=上・1=下、`QUAD` なら 0=左上・1=右上・2=左下・3=右下。"
                      "渡さなかった領域は空のまま。テキストは領域内で折り返し、あふれた分は切られる。"
                      "分割して送れないため、テキストの合計は190バイト程度までに収める。"
                      "FEATURE_VERSION 2.0.0 以上のファームが対象。",
              related=["sendLayoutTexts", "closeLayout"]),
            m("sendLayoutTexts", "fun sendLayoutTexts(texts: Map<Int, String>)",
              [("texts", "Map<Int, String>",
                "領域番号ごとの表示テキスト。空文字でその領域を消す")],
              summary="分割を保ったまま、指定した領域のテキストだけ差し替える。"
                      "レイアウトが閉じているときは全画面1領域として開く。",
              related=["sendLayout", "closeLayout"]),
            m("closeLayout", "fun closeLayout()",
              summary="分割レイアウトを閉じてホームなどに戻す。表示していたテキストは破棄される。"
                      "リモコンの戻る操作やホームへの遷移でも閉じる。",
              related=["sendLayout"]),
            m("sendCanvas",
              "fun sendCanvas(elements: List<CommandManager.CanvasElement>)",
              [("elements", "List<CommandManager.CanvasElement>",
                "配置する要素。id は 0..7 の8個まで")],
              summary="自由配置キャンバスを開いて、要素を置き直す。送るだけで画面が切り替わるので、"
                      "先にページを開く必要はない。今ある要素は全て消えてから elements が並ぶ。"
                      "キャンバスは 576×360 で、座標は左上が原点。はみ出した矩形は端で切られ、"
                      "キャンバスの外に出た要素は描かれない。テキストは矩形内で左揃えに折り返し、"
                      "あふれた分は切られる。分割して送れないため、テキストの合計は190バイト程度までに"
                      "収める。収まらないときは sendCanvasElements で1要素ずつ送れば表示は積み上がる。"
                      "FEATURE_VERSION 2.1.0 以上のファームが対象。",
              related=["sendCanvasElements", "clearCanvas", "closeCanvas"]),
            m("sendCanvasElements",
              "fun sendCanvasElements(elements: List<CommandManager.CanvasElement>)",
              [("elements", "List<CommandManager.CanvasElement>",
                "配置する要素。テキストが空の要素はその id を消す")],
              summary="今ある要素を残したまま、渡した要素だけ置き直す。既にある id に送ると座標と"
                      "サイズごと差し替わる。キャンバスが閉じているときは新しく開く。",
              related=["sendCanvas", "clearCanvas", "closeCanvas"]),
            m("sendCanvasImage",
              "fun sendCanvasImage(x: Int, y: Int, width: Int, height: Int, grayscale: ByteArray)",
              [("x", "Int", "画像の左上のx座標。x + width は 576 まで"),
               ("y", "Int", "画像の左上のy座標。y + height は 360 まで"),
               ("width", "Int", "画像の幅"),
               ("height", "Int", "画像の高さ"),
               ("grayscale", "ByteArray",
                "1画素1バイトのグレースケール。長さは width * height 以上")],
              summary="キャンバスに画像を置く。送るだけで画面が切り替わるので、先にページを開く必要はない。"
                      "今ある要素は残したまま、画像だけ差し替わる。渡すのはリサイズ済みのグレースケールで、"
                      "左上から行優先の並び。輝度は 0-255 のまま渡してよく、3bit(0-7)への量子化と"
                      "RLE圧縮はSDK内で行う。置けるのは1枚だけで、位置をずらして送っても最後の1枚しか残らない。"
                      "テキスト要素とは共存でき、テキストは画像の手前に描かれる。"
                      "ナビの全体ルート画像とバッファを共有しているため、"
                      "ナビ表示中は使えない。数百バイトずつに分けて送るので、大きい画像ほど表示まで時間がかかる。"
                      "FEATURE_VERSION 2.2.0 以上のファームが対象。",
              related=["sendCanvas", "sendCanvasElements", "clearCanvas"]),
            m("clearCanvas", "fun clearCanvas()",
              summary="キャンバスは開いたまま、全ての要素を消す。画像も一緒に消える。",
              related=["sendCanvas", "sendCanvasImage", "closeCanvas"]),
            m("closeCanvas", "fun closeCanvas()",
              summary="自由配置キャンバスを閉じてホームなどに戻す。表示していた要素は破棄される。"
                      "リモコンの戻る操作やホームへの遷移でも閉じる。",
              related=["sendCanvas"]),
            m("enterNavigationPage", "fun enterNavigationPage()",
              summary="ナビページを開く。案内内容は sendNaviStatus と sendNavi で送る。",
              related=["sendNaviStatus", "sendNavi"]),
            m("enterGlassAngleAdjustmentPage", "fun enterGlassAngleAdjustmentPage()",
              summary="ヘッドアップ角度調整ページを開く。閾値は sendWakeupTiltThreshold で送る。",
              related=["sendWakeupTiltThreshold"]),
            m("enterImuDebugPage", "fun enterImuDebugPage()",
              summary="IMU・照度のデバッグページを開く。"),
            m("sendTeleprompterLine",
              "fun sendTeleprompterLine(text: String, percent: Int, scrollUp: Boolean = false)",
              [("text", "String", "追記する1行"),
               ("percent", "Int", "スクロールバーの位置（0..100）"),
               ("scrollUp", "Boolean", "`true` で1行上へ、`false` で1行下へスクロールさせる")],
              summary="テレプロンプトに1行だけ追記する。全文を送り直す sendTeleprompterContent と違い、"
                      "読み上げに合わせて差分だけを流すのに使う。",
              related=["sendTeleprompterContent"]),
            m("sendTeleprompterStatus",
              "fun sendTeleprompterStatus(\n"
              "    status: CommandManager.TeleprompterStatus,\n"
              "    mode: CommandManager.TeleprompterMode,\n"
              ")",
              [("status", "CommandManager.TeleprompterStatus", "`READY` / `STARTED` / `PAUSED`"),
               ("mode", "CommandManager.TeleprompterMode",
                "`TELEPROMPT` / `TRANSCRIPT` / `TRANSLATION`")],
              summary="テレプロンプトの再生状態と表示モードを送る。"
                      "別パケットに分けると動作が安定しないため、ファーム側の都合で必ず両方まとめて送る。",
              related=["sendTeleprompterTime"]),
            m("sendTeleprompterTime", "fun sendTeleprompterTime(time: String)",
              [("time", "String", "`mm:ss` 形式の5文字。短ければ先頭を0埋め、長ければ切り捨てる")],
              summary="再生開始からの経過時間を送る。",
              related=["sendTeleprompterStatus"]),
            m("sendTeleprompterGenerating", "fun sendTeleprompterGenerating()",
              summary="テレプロンプトに生成中の表示を出す。",
              related=["sendTeleprompterContent"]),
            m("clearInscriptionText", "fun clearInscriptionText()",
              summary="テレプロンプトと翻訳の表示テキストを消す。"
                      "どちらもグラス側で同じバッファを共有しているため、消去も共通。",
              related=["sendTeleprompterContent", "sendTranslateContent"]),
            m("sendEmptyScreenContent", "fun sendEmptyScreenContent(content: String)",
              [("content", "String", "表示する本文")],
              summary="汎用テキスト表示ページに本文を送る。200バイトを超える分は分割して送られる。",
              related=["enterEmptyScreenPage"]),
            m("sendEmptyScreenStatus",
              "fun sendEmptyScreenStatus(status: CommandManager.TeleprompterStatus)",
              [("status", "CommandManager.TeleprompterStatus", "`READY` / `STARTED` / `PAUSED`")],
              summary="汎用テキスト表示ページの状態を送る。`READY` で空画面に戻る。",
              related=["enterEmptyScreenPage"]),
            m("sendImage", "fun sendImage(width: Int, height: Int, grayscale: ByteArray)",
              [("width", "Int", "画像の幅。196まで"),
               ("height", "Int", "画像の高さ。196まで"),
               ("grayscale", "ByteArray",
                "1画素1バイトのグレースケール。長さは `width * height` 以上")],
              summary="画像表示ページに画像を送る。enterImageDisplayPage で開いてから呼ぶ。"
                      "渡すのはリサイズ済みのグレースケールで、1画素1バイト・左上から行優先の並び。"
                      "輝度は 0-255 のままでよく、グラスが読む3bitへの量子化とRLE圧縮は SDK が行う。"
                      "グラス側のバッファは静的で、196x196 を超えるサイズはファーム側で弾かれ、"
                      "何も表示されない。",
              related=["enterImageDisplayPage"]),
            m("sendAiChatLanguage", "fun sendAiChatLanguage(languageCode: String)",
              [("languageCode", "String", '`"JPN"` / `"ENG"` / `"CHS"` / `"CHT"` 等の3文字')],
              summary="AI チャットの表示言語を通知する。グラス側の本文フォントの選択に使われ、"
                      "画面遷移は起こさない。フォントを先に確定させるため enterAiChatPage の前に送る。",
              related=["enterAiChatPage"]),
            m("clearAiChat", "fun clearAiChat()",
              summary="AI チャットの表示を消して先頭に戻す。FEATURE_VERSION 1.1.0 以降のファームが対象で、"
                      "未対応のファームはこのコマンドを読み捨てるため表示が残る。",
              related=["clearAiChatLegacy"]),
            m("clearAiChatLegacy", "fun clearAiChatLegacy()",
              summary="FEATURE_VERSION 1.1.0 未満のファーム向けに、改行を流し込んで見かけ上クリアする。"
                      "グラス側に履歴が残るため、対応ファームでは clearAiChat を使う。",
              related=["clearAiChat"]),
            m("sendNaviStatus", "fun sendNaviStatus(status: CommandManager.NaviStatus)",
              [("status", "CommandManager.NaviStatus", "`READY` / `START` / `ARRIVED`")],
              summary="ナビの状態を送る。sendNavi で送った案内は `START` のときだけ表示される。"
                      "`READY` は案内前の待機画面、`ARRIVED` は到着画面になる。",
              related=["enterNavigationPage", "sendNavi"]),
            m("sendNaviCourse", "fun sendNaviCourse(courseDegrees: Double)",
              [("courseDegrees", "Double", "進行方向[度]。0以上360未満。北を0として時計回り")],
              summary="端末の GPS 進行方向を送る。グラスは磁力計を持たず方位を単体で保てないため、"
                      "この値をジャイロのドリフト補正に使う。案内中に数秒おきに送る。",
              related=["sendNaviStatus"]),
            m("sendNaviLanguage", "fun sendNaviLanguage(languageCode: String)",
              [("languageCode", "String", '`"JPN"` / `"ENG"` 等の3文字')],
              summary="ナビ画面の表示言語を通知する。到着時刻ラベル等の切り替えに使われ、"
                      "画面遷移や表示状態には影響しない。",
              related=["enterNavigationPage"]),
            m("sendNavi",
              "fun sendNavi(\n"
              "    maneuverIcon: CommandManager.ManeuverIcon,\n"
              "    instructionText: String,\n"
              "    distanceText: String,\n"
              "    estimatedArrivalText: String,\n"
              "    timeAndDistanceText: String,\n"
              "    bitmapWidth: Int? = null,\n"
              "    bitmapHeight: Int? = null,\n"
              "    grayscale: ByteArray? = null,\n"
              ")",
              [("maneuverIcon", "CommandManager.ManeuverIcon",
                "次のポイントの進行方向アイコン。`TURN_LEFT` / `STRAIGHT` など"),
               ("instructionText", "String", "次のポイントでの指示。「○○を右折」など"),
               ("distanceText", "String", "次のポイントまでの距離。「300m」など"),
               ("estimatedArrivalText", "String", "予想到着時刻"),
               ("timeAndDistanceText", "String", "画面左下に出す残り時間と距離"),
               ("bitmapWidth", "Int?", "地図画像の幅。255まで。`grayscale` があるときは必須"),
               ("bitmapHeight", "Int?", "地図画像の高さ。255まで。`grayscale` があるときは必須"),
               ("grayscale", "ByteArray?",
                "地図画像。長さは `bitmapWidth * bitmapHeight` 以上")],
              summary="ナビの案内情報を送る。事前に sendNaviStatus で `START` にしておく。"
                      "地図画像は sendImage と同じく1画素1バイト・左上から行優先のグレースケールで、"
                      "3bitへの量子化とRLE圧縮は SDK が行う。",
              related=["enterNavigationPage", "sendNaviStatus", "sendNaviLargeImage"]),
            m("sendNaviLargeImage",
              "fun sendNaviLargeImage(width: Int, height: Int, grayscale: ByteArray)",
              [("width", "Int", "画像の幅"),
               ("height", "Int", "画像の高さ"),
               ("grayscale", "ByteArray",
                "1画素1バイトのグレースケール。長さは `width * height` 以上")],
              summary="ナビ画面に全体ルートの地図画像を送る。sendNavi に載せる地図より"
                      "大きいサイズを扱える。",
              related=["sendNavi"]),
            m("sendAdjust",
              "fun sendAdjust(\n"
              "    status: CommandManager.AdjustStatus,\n"
              "    imageType: CommandManager.AdjustImageType,\n"
              ")",
              [("status", "CommandManager.AdjustStatus", "`SHOW` / `CLOSE`"),
               ("imageType", "CommandManager.AdjustImageType",
                "`HOME` / `NAVIGATE` / `TELEPROMPT`")],
              summary="画面位置調整用の画像の表示を制御する。"),
            m("sendWakeupTiltThreshold", "fun sendWakeupTiltThreshold(degrees: Int)",
              [("degrees", "Int", "しきい値[度]。0..65535")],
              summary="ヘッドアップでウェイクアップする傾きのしきい値を設定する。",
              related=["enterGlassAngleAdjustmentPage"]),
            m("sendSettingPageVisibility", "fun sendSettingPageVisibility(show: Boolean)",
              [("show", "Boolean", "`true` で表示、`false` で非表示")],
              summary="グラス側の設定画面の表示・非表示を通知する。"),
            m("sendSetting",
              "fun sendSetting(name: String, value: Int)\n"
              "fun sendSetting(name: String, value: Boolean)\n"
              "fun sendSetting(name: String, value: String)\n"
              "fun sendSetting(name: String, value: ByteArray)",
              [("name", "String", "設定キー。`CommandManager.SettingKey` の定数を使う"),
               ("value", "Int / Boolean / String / ByteArray", "設定値")],
              summary="グラスの設定値を書き換える。値の型ごとにグラスへ送る型が変わるため、"
                      "文字列とバイト列は別の overload になっている。",
              related=["requestSettingSync"]),
            m("requestSettingSync", "fun requestSettingSync()",
              summary="全設定値の送信をグラスに要求する。応答は parseResponse で受ける。",
              related=["sendSetting", "parseResponse"]),
            m("requestLog", "fun requestLog(type: CommandManager.GlassLogType)",
              [("type", "CommandManager.GlassLogType",
                "`REALTIME` / `SYSLOG` / `RUNTIME` / `RESET_REASON` / `STOP`")],
              summary="グラスにログを要求する。クラッシュ前のログや再起動理由の調査に使う。"),
            m("startImuData", "fun startImuData()",
              summary="6DoF の送信を開始する。値は imuData に流れる。"
                      "FEATURE_VERSION 2.0.0 以上のファームが対象で、それ未満では何も起きない。"
                      "切断するとグラス側で止まるため、再接続後も続けるなら呼び直す。",
              related=["imuData", "stopImuData"]),
            m("stopImuData", "fun stopImuData()",
              summary="6DoF の送信を止める。",
              related=["imuData", "startImuData"]),
            m("requestNotificationCountSync", "fun requestNotificationCountSync()",
              summary="未読通知数の同期をグラスに要求する。",
              related=["syncNotificationCount"]),
            m("syncTime", "fun syncTime()",
              summary="端末の現在時刻をグラスに同期する。ホーム画面の時計に反映される。"),
            m("syncWeather", "fun syncWeather(type: CommandManager.WeatherType, value: Int)",
              [("type", "CommandManager.WeatherType", "`TEMPERATURE` か `ICON`"),
               ("value", "Int", "気温、または天気アイコンの種別")],
              summary="天気情報をグラスに同期する。気温とアイコンは別々に送る。"),
            m("requestSystemStatus", "fun requestSystemStatus()",
              summary="バッテリー残量・装着状態・充電状態の通知をグラスに要求する。"
                      "応答は parseResponse で受ける。",
              related=["parseResponse"]),
        ],
    },
    {
        "dir": "sdk-activity-host",
        "title": "SdkActivityHost",
        "nav_order": 5,
        "summary": "Activity を必要とする処理を差し込むシングルトン（Android のみ）。",
        "methods": [
            m(
                "showBleDeviceSelectionDialog",
                "var showBleDeviceSelectionDialog: ((Context, (String?) -> Unit) -> Unit)?",
                returns="((Context, (String?) -> Unit) -> Unit)?",
            ),
        ],
    },
    {
        "dir": "sdk-device-persistence",
        "title": "SdkDevicePersistence",
        "nav_order": 6,
        "summary": "最後に接続したデバイスの保存先。アプリ側が実装する SPI。",
        "methods": [
            m("lastDeviceId", "var lastDeviceId: String?", returns="String?"),
        ],
    },
    {
        "dir": "ble",
        "title": "BLE (Android)",
        "nav_order": 7,
        "summary": "Companion Device Manager 周り。Android 固有。",
        "methods": [
            m(
                "BleCompanionDeviceService.connectToLastDevice",
                "fun BleCompanionDeviceService.Companion.connectToLastDevice(context: Context)",
                [("context", "Context")],
            ),
            m(
                "BleDeviceSelector.showDialog",
                "fun showDialog(scope: CoroutineScope, singleTarget: Boolean, callback: (String?) -> Unit)",
                [("scope", "CoroutineScope"), ("singleTarget", "Boolean"), ("callback", "(String?) -> Unit")],
                related=["BleDeviceSelector.onActivityResult"],
            ),
            m(
                "BleDeviceSelector.onActivityResult",
                "fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, scope: CoroutineScope): Boolean",
                [("requestCode", "Int"), ("resultCode", "Int"), ("data", "Intent?"), ("scope", "CoroutineScope")],
                "Boolean",
                ["BleDeviceSelector.showDialog"],
            ),
        ],
    },
]


def slug(name):
    tail = name.split(".")[-1]
    return re.sub(r"(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])", "-", tail).lower()


def heading(type_title, name):
    if "." in name:
        return name
    if type_title == "GlassManager" and name == "getGlassManager":
        return name
    return f"{type_title}.{name}"


def method_page(group, meth, nav_order):
    lines = [
        "---",
        f"title: {meth['name']}",
        f"parent: {group['title']}",
        f"grandparent: {API_TITLE}",
        f"nav_order: {nav_order}",
        "---",
        "",
        f"# {heading(group['title'], meth['name'])}",
        "",
        "{: .warning }",
        "> このページは執筆中です。",
        "",
        "```kotlin",
        meth["sig"],
        "```",
        "",
        "## 概要",
        "",
        meth["summary"] or "<!-- WIP -->",
        "",
    ]
    if meth["summary"]:
        # 本文を書いたページに執筆中の警告は出さない
        lines = [x for x in lines if x not in ("{: .warning }", "> このページは執筆中です。")]
        lines = [x for i, x in enumerate(lines) if x or lines[i - 1 : i] != [""]]
    if meth["note"]:
        lines += ["{: .note }", f"> {meth['note']}", ""]
    if meth["params"]:
        lines += ["## 引数", "", "| 名前 | 型 | 説明 |", "|---|---|---|"]
        lines += [
            f"| `{n}` | `{t}` | {d or '<!-- WIP -->'} |" for n, t, d in meth["params"]
        ]
        lines += [""]
    lines += [
        "## 戻り値",
        "",
        f"`{meth['returns']}`",
        "",
        "## 使用例",
        "",
        f"<!-- snippet: {heading(group['title'], meth['name'])} -->",
        "<!-- WIP -->",
        "<!-- /snippet -->",
        "",
    ]
    if meth["related"]:
        lines += ["## 関連", ""]
        lines += [f"- [{r}]({slug(r)}.html)" for r in meth["related"]]
        lines += [""]
    return "\n".join(lines)


def table_sig(sig):
    """複数行のシグネチャを表のセルに収める。

    Markdown の表は改行でセルが切れる。宣言そのものの折り返しは1行に畳み、
    オーバーロードのように宣言が複数あるものは `<br>` で分ける。
    """
    decls = []
    for line in sig.splitlines():
        stripped = line.strip()
        if not stripped:
            continue
        if decls and not stripped.startswith(("fun ", "val ", "var ")):
            decls[-1] += " " + stripped
        else:
            decls.append(stripped)
    flat = [d.replace("( ", "(").replace(", )", ")").replace(" )", ")") for d in decls]
    return "<br>".join(f"`{d}`" for d in flat)


def type_index(group):
    lines = [
        "---",
        f"title: {group['title']}",
        f"parent: {API_TITLE}",
        f"nav_order: {group['nav_order']}",
        "has_children: true",
        "---",
        "",
        f"# {group['title']}",
        "",
        group["summary"],
        "",
        "| メソッド | シグネチャ |",
        "|---|---|",
    ]
    lines += [f"| [{x['name']}]({slug(x['name'])}.html) | {table_sig(x['sig'])} |"
              for x in group["methods"]]
    lines += [""]
    return "\n".join(lines)


def api_index():
    lines = [
        "---",
        f"title: {API_TITLE}",
        "nav_order: 4",
        "has_children: true",
        "---",
        "",
        f"# {API_TITLE}",
        "",
        "Sabera App SDK (Kotlin) の公開 API。バージョン 0.4.0 時点。",
        "",
        "メソッドごとに使えるようになったバージョンは"
        "[メソッドの追加履歴](../api-history.html)にまとめてある。",
        "",
        "| 型 | 説明 |",
        "|---|---|",
    ]
    lines += [f"| [{g['title']}]({g['dir']}/) | {g['summary']} |" for g in SPEC]
    lines += [""]
    return "\n".join(lines)


def link_index():
    """`_plugins/api_autolink.rb` が読む、名前 → ページの対応表。

    同じ名前が複数の型にある場合（`connected` など）はどちらを指すか決められないため、
    裸の名前では引けなくし、`GlassClient.connected` の形だけを残す。
    """
    entries = {}
    ambiguous = set()
    for group in SPEC:
        for meth in group["methods"]:
            url = f"/api/{group['dir']}/{slug(meth['name'])}.html"
            qualified = heading(group["title"], meth["name"])
            bare = meth["name"].split(".")[-1]
            for key in {qualified, meth["name"], bare}:
                if key in entries and entries[key] != url:
                    ambiguous.add(key)
                entries[key] = url
    for group in SPEC:
        entries[group["title"]] = f"/api/{group['dir']}/"

    lines = ["# scripts/gen-api-docs.py が生成する。直接編集しない。"]
    for key in sorted(entries.keys() - ambiguous):
        lines += [f'"{key}": "{entries[key]}"']
    return "\n".join(lines) + "\n"


def sync_nav_order(path, body):
    want = re.search(r"^nav_order: (\d+)$", body, re.M)
    current = path.read_text(encoding="utf-8")
    if want is None or re.search(r"^nav_order: \d+$", current, re.M) is None:
        return
    updated = re.sub(r"^nav_order: \d+$", want.group(0), current, count=1, flags=re.M)
    if updated != current:
        path.write_text(updated, encoding="utf-8")


def write(path, body, force, stats):
    if path.exists() and not force:
        # メソッドが増えると後ろのページの並び順がずれるので、nav_order だけは追従させる
        sync_nav_order(path, body)
        stats["skipped"] += 1
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(body, encoding="utf-8")
    stats["written"] += 1


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--force", action="store_true", help="既存ファイルを上書きする")
    args = parser.parse_args()

    stats = {"written": 0, "skipped": 0}
    write(DOCS / "api" / "index.md", api_index(), args.force, stats)
    for group in SPEC:
        base = DOCS / "api" / group["dir"]
        # 型のindexは表だけの生成物なので常に作り直す
        write(base / "index.md", type_index(group), True, stats)
        for i, meth in enumerate(group["methods"], start=1):
            write(base / f"{slug(meth['name'])}.md", method_page(group, meth, i), args.force, stats)

    # 対応表は SPEC から機械的に決まるので、常に作り直す
    index_path = DOCS / "_data" / "api_links.yml"
    index_path.parent.mkdir(parents=True, exist_ok=True)
    index_path.write_text(link_index(), encoding="utf-8")

    print(f"written={stats['written']} skipped={stats['skipped']}")


if __name__ == "__main__":
    main()
