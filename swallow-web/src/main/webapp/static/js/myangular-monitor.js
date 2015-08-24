function renderGraph(url, divName, http) {
	http({
		method : 'POST',
		url : window.contextpath + url + location.search
	})
			.success(
					function(data, status, headers, config) {

						var parent = $('#' + divName);
						parent.empty();
						var count = 0;
						if (data.length == 0) {
							return;
						}
						console.log(data);
						data
								.forEach(function(item) {
									count++;
									var childdiv = $('<div></div>');
									childdiv.appendTo(parent);
									$(function() {
										childdiv
												.highcharts({
													title : {
														text : item.title,
														x : 0
													// center
													},
													subtitle : {
														text : item.subTitle,
														x : 0
													},
													xAxis : {
														type : 'datetime'
													},
													yAxis : {
														title : {
															text : item.yAxisTitle
														},
														plotLines : [ {
															value : 0,
															width : 10,
															color : '#808080'
														} ]
													},
													tooltip : {
														valueSuffix : ''
													},
													legend : {
														layout : 'vertical',
														align : 'right',
														verticalAlign : 'middle',
														borderWidth : 0
													},
													plotOptions : {
														series : {
															pointStart : item.plotOption.series.pointStart + 8 * 3600 * 1000,
															pointInterval : item.plotOption.series.pointInterval
														// one day
														}
													},
													series : item.series
												});
									});
								});
					}).error(function(data, status, headers, config) {

				alert("响应错误" + data);
				// app.appError("响应错误", data);
			});
}

module.controller('ProducerServerQpsController', function($scope, $http) {

	$scope.getProducerServerQps = function() {
		renderGraph("/console/monitor/producerserver/qps/get", "container",
				$http);
	};
	$scope.startTime = "";
	$scope.endTime = "";
	$scope.queryServerQps = function() {
		$scope.startTime = $("#starttime").val();
		$scope.endTime = $("#stoptime").val();
		if ($scope.startTime != null && $scope.endTime != null) {
			if ($scope.startTime.length == 0 && $scope.endTime.length == 0) {
				renderGraph("/console/monitor/producerserver/qps/get", "container",
				$http);
			}else if($scope.startTime.length == 0 && $scope.endTime.length > 0){
				alert("开始时间不能为空");
				return;
			}else if($scope.startTime.length > 0 && $scope.endTime.length == 0){
				$scope.endTime = new Date().format("yyyy-MM-dd HH:mm:ss");
			}else if($scope.startTime.length > 0 && $scope.endTime.length > 0){
				console.log($scope.startTime.length);
				startDate = new Date($scope.startTime);
				endDate = new Date($scope.endTime);
				if (endDate <= startDate) {
					alert("结束时间不能小于开始时间");
					return;
				}
			}
		}
		
		renderGraph("/console/monitor/producerserver/qps/get/"
				+ $scope.startTime + "/" + $scope.endTime, "container", $http);
	};

});

module.controller('ConsumerServerQpsController', function($scope, $http) {

	$scope.getConsumerServerQps = function() {
		renderGraph("/console/monitor/consumerserver/qps/get", "container",
				$http);
	};
	$scope.startTime = "";
	$scope.endTime = "";
	$scope.queryServerQps = function() {
		$scope.startTime = $("#starttime").val();
		$scope.endTime = $("#stoptime").val();
		if ($scope.startTime != null && $scope.endTime != null) {
			if ($scope.startTime.length == 0 && $scope.endTime.length == 0) {
				renderGraph("/console/monitor/consumerserver/qps/get", "container",
				$http);
			}else if($scope.startTime.length == 0 && $scope.endTime.length > 0){
				alert("开始时间不能为空");
				return;
			}else if($scope.startTime.length > 0 && $scope.endTime.length == 0){
				$scope.endTime = new Date().format("yyyy-MM-dd HH:mm:ss");
			}else if($scope.startTime.length > 0 && $scope.endTime.length > 0){
				console.log($scope.startTime.length);
				startDate = new Date($scope.startTime);
				endDate = new Date($scope.endTime);
				if (endDate <= startDate) {
					alert("结束时间不能小于开始时间");
					return;
				}
			}
		}
		renderGraph("/console/monitor/consumerserver/qps/get/"
				+ $scope.startTime + "/" + $scope.endTime, "container", $http);
	};
});

