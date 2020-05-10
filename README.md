# react-native-ringtones

## Getting started

`$ npm install react-native-ringtones --save`

### Mostly automatic installation

`$ react-native link react-native-ringtones`

## Usage

```javascript
import Ringtones from 'react-native-ringtones';

// Get all ringtones
getRingtones({ type }: { type: number }): Promise<Ringtone[]>;

// Get current active ringtone
getActualRingtone(): Promise<Ringtone>;

// Set rintone
setRingrone({ uri }: { uri: string }): Promise<boolean>;

// Add new ringtone
setNewRingrone({
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
}): Prmise<Ringtone>;

// Delete ringtone by uri
deleteRingtone({ uri }: { uri: string }): Promise<boolean>;

// Check modify system settings
hasPermission(): Promise<boolean>;

// Request modify system setting to change the ringtone
requestSettingsPermission(): void;
```

## Example

![React Native Ringtone Example](https://github.com/minhtc/react-native-ringtones/raw/master/screenshots/ss.png "React Native Ringtone Example")

## Question?

https://github.com/minhtc/react-native-ringtones/issues
