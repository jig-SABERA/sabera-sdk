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

AI_CHAT_INTERNAL = (
    "引数の型が SDK 内部の型のため、アプリからは呼び出せない。"
    "AI チャットに文字列を送るには [sendAiChatText](send-ai-chat-text.html) を使う。"
)


def m(name, sig, params=(), returns="Unit", related=(), note=None):
    return {
        "name": name,
        "sig": sig,
        "params": list(params),
        "returns": returns,
        "related": list(related),
        "note": note,
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
            m("sendTeleprompterContent", "fun sendTeleprompterContent(content: String)", [("content", "String")],
              related=["enterTeleprompterPage"]),
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
            m("sendAiChatSender", "fun sendAiChatSender(sender: PacketCommandUtils.AiChatSender)",
              [("sender", "PacketCommandUtils.AiChatSender")], related=["sendAiChatText"], note=AI_CHAT_INTERNAL),
            m("sendAiChatText", "fun sendAiChatText(text: String)", [("text", "String")],
              related=["enterAiChatPage"]),
            m("sendAiChatStatus", "fun sendAiChatStatus(status: PacketCommandUtils.AiChatStatus)",
              [("status", "PacketCommandUtils.AiChatStatus")], related=["sendAiChatText"], note=AI_CHAT_INTERNAL),
            m("sendAiChatSenderText",
              "fun sendAiChatSenderText(sender: PacketCommandUtils.AiChatSender, text: String)",
              [("sender", "PacketCommandUtils.AiChatSender"), ("text", "String")],
              related=["sendAiChatText"], note=AI_CHAT_INTERNAL),
            m("sendAiChatSenderStatus",
              "fun sendAiChatSenderStatus(sender: PacketCommandUtils.AiChatSender, status: PacketCommandUtils.AiChatStatus)",
              [("sender", "PacketCommandUtils.AiChatSender"), ("status", "PacketCommandUtils.AiChatStatus")],
              related=["sendAiChatText"], note=AI_CHAT_INTERNAL),
            m("openGlassMic", "fun openGlassMic()", related=["closeGlassMic"]),
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
        "<!-- WIP -->",
        "",
    ]
    if meth["note"]:
        lines += ["{: .note }", f"> {meth['note']}", ""]
    if meth["params"]:
        lines += ["## 引数", "", "| 名前 | 型 | 説明 |", "|---|---|---|"]
        lines += [f"| `{n}` | `{t}` | <!-- WIP --> |" for n, t in meth["params"]]
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
        "nav_order: 3",
        "has_children: true",
        "---",
        "",
        f"# {API_TITLE}",
        "",
        "Sabera App SDK (Kotlin) の公開 API。バージョン 0.0.10 時点。",
        "",
        "| 型 | 説明 |",
        "|---|---|",
    ]
    lines += [f"| [{g['title']}]({g['dir']}/) | {g['summary']} |" for g in SPEC]
    lines += [""]
    return "\n".join(lines)


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

    print(f"written={stats['written']} skipped={stats['skipped']}")


if __name__ == "__main__":
    main()
