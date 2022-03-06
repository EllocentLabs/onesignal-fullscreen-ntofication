# onesignal-full-screen-notification

full screen android notificattion

## Installation

```sh
npm install onesignal-full-screen-notification
```

## Linking

# Add This permission in AndroidManifest.xml

<uses-permission android:name="android.permission.VIBRATE" />

# Add This under in application tag in AndroidManifest.xml

<activity android:name="com.onesignalfullscreennotification.RideActivity" />
<meta-data 
  android:name="com.onesignal.NotificationServiceExtension"
  android:value="com.onesignalfullscreennotification.NotificationServiceExtension" 
/>

https://documentation.onesignal.com/docs/react-native-sdk-setup

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
