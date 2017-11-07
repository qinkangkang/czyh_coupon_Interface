package com.innee.czyhInterface.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.innee.czyhInterface.service.TimingTaskService;

/**
 * 被Spring的Quartz MethodInvokingJobDetailFactoryBean定时执行的普通Spring Bean.
 */
public class FlxQuartzJob {

	private static Logger logger = LoggerFactory.getLogger(FlxQuartzJob.class);

	@Autowired
	private TimingTaskService timingTaskService;

	// @Autowired
	// private CarnivalService carnivalService;

	public void executeTimingTask() {
		StringBuilder info = new StringBuilder();
		int i = timingTaskService.updateEventStatus();
		logger.warn(info.append("执行了").append(i).append("个定时任务！").toString());
	}

	public void resetSerial() {
		NumberUtil.setSerial(0L);
		NumberUtil.setOtherSerial(0L);
		logger.warn("重置订单流水号成功成功！");
		// eventService.updateCalendarDay();
		// logger.warn("更新活动场次日历表成功！");
		// carnivalService.updateCarnival();
		// logger.warn("更新嘉年华用户的排名以及积分");
		StringBuilder info = new StringBuilder();
	}
}
