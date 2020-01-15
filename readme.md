build cordova demo app command:
cordova build android

run cordova demo app command:
cordova run android

notes: If you encounter cordova gradlew: Command failed with exit code EACCES error

solution: cordova platform rm android
          cordova platform add android
