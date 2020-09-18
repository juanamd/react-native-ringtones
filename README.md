# react-native-ringtones
Based on **[minhtc/react-native-ringtones](https://github.com/minhtc/react-native-ringtones)**

## Getting started

`$ yarn add https://github.com/juanamd/react-native-ringtones`

## Usage

```javascript
import Ringtones from 'react-native-ringtones';

// Get all ringtones
getRingtones({ type }: { type: number }): Promise<Ringtone[]>;

// Get current active ringtone
getActualRingtone(): Promise<Ringtone>;

// Set rintone
setRingtone({ uri }: { uri: string }): Promise<boolean>;

// Add new ringtone
// filepath can be either an audio file accessible by the app (including internal storage)
//  -> filepath: "/data/user/0/com.myapp/files/my_sound.mp3"
// or the file name of any resource inside the "raw" folder (without extension)
//  -> filepath: "my_sound"
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

// Delete ringtone by uri
deleteRingtone({ uri }: { uri: string }): Promise<boolean>;

// Check modify system settings
hasPermission(): Promise<boolean>;

// Request modify system setting to change the ringtone
requestSettingsPermission(): void;
```
