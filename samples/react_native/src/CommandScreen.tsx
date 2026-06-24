import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
} from 'react-native';
import GlassesSdk from './GlassesSdk';
import { ConnectionState } from './GlassesSdk';
import { useGestureEvents } from './useGlasses';

interface Props {
  connection: ConnectionState;
}

export default function CommandScreen({ connection }: Props) {
  const gestures = useGestureEvents();
  const [error, setError] = useState<string | null>(null);

  const [teleprompterText, setTeleprompterText] = useState('Hello from React Native');
  const [aiText, setAiText] = useState('質問内容をどうぞ');
  const [translateText, setTranslateText] = useState('Translate this');
  const [sourceLang, setSourceLang] = useState('en');
  const [targetLang, setTargetLang] = useState('ja');

  const safeRun = async (action: () => Promise<void>) => {
    try {
      await action();
    } catch (e: any) {
      setError(e.message);
    }
  };

  const deviceName = connection.deviceName ?? connection.deviceId ?? 'Unknown';

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerText}>接続中: {deviceName}</Text>
      </View>
      <ScrollView style={styles.scroll} contentContainerStyle={styles.content}>
        {/* Page navigation */}
        <Text style={styles.sectionTitle}>ページ遷移</Text>
        <CommandButton label="Home に戻す" onPress={() => safeRun(GlassesSdk.enterHomePage)} />
        <CommandButton label="Teleprompter を開く" onPress={() => safeRun(GlassesSdk.enterTeleprompterPage)} />
        <CommandButton label="AI ページを開く" onPress={() => safeRun(() => GlassesSdk.enterAIPage(false))} />
        <CommandButton label="翻訳ページを開く" onPress={() => safeRun(GlassesSdk.enterTranslatePage)} />

        <View style={styles.divider} />

        {/* Text sending */}
        <Text style={styles.sectionTitle}>テキスト送信</Text>
        <SendableTextField
          label="Teleprompter テキスト"
          value={teleprompterText}
          onChangeText={setTeleprompterText}
          onSend={() => safeRun(() => GlassesSdk.sendTeleprompterContent(teleprompterText))}
        />
        <SendableTextField
          label="AI テキスト"
          value={aiText}
          onChangeText={setAiText}
          onSend={() => safeRun(() => GlassesSdk.sendAIContent(aiText))}
        />
        <SendableTextField
          label="翻訳テキスト"
          value={translateText}
          onChangeText={setTranslateText}
          onSend={() => safeRun(() => GlassesSdk.sendTranslateContent(translateText))}
        />

        {/* Language pair */}
        <View style={styles.langRow}>
          <View style={styles.langField}>
            <Text style={styles.inputLabel}>翻訳元</Text>
            <TextInput style={styles.input} value={sourceLang} onChangeText={setSourceLang} />
          </View>
          <View style={styles.langField}>
            <Text style={styles.inputLabel}>翻訳先</Text>
            <TextInput style={styles.input} value={targetLang} onChangeText={setTargetLang} />
          </View>
        </View>
        <CommandButton
          label="翻訳言語を送信"
          onPress={() => safeRun(() => GlassesSdk.sendTranslateLanguage(sourceLang, targetLang))}
        />

        <View style={styles.divider} />

        {/* Gesture events */}
        {gestures.length > 0 && (
          <>
            <Text style={styles.sectionTitle}>ジェスチャーイベント</Text>
            <Text style={styles.gestureText}>{gestures.join(', ')}</Text>
          </>
        )}

        {/* Disconnect */}
        <TouchableOpacity
          style={styles.disconnectButton}
          onPress={() => safeRun(GlassesSdk.disconnect)}>
          <Text style={styles.disconnectText}>切断</Text>
        </TouchableOpacity>

        {/* Error display */}
        {error && (
          <View style={styles.errorContainer}>
            <Text style={styles.errorText}>{error}</Text>
            <TouchableOpacity onPress={() => setError(null)}>
              <Text style={styles.clearError}>エラーを消す</Text>
            </TouchableOpacity>
          </View>
        )}
      </ScrollView>
    </View>
  );
}

function CommandButton({ label, onPress }: { label: string; onPress: () => void }) {
  return (
    <TouchableOpacity style={styles.commandButton} onPress={onPress}>
      <Text style={styles.commandButtonText}>{label}</Text>
    </TouchableOpacity>
  );
}

function SendableTextField({
  label,
  value,
  onChangeText,
  onSend,
}: {
  label: string;
  value: string;
  onChangeText: (text: string) => void;
  onSend: () => void;
}) {
  return (
    <View style={styles.sendableField}>
      <Text style={styles.inputLabel}>{label}</Text>
      <TextInput style={styles.input} value={value} onChangeText={onChangeText} multiline />
      <CommandButton label={`${label} を送信`} onPress={onSend} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  header: { padding: 16, backgroundColor: '#6750A4' },
  headerText: { color: '#fff', fontSize: 18, fontWeight: 'bold' },
  scroll: { flex: 1 },
  content: { padding: 24, paddingBottom: 48 },
  sectionTitle: { fontWeight: 'bold', fontSize: 14, marginBottom: 8 },
  commandButton: {
    backgroundColor: '#6750A4',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 8,
  },
  commandButtonText: { color: '#fff', fontSize: 14 },
  divider: { height: 1, backgroundColor: '#ddd', marginVertical: 16 },
  sendableField: { marginBottom: 12 },
  inputLabel: { fontSize: 12, color: '#666', marginBottom: 4 },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 10,
    marginBottom: 4,
    fontSize: 14,
  },
  langRow: { flexDirection: 'row', gap: 8, marginBottom: 8 },
  langField: { flex: 1 },
  gestureText: { marginBottom: 16, color: '#333' },
  disconnectButton: {
    borderWidth: 1,
    borderColor: '#6750A4',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  disconnectText: { color: '#6750A4', fontSize: 14 },
  errorContainer: { marginTop: 16, alignItems: 'center' },
  errorText: { color: 'red', marginBottom: 8 },
  clearError: { color: '#6750A4' },
});
