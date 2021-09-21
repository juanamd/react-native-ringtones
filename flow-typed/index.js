declare module "react-native-ringtones" {
	declare export type Ringtone = {
		title: string,
		uri: string,
	}

	declare export type RingtoneParams = {
		filepath: string,
		title: string,
		mimeType?: string,
		artist?: string,
		isRingtone?: boolean,
		isNotification?: boolean,
		isAlarm?: boolean,
		isMusic?: boolean,
	}

	declare export var RINGTONE_TYPES: {
		all: number,
		ringtone: number,
		notification: number,
		alarm: number,
	};

	declare export default class Ringtones {
		static getRingtones(type: number): Promise<Ringtone[]>;
		static getMediaStoreRingtones(): Promise<Ringtone[]>;
		static getActualRingtone(type: number): Promise<Ringtone>;
		static setRingtone(uri: string | null, type: number): Promise<void>;
		static setNewRingtone(params: RingtoneParams): Promise<Ringtone>;
		static deleteRingtone(uri: string): Promise<number>;
		static hasSettingsPermission(): Promise<boolean>;
		static requestSettingsPermission(): Promise<boolean>;
	}
}
