//
//  dspread_pos_plugin.m
//  qpos-ios-demo
//
//  Created by dspread-mac on 2018/2/1.
//  Copyright © 2018年 Robin. All rights reserved.
//

#import "dspread_pos_plugin.h"
#import "QPOSUtil.h"
#import <AudioToolbox/AudioToolbox.h>
typedef void(^imgBlock)(NSString * data);
@interface dspread_pos_plugin()

@property(nonatomic,strong) imgBlock MyBlock;
@property (nonatomic,copy)NSString *terminalTime;
@property (nonatomic,copy)NSString *currencyCode;
@property (nonatomic, strong)CDVInvokedUrlCommand *urlCommand;
@property(nonatomic,strong)QPOSService *mPos;
@property(nonatomic,strong)BTDeviceFinder *bt;
@end


@implementation dspread_pos_plugin
{
    NSMutableArray *allBluetooth;
    NSString *btAddress;
    TransactionType mTransType;
    NSString *amount;
    UIAlertView *mAlertView;
    UIActionSheet *mActionSheet;
    PosType     mPosType;
    dispatch_queue_t self_queue;
    NSString *msgStr;
    NSTimer* appearTimer;

}
@synthesize bluetoothAddress;
@synthesize amount;
@synthesize cashbackAmount;
-(id)init{
    self = [super init];
    if(self != nil){
        [self initPos];
    }
    return self;
}

-(void)scanQPos2Mode:(CDVInvokedUrlCommand *)command{
  [self executeMyMethodWithCommand:command withActionName:@"scanQPos2Mode"];
}

-(void)connectBluetoothDevice:(CDVInvokedUrlCommand *)command{
     [self executeMyMethodWithCommand:command withActionName:@"connectBluetoothDevice"];
}

-(void)disconnectBT:(CDVInvokedUrlCommand *)command{
     [self executeMyMethodWithCommand:command withActionName:@"disconnectBT"];
}

-(void)doTrade:(CDVInvokedUrlCommand *)command{
    [self executeMyMethodWithCommand:command withActionName:@"doTrade"];
}

-(void)stopScanQPos2Mode:(CDVInvokedUrlCommand *)command{
   [self executeMyMethodWithCommand:command withActionName:@"stopScanQPos2Mode"];
}

-(void)getQposInfo:(CDVInvokedUrlCommand *)command{
    [self executeMyMethodWithCommand:command withActionName:@"getQposInfo"];
}

-(void)getQposId:(CDVInvokedUrlCommand *)command{
   [self executeMyMethodWithCommand:command withActionName:@"getQposId"];
}

-(void)updateIPEK:(CDVInvokedUrlCommand *)command{
    [self executeMyMethodWithCommand:command withActionName:@"updateIPEK"];
}
-(void)updateEmvCAPK:(CDVInvokedUrlCommand *)command{
    [self executeMyMethodWithCommand:command withActionName:@"updateEmvCAPK"];
}
-(void)updateEmvApp:(CDVInvokedUrlCommand *)command{
    [self executeMyMethodWithCommand:command withActionName:@"updateEmvApp"];
}
-(void)setMasterKey:(CDVInvokedUrlCommand*)command{
    [self executeMyMethodWithCommand:command withActionName:@"setMasterKey"];
}
    
-(void)updatePosFirmware:(CDVInvokedUrlCommand*)command{
     [self executeMyMethodWithCommand:command withActionName:@"updatePosFirmware"];
}

-(void)initPos{
    if (_mPos == nil) {
        _mPos = [QPOSService sharedInstance];
    }
    [_mPos setDelegate:self];
    [_mPos setQueue:nil];
    [_mPos setPosType:PosType_BLUETOOTH_2mode];
    if (_bt== nil) {
        _bt = [[BTDeviceFinder alloc]init];
    }
    allBluetooth = [[NSMutableArray alloc]init];
}
    
-(void) onQposIdResult: (NSDictionary*)posId{
    NSString *aStr = [@"posId:" stringByAppendingString:posId[@"posId"]];
    
    NSString *temp = [@"psamId:" stringByAppendingString:posId[@"psamId"]];
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:temp];
    
    temp = [@"merchantId:" stringByAppendingString:posId[@"merchantId"]];
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:temp];
    
    temp = [@"vendorCode:" stringByAppendingString:posId[@"vendorCode"]];
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:temp];
    
    temp = [@"deviceNumber:" stringByAppendingString:posId[@"deviceNumber"]];
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:temp];
    
    temp = [@"psamNo:" stringByAppendingString:posId[@"psamNo"]];
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:temp];
    
    NSLog(@"posid == %@",aStr);

}

