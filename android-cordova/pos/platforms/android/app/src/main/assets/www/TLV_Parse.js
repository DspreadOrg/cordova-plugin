
//十六进制字符串转字节数组，跟网上demo一样
function HexString2Bytes(str) {
    var pos = 0;
    var len = str.length;
    if (len % 2 != 0) {
      return null;
    }
    len /= 2;
    var arrBytes = new Array();
    for (var i = 0; i < len; i++) {
      var s = str.substr(pos, 2);
      var v = parseInt(s, 16);
      arrBytes.push(v);
      pos += 2;
    }
    return arrBytes;
  }
  
  //字节数组转十六进制字符串，对负值填坑
  function Bytes2HexString(arrBytes) {
    var str = "";
    for (var i = 0; i < arrBytes.length; i++) {
      var tmp;
      var num=arrBytes[i];
      if (num < 0) {
      //此处填坑，当byte因为符合位导致数值为负时候，需要对数据进行处理
        tmp =(255+num+1).toString(16);
      } else {
        tmp = num.toString(16);
      }
      if (tmp.length == 1) {
        tmp = "0" + tmp;
      }
      str += tmp;
    }
    return str;
  }

/**
   * 字节数组转十六进制字符串
   * [-112, 49, 50, 51, 52, 53, 54, 55, 56] 转换 903132333435363738
   * @param {Array} arr 符合16进制数组
   */
 function Bytes2Str(arr) {
  var str = "";
  for (var i = 0; i < arr.length; i++) {
    var tmp;
    var num=arr[i];
    if (num < 0) {
    //此处填坑，当byte因为符合位导致数值为负时候，需要对数据进行处理
      tmp =(255+num+1).toString(16);
    } else {
      tmp = num.toString(16);
    }
    if (tmp.length == 1) {
      tmp = "0" + tmp;
    }
    str += tmp;
  }
  return str;
}


function parseTLV(tlvStr){
  var tlvData = HexString2Bytes(tlvStr);
  // console.log("parseTLV: " + tlvData);
  var dict = {};
   for(var i=0; i<tlvData.length;){
    if ( (tlvData[i]&0x20) != 0x20)//单一结构
    {
      //console.log("单一结构");
      var tagLength = 0;
      if ( (tlvData[i]&0x1F) == 0x1F)//tag两字节
      {
        tagLength = 2;
        //console.log("tag两字节: " + tlvData[i]);
       //解析length域
       //解析value域
      }
      else//tag单字节
      {
        //console.log("tag单字节: " + tlvData[i]);
        tagLength = 1
      //解析length域
      //解析value域
      }
      var tag = Bytes2Str(getBytes(tlvData,i,tagLength));
      i += tagLength;
      var valueLength;
      var valueLengthLen;
      if ( (tlvData[i]&0x80) != 0x80)//length长度为1字节
      {
          valueLength = tlvData[i];
          valueLengthLen = 1;
      }
      else //length长度为多字节
      {
          valueLengthLen = tlvData[i] & 0x7f;
         // console.log("tlvData[i]: " + tlvData[i] + "valueLengthLen: " + valueLengthLen);
          i++;
          var valueLenByte = getBytes(tlvData,i,valueLengthLen);
          valueLength = byteArrayToInt(valueLenByte);
      }
      
      i+=valueLengthLen;
      var value = Bytes2Str(getBytes(tlvData,i,valueLength));
      i+=valueLength;
      //console.log("tag: " + tag + " length: " + valueLength + " value: " + value);
      dict[tag] = value;
    }
    else//复合结构
    {
      //console.log("复合结构: " + tlvData[i]);
      var tagLength = 1;
      i += tagLength;
      var valueLength;
      var valueLengthLen;
      if ( (tlvData[i]&0x80) != 0x80)//length长度为1字节
      {
          valueLength = tlvData[i];
          valueLengthLen = 1;
      }
      else //length长度为多字节
      {
          valueLengthLen = tlvData[i] & 0x7f;
         // console.log("tlvData[i]: " + tlvData[i] + "valueLengthLen: " + valueLengthLen);
          i++;
          var valueLenByte = getBytes(tlvData,i,valueLengthLen);
          valueLength = byteArrayToInt(valueLenByte);
      }
      
      i+= valueLengthLen;
      var value = Bytes2Str(getBytes(tlvData,i,valueLength));
      i+=valueLength;
      //console.log("tag: " + tag + " length: " + valueLength + " value: " + value);
      parseTLV(HexString2Bytes(value));
    }
   }
   return dict;
}

function searchTLV(tlvStr, tagArr){
  var tlvDict = parseTLV(tlvStr);
  var tagDict = {};
  for(var key in tlvDict){
      if(tagArr.indexOf(key) > -1){
         tagDict[key] = tlvDict[key];
      }
  }
  return tagDict; 
}

function getBytes(tlvData,offset,len){
   
    var newTlvData = new Uint8Array(len);
    for(var k = 0; k < len; k++){
        newTlvData[k] = tlvData[offset+k];
    }
    //console.log("newTlvData: " + newTlvData);
  return newTlvData;
}

function byteArrayToInt(b) {
  var result = 0;
  for (var i = 0; i < b.length; i++) {
      result <<= 8;
      result |= (b[i] & 0xff); 
  }
  return result;
}

var tagDict = searchTLV("4F08A000000333010101500A50424F4320444542495482027C008407A00000033301018E0C000000000000000042031E039505008804E0009A032104089B02E8009C01005F24032710315F25031710275F280201565F2A0201565F300202205F3401009F01060012345678909F02060000000000129F03060000000000009F0607A00000033301019F0702FF009F0902008C9F0D05D86004A8009F0E0500109800009F0F05D86804F8009F10130701010360BC06010A0100000000008A19844F9F120E434D4220444542495420434152449F160F4243544553543132333435363738399F1A0203569F1C084E4C2D47503733309F1E0838333230314943439F21031255249F260853E203DA54DC8D179F2701409F3303E0F8C89F34034203009F3501229F360202D09F3704573EE5AD9F3901059F40057000B0A0019F4104000000149F4E04616263649F110101C408621483FFFFFF4663C00A00120010700247E0000CC28201808F95594927B3CEA75727E1994817183263BBCB87EBC3A764F6BFFE049BB05C3186F42BF9C7424CB561F19103D43BD84807EC089C6B88DFD6247E484D2CDEBC2D1D16A70ACACE697FCC9844BF86E3FFA79373C31416677787B3EE80CD42ACB42A8565077852B70BC691100B0D4764684A19F2A376D20F4C1BD35ED60E01B5A8584F3FEC2EF45EB5371FA812DA379E20CC21F296A3CCA59F3445167C80CCD07339B1D0E02584B4558557C1EBFA684D2CE8A210128EACB923C49FF044CBBB12700F90E130B08B56C7F4B84B1EC376196D02C58B42432E24FFE9B5696E657DBDCC79EC14819B2AC9FD97EA577B1700ED4404EA4619D68231A129552CEDED2B16702E55929FAA15C42218D11B4467C3571AF7C5A109E5F65743C322F3D44E1410368F041A49113B2D264B8DBE29E3A867B5E51DA5B58BA392938D8E75E2FC01F01BAB956C7766BCE5EB51A0F3DC107E1CB1944F5272CD286C2DFFBFBFFBE3EC5D696C4B3C75D5E300D8459E2C71ED6A54B00B23ABACF673631B827083AF4DBD0F2487D010A7924076FD47B7FC875627803CF0B102", ["5f24","9f09"]);
for(var key in tagDict){
      console.log("key: " + key + "value: " + tagDict[key]);
}