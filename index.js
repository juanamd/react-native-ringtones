import { NativeModules } from "react-native";

const { Ringtones } = NativeModules;

export const RINGTONE_TYPES = {
	TYPE_ALL: 7,
	TYPE_RINGTONE: 1,
	TYPE_NOTIFICATION: 2,
	TYPE_ALARM: 4,
};

export default Ringtones;
