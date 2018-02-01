cordova.define("posPlugin.dspread_pos_plugin", function(require, exports, module) {
cordova.define("posPlugin.dspread_pos_plugin", function(require, exports, module) {
var exec = require('cordova/exec');

var posPlug =function(){};
posPlug.prototype.scanQPos2Mode=function(){
  exec(success,fail,"dspread_pos_plugin","scanQPos2Mode",[]);
}

posPlug.prototype.connectBluetoothDevice=function(isConnect){
  exec(success,fail,"dspread_pos_plugin","connectBluetoothDevice",[isConnect]);
}

posPlug.prototype.doTrade=function(timeout){
	  exec(success,faill,"dspread_pos_plugin","doTrade",[timeout]);
	}

posPlug.prototype.getDeviceList=function(){
	  exec(success,fail,"dspread_pos_plugin","getDeviceList",[]);
	}

posPlug.prototype.stopScanQPos2Mode=function(){
	  exec(success,fail,"dspread_pos_plugin","stopScanQPos2Mode",[]);
	}

posPlug.prototype.disconnectBT=function(){
	  exec(success,fail,"dspread_pos_plugin","disconnectBT",[]);
	}

posPlug.prototype.getQposInfo=function(){
	  exec(success,fail,"dspread_pos_plugin","getQposInfo",[]);
	}

posPlug.prototype.getQposId=function(){
	  exec(success,fail,"dspread_pos_plugin","getQposId",[]);
	}

posPlug.prototype.updateIPEK=function(){
	  exec(success,fail,"dspread_pos_plugin","updateIPEK",[]);
	}

posPlug.prototype.updateEmvApp=function(){
	  exec(success,fail,"dspread_pos_plugin","updateEmvApp",[]);
	}

posPlug.prototype.updateEmvCAPK=function(){
	  exec(success,fail,"dspread_pos_plugin","updateEmvCAPK",[]);
	}

posPlug.prototype.setMasterKey=function(){
	  exec(success,fail,"dspread_pos_plugin","setMasterKey",[key,checkValue]);
	}


var  pos=new posPlug();
module.exports=pos;

});

});