-(void) onQposInfoResult: (NSDictionary*)posInfoData{
    NSLog(@"onQposInfoResult: %@",posInfoData);
    NSString *aStr = @"SUB :";
    aStr = [aStr stringByAppendingString:posInfoData[@"SUB"]];
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:posInfoData[@"bootloaderVersion"]];
    
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:@"Firmware Version: "];
    aStr = [aStr stringByAppendingString:posInfoData[@"firmwareVersion"]];
    
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:@"Hardware Version: "];
    aStr = [aStr stringByAppendingString:posInfoData[@"hardwareVersion"]];
    
    
    NSString *batteryPercentage = posInfoData[@"batteryPercentage"];
    if (batteryPercentage==nil || [@"" isEqualToString:batteryPercentage]) {
        aStr = [aStr stringByAppendingString:@"\n"];
        aStr = [aStr stringByAppendingString:@"Battery Level: "];
        aStr = [aStr stringByAppendingString:posInfoData[@"batteryLevel"]];
        
    }else{
        aStr = [aStr stringByAppendingString:@"\n"];
        aStr = [aStr stringByAppendingString:@"Battery Percentage: "];
        aStr = [aStr stringByAppendingString:posInfoData[@"batteryPercentage"]];
    }
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:@"Charge: "];
    aStr = [aStr stringByAppendingString:posInfoData[@"isCharging"]];
    
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:@"USB: "];
    aStr = [aStr stringByAppendingString:posInfoData[@"isUsbConnected"]];
    
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:@"Track 1 Supported: "];
    aStr = [aStr stringByAppendingString:posInfoData[@"isSupportedTrack1"]];
    
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:@"Track 2 Supported: "];
    aStr = [aStr stringByAppendingString:posInfoData[@"isSupportedTrack2"]];
    
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:@"Track 3 Supported: "];
    aStr = [aStr stringByAppendingString:posInfoData[@"isSupportedTrack3"]];
    
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:@"updateWorkKeyFlag: "];
    aStr = [aStr stringByAppendingString:posInfoData[@"updateWorkKeyFlag"]];
    
    NSString *posinfo = aStr;
}

-(void)scanBluetooth{
    [self initPos];
    NSInteger delay = 30;
    NSLog(@"蓝牙状态:%ld",(long)[self.bt getCBCentralManagerState]);
    [self.bt setBluetoothDelegate2Mode:self];
    if ([self.bt getCBCentralManagerState] == CBCentralManagerStateUnknown) {
            while ([self.bt getCBCentralManagerState]!= CBCentralManagerStatePoweredOn) {
                NSLog(@"Bluetooth state is not power on");
                [self sleepMs:10];
                if(delay++==10){
                    return;
                }
            }
        }
        [self.bt scanQPos2Mode:delay];
}

-(void) sleepMs: (NSInteger)msec {
    NSTimeInterval sec = (msec / 1000.0f);
    [NSThread sleepForTimeInterval:sec];
}

-(void)onBluetoothName2Mode:(NSString *)bluetoothName{
     if (bluetoothName != nil && ![bluetoothName isEqualToString:@""]) {
           NSLog(@"蓝牙名: %@",bluetoothName);
           NSString *jsStr = [NSString stringWithFormat:@"addrow('%@')",bluetoothName];
           [self.commandDelegate evalJs:jsStr];
       }
}
    
-(NSString* )getEMVStr:(NSString *)emvStr{
    NSInteger emvLen = 0;
    if (emvStr != NULL &&![emvStr  isEqual: @""]) {
        if ([emvStr length]%2 != 0) {
            emvStr = [@"0" stringByAppendingString:emvStr];
        }
        emvLen = [emvStr length]/2;
    }else{
        NSLog(@"init emv app config str could not be empty");
        return nil;
    }
    NSData *emvLenData = [QPOSUtil IntToHex:emvLen];
    NSString *totalStr = [[[QPOSUtil byteArray2Hex:emvLenData] substringFromIndex:2] stringByAppendingString:emvStr];
    return totalStr;
}

-(NSString *)getHexFromStr:(NSString *)str{
    NSData *data = [str dataUsingEncoding:NSUTF8StringEncoding];
    NSString *hex = [QPOSUtil byteArray2Hex:data];
    return hex ;
}

- (NSString *)getHexFromIntStr:(NSString *)tmpidStr
{
    NSInteger tmpid = [tmpidStr intValue];
    NSString *nLetterValue;
    NSString *str =@"";
    int ttmpig;
    for (int i = 0; i<9; i++) {
        ttmpig=tmpid%16;
        tmpid=tmpid/16;
        switch (ttmpig)
        {
            case 10:
                nLetterValue =@"A";break;
            case 11:
                nLetterValue =@"B";break;
            case 12:
                nLetterValue =@"C";break;
            case 13:
                nLetterValue =@"D";break;
            case 14:
                nLetterValue =@"E";break;
            case 15:
                nLetterValue =@"F";break;
            default:
                nLetterValue = [NSString stringWithFormat:@"%u",ttmpig];
        }
        str = [nLetterValue stringByAppendingString:str];
        if (tmpid == 0) {
            break;
        }
    }
    //不够一个字节凑0
    if(str.length == 1){
        return [NSString stringWithFormat:@"0%@",str];
    }else{
        if ([str length]<8) {
            if ([str length] == (8-1)) {
                str = [@"0" stringByAppendingString:str];
            }else if ([str length] == (8-2)){
                str = [@"00" stringByAppendingString:str];
            }else if  ([str length] == (8-3)){
                str = [@"000" stringByAppendingString:str];
            }
            else if ([str length] == (8-4)) {
                str = [@"0000" stringByAppendingString:str];
            } else if([str length] == (8-5)){
                str = [@"00000" stringByAppendingString:str];
            }else if([str length] == (8-6)){
                str = [@"000000" stringByAppendingString:str];
            }
        }
        return str;
    }
}
    