module.controller('ConsumerQpsController', function($scope, $http) {
	$http({
		method : 'POST',
		url : window.contextPath + '/console/monitor/topiclist/get'
	}).success(
			function(topicList, status, headers, config) {

				$("#consumer-div").typeahead(
						{
							source : topicList,
							updater : function(c) {
								window.location = window.contextPath
										+ "/console/monitor/consumer/" + c
										+ "/qps";
								return c;
							}
						})
			}).error(function(data, status, headers, config) {
		app.appError("响应错误", data);
	});

	$scope.getConsumerQps = function(topicName) {
		renderGraph("/console/monitor/consumer/" + topicName + "/qps/get",
				"container", $http);
	};

	$scope.startTime = "";
	$scope.endTime = "";
	$scope.queryByTime = function(topicName) {
		$scope.startTime = $("#starttime").val();
		$scope.endTime = $("#stoptime").val();
		if ($scope.startTime != null && $scope.endTime != null) {
			if ($scope.startTime.length == 0 && $scope.endTime.length == 0) {
				renderGraph("/console/monitor/consumer/" + topicName + "/qps/get/", "container",
				$http);
			}else if($scope.startTime.length == 0 && $scope.endTime.length > 0){
				alert("开始时间不能为空");
				return;
			}else if($scope.startTime.length > 0 && $scope.endTime.length == 0){
				$scope.endTime = new Date().format("yyyy-MM-dd HH:mm:ss");
			}else if($scope.startTime.length > 0 && $scope.endTime.length > 0){
				console.log($scope.startTime.length);
				startDate = new Date($scope.startTime);
				endDate = new Date($scope.endTime);
				if (endDate <= startDate) {
					alert("结束时间不能小于开始时间");
					return;
				}
			}
		}
		renderGraph("/console/monitor/consumer/" + topicName + "/qps/get/"
				+ $scope.startTime + "/" + $scope.endTime, "container", $http);
	};
});

module.controller('ConsumerAccuController', function($scope, $http) {

	$http({
		method : 'POST',
		url : window.contextpath + '/console/monitor/topiclist/get'
	}).success(
			function(topicList, status, headers, config) {

				$("#consumer-div").typeahead(
						{
							source : topicList,
							updater : function(c) {
								window.location = window.contextpath
										+ "/console/monitor/consumer/" + c
										+ "/accu";
								return c;
							}
						})
			}).error(function(data, status, headers, config) {
		app.appError("响应错误", data);
	});

	$scope.getAccu = function(topicName) {
		renderGraph("/console/monitor/consumer/" + topicName + "/accu/get",
				"container", $http);
	};

	$scope.startTime = "";
	$scope.endTime = "";
	$scope.queryByTime = function(topicName) {
		$scope.startTime = $("#starttime").val();
		$scope.endTime = $("#stoptime").val();
		if ($scope.startTime != null && $scope.endTime != null) {
			if ($scope.startTime.length == 0 && $scope.endTime.length == 0) {
				renderGraph("/console/monitor/consumer/" + topicName + "/accu/get", "container",
				$http);
			}else if($scope.startTime.length == 0 && $scope.endTime.length > 0){
				alert("开始时间不能为空");
				return;
			}else if($scope.startTime.length > 0 && $scope.endTime.length == 0){
				$scope.endTime = new Date().format("yyyy-MM-dd HH:mm:ss");
			}else if($scope.startTime.length > 0 && $scope.endTime.length > 0){
				console.log($scope.startTime.length);
				startDate = new Date($scope.startTime);
				endDate = new Date($scope.endTime);
				if (endDate <= startDate) {
					alert("结束时间不能小于开始时间");
					return;
				}
			}
		}
		renderGraph("/console/monitor/consumer/" + topicName + "/accu/get/"
				+ $scope.startTime + "/" + $scope.endTime, "container", $http);
	}
});

