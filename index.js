import { NativeModules } from "react-native";

const { Ringtones } = NativeModules;

export const RINGTONE_TYPES = {
	all: 7,
	ringtone: 1,
	notification: 2,
	alarm: 4,
};

export default Ringtones;
