#!/usr/bin/env python3
"""docs/api 配下の API リファレンス雛形を生成する。

シグネチャは SDK 0.0.10 の AAR を javap -public で読んで確定したもの。
SDK のバージョンを上げてメソッドが増減したら SPEC を更新して再実行する。

既存ファイルは上書きしない（人間が書いた本文を守るため）。--force で上書き。
"""

import argparse
import re
from pathlib import Path

DOCS = Path(__file__).resolve().parent.parent / "docs"
API_TITLE = "API リファレンス"

AI_CHAT_ENUM = (
    "0.0.10 では引数の型が SDK 内部の型のため呼び出せない。"
    "次のリリースで `CommandManager` の入れ子 enum に変わり、アプリから呼べるようになる。"
)

UNRELEASED = "0.0.10 には含まれない。次のリリースから使える。"


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
            m("connected", "val connected: StateFlow<Boolean>", returns="StateFlow<Boolean>"),
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
            m("connected", "val connected: StateFlow<Boolean>", returns="StateFlow<Boolean>"),
            m("gestureEvents", "val gestureEvents: SharedFlow<GestureType>", returns="SharedFlow<GestureType>"),
            m("enterHomePage", "fun enterHomePage()"),
            m("enterTeleprompterPage", "fun enterTeleprompterPage()", related=["sendTeleprompterContent"]),
            m("sendTeleprompterContent",
              "fun sendTeleprompterContent(content: String)\n"
              "fun sendTeleprompterContent(content: String, percent: Int)",
              [("content", "String", "表示する原稿"),
               ("percent", "Int", "スクロールバーの位置（0..100）")],
              summary="テレプロンプトに原稿を送る。200バイトを超える分は分割して送られる。"
                      "`percent` つきの overload はスクロールバーの位置も一緒に送る。",
              related=["enterTeleprompterPage", "sendTeleprompterLine"],
              note="`percent` つきの overload は 0.0.10 には含まれない。"),
            m("enterAIPage", "fun enterAIPage(isAiPower: Boolean = false)", [("isAiPower", "Boolean")],
              related=["sendAIContent"]),
            m("sendAIContent", "fun sendAIContent(content: String)", [("content", "String")], related=["enterAIPage"]),
            m("enterTranslatePage", "fun enterTranslatePage()",
              related=["sendTranslateContent", "sendTranslateLanguage"]),
            m("sendTranslateContent", "fun sendTranslateContent(content: String)", [("content", "String")],
              related=["enterTranslatePage", "sendTranslateLanguage"]),
            m("sendTranslateLanguage", "fun sendTranslateLanguage(source: String, target: String)",
              [("source", "String"), ("target", "String")], related=["sendTranslateContent"]),
            m("enterMeetingPage", "fun enterMeetingPage()", related=["sendMeeting"]),
            m("sendMeeting", "fun sendMeeting(meetingType: Byte, text: String, percent: Int)",
              [("meetingType", "Byte"), ("text", "String"), ("percent", "Int")], related=["enterMeetingPage"]),
            m("enterAiChatPage", "fun enterAiChatPage()", related=["sendAiChatSenderText"]),
            m("sendAiChatSender", "fun sendAiChatSender(sender: CommandManager.AiChatSender)",
              [("sender", "CommandManager.AiChatSender", "吹き出しの主体。`USER` か `AI`")],
              summary="次に送る本文の吹き出しをどちら側にするかを切り替える。"
                      "本文と一度に送る sendAiChatSenderText の方が確実。",
              related=["sendAiChatSenderText"], note=AI_CHAT_ENUM),
            m("sendAiChatText", "fun sendAiChatText(text: String)", [("text", "String")],
              related=["enterAiChatPage"]),
            m("sendAiChatStatus", "fun sendAiChatStatus(status: CommandManager.AiChatStatus)",
              [("status", "CommandManager.AiChatStatus", "`GENERATING` か `COMPLETE`")],
              summary="AI 応答の生成状態を送る。`COMPLETE` を送るまでグラスは生成中の表示を続ける。",
              related=["sendAiChatSenderStatus"], note=AI_CHAT_ENUM),
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
              related=["sendAiChatText"], note=AI_CHAT_ENUM),
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
              related=["sendAiChatText"], note=AI_CHAT_ENUM),
            m("openGlassMic", "fun openGlassMic()",
              summary="グラスのマイクを開く。開いている間、音声は "
                      "`GlassClient.addAudioDataEventListener` に届く。"
                      "マイクのチャンネルは接続中のデバイスに合わせて SDK 側で決まる。",
              related=["closeGlassMic"]),
            m("closeGlassMic", "fun closeGlassMic()", related=["openGlassMic"]),
            m("sendMessage", "fun sendMessage(sender: String, body: String, timestamp: Long, appName: String)",
              [("sender", "String"), ("body", "String"), ("timestamp", "Long"), ("appName", "String")],
              related=["syncNotificationCount"]),
            m("syncNotificationCount", "fun syncNotificationCount(count: Int)", [("count", "Int")],
              related=["sendMessage"]),
            m("sendDebugPhoneName", "fun sendDebugPhoneName(phoneName: String)", [("phoneName", "String")]),
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
            m("parseResponse", "fun parseResponse(value: ByteArray)", [("value", "ByteArray")]),

            # ここから下は新ファーム向けに追加したコマンド。0.0.10 には含まれない
            m("enterNotificationPage", "fun enterNotificationPage()",
              summary="通知一覧ページを開く。", related=["sendMessage"], note=UNRELEASED),
            m("enterEmptyScreenPage", "fun enterEmptyScreenPage()",
              summary="汎用テキスト表示ページを開く。本文は sendEmptyScreenContent で送る。",
              related=["sendEmptyScreenContent", "sendEmptyScreenStatus"], note=UNRELEASED),
            m("enterGlassAngleAdjustmentPage", "fun enterGlassAngleAdjustmentPage()",
              summary="ヘッドアップ角度調整ページを開く。閾値は sendWakeupTiltThreshold で送る。",
              related=["sendWakeupTiltThreshold"], note=UNRELEASED),
            m("enterImuDebugPage", "fun enterImuDebugPage()",
              summary="IMU・照度のデバッグページを開く。", note=UNRELEASED),
            m("sendTeleprompterLine",
              "fun sendTeleprompterLine(text: String, percent: Int, scrollUp: Boolean = false)",
              [("text", "String", "追記する1行"),
               ("percent", "Int", "スクロールバーの位置（0..100）"),
               ("scrollUp", "Boolean", "`true` で1行上へ、`false` で1行下へスクロールさせる")],
              summary="テレプロンプトに1行だけ追記する。全文を送り直す sendTeleprompterContent と違い、"
                      "読み上げに合わせて差分だけを流すのに使う。",
              related=["sendTeleprompterContent"], note=UNRELEASED),
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
              related=["sendTeleprompterTime"], note=UNRELEASED),
            m("sendTeleprompterTime", "fun sendTeleprompterTime(time: String)",
              [("time", "String", "`mm:ss` 形式の5文字。短ければ先頭を0埋め、長ければ切り捨てる")],
              summary="再生開始からの経過時間を送る。",
              related=["sendTeleprompterStatus"], note=UNRELEASED),
            m("sendTeleprompterGenerating", "fun sendTeleprompterGenerating()",
              summary="テレプロンプトに生成中の表示を出す。",
              related=["sendTeleprompterContent"], note=UNRELEASED),
            m("clearInscriptionText", "fun clearInscriptionText()",
              summary="テレプロンプトと翻訳の表示テキストを消す。"
                      "どちらもグラス側で同じバッファを共有しているため、消去も共通。",
              related=["sendTeleprompterContent", "sendTranslateContent"], note=UNRELEASED),
            m("sendEmptyScreenContent", "fun sendEmptyScreenContent(content: String)",
              [("content", "String", "表示する本文")],
              summary="汎用テキスト表示ページに本文を送る。200バイトを超える分は分割して送られる。",
              related=["enterEmptyScreenPage"], note=UNRELEASED),
            m("sendEmptyScreenStatus",
              "fun sendEmptyScreenStatus(status: CommandManager.TeleprompterStatus)",
              [("status", "CommandManager.TeleprompterStatus", "`READY` / `STARTED` / `PAUSED`")],
              summary="汎用テキスト表示ページの状態を送る。`READY` で空画面に戻る。",
              related=["enterEmptyScreenPage"], note=UNRELEASED),
            m("sendAiChatLanguage", "fun sendAiChatLanguage(languageCode: String)",
              [("languageCode", "String", '`"JPN"` / `"ENG"` / `"CHS"` / `"CHT"` 等の3文字')],
              summary="AI チャットの表示言語を通知する。グラス側の本文フォントの選択に使われ、"
                      "画面遷移は起こさない。フォントを先に確定させるため enterAiChatPage の前に送る。",
              related=["enterAiChatPage"], note=UNRELEASED),
            m("clearAiChat", "fun clearAiChat()",
              summary="AI チャットの表示を消して先頭に戻す。FEATURE_VERSION 1.1.0 以降のファームが対象で、"
                      "未対応のファームはこのコマンドを読み捨てるため表示が残る。",
              related=["clearAiChatLegacy"], note=UNRELEASED),
            m("clearAiChatLegacy", "fun clearAiChatLegacy()",
              summary="FEATURE_VERSION 1.1.0 未満のファーム向けに、改行を流し込んで見かけ上クリアする。"
                      "グラス側に履歴が残るため、対応ファームでは clearAiChat を使う。",
              related=["clearAiChat"], note=UNRELEASED),
            m("sendAdjust",
              "fun sendAdjust(\n"
              "    status: CommandManager.AdjustStatus,\n"
              "    imageType: CommandManager.AdjustImageType,\n"
              ")",
              [("status", "CommandManager.AdjustStatus", "`SHOW` / `CLOSE`"),
               ("imageType", "CommandManager.AdjustImageType",
                "`HOME` / `NAVIGATE` / `TELEPROMPT`")],
              summary="画面位置調整用の画像の表示を制御する。",
              note=UNRELEASED),
            m("sendWakeupTiltThreshold", "fun sendWakeupTiltThreshold(degrees: Int)",
              [("degrees", "Int", "しきい値[度]。0..65535")],
              summary="ヘッドアップでウェイクアップする傾きのしきい値を設定する。",
              related=["enterGlassAngleAdjustmentPage"], note=UNRELEASED),
            m("sendSettingPageVisibility", "fun sendSettingPageVisibility(show: Boolean)",
              [("show", "Boolean", "`true` で表示、`false` で非表示")],
              summary="グラス側の設定画面の表示・非表示を通知する。",
              note=UNRELEASED),
            m("sendSetting",
              "fun sendSetting(name: String, value: Int)\n"
              "fun sendSetting(name: String, value: Boolean)\n"
              "fun sendSetting(name: String, value: String)\n"
              "fun sendSetting(name: String, value: ByteArray)",
              [("name", "String", "設定キー。`CommandManager.SettingKey` の定数を使う"),
               ("value", "Int / Boolean / String / ByteArray", "設定値")],
              summary="グラスの設定値を書き換える。値の型ごとにグラスへ送る型が変わるため、"
                      "文字列とバイト列は別の overload になっている。",
              related=["requestSettingSync"], note=UNRELEASED),
            m("requestSettingSync", "fun requestSettingSync()",
              summary="全設定値の送信をグラスに要求する。応答は parseResponse で受ける。",
              related=["sendSetting", "parseResponse"], note=UNRELEASED),
            m("requestLog", "fun requestLog(type: CommandManager.GlassLogType)",
              [("type", "CommandManager.GlassLogType",
                "`REALTIME` / `SYSLOG` / `RUNTIME` / `RESET_REASON` / `STOP`")],
              summary="グラスにログを要求する。クラッシュ前のログや再起動理由の調査に使う。",
              note=UNRELEASED),
            m("requestNotificationCountSync", "fun requestNotificationCountSync()",
              summary="未読通知数の同期をグラスに要求する。",
              related=["syncNotificationCount"], note=UNRELEASED),
            m("syncTime", "fun syncTime()",
              summary="端末の現在時刻をグラスに同期する。ホーム画面の時計に反映される。",
              note=UNRELEASED),
            m("syncWeather", "fun syncWeather(type: CommandManager.WeatherType, value: Int)",
              [("type", "CommandManager.WeatherType", "`TEMPERATURE` か `ICON`"),
               ("value", "Int", "気温、または天気アイコンの種別")],
              summary="天気情報をグラスに同期する。気温とアイコンは別々に送る。",
              note=UNRELEASED),
            m("requestSystemStatus", "fun requestSystemStatus()",
              summary="バッテリー残量・装着状態・充電状態の通知をグラスに要求する。"
                      "応答は parseResponse で受ける。",
              related=["parseResponse"], note=UNRELEASED),
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
    lines += [f"| [{x['name']}]({slug(x['name'])}.html) | `{x['sig']}` |" for x in group["methods"]]
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
        "Sabera App SDK (Kotlin) の公開 API。配布中のバージョンは 0.0.10。",
        "",
        "「0.0.10 には含まれない」と注記のあるメソッドは、新ファーム向けに SDK 側へ追加した分で、",
        "次のリリース以降で使える。",
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


def write(path, body, force, stats):
    if path.exists() and not force:
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
        write(base / "index.md", type_index(group), args.force, stats)
        for i, meth in enumerate(group["methods"], start=1):
            write(base / f"{slug(meth['name'])}.md", method_page(group, meth, i), args.force, stats)

    # 対応表は SPEC から機械的に決まるので、常に作り直す
    index_path = DOCS / "_data" / "api_links.yml"
    index_path.parent.mkdir(parents=True, exist_ok=True)
    index_path.write_text(link_index(), encoding="utf-8")

    print(f"written={stats['written']} skipped={stats['skipped']}")


if __name__ == "__main__":
    main()
