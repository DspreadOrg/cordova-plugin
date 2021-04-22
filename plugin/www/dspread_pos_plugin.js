var exec = require('cordova/exec');

var posPlug = {
	scanQPos2Mode:function(success,fail){
	    exec(success,fail,"dspread_pos_plugin","scanQPos2Mode",[]);
	},

	openUart:function(success,fail){
	    exec(success,fail,"dspread_pos_plugin","openUart",[]);
	},

	connectBluetoothDevice:function(success,fail,isConnect,bluetoothAddress){
		exec(success,fail,"dspread_pos_plugin","connectBluetoothDevice",[isConnect,bluetoothAddress]);
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
	
	disconnect:function(success,fail){
		exec(success,fail,"dspread_pos_plugin","disconnect",[]);
	},
               
    updateEMVConfigByXml:function(success,fail,xmlStr){
        exec(success,fail,"dspread_pos_plugin","updateEMVConfigByXml",[xmlStr]);
    },
	
	getQposInfo:function(success,fail){
		exec(success,fail,"dspread_pos_plugin","getQposInfo",[]);
	},
	
	getQposId:function(success,fail){
		exec(success,fail,"dspread_pos_plugin","getQposId",[]);
	},
	
	updateIPEK:function(success,fail,ipekgroup, trackksn, trackipek, trackipekCheckvalue, emvksn, emvipek, emvipekCheckvalue, pinksn, pinipek, pinipekCheckvalue){
		exec(success,fail,"dspread_pos_plugin","updateIPEK",[ipekgroup, trackksn, trackipek, trackipekCheckvalue, emvksn, emvipek, emvipekCheckvalue, pinksn, pinipek, pinipekCheckvalue]);
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
	},

	getIccCardNo:function(success,fail){
		exec(success,fail,"dspread_pos_plugin","getIccCardNo",[]);
	},
	
	setAmount:function(success,fail,amount,cashbackAmount,currencyCode,transactionType){
    	exec(success,fail,"dspread_pos_plugin","setAmount",[amount,cashbackAmount,currencyCode,transactionType]);
	},

	getICCTag:function(success,fail,encryptType,cardType,tagCount,tagArrStr){
		exec(success,fail,"dspread_pos_plugin","getICCTag",[encryptType,cardType,tagCount,tagArrStr]);
	},

	pollOnMifareCard:function(success,fail,timeout){
		exec(success,fail,"dspread_pos_plugin","pollOnMifareCard",[timeout]);
	},

	finishMifareCard:function(success,fail,timeout){
		exec(success,fail,"dspread_pos_plugin","finishMifareCard",[timeout]);
	},

	lcdShowCustomDisplay:function(success,fail,lcdModeAlign,lcdFont,timeout){
		exec(success,fail,"dspread_pos_plugin","lcdShowCustomDisplay",[lcdModeAlign,lcdFont,timeout]);
	},

	customInputDisplay:function(success,fail,operationType, displayType, maxLen, DisplayStr,initiator,timeout){
		exec(success,fail,"dspread_pos_plugin","customInputDisplay",[operationType, displayType, maxLen, DisplayStr,initiator,timeout]);
	},

	sendPosition:function(success,fail,position){
    	exec(success,fail,"dspread_pos_plugin","sendPosition",[position]);
	}

	};
	module.exports =posPlug;


