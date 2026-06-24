import React from 'react';
import { useConnectionState } from './src/useGlasses';
import ScanScreen from './src/ScanScreen';
import CommandScreen from './src/CommandScreen';

export default function App() {
  const connection = useConnectionState();

  if (connection.connected) {
    return <CommandScreen connection={connection} />;
  }
  return <ScanScreen />;
}
