function pollOnMifareCard(){
    cordova.plugins.dspread_pos_plugin.pollOnMifareCard(function(message){
        console.log("pollOnMifareCard->success: " + message);
        this.posresult(message);
        },function(message){
            console.log("pollOnMifareCard->fail: " + message);
            this.posresult(message);
//        },timeout);
        },10);
}

function authenticateMifareCard(){
     cordova.plugins.dspread_pos_plugin.authenticateMifareCard(function(message){
            console.log("authenticateMifareCard->success: " + message);
            this.posresult(message);
            },function(message){
                console.log("authenticateMifareCard->fail: " + message);
                this.posresult(message);
//            },mifareCardType,keyclass,blockaddr,keyValue,timeout);
            },"CLASSIC","Key A","0A","ffffffffffff",10);
}
function readMifareCard(){
     cordova.plugins.dspread_pos_plugin.readMifareCard(function(message){
                console.log("readMifareCard->success: " + message);
                this.posresult(message);
                },function(message){
                    console.log("readMifareCard->fail: " + message);
                    this.posresult(message);
                },"ULTRALIGHT","0A",10);
}

function writeMifareCard(){
     cordova.plugins.dspread_pos_plugin.writeMifareCard(function(message){
                console.log("writeMifareCard->success: " + message);
                this.posresult(message);
                },function(message){
                    console.log("writeMifareCard->fail: " + message);
                    this.posresult(message);
                },"ULTRALIGHT","0A","1122",10);
}

function operateMifareCardData(){
                    console.log("operateMifareCardData");
     cordova.plugins.dspread_pos_plugin.operateMifareCardData(function(message){
                console.log("operateMifareCardData->success: " + message);
                this.posresult(message);
                },function(message){
                    console.log("operateMifareCardData->fail: " + message);
                    this.posresult(message);
                },"ADD","0A","01",10);
}

function fastReadMifareCardData(){
     cordova.plugins.dspread_pos_plugin.fastReadMifareCardData(function(message){
                console.log("fastReadMifareCardData->success: " + message);
                this.posresult(message);
                },function(message){
                    console.log("fastReadMifareCardData->fail: " + message);
                    this.posresult(message);
                },"0A","0E",10);
}

function finishMifareCard(){
     cordova.plugins.dspread_pos_plugin.finishMifareCard(function(message){
                console.log("finishMifareCard->success: " + message);
                this.posresult(message);
                },function(message){
                    console.log("finishMifareCard->fail: " + message);
                    this.posresult(message);
                },10);
}

function powerOnNFC(){
    cordova.plugins.dspread_pos_plugin.powerOnNFC(function(message){
                console.log("powerOnNFC->success: " + message);
                this.posresult(message);
                },function(message){
                    console.log("powerOnNFC->fail: " + message);
                    this.posresult(message);
                },false,10);
}

function sendApduByNFC(){
    cordova.plugins.dspread_pos_plugin.sendApduByNFC(function(message){
                console.log("sendApduByNFC->success: " + message);
                this.posresult(message);
                },function(message){
                    console.log("sendApduByNFC->fail: " + message);
                    this.posresult(message);
                },"0123",10);
}

function powerOffNFC(){
    cordova.plugins.dspread_pos_plugin.powerOffNFC(function(message){
                console.log("powerOffNFC->success: " + message);
                this.posresult(message);
                },function(message){
                    console.log("powerOffNFC->fail: " + message);
                    this.posresult(message);
                },10);
}