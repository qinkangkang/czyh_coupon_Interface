package com.innee.czyhInterface.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.innee.czyhInterface.dao.CouponDAO;
import com.innee.czyhInterface.dao.CustomerBargainingDAO;
import com.innee.czyhInterface.dao.CustomerDAO;
import com.innee.czyhInterface.dao.DeliveryDAO;
import com.innee.czyhInterface.dao.EventBargainingDAO;
import com.innee.czyhInterface.dao.EventDAO;
import com.innee.czyhInterface.dao.TimingTaskDAO;
import com.innee.czyhInterface.entity.TCustomer;
import com.innee.czyhInterface.entity.TCustomerBargaining;
import com.innee.czyhInterface.entity.TEventBargaining;
import com.innee.czyhInterface.entity.TTimingTask;
import com.innee.czyhInterface.util.Constant;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 定时器任务管理类
 * 
 * @author jinshengzhi
 */
@Component
@Transactional
public class TimingTaskService {

	private static final Logger logger = LoggerFactory.getLogger(TimingTaskService.class);

	// private static JsonMapper mapper = new JsonMapper(Include.ALWAYS);

	@Autowired
	private FxlService fxlService;

	@Autowired
	private EventDAO eventDAO;

	@Autowired
	private TimingTaskDAO timingTaskDAO;

	@Autowired
	private CouponDAO couponDAO;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private DeliveryDAO deliveryDAO;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private CustomerBargainingDAO customerBargainingDAO;

	@Autowired
	private EventBargainingDAO eventBargainingDAO;

	
	public int updateEventStatus() {
		// 1、活动自动上架任务 x
		// 2、活动自动下架任务  (去掉钉钉任务推送)
		// 3、场次报名截止任务 x
		// 4、场次退款截止任务 x
		// 5、场次开始任务         x
		// 6、场次结束任务         x
		// 7、未支付订单过期作废
		// 8、活动手动上架任务，这时活动已经是上架状态，但是活动场次信息在场次日历表中的数据还没有增加，需要添加本任务来异步处理 x
		// 9、活动手动下架任务，这时活动已经是下架状态，但是活动场次信息在场次日历表中的数据还没有减去，需要添加本任务来异步处理 x
		// 10、优惠券领取有效期开始任务，将该优惠券的状态变更为可领取状态
		// 11、优惠券领取有效期截止任务，将该优惠券的状态变更为领取过期状态
		// 12、APP版本发布定时任务，到期后将去执行将新的APP版本信息更新到APP版本MAP中 
		// 13、文章下架定时任务，到期后将执行清除文章推荐数缓存
		// 14、优惠券使用有效期开始任务，到期后更改用户优惠券未到有效期状态变更为可使用状态
		// 15、优惠券使用有效期截止任务，到期后将该优惠券的状态变更为使用过期状态，同时更改用户优惠券未使用状态变更为使用过期状态
		// 16、推送信息定时任务，触发友盟推送任务
		// 17、 发送订单评价提醒模板消息      (可变更为短信消息/推送消息)
		// 18、 发送订单未支付通知模板消息  (可变更为短信消息/推送消息)
		// 20、自动核销订单 x
		List<TTimingTask> list = timingTaskDAO.findByTaskTimeLessThan(new Date().getTime());
		int taskType = 0;

		// 定义场次起止日期的最小值和最大值，为了去日历表中获取日历记录为条件
		for (TTimingTask timingTask : list) {
			taskType = timingTask.getTaskType().intValue();
			switch (taskType) {
			case 1: {
				break;
			}
			case 2: {
				// 同步发送钉钉通知
				break;
			}
			case 3: {
				break;
			}
			// case 4: {
			// break;
			// }
			// case 5: {
			// eventSessionDAO.saveStatus(40, timingTask.getEntityId());
			// break;
			// }
			// case 6: {
			// eventSessionDAO.saveStatus(50, timingTask.getEntityId());
			// break;
			// }
			case 7: {
				break;
			}
			case 8: {
				break;
			}
			case 9: {
				break;
			}
			case 10: {
				couponDeliveryStart(timingTask.getEntityId());
				break;
			}
			case 11: {
				couponDeliveryEnd(timingTask.getEntityId());
				break;
			}
			case 12: {
				break;
			}
			case 13: {
				break;
			}
			case 14: {
				couponUseStart(timingTask.getEntityId());
				break;
			}
			case 15: {
				couponUseEnd(timingTask.getEntityId());
				break;
			}
			case 16: {
				pushMessage(timingTask.getEntityId());
				break;
			}
			case 17: {
				break;
			}
			case 18: {
				break;
			}
			case 19: {
				break;
			}
			case 20: {
				break;
			}
			default: {
				break;
			}
			}
		}

		return list.size();
	}

	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void couponDeliveryStart(String delivareyId) {
		deliveryDAO.updateStatus(delivareyId, 40);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void couponDeliveryEnd(String delivareyId) {
		deliveryDAO.updateStatus(delivareyId, 100);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void couponUseStart(String couponId) {
		// couponDeliveryDAO.updateStatusByCouponId2(couponId, 80, 10);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void couponUseEnd(String couponId) {
		couponDAO.updateStatus(couponId, 110);
		// couponDeliveryDAO.updateStatusByCouponId(couponId, 20, 90);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void pushMessage(String pushId) {
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void eventAutoOnsale(String eventId) {
		//eventDAO.updateStockFlagBySpec(eventId);
	}

	
}