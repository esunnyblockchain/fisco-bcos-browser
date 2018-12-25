<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<head>
<title>Warrant ${warrantId} Info</title>
<%--公共的css和js文件--%>
<%@ include file="../pages/comm/JSandCSS.jsp"%>

</head>
<body>
	<div class="wrapper">
		<%@ include file="../pages/comm/header.jsp"%>

		<div class="breadcrumbs">
			<div class="container">
				<h1 class="pull-left">
					仓单 <span class="lead-modify" style="color: #999999">${warrantId}</span><br />
				</h1>
				<ul class="pull-right breadcrumb">
					<li><a href="../">首页</a></li>
					<li><a href="../block/block.page">仓单列表</a></li>
					<li class="active">仓单详情</li>
				</ul>
			</div>
		</div>


		<div class="profile container blockImfo" id="Top">
			<span id="ContentPlaceHolder1_lblAdResult"></span> <br />
			<div class="tab-v1">
				<ul class="nav nav-tabs" id='nav_tabs'>
					<li class="active"><a href="#overview" data-toggle="tab">仓单概览</a></li>
					<%--<li id="ContentPlaceHolder1_li_disqus"><a href="#comments" data-toggle="tab" onclick="javascript:loaddisqus();">Comments</a></li>--%>
				</ul>

				<div class="tab-content" style="padding: 1px 0;">
					<div class="tab-pane fade in active" id="overview">
						<div class="panel panel-info table-responsive">
							<div class="panel-heading margin-bottom-15">
								<h3 class="panel-title">仓单信息</h3>
							</div>
							<div class="about-history" id="fzlc">
								<div class="about-history-list wow zoomIn" data-wow-delay=".4s">
									<div class="flex-viewport"
										style="overflow: hidden; position: relative;">
										<ul class="slides clearfix list" >
											<!-- <li>
												<div class="item">
													<h3>2017</h3>
													<div class="desc">
														<p>公司进行大项目组改制，深化管理层次，确定项目组责任制与分红制，集团凝聚力更上新的台阶。</p>
													</div>
												</div>
											</li>   -->
                                            <li style="width: 50px;"></li>
                                         </ul>
									</div>
								</div>
							</div>

						</div>
					</div> 
				</div>
			</div>
</body>
<script type="text/javascript">
	//提示工具
	$("[rel='tooltip']").tooltip({
		html : true
	});

	

	//显示json
	var transferEvent = JSON.parse('${transferEvent}');

	var marketEvents = JSON.parse('${marketEvents}');

	var html = '<li><div class="item">'
		   + '<h3>仓单生成&nbsp;&nbsp;&nbsp;区块高度：'
		   + transferEvent.blockNumber
		   + '</h3>'
		   + '<div class="desc"><p><table>'
           + '<tr><td>持有者：</td><td>' + transferEvent.toName + '</td></tr>'
		   + '<tr><td>时间：</td><td>' + transferEvent.blockTimestamp + '</td></tr>'
           + '<tr><td>交易哈希：</td><td title="'+ transferEvent.transactionHash +'" >' + transferEvent.transactionHash.substr(0, 20) + '...</td></tr>'
		   + '</table></p></div></li>';

	var length = marketEvents.length;
	for (var i = 0; i < length; ++i) {
		var marketEvent = marketEvents[i];
		html  += '<li><div class="item">'
	           + '<h3>仓单交易&nbsp;&nbsp;&nbsp;区块高度：'
	           + marketEvent.blockNumber
	           + '</h3>'
	           + '<div class="desc"><p><table>' 
	           + '<tr><td>持有者：</td><td>' + marketEvent.transferToName + '</td></tr>'
	           + '<tr><td>时间：</td><td>' + marketEvent.blockTimestamp + '</td></tr>'
	           + '<tr><td>交易哈希：</td><td title="'+marketEvent.transactionHash+'">' + marketEvent.transactionHash.substr(0, 20) + '...</td></tr>'
	           + '</table></p></div></li>';
	}

	$(".about-history .list").prepend(html);
	
	if ((navigator.userAgent
            .match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i))) {

    } else {
        $(".about-history-list").flexslider({
            animation : "slide",
            slideshow : false,
            controlNav : false,
            itemWidth : 253,
            itemMargin : 31,
            prevText : "<", nextText: ">",
            move : 1
        });
    } 
</script>
</html>