- (NSData*)readLine:(NSString*)name
    {
        NSString* file = [[NSBundle mainBundle]pathForResource:name ofType:@".asc"];
        NSFileManager* Manager = [NSFileManager defaultManager];
        NSData* data = [[NSData alloc] init];
        data = [Manager contentsAtPath:file];
        return data;
}

-(void)doTradeSelf{
    NSDateFormatter *dateFormatter = [NSDateFormatter new];
    [dateFormatter setDateFormat:@"yyyyMMddHHmmss"];
    self.terminalTime = [dateFormatter stringFromDate:[NSDate date]];
    mTransType = TransactionType_GOODS;
    _currencyCode = @"156";
    [self.mPos setDoTradeMode:DoTradeMode_CHECK_CARD_NO_IPNUT_PIN];
    [self.mPos setCardTradeMode:CardTradeMode_SWIPE_TAP_INSERT_CARD_NOTUP];
    [self.mPos doTrade:30];
}

-(void) onRequestSetAmount{
    amount = @"100";
    [self.mPos setAmount:amount aAmountDescribe:@"1000" currency:@"156" transactionType:TransactionType_GOODS];
}
-(void) onRequestWaitingUser{
    NSString *displayStr  =@"Please insert/swipe/tap card now.";
}
-(void) onDHError: (DHError)errorState{
    NSString *msg = @"";
    
    if(errorState ==DHError_TIMEOUT) {
        msg = @"Pos no response";
    } else if(errorState == DHError_DEVICE_RESET) {
        msg = @"Pos reset";
    } else if(errorState == DHError_UNKNOWN) {
        msg = @"Unknown error";
    } else if(errorState == DHError_DEVICE_BUSY) {
        msg = @"Pos Busy";
    } else if(errorState == DHError_INPUT_OUT_OF_RANGE) {
        msg = @"Input out of range.";
    } else if(errorState == DHError_INPUT_INVALID_FORMAT) {
        msg = @"Input invalid format.";
    } else if(errorState == DHError_INPUT_ZERO_VALUES) {
        msg = @"Input are zero values.";
    } else if(errorState == DHError_INPUT_INVALID) {
        msg = @"Input invalid.";
    } else if(errorState == DHError_CASHBACK_NOT_SUPPORTED) {
        msg = @"Cashback not supported.";
    } else if(errorState == DHError_CRC_ERROR) {
        msg = @"CRC Error.";
    } else if(errorState == DHError_COMM_ERROR) {
        msg = @"Communication Error.";
    }else if(errorState == DHError_MAC_ERROR){
        msg = @"MAC Error.";
    }else if(errorState == DHError_CMD_TIMEOUT){
        msg = @"CMD Timeout.";
    }else if(errorState == DHError_AMOUNT_OUT_OF_LIMIT){
        msg = @"Amount out of limit.";
    }
    
    NSString *error = msg;
    NSLog(@"onError = %@",msg);
}

