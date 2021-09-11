declare module "react-native-ringtones" {
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

	export default class Ringtones {
		static getRingtones(type: number): Promise<Ringtone[]>;
		static getMediaStoreRingtones(): Promise<Ringtone[]>;
		static getActualRingtone(): Promise<Ringtone>;
		static setRingtone(uri: string | null): Promise<void>;
		static setNewRingtone(params: RingtoneParams): Promise<Ringtone>;
		static deleteRingtone(uri: string): Promise<number>;
		static hasSettingsPermission(): Promise<boolean>;
		static requestSettingsPermission(): Promise<boolean>;
	}
}
