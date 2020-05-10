import React, {Component} from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  ScrollView,
  Alert,
} from 'react-native';
import Ringtones, {RingtoneManager} from 'react-native-ringtones';

export default class App extends Component {
  state = {
    currentRingtone: {
      title: '',
      uri: '',
    },
    ringtones: [],
  };
  componentDidMount() {}

  onPressGetList = async () => {
    try {
      const ringtones = await Ringtones.getRingtones({
        type: RingtoneManager.TYPE_ALL,
      });
      this.setState({ringtones});
    } catch (error) {
      console.log('error', error);
    }
  };

  onPressGetCurrentRingtone = async () => {
    try {
      const ringtone = await Ringtones.getActualRingtone();
      this.setState({currentRingtone: ringtone});
    } catch (error) {
      console.log('error', error);
    }
  };

  onPressSetRingtone = async (ringtone) => {
    try {
      const isHasPermission = await Ringtones.hasPermission();
      if (!isHasPermission) {
        Ringtones.requestSettingsPermission();
        return;
      }
      const isSuccess = await Ringtones.setRingrone({
        uri: ringtone.uri,
      });
      if (isSuccess) {
        await this.onPressGetCurrentRingtone();
        Alert.alert(
          'Successed!',
          `Ringtone has been changed to ${ringtone.title}`,
        );
      }
    } catch (error) {
      console.log('error', error);
    }
  };

  onPressDeleteRingtone = async (ringtone) => {
    try {
      const isSuccess = await Ringtones.deleteRingtone({
        uri: ringtone.uri,
      });
      if (isSuccess) {
        await this.onPressGetList();
        Alert.alert(
          'Successed!',
          `Ringtone "${ringtone.title}" has been deleted`,
        );
      }
    } catch (error) {
      console.log('error', error);
    }
  };

  onPressAddNewRingtone = async () => {
    try {
      const ringtone = await Ringtones.setNewRingrone({
        filepath: '/sdcard/mp3_2010.mp3',
        title: 'Meow Meow',
        mimeType: 'audio/mp3',
        artist: 'Mr.Meo',
        isRingtone: true,
        isNotification: false,
        isAlarm: false,
        isMusic: false,
        isSetDefault: true,
      });

      if (ringtone) {
        Alert.alert(
          'Successed!',
          `New ringtone "${ringtone.title}" has been added ${
            ringtone.isSetDefault ? 'and actived' : ''
          }`,
        );
      }
    } catch (error) {
      console.log('error', error);
    }
  };

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity style={styles.button} onPress={this.onPressGetList}>
          <Text style={styles.textButton}>Get List</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.button}
          onPress={this.onPressAddNewRingtone}>
          <Text style={styles.textButton}>Add New Ringtone</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.button}
          onPress={this.onPressGetCurrentRingtone}>
          <Text style={styles.textButton}>Get Current Ringtone</Text>
        </TouchableOpacity>
        <Text style={styles.textButton}>Current Ringtone:</Text>
        <View style={styles.item}>
          <Text>title: {this.state.currentRingtone.title}</Text>
          <Text>uri: {this.state.currentRingtone.uri}</Text>
        </View>
        <Text>List: {this.state.ringtones.length} items</Text>
        <ScrollView>
          {this.state.ringtones.map((ringtone, index) => (
            <View
              key={index}
              style={styles.item}
              onPress={() => this.onPressSetRingtone(ringtone)}>
              <Text>title: {ringtone.title}</Text>
              <Text>uri: {ringtone.uri}</Text>
              <TouchableOpacity
                onPress={() => this.onPressSetRingtone(ringtone)}>
                <Text style={styles.textButton}>Set Default</Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => this.onPressDeleteRingtone(ringtone)}>
                <Text style={styles.textButton}>Delete</Text>
              </TouchableOpacity>
            </View>
          ))}
        </ScrollView>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 20,
    margin: 20,
  },
  button: {
    padding: 10,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 1,
    marginBottom: 10,
  },
  item: {
    padding: 5,
    borderWidth: 1,
    marginVertical: 5,
    borderColor: '#33333350',
  },
  textButton: {
    fontWeight: 'bold',
  },
});
