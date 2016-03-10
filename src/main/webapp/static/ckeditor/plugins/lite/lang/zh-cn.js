CKEDITOR.plugins.setLang( 'lite', 'zh', {
	TOGGLE_TRACKING: "Toggle Tracking Changes",
	TOGGLE_SHOW: "Toggle Tracking Changes",
	ACCEPT_ALL: "接受所有修改",
	REJECT_ALL: "拒绝所有修改",
	ACCEPT_ONE: "接受修改",
	REJECT_ONE: "拒绝修改",
	START_TRACKING: "开始痕迹修改",
	STOP_TRACKING: "停止痕迹修改",
	PENDING_CHANGES: "您的文档还有未处理的修改。\n请先处理再关闭修改痕迹。",
	HIDE_TRACKED: "隐藏痕迹修改",
	SHOW_TRACKED: "显示痕迹修改",
	CHANGE_TYPE_ADDED: "已添加",
	CHANGE_TYPE_DELETED: "已删除",
	MONTHS: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
	NOW: "现在",
	MINUTE_AGO: "一分钟前",
	MINUTES_AGO: "x分钟前",
	BY: "by",
	ON: "on",
	AT: "on",
	LITE_LABELS_DATE: function(day, month, year)
	{
		if(typeof(year) != 'undefined') {
			year = ", " + year;
		}
		else {
			year = "";
		}
		return this.MONTHS[month] + " " + day + year;
	}
});