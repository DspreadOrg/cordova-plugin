var exec = require('cordova/exec');

var posPlug = {
	  scanQPos2Mode:function(success,fail){
	   exec(success,fail,"dspread_pos_plugin","scanQPos2Mode",[]);
	},
	
	  connectBluetoothDevice:function(success,fail,isConnect){
		  exec(success,fail,"dspread_pos_plugin","connectBluetoothDevice",[isConnect]);
	},

	doTrade:function(success,faill,timeout){
		  exec(success,faill,"dspread_pos_plugin","doTrade",[timeout]);
		},
	
	getDeviceList:function(success,fail){
		  exec(success,fail,"dspread_pos_plugin","getDeviceList",[]);
		},
	
	stopScanQPos2Mode:function(success,fail){
		  exec(success,fail,"dspread_pos_plugin","stopScanQPos2Mode",[]);
		},
	
	disconnectBT:function(success,fail){
		  exec(success,fail,"dspread_pos_plugin","disconnectBT",[]);
		},
	
	getQposInfo:function(success,fail){
		  exec(success,fail,"dspread_pos_plugin","getQposInfo",[]);
		},
	
	getQposId:function(success,fail){
		  exec(success,fail,"dspread_pos_plugin","getQposId",[]);
		},
	
	updateIPEK:function(success,fail){
		  exec(success,fail,"dspread_pos_plugin","updateIPEK",[]);
		},
	
	updateEmvApp:function(success,fail){
		  exec(success,fail,"dspread_pos_plugin","updateEmvApp",[]);
		},
	
	updateEmvCAPK:function(success,fail){
		  exec(success,fail,"dspread_pos_plugin","updateEmvCAPK",[]);
		},
	
	setMasterKey:function(success,fail,key,checkValue){
		  exec(success,fail,"dspread_pos_plugin","setMasterKey",[key,checkValue]);
		},
	updatePosFirmware:function(success,fail){
		exec(success,fail,"dspread_pos_plugin","updatePosFirmware",[]);
	}
	
	};
	module.exports =posPlug;