	var Url = "http://192.168.3.19:8081"
    
	$(document).ready(function(){
		var newkey= GetQueryString('key');
		var newSearch =decodeURIComponent(newkey)
		$("#search").attr("value",newSearch);
		if(newSearch && newSearch !=""){
			querylist()
		}
		
		$(".search-input").keydown(function(event){
		　　if(event.keyCode==13){
		　　	querylist()
		　　}
		});        
	});
	
	function querylist(){
		$(".list").empty();
		$("#total").empty();
		var search = $("#search").val(); 
		if(search ==""){
			alert('请输入关键字')
		}else{
			$.ajax({
				type: "GET", 
				dataType:"json",
				url: Url+"/file/findFile?searchContent="+encodeURIComponent(search),
				success: function(data){
					if(data && data.state == 200 && data.data.result.length >0){
						var rowCount = data.data.rowCount;
						$('#total').html(rowCount);	
						var list = '';
						$.each(data.data.result, function(commentIndex, comment){
							list +=	'<div class="list-box">'+
										'<img class="list-icon" src="'+comment.img+'" />'+
										'<p class="list-title" onclick="golist(\''+btoa(encodeURIComponent(comment.filePath))+'\',\''+ comment.isLock +'\')">'+comment.fileName+'</p>'+
										(comment.isLock !=1 ?
										'<div class="list-sub">'+comment.fileContent+'</div>'
										:'<div class="lock-warp"><img class="lcok-icon" src="/img/lcok.png"/><p class="lcok-text">文件已加密</p></div>')+
										'<p class="list-add">'+comment.filePath+'</p>'+
									'</div>'
						});
						$('.list').html(list);
					}else{
						var list = '';
						list += '<div class="no-page" id="no-page">'+
								'<img class="no-page-img" src="img/no-oncall.png" />'+
								'<p class="no-page-text">暂无搜到文档 ~ ~</p>'+
							'</div>	'	
							$('.list').html(list);	
							$('#total').html('0');	
					}		
				}			
			})
		}
	};
	
	
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
	
	function golist(path,isLock){
        window.spath=path;
		if(isLock == 0){
			window.open("show.html?file="+path)
		}else{
			$("#myModal").show();
            firstSubmit(path);
		}
	}	
	
	
    function firstSubmit(path){
        var spath1=decodeURIComponent(atob(path))
        $.post(Url+"/file/checkFilePwd",{filePath:spath1},function(res){
            if(res.state == 200){
                window.loaded=true;
            }else{
                window.location.href = "error.html";
            }
        });
    }
	
    function submitForm(){
        var spath1=decodeURIComponent(atob(window.spath))
        var passValue = $(".pass-input").val();
        if(passValue ==''){
            $('.error').html('密码不能为空')
        }else{
          $.post(Url+"/file/checkFilePwd",{filePath:spath1,pwd:passValue},function(data){
            	if(data && data.state == 200){
                    window.open("show.html?file="+spath);
                    $("#myModal").hide();
                    $(".pass-input").val("");
                    $('.error').html('')
                }else{
                    $('.error').html(data.msg)
                }
          });
        }
    }
    
    $(".pass-input").keydown(function(event){
    　　if(event.keyCode==13){
    　　	submitForm()
    　　}
    });
     
	$("#cancel").click(function(){
		$("#myModal").hide();
		$(".pass-input").val("");
        $('.error').html('')
	});
	$('.pass-icon').click(function () {
		let passType = $('.pass-input').attr('type');
		if (passType === 'password'){
			$('.pass-input').attr('type','text');
			$('.pass-icon').attr("src","img/open.png");
		} else {
			$('.pass-input').attr('type','password');
			$('.pass-icon').attr("src","img/close.png");
		}
	})
    
    $(".pass-input").bind('input propertychange',function(){
        if($('.pass-input').val().length <1){
            $('.error').html('')
        }
    });