//开始执行start 按钮后返回的结果状态
-(void) onDoTradeResult: (DoTradeResult)result DecodeData:(NSDictionary*)decodeData{
    NSLog(@"onDoTradeResult?>> result %ld",(long)result);
    if (result == DoTradeResult_NONE) {
        NSString *display = @"No card detected. Please insert or swipe card again and press check card.";
        [self.mPos doTrade:30];
        NSLog(@"%@",display);
    }else if (result==DoTradeResult_ICC) {
        NSString *display = @"ICC Card Inserted";
        NSLog(@"%@",display);
        [self.mPos doEmvApp:EmvOption_START];
    }else if(result==DoTradeResult_NOT_ICC){
        NSString *display = @"Card Inserted (Not ICC)";
        NSLog(@"%@",display);
    }else if(result==DoTradeResult_MCR){
        //        [pos getCardNo]
        ;        NSLog(@"decodeData: %@",decodeData);
        NSString *formatID = [NSString stringWithFormat:@"Format ID: %@\n",decodeData[@"formatID"]] ;
        NSString *maskedPAN = [NSString stringWithFormat:@"Masked PAN: %@\n",decodeData[@"maskedPAN"]];
        NSString *expiryDate = [NSString stringWithFormat:@"Expiry Date: %@\n",decodeData[@"expiryDate"]];
        NSString *cardHolderName = [NSString stringWithFormat:@"Cardholder Name: %@\n",decodeData[@"cardholderName"]];
        //NSString *ksn = [NSString stringWithFormat:@"KSN: %@\n",decodeData[@"ksn"]];
        NSString *serviceCode = [NSString stringWithFormat:@"Service Code: %@\n",decodeData[@"serviceCode"]];
        //NSString *track1Length = [NSString stringWithFormat:@"Track 1 Length: %@\n",decodeData[@"track1Length"]];
        //NSString *track2Length = [NSString stringWithFormat:@"Track 2 Length: %@\n",decodeData[@"track2Length"]];
        //NSString *track3Length = [NSString stringWithFormat:@"Track 3 Length: %@\n",decodeData[@"track3Length"]];
        //NSString *encTracks = [NSString stringWithFormat:@"Encrypted Tracks: %@\n",decodeData[@"encTracks"]];
        NSString *encTrack1 = [NSString stringWithFormat:@"Encrypted Track 1: %@\n",decodeData[@"encTrack1"]];
        NSString *encTrack2 = [NSString stringWithFormat:@"Encrypted Track 2: %@\n",decodeData[@"encTrack2"]];
        NSString *encTrack3 = [NSString stringWithFormat:@"Encrypted Track 3: %@\n",decodeData[@"encTrack3"]];
        //NSString *partialTrack = [NSString stringWithFormat:@"Partial Track: %@",decodeData[@"partialTrack"]];
        NSString *pinKsn = [NSString stringWithFormat:@"PIN KSN: %@\n",decodeData[@"pinKsn"]];
        NSString *trackksn = [NSString stringWithFormat:@"Track KSN: %@\n",decodeData[@"trackksn"]];
        NSString *pinBlock = [NSString stringWithFormat:@"pinBlock: %@\n",decodeData[@"pinblock"]];
        NSString *encPAN = [NSString stringWithFormat:@"encPAN: %@\n",decodeData[@"encPAN"]];
        
        NSString *msg = [NSString stringWithFormat:@"Card Swiped:\n"];
        msg = [msg stringByAppendingString:formatID];
        msg = [msg stringByAppendingString:maskedPAN];
        msg = [msg stringByAppendingString:expiryDate];
        msg = [msg stringByAppendingString:cardHolderName];
        //msg = [msg stringByAppendingString:ksn];
        msg = [msg stringByAppendingString:pinKsn];
        msg = [msg stringByAppendingString:trackksn];
        msg = [msg stringByAppendingString:serviceCode];
        
        msg = [msg stringByAppendingString:encTrack1];
        msg = [msg stringByAppendingString:encTrack2];
        msg = [msg stringByAppendingString:encTrack3];
        msg = [msg stringByAppendingString:pinBlock];
        msg = [msg stringByAppendingString:encPAN];
        NSString *display = msg;
         amount = @"";
        NSString *displayAmount = @"";
        
    }else if(result==DoTradeResult_NFC_OFFLINE || result == DoTradeResult_NFC_ONLINE){
        NSLog(@"decodeData: %@",decodeData);
        NSString *formatID = [NSString stringWithFormat:@"Format ID: %@\n",decodeData[@"formatID"]] ;
        NSString *maskedPAN = [NSString stringWithFormat:@"Masked PAN: %@\n",decodeData[@"maskedPAN"]];
        NSString *expiryDate = [NSString stringWithFormat:@"Expiry Date: %@\n",decodeData[@"expiryDate"]];
        NSString *cardHolderName = [NSString stringWithFormat:@"Cardholder Name: %@\n",decodeData[@"cardholderName"]];
        //NSString *ksn = [NSString stringWithFormat:@"KSN: %@\n",decodeData[@"ksn"]];
        NSString *serviceCode = [NSString stringWithFormat:@"Service Code: %@\n",decodeData[@"serviceCode"]];
        //NSString *track1Length = [NSString stringWithFormat:@"Track 1 Length: %@\n",decodeData[@"track1Length"]];
        //NSString *track2Length = [NSString stringWithFormat:@"Track 2 Length: %@\n",decodeData[@"track2Length"]];
        //NSString *track3Length = [NSString stringWithFormat:@"Track 3 Length: %@\n",decodeData[@"track3Length"]];
        //NSString *encTracks = [NSString stringWithFormat:@"Encrypted Tracks: %@\n",decodeData[@"encTracks"]];
        NSString *encTrack1 = [NSString stringWithFormat:@"Encrypted Track 1: %@\n",decodeData[@"encTrack1"]];
        NSString *encTrack2 = [NSString stringWithFormat:@"Encrypted Track 2: %@\n",decodeData[@"encTrack2"]];
        NSString *encTrack3 = [NSString stringWithFormat:@"Encrypted Track 3: %@\n",decodeData[@"encTrack3"]];
        //NSString *partialTrack = [NSString stringWithFormat:@"Partial Track: %@",decodeData[@"partialTrack"]];
        NSString *pinKsn = [NSString stringWithFormat:@"PIN KSN: %@\n",decodeData[@"pinKsn"]];
        NSString *trackksn = [NSString stringWithFormat:@"Track KSN: %@\n",decodeData[@"trackksn"]];
        NSString *pinBlock = [NSString stringWithFormat:@"pinBlock: %@\n",decodeData[@"pinblock"]];
        NSString *encPAN = [NSString stringWithFormat:@"encPAN: %@\n",decodeData[@"encPAN"]];
        
        NSString *msg = [NSString stringWithFormat:@"Tap Card:\n"];
        msg = [msg stringByAppendingString:formatID];
        msg = [msg stringByAppendingString:maskedPAN];
        msg = [msg stringByAppendingString:expiryDate];
        msg = [msg stringByAppendingString:cardHolderName];
        //msg = [msg stringByAppendingString:ksn];
        msg = [msg stringByAppendingString:pinKsn];
        msg = [msg stringByAppendingString:trackksn];
        msg = [msg stringByAppendingString:serviceCode];
        
        msg = [msg stringByAppendingString:encTrack1];
        msg = [msg stringByAppendingString:encTrack2];
        msg = [msg stringByAppendingString:encTrack3];
        msg = [msg stringByAppendingString:pinBlock];
        msg = [msg stringByAppendingString:encPAN];
        
        dispatch_async(dispatch_get_main_queue(),  ^{
            NSDictionary *mDic = [self.mPos getNFCBatchData];
            NSString *tlv;
            if(mDic !=nil){
                tlv= [NSString stringWithFormat:@"NFCBatchData: %@\n",mDic[@"tlv"]];
            }else{
                tlv = @"";
            }
            NSString *displayStr = [msg stringByAppendingString:tlv];
            NSLog(@"%@",displayStr);
            amount = @"";
        });
        
    }else if(result==DoTradeResult_NFC_DECLINED){
        NSString *displayStr = @"Tap Card Declined";
    }else if (result==DoTradeResult_NO_RESPONSE){
        NSString *displayStr = @"Check card no response";
    }else if(result==DoTradeResult_BAD_SWIPE){
        NSString *displayStr = @"Bad Swipe. \nPlease swipe again and press check card.";
        
        //        [pos doTrade:30];
    }else if(result==DoTradeResult_NO_UPDATE_WORK_KEY){
        NSString *displayStr = @"device not update work key";
    }
    
}