module.controller('ConsumerDelayController', function($scope, $http) {

	// search
	$http({
		method : 'POST',
		url : window.contextpath + '/console/monitor/topiclist/get'
	}).success(
			function(topicList, status, headers, config) {

				$("#consumer-div").typeahead(
						{
							source : topicList,
							updater : function(c) {
								window.location = window.contextpath
										+ "/console/monitor/consumer/" + c
										+ "/delay";
								return c;
							}
						})
			}).error(function(data, status, headers, config) {
		app.appError("响应错误", data);
	});

	$scope.getDelay = function(topicName) {
		renderGraph('/console/monitor/consumer/' + topicName + '/delay/get',
				"container", $http);
	};
	$scope.startTime = "";
	$scope.endTime = "";
	$scope.queryByTime = function(topicName) {
		$scope.startTime = $("#starttime").val();
		$scope.endTime = $("#stoptime").val();
		if ($scope.startTime != null && $scope.endTime != null) {
			if ($scope.startTime.length == 0 && $scope.endTime.length == 0) {
				renderGraph("/console/monitor/consumer/" + topicName + "/delay/get/", "container",
				$http);
			}else if($scope.startTime.length == 0 && $scope.endTime.length > 0){
				alert("开始时间不能为空");
				return;
			}else if($scope.startTime.length > 0 && $scope.endTime.length == 0){
				$scope.endTime = new Date().format("yyyy-MM-dd HH:mm:ss");
			}else if($scope.startTime.length > 0 && $scope.endTime.length > 0){
				console.log($scope.startTime.length);
				startDate = new Date($scope.startTime);
				endDate = new Date($scope.endTime);
				if (endDate <= startDate) {
					alert("结束时间不能小于开始时间");
					return;
				}
			}
		}
		renderGraph("/console/monitor/consumer/" + topicName + "/delay/get/"
				+ $scope.startTime + "/" + $scope.endTime, "container", $http);
	};
});

