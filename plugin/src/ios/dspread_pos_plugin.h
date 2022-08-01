//
//  dspread_pos_plugin.h
//  qpos-ios-demo
//
//  Created by dspread-mac on 2018/2/1.
//  Copyright © 2018年 Robin. All rights reserved.
//

#import <Cordova/CDVPlugin.h>
#import "QPOSService.h"
#import "BTDeviceFinder.h"
@interface dspread_pos_plugin : CDVPlugin<UIImagePickerControllerDelegate, UINavigationControllerDelegate,QPOSServiceListener,UIActionSheetDelegate,BluetoothDelegate2Mode>

-(void)pluginListener:(CDVInvokedUrlCommand *)command;
-(void)scanQPos2Mode:(CDVInvokedUrlCommand *)command;
-(void)connectBluetoothDevice:(CDVInvokedUrlCommand *)command;
-(void)doTrade:(CDVInvokedUrlCommand *)command;
-(void)stopScanQPos2Mode:(CDVInvokedUrlCommand *)command;
-(void)getQposInfo:(CDVInvokedUrlCommand *)command;
-(void)getQposId:(CDVInvokedUrlCommand *)command;
-(void)updateIPEK:(CDVInvokedUrlCommand *)command;
-(void)updateEmvCAPK:(CDVInvokedUrlCommand *)command;
-(void)updateEmvApp:(CDVInvokedUrlCommand *)command;
-(void)disconnect:(CDVInvokedUrlCommand *)command;
-(void)updateEMVConfigByXml:(CDVInvokedUrlCommand *)command;
-(void)updateEMVConfigForQPOScute:(CDVInvokedUrlCommand *)command;
-(void)updateEMVConfigForQPOSmini:(CDVInvokedUrlCommand *)command;
-(void)getICCTag:(CDVInvokedUrlCommand *)command;
-(void)sendPin:(CDVInvokedUrlCommand*)command;
-(void)sendOnlineProcessResult:(CDVInvokedUrlCommand*)command;
-(void)resetQPosStatus:(CDVInvokedUrlCommand*)command;
-(void)connectBluetoothNoScan:(CDVInvokedUrlCommand*)command;
@end
