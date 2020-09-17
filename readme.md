Android Operation:

build cordova demo app command:
cordova build android

run cordova demo app command:
cordova run android

notes: If you encounter cordova gradlew: Command failed with exit code EACCES error

solution: cordova platform rm android
          cordova platform add android


iOS Operation:

1.Add ios platform command:
  cordova platform add ios

2.use xcode to open ./platforms/ios/posPlugin.xcworkspace

3.you can use xcode to debug you ios project. 
