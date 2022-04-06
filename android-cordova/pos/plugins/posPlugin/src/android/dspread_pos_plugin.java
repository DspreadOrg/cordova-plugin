package org.apache.cordova.posPlugin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Trace;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dspread.xpos.CQPOSService;
import com.dspread.xpos.EmvAppTag;
import com.dspread.xpos.EmvCapkTag;
import com.dspread.xpos.QPOSService;
import com.dspread.xpos.QPOSService.CommunicationMode;
import com.dspread.xpos.QPOSService.Display;
import com.dspread.xpos.QPOSService.DoTradeResult;
import com.dspread.xpos.QPOSService.EMVDataOperation;
import com.dspread.xpos.QPOSService.EmvOption;
import com.dspread.xpos.QPOSService.TransactionResult;
import com.dspread.xpos.QPOSService.TransactionType;
import com.dspread.xpos.QPOSService.UpdateInformationResult;
import com.pos.demoui.R;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.PluginResult;
import org.apache.cordova.engine.SystemWebChromeClient;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import javax.script.Invocable;
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;


/**
 * This class echoes a string called from JavaScript.
 */
public class dspread_pos_plugin extends CordovaPlugin{
	private MyPosListener listener;
	private QPOSService pos;
	private UpdateThread updateThread;
	private BluetoothAdapter mAdapter;
	private String sdkVersion;
	private String blueToothAddress;
	private List<BluetoothDevice> listDevice;
	private String terminalTime = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
	private String currencyCode = "156";
	private TransactionType transactionType = TransactionType.GOODS;
	ArrayList<String> list=new ArrayList<String>();
	private String amount = "";
	private String cashbackAmount = "";
	private List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
	private static final int PROGRESS_UP = 1001;
	private Hashtable<String, String> pairedDevice;
	private Activity activity;
	private CordovaWebView webView;
	private LocationManager lm;//【位置管理】
	private boolean posFlag=false;
	private List blueToothNameArr = new ArrayList();
	private Map map = new HashMap();
	private PluginResult pluginResult = null;
	private Dialog dialog;
	private ListView appListView;
	private String position;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		return super.execute(action, args, callbackContext);
	}

	@Override
	public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
		map.put(action,callbackContext.getCallbackId());
		if(action.equals("scanQPos2Mode")) {
			posType = POS_TYPE.BLUETOOTH;
			open(CommunicationMode.BLUETOOTH);//initial the open mode
			boolean a=pos.scanQPos2Mode(activity, 10);
			Toast.makeText(cordova.getActivity(), "scan success "+a, Toast.LENGTH_LONG).show();
		} else if(action.equals("openUart")){
			posType = POS_TYPE.UART;
			open(CommunicationMode.UART);
			blueToothAddress = "/dev/ttyS1";//"ttyS1" is for D20; "ttys1" is for tongfang; "ttys3" is for tianbo
			pos.setDeviceAddress(blueToothAddress);
			pos.openUart();

		} else if(action.equals("setAmount")){
			amount = args.getInt(0)+"";
			cashbackAmount = args.getInt(1)+"";
			currencyCode = args.getString(2);
			int transactionType = args.getInt(3);
			pos.setAmount(amount,cashbackAmount,currencyCode,TransactionType.values()[transactionType]);
			TRACE.d("args0: "+args.getInt(0) + "args1: "+args.getInt(1) + "args2: "+args.getString(2) + "args3: "+args.getInt(3));
		}else if(action.equals("connectBluetoothDevice")){//connect
			pos.stopScanQPos2Mode();
			boolean isAutoConnect=args.getBoolean(0);
			String address=args.getString(1);
			int a = address.indexOf(" ");
			address = address.substring(a+1);
			TRACE.d("address==="+address);
			blueToothAddress = address;
			pos.connectBluetoothDevice(isAutoConnect, 20, address);
		}else if(action.equals("doTrade")){//start to do a trade
			TRACE.d("native--> doTrade");
			pos.setFormatId(QPOSService.FORMATID.DUKPT);
			if(posType == POS_TYPE.UART){
				pos.setCardTradeMode(QPOSService.CardTradeMode.SWIPE_TAP_INSERT_CARD_NOTUP);
				pos.doTrade(70);
			} else {
				pos.setCardTradeMode(QPOSService.CardTradeMode.SWIPE_TAP_INSERT_CARD);
				pos.doTrade(20);
			}

		}else if(action.equals("getDeviceList")){//get all scaned devices
			TRACE.w("getDeviceList===");
			posFlag=true;
			listDevice=pos.getDeviceList();//can get all scaned device
			if(listDevice.size() > 0) {
				String[] macAddress = new String[listDevice.size()];
				String devices = "";
				for (int i = 0; i < listDevice.size(); i++) {
					macAddress[i] = listDevice.get(i).getName() + "(" + listDevice.get(i).getAddress() + "),";
					devices += macAddress[i];
				}
				TRACE.w("get devi==" + devices);
			}
		}else if(action.equals("stopScanQPos2Mode")){//stop scan bluetooth
			pos.stopScanQPos2Mode();
		}else if(action.equals("disconnect")){//discooect bluetooth
			//pos.disconnectBT();
			close();
		}else if(action.equals("getQposInfo")){//get the pos info
			pos.getQposInfo();
		}else if(action.equals("getQposId")){//get the pos id
			pos.getQposId(20);
		}else if(action.equals("updateIPEK")){//update the ipek key
			TRACE.d("native--> updateIPEK: "+ args);
			String ipekGroup=args.getString(0);
			String trackksn=args.getString(1);
			String trackipek=args.getString(2);
			String trackipekCheckvalue=args.getString(3);
			String emvksn=args.getString(4);
			String emvipek=args.getString(5);
			String emvipekCheckvalue=args.getString(6);
			String pinksn=args.getString(7);
			String pinipek=args.getString(8);
			String pinipekCheckvalue=args.getString(9);
			// This IPEK is generated from default BDK
//        	pos.doUpdateIPEKOperation("00",
//					"FFFF9876543210E00001",
//					"D6BAB875F279357275DFF0395AA3CBBF",
//					"AF8C074A692A3666",
//					"FFFF9876543210E00001",
//					"D6BAB875F279357275DFF0395AA3CBBF",
//					"AF8C074A692A3666",
//					"FFFF9876543210E00001",
//					"D6BAB875F279357275DFF0395AA3CBBF",
//					"AF8C074A692A3666");
			pos.doUpdateIPEKOperation(ipekGroup, trackksn, trackipek, trackipekCheckvalue, emvksn, emvipek, emvipekCheckvalue, pinksn, pinipek, pinipekCheckvalue);
		}else if(action.equals("updateEmvApp")){//update the emv app config
			list.add(EmvAppTag.Terminal_Default_Transaction_Qualifiers+"36C04000");
			list.add(EmvAppTag.Contactless_CVM_Required_limit+"000000060000");
			list.add(EmvAppTag.terminal_contactless_transaction_limit+"000000060000");
			pos.updateEmvAPP(EMVDataOperation.update,list);
		}else if(action.equals("updateEmvCAPK")){//update the emv capk config
			list.add(EmvCapkTag.RID+"A000000004");
			list.add(EmvCapkTag.Public_Key_Index+"F1");
			list.add(EmvCapkTag.Public_Key_Module+"A0DCF4BDE19C3546B4B6F0414D174DDE294AABBB828C5A834D73AAE27C99B0B053A90278007239B6459FF0BBCD7B4B9C6C50AC02CE91368DA1BD21AAEADBC65347337D89B68F5C99A09D05BE02DD1F8C5BA20E2F13FB2A27C41D3F85CAD5CF6668E75851EC66EDBF98851FD4E42C44C1D59F5984703B27D5B9F21B8FA0D93279FBBF69E090642909C9EA27F898959541AA6757F5F624104F6E1D3A9532F2A6E51515AEAD1B43B3D7835088A2FAFA7BE7");
			list.add(EmvCapkTag.Public_Key_CheckValue+"D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BB");
			list.add(EmvCapkTag.Pk_exponent+"03");
			pos.updateEmvCAPK(EMVDataOperation.update, list);
		}else if(action.equals("setMasterKey")){//set the masterkey
			TRACE.d("native--> setMasterKey: "+ args);
			String key=args.getString(0);
			String checkValue=args.getString(1);
			pos.setMasterKey(key,checkValue);
		}else if(action.equals("updatePosFirmware")){//update pos firmware
			String filename = args.getString(0);
			byte[] data=readLine(filename);//upgrader.asc place in the assets folder
			if(data == null) {
				callbackKeepResult(PluginResult.Status.OK, false, "updatePosFirmware", "Can't find this file");
			}
			int a = pos.updatePosFirmware(data, blueToothAddress);//deviceAddress is BluetoothDevice addres
			if (a == -1) {
				Toast.makeText(cordova.getActivity(), "please keep the device charging", Toast.LENGTH_LONG).show();
				callbackKeepResult(PluginResult.Status.OK, false, "updatePosFirmware", "please keep charge");
			}
			updateThread = new UpdateThread();
			updateThread.start();
		}else if(action.equals("updateEMVConfigByXml")){
			TRACE.d("native--> updateEMVConfigByXml");
			String xmlStr = args.getString(0);
			TRACE.d("bytes: "+ xmlStr);
			pos.updateEMVConfigByXml(new String(xmlStr));
		}else if(action.equals("getIccCardNo")){
			TRACE.d("native--> getIccCardNo");
			pos.getIccCardNo(terminalTime);
		}else if(action.equals("getICCTag")){
			TRACE.d("native--> getICCTag");
			int encryptType=args.getInt(0);
			QPOSService.EncryptType type = QPOSService.EncryptType.PLAINTEXT;
			if (encryptType==0) {
				type = QPOSService.EncryptType.PLAINTEXT;
			}
			int cardType=args.getInt(1);
			int tagCount=args.getInt(2);
			String tagArrStr=args.getString(3);
            Hashtable hashtable = pos.getICCTag(type,cardType, tagCount, tagArrStr);
            TRACE.d("hashtable: "+ hashtable.get("tlv"));
		}else if(action.equals("pollOnMifareCard")){
			int timeout = args.getInt(0);
			TRACE.d("poll on timeout:"+timeout);
			pos.pollOnMifareCard(timeout);
		}else if(action.equals("authenticateMifareCard")){
			String mifareCardType = args.getString(0);
			String keyclass =args.getString(1);
			String blockaddr = args.getString(2);
			String keyValue = args.getString(3);
			int timout = args.getInt(4);
			TRACE.d("authenticate:"+mifareCardType+" "+keyValue+" "+keyclass);
			if(keyclass.equals("Key A")||keyclass.equals("Key B")){
				if(mifareCardType.equals("CLASSIC")){
					pos.authenticateMifareCard(QPOSService.MifareCardType.CLASSIC,keyclass,blockaddr,keyValue,timout);
				} else if(mifareCardType.equals("ULTRALIGHT")){
					pos.authenticateMifareCard(QPOSService.MifareCardType.UlTRALIGHT,keyclass,blockaddr,keyValue,timout);

				} else {
					TRACE.d("mifare card type error,only can be CLASSIC / ULTRALIGHT");
					Toast.makeText(cordova.getActivity(), "mifare card type error,only can be CLASSIC / ULTRALIGHT", Toast.LENGTH_LONG).show();
				}
			} else {
				TRACE.d("mifare keyclass error,only can be Key A / Key B, yours is "+ keyclass);
				Toast.makeText(cordova.getActivity(), "mifare keyclass error,only can be Key A / Key B", Toast.LENGTH_LONG).show();
			}
		}else if(action.equals("readMifareCard")){
			String mifareCardType = args.getString(0);
			String blockaddr = args.getString(1);
			int timout = args.getInt(2);
			TRACE.d("read:"+mifareCardType+" "+ blockaddr+ " "+ timout);
			if(mifareCardType.equals("CLASSIC")){
				pos.readMifareCard(QPOSService.MifareCardType.CLASSIC,blockaddr,timout);
			} else if(mifareCardType.equals("ULTRALIGHT")){
				pos.readMifareCard(QPOSService.MifareCardType.UlTRALIGHT,blockaddr,timout);

			} else {
				TRACE.d("mifare card type error,only can be CLASSIC / ULTRALIGHT");
				Toast.makeText(cordova.getActivity(), "mifare card type error,only can be CLASSIC / ULTRALIGHT", Toast.LENGTH_LONG).show();
			}
		}else if(action.equals("writeMifareCard")){
			String mifareCardType = args.getString(0);
			String blockaddr = args.getString(1);
			String cardData = args.getString(2);
			int timout = args.getInt(3);
			TRACE.d("writemifare:"+mifareCardType+" "+blockaddr+" "+cardData+" "+timout);
			if(mifareCardType.equals("CLASSIC")){
				pos.writeMifareCard(QPOSService.MifareCardType.CLASSIC,blockaddr,cardData,timout);
			} else if(mifareCardType.equals("ULTRALIGHT")){
				pos.writeMifareCard(QPOSService.MifareCardType.UlTRALIGHT,blockaddr,cardData,20);
			} else {
				TRACE.d("mifare card type error,only can be CLASSIC / ULTRALIGHT");
				Toast.makeText(cordova.getActivity(), "mifare card type error,only can be CLASSIC / ULTRALIGHT", Toast.LENGTH_LONG).show();
			}
		}else if(action.equals("operateMifareCardData")){
			String mifareOperatieType = args.getString(0).toUpperCase();
			String blockaddr = args.getString(1);
			String cardData = args.getString(2);
			int timout = args.getInt(3);
			TRACE.d("operateMifare:"+mifareOperatieType+" "+blockaddr+" "+cardData);
			QPOSService.MifareCardOperationType cmd = null;
			switch (mifareOperatieType){
				case "ADD":
					cmd = QPOSService.MifareCardOperationType.ADD;
					break;
				case "REDUCE":
					cmd = QPOSService.MifareCardOperationType.REDUCE;
					break;
				case "RESTORE":
					cmd = QPOSService.MifareCardOperationType.RESTORE;
					break;
				default:
					break;
			}
			if(!mifareOperatieType.equals("ADD")&&!mifareOperatieType.equals("REDUCE")&&!mifareOperatieType.equals("RESTORE")){
				TRACE.d("mifare operation type error,only can be ADD / REDUCE / RESTORE; yours is"+mifareOperatieType);
				Toast.makeText(cordova.getActivity(), "mifare operation type error,only can be ADD / REDUCE / RESTORE", Toast.LENGTH_LONG).show();
			}else {
				pos.operateMifareCardData(cmd,blockaddr,cardData,timout);
			}
		}else if(action.equals("fastReadMifareCardData")){
			String startAddr = args.getString(0);
			String endAddr = args.getString(1);
			int timout = args.getInt(2);
			pos.fastReadMifareCardData(startAddr,endAddr,timout);
		}else if(action.equals("finishMifareCard")){
			pos.finishMifareCard(20);
		}else if(action.equals("powerOnNFC")){
			boolean isEncrypt = args.getBoolean(0);
			int timout = args.getInt(1);
			pos.powerOnNFC(isEncrypt,timout);
		}else if(action.equals("sendApduByNFC")){
			String apduStr = args.getString(0);
			int timout = args.getInt(1);
			pos.sendApduByNFC(apduStr,timout);
		}else if(action.equals("powerOffNFC")){
			int timout = args.getInt(0);
			pos.powerOffNFC(timout);
		}else if(action.equals("lcdShowCustomDisplay")){
			String customDisplayString = "";
			String diplay=args.getString(1);
	        try {
	            byte[] paras = diplay.getBytes("GBK");
	            customDisplayString = QPOSUtil.byteArray2Hex(paras);
	            pos.lcdShowCustomDisplay(QPOSService.LcdModeAlign.LCD_MODE_ALIGNCENTER, customDisplayString, 60);
	        } catch (Exception e) {
	            e.printStackTrace();
	            TRACE.d("gbk error");
	        }
		}else if(action.equals("customInputDisplay")){
			int operationType = args.getInt(0);
			int displayType = args.getInt(1);
			int maxLen = args.getInt(2);
			String DisplayStr = args.getString(3);
			String initiator = args.getString(4);
			int timeout = args.getInt(5);
			TRACE.d("type:"+operationType+"dis:"+displayType+"maxLen:"+maxLen+"Str:"+DisplayStr+"initiator:"+initiator);
			pos.customInputDisplay(operationType,displayType,maxLen,DisplayStr,initiator,timeout);
		}else if(action.equals("sendPosition")){
			position = args.getString(0);

			position = position.substring(2,position.length()-2);
			TRACE.d("get pos:"+position);
			pos.pinMapSync(position,30);
			//callbackKeepResult(PluginResult.Status.OK, true, "sendPosition", "ok");
		}else if(action.equals("resetQPosStatus")){
			boolean a = pos.resetQPosStatus();
			if(a){
				callbackKeepResult(PluginResult.Status.OK, true, "resetQPosStatus", "pos reset");
			}
		}else if(action.equals("doSetBuzzerOperation")){
			int times = args.getInt(0);
			pos.doSetBuzzerOperation(times);
		}
		return true;
	}

	public void callbackKeepResult(PluginResult.Status status,Boolean isKeep,String key, String message){
		if (!map.containsKey(key)){
			return;
		}
		pluginResult = new PluginResult(status,message);
		pluginResult.setKeepCallback(isKeep);
		CallbackContext callbackContext = new CallbackContext((String) map.get(key),webView);
		callbackContext.sendPluginResult(pluginResult);
	}

	public static byte[] readAssetsLine(String fileName, Context context) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			ContextWrapper contextWrapper = new ContextWrapper(context);
			AssetManager assetManager = contextWrapper.getAssets();
			InputStream inputStream = assetManager.open(fileName);
			byte[] data = new byte[512];
			int current = 0;
			while ((current = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, current);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return  null;
		}
		return buffer.toByteArray();
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		// TODO Auto-generated method stub
		super.initialize(cordova, webView);
		this.activity=cordova.getActivity();
		this.webView=webView;
