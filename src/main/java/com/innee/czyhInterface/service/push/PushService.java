package com.innee.czyhInterface.service.push;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.utils.Exceptions;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.innee.czyhInterface.dao.PushCustomerInfoDAO;
import com.innee.czyhInterface.entity.TPushCustomerInfo;
import com.innee.czyhInterface.service.CommonService;
import com.innee.czyhInterface.service.FxlService;
import com.innee.czyhInterface.service.PublicService;
import com.innee.czyhInterface.util.DictionaryUtil;
import com.innee.czyhInterface.util.PropertiesUtil;
import com.innee.czyhInterface.util.push.AndroidNotification;
import com.innee.czyhInterface.util.push.PushClient;
import com.innee.czyhInterface.util.push.android.AndroidUnicast;
import com.innee.czyhInterface.util.push.ios.IOSUnicast;

/**
 * 推送类接口
 * 
 * @author jinshengzhi
 *
 */
@Component("PushServiceV1")
@Transactional
public class PushService {

	private static final Logger logger = LoggerFactory.getLogger(PushService.class);

	private PushClient client = new PushClient();

	protected final JSONObject rootJson = new JSONObject();

	protected final String USER_AGENT = "Mozilla/5.0";

	private static JsonMapper mapper = new JsonMapper(Include.ALWAYS);

	@Autowired
	PublicService publicService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private FxlService fxlService;

	@Autowired
	private PushCustomerInfoDAO pushCustomerInfoDAO;

	public void savePushCustomerInfo(String fcustomerId, String ftitle, String fcontent, String fimage, String ftype,
			Integer ftargetType, String ftargetObject, Date fpushTime, String fdescription, String fpageTitle) {

		try {
			TPushCustomerInfo tPushCustomerInfo = new TPushCustomerInfo();
			tPushCustomerInfo.setFcustomerId(fcustomerId);
			tPushCustomerInfo.setFtitle(ftitle);
			tPushCustomerInfo.setFcontent(fcontent);
			tPushCustomerInfo.setFimage(fimage);
			tPushCustomerInfo.setFtype(ftype);
			tPushCustomerInfo.setFtargetType(ftargetType);
			tPushCustomerInfo.setFtargetObject(ftargetObject);
			tPushCustomerInfo.setFpushTime(fpushTime);
			tPushCustomerInfo.setFdescription(fdescription);
			tPushCustomerInfo.setFpageTitle(fpageTitle);
			tPushCustomerInfo.setFunread(0);
			tPushCustomerInfo.setFstatus(20);

			pushCustomerInfoDAO.save(tPushCustomerInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 系统推送单播用
	 * 
	 * @param valueMap
	 */
	public void pushMessage(Integer ftargetType, String ftitle, String fcontent, String fdescription,
			String ftargetObjectId, String fdeviceToken, String fcustomerId, String fimage, String ftype,
			String fpageTitle) {

		String targetType = DictionaryUtil.getString(DictionaryUtil.PushLinkTargetType, ftargetType);

		Date date = new Date();
		try {
			AndroidUnicast unicast = new AndroidUnicast(PropertiesUtil.getProperty("umappkey"),
					PropertiesUtil.getProperty("appMasterSecret"));

			unicast.setDeviceToken(fdeviceToken);
			unicast.setTicker("您有新的通知消息");
			unicast.setTitle(ftitle);
			unicast.setText(fcontent);
			unicast.setDescription(fdescription);
			unicast.goAppAfterOpen();
			unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);

			unicast.setProductionMode();// 生产环境 “上线后打开此注释切换生产模式”
			// unicast.setTestMode();// 测试环境

			unicast.setExtraField("target_type", targetType);
			unicast.setExtraField("target_id", ftargetObjectId);

			client.send(unicast);

		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
		}

		try {

			IOSUnicast unicast = new IOSUnicast(PropertiesUtil.getProperty("IOSUMAppKey"),
					PropertiesUtil.getProperty("IOSappMasterSecret"));

			unicast.setDeviceToken(fdeviceToken);
			unicast.setAlert(fcontent);
			unicast.setBadge(1);
			unicast.setSound("default");
			unicast.setDescription(fdescription);
			unicast.setProductionMode();// 生产环境 “上线后打开此注释切换生产模式”
			// unicast.setTestMode();// 测试环境

			unicast.setCustomizedField("target_type", targetType);
			unicast.setCustomizedField("target_id", ftargetObjectId);

			client.send(unicast);

		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
		}

		savePushCustomerInfo(fcustomerId, ftitle, fcontent, fimage, ftype, ftargetType, ftargetObjectId, date,
				fdescription, fpageTitle);
	}

	/*
	 * 订单待付款通知
	 */
	public void toPaid(String orderNum, String orderId, String fdeviceToken, String fcustomerId) {
		this.pushMessage(10, "订单未付款通知", "您的订单:" + orderNum + "暂未付款，系统将在20分钟后自动取消订单，请在“我-待付款”中及时付款。。", "您有一个订单尚未付款", orderId,
				fdeviceToken, fcustomerId, "", "1", "");
	}

	/*
	 * 订单支付成功推送
	 */
	public void successPayment(String orderNum, String orderId, String fdeviceToken, String fcustomerId) {
		this.pushMessage(10, "订单支付成功通知", "您好，您的订单编号为:" + orderNum + "}付款已成功！请在“我-待发货”中查看物流动向。", "您有一个订单支付成功",
				orderId, fdeviceToken, fcustomerId, "", "1", "");
	}

	/*
	 * 物流发货推送
	 */
	public void confirmGoods(String orderNum, String express, String orderId, String fdeviceToken, String fcustomerId) {
		this.pushMessage(10, "订单物流发货通知", "您好，您的订单编号为：" + orderNum + "正在向您飞奔！请在“我-待收货”中查看物流状态。",
				"您的订单已发货", orderId, fdeviceToken, fcustomerId, "", "1", "");
	}

	/*
	 * 退款发起通知
	 */
	public void refund(String orderNum, String orderId, String fdeviceToken, String fcustomerId) {
		this.pushMessage(10, "订单退款审核成功通知", "您的订单编号为：" + orderNum + "退款已通过审核，退款金额将在0-7个工作日返回原支付账户，请注意查收~", "您的退款订单已审核通过",
				orderId, fdeviceToken, fcustomerId, "", "1", "");
	}

	/*
	 * 优惠券过期通知
	 */
	public void couponOverdue(String money, String fdeviceToken, String fcustomerId,
			String customerName) {
		this.pushMessage(8, "优惠券即将过期通知", "亲爱的" + customerName + "您有1张优惠券即将过期，快来查找优惠放肆买买买吧！", "您有一张优惠券即将过期",
				"mecoupon", fdeviceToken, fcustomerId, "", "1", "");
	}

	/*
	 * 积分到账通知
	 */
	public void bonusAccount(String bonus, String fdeviceToken, String fcustomerId, String customerName) {
		this.pushMessage(9, "积分到账通知", "亲爱的" + customerName + "，您的U币已到账，请在“我-U币社”中兑换你心水的商品哦~", "您有积分入账", "welfare",
				fdeviceToken, fcustomerId, "", "1", "");
	}

	/*
	 * 积分消耗通知
	 */
	public void bonusConsumption(String bonus, String goodsTitle, String fdeviceToken, String fcustomerId,
			String customerName) {
		this.pushMessage(9, "消耗积分通知",
				"恭喜您兑换商品" + goodsTitle + "成功！本次消耗" + bonus + "U币，邀请好友得更多U币！详情请【点击这里】",
				"您消耗了积分", "welfare", fdeviceToken, fcustomerId, "", "1", "");
	}

}