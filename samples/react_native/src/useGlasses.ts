import { useState, useEffect } from 'react';
import { GlassesSdkEmitter, ConnectionState, GestureEvent } from './GlassesSdk';

export function useConnectionState() {
  const [state, setState] = useState<ConnectionState>({
    connected: false,
    deviceId: null,
    deviceName: null,
  });

  useEffect(() => {
    const sub = GlassesSdkEmitter.addListener('onConnectionStateChange', (event: ConnectionState) => {
      setState(event);
    });
    return () => sub.remove();
  }, []);

  return state;
}

export function useGestureEvents() {
  const [gestures, setGestures] = useState<string[]>([]);

  useEffect(() => {
    const sub = GlassesSdkEmitter.addListener('onGestureEvent', (event: GestureEvent) => {
      setGestures((prev) => [...prev, event.type]);
    });
    return () => sub.remove();
  }, []);

  return gestures;
}
