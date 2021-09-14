# react-native-ringtones
Based on **[minhtc/react-native-ringtones](https://github.com/minhtc/react-native-ringtones)**

## Getting started

    yarn add https://github.com/juanamd/react-native-ringtones

## Permissions
If your app is used on devices that run Android API 18 or lower, you should add the `WRITE_EXTERNAL_STORAGE` permission to your app manifest, as described [here](https://developer.android.com/training/data-storage).

## Usage

```typescript
import Ringtones, { Ringtone, RingtoneParams, RINGTONE_TYPES } from 'react-native-ringtones';

// Get all ringtones. Use RINGTONE_TYPES constants
Ringtones.getRingtones(type: number): Promise<Ringtone[]>;

// Get only the ringtones available from MediaStore
Ringtones.getMediaStoreRingtones(): Promise<Ringtone[]>;

// Get current active ringtone
Ringtones.getActualRingtone(): Promise<Ringtone>;

// Set rintone
Ringtones.setRingtone(uri: string): Promise<void>;

/* Add new ringtone.
filepath can be either an audio file accessible by the app (including internal storage)
-> filepath: "/data/user/0/com.myapp/files/my_sound.mp3"
or the file name of any resource inside the "raw" folder (without extension)
-> filepath: "my_sound" */
Ringtones.setNewRingtone(params: RingtoneParams): Promise<Ringtone>;

// Delete ringtone by uri. Returns number of deleted items
Ringtones.deleteRingtone(uri: string): Promise<number>;

// Check if access to ACTION_MANAGE_WRITE_SETTINGS has been granted
Ringtones.hasSettingsPermission(): Promise<boolean>;

// Request the user to enable ACTION_MANAGE_WRITE_SETTINGS permission
// Returns false if the permission has already been granted
Ringtones.requestSettingsPermission(): Promise<boolean>;
```
