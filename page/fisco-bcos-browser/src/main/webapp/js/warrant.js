/**
 * This file is part of FISCO BCOS Browser.
 *
 * FISCO BCOS Browser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FISCO BCOS Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FISCO BCOS Browser.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * @file: block.js
 * @author: v_wbsqwu
 * @date: 2018
 */

//请求参数
var paramData = {
    pageNumber: 1,//默认第一页
    pageSize:20,//默认每页查询20条数据
    hash:null,
    dateTime1:null,
    dateTime2:null,
}


$( document ).ready(function() {
     
    //获取区块表信息列表
    queryWarrantList();
});

//定时十秒刷新一次
setInterval("getBlocksListByPage()",30000);

/**
 *@Description: 分页获取区块表信息列表
 */
function getWarrantListByPage() {
    //清空table body
    $("#tableBody").html("");

    $.ajax({
        url:'../warrant/getTbWarrantInfoByPage.json',//URI
        contentType:"application/json;charset=UTF-8",//设置头信息
        type:'get',
        cache:false,
        dataType:'json',
        success:function(DATA) {
            if(DATA.status==0){ 
            	var warrantList = DATA.data;
            	if(warrantList!=null && warrantList.length>0) {
            		var htmlStr = "";
            		for (var index in warrantList) {
            			var warrant = warrantList[index];
            			htmlStr += '<tr>'
            				+ '<td><a href="../warrant/getWarrantDetailPage.page?warrantId='+warrant.warrantId+'">'+warrant.warrantId+'</a></td>'
            				+ '<td>'+warrant.warrantName+'</td>'
            				+ '<td>'+warrant.warrantQty+'</td>' 
            				+ '<td>'+warrant.transactionHash+'</td>'
            				+ '<td>'+warrant.blockNumber+'</td>'
            				+ '<td>'+warrant.dateTimeStr+'</td>'
            				+ '</tr>';
            		}
                    $("#tableBody").html(htmlStr);
            	}
                /*var blockList = DATA.list;
                if(blockList!=null&&blockList.length>0){
                    var htmlStr = "";
                    for(var index in blockList){
                        var rowData = blockList[index];
                        var GasUsedPercent = rowData.gasUsed/rowData.gasLimit;//取商
                        var GasUsedPercentStr = Math.round(GasUsedPercent*10000)/100+"%";//转换成百分比
                        htmlStr +='<tr>'
                            +'<td><a href="../block/getTbBlockDetailPage.page?blockHash='+rowData.pkHash+'">'+rowData.number+'</a></td>'
                            +' <td>'+rowData.dateTimeStr+'</td>'
                            +'<td><a href="../transaction/transaction.page?blockHeight='+rowData.number+'">'+rowData.transactionNumber+'</a></td>'
                            +'<td><span class="address-tag" style="cursor: pointer" title="'+rowData.miner+'">'+rowData.miner+'</span></td>'
                            +'<td><span class="address-tag" style="cursor: pointer" title="'+rowData.pkHash+'"><a href="../block/getTbBlockDetailPage.page?blockHash='+rowData.pkHash+'">'+rowData.pkHash+'</a></span></td>'
                            +'<td>'+rowData.gasUsed+' <span style="font-family: Monospace;">('+GasUsedPercentStr+')</span></td>'
                            +' <td>'+rowData.gasLimit+'</td>'
                            +'</tr>'
                    }
                    $("#tableBody").html(htmlStr);

                }*/
                //添加分页
               // addPaginator(DATA.pageNumber,DATA.pageSize,DATA.pageTotal);

            }else {
                alert(DATA.msg);
            }

        },
        error : function(DATA) {
            console.log("query fail:"+DATA);
        }
    });
}



/**
 *@Description: 添加分页
 */
function addPaginator(pageNumber,pageSize,totalPages) {

    $("#pagination3").pagination({
        currentPage: pageNumber,
        totalPage: totalPages,
        isShow: true,
        count: 7,
        homePageText: "首页",
        endPageText: "尾页",
        prevPageText: "上一页",
        nextPageText: "下一页",
        callback: function (currentPage) {
            onPaginatorClick(currentPage,pageSize);
        }
    });
}

//点击分页按钮之后
function onPaginatorClick(pageNumber,pageSize) {
    paramData.pageNumber = pageNumber;//页码
    paramData.pageSize = pageSize;//页面大小
    getWarrantListByPage();
}


/**
 *@Description: 根据hash查询
 */
function queryWarrantList() { 

    getWarrantListByPage();
}