-(void) onRequestSelectEmvApp: (NSArray*)appList{
    //NSString *resultStr = @"";
    
    mActionSheet = [[UIActionSheet new] initWithTitle:@"Please select app" delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil, nil];
    
    for (int i=0 ; i<[appList count] ; i++){
        NSString *emvApp = [appList objectAtIndex:i];
        [mActionSheet addButtonWithTitle:emvApp];
        
        //resultStr = [NSString stringWithFormat:@"%@[%@] ", resultStr,emvApp];
    }
    [mActionSheet addButtonWithTitle:@"Cancel"];
    [mActionSheet setCancelButtonIndex:[appList count]];
    [mActionSheet showInView:[UIApplication sharedApplication].keyWindow];
    
    //NSLog(@"resultStr: %@",resultStr);
    
}
-(void) onRequestFinalConfirm{
    
    NSLog(@"onRequestFinalConfirm-------amount = %@",amount);
    NSString *msg = [NSString stringWithFormat:@"Amount: $%@",amount];
    mAlertView = [[UIAlertView new]
                  initWithTitle:@"Confirm amount"
                  message:msg
                  delegate:self
                  cancelButtonTitle:@"Confirm"
                  otherButtonTitles:@"Cancel",
                  nil ];
    [mAlertView show];
    msgStr = @"Confirm amount";
}
-(void) onRequestTime{
//        NSDateFormatter *dateFormatter = [NSDateFormatter new];
//        [dateFormatter setDateFormat:@"yyyyMMddHHmmss"];
//        NSString *terminalTime = [dateFormatter stringFromDate:[NSDate date]];
//
    [self.mPos sendTime:self.terminalTime];
    
}
-(void) onRequestIsServerConnected{
    NSString *msg = @"Replied connected.";
    msgStr = @"Online process requested.";
    
    [self conductEventByMsg:msgStr];
    
    //    mAlertView = [[UIAlertView new]
    //                  initWithTitle:@"Online process requested."
    //                  message:msg
    //                  delegate:self
    //                  cancelButtonTitle:@"Confirm"
    //                  otherButtonTitles:nil,
    //                  nil ];
    
    //    [mAlertView show];
    
    
}

-(void)conductEventByMsg:(NSString *)msg{
    if ([msg isEqualToString:@"Online process requested."]){
        [self.mPos isServerConnected:YES];
        
    }else if ([msg isEqualToString:@"Request data to server."]){
        
        [self.mPos sendOnlineProcessResult:@"8A023030"];
        
    }else if ([msg isEqualToString:@"Transaction Result"]){
        
    }
    
    
}
-(void) onRequestOnlineProcess: (NSString*) tlv{
    NSLog(@"tlv == %@",tlv);
    NSLog(@"onRequestOnlineProcess = %@",[[QPOSService sharedInstance] anlysEmvIccData:tlv]);

    NSString *msg = @"Replied success.";
    NSString *displayStr = tlv;
    msgStr = @"Request data to server.";
    [self conductEventByMsg:msgStr];
}