//		lin = findViewById(MResource.getIdByName(this.activity,"id","lin"));
		requestPer();
	}

	private POS_TYPE posType = POS_TYPE.BLUETOOTH;

	private enum POS_TYPE {
		BLUETOOTH, AUDIO, UART, USB, OTG, BLUETOOTH_BLE
	}
	//initial the pos
	private void open(CommunicationMode mode) {
		TRACE.d("open");
		listener = new MyPosListener();
		pos = QPOSService.getInstance(mode);
		if (pos == null) {
			TRACE.d("CommunicationMode unknow");
			return;
		}
		if(posType == POS_TYPE.UART){
			pos.setD20Trade(true);
		} else {
			pos.setD20Trade(false);
		}
		pos.setConext(cordova.getActivity());

		Handler handler = new Handler(Looper.myLooper());
		pos.initListener(handler, listener);
		sdkVersion = pos.getSdkVersion();
		TRACE.i("sdkVersion:"+sdkVersion);
		mAdapter=BluetoothAdapter.getDefaultAdapter();
	}

	private void close(){
		TRACE.d("close");
		if (pos == null) {
			return;
		} else if (posType == POS_TYPE.AUDIO) {
			pos.closeAudio();
		} else if (posType == POS_TYPE.BLUETOOTH) {
			pos.disconnectBT();
//			pos.disConnectBtPos();
		} else if (posType == POS_TYPE.BLUETOOTH_BLE) {
			pos.disconnectBLE();
		} else if (posType == POS_TYPE.UART) {
			pos.closeUart();
		} else if (posType == POS_TYPE.USB) {
			pos.closeUsb();
		} else if (posType == POS_TYPE.OTG) {
			pos.closeUsb();
		}
	}

	private void requestPer(){
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null && !adapter.isEnabled()) {//表示蓝牙不可用
			Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivity(enabler);
		}
		lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (ok) {//开了定位服务
			if (!cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
				Log.e("POS_SDK", "没有权限");
				// 没有权限，申请权限。
				// 申请授权。
				cordova.requestPermissions(this,100,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION});
			} else {
				// 有权限了，去放肆吧。
				Toast.makeText(activity, "Has permission!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.e("BRG", "系统检测到未开启GPS定位服务");
			Toast.makeText(activity, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			activity.startActivity(intent);
		}
	}

	@Override
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		super.onRequestPermissionResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case 100: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 权限被用户同意。
					Toast.makeText(activity, "Has open the permission!", Toast.LENGTH_LONG).show();
				} else {
					// 权限被用户拒绝了。
					Toast.makeText(activity, "Permission has been limited", Toast.LENGTH_LONG).show();
				}

			}
			break;
		}
	}

	private void sendMsg(int what) {
		Message msg = new Message();
		msg.what = what;
		mHandler.sendMessage(msg);
	}

	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 8003:
					Hashtable<String, String> h =  pos.getNFCBatchData();
					TRACE.w("nfc batchdata: "+h);
					String content = "\nNFCbatchData: "+h.get("tlv");
					break;
				default:
					break;
			}
		}
	};

	//read the buffer
	private byte[] readLine(String Filename) {

		String str = "";
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(0);
		try {
			ContextWrapper contextWrapper = new ContextWrapper(cordova.getActivity());
			AssetManager assetManager = contextWrapper.getAssets();
			InputStream inputStream = assetManager.open(Filename);
			// BufferedReader br = new BufferedReader(new
			// InputStreamReader(inputStream));
			// str = br.readLine();
			int b = inputStream.read();
			while (b != -1) {
				buffer.write((byte) b);
				b = inputStream.read();
			}
			TRACE.d("-----------------------");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return buffer.toByteArray();
	}

	class UpdateThread extends Thread {
		private boolean concelFlag = false;
		public void run() {

			while (true) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int progress = pos.getUpdateProgress();
				if (progress < 100) {
					Message msg = updata_handler.obtainMessage();
					msg.what = PROGRESS_UP;
					msg.obj = progress;
					msg.sendToTarget();
					continue;
				}
				Message msg = updata_handler.obtainMessage();
				msg.what = PROGRESS_UP;
				msg.obj = "update success";
				msg.sendToTarget();
				break;
			}
		};
		public void concelSelf() {
			concelFlag = true;
		}
	}

	private Handler updata_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case PROGRESS_UP://update the firmware
					TRACE.i(msg.obj.toString() + "%");
					break;
				case 101://the callback of the connect the printer success
					Toast.makeText(cordova.getActivity(), "connect the printer success", Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
		}
	};


	//our sdk api callback(success or fail)
	class MyPosListener extends CQPOSService {

		@Override
		public void onDeviceFound(BluetoothDevice arg0) {
			if (arg0 != null) {
				String address = arg0.getAddress();
				String name = arg0.getName();
				String mac = name + " " + address;
				if (!blueToothNameArr.contains(mac)) {
					blueToothNameArr.add(mac);
					TRACE.i("scaned the device:\n" + name + "(" + address + ")");
					if (name != null) {
						callbackKeepResult(PluginResult.Status.OK, true, "scanQPos2Mode", mac);
					}
				}
			}
		}

		@Override
		public void onQposIdResult(Hashtable<String, String> arg0) {
			if (arg0 != null) {
				String posId = arg0.get("posId") == null ? "" : arg0
						.get("posId");
				String csn = arg0.get("csn") == null ? "" : arg0
						.get("csn");
				String psamId = arg0.get("psamId") == null ? "" : arg0
						.get("psamId");

				String content = "";
				content += "posId" + posId + "\n";
				content += "csn: " + csn + "\n";
				content += "conn: " + pos.getBluetoothState() + "\n";
				content += "psamId: " + psamId + "\n";
			}
		}

		@Override
		public void onRequestWaitingUser() {
			TRACE.d("onRequestWaitingUser()");
			callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "onRequestWaitingUser");
		}


		@Override
		public void onQposInfoResult(Hashtable<String, String> arg0) {
			TRACE.d("onQposInfoResult" + arg0);
			String isSupportedTrack1 = arg0.get("isSupportedTrack1") == null ? "" : arg0.get("isSupportedTrack1");
			String isSupportedTrack2 = arg0.get("isSupportedTrack2") == null ? "" : arg0.get("isSupportedTrack2");
			String isSupportedTrack3 = arg0.get("isSupportedTrack3") == null ? "" : arg0.get("isSupportedTrack3");
			String bootloaderVersion = arg0.get("bootloaderVersion") == null ? "" : arg0.get("bootloaderVersion");
			String firmwareVersion = arg0.get("firmwareVersion") == null ? "" : arg0.get("firmwareVersion");
			String isUsbConnected = arg0.get("isUsbConnected") == null ? "" : arg0.get("isUsbConnected");
			String isCharging = arg0.get("isCharging") == null ? "" : arg0.get("isCharging");
			String batteryLevel = arg0.get("batteryLevel") == null ? "" : arg0.get("batteryLevel");
			String batteryPercentage = arg0.get("batteryPercentage") == null ? ""
					: arg0.get("batteryPercentage");
			String hardwareVersion = arg0.get("hardwareVersion") == null ? "" : arg0.get("hardwareVersion");
			String SUB = arg0.get("SUB") == null ? "" : arg0.get("SUB");
			String content = "";
			content += "bootloader_version" + bootloaderVersion + "\n";
			content += "firmwareVersion" + firmwareVersion + "\n";
			content += "isUsbConnected" + isUsbConnected + "\n";
			content += "isCharging" + isCharging + "\n";
//			if (batteryPercentage==null || "".equals(batteryPercentage)) {
			content += "batteryLevel" + batteryLevel + "\n";
//			}else {
			content += "batteryPercentage" + batteryPercentage + "\n";
//			}
			content += "hardwareVersion" + hardwareVersion + "\n";
			content += "SUB : " + SUB + "\n";
			content += "isSupportedTrack1" + isSupportedTrack1 + "\n";
			content += "isSupportedTrack2" + isSupportedTrack2 + "\n";
			content += "isSupportedTrack3" + isSupportedTrack3 + "\n";
			callbackKeepResult(PluginResult.Status.OK, true, "getQposInfo", content);
		}


		@Override
		public void onDoTradeResult(DoTradeResult arg0, Hashtable<String, String> arg1) {
			if (arg0 == DoTradeResult.NONE) {
				TRACE.d("no_card_detected");
				callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "no_card_detected");
			} else if(arg0 == DoTradeResult.TRY_ANOTHER_INTERFACE){
				TRACE.d("Try another interface");
				callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "Restart and try another interface");
			}else if (arg0 == DoTradeResult.ICC) {
				TRACE.d("icc_card_inserted");
				TRACE.d("EMV ICC Start");
				pos.doEmvApp(EmvOption.START);//do the icc card trade
			} else if (arg0 == DoTradeResult.NOT_ICC) {
				TRACE.d("card_inserted(NOT_ICC)");
				callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "card_inserted(NOT_ICC)");
			} else if (arg0 == DoTradeResult.BAD_SWIPE) {
				TRACE.d("bad_swipe");
				callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "bad_swipe");
			} else if (arg0 == DoTradeResult.MCR) {//
				String content = "Swipe Card:\n";
				String formatID = arg1.get("formatID");
				if (formatID.equals("31") || formatID.equals("40") || formatID.equals("37") || formatID.equals("17") || formatID.equals("11") || formatID.equals("10")) {
					String maskedPAN = arg1.get("maskedPAN");
					String expiryDate = arg1.get("expiryDate");
					String cardHolderName = arg1.get("cardholderName");
					String serviceCode = arg1.get("serviceCode");
					String trackblock = arg1.get("trackblock");
					String psamId = arg1.get("psamId");
					String posId = arg1.get("posId");
					String pinblock = arg1.get("pinblock");
					String macblock = arg1.get("macblock");
					String activateCode = arg1.get("activateCode");
					String trackRandomNumber = arg1.get("trackRandomNumber");

					content += "format_id" + " " + formatID + "\n";
					content += "masked_pan" + " " + maskedPAN + "\n";
					content += "expiry_date" + " " + expiryDate + "\n";
					content += "cardholder_name" + " " + cardHolderName + "\n";

					content += "service_code" + " " + serviceCode + "\n";
					content += "trackblock: " + trackblock + "\n";
					content += "psamId: " + psamId + "\n";
					content += "posId: " + posId + "\n";
					content += "pinBlock" + " " + pinblock + "\n";
					content += "macblock: " + macblock + "\n";
					content += "activateCode: " + activateCode + "\n";
					content += "trackRandomNumber: " + trackRandomNumber + "\n";
				} else if (formatID.equals("FF")) {
					String type = arg1.get("type");
					String encTrack1 = arg1.get("encTrack1");
					String encTrack2 = arg1.get("encTrack2");
					String encTrack3 = arg1.get("encTrack3");
					content += "cardType:" + " " + type + "\n";
					content += "track_1:" + " " + encTrack1 + "\n";
					content += "track_2:" + " " + encTrack2 + "\n";
					content += "track_3:" + " " + encTrack3 + "\n";
				} else {
					String orderID = arg1.get("orderId");
					String maskedPAN = arg1.get("maskedPAN");
					String expiryDate = arg1.get("expiryDate");
					String cardHolderName = arg1.get("cardholderName");
//					String ksn = arg1.get("ksn");
					String serviceCode = arg1.get("serviceCode");
					String track1Length = arg1.get("track1Length");
					String track2Length = arg1.get("track2Length");
					String track3Length = arg1.get("track3Length");
					String encTracks = arg1.get("encTracks");
					String encTrack1 = arg1.get("encTrack1");
					String encTrack2 = arg1.get("encTrack2");
					String encTrack3 = arg1.get("encTrack3");
					String partialTrack = arg1.get("partialTrack");
					// TODO
					String pinKsn = arg1.get("pinKsn");
					String trackksn = arg1.get("trackksn");
					String pinBlock = arg1.get("pinBlock");
					String encPAN = arg1.get("encPAN");
					String trackRandomNumber = arg1.get("trackRandomNumber");
					String pinRandomNumber = arg1.get("pinRandomNumber");
					if (orderID != null && !"".equals(orderID)) {
						content += "orderID:" + orderID;
					}
					content += "formatID: " + formatID + "\n";
					content += "maskedPAN: " + maskedPAN + "\n";
					content += "expiryDate: " + expiryDate + "\n";
					content += "cardHolderName: " + cardHolderName + "\n";
//					content += getString(R.string.ksn) + " " + ksn + ",";
					content += "pinKsn: " + pinKsn + "\n";
					content += "trackksn: " + trackksn + "\n";
					content += "serviceCode: " + serviceCode + "\n";
					content += "track1Length: " + track1Length + "\n";
					content += "track2Length: " + track2Length + "\n";
					content += "track3Length: " + track3Length + "\n";
					content += "encTracks: " + encTracks + "\n";
					content += "encTrack1: " + encTrack1 + "\n";
					content += "encTrack2: " + encTrack2 + "\n";
					content += "encTrack3: " + encTrack3 + "\n";
					content += "partialTrack: " + partialTrack + "\n";
					content += "pinBlock: " + pinBlock + "\n";
					content += "encPAN: " + encPAN + "\n";
					content += "trackRandomNumber: " + trackRandomNumber + "\n";
					content += "pinRandomNumber: " + " " + pinRandomNumber;
					callbackKeepResult(PluginResult.Status.OK, true, "doTrade", content);
				}
				TRACE.d("=====:" + content);
			} else if ((arg0 == DoTradeResult.NFC_ONLINE) || (arg0 == DoTradeResult.NFC_OFFLINE)) {
				TRACE.d(arg0 + ", arg1: " + arg1);
//				nfcLog=arg1.get("nfcLog");
				String content = "Tap Card:" + "\n";
				String formatID = arg1.get("formatID");
				if (formatID.equals("31") || formatID.equals("40")
						|| formatID.equals("37") || formatID.equals("17")
						|| formatID.equals("11") || formatID.equals("10")) {
					String maskedPAN = arg1.get("maskedPAN");
					String expiryDate = arg1.get("expiryDate");
					String cardHolderName = arg1.get("cardholderName");
					String serviceCode = arg1.get("serviceCode");
					String trackblock = arg1.get("trackblock");
					String psamId = arg1.get("psamId");
					String posId = arg1.get("posId");
					String pinblock = arg1.get("pinblock");
					String macblock = arg1.get("macblock");
					String activateCode = arg1.get("activateCode");
					String trackRandomNumber = arg1
							.get("trackRandomNumber");

					content += "formatID" + " " + formatID
							+ "\n";
					content += "maskedPAN" + " " + maskedPAN
							+ "\n";
					content += "expiryDate" + " "
							+ expiryDate + "\n";
					content += "cardHolderName" + " "
							+ cardHolderName + "\n";

					content += "serviceCode" + " "
							+ serviceCode + "\n";
					content += "trackblock: " + trackblock + "\n";
					content += "psamId: " + psamId + "\n";
					content += "posId: " + posId + "\n";
					content += "pinblock" + " " + pinblock
							+ "\n";
					content += "macblock: " + macblock + "\n";
					content += "activateCode: " + activateCode + "\n";
					content += "trackRandomNumber: " + trackRandomNumber + "\n";
				} else {

					String maskedPAN = arg1.get("maskedPAN");
					String expiryDate = arg1.get("expiryDate");
					String cardHolderName = arg1.get("cardholderName");
//					String ksn = arg1.get("ksn");
					String serviceCode = arg1.get("serviceCode");
					String track1Length = arg1.get("track1Length");
					String track2Length = arg1.get("track2Length");
					String track3Length = arg1.get("track3Length");
					String encTracks = arg1.get("encTracks");
					String encTrack1 = arg1.get("encTrack1");
					String encTrack2 = arg1.get("encTrack2");
					String encTrack3 = arg1.get("encTrack3");
					String partialTrack = arg1.get("partialTrack");
					// TODO
					String pinKsn = arg1.get("pinKsn");
					String trackksn = arg1.get("trackksn");
					String pinBlock = arg1.get("pinBlock");
					String encPAN = arg1.get("encPAN");
					String trackRandomNumber = arg1
							.get("trackRandomNumber");
					String pinRandomNumber = arg1.get("pinRandomNumber");

					content += "formatID: " + formatID + "\n";
					content += "maskedPAN: " + maskedPAN + "\n";
					content += "expiryDate: " + expiryDate + "\n";
					content += "cardHolderName: " + cardHolderName + "\n";
					content += "pinKsn: " + pinKsn + "\n";
					content += "trackksn: " + trackksn + "\n";
					content += "trackksn: " + serviceCode + "\n";

					content += "track1Length: " + track1Length + "\n";

					content += "track2Length: " + track2Length + "\n";

					content += "track3Length: " + track3Length + "\n";

					content += "encTracks: " + encTracks + "\n";

					content += "encTracks1: " + encTrack1 + "\n";

					content += "encTracks2: " + encTrack2 + "\n";

					content += "encTracks3: " + encTrack3 + "\n";

					content += "partialTrack: " + partialTrack + "\n";

					content += "pinBlock: " + pinBlock + "\n";

					content += "encPAN: " + encPAN + "\n";
					content += "trackRandomNumber: " + trackRandomNumber + "\n";
					content += "pinRandomNumber: " + pinRandomNumber + "\n";
				}
				TRACE.w(arg0 + ": " + content);
//				sendMsg(8003);
				Hashtable<String, String> h = pos.getNFCBatchData();
				TRACE.w("nfc batchdata: " + h);
				content += "NFCbatchData: " + h.get("tlv");
				callbackKeepResult(PluginResult.Status.OK, true, "doTrade", content);
			} else if ((arg0 == DoTradeResult.NFC_DECLINED)) {
				TRACE.d("transaction_declined");
				callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "transaction_declined");
			} else if (arg0 == DoTradeResult.NO_RESPONSE) {
				TRACE.d("card_no_response");
				callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "card_no_response");
			}
		}

		@Override
		public void onRequestDeviceScanFinished() {
			TRACE.i("scan finished");
			Toast.makeText(activity, "scan finished", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestDisplay(Display arg0) {
			TRACE.d("onRequestDisplay");

			String msg = "";
			if (arg0 == Display.CLEAR_DISPLAY_MSG) {
				msg = "";
			} else if (arg0 == Display.MSR_DATA_READY) {
				AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());
				builder.setTitle("???");
				builder.setMessage("Success,Contine ready");
				builder.setPositiveButton("???", null);
				builder.show();
			} else if (arg0 == Display.PLEASE_WAIT) {
				msg = "please wait..";
			} else if (arg0 == Display.REMOVE_CARD) {
				msg = "remove card";
			} else if (arg0 == Display.TRY_ANOTHER_INTERFACE) {
				msg = "try another interface";
			} else if (arg0 == Display.PROCESSING) {
				msg = "processing...";
			} else if (arg0 == Display.INPUT_PIN_ING) {
				msg = "please input pin on pos";
			} else if (arg0 == Display.MAG_TO_ICC_TRADE) {
				msg = "please insert chip card on pos";
			} else if (arg0 == Display.CARD_REMOVED) {
				msg = "card removed";
			}
			TRACE.d(msg);
			callbackKeepResult(PluginResult.Status.OK, true, "doTrade", msg);
		}

		@Override
		public void onRequestIsServerConnected() {
			TRACE.d("onRequestIsServerConnected");
			pos.isServerConnected(true);
		}

		@Override
		public void onRequestNoQposDetected() {
			TRACE.w("onRequestNoQposDetected");
			Toast.makeText(cordova.getActivity(), "onRequestNoQposDetected", Toast.LENGTH_LONG).show();
			callbackKeepResult(PluginResult.Status.OK, true, "connectBluetoothDevice", "onRequestNoQposDetected");
		}

		@Override
		public void onRequestNoQposDetectedUnbond() {

		}

		@Override
		public void onRequestOnlineProcess(String arg0) {
			TRACE.d("onRequestOnlineProcess");
			TRACE.i("return transaction online data:" + arg0);
			Hashtable<String, String> decodeData = pos.anlysEmvIccData(arg0);
			TRACE.i("decodeData:" + decodeData);
			String tlvString = pos.anlysEmvTLVData(arg0);
			TRACE.i("tlvString: " + tlvString);
			//go online
			callbackKeepResult(PluginResult.Status.OK,true,"doTrade","onRequestOnlineProcess: " + tlvString);
			pos.sendOnlineProcessResult("8A023030");
		}

		@Override
		public void onRequestQposConnected() {
			TRACE.w("onRequestQposConnected");
			Toast.makeText(cordova.getActivity(), "onRequestQposConnected", Toast.LENGTH_LONG).show();
			if (posType == POS_TYPE.UART) {
				callbackKeepResult(PluginResult.Status.OK, true, "openUart", "onRequestQposConnected");
			} else {
				callbackKeepResult(PluginResult.Status.OK, true, "connectBluetoothDevice", "onRequestQposConnected");
			}
		}

		@Override
		public void onRequestQposDisconnected() {
			TRACE.w("onRequestQposDisconnected");
			Toast.makeText(cordova.getActivity(), "onRequestQposDisconnected", Toast.LENGTH_LONG).show();
			callbackKeepResult(PluginResult.Status.OK, true, "disconnect", "onRequestQposDisconnected");
		}

		@Override
		public void onRequestSelectEmvApp(ArrayList<String> appList) {
			callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "onRequestSelectEmvApp");
			TRACE.d("onRequestSelectEmvApp():" + appList.toString());
			TRACE.d("请选择App -- S，emv卡片的多种配置");
			dismissDialog();
			dialog = new Dialog(cordova.getActivity());
			dialog.setContentView(R.layout.emv_app_dialog);
			dialog.setTitle("Please select app");

			String[] appNameList = new String[appList.size()];
			for (int i = 0; i < appNameList.length; ++i) {
				appNameList[i] = appList.get(i);
			}

			appListView = (ListView) dialog.findViewById(R.id.appList);
			appListView.setAdapter(new ArrayAdapter<String>(cordova.getActivity(), android.R.layout.simple_list_item_1, appNameList));
			appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					pos.selectEmvApp(position);
					TRACE.d("请选择App -- 结束 position = " + position);
					dismissDialog();
				}
			});

			dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pos.cancelSelectEmvApp();
					dismissDialog();
				}
			});
			dialog.show();
		}

		@Override
		public void onRequestSetAmount() {
			TRACE.d("onRequestSetAmount");
			callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "onRequestSetAmount");
		}

		@Override
		public void onRequestSetPin() {
			TRACE.d("onRequestSetPin");
			callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "onRequestSetPin");
			TRACE.d("onRequestSetPin()===");
			dismissDialog();
			dialog = new Dialog(cordova.getActivity());
			dialog.setContentView(R.layout.pin_dialog);
			dialog.setTitle("Please enter PIN");
			dialog.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String pin = ((EditText) dialog.findViewById(R.id.pinEditText)).getText().toString();
					if (pin.length() >= 4 && pin.length() <= 12) {
						pos.sendPin(pin);
						dismissDialog();
					}
				}
			});

			dialog.findViewById(R.id.bypassButton).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pos.sendPin("");
					dismissDialog();
				}
			});

			dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pos.cancelPin();
					dismissDialog();
				}
			});
			dialog.show();
		}

		public void dismissDialog() {
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
		}

		@Override
		public void onRequestTime() {
			TRACE.d("onRequestTime");
			pos.sendTime(terminalTime);
			TRACE.d("request_terminal_time:" + " " + terminalTime);
		}

		@Override
		public void onRequestBatchData(String arg0) {
			if (arg0 != null) {
				callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "onRequestBatchData: " + arg0);
			} else {
				callbackKeepResult(PluginResult.Status.ERROR, true, "doTrade", "");
			}
		}

		@Override
		public void onRequestTransactionResult(TransactionResult arg0) {
			TRACE.d("onRequestTransactionResult");
			String message = "";
			if (arg0 == TransactionResult.APPROVED) {
				TRACE.d("TransactionResult.APPROVED");
				message = "transaction_approved" + "\n" + "amount" + ": $" + amount + "\n";
				if (!cashbackAmount.equals("")) {
					message += "cashbackAmount" + ": " + cashbackAmount;
				}
				callbackKeepResult(PluginResult.Status.OK, true, "doTrade", message);
				return ;
			} else if (arg0 == TransactionResult.TERMINATED) {
				message = "TERMINATED";
				TRACE.d("TERMINATED");
				callbackKeepResult(PluginResult.Status.ERROR, true, "doTrade", message);
				return ;
			} else if (arg0 == TransactionResult.DECLINED) {
				message = "DECLINED";
				TRACE.d("DECLINED");
				callbackKeepResult(PluginResult.Status.ERROR, true, "doTrade", message);
				return ;
			} else if (arg0 == TransactionResult.CANCEL) {
				message = "CANCEL";
				TRACE.d("CANCEL");
				callbackKeepResult(PluginResult.Status.ERROR, true, "doTrade", message);
				return ;
			} else if (arg0 == TransactionResult.CAPK_FAIL) {
				message = "CAPK_FAIL";
				TRACE.d("CAPK_FAIL");
				callbackKeepResult(PluginResult.Status.ERROR, true, "doTrade", message);
				return ;
			} else if (arg0 == TransactionResult.NOT_ICC) {
				message = "NOT_ICC";
				TRACE.d("NOT_ICC");
				callbackKeepResult(PluginResult.Status.ERROR, true, "doTrade", message);
				return ;
			} else if (arg0 == TransactionResult.SELECT_APP_FAIL) {
				message = "SELECT_APP_FAIL";
				TRACE.d("SELECT_APP_FAIL");
				callbackKeepResult(PluginResult.Status.ERROR, true, "doTrade", message);
				return ;
			} else if (arg0 == TransactionResult.DEVICE_ERROR) {
				message = "DEVICE_ERROR";
				TRACE.d("DEVICE_ERROR");
				callbackKeepResult(PluginResult.Status.ERROR, true, "doTrade", message);
				return ;
			} else if (arg0 == TransactionResult.TRADE_LOG_FULL) {
				TRACE.d("pls clear the trace log and then to begin do trade");
			} else if (arg0 == TransactionResult.CARD_NOT_SUPPORTED) {
				message = "CARD_NOT_SUPPORTED";
				TRACE.d("CARD_NOT_SUPPORTED");
			} else if (arg0 == TransactionResult.MISSING_MANDATORY_DATA) {
				TRACE.d("MISSING_MANDATORY_DATA");
				message = "MISSING_MANDATORY_DATA";
			} else if (arg0 == TransactionResult.CARD_BLOCKED_OR_NO_EMV_APPS) {
				message = "CARD_BLOCKED_OR_NO_EMV_APPS";
				TRACE.d("CARD_BLOCKED_OR_NO_EMV_APPS");
			} else if (arg0 == TransactionResult.INVALID_ICC_DATA) {
				message = "INVALID_ICC_DATA";
				TRACE.d("INVALID_ICC_DATA");
			} else if (arg0 == TransactionResult.FALLBACK) {
				message = "FALLBACK";
				TRACE.d("FALLBACK");
			} else if (arg0 == TransactionResult.NFC_TERMINATED) {
				message = "NFC_TERMINATED";
				TRACE.d("NFC_TERMINATED");
			} else if (arg0 == TransactionResult.CARD_REMOVED) {
				message = "CARD_REMOVED";
				TRACE.d("CARD_REMOVED");
			}
			callbackKeepResult(PluginResult.Status.OK, true, "doTrade", message);
		}

		@Override
		public void onReturnCustomConfigResult(boolean arg0, String arg1) {
			// TODO Auto-generated method stub
			if (arg0) {
				callbackKeepResult(PluginResult.Status.OK, false, "updateEMVConfigByXml", "onReturnCustomConfigResult: success");
			} else {
				callbackKeepResult(PluginResult.Status.ERROR, false, "updateEMVConfigByXml", "onReturnCustomConfigResult: " + arg1);
			}
		}

		@Override
		public void onReturnDoInputCustomStr(boolean arg0, String arg1, String initiator) {
			// TODO Auto-generated method stub
			if (arg0) {
				arg1 = QPOSUtil.convertHexToString(arg1);
				callbackKeepResult(PluginResult.Status.OK, false, "customInputDisplay", "success" + "\n" + arg1 + "\n" + initiator);
			} else {
				callbackKeepResult(PluginResult.Status.ERROR, false, "customInputDisplay", "fail" + "\n" + arg1 + "\n" + initiator);
			}
		}

		@Override
		public void onUpdatePosFirmwareResult(UpdateInformationResult arg0) {
			if (arg0 != UpdateInformationResult.UPDATE_SUCCESS) {
				updateThread.concelSelf();
			}
			String message = null;
			if (arg0 == null) {
				return;
			} else if (arg0 == UpdateInformationResult.UPDATE_FAIL) {
				TRACE.d("update fail");
				message = "update fail";
			} else if (arg0 == UpdateInformationResult.UPDATE_SUCCESS) {
				TRACE.d("update success");
				message = "update success";
			} else if (arg0 == UpdateInformationResult.UPDATE_PACKET_VEFIRY_ERROR) {
				TRACE.d("update packet error");
				message = "update packet error";
			}
			callbackKeepResult(PluginResult.Status.OK, false, "updatePosFirmware", message);
		}

		@Override
		public void onReturnSetMasterKeyResult(boolean arg0) {
			// TODO Auto-generated method stub
			if (arg0) {
				callbackKeepResult(PluginResult.Status.OK, false, "setMasterKey", "onReturnSetMasterKeyResult: success");
			} else {
				callbackKeepResult(PluginResult.Status.OK, false, "setMasterKey", "onReturnSetMasterKeyResult: fail");
			}

		}

		@Override
		public void onReturnUpdateIPEKResult(boolean arg0) {
			// TODO Auto-generated method stub
			if (arg0) {
				callbackKeepResult(PluginResult.Status.OK, false, "updateIPEK", "onReturnUpdateIPEKResult: success");
			} else {
				callbackKeepResult(PluginResult.Status.OK, false, "updateIPEK", "onReturnUpdateIPEKResult: fail");
			}
		}

		@Override
		public void onRequestSignatureResult(byte[] arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEmvICCExceptionData(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetCardNoResult(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPosComm(int arg0, String arg1, String arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetShutDownTime(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetSleepModeTime(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLcdShowCustomDisplay(boolean arg0) {
			// TODO Auto-generated method stub

		}


		@Override
		public void onPinKey_TDES_Result(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onQposDoGetTradeLog(String arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestDevice() {

		}

		@Override
		public void onGetKeyCheckValue(List<String> list) {

		}

		@Override
		public void onGetDevicePubKey(String s) {

		}


		@Override
		public void onTradeCancelled() {

		}

		@Override
		public void onQposIsCardExistInOnlineProcess(boolean b) {

		}

		@Override
		public void onReturnSetConnectedShutDownTimeResult(boolean b) {

		}

		@Override
		public void onReturnGetConnectedShutDownTimeResult(String s) {

		}

		@Override
		public void onQposDoGetTradeLogNum(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onQposDoSetRsaPublicKey(boolean arg0) {
			// TODO Auto-generated method stub

		}


		@Override
		public void onQposCertificateInfoResult(List<String> list) {

		}

		@Override
		public void onQposIsCardExist(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onQposKsnResult(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestUpdateKey(String arg0) {

		}

		@Override
		public void onRequestFinalConfirm() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestUpdateWorkKeyResult(UpdateInformationResult arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestSendTR31KeyResult(boolean b) {

		}

		@Override
		public void onQposRequestPinResult(List<String> list, int i) {

			callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "onQposRequestPinResult");
			TRACE.d("POSTION:"+position);

		}
		@Override
		public void onReturnD20SleepTimeResult(boolean b) {

		}

		@Override
		public void onQposRequestPinStartResult(List<String> list) {

		}

		@Override
		public void onQposPinMapSyncResult(boolean b, boolean b1) {

		}

		@Override
		public void onReturnRsaResult(String s) {

		}

		@Override
		public void onQposInitModeResult(boolean b) {

		}

		@Override
		public void onD20StatusResult(String s) {

		}

		@Override
		public void onQposTestSelfCommandResult(boolean b, String s) {

		}

		@Override
		public void onQposTestCommandResult(boolean b, String s) {

		}

		@Override
		public void onReturnApduResult(boolean arg0, String arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnBatchSendAPDUResult(LinkedHashMap<Integer, String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRetuenGetTR31Token(String s) {

		}

		@Override
		public void onReturnDownloadRsaPublicKey(HashMap<String, String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnGetEMVListResult(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnGetCustomEMVListResult(Map<String, String> map) {

		}

		@Override
		public void onReturnGetPinResult(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnGetQuickEmvResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnNFCApduResult(boolean arg0, String arg1, int arg2) {
			// TODO Auto-generated method stub
			TRACE.d("onReturnNFCApduResult(boolean arg0, String arg1, int arg2):" + arg0 + "\n" + arg1 + "\n" + arg2);
			if(arg0){
				callbackKeepResult(PluginResult.Status.OK, false, "sendApduByNFC", "success"+ "\n" + arg1 + "\n" + arg2 );
			}else {
				callbackKeepResult(PluginResult.Status.ERROR, false, "sendApduByNFC", "fail"+ "\n" + arg1 + "\n" + arg2);
			}
		}

		@Override
		public void onReturnPowerOffIccResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnPowerOffNFCResult(boolean arg0) {
			// TODO Auto-generated method stub
			TRACE.d(" onReturnPowerOffNFCResult(boolean arg0) :" + arg0);
			if(arg0){
				callbackKeepResult(PluginResult.Status.OK, false, "powerOffNFC", "success" );
			}else {
				callbackKeepResult(PluginResult.Status.ERROR, false, "powerOffNFC", "fail");
			}
		}

		@Override
		public void onReturnPowerOnIccResult(boolean arg0, String arg1, String arg2, int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnPowerOnNFCResult(boolean arg0, String arg1, String arg2, int arg3) {
			// TODO Auto-generated method stub
			TRACE.d("onReturnPowerOnNFCResult(boolean arg0, String arg1, String arg2, int arg3):" + arg0 + "\n" + arg1 + "\n" + arg2 + "\n" + arg3);
			if(arg0){
				callbackKeepResult(PluginResult.Status.OK, false, "powerOnNFC", "success" + "\n" + arg1 + "\n" + arg2 + "\n" + arg3);
			}else {
				callbackKeepResult(PluginResult.Status.ERROR, false, "powerOnNFC", "fail" + "\n" + arg1 + "\n" + arg2 + "\n" + arg3);
			}
		}

		@Override
		public void onReturnReversalData(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnGetPinInputResult(int i) {
			TRACE.d("pin input amount:"+i);
			callbackKeepResult(PluginResult.Status.OK, true, "doTrade", "Num:"+i);
		}

		@Override
		public void onReturnGetKeyBoardInputResult(String s) {

		}

		@Override
		public void onReturnSetSleepTimeResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnUpdateEMVRIDResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnUpdateEMVResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnRSAResult(String s) {

		}

		@Override
		public void onReturniccCashBack(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSearchMifareCardResult(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub
			String content;
			if (arg0 != null) {
				TRACE.d("onSearchMifareCardResult(Hashtable<String, String> arg0):" + arg0.toString());

				String statuString = arg0.get("status");
				String cardTypeString = arg0.get("cardType");
				String cardUidLen = arg0.get("cardUidLen");
				String cardUid = arg0.get("cardUid");
				String cardAtsLen = arg0.get("cardAtsLen");
				String cardAts = arg0.get("cardAts");
				String ATQA = arg0.get("ATQA");
				String SAK = arg0.get("SAK");
				content = "statuString:" + statuString + "\n" + "cardTypeString:" + cardTypeString + "\ncardUidLen:" + cardUidLen
						+ "\ncardUid:" + cardUid + "\ncardAtsLen:" + cardAtsLen + "\ncardAts:" + cardAts
						+ "\nATQA:" + ATQA + "\nSAK:" + SAK;
			} else {
				content = "poll on failed";
			}
			callbackKeepResult(PluginResult.Status.OK, true, "pollOnMifareCard", content);
		}

		@Override
		public void onFinishMifareCardResult(boolean arg0) {
			// TODO Auto-generated method stub
			TRACE.d("onFinishMifareCardResult(boolean arg0):" + arg0);

			if (arg0) {
				callbackKeepResult(PluginResult.Status.OK, true, "finishMifareCard", "success");
			} else {
				callbackKeepResult(PluginResult.Status.OK, true, "finishMifareCard", "fail");
			}
		}

		@Override
		public void onBatchReadMifareCardResult(String s, Hashtable<String, List<String>> hashtable) {

		}

		@Override
		public void onBatchWriteMifareCardResult(String s, Hashtable<String, List<String>> hashtable) {

		}

		@Override
		public void onSetBuzzerResult(boolean arg0) {
			// TODO Auto-generated method stub
			if (arg0) {
				callbackKeepResult(PluginResult.Status.OK, true, "doSetBuzzerOperation", "doSetBuzzerOperation:success");
			} else {
				callbackKeepResult(PluginResult.Status.OK, true, "doSetBuzzerOperation", "doSetBuzzerOperation:fail");
			}
		}

		@Override
		public void onSetBuzzerTimeResult(boolean b) {

		}

		@Override
		public void onSetBuzzerStatusResult(boolean b) {

		}

		@Override
		public void onGetBuzzerStatusResult(String s) {

		}

		@Override
		public void onSetManagementKey(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSetParamsResult(boolean arg0, Hashtable<String, Object> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSetVendorIDResult(boolean b, Hashtable<String, Object> hashtable) {

		}

		@Override
		public void onSetSleepModeTime(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpdateMasterKeyResult(boolean arg0, Hashtable<String, String> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onVerifyMifareCardResult(boolean arg0) {
			// TODO Auto-generated method stub
			String re ;
			if(arg0) re = "success";
			else re = "fail";
			callbackKeepResult(PluginResult.Status.OK, true, "authenticateMifareCard", re);
		}

		@Override
		public void onReadMifareCardResult(Hashtable<String, String> arg0) {
			String content;
			if (arg0 != null) {
				TRACE.d("onReadMifareCardResult(Hashtable<String, String> arg0):" + arg0.toString());
				String addr = arg0.get("addr");
				String cardDataLen = arg0.get("cardDataLen");
				String cardData = arg0.get("cardData");
				content = "addr:" + addr + "\ncardDataLen:" + cardDataLen + "\ncardData:" + cardData;
			} else {
				content = "onReadWriteMifareCardResult fail";
			}
			callbackKeepResult(PluginResult.Status.OK, true, "readMifareCard", content);

		}


		@Override
		public void onWaitingforData(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onWriteBusinessCardResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onWriteMifareCardResult(boolean arg0) {
			// TODO Auto-generated method stub
			String re;
			if (arg0) re = "success";
			else re = "fail";
			callbackKeepResult(PluginResult.Status.OK, true, "writeMifareCard","writeMifareCard: "+ re);
		}

		@Override
		public void onOperateMifareCardResult(Hashtable<String, String> arg0) {
			String content;
			if (arg0 != null) {
				TRACE.d("onOperateMifareCardResult(Hashtable<String, String> arg0):" + arg0.toString());
				String cmd = arg0.get("Cmd");
				String blockAddr = arg0.get("blockAddr");
				content ="Cmd:" + cmd + "\nBlock Addr:" + blockAddr;
			} else {
				content = "operate failed";
			}
			callbackKeepResult(PluginResult.Status.OK, true, "operateMifareCardData","operateMifareCardData: "+ content);
		}

		@Override
		public void getMifareFastReadData(Hashtable<String, String> arg0) {
			String content;
			if (arg0 != null) {
				TRACE.d("getMifareFastReadData(Hashtable<String, String> arg0):" + arg0.toString());
				String startAddr = arg0.get("startAddr");
				String endAddr = arg0.get("endAddr");
				String dataLen = arg0.get("dataLen");
				String cardData = arg0.get("cardData");
				content = "startAddr:" + startAddr + "\nendAddr:" + endAddr + "\ndataLen:" + dataLen
						+ "\ncardData:" + cardData;
			} else {
				content = "read fast UL failed";
			}
			callbackKeepResult(PluginResult.Status.OK, true, "fastReadMifareCardData","fastReadMifareCardData: "+ content);

		}

		@Override
		public void transferMifareData(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void verifyMifareULData(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void writeMifareULData(String arg0) {
			// TODO Auto-generated method stub
			String content;
			if (arg0 != null) {
				TRACE.d("writeMifareULData(String arg0):" + arg0.toString());

				content="addr:" + arg0;
			} else {
				content="write UL failed";
			}
			callbackKeepResult(PluginResult.Status.OK, true, "writeMifareCard","writeMifareCard: "+ content);
		}

		@Override
		public void getMifareReadData(Hashtable<String, String> arg0) {
			String content;
			if (arg0 != null) {
				TRACE.d("getMifareReadData(Hashtable<String, String> arg0):" + arg0.toString());

				String blockAddr = arg0.get("blockAddr");
				String dataLen = arg0.get("dataLen");
				String cardData = arg0.get("cardData");
				content="blockAddr:" + blockAddr + "\ndataLen:" + dataLen + "\ncardData:" + cardData;
			} else {
				content="read mafire UL failed";
			}
			callbackKeepResult(PluginResult.Status.OK, true, "readMifareCard","readMifareCard: "+ content);
		}

	}
}
