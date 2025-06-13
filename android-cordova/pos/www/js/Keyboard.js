/**
 * positionAll is the keyboard position,use getPosition() to get.
 * Need to multiple window.devicePixelRatio to get physical pixel, add 36(Status bar height) to get precise location.
**/

//Generate random number
function a(){
    var l = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
    var k = [];
 
    var lenl = 10;
    for(var i=0; i<10; i++){
        var index = Math.floor(Math.random() * lenl);
        k[i] = l[index];
        l[index] = l[lenl - 1];
        lenl -= 1;
    }
    
    console.log("k"+k);
    return k;
}

//Generate number
function a2(){
    var l = [1, 2, 3, 4, 5, 6, 7, 8, 9, 0];
    return l;
}

function setLayout(numberBox,height,value){
    numberBox.innerHTML = value;
    if(value == 'Cancel' || value == 'Del' || value == 'Confirm'){
        numberBox.id = value;
    } else {
        numberBox.id = '9';
    }
    numberBox.className = 'numberkey color';
    numberBox.style.height = height;
    numberBox.style.width = '82px';

    numberBox.style.backgroundColor = '#FFFFFF';
    numberBox.style.cssFloat = 'left';

    numberBox.style.marginRight = '9px';
    numberBox.style.marginTop = '8px';
    numberBox.style.lineHeight = '40px';
    numberBox.style.textAlign = 'center';
    numberBox.style.fontSize = '19px';
    numberBox.style.color = '#333333';
    //'#f7fcff';
    if(value == 'Cancel' )
    {
        //numberBox.style.marginRight = '46px';
        numberBox.style.marginLeft = '91px';

        numberBox.style.marginTop = '-39px';
        //numberBox.top = "-12px";
    }

}
var positionAll = "";
var ob;
function miniNumberKeyboard () {
    positionAll = "";
    var keyBoard = document.getElementById("keyBoard");
    // 确保先清除所有子元素
    while (keyBoard.firstChild) {
        keyBoard.removeChild(keyBoard.firstChild);
    }
    var div = document.createElement('div');
    div.id = 'div';
    keyBoard.appendChild(div);
    div.style.backgroundColor = '#E5E5E5';
    div.style.width = "100%";
    div.style.height = '280px';
    div.style.borderRadius = '15px';
   // div.style.paddingRight = '20px';
    div.style.position = //'absolute';
    'relative';
    var inputBox = document.createElement('div');
    inputBox.style.backgroundColor = '#FFFFFF';
    //'#353945';
    //document.body.appendChild(inputBox);
    div.appendChild(inputBox);
    inputBox.id = 'inputBox';
    inputBox.innerHTML = '';
    inputBox.style.position = 'absolute';
    inputBox.style.top = '5px';
    inputBox.style.left = '27px';
    inputBox.style.width = '264px';
    inputBox.style.height = '40px';
    inputBox.style.lineHeight = '45px';
    inputBox.style.fontSize = '20px';
    inputBox.style.color = '#333333';
    //'#f7fcff';
    //inputBox.style.paddingLeft = '1%';

    var number = document.createElement('div');
    number.style.width = '100%'; //'270px';
    number.style.height = '190px';
    number.style.position = 'absolute';
    number.style.top = '46px';
    number.style.left = '27px';
    number.style.backgroundColor = '#E5E5E5';
    //.style.color = '#333333';
    number.id = 'number';
   // document.body.appendChild(number);
    div.appendChild(number);
    var Box = document.getElementById('number');
    var str = a2();
    for (var i = 0; i < 9; i++) {
        var numberBox = document.createElement('div');
        numberBox.innerHTML = str[i];
        numberBox.id = i;
        numberBox.className = 'numberkey color';
        numberBox.style.height = '38px';
        numberBox.style.width = '82px';

        numberBox.style.backgroundColor = '#FFFFFF';
        numberBox.style.cssFloat = 'left';
        numberBox.style.marginRight = '9px';
        numberBox.style.marginTop = '8px';
        numberBox.style.lineHeight = '40px';
        numberBox.style.textAlign = 'center';
        numberBox.style.fontSize = '19px';
        numberBox.style.color = '#333333';
        Box.appendChild(numberBox);
        ob = document.getElementById(i).getBoundingClientRect();
        positionAll = positionAll + "000"+document.getElementById(i).innerHTML + divert(ob.left*window.devicePixelRatio) + divert(ob.top*window.devicePixelRatio+36) + divert(ob.right*window.devicePixelRatio) + divert(ob.bottom*window.devicePixelRatio+36);
    };
    var delBox = document.createElement('div');
    // var cancelBox = document.createElement('div');
    var cancelBox = document.createElement('div');
    // var numberLBox = document.createElement('div');
    var containBox = document.createElement('div');
    containBox.height = '100px';
    containBox.width = '40px';
    for(var i = 0 ; i < 2; i ++){
        var containChildBox = document.createElement('div');

        if(i == 0){
            setLayout(containChildBox,'38px',str[str.length -1]);
        }else{
            setLayout(containChildBox,'82px','Confirm');
        }
        containBox.appendChild(containChildBox);
    }


    setLayout(delBox,'82px','Del');

    setLayout(cancelBox,'38px','Cancel');

    Box.appendChild(delBox);
    // Box.appendChild(numberLBox);
    // Box.appendChild(cancelBox);
    Box.appendChild(containBox);
    Box.appendChild(cancelBox);
    ob = document.getElementById('9').getBoundingClientRect();
    positionAll = positionAll  + "000"+ document.getElementById('9').innerHTML+ divert(ob.left*window.devicePixelRatio) + divert(ob.top*window.devicePixelRatio+36) + divert(ob.right*window.devicePixelRatio) + divert(ob.bottom*window.devicePixelRatio+36);

    ob = document.getElementById('Confirm').getBoundingClientRect();
    positionAll = positionAll  + "000F" + divert(ob.left*window.devicePixelRatio) + divert(ob.top*window.devicePixelRatio+36) + divert(ob.right*window.devicePixelRatio) + divert(ob.bottom*window.devicePixelRatio+36) ;

    ob  = document.getElementById("Del").getBoundingClientRect();
    positionAll = positionAll  + "000E" + divert(ob.left*window.devicePixelRatio) + divert(ob.top*window.devicePixelRatio+36) + divert(ob.right*window.devicePixelRatio) + divert(ob.bottom*window.devicePixelRatio+36) ;

    ob = document.getElementById("Cancel").getBoundingClientRect();
    positionAll = positionAll  + "000D" + divert(ob.left*window.devicePixelRatio) + divert(ob.top*window.devicePixelRatio+36) + divert(ob.right*window.devicePixelRatio) + divert(ob.bottom*window.devicePixelRatio+36) ;


    //取消按钮的功能
    document.getElementById('Cancel').onclick = function () {
        keyBoard.removeChild(document.getElementById('div'));
}

    //取消按钮的移入移出
    var cancelBtn = document.getElementById('Cancel');
    cancelBtn.onmouseover = function over () {
        cancelBtn.style.backgroundColor = '#e75e61';
    }
    cancelBtn.onmouseout = function out() {
        cancelBtn.style.backgroundColor = '#454955';
    }

    //删除按钮
    document.getElementById('Del').onclick = function () {
        var length = document.getElementById('inputBox').innerHTML.length;
        inputBox.innerHTML = inputBox.innerHTML.substr(0, length - 1);
    }

    //删除按钮移入移出
    var delBtn = document.getElementById('Del');
    delBtn.onmouseover = function over () {
        delBtn.style.backgroundColor = '#e75e61';
    }
    delBtn.onmouseout = function out () {
        delBtn.style.backgroundColor = '#454955';
    }

    //确认按钮    
    document.getElementById('Confirm').onclick = function () {
        console.log("click confirm");
    }

    //确认按钮移入移出
    var trueBtn = document.getElementById('Confirm');
    trueBtn.onmouseover = function over () {
        trueBtn.style.backgroundColor = '#39bdb5';
    }
    trueBtn.onmouseout = function out () {
        trueBtn.style.backgroundColor = '#454955';
    }

    //按钮移入样式
    var mouseColor = document.querySelectorAll('.color');
        for (let i = 0; i < mouseColor.length; i++) {
            mouseColor[i].onmouseover = function () {
                mouseColor[i].style.backgroundColor = '#D4D6D8';
            };
            mouseColor[i].onmouseout = function () {
                mouseColor[i].style.backgroundColor = '#FFFFFF';
            }
        }
//    getPosition();
};


function divert (position){
    //console.log("original："+position);
    position = Math.round(position);
    var c = position.toString(16);
    //console.log(position);

    while(c.length < 4){
        c = "0"+c;
    }
    //console.log(c);
    return c;
}
function getPosition(){
    return positionAll;
}

function pinInput(i){
    console.log("amount==="+i);
    var inputInHTML = document.getElementById('inputBox').innerHTML;
    var newHTML = "";
    if(i == -1)
    {
        setTimeout("hideKeyboard()",100);
    } else {
        while(i>0){
            newHTML = '*' + newHTML;
            i--;
        }
        inputBox.innerHTML = newHTML;
    }

}

function hideKeyboard(){
    console.log("hide");
    var keyBoard = document.getElementById("keyBoard");
    keyBoard.removeChild(document.getElementById('div'));
}

