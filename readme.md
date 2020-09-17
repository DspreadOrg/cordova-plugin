## Android Operation:

### 1.build cordova demo app command:
```
cordova build android
```

### run cordova demo app command:
```
cordova run android
```

### notes: If you encounter cordova gradlew: Command failed with exit code EACCES error

### solution: 
```
cordova platform rm android
cordova platform add android
```


## iOS Operation:

### 1.Add ios platform command:
```
cordova platform add ios
```

### 2.use xcode to open ./platforms/ios/posPlugin.xcworkspace

### 3.you can use xcode to debug you ios project. 


### Notes:
```
Undefined symbols for architecture arm64:
  "_OBJC_CLASS_$_QPOSUtil", referenced from:
      objc-class-ref in dspread_pos_plugin.o
  "_OBJC_CLASS_$_BTDeviceFinder", referenced from:
      objc-class-ref in dspread_pos_plugin.o
  "_OBJC_CLASS_$_QPOSService", referenced from:
      objc-class-ref in dspread_pos_plugin.o
ld: symbol(s) not found for architecture arm64
clang: error: linker command failed with exit code 1 (use -v to see invocation)
```

if you encounter above issue, pls remove reference libqpos-ios-sdk.a, then add reference libqpos-ios-sdk.a
