package com.innee.czyhInterface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.innee.czyhInterface.exception.ServiceException;
import com.innee.czyhInterface.util.SpringContextHolder;
import com.innee.czyhInterface.util.asynchronoustasks.ITaskBean;
import com.innee.czyhInterface.util.asynchronoustasks.taskBeanImpl.AppStartupBean;
import com.innee.czyhInterface.util.asynchronoustasks.taskBeanImpl.CustomerAddEventRecommendBean;
import com.innee.czyhInterface.util.asynchronoustasks.taskBeanImpl.EventSoldOutBean;
import com.innee.czyhInterface.util.asynchronoustasks.taskBeanImpl.OrderSendSmsBean;
import com.innee.czyhInterface.util.asynchronoustasks.taskBeanImpl.OrderUpdateCustomerInfoBean;
import com.innee.czyhInterface.util.asynchronoustasks.taskBeanImpl.OrderUpdateCustomerTagBean;
import com.innee.czyhInterface.util.asynchronoustasks.taskBeanImpl.OrderUpdateOrderGpsByIpBean;

/**
 * 
 * @author zgzhou
 */
@Component
@Transactional
public class AsynchronousTasksService {

	private static Logger logger = LoggerFactory.getLogger(AsynchronousTasksService.class);

	/**
	 * 执行异步任务
	 */
	public void performTasks(ITaskBean taskBean) throws ServiceException {
		int taskType = taskBean.getTaskType();
		switch (taskType) {
		case 2: {
			// 异步写入订单附加统计信息
		}
		case 3: {
			// 异步发送短信
		}
		case 4: {
			// 异步发送短信
		}
		case 5: {
			// 下单时异步修改用户标签
		}
		case 6: {
			// 下单时异步修改用户gps
		}

		case 7: {
			// 记录点赞用户的id
			try {
				break;
			} catch (Exception e) {
				logger.error("点赞时记录用户id", e);
				throw new ServiceException("点赞时异步添加用户id出错", e);
			}
		}

		case 10: {
			// 下单时若该活动规格售完则发送钉钉推送
			try {
				EventSoldOutBean eso = (EventSoldOutBean) taskBean;
				StringBuilder dingTalk = new StringBuilder();
				dingTalk.append("活动售罄提醒：活动名称【").append(eso.getEventTitle()).append("】，场次【")
						.append(eso.getSessionTitle()).append("】，规格【").append(eso.getSpecTitle())
						.append("】已经售罄，请及时处理。");
				break;
			} catch (Exception e) {
				logger.error("下单时若该活动规格售完则发送钉钉推送出错", e);
				throw new ServiceException("下单时若该活动规格售完则发送钉钉推送出错", e);
			}
		}
		case 11: {
			// 收集用户启动位置信息
		}
		case 12: {
			// 异步写入订单附加统计信息
			try {
				break;
			} catch (Exception e) {
				logger.error("下单时异步写入订单统计到用户附加信息表出错", e);
				throw new ServiceException("下单时异步写入订单统计到用户附加信息表出错", e);
			}
		}
		default: {
			break;
		}
		}
	}

}