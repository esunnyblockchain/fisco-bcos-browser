<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Warrants Verify</title>
<%--公共的css和js文件--%>
<%@ include file="../pages/comm/JSandCSS.jsp"%>
<script type="text/javascript" src="../assets/js/dateUtils.js"></script>

</head>
<body>
	<input id="blockHeight" type="hidden" value="${blockHeight}">
	<div class="wrapper">
		<%@ include file="../pages/comm/header.jsp"%>

		<%--    <div class="container left hidden-lg hidden-md" id="divmobilesearch" style="margin-top: 5px; margin-bottom: -18px; padding-right: 20px; padding-left: 20px;">
        <form action="/search" method="GET">
            <input id="txtSearchInputMobile" type="text" placeholder="Search for Account, Tx Hash or Data" class="form-control" style="text-align: center;" name="q" maxlength="100" title="Address, Contract, Txn Hash or Data" />
        </form>
        <br /><br />
    </div>--%>

		<div class="breadcrumbs">
			<div class="container">
				<h1 class="pull-left">
					仓单验证 <span class="lead-modify" style="color: #999999">&nbsp;</span><br />
				</h1>
				<ul class="pull-right breadcrumb">
					<li><a href="../">首页</a></li>
					<li class="active">仓单验证</li>
				</ul>
			</div>
		</div>

		<div class="profile container " style="margin-top: 5px">
			<div class="row">
				<div class="col-md-12">


					<div class="form-group">
						<label for="exampleInputFile">选择证据文件</label> 
						
						<input type="file" id="proof-file-path" style="width:100%; padding:15px;"" >
					</div>
					<div class="form-group">

					<button type="submit" class="btn btn-success" id="btn-verify">提交</button>
					</div>
				</div>
			</div>

            <div class="row" style="padding-top: 10px;">
                <div class="col-md-12">
                    <pre class="result" style="font-size:14px; line-height: 24px;">
                     </pre>
                </div>
            </div>

			<div class="row" style="padding-top: 10px;">
				<div class="col-md-12">
					<pre class="content">
					 </pre>
				</div>
			</div>

		</div>
	</div>
</body>

<script>
	$(function() {
		$("#btn-verify").click(function() {
			$(".content").html('');
			$(".result").html('');
			$("#btn-verify").prop("disabled", true);
			var file = document.getElementById('proof-file-path').files[0];
			console.log(file);

            try {
				var oFReader = new FileReader();
				oFReader.readAsText(file);
				oFReader.onloadend = function(oFRevent) {
					var src = oFRevent.target.result;
		            $('.content').html(JSON.stringify(JSON.parse(src), null, 4));
					$.ajax({
						url : '../warrant/getVerifyResult.json',//URI
						contentType : "application/json;charset=UTF-8",//设置头信息
						type : 'post',
						data : src,
						contentType:"application/json",
						cache : false,
						dataType : 'json',
						success : function(DATA) { 
	                           console.log(DATA);
							if (DATA.status == 0) {
								if (DATA.data.valid) {
									alert("验证成功！");
									var result = DATA.data;
									var html = "区块高度：" + result.blockNumber + "\n"
										     + "区块哈希：" + result.blockHash+ "\n"
										     + "区块时间：" + result.timeStr+ "\n\n"
										     + "交易索引：" + result.transactionIndex+ "\n"
										     + "交易哈希：" + result.transactionHash+ "\n"
										     + "交易发送者：" + result.transactionFrom+ "\n"
	                                         + "处理合约：" + result.transactionTo+ "\n\n"
	                                         + "事件列表：" + "\n";
									var events = result.events;
									for (var i = 0; i < events.length; ++i) {
										var event = events[i];
										html += "合约名称：" + event.contractName + " 合约地址：" + event.contractAddress  + " 事件名称：" + event.eventName + "\n";  
										for (var j = 0; j < event.eventParams.length; ++j) {
											html += event.eventParams[j] + "：" + event.eventValues[j] + " ";
										}
										html += "\n\n";
									}
									$(".result").html(html);
								} else {
									alert("验证失败，数据有误！");
								}
							} else {
								alert(DATA.msg);
							}
				            $("#btn-verify").prop("disabled", false);
						},
						error : function(DATA) {
							console.log(DATA);
							alert("query fail");
	                        $("#btn-verify").prop("disabled", false);
						}
					});
				}
            } catch (error) {
                alert("file format error");
                $("#btn-verify").prop("disabled", false);
            }
            
			return;
		});
	});
</script>

</html>
