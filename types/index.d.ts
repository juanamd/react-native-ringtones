declare module "react-native-rintones" {
	export type Ringtone = {
		title: string,
		uri: string,
	}

	export type RingtoneParams = {
		filepath: string,
		title: string,
		mimeType?: string,
		artist?: string,
		isRingtone?: boolean,
		isNotification?: boolean,
		isAlarm?: boolean,
		isMusic?: boolean,
		isSetDefault?: boolean,
	}

	export default interface Ringtones {
		getRingtones(type: number): Promise<Ringtone[]>;
		getMediaStoreRingtones(type: number): Promise<Ringtone[]>;
		getActualRingtone(): Promise<Ringtone>;
		setRingtone(uri: string | void): Promise<void>;
		setNewRingtone(params: RingtoneParams): Promise<Ringtone>;
		deleteRingtone(uri: string): Promise<number>;
		hasSettingsPermission(): Promise<boolean>;
		requestSettingsPermission(): Promise<boolean>;
	}
}
