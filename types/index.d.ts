declare module "react-native-rintones" {
  export interface Ringtone {
    title: string;
    uri: string;
  }

  export default interface Ringtones {
    getRingtones({ type }: { type: number }): Promise<Ringtone[]>;

    getActualRingtone(): Promise<Ringtone>;

    setRingtone({ uri }: { uri: string | void }): Promise<boolean>;

    setNewRingtone({
      filepath,
      title,
      mimeType,
      artist,
      isRingtone,
      isNotification,
      isAlarm,
      isMusic,
      isSetDefault,
    }: {
      filepath: string;
      title: string;
      mimeType: string;
      artist: string;
      isRingtone: boolean;
      isNotification: boolean;
      isAlarm: boolean;
      isMusic: boolean;
      isSetDefault: boolean;
    }): Promise<Ringtone>;

    deleteRingtone({ uri }: { uri: string }): Promise<boolean>;

    hasPermission(): Promise<boolean>;

    requestSettingsPermission(): void;
  }
}
