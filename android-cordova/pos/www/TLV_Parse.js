function textToArrayBuffer(s) {
  var i = s.length;
  var n = 0;
  var ba = new Array()
  for (var j = 0; j < i;) {
    // var c = s.codePointAt(j);
    var tag = s.substring(j, j + 2);
    var c = parseInt(tag,16);
    if (c < 128) {
      ba[n++] = c;
      j+=2;
    }
    else if ((c > 127) && (c < 2048)) {
      ba[n++] = (c >> 6) | 192;
      ba[n++] = (c & 63) | 128;
      j++;
    }
    else if ((c > 2047) && (c < 65536)) {
      ba[n++] = (c >> 12) | 224;
      ba[n++] = ((c >> 6) & 63) | 128;
      ba[n++] = (c & 63) | 128;
      j+=2;
    }
    else {
      ba[n++] = (c >> 18) | 240;
      ba[n++] = ((c >> 12) & 63) | 128;
      ba[n++] = ((c >> 6) & 63) | 128;
      ba[n++] = (c & 63) | 128;
      j+=2;
    }
  }
  
  return new Uint8Array(ba);
}
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
  console.log("parseTLV: " + tlvData);
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
      
      i+= valueLengthLen;
      var value = Bytes2Str(getBytes(tlvData,i,valueLength));
      i+=valueLength;
      console.log("tag: " + tag + " length: " + valueLength + " value: " + value);
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
      console.log("tag: " + tag + " length: " + valueLength + " value: " + value);
      parseTLV(HexString2Bytes(value));
    }
   }
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