-(void) onRequestTransactionResult: (TransactionResult)transactionResult{
    NSString *messageTextView = @"";
    if (transactionResult==TransactionResult_APPROVED) {
        NSString *message = [NSString stringWithFormat:@"Approved\nAmount: $%@\n",amount];
        if([cashbackAmount isEqualToString:@""]) {
            message = [message stringByAppendingString:@"Cashback: $"];
            message = [message stringByAppendingString:cashbackAmount];
        }
        messageTextView = message;
//        self.textViewLog.backgroundColor = [UIColor greenColor];
    }else if(transactionResult == TransactionResult_TERMINATED) {
        [self clearDisplay];
        messageTextView = @"Terminated";
    } else if(transactionResult == TransactionResult_DECLINED) {
        messageTextView = @"Declined";
    } else if(transactionResult == TransactionResult_CANCEL) {
        [self clearDisplay];
        messageTextView = @"Cancel";
    } else if(transactionResult == TransactionResult_CAPK_FAIL) {
        [self clearDisplay];
        messageTextView = @"Fail (CAPK fail)";
    } else if(transactionResult == TransactionResult_NOT_ICC) {
        [self clearDisplay];
        messageTextView = @"Fail (Not ICC card)";
    } else if(transactionResult == TransactionResult_SELECT_APP_FAIL) {
        [self clearDisplay];
        messageTextView = @"Fail (App fail)";
    } else if(transactionResult == TransactionResult_DEVICE_ERROR) {
        [self clearDisplay];
        messageTextView = @"Pos Error";
    } else if(transactionResult == TransactionResult_CARD_NOT_SUPPORTED) {
        [self clearDisplay];
        messageTextView = @"Card not support";
    } else if(transactionResult == TransactionResult_MISSING_MANDATORY_DATA) {
        [self clearDisplay];
        messageTextView = @"Missing mandatory data";
    } else if(transactionResult == TransactionResult_CARD_BLOCKED_OR_NO_EMV_APPS) {
        [self clearDisplay];
        messageTextView = @"Card blocked or no EMV apps";
    } else if(transactionResult == TransactionResult_INVALID_ICC_DATA) {
        [self clearDisplay];
        messageTextView = @"Invalid ICC data";
    }else if(transactionResult == TransactionResult_NFC_TERMINATED) {
        [self clearDisplay];
        messageTextView = @"NFC Terminated";
    }
    NSString *displayStr = messageTextView;
    mAlertView = [[UIAlertView new]
                  initWithTitle:@"Transaction Result"
                  message:messageTextView
                  delegate:self
                  cancelButtonTitle:@"Confirm"
                  otherButtonTitles:nil,
                  nil ];
    [mAlertView show];
    self.amount = @"";
    self.cashbackAmount = @"";
    amount = @"";
}

-(void) onRequestTransactionLog: (NSString*)tlv{
    NSLog(@"onTransactionLog %@",tlv);
}

-(void) onRequestBatchData: (NSString*)tlv{
    NSLog(@"onBatchData %@",tlv);
    tlv = [@"batch data:\n" stringByAppendingString:tlv];
    NSString *displayStr = tlv;
}

-(void) onReturnReversalData: (NSString*)tlv{
    NSLog(@"onReversalData %@",tlv);
    tlv = [@"reversal data:\n" stringByAppendingString:tlv];
    NSString *displayStr = tlv;
}

//pos 连接成功的回调
-(void) onRequestQposConnected{
    NSLog(@"onRequestQposConnected");
    if ([self.bluetoothAddress  isEqual: @"audioType"]) {
        NSString *displayStr = @"AudioType connected.";
       
    }else{
        NSString *displayStr = @"Bluetooth connected.";
    }
    [self.bt stopQPos2Mode];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"connected"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.urlCommand.callbackId];
    
}
-(void) onRequestQposDisconnected{
    NSLog(@"onRequestQposDisconnected");
    NSString *displayStr = @"pos disconnected.";
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"disconnect"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.urlCommand.callbackId];
}

-(void) onRequestNoQposDetected{
    NSLog(@"onRequestNoQposDetected");
    NSString *displayStr = @"No pos detected.";
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"detected"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.urlCommand.callbackId];
}

-(void) onRequestDisplay: (Display)displayMsg{
    NSString *msg = @"";
    if (displayMsg==Display_CLEAR_DISPLAY_MSG) {
        msg = @"";
    }else if(displayMsg==Display_PLEASE_WAIT){
        msg = @"Please wait...";
    }else if(displayMsg==Display_REMOVE_CARD){
        msg = @"Please remove card";
    }else if (displayMsg==Display_TRY_ANOTHER_INTERFACE){
        msg = @"Please try another interface";
    }else if (displayMsg == Display_TRANSACTION_TERMINATED){
        msg = @"Terminated";
    }else if (displayMsg == Display_PIN_OK){
        msg = @"Pin ok";
    }else if (displayMsg == Display_INPUT_PIN_ING){
        msg = @"please input pin on pos";
    }else if (displayMsg == Display_MAG_TO_ICC_TRADE){
        msg = @"please insert chip card on pos";
    }else if (displayMsg == Display_INPUT_OFFLINE_PIN_ONLY){
        msg = @"input offline pin only";
    }else if(displayMsg == Display_CARD_REMOVED){
        msg = @"Card Removed";
    }
    NSString *displayStr = msg;
}
-(void) onReturnGetPinResult:(NSDictionary*)decodeData{
    NSString *aStr = @"pinKsn: ";
    aStr = [aStr stringByAppendingString:decodeData[@"pinKsn"]];
    
    aStr = [aStr stringByAppendingString:@"\n"];
    aStr = [aStr stringByAppendingString:@"pinBlock: "];
    aStr = [aStr stringByAppendingString:decodeData[@"pinBlock"]];
    
    NSString *displayStr = aStr;
}
-(void)clearDisplay{
    
}
-(void) onReturnSetMasterKeyResult: (BOOL)isSuccess{
    if(isSuccess){
         NSLog( @"Success");
    }else{
         NSLog(@"Failed");
    }
    //    NSLog(@"result: %@",resutl);
}
-(void) onRequestUpdateWorkKeyResult:(UpdateInformationResult)updateInformationResult
{
    NSLog(@"onRequestUpdateWorkKeyResult %ld",(long)updateInformationResult);
    if (updateInformationResult==UpdateInformationResult_UPDATE_SUCCESS) {
        
    }else if(updateInformationResult==UpdateInformationResult_UPDATE_FAIL){
         NSLog(@"Failed");
    }else if(updateInformationResult==UpdateInformationResult_UPDATE_PACKET_LEN_ERROR){
         NSLog(@"Packet len error");
    }
    else if(updateInformationResult==UpdateInformationResult_UPDATE_PACKET_VEFIRY_ERROR){
        NSLog(@"Packer vefiry error");
    }
    
}

