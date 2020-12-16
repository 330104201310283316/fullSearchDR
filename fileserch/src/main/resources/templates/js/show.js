var Url = "http://192.168.3.19:8081"

$(document).ready(function(){
    var path= GetQueryString('file');
    var newPath = decodeURIComponent(atob(path))
    queryUrl(newPath);

    $(document).keydown(function(event){
    　　if(event.keyCode==13){
    　　	goDefault()
    　　}
    });
})

function GetQueryString(name) { 
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
	var r = window.location.search.substr(1).match(reg);
	var context = ""; 
	if (r != null) 
		context = r[2]; 
		reg = null; 
		r = null; 
	return context == null || context == "" || context == "undefined" ? "" : context; 
}

const iframe = document.getElementById("pdf");

function queryUrl(newPath){
    $(".loading").css("display","block");
	$.ajax({
		type: "GET", 
		dataType:"json",
		url: Url+"/file/findFileToHtmlUrl?filePath="+encodeURIComponent(newPath),
		success: function(data){
			if(data && data.state == 200){
                $(".loading").css("display","none");
				fileUrl = data.data.fileUrl;
                // console.log(fileUrl,'fileUrl')
				//fileUrl = 'http://192.168.3.158:8080/group/121212.html';
				iframe.setAttribute("src",fileUrl);
			}else{
                $('.loading-img').attr("src","img/fail.png");
                $('.loading-text').html('请求超时')
            }
		}			
	})
}

var getIFrameSet = function(iobj){
	let frame = $(iobj).contents().find('frameset');
	if(frame.length === 0){
		return null;
	}
	return frame[0];
}


var getIFrame = function(iobj){
	let frame = $(iobj).contents().find('frame[name=frSheet]');
	if(frame.length === 0){
		return iobj
	}
	return frame[0];
}

var getIFrameDoc = function(iobj){
	return iobj.contentDocument || iobj.contentWindow.document;
}
let x = '';
let y = '';
let _x = '';
let _y = '';
let selectText = '';
iframe.onload = function () {
	const frame = getIFrame(iframe);
	addListener(frame);
	iFrameHeight();
	if(frame !== iframe){
		
		frame.onload = function () {
			//console.log(11111);
			addListener(frame);
			
		}
		frame.onunload = function(){
			//console.log(2222);
			removeListener(frame);
		}
	}
};

iframe.onunload = function(){
	removeListener(iframe);
};
function removeListener(frame){
	let contentDocument = getIFrameDoc(frame);
	contentDocument.removeEventListener("mousedown");
	contentDocument.removeEventListener("mouseup");
}
function addListener(frame){
	let contentDocument = getIFrameDoc(frame);
	//console.log(frame);
	// 鼠标点击监听
	contentDocument.addEventListener('mousedown', function (e) {
	  x = e.pageX;
	  y = e.pageY;
	}, true);
	//鼠标抬起监听
	contentDocument.addEventListener('mouseup', function (e) {
	  _x = e.pageX;
	  _y = e.pageY;
	  if (x == _x && y == _y) {
		  document.getElementsByClassName("s-search")[0].setAttribute("style",
		  'display:none;');
		  return;
	  }
	  
	  const selectionPosition = getSelectionTopLeft(frame);
      
	  const rect = iframe.getBoundingClientRect();
      
      const rx = rect.x || rect.left;
      const ry = rect.y || rect.top;
	  const left = selectionPosition.x+(selectionPosition.width)/2 +rx;
	  const top = selectionPosition.y+ry+window.pageYOffset - 10;
      
	  document.getElementsByClassName("s-search")[0].setAttribute("style",
	  // `left:${left}px;top:${top}px;display:block;`);
      "left:"+left+"px;top:"+top+"px;display:block;");
	  
	  const selection = frame.contentWindow.getSelection();
	  var choose = selection.toString();
	  selectText = choose;
	}, true);
}


function getSelectionTopLeft(iframe) {
	var x = 0, y = 0;
	// Use standards-based method only if Range has getBoundingClientRect
	if (iframe.contentWindow.getSelection && iframe.contentDocument.createRange
	&& typeof iframe.contentDocument.createRange().getBoundingClientRect != "undefined") {
		var sel = iframe.contentWindow.getSelection();
		if (sel.rangeCount > 0) {
			var rect = sel.getRangeAt(0).getBoundingClientRect();
			x = rect.left;
			y = rect.top;
			width = rect.width;
			height = rect.height
		}
	} else if (iframe.contentDocument.selection && iframe.contentDocument.selection.type != "Control") {
		// All versions of IE
		var textRange = iframe.contentDocument.selection.createRange();
		x = textRange.boundingLeft;
		y = textRange.boundingTop;
		width = textRange.boundingWidth;
		height = textRange.boundingHeight;
	}
	return { x: x, y: y,width:width,height:height };
} 

function goSearch(){
	if(selectText !== ''){
		window.open("default.html?key="+selectText);
	}
}	
	
function goDefault(){
	var search = $("#search").val(); 
	window.open("default.html?key="+search);
}	

function copy(){
	ncopy(selectText);
}

function ncopy(content, message) {
    var aux = document.createElement("input"); 
    aux.setAttribute("value", content); 
    document.body.appendChild(aux); 
    aux.select();
    document.execCommand("copy"); 
    document.body.removeChild(aux);
    if (message == null) {
        alert("复制成功");
    } else{
        alert(message);
    }
}

function iFrameHeight() {
	var ifm= document.getElementById("pdf");
		//ifm.style.display="block";
	var subIframe = getIFrame(ifm);
	var subWeb = getIFrameDoc(subIframe);
    //console.log(subWeb.body.scrollHeight);
	if(ifm != null && subWeb != null) {
		ifm.style.height = 'auto';
		ifm.style.display = 'block';
		//subIframe.setAttribute("scrolling","no");
		ifm.style.height = (subWeb.body.scrollHeight+100)+'px';
    }
 };