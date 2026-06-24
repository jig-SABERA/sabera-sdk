import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import GlassesSdk from './GlassesSdk';

export default function ScanScreen() {
  const [scanning, setScanning] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleScan = async () => {
    setError(null);
    setScanning(true);
    try {
      await GlassesSdk.showSelectionDialog();
    } catch (e: any) {
      setError(`Connection error: ${e.message}`);
    } finally {
      setScanning(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Glasses SDK React Native Sample</Text>
      <Text style={styles.description}>
        OS のデバイス選択ダイアログから glasses を選んでください
      </Text>
      <TouchableOpacity
        style={[styles.button, scanning && styles.buttonDisabled]}
        onPress={handleScan}
        disabled={scanning}>
        <Text style={styles.buttonText}>
          {scanning ? 'スキャン中...' : 'スキャン開始'}
        </Text>
      </TouchableOpacity>
      {error && (
        <View style={styles.errorContainer}>
          <Text style={styles.errorText}>{error}</Text>
          <TouchableOpacity onPress={() => setError(null)}>
            <Text style={styles.clearError}>エラーを消す</Text>
          </TouchableOpacity>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 24 },
  title: { fontSize: 20, fontWeight: 'bold', marginBottom: 16 },
  description: { fontSize: 14, color: '#666', marginBottom: 24, textAlign: 'center' },
  button: { backgroundColor: '#6750A4', paddingHorizontal: 32, paddingVertical: 12, borderRadius: 24 },
  buttonDisabled: { opacity: 0.5 },
  buttonText: { color: '#fff', fontSize: 16 },
  errorContainer: { marginTop: 16, alignItems: 'center' },
  errorText: { color: 'red', marginBottom: 8 },
  clearError: { color: '#6750A4' },
});