-(void)executeMyMethodWithCommand:(CDVInvokedUrlCommand*)command withActionName:(NSString *)name{
    self.urlCommand = command;
    
    [self.commandDelegate runInBackground:^{
        
        if (name != nil) {
            if ([name isEqualToString:@"scanQPos2Mode"]) {
                [self scanBluetooth];
            }else if ([name isEqualToString:@"scanQPos2Mode"]){
                
            }else if([name isEqualToString:@"connectBluetoothDevice"]) {
                if (command.arguments.count>0) {
                       //customize argument
                    NSString* address = command.arguments[1];
                    NSLog(@"address: %@",address);
                    [self.mPos connectBT:address];
                }
            }else if([name isEqualToString:@"doTrade"]) {
                [self doTradeSelf];
            }else if([name isEqualToString:@"getDeviceList"]) {
                [self scanBluetooth];
            }else if([name isEqualToString:@"stopScanQPos2Mode"]) {
                [self.bt stopQPos2Mode];
            }else if ([name isEqualToString:@"disconnectBT"]) {
                [self.mPos disconnectBT];
            }else if ([name isEqualToString:@"getQposInfo"]) {
                [self.mPos getQPosInfo];
            }else if ([name isEqualToString:@"getQposId"]) {
                [self.mPos getQPosId];
            }else if ([name isEqualToString:@"updateIPEK"]) {
                [self.mPos doUpdateIPEKOperation:@"00" tracksn:@"FFFF000000BB81200000" trackipek:@"F24B13AC6F579B929FBBFE58BC2A0647" trackipekCheckValue:@"CDF80B70C3BBCDDC" emvksn:@"FFFF000000BB81200000" emvipek:@"F24B13AC6F579B929FBBFE58BC2A0647" emvipekcheckvalue:@"CDF80B70C3BBCDDC" pinksn:@"FFFF000000BB81200000" pinipek:@"F24B13AC6F579B929FBBFE58BC2A0647" pinipekcheckValue:@"CDF80B70C3BBCDDC" block:^(BOOL isSuccess, NSString *stateStr) {
                    if (isSuccess) {
                        NSLog(@"stateStr == %@",stateStr);
                    }
                }];
            }else if([name isEqualToString:@"updateEmvApp"]) {
                NSMutableDictionary * emvAPPDict = [self.mPos getEMVAPPDict];
                
                NSString *AID = @"A0000000044010";
                NSString * o1  =[[emvAPPDict valueForKey:@"Application_Identifier_AID_terminal"] stringByAppendingString:[self getEMVStr:AID]];
                NSString * o2 =[[emvAPPDict valueForKey:@"TAC_Default"] stringByAppendingString:[self getEMVStr:@"FC5080A000"]];
                NSString * o3  =[[emvAPPDict valueForKey:@"TAC_Online"] stringByAppendingString:[self getEMVStr:@"FC5080F800"]];
                NSString * o4  =[[emvAPPDict valueForKey:@"TAC_Denial"] stringByAppendingString:[self getEMVStr:@"0000000000"]];
                NSString * o5 =[[emvAPPDict valueForKey:@"Target_Percentage_to_be_Used_for_Random_Selection"] stringByAppendingString:[self getEMVStr:@"00"]];
                NSString * o6  =[[emvAPPDict valueForKey:@"Maximum_Target_Percentage_to_be_used_for_Biased_Random_Selection"] stringByAppendingString:[self getEMVStr:@"00"]];
                NSString * o7  =[[emvAPPDict valueForKey:@"Threshold_Value_BiasedRandom_Selection"] stringByAppendingString:[self getEMVStr:[self getHexFromIntStr:@"999999"]]];
                NSString * o8  =[[emvAPPDict valueForKey:@"Terminal_Floor_Limit"] stringByAppendingString:[self getEMVStr:@"00000000"]];
                NSString * o9 =[[emvAPPDict valueForKey:@"Application_Version_Number"] stringByAppendingString:[self getEMVStr:@"0002"]];
                NSString * o10 =[[emvAPPDict valueForKey:@"Point_of_Service_POS_EntryMode"] stringByAppendingString:[self getEMVStr:@"05"]];
                NSString * o11  =[[emvAPPDict valueForKey:@"Acquirer_Identifier"] stringByAppendingString:[self getEMVStr:@"000000008080"]];
                NSString * o12 =[[emvAPPDict valueForKey:@"Merchant_Category_Code"] stringByAppendingString:[self getEMVStr:@"1234"]];
                NSString * o13  =[[emvAPPDict valueForKey:@"Merchant_Identifier"] stringByAppendingString:[self getEMVStr:[self getHexFromStr: @"BCTEST 12345678"]]];
                NSString * o14  =[[emvAPPDict valueForKey:@"Merchant_Name_and_Location"] stringByAppendingString:[self getEMVStr:[[self getHexFromStr:@"abcd"] stringByAppendingString:@"0000000000000000000000"]]];
                NSString * o15  = [[emvAPPDict valueForKey:@"Transaction_Currency_Code"] stringByAppendingString:[self getEMVStr:@"0608"]];
                NSString * o16 = [[emvAPPDict valueForKey:@"Transaction_Currency_Exponent"] stringByAppendingString:[self getEMVStr:@"02"]];
                NSString * o17  = [[emvAPPDict valueForKey:@"Transaction_Reference_Currency_Code"] stringByAppendingString:[self getEMVStr:@"0608"]];
                NSString * o18 = [[emvAPPDict valueForKey:@"Transaction_Reference_Currency_Exponent"] stringByAppendingString:[self getEMVStr:@"02"]];
                NSString * o19  = [[emvAPPDict valueForKey:@"Terminal_Country_Code"] stringByAppendingString:[self getEMVStr:@"0608"]];
                NSString * o20  = [[emvAPPDict valueForKey:@"Interface_Device_IFD_Serial_Number"] stringByAppendingString:[self getEMVStr:[self getHexFromStr:@"83201ICC"]]];
                NSString * o21 =[[emvAPPDict valueForKey:@"Terminal_Identification"] stringByAppendingString:[self getEMVStr:[self getHexFromStr:@"NL-GP730"]]];
                NSString * o22  =[[emvAPPDict valueForKey:@"Default_DDOL"] stringByAppendingString:[self getEMVStr:@"9f3704"]];
                NSString * o23 =[[emvAPPDict valueForKey:@"Default_Tdol"] stringByAppendingString:[self getEMVStr:@"9F1A0295059A039C01"]];
                NSString * o24  =[[emvAPPDict valueForKey:@"Application_Selection_Indicator"] stringByAppendingString:[self getEMVStr:@"01"]];
                NSString * o25  =[[emvAPPDict valueForKey:@"terminal_contactless_transaction_limit"] stringByAppendingString:[self getEMVStr:@"000000200001"]];
                NSString * o26  =[[emvAPPDict valueForKey:@"terminal_execute_cvm_limit"] stringByAppendingString:[self getEMVStr:@"000000999999"]];
                
                NSArray *certainAIDConfigArr = @[o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13,o14,o15,o16,o17,o18,o19,o20,o21,o22,o23,o24,o25,o26];
                [self.mPos updateEmvAPP:EMVOperation_update data:certainAIDConfigArr block:^(BOOL isSuccess, NSString *stateStr) {
                    if (isSuccess) {
                        NSString *state = stateStr;
                        
                    }else{
                        NSString *stateStr = [NSString stringWithFormat:@"update aid fail"];
                    }
                    
                }];
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:YES];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                
            }else if([name isEqualToString:@"updateEmvCAPK"]) {
                NSMutableDictionary * emvCAPKDict = [self.mPos getEMVCAPK];
                NSString * rid =[[emvCAPKDict valueForKey:@"RID"] stringByAppendingString:[self getEMVStr:@"A0000000004"]];
                NSString * pkindex =[[emvCAPKDict valueForKey:@"public_Key_Index"] stringByAppendingString:[self getEMVStr:@"F1"]];
                NSString * pkCheck  =[[emvCAPKDict valueForKey:@"Public_Key_CheckValue"] stringByAppendingString:[self getEMVStr:@"A0000000004"]];
                NSString * pkExponent =[[emvCAPKDict valueForKey:@"Pk_exponent"] stringByAppendingString:[self getEMVStr:@"A0000000004"]];
                NSString * hashAI  =[[emvCAPKDict valueForKey:@"Hash_algorithm_identification"] stringByAppendingString:[self getEMVStr:@"A0000000004"]];
                NSString * pkAI  =[[emvCAPKDict valueForKey:@"Hash_algorithm_identification"] stringByAppendingString:[self getEMVStr:@"A0000000004"]];
                
                NSArray *arr = @[rid,pkindex,pkCheck,pkExponent,hashAI];
                [self.mPos updateEmvAPP:EMVOperation_update data:arr block:^(BOOL isSuccess, NSString *stateStr) {
                    if (isSuccess) {
                        NSString *state = stateStr;
                    }else{
                        NSString *stateStr = [NSString stringWithFormat:@"update emv capk fail"];
                    }
                }];
                
            }else if([name isEqualToString:@"setMasterKey"]){
              
                NSString *pik = @"89EEF94D28AA2DC189EEF94D28AA2DC1";//111111111111111111111111
                NSString *pikCheck = @"82E13665B4624DF5";
                
                pik = @"F679786E2411E3DEF679786E2411E3DE";//33333333333333333333333333333
                pikCheck = @"ADC67D8473BF2F06";
                [self.mPos setMasterKey:pik checkValue:pikCheck keyIndex:0];

            }else if([name isEqualToString:@"updatePosFirmware"]){
                NSData *data = [self readLine:@"A27CAYC_S1(IBOX)_master"];//read a14upgrader.asc
                
                if (data != nil) {
                    [[QPOSService sharedInstance] updatePosFirmware:data address:self.bluetoothAddress];
                }else{
                    NSLog(@"pls make sure you have passed the right data");
                }
                NSLog(@"firmware updating...");
                appearTimer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(progressMethod) userInfo:nil repeats:YES];
            }
        }else{
            //callback
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[NSString stringWithFormat:@"no method found to %@",name]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];

}
@end