module.controller('ConsumerDashboardController', function($scope, $http) {
	$("#sidebar").addClass("responsive menu-min");

	$scope.starttime = "";
	$scope.stoptime = "";
	$scope.currentMin = -1; // 当前分钟，第一次时设置
	$scope.currentRed = -1;

	$scope.whatClassIsIt = function(index) {
		if (index == $scope.currentRed)
			return "red-num"
		else if (index > $scope.currentMin)
			return "not-active";
		else
			return "";
	}

	$scope.boards = [ {
		id : 1,
		type : "综合大盘"
	}, {
		id : 2,
		type : "发送延迟大盘"
	}, {
		id : 3,
		type : "确认延迟大盘"
	}, {
		id : 4,
		type : "堆积大盘"
	} ];
	$scope.boardsmap = {
		"综合大盘" : "comprehensive",
		"发送延迟大盘" : "senddelay",
		"确认延迟大盘" : "ackdelay",
		"堆积大盘" : "accu"
	}
	$scope.boardtype = $scope.boards[0].type;

	// $scope.getEntry = function(delayEntry) {
	// defaultSize = 12;
	// var size = delayEntry.size > defaultSize ? defaultSize
	// : delayEntry.size;
	// var emp = delayEntry.heap;
	// var entrys = delayEntry.heap.slice(0, size)
	// return entrys;
	// }

	$scope.setStep = function(step) {
		$scope.step = step + $scope.step;
		if ($scope.step > 0) {
			$scope.step = 0;
		}
		if (typeof ($scope.currentRed) != "undefined") {
			if ($scope.step == 0) {
				var date = new Date();
				var min = date.getMinutes();
				if ($scope.currentRed > min) {
					$scope.currentRed = min;
				}
			}
			$scope.getDashboardDelay($scope.currentRed);
		}
	}

	$scope.setNow = function() {
		$scope.step = 0;
		$scope.getDashboardDelay(-1);
	}

	$scope.step = 0;
	$scope.firstindex = 0;
	$scope.starttime = "";
	$scope.stoptime = "";
	$scope.requestindex = 0;

	$scope.getDashboardDelay = function(index) {
		$scope.requestindex = index;
		$scope.minuteEntrys = [];
		var date = new Date();
		if (index != -1) {
			date.setMinutes(index, 59, 999);
		} else {
			date.setSeconds(59, 999);
		}
		date.setHours(date.getHours() + 8);
		$http(
				{
					method : 'GET',
					params : {
						date : date,
						step : $scope.step,
						type : $scope.boardsmap[$scope.boardtype]
					},
					url : window.contextPath
							+ '/console/monitor/dashboard/delay/minute'
				}).success(
				function(data, status, headers, config) {
					$scope.minuteEntrys = data.entry;
					$scope.starttime = data.starttime;
					$scope.stoptime = data.stoptime;
					var date;
					if (typeof ($scope.minuteEntrys) != "undefined"
							&& $scope.minuteEntrys.length > 0) {
						date = new Date($scope.minuteEntrys[0].time)
						$scope.currentRed = date.getMinutes();
					} else {
						date = new Date();
						if (index != -1) {
							$scope.currentRed = index;
						} else {
							$scope.currentRed = date.getMinutes();
						}
					}
					if (index == -1) {
						$scope.currentMin = date.getMinutes();
						$scope.firstindex = $scope.currentMin;
					}

					if ($scope.step < 0) {
						$scope.currentMin = 60;
					} else {
						$scope.currentMin = $scope.firstindex;
					}
				});

	};

	$scope.onchanged = function() {
		$scope.getDashboardDelay($scope.requestindex);
	};

	$scope.showBoard = function() {
		var mesize = $scope.minuteEntrys.length;
		for (i = 0; i < mesize; ++i) {
			var array = $scope.minuteEntrys[i].delayEntry.heap;
			array.sort(function(a, b) {
				var numAlarm = parseFloat(b.numAlarm) - parseFloat(a.numAlarm);
				if (numAlarm == 0) {
					var _f = b.normalizedSendDelaly + b.normalizedAckDelaly
							+ b.normalizedAccu;
					var f = a.normalizedSendDelaly + a.normalizedAckDelaly
							+ a.normalizedAccu;
					return parseFloat(_f) - parseFloat(f);
				} else {
					return numAlarm;
				}
			});
			$scope.minuteEntrys[i].delayEntry.heap = array;
		}
	}

	$scope.showSendDelayBoard = function() {
		var mesize = $scope.minuteEntrys.length;
		for (i = 0; i < mesize; ++i) {
			var array = $scope.minuteEntrys[i].delayEntry.heap;
			array.sort(function(a, b) {
				return parseFloat(b.normalizedSendDelaly)
						- parseFloat(a.normalizedSendDelaly);
			});
			$scope.minuteEntrys[i].delayEntry.heap = array;
		}
	}

	$scope.showAckDelayBoard = function() {
		var mesize = $scope.minuteEntrys.length;
		for (i = 0; i < mesize; ++i) {
			var array = $scope.minuteEntrys[i].delayEntry.heap;
			array.sort(function(a, b) {
				return parseFloat(b.normalizedAckDelaly)
						- parseFloat(a.normalizedAckDelaly);
			});
			$scope.minuteEntrys[i].delayEntry.heap = array;
		}
	}

	$scope.showAccuBoard = function() {
		var mesize = $scope.minuteEntrys.length;
		for (i = 0; i < mesize; ++i) {
			var array = $scope.minuteEntrys[i].delayEntry.heap;
			array.sort(function(a, b) {
				return parseFloat(b.normalizedAccu)
						- parseFloat(a.normalizedAccu);
			});
			$scope.minuteEntrys[i].delayEntry.heap = array;
		}
	}

	$scope.pages = [ "00", "01", "02", "03", "04", "05", "06", "07", "08",
			"09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
			"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
			"31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41",
			"42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52",
			"53", "54", "55", "56", "57", "58", "59" ];

});
