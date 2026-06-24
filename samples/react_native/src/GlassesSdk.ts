import { NativeModules, NativeEventEmitter } from 'react-native';

const { GlassesSdkModule } = NativeModules;

export const GlassesSdkEmitter = new NativeEventEmitter(GlassesSdkModule);

export interface ConnectionState {
  connected: boolean;
  deviceId: string | null;
  deviceName: string | null;
}

export interface GestureEvent {
  type: string;
}

const GlassesSdk = {
  showSelectionDialog: (): Promise<{ deviceId: string; deviceName: string } | null> =>
    GlassesSdkModule.showSelectionDialog(),

  disconnect: (): Promise<void> => GlassesSdkModule.disconnect(),

  // Page navigation
  enterHomePage: (): Promise<void> => GlassesSdkModule.enterHomePage(),
  enterTeleprompterPage: (): Promise<void> => GlassesSdkModule.enterTeleprompterPage(),
  enterAIPage: (isAiPower = false): Promise<void> => GlassesSdkModule.enterAIPage(isAiPower),
  enterTranslatePage: (): Promise<void> => GlassesSdkModule.enterTranslatePage(),

  // Content sending
  sendTeleprompterContent: (content: string): Promise<void> =>
    GlassesSdkModule.sendTeleprompterContent(content),
  sendAIContent: (content: string): Promise<void> =>
    GlassesSdkModule.sendAIContent(content),
  sendTranslateContent: (content: string): Promise<void> =>
    GlassesSdkModule.sendTranslateContent(content),
  sendTranslateLanguage: (source: string, target: string): Promise<void> =>
    GlassesSdkModule.sendTranslateLanguage(source, target),
};

export default GlassesSdk;
