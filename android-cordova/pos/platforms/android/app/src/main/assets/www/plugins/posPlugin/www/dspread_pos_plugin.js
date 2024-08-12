cordova.define("posPlugin.dspread_pos_plugin", function(require, exports, module) {
var exec = require('cordova/exec');

var posPlug = {

	pluginListener:function(success,fail){
	    exec(success,fail,"dspread_pos_plugin","pluginListener",[]);
	},

	scanQPos2Mode:function(success,fail){
	    exec(success,fail,"dspread_pos_plugin","scanQPos2Mode",[]);
	},

	openUart:function(success,fail){
	    exec(success,fail,"dspread_pos_plugin","openUart",[]);
	},

	connectBluetoothDevice:function(success,fail,isConnect,bluetoothAddress){
		exec(success,fail,"dspread_pos_plugin","connectBluetoothDevice",[isConnect,bluetoothAddress]);
	},
	
	setCardTradeMode:function(success,fail,cardTradeMode){
    	exec(success,fail,"dspread_pos_plugin","setCardTradeMode",[cardTradeMode]);
    },

	doTrade:function(success,fail,timeout){
		exec(success,fail,"dspread_pos_plugin","doTrade",[timeout]);
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
	
	updateEmvAPPByTlv:function(success,fail,aid){
        exec(success,fail,"dspread_pos_plugin","updateEmvAPPByTlv",[aid]);
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

	updatePosFirmware:function(success,fail,fileName){
		exec(success,fail,"dspread_pos_plugin","updatePosFirmware",[fileName]);
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

    authenticateMifareCard:function(success,fail,mifareCardType,keyclass,blockaddr,keyValue,timeout){
        exec(success,fail,"dspread_pos_plugin","authenticateMifareCard",[mifareCardType,keyclass,blockaddr,keyValue,timeout]);
    },

    readMifareCard:function(success,fail,mifareCardType,blockaddr,timeout){
        exec(success,fail,"dspread_pos_plugin","readMifareCard",[mifareCardType,blockaddr,timeout]);
    },

    writeMifareCard:function(success,fail,mifareCardType,blockaddr,cardData,timeout){
        exec(success,fail,"dspread_pos_plugin","writeMifareCard",[mifareCardType,blockaddr,cardData,timeout]);
    },

    operateMifareCardData:function(success,fail,mifareOperatieType,blockaddr,cardData,timeout){
         exec(success,fail,"dspread_pos_plugin","operateMifareCardData",[mifareOperatieType,blockaddr,cardData,timeout]);
    },

    fastReadMifareCardData:function(success,fail,startAddr,endAddr,timeout){
         exec(success,fail,"dspread_pos_plugin","fastReadMifareCardData",[startAddr,endAddr,timeout]);
    },

	finishMifareCard:function(success,fail,timeout){
		exec(success,fail,"dspread_pos_plugin","finishMifareCard",[timeout]);
	},

	powerOnNFC:function(success,fail,isEncrypt,timeout){
	    exec(success,fail,"dspread_pos_plugin","powerOnNFC",[isEncrypt,timeout]);
	},

	sendApduByNFC:function(success,fail,apduStr,timeout){
	    exec(success,fail,"dspread_pos_plugin","sendApduByNFC",[apduStr,timeout]);
	},

	powerOffNFC:function(success,fail,timeout){
	    exec(success,fail,"dspread_pos_plugin","powerOffNFC",[timeout]);
	},

	lcdShowCustomDisplay:function(success,fail,lcdModeAlign,lcdFont,timeout){
		exec(success,fail,"dspread_pos_plugin","lcdShowCustomDisplay",[lcdModeAlign,lcdFont,timeout]);
	},

	customInputDisplay:function(success,fail,operationType, displayType, maxLen, DisplayStr,initiator,timeout){
		exec(success,fail,"dspread_pos_plugin","customInputDisplay",[operationType, displayType, maxLen, DisplayStr,initiator,timeout]);
	},

	sendPosition:function(success,fail,position){
    	exec(success,fail,"dspread_pos_plugin","sendPosition",[position]);
	},
	
	resetQPosStatus:function(success,fail){
	    exec(success,fail,"dspread_pos_plugin","resetQPosStatus",[]);
	},

    doSetBuzzerOperation:function(success,fail,times){
        exec(success,fail,"dspread_pos_plugin","doSetBuzzerOperation",[times]);
    },
	
    getUpdateCheckValue:function(success,fail){
        exec(success,fail,"dspread_pos_plugin","getUpdateCheckValue",[]);
    },

    sendPin:function(success,fail,pinStr){
        exec(success,fail,"dspread_pos_plugin","sendPin",[pinStr]);
    },

    sendOnlineProcessResult:function(success,fail,onlineResult){
        exec(success,fail,"dspread_pos_plugin","sendOnlineProcessResult",[onlineResult]);
    },

    connectBluetoothNoScan:function(success,fail,bluetoothName){
        exec(success,fail,"dspread_pos_plugin","connectBluetoothNoScan",[bluetoothName]);
    },

    /*
     * @param encryptType default:0 ; 0 means don't xor with PAN, 1 means xor with PAN; 2 means encrypt with timestamp
     * @param keyIndex    default:0
     * @param maxLen      max length of pin
     * @param typeFace    display the font
     * @param cardNo      the cardPan
     * @param data        attached data
     * @param timeout     maximum time to get which is in seconds
     */

    getPin:function(success,fail, encryptType, keyIndex, maxLen, typeFace, cardNo, data, timeout){
        exec(success,fail,"dspread_pos_plugin","getPin",[encryptType, keyIndex, maxLen, typeFace, cardNo, data, timeout]);
	},

	};
	module.exports =posPlug;


});
