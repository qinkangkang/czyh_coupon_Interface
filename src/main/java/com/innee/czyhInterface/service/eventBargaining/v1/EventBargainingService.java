package com.innee.czyhInterface.service.eventBargaining.v1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.mapper.JsonMapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.innee.czyhInterface.common.dict.ResponseConfigurationDict;
import com.innee.czyhInterface.dao.CustomerBargainingDAO;
import com.innee.czyhInterface.dao.CustomerDAO;
import com.innee.czyhInterface.dao.CustomerInfoDAO;
import com.innee.czyhInterface.dao.EventBargainingDAO;
import com.innee.czyhInterface.dao.EventDAO;
import com.innee.czyhInterface.dao.HelpBargainingDetailDAO;
import com.innee.czyhInterface.dao.SceneUserDAO;
import com.innee.czyhInterface.dao.TimingTaskDAO;
import com.innee.czyhInterface.dto.CustomerDTO;
import com.innee.czyhInterface.dto.bargaining.EventBargainingDTO;
import com.innee.czyhInterface.dto.coupon.PageDTO;
import com.innee.czyhInterface.dto.coupon.ResponseDTO;
import com.innee.czyhInterface.entity.TCustomer;
import com.innee.czyhInterface.entity.TCustomerBargaining;
import com.innee.czyhInterface.entity.TCustomerInfo;
import com.innee.czyhInterface.entity.TEvent;
import com.innee.czyhInterface.entity.TEventBargaining;
import com.innee.czyhInterface.entity.THelpBargainingDetail;
import com.innee.czyhInterface.entity.TSceneUser;
import com.innee.czyhInterface.impl.bargainImpl.EventBargainsService;
import com.innee.czyhInterface.service.CommonService;
import com.innee.czyhInterface.service.FxlService;
import com.innee.czyhInterface.util.ArrayStringUtils;
import com.innee.czyhInterface.util.CommonPage;
import com.innee.czyhInterface.util.ConfigurationUtil;
import com.innee.czyhInterface.util.DictionaryUtil;
import com.innee.czyhInterface.util.HeadImageUtil;
import com.innee.czyhInterface.util.PropertiesUtil;
import com.innee.czyhInterface.util.asynchronoustasks.taskBeanImpl.BargainCountBean;

import net.sf.ehcache.CacheManager;

/**
 * 砍一砍service
 * 
 * @author jinshengzhi
 *
 */
@Component
@Transactional
public class EventBargainingService implements EventBargainsService{

	private static final Logger logger = LoggerFactory.getLogger(EventBargainingService.class);

	private static JsonMapper mapper = JsonMapper.nonDefaultMapper();

	@Autowired
	private CommonService commonService;

	@Autowired
	private EventBargainingDAO eventBargainingDAO;

	@Autowired
	private CustomerBargainingDAO customerBargainingDAO;

	@Autowired
	private HelpBargainingDetailDAO helpBargainingDetailDAO;

	@Autowired
	private FxlService fxlService;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private EventDAO eventDAO;

	@Autowired
	private TimingTaskDAO timingTaskDAO;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private CustomerInfoDAO customerInfoDAO;
	
	@Autowired
	private SceneUserDAO sceneUserDAO;

	/**
	 * 砍一砍商品列表
	 * 
	 * @param customerId
	 * @param request
	 * @return
	 */
	@Transactional(readOnly = true)
	public ResponseDTO eventBargainingList(Integer pageSize, Integer offset) {
		ResponseDTO responseDTO = new ResponseDTO();

		CommonPage page = new CommonPage();
		if (pageSize != null) {
			page.setPageSize(pageSize);
		}
		if (offset != null) {
			page.setOffset(offset);
		}

		List<EventBargainingDTO> eventBargainingList = Lists.newArrayList();
		EventBargainingDTO eventBargainingDTO = null;

		StringBuilder hql = new StringBuilder();
		Map<String, Object> hqlMap = Maps.newHashMap();

		hql.append(
				"select t.id as id, t.feventId as goodsId,t.fimage as fimage from TEventBargaining t where t.fstatus = 20");

		commonService.findPage(hql.toString(), page, hqlMap);
		List<Map<String, Object>> list = page.getResult();

		for (Map<String, Object> amap : list) {
			eventBargainingDTO = new EventBargainingDTO();

			if (amap.get("id") != null && StringUtils.isNotBlank(amap.get("id").toString())) {
				eventBargainingDTO.setBargainingId(amap.get("id").toString());
			}

			if (amap.get("goodsId") != null && StringUtils.isNotBlank(amap.get("goodsId").toString())) {
				eventBargainingDTO.setGoodsId(amap.get("goodsId").toString());
			}

			if (amap.get("fimage") != null && StringUtils.isNotBlank(amap.get("fimage").toString())) {
				eventBargainingDTO.setGoodsImageUrl(fxlService.getImageUrl(amap.get("fimage").toString(), false));
			}
			eventBargainingList.add(eventBargainingDTO);
		}
		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("eventBargainingList", eventBargainingList);
		PageDTO pageDTO = new PageDTO(page.getTotalCount(), page.getPageSize(), page.getOffset());
		returnData.put("page", pageDTO);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("促销商品列表加载成功！");
		return responseDTO;
	}

	/*
	 * 计算出该商户参加砍一砍的低价
	 */
	public TCustomerBargaining bargainFloorPrice(TEventBargaining tEventBargaining, String customerId) {
		BigDecimal bargainFloorPrice = tEventBargaining.getFfloorPrice1();

		Date now = new Date();
		int count3 = tEventBargaining.getFstock3()
				- customerBargainingDAO.getByBargainingId(tEventBargaining.getId(), 3).size();
		int count2 = tEventBargaining.getFstock2()
				- customerBargainingDAO.getByBargainingId(tEventBargaining.getId(), 2).size();
		TCustomerBargaining tCustomerBargaining = new TCustomerBargaining();
		
		//随机出1/20可以砍至0元
		int randomNum = RandomUtils.nextInt(1, 20);
		if (randomNum <= 1 && count3 > 0) {
			bargainFloorPrice = tEventBargaining.getFfloorPrice3();
			tCustomerBargaining.setFdefaultLevel(3);
		}/* else if (randomNum > 1 && randomNum <= 3 && count2 > 0) {
			bargainFloorPrice = tEventBargaining.getFfloorPrice2();
			tCustomerBargaining.setFdefaultLevel(2);
		}*/ else {
			bargainFloorPrice = tEventBargaining.getFfloorPrice1();
			tCustomerBargaining.setFdefaultLevel(1);
		}
		String number = this.getMAXPointCode();
		Integer qrcodeNum = Integer.parseInt(number) + 1;

		tCustomerBargaining.setFbargainingCount(0);
		tCustomerBargaining.setFbargainingId(tEventBargaining.getId());
		tCustomerBargaining.setFcustomerId(customerId);
		tCustomerBargaining.setFcreateTime(now);
		tCustomerBargaining.setFdeadline(tEventBargaining.getFendTime());
		tCustomerBargaining.setFdefaultFloorPrice(bargainFloorPrice);
		tCustomerBargaining.setFendPrice(tEventBargaining.getFstartPrice());
		tCustomerBargaining.setFstartPrice(tEventBargaining.getFstartPrice());
		tCustomerBargaining.setFstatus(20);// 0代表有效占用库存
		tCustomerBargaining.setFbargainingNum(qrcodeNum.toString());
		customerBargainingDAO.save(tCustomerBargaining);
		return tCustomerBargaining;
	}

	/**
	 * 计算用户每次帮砍掉多少金额
	 */
	public BigDecimal calculateBargainPrice(TCustomerBargaining tCustomerBargaining, String fHelperId) {
		TEventBargaining tEventBargaining = eventBargainingDAO.findOne(tCustomerBargaining.getFbargainingId());

		BigDecimal Z = BigDecimal.ZERO;// 砍掉价格
		BigDecimal A = tEventBargaining.getFstartPrice();// 原价
		BigDecimal B = tCustomerBargaining.getFdefaultFloorPrice();// 能砍到最低价
		BigDecimal min = tEventBargaining.getFminBargaining();// 最少砍掉的价格
		BigDecimal max = tEventBargaining.getFmaxBargaining();// 最多砍掉的价格
		BigDecimal X = tCustomerBargaining.getFendPrice();// 砍完之后剩余的价格
		/*砍价出现正数次数*/
		Integer bargainCount = Integer.parseInt(
				ConfigurationUtil.getPropertiesValue(ResponseConfigurationDict.RESPONSE_PROPERTIES_BARGAINCOUNT));
		/*砍价出现正数概率*/
		Integer bargainNegative = Integer.parseInt(
				ConfigurationUtil.getPropertiesValue(ResponseConfigurationDict.RESPONSE_PROPERTIES_BARGAINNEGATIVE));
		// 如果砍完之后剩余的价格等于能砍到的最低低价
		if (X.compareTo(B) == 0) {
			Z = (new BigDecimal(RandomUtils.nextDouble((new BigDecimal(0.01).multiply(A)).doubleValue(),
					(new BigDecimal(0.03).multiply(A)).doubleValue()))).multiply(new BigDecimal(-1));
		} else {
			// 还可以砍的价格如果比最低可以砍的价低
			if ((X.subtract(B)).compareTo(min) <= 0) {
				Z = X.subtract(B);
			} else {
				BigDecimal randomNum = new BigDecimal(RandomUtils.nextDouble(min.doubleValue(), max.doubleValue()));
				// 还可以砍的价格除以最多可以砍的价格
				Z = (X.subtract(B)).divide(A.subtract(B), 2).multiply(randomNum);
				if (Z.compareTo(min) < 0) {
					Z = min.add(Z);
					if ((X.subtract(B)).compareTo(Z) < 0) {
						Z = X.subtract(B);
					}
				}
				if (Z.compareTo(max) > 0) {
					Z = max;
				}
				int count = helpBargainingDetailDAO.getDetailByBargainId(tCustomerBargaining.getId()).intValue();
				if (count > bargainCount.intValue()) {
					// 随机一定概率的负数
					if (RandomUtils.nextInt(1, 100) <= bargainNegative.intValue()) {
						Z = Z.multiply(new BigDecimal(-1));
					}

				}
			}
		}
		Z = Z.multiply(new BigDecimal(-1)).setScale(2, RoundingMode.HALF_UP);
		tCustomerBargaining.setFendPrice(tCustomerBargaining.getFendPrice().add(Z));
		tCustomerBargaining.setFbargainingCount(tCustomerBargaining.getFbargainingCount() + 1);
		tCustomerBargaining.setFstatus(20);
		tCustomerBargaining = customerBargainingDAO.save(tCustomerBargaining);
		THelpBargainingDetail tHelpBargainingDetail = new THelpBargainingDetail();
		tHelpBargainingDetail.setFbargainingId(tCustomerBargaining.getFbargainingId());
		tHelpBargainingDetail.setFchangeAmount(Z);
		tHelpBargainingDetail.setFchangePrice(tCustomerBargaining.getFendPrice());
		tHelpBargainingDetail.setFcreateTime(new Date());
		tHelpBargainingDetail.setFcustomerBargainingId(tCustomerBargaining.getId());
		tHelpBargainingDetail.setFhelperId(fHelperId);
		tHelpBargainingDetail.setFkykType(2);
		helpBargainingDetailDAO.save(tHelpBargainingDetail);
		return Z;

	}

	public ResponseDTO getMyBargain(String eventBargainingId, String customerId, String customerBargainingId) {

		ResponseDTO responseDTO = new ResponseDTO();
		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}
		TEventBargaining tEventBargaining = eventBargainingDAO.findOne(eventBargainingId);
		if (tEventBargaining == null) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(202);
			responseDTO.setMsg("eventBargainingId参数有误，请检查eventBargainingId的传递参数值！");
			return responseDTO;
		}
		boolean ifHaveBargained = false;// 是否有人帮我砍
		boolean isMyBargain = true;// 是否是本人的砍一砍首页
		boolean ifHelpBargained = false;// 是否帮他砍了
		TCustomerBargaining Bargaining = null;
		TCustomer tCustomer = customerDAO.findOne(customerId);
		if (StringUtils.isNotBlank(customerBargainingId)) {
			Bargaining = customerBargainingDAO.findOne(customerBargainingId);
			if (Bargaining.getFcustomerId().equals(customerId)) {

			} else {
				isMyBargain = false;
				int countHelp = helpBargainingDetailDAO.getByBargainIdAndBargainId(customerId, customerBargainingId)
						.intValue();
				String helpBargainCount = ConfigurationUtil
						.getPropertiesValue(ResponseConfigurationDict.RESPONSE_PROPERTIES_HELPBARGAINCOUNT);
				if (StringUtils.isNotBlank(helpBargainCount) && countHelp >= Integer.parseInt(helpBargainCount)) {
					ifHelpBargained = true;
				}
			}
		} else {
			Bargaining = customerBargainingDAO.getByCustomerId(customerId, tEventBargaining.getId());
		}

		TSceneUser sceneUser = sceneUserDAO.findOneByOpenid(tCustomer.getFweixinId());
		if (Bargaining == null) {
			Bargaining = this.bargainFloorPrice(tEventBargaining, customerId);
		} else {
			if (Bargaining.getFbargainingCount() != 0) {
				ifHaveBargained = true;
			}
		}
		
		String ifcanBargain = ConfigurationUtil
				.getPropertiesValue(ResponseConfigurationDict.RESPONSE_PROPERTIES_COUNTBARGAIN);

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("isMyBargain", isMyBargain);// 是否是自己进入自己的首页
		TEvent event = eventDAO.findOne(tEventBargaining.getFeventId());
		returnData.put("eventBargainImage", fxlService.getImageUrl(event.getFimage1().toString(), false));
		if (tEventBargaining.getFtitle() != null && StringUtils.isNotBlank(tEventBargaining.getFtitle())) {
			returnData.put("eventBargainTitle", tEventBargaining.getFtitle());
		}
		if (tEventBargaining.getFpackageDesc() != null && StringUtils.isNotBlank(tEventBargaining.getFpackageDesc())) {
			returnData.put("subTitle", tEventBargaining.getFpackageDesc());
		}
		returnData.put("customerImage", customerDAO.findOne(Bargaining.getFcustomerId()).getFphoto());
		returnData.put("customerName", customerDAO.findOne(Bargaining.getFcustomerId()).getFname());
		Integer second = Seconds.secondsBetween(new DateTime(new Date()), new DateTime(tEventBargaining.getFendTime()))
				.getSeconds();
		if (second.intValue() <= 0) {
			returnData.put("leftTime", 0);
			returnData.put("eventBargainStatus", 10);
		} else {
			returnData.put("leftTime", second);
			returnData.put("eventBargainStatus", tEventBargaining.getFstatus());
		}
		returnData.put("goodsId", tEventBargaining.getFeventId());
		if(sceneUser!=null){
			returnData.put("subscribe", sceneUser.getFsubscribe());
		}else{
			returnData.put("subscribe", 0);
		}
		returnData.put("customerBargainingId", Bargaining.getId());
		returnData.put("ifHaveBargained", ifHaveBargained);// 是否有人帮砍了
		returnData.put("ifHelpBargained", ifHelpBargained);// 是否帮砍了
		returnData.put("BargainAfterPrice", Bargaining.getFendPrice().toString());
		returnData.put("originPrice", Bargaining.getFstartPrice().toString());
		returnData.put("BargainCount", Bargaining.getFbargainingCount());
		returnData.put("BargainPrice", Bargaining.getFstartPrice().subtract(Bargaining.getFendPrice()).toString());
		returnData.put("customerBargainStatus", Bargaining.getFstatus());
		returnData.put("orderId", Bargaining.getForderId());
		returnData.put("percentage", Bargaining.getFstartPrice().subtract(Bargaining.getFendPrice())
			.multiply(new BigDecimal(100)).divide(Bargaining.getFstartPrice(),2,RoundingMode.HALF_UP).intValue());
		int count = 0;
		if (Bargaining.getFdefaultLevel() == 1) {
			count = tEventBargaining.getFremainingStock1();
		} else if (Bargaining.getFdefaultLevel() == 2) {
			count = tEventBargaining.getFremainingStock2();
		} else if (Bargaining.getFdefaultLevel() == 3) {
			count = tEventBargaining.getFremainingStock3();
		}
		returnData.put("stock", count);
		returnData.put("count", Integer.parseInt(ifcanBargain));
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("加载砍一砍活动首页成功");
		return responseDTO;
	}

	public ResponseDTO helpBargain(String customerId, String customerBargainingId) {

		ResponseDTO responseDTO = new ResponseDTO();
		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}
		TCustomerBargaining tCustomerBargaining = customerBargainingDAO.findOne(customerBargainingId);
		TCustomerInfo tCustomerInfo = customerInfoDAO.getByCustomerId(customerId);

		int countBargain = helpBargainingDetailDAO.getDetailByHelper(customerId, tCustomerBargaining.getFbargainingId())
				.intValue();
		int countHelp = helpBargainingDetailDAO.getByBargainIdAndBargainId(customerId, customerBargainingId).intValue();
		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		Map<String, Object> returnData = Maps.newHashMap();

		String helpBargainCount = ConfigurationUtil
				.getPropertiesValue(ResponseConfigurationDict.RESPONSE_PROPERTIES_HELPBARGAINCOUNT);
		if (StringUtils.isNotBlank(helpBargainCount) && countHelp >= Integer.parseInt(helpBargainCount)) {
			returnData.put("status", 4);
			returnData.put("bargainPrice", BigDecimal.ZERO);
			responseDTO.setData(returnData);
			responseDTO.setMsg("每个用户只能帮砍" + helpBargainCount + "次！");
			return responseDTO;
		}
		String ifcanBargain = ConfigurationUtil
				.getPropertiesValue(ResponseConfigurationDict.RESPONSE_PROPERTIES_COUNTBARGAIN);
		if (StringUtils.isNotBlank(ifcanBargain) && countBargain >= Integer.parseInt(ifcanBargain)) {
			returnData.put("status", 3);
			returnData.put("bargainPrice", BigDecimal.ZERO);
			returnData.put("count", Integer.parseInt(ifcanBargain));
			responseDTO.setData(returnData);
			responseDTO.setMsg("休息一下，每个用户只能参加三次次活动！");
			return responseDTO;
		}

		if (tCustomerBargaining.getFstatus().intValue() != 20) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(205);
			responseDTO.setMsg("当前砍一砍活动已结束");
			return responseDTO;
		}

		BigDecimal bargainPrice = BigDecimal.ZERO;
		if (tCustomerInfo.getFkykFlag() != null && tCustomerInfo.getFkykFlag().intValue() == 1) {
			bargainPrice = this.newCustomerCalculatePrice(tCustomerBargaining, customerId);
			returnData.put("bargainPrice", "¥"+bargainPrice.divide(new BigDecimal(3), 2, RoundingMode.HALF_UP).abs() + "X3");
			returnData.put("ifNewConcern", true);
			customerInfoDAO.updateKykFlag(customerId, 2);
		} else {
			bargainPrice = this.calculateBargainPrice(tCustomerBargaining, customerId);
			if (bargainPrice.compareTo(BigDecimal.ZERO) <= 0) {
				if (bargainPrice.abs().compareTo(new BigDecimal(5)) == 1) {
					returnData.put("opposite", 4);
				}else if (bargainPrice.abs().compareTo(new BigDecimal(3)) == -1) {
					returnData.put("opposite", 2);
				}else{
					returnData.put("opposite", 3);
				}
			} else if (bargainPrice.compareTo(BigDecimal.ZERO) > 0) {
				returnData.put("opposite", 1);
			}
			returnData.put("bargainPrice", "¥"+bargainPrice.abs());
			returnData.put("ifNewConcern", false);
			
			//如果帮砍人数量大于4次，且是第一等级，且第二等级库存还有剩余，则修改为第二等级
			if(tCustomerBargaining.getFbargainingCount().intValue()>4 && tCustomerBargaining.getFdefaultLevel().intValue()==1){
				TEventBargaining eventBargaining = eventBargainingDAO.findOne(tCustomerBargaining.getFbargainingId());
				if(eventBargaining!=null && eventBargaining.getFremainingStock2()>=0){
					customerBargainingDAO.updateLevel(tCustomerBargaining.getId(), 2,eventBargaining.getFfloorPrice2());
				}
			}
		}
		if (bargainPrice.compareTo(BigDecimal.ZERO) < 0) {
			returnData.put("status", 1);
		} else {
			returnData.put("status", 2);
		}
		responseDTO.setData(returnData);
		return responseDTO;
	}

	/**
	 * 砍价英雄榜
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public ResponseDTO getbargainList(String customerId, String eventBargainingId,
			Integer pageSize, Integer offset) {
		ResponseDTO responseDTO = new ResponseDTO();
		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}
		if (StringUtils.isBlank(eventBargainingId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(202);
			responseDTO.setMsg("eventBargainingId参数不能为空，请检查eventBargainingId的传递参数值！");
			return responseDTO;
		}

		CommonPage page = new CommonPage();
		if (pageSize != null) {
			page.setPageSize(pageSize);
		}
		if (offset != null) {
			page.setOffset(offset);
		}
		StringBuilder hql = new StringBuilder();
		hql.append(
				"select t.id as id,t.fcustomerId as fcustomerId,t.fendPrice as fendPrice from TCustomerBargaining t where t.fbargainingId=:eventBargainingId");

		Map<String, Object> hqlMap = new HashMap<String, Object>();
		hqlMap.put("eventBargainingId", eventBargainingId);
		hql.append(" order by t.fendPrice asc");
		commonService.findPage(hql.toString(), page, hqlMap);
		List<Map<String, Object>> list = page.getResult();

		List<Map<String, Object>> bargainList = Lists.newArrayList();
		Integer rank = 1;
		for (Map<String, Object> amap : list) {
			Map<String, Object> bonusEventMap = new HashMap<String, Object>();

			if (amap.get("fcustomerId") != null && StringUtils.isNotBlank(amap.get("fcustomerId").toString())) {
				CustomerDTO custDTO = fxlService.getCustomerByCustomerId(amap.get("fcustomerId").toString());
				bonusEventMap.put("name", ArrayStringUtils.addAsterisk(custDTO.getName()));
			}
			if (amap.get("fendPrice") != null && StringUtils.isNotBlank(amap.get("fendPrice").toString())) {
				bonusEventMap.put("endPrice", "¥"+amap.get("fendPrice").toString());
			}
			bonusEventMap.put("rank", page.getOffset() + rank++);
			bargainList.add(bonusEventMap);
		}

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("bargainList", bargainList);
		PageDTO pageDTO = new PageDTO(page.getTotalCount(), page.getPageSize(), page.getOffset());
		returnData.put("page", pageDTO);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("加载砍价英雄榜列表成功");
		return responseDTO;
	}

	/**
	 * 帮砍列表
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public ResponseDTO getbargainHelpList(String customerId, String customerBargainingId, Integer pageSize,
			Integer offset) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}
		if (StringUtils.isBlank(customerBargainingId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(202);
			responseDTO.setMsg("customerBargainingId参数不能为空，请检查customerBargainingId的传递参数值！");
			return responseDTO;
		}

		CommonPage page = new CommonPage();
		if (pageSize != null) {
			page.setPageSize(pageSize);
		}
		if (offset != null) {
			page.setOffset(offset);
		}
		StringBuilder hql = new StringBuilder();
		hql.append(
				"select t.id as id,t.fhelperId as fhelperId,t.fchangeAmount as fchangeAmount,t.fchangePrice as fchangePrice,t.fkykType as fkykType,t.fcreateTime as createTime from THelpBargainingDetail t")
				.append(" where t.fcustomerBargainingId=:fcustomerBargainingId");

		Map<String, Object> hqlMap = new HashMap<String, Object>();
		hqlMap.put("fcustomerBargainingId", customerBargainingId);
		hql.append(" order by t.fcreateTime desc");
		commonService.findPage(hql.toString(), page, hqlMap);
		List<Map<String, Object>> list = page.getResult();

		List<Map<String, Object>> bargainHelpList = Lists.newArrayList();
		String changeAmount = null;
		for (Map<String, Object> amap : list) {
			Map<String, Object> bonusEventMap = new HashMap<String, Object>();

			CustomerDTO custDTO = fxlService.getCustomerByCustomerId(amap.get("fhelperId").toString());
			bonusEventMap.put("name", custDTO.getName());
			bonusEventMap.put("photo", HeadImageUtil.getHeadImage(custDTO.getPhoto(), 46));

			if (amap.get("fkykType") != null && StringUtils.isNotBlank(amap.get("fkykType").toString())
					&& (Integer.parseInt(amap.get("fkykType").toString())) == 1) {
				bonusEventMap.put("ifNewConcern", true);
				if (amap.get("fchangeAmount") != null && StringUtils.isNotBlank(amap.get("fchangeAmount").toString())) {
					changeAmount = new StringBuilder().append("¥").append(((BigDecimal) amap.get("fchangeAmount"))
							.abs().divide(new BigDecimal(3), 2, RoundingMode.HALF_UP)).append("X3").toString();
					bonusEventMap.put("changeAmount", changeAmount);
					if (((BigDecimal) amap.get("fchangeAmount")).abs().compareTo(new BigDecimal(5)) == 1) {
						bonusEventMap.put("opposite", 4);
					}else if (((BigDecimal) amap.get("fchangeAmount")).abs().compareTo(new BigDecimal(3)) == -1) {
						bonusEventMap.put("opposite", 2);
					}else{
						bonusEventMap.put("opposite", 3);
					}
				}
			} else {
				bonusEventMap.put("ifNewConcern", false);
				if (amap.get("fchangeAmount") != null && StringUtils.isNotBlank(amap.get("fchangeAmount").toString())) {
					if (((BigDecimal) amap.get("fchangeAmount")).compareTo(BigDecimal.ZERO) <= 0) {
						changeAmount = new StringBuilder().append("¥")
								.append(((BigDecimal) amap.get("fchangeAmount")).abs()).toString();
						if (((BigDecimal) amap.get("fchangeAmount")).abs().compareTo(new BigDecimal(5)) == 1) {
							bonusEventMap.put("opposite", 4);
						}else if (((BigDecimal) amap.get("fchangeAmount")).abs().compareTo(new BigDecimal(3)) == -1) {
							bonusEventMap.put("opposite", 2);
						}else{
							bonusEventMap.put("opposite", 3);
						}
					} else if (((BigDecimal) amap.get("fchangeAmount")).compareTo(BigDecimal.ZERO) > 0) {
						changeAmount = new StringBuilder().append("¥")
								.append(((BigDecimal) amap.get("fchangeAmount")).abs()).toString();
						bonusEventMap.put("opposite", 1);
					}
					bonusEventMap.put("changeAmount", changeAmount);
				}
			}
			if (amap.get("fchangePrice") != null && StringUtils.isNotBlank(amap.get("fchangePrice").toString())) {
				bonusEventMap.put("changePrice", "¥"+amap.get("fchangePrice").toString());
			}
			if (amap.get("createTime") != null && StringUtils.isNotBlank(amap.get("createTime").toString())) {
				bonusEventMap.put("createTime",DateFormatUtils.format((Date)amap.get("createTime"), "yyyy-MM-dd"));
			}
			bargainHelpList.add(bonusEventMap);
		}

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("bargainHelpList", bargainHelpList);
		PageDTO pageDTO = new PageDTO(page.getTotalCount(), page.getPageSize(), page.getOffset());
		returnData.put("page", pageDTO);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("加载帮砍用户列表成功");
		return responseDTO;
	}

	/**
	 * 弹幕列表
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public ResponseDTO getbarrage(String customerId, String customerBargainingId, Integer pageSize, Integer offset) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}
		if (StringUtils.isBlank(customerBargainingId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(202);
			responseDTO.setMsg("customerBargainingId参数不能为空，请检查customerBargainingId的传递参数值！");
			return responseDTO;
		}

		CommonPage page = new CommonPage();
		if (pageSize != null) {
			page.setPageSize(pageSize);
		}
		if (offset != null) {
			page.setOffset(offset);
		}

		StringBuilder hql = new StringBuilder();
		hql.append(
				"select t.id as id,t.fhelperId as fhelperId,t.fchangeAmount as fchangeAmount,t.fchangePrice as fchangePrice,t.fbargainingId as fbargainingId from THelpBargainingDetail t where t.fcustomerBargainingId=:fcustomerBargainingId");

		Map<String, Object> hqlMap = new HashMap<String, Object>();
		hqlMap.put("fcustomerBargainingId", customerBargainingId);
		hql.append(" order by t.fcreateTime desc ");
		commonService.findPage(hql.toString(), page, hqlMap);
		List<Map<String, Object>> list = page.getResult();
		// List<Map<String, Object>> list = commonService.find(hql.toString(),
		// hqlMap);

		List<Map<String, Object>> getbarrageList = Lists.newArrayList();
		TCustomerBargaining tCustomerBargaining = customerBargainingDAO.getOne(customerBargainingId);
		TCustomer tCustomer2 = customerDAO.getOne(tCustomerBargaining.getFcustomerId());
		CustomerDTO custDTO = null;
		Map<String, Object> barrageMap = null;
		String name = null;
		String names = null;
		if (tCustomer2.getFname().length() > 5) {
			name = tCustomer2.getFname().substring(0, 5);
		} else {
			name = tCustomer2.getFname();
		}
		for (Map<String, Object> amap : list) {
			barrageMap = Maps.newHashMap();

			if (amap.get("fhelperId") != null && StringUtils.isNotBlank(amap.get("fhelperId").toString())) {
				custDTO = fxlService.getCustomerByCustomerId(amap.get("fhelperId").toString());
				barrageMap.put("headUrl", HeadImageUtil.getHeadImage(custDTO.getPhoto(), 46));
				BigDecimal bd = (BigDecimal) amap.get("fchangeAmount");
				BigDecimal ac = BigDecimal.ZERO;

				if (custDTO.getName().length() > 5) {
					names = custDTO.getName().substring(0, 5);
				} else {
					names = custDTO.getName();
				}
				StringBuilder sb = new StringBuilder();
				if (bd.compareTo(ac) == -1) {
					sb.append(names).append("帮小伙伴").append(name);
					if (bd.compareTo(new BigDecimal(5)) == 1) {
						sb.append("大刀阔斧砍掉了");
					}else if (bd.compareTo(new BigDecimal(3)) == -1) {
						sb.append("小李飞刀飞掉了");
					}else{
						sb.append("粉红小拳拳捶掉了");
					}
					sb.append("￥")
							.append(bd.abs().setScale(2, RoundingMode.HALF_UP)).append(",")
							.append(RandomUtils.nextInt(1, 5)).append("秒前");
				} else {
					sb.append(names).append("帮").append(name).append("手滑了一下添了￥")
							.append(bd.setScale(2, RoundingMode.HALF_UP)).append(",").append(RandomUtils.nextInt(1, 5))
							.append("秒前");
				}
				barrageMap.put("textDes", sb.toString());
			}
			getbarrageList.add(barrageMap);
		}
		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("getbarrageList", getbarrageList);
		responseDTO.setData(returnData);
		return responseDTO;
	}

	/**
	 * 分享当前砍一砍
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public ResponseDTO getbargainShare(String customerBargainingId, HttpServletRequest request) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (StringUtils.isBlank(customerBargainingId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(107);
			responseDTO.setMsg("customerBargainingId参数不能为空，请检查customerBargainingId的传递参数值！");
			return responseDTO;
		}

		Map<String, Object> shareBargaining = new HashMap<String, Object>();
		TCustomerBargaining tCustomerBargaining = customerBargainingDAO.getOne(customerBargainingId);
		TEventBargaining tEventBargaining = eventBargainingDAO.getOne(tCustomerBargaining.getFbargainingId());
		// String url =
		// fxlService.getImageUrl(String.valueOf(tEventBargaining.getFimage()),
		// true);

		shareBargaining.put("title", tEventBargaining.getFtitle());
		shareBargaining.put("text", tEventBargaining.getFinputText());
		shareBargaining.put("imageUrl", new StringBuilder(PropertiesUtil.getProperty("fileServerUrl"))
				.append(PropertiesUtil.getProperty("imageRootPath")).append("/foms/kyk1.png").toString());
		StringBuilder shareUrl = new StringBuilder();
		shareUrl.append(request.getScheme()).append("://").append(request.getServerName()).append(":")
				.append(request.getServerPort()).append(request.getContextPath())
				.append("/api/system/share/shareBargain/").append(tCustomerBargaining.getFbargainingId()).append("/")
				.append(customerBargainingId);
		shareBargaining.put("url", shareUrl.toString());

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("shareBargaining", shareBargaining);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("分享砍一砍活动成功");
		return responseDTO;
	}

	/*
	 * @Transactional(readOnly = true) public ResponseDTO
	 * bargainTFillOrder(Integer clientType, String ticket, String
	 * customerBargainingId) { ResponseDTO responseDTO = new ResponseDTO(); if
	 * (StringUtils.isBlank(ticket)) { responseDTO.setSuccess(false);
	 * responseDTO.setStatusCode(201);
	 * responseDTO.setMsg("ticket参数不能为空，请检查ticket的传递参数值！"); return responseDTO;
	 * } if (StringUtils.isBlank(customerBargainingId)) {
	 * responseDTO.setSuccess(false); responseDTO.setStatusCode(202);
	 * responseDTO.setMsg(
	 * "customerBargainingId参数不能为空，请检查customerBargainingId的传递参数值！"); return
	 * responseDTO; } CustomerDTO customerDTO =
	 * fxlService.getCustomerByTicket(ticket, clientType); if
	 * (!customerDTO.isEnable()) { responseDTO.setSuccess(false);
	 * responseDTO.setStatusCode(customerDTO.getStatusCode());
	 * responseDTO.setMsg(customerDTO.getMsg()); return responseDTO; }
	 * TCustomerBargaining tCustomerBargaining =
	 * customerBargainingDAO.findOne(customerBargainingId); TEventBargaining
	 * tEventBargaining =
	 * eventBargainingDAO.findOne(tCustomerBargaining.getFbargainingId()); if
	 * (tEventBargaining == null) { responseDTO.setSuccess(false);
	 * responseDTO.setStatusCode(205); responseDTO
	 * .setMsg("您输入的customerBargainingId参数有误，customerBargainingId=“" +
	 * customerBargainingId + "”的活动不存在！"); return responseDTO; } if
	 * (tEventBargaining.getFstatus().intValue() != 20) {
	 * responseDTO.setSuccess(false); responseDTO.setStatusCode(205);
	 * responseDTO.setMsg("该砍一砍活动已结束"); return responseDTO; } // 限购规则器判断是否可以购买
	 * int count = 0; if (tCustomerBargaining.getFdefaultLevel() == 1) { count =
	 * tEventBargaining.getFremainingStock1(); } else if
	 * (tCustomerBargaining.getFdefaultLevel() == 2) { count =
	 * tEventBargaining.getFremainingStock2(); } else if
	 * (tCustomerBargaining.getFdefaultLevel() == 3) { count =
	 * tEventBargaining.getFremainingStock3(); } if (count <= 0) {
	 * responseDTO.setSuccess(false); responseDTO.setStatusCode(107);
	 * responseDTO.setMsg("本活动已被抢完，请留意参加抢购其它活动！"); return responseDTO; }
	 * 
	 * TEvent tEvent = eventDAO.findOne(tEventBargaining.getFeventId());
	 * 
	 * 
	 * BigDecimal total = tEventBargaining.getFstartPrice().setScale(2,
	 * RoundingMode.HALF_UP);
	 * 
	 * BuyInfoDTO buyInfoDTO = new BuyInfoDTO(); String couponDeliveryId = null;
	 * BigDecimal maxAmount = BigDecimal.ZERO; // 获取出该订单可使用最优优惠券 maxAmount =
	 * tCustomerBargaining.getFstartPrice().subtract(tCustomerBargaining.
	 * getFendPrice()); buyInfoDTO.setCouponDeliveryId(couponDeliveryId);
	 * buyInfoDTO.setAmount(maxAmount);
	 * buyInfoDTO.setOrderType(tEvent.getForderType());
	 * buyInfoDTO.setAppointment(tEvent.getFappointment());
	 * buyInfoDTO.setReturnReplace(tEvent.getFreturn());
	 * buyInfoDTO.setUsePreferential(tEvent.getFusePreferential());
	 * buyInfoDTO.setEventId(tEventBargaining.getFeventId());
	 * buyInfoDTO.setEventTitle(tEvent.getFtitle());
	 * buyInfoDTO.setSessionId(tEventBargaining.getFsessionId());
	 * buyInfoDTO.setSessionTitle(tEventBargaining.getFsessionTitle());
	 * buyInfoDTO.setSpecId(tEventBargaining.getFspecId());
	 * buyInfoDTO.setSpecTitle(tEventBargaining.getFspecTitle()); int specPerson
	 * = 0; if (tEventSpec.getFadult() != null) { specPerson +=
	 * tEventSpec.getFadult().intValue(); } if (tEventSpec.getFchild() != null)
	 * { specPerson += tEventSpec.getFchild().intValue(); }
	 * buyInfoDTO.setSpecPerson(specPerson); // 返回活动附属信息
	 * buyInfoDTO.setPrice(tEventSpec.getFprice());
	 * buyInfoDTO.setDeal(tEventBargaining.getFstartPrice());
	 * buyInfoDTO.setCount(1); buyInfoDTO.setPostage(tEventSpec.getFpostage());
	 * buyInfoDTO.setReceivableTotal(total); if
	 * (total.subtract(maxAmount).compareTo(BigDecimal.ZERO) < 0) {
	 * buyInfoDTO.setTotal(BigDecimal.ZERO); } else {
	 * buyInfoDTO.setTotal(total.subtract(maxAmount)); } if
	 * (tEvent.getForderType().intValue() == 2) { List<TCommonInfo>
	 * tCommonInfoList =
	 * commonInfoDAO.findByFcustomerIdAndFtype(customerDTO.getCustomerId(), 1);
	 * if (CollectionUtils.isNotEmpty(tCommonInfoList)) { TCommonInfo
	 * tCommonInfo = tCommonInfoList.get(0); if
	 * (StringUtils.isNotBlank(tCommonInfo.getFinfo())) { RecipientDTO
	 * recipientDTO = mapper.fromJson(tCommonInfo.getFinfo(),
	 * RecipientDTO.class); if (recipientDTO != null) {
	 * buyInfoDTO.setRecipient(recipientDTO.getRecipient());
	 * buyInfoDTO.setPhone(recipientDTO.getPhone());
	 * buyInfoDTO.setAddress(recipientDTO.getAddress()); } } } } //
	 * 判断客户是否有手机信息，如果有则直接返回 String phone = customerDTO.getPhone(); if
	 * (StringUtils.isNotBlank(phone)) { buyInfoDTO.setPhone(phone); } else { //
	 * 如果客户信息没有手机信息，则获取该客户上次下单保存的手机信息 List<TCommonInfo> tCommonInfoList =
	 * commonInfoDAO.findByFcustomerIdAndFtype(customerDTO.getCustomerId(), 3);
	 * if (CollectionUtils.isNotEmpty(tCommonInfoList)) { TCommonInfo
	 * tCommonInfo = tCommonInfoList.get(0); if
	 * (StringUtils.isNotBlank(tCommonInfo.getFinfo())) {
	 * buyInfoDTO.setPhone(tCommonInfo.getFinfo()); } } }
	 * 
	 * responseDTO.setSuccess(true); responseDTO.setStatusCode(0); Map<String,
	 * Object> returnData = Maps.newHashMap(); returnData.put("buyInfo",
	 * buyInfoDTO); returnData.put("customerBargainingId",
	 * customerBargainingId); responseDTO.setData(returnData); return
	 * responseDTO; }
	 */

	/*
	 * public ResponseDTO bargainToPayOrder(Integer clientType, String ticket,
	 * Integer payClientType, String recipient, String phone, String address,
	 * String insuranceInfo, String remark, Integer payType, String ip, Integer
	 * channel, String gps, String customerBargainingId, String deviceId) {
	 * ResponseDTO responseDTO = new ResponseDTO(); if
	 * (StringUtils.isBlank(ticket)) { responseDTO.setSuccess(false);
	 * responseDTO.setStatusCode(201);
	 * responseDTO.setMsg("ticket参数不能为空，请检查ticket的传递参数值！"); return responseDTO;
	 * } if (payType == null) { responseDTO.setSuccess(false);
	 * responseDTO.setStatusCode(205);
	 * responseDTO.setMsg("payType参数不能为空，请检查payType的传递参数值！"); return
	 * responseDTO; } CustomerDTO customerDTO =
	 * fxlService.getCustomerByTicket(ticket, clientType); if
	 * (!customerDTO.isEnable()) { responseDTO.setSuccess(false);
	 * responseDTO.setStatusCode(customerDTO.getStatusCode());
	 * responseDTO.setMsg(customerDTO.getMsg()); return responseDTO; } //
	 * 限购规则器判断是否可以购买 TCustomerBargaining tCustomerBargaining =
	 * customerBargainingDAO.findOne(customerBargainingId); TEventBargaining
	 * tEventBargaining =
	 * eventBargainingDAO.findOne(tCustomerBargaining.getFbargainingId()); if
	 * (tEventBargaining.getFstatus().intValue() != 20) {
	 * responseDTO.setSuccess(false); responseDTO.setStatusCode(205);
	 * responseDTO.setMsg("该砍一砍活动已结束"); return responseDTO; } int count = 0; if
	 * (tCustomerBargaining.getFdefaultLevel() == 1) { count =
	 * tEventBargaining.getFremainingStock1(); } else if
	 * (tCustomerBargaining.getFdefaultLevel() == 2) { count =
	 * tEventBargaining.getFremainingStock2(); } else if
	 * (tCustomerBargaining.getFdefaultLevel() == 3) { count =
	 * tEventBargaining.getFremainingStock3(); } if (count <= 0) {
	 * responseDTO.setSuccess(false); responseDTO.setStatusCode(107);
	 * responseDTO.setMsg("亲，该商品已经被别人抢光啦，后续我们还会推出更多的好玩活动，敬请关注~！"); return
	 * responseDTO; }
	 * 
	 * if (tEventBargaining.getFremainingStock1().intValue() == 1) {
	 * BargainCountBean eso = new BargainCountBean();
	 * eso.setEventBargainId(tEventBargaining.getId());
	 * eso.setEventTitle(tEventBargaining.getFtitle()); eso.setTaskType(15);
	 * AsynchronousTasksManager.put(eso); }
	 * 
	 * TEvent tEvent = eventDAO.findOne(tEventBargaining.getFeventId());
	 * 
	 * String orderNum = NumberUtil.getOrderNum(1 != null ? tEvent.getFcity() :
	 * 0); TSponsor tSponsor = tEvent.getTSponsor(); TOrder tOrder = new
	 * TOrder();
	 * 
	 * TEventSession tEventSession =
	 * eventSessionDAO.getOne(tEventBargaining.getFsessionId());
	 * 
	 * TEventSpec tEventSpec =
	 * eventSpecDAO.getOne(tEventBargaining.getFspecId()); //
	 * eventDAO.subStock(count, eventId); // fxlService.addCustomerBuy(eventId,
	 * customerDTO.getCustomerId(), // count);
	 * 
	 * tOrder.setTEvent(tEvent); tOrder.setFcityId(tEvent.getFcity());
	 * tOrder.setFeventTitle(tEvent.getFtitle());
	 * tOrder.setForderType(tEvent.getForderType());
	 * tOrder.setFappointment(tEvent.getFappointment());
	 * tOrder.setFreturn(tEvent.getFreturn());
	 * tOrder.setFusePreferential(tEvent.getFusePreferential());
	 * tOrder.setFverificationType(tEvent.getFverificationType()); //
	 * 获取到活动类目缓存对象 
	 * eventCategoryCache.get(tEvent.getFtypeA()); tOrder.setFtypeA(ele != null
	 * ? ele.getObjectValue().toString() : null); tOrder.setTSponsor(tSponsor);
	 * tOrder.setFsponsorName(tSponsor.getFname());
	 * tOrder.setFsponsorFullName(tSponsor.getFfullName());
	 * tOrder.setFsponsorPhone(tSponsor.getFphone());
	 * tOrder.setFsponsorNumber(tSponsor.getFnumber());
	 * tOrder.setForderNum(orderNum); tOrder.setTCustomer(new
	 * TCustomer(customerDTO.getCustomerId()));
	 * tOrder.setFcustomerName(customerDTO.getName()); if
	 * (StringUtils.isNotBlank(phone)) { tOrder.setFcustomerPhone(phone); } else
	 * { tOrder.setFcustomerPhone(customerDTO.getPhone()); }
	 * tOrder.setFcustomerSex(DictionaryUtil.getString(DictionaryUtil.Sex,
	 * customerDTO.getSex())); tOrder.setTEventSession(tEventSession);
	 * tOrder.setFsessionTitle(tEventSession.getFtitle());
	 * tOrder.setTEventSpec(tEventSpec); //
	 * tOrder.setFspecTitle(tEventSpec.getFtitle()); tOrder.setFcount(1);
	 * tOrder.setFpostage(tEventSpec.getFpostage());
	 * tOrder.setFstockFlag(tEvent.getFstockFlag());
	 * tOrder.setFprice(tEventSpec.getFdeal()); BigDecimal total =
	 * tEventBargaining.getFstartPrice().setScale(2, RoundingMode.HALF_UP);
	 * tOrder.setFtotal(total); tOrder.setFreceivableTotal(total);
	 * tOrder.setFremark(remark); OrderRecipientDTO recipientDTO = new
	 * OrderRecipientDTO(); recipientDTO.setRecipient(recipient);
	 * recipientDTO.setPhone(phone); recipientDTO.setAddress(address);
	 * recipientDTO.setInsuranceInfo(insuranceInfo); int specPerson = 0; if
	 * (tEventSpec.getFadult() != null) { specPerson +=
	 * tEventSpec.getFadult().intValue(); } if (tEventSpec.getFchild() != null)
	 * { specPerson += tEventSpec.getFchild().intValue(); }
	 * recipientDTO.setSpecPerson(specPerson); String recipientJson =
	 * mapper.toJson(recipientDTO); tOrder.setFrecipient(recipientJson); Date
	 * now = new Date(); tOrder.setFcreateTime(now); Date fdate = null; if
	 * (Constant.getUnPayFailureMinute() != 0) { fdate =
	 * DateUtils.addMinutes(now, Constant.getUnPayFailureMinute());
	 * tOrder.setFunPayFailureTime(fdate); } tOrder.setFlockFlag(0);
	 * 
	 * // 如果客户没有手机号码，将电话信息保存到客户公共信息表中 if (StringUtils.isNotBlank(phone)) {
	 * TCommonInfo tCommonInfo = null; // 如果客户信息没有手机信息，则获取该客户上次下单保存的手机信息
	 * List<TCommonInfo> tCommonInfoList =
	 * commonInfoDAO.findByFcustomerIdAndFtype(customerDTO.getCustomerId(), 3);
	 * if (CollectionUtils.isNotEmpty(tCommonInfoList)) { tCommonInfo =
	 * tCommonInfoList.get(0); tCommonInfo.setFinfo(phone);
	 * tCommonInfo.setFupdateTime(now); } else { tCommonInfo = new
	 * TCommonInfo(); tCommonInfo.setFcreateTime(now);
	 * tCommonInfo.setFcustomerId(customerDTO.getCustomerId());
	 * tCommonInfo.setFtype(3); tCommonInfo.setFupdateTime(now);
	 * tCommonInfo.setFinfo(phone); } commonInfoDAO.save(tCommonInfo); }
	 * 
	 * // 增加1.下单渠道 2.下单地址(下单地址定位失败则用ip定位转换经纬度存入)3.唯一标识 boolean gpsIsError =
	 * false; if (StringUtils.isBlank(gps)) { gpsIsError = true; } else { if
	 * (!gps.contains(",") || gps.contains("E")) { gpsIsError = true; } String[]
	 * gpsa = StringUtils.split(gps, ','); if (gpsa.length != 2) { gpsIsError =
	 * true; } else if (gpsa.length == 2) { if
	 * (!NumberUtils.isCreatable(gpsa[0]) || !NumberUtils.isCreatable(gpsa[1]))
	 * { gpsIsError = true; } } else { gpsIsError = true; } }
	 * 
	 * tOrder.setFchannel(channel); tOrder.setFgps(gps);
	 * tOrder.setFdeviceId(deviceId); tOrder.setFsource(20); tOrder =
	 * orderDAO.save(tOrder);
	 * 
	 * // 修改砍一砍 tCustomerBargaining.setFstatus(30);// 砍价已下单
	 * tCustomerBargaining.setForderId(tOrder.getId());
	 * tCustomerBargaining.setForderTime(tOrder.getFcreateTime());
	 * customerBargainingDAO.save(tCustomerBargaining); // 修改砍一砍活动库存 Integer
	 * remainCount = 0; if (tCustomerBargaining.getFdefaultLevel() == 1) {
	 * tEventBargaining.setFremainingStock1(tEventBargaining.getFremainingStock1
	 * () - 1); } else if (tCustomerBargaining.getFdefaultLevel() == 2) {
	 * tEventBargaining.setFremainingStock2(tEventBargaining.getFremainingStock2
	 * () - 1); } else if (tCustomerBargaining.getFdefaultLevel() == 3) {
	 * tEventBargaining.setFremainingStock3(tEventBargaining.getFremainingStock3
	 * () - 1); } tEventBargaining = eventBargainingDAO.save(tEventBargaining);
	 * remainCount = tEventBargaining.getFremainingStock1() +
	 * tEventBargaining.getFremainingStock2() +
	 * tEventBargaining.getFremainingStock3(); String remainCountWXPush =
	 * ConfigurationUtil.getPropertiesValue(ResponseConfigurationDict.
	 * RESPONSE_PROPERTIES_REMAINCOUNTWXPUSH); if (remainCount.intValue() == 20
	 * &&(StringUtils.isNotBlank(remainCountWXPush)&&remainCountWXPush.equals(
	 * "1"))) { // 当库存低于20个时候，给所有参与砍价活动但是未支付的用户发送模板消息提醒 BargainCountBean eso =
	 * new BargainCountBean(); eso.setEventBargainId(tEventBargaining.getId());
	 * eso.setEventTitle(tEventBargaining.getFeventTitle());
	 * eso.setRemainingStock(remainCount); eso.setEventCode("无");
	 * eso.setTaskType(14); AsynchronousTasksManager.put(eso); }
	 * 
	 * // 如果传入的gps格式信息错误，将使用百度地图IP地址定位方式 if (gpsIsError) {
	 * OrderUpdateOrderGpsByIpBean ouog = new OrderUpdateOrderGpsByIpBean();
	 * ouog.setIp(ip); ouog.setOrderId(tOrder.getId()); ouog.setTaskType(6);
	 * AsynchronousTasksManager.put(ouog); } // 如果用户选择了优惠券进行付款，则进行优惠券抵扣 //
	 * 判断优惠券是否可用 TCustomer customer =
	 * customerDAO.findOne(customerDTO.getCustomerId());
	 * 
	 * BigDecimal maxAmount = BigDecimal.ZERO; // 获取出该订单可使用最优优惠券 maxAmount =
	 * tCustomerBargaining.getFstartPrice().subtract(tCustomerBargaining.
	 * getFendPrice()); total =
	 * tOrder.getFreceivableTotal().subtract(maxAmount); if
	 * (total.compareTo(BigDecimal.ZERO) < 0) { total = BigDecimal.ZERO; }
	 * tOrder.setFtotal(total); tOrder.setFchangeAmount(maxAmount);
	 * tOrder.setFchangeAmountInstruction("参加砍一砍活动抵扣了" + maxAmount.toString() +
	 * "元"); tOrder = orderDAO.save(tOrder);
	 * 
	 * // 保存优惠金额 TOrderAmountChange tOrderAmountChange = new
	 * TOrderAmountChange(); tOrderAmountChange.setFbonusChange(0);
	 * tOrderAmountChange.setFbargainChange(maxAmount);
	 * tOrderAmountChange.setFcouponChange(BigDecimal.ZERO);
	 * tOrderAmountChange.setFcreateTime(tOrder.getFcreateTime());
	 * tOrderAmountChange.setForderId(tOrder.getId());
	 * tOrderAmountChange.setFotherChange(BigDecimal.ZERO);
	 * tOrderAmountChange.setFspellChange(BigDecimal.ZERO);
	 * orderAmountChangeDAO.save(tOrderAmountChange);
	 * 
	 * Map<String, Object> returnData = Maps.newHashMap(); //
	 * 如果订单是则返回支付成功信息，如果是非零元单则返回支付信息 if
	 * (tOrder.getFtotal().compareTo(BigDecimal.ZERO) == 0) { //
	 * 如果是零元单，则直接记录订单状态变更为已支付状态 fxlService.orderStatusChange(1,
	 * customerDTO.getName(), tOrder.getId(), null, 0, 20); //
	 * 变更订单状态为已支付和支付类型是零元单支付 orderDAO.updateOrderStatusAndPayType(20, 90,
	 * tOrder.getId()); // 更改用户附加信息表 OrderUpdateCustomerInfoBean ouci = new
	 * OrderUpdateCustomerInfoBean(); ouci.setCreateTime(now);
	 * ouci.setCustomerId(customer.getId()); ouci.setOrderId(tOrder.getId());
	 * ouci.setTotal(tOrder.getFtotal()); ouci.setTaskType(2);
	 * AsynchronousTasksManager.put(ouci);
	 * 
	 * // 添加线程任务发送购买成功通知短信 OrderSendSmsBean oss = new OrderSendSmsBean();
	 * oss.setCreateTime(now); oss.setCustomerId(customer.getId());
	 * oss.setCustomerName(tOrder.getFcustomerName());
	 * oss.setCustomerPhone(tOrder.getFcustomerPhone());
	 * oss.setEventTitle(tOrder.getFeventTitle());
	 * oss.setOrderId(tOrder.getId()); oss.setOrderNum(tOrder.getForderNum());
	 * oss.setTaskType(3); AsynchronousTasksManager.put(oss);
	 * 
	 * // 发送购买成功通知短信 // this.sendPayZeroSuccessSms(tOrder);
	 * 
	 * // 修改用户砍一砍状态 tCustomerBargaining.setFstatus(40);// 砍价已支付
	 * tCustomerBargaining.setFpayTime(tOrder.getFpayTime());
	 * customerBargainingDAO.save(tCustomerBargaining);
	 * 
	 * returnData.put("zero", true); returnData.put("orderId", tOrder.getId());
	 * responseDTO.setMsg("订单支付成功，带着宝宝去体验吧！"); } else { //
	 * 如果是非零元单，则记录订单状态变更为未支付状态 fxlService.orderStatusChange(1,
	 * customerDTO.getName(), tOrder.getId(), null, 0, 10); //
	 * 变更订单状态为待支付和支付类型是相应支付类型 orderDAO.updateOrderStatusAndPayType(10, payType,
	 * tOrder.getId()); // 将添加订单超时未支付取消定时任务 if (fdate != null) { TTimingTask
	 * timingTask = new TTimingTask(); timingTask.setEntityId(tOrder.getId());
	 * timingTask.setTaskTime(fdate.getTime()); timingTask.setTaskType(7);
	 * timingTaskDAO.save(timingTask); } String orderPushWeiXin =
	 * ConfigurationUtil .getPropertiesValue(ResponseConfigurationDict.
	 * RESPONSE_PROPERTIES_UNPAIDORDER); if
	 * (StringUtils.isNotBlank(orderPushWeiXin) && orderPushWeiXin.equals("1"))
	 * { // 添加发送订单未支付通知模板消息 TTimingTask timingTask = new TTimingTask();
	 * timingTask.setEntityId(tOrder.getId());
	 * timingTask.setTaskTime(DateUtils.addMinutes(fdate, -20).getTime());
	 * timingTask.setTaskType(18); timingTaskDAO.save(timingTask); }
	 * 
	 * // 根据用户选择的支付类型，来进行条用不同的支付接口 WxPayResult wxPayResult = null; if
	 * (payType.intValue() == 20) { // 调用微信支付操作 try { //
	 * TODO根据支付方式调用不同的支付接口，获取prePayId返回给前台 String nonceStr =
	 * RandomStringUtils.randomAlphanumeric(32);
	 * 
	 * // ip = "192.168.1.1"; if (payClientType.intValue() == 1) { wxPayResult =
	 * WxPayUtil.wxPay(tOrder.getForderNum(), tOrder.getFeventTitle(),
	 * tOrder.getFtotal().multiply(new BigDecimal(100)).intValue(),
	 * customerDTO.getWxId(), now, DateUtils.addDays(now, 1), ip, nonceStr); }
	 * else { wxPayResult = WxPayUtil.wxAppPay(tOrder.getForderNum(),
	 * tOrder.getFeventTitle(), tOrder.getFtotal().multiply(new
	 * BigDecimal(100)).intValue(), now, DateUtils.addDays(now, 1), ip,
	 * nonceStr); } logger.info(wxPayResult.getResponse());
	 * 
	 * TWxPay tWxPay = wxPayDAO.getByOrderIdAndInOutAndStatus(tOrder.getId(), 1,
	 * 10); if (tWxPay == null) { tWxPay = new TWxPay();
	 * tWxPay.setFclientType(payClientType); tWxPay.setFinOut(1);
	 * tWxPay.setFcreateTime(now); tWxPay.setForderId(tOrder.getId());
	 * tWxPay.setForderType(1); tWxPay.setTCustomer(tOrder.getTCustomer());
	 * tWxPay.setFstatus(10); } tWxPay.setFupdateTime(now);
	 * tWxPay.setFppResponseInfo(wxPayResult.getResponse());
	 * wxPayDAO.save(tWxPay);
	 * 
	 * } catch (Exception e) { Map<String, String> map = new HashMap<String,
	 * String>(); map.put("orderId", tOrder.getId()); map.put("customerId",
	 * customerDTO.getCustomerId()); OutPutLogUtil.printLoggger(e, map, logger);
	 * responseDTO.setSuccess(false); responseDTO.setStatusCode(105);
	 * responseDTO.setMsg("调用微信支付接口时出错，请稍后再进行支付！"); return responseDTO; } }
	 * 
	 * PayDTO payDTO = new PayDTO(); payDTO.setAppId(wxPayResult.getAppId());
	 * payDTO.setPartnerId(wxPayResult.getPartnerId());
	 * payDTO.setPayType(payType); payDTO.setOrderId(tOrder.getId());
	 * payDTO.setOrderNum(tOrder.getForderNum());
	 * payDTO.setPrepayId(wxPayResult.getPrepayId());
	 * payDTO.setNonceStr(wxPayResult.getNonceStrVal());
	 * payDTO.setPaySign(wxPayResult.getPaySign());
	 * payDTO.setTimestamp(wxPayResult.getTimestamp());
	 * payDTO.setSignType(wxPayResult.getSignType());
	 * payDTO.setPayPackage(wxPayResult.getPayPackage());
	 * 
	 * returnData.put("zero", false); returnData.put("payInfo", payDTO); } //
	 * 添加线程任务修改客户附加信息表 OrderUpdateCustomerTagBean ouct = new
	 * OrderUpdateCustomerTagBean(); ouct.setCreateTime(now);
	 * ouct.setCustomerId(customer.getId()); ouct.setOrderId(tOrder.getId());
	 * ouct.setTotal(tOrder.getFtotal()); ouct.setTaskType(5);
	 * AsynchronousTasksManager.put(ouct);
	 * 
	 * responseDTO.setSuccess(true); responseDTO.setStatusCode(0);
	 * responseDTO.setData(returnData); return responseDTO; }
	 */

	/**
	 * 翻译接口
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public ResponseDTO getGamesType(String eventId) {
		ResponseDTO responseDTO = new ResponseDTO();
		Map<String, Object> saleTypeMap = new HashMap<String, Object>();

		if (StringUtils.isBlank(eventId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("eventId参数不能为空，请检查eventId的传递参数值！");
			return responseDTO;
		}

		TEvent tEvent = eventDAO.getOne(eventId);
		if (tEvent.getFsalesType().intValue() == 1) {
			TEventBargaining tEventBargaining = eventBargainingDAO.getByEventId(eventId);
			if (tEventBargaining != null && tEventBargaining.getFstatus().intValue() == 20) {
				saleTypeMap.put("saleType", "1");
				saleTypeMap.put("saleTitle", DictionaryUtil.getCode(DictionaryUtil.EventSalesType, 1));
				saleTypeMap.put("eventBargainingId", tEventBargaining.getId());
			} else {
				saleTypeMap.put("saleType", StringUtils.EMPTY);
			}
		} else {
			saleTypeMap.put("saleType", StringUtils.EMPTY);
		}

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("saleTypeMap", saleTypeMap);
		responseDTO.setData(returnData);
		return responseDTO;
	}

	/**
	 * 获取用户附加信息表场景码当前最大值
	 * 
	 * @param qrcodeUrl
	 * @param headUrl
	 * @return
	 */
	public String getMAXPointCode() {

		String pointCodeNum = customerBargainingDAO.getMaxPointCode();
		String codeNum;
		if (pointCodeNum != null) {
			codeNum = pointCodeNum;
			return codeNum;
		} else {
			codeNum = "50000000";
			return codeNum;

		}
	}

	public static void main(String[] args) {
		BigDecimal Z = BigDecimal.ZERO;// 砍掉价格
		BigDecimal A = new BigDecimal(46.9);// 原价
		BigDecimal B = new BigDecimal(20.9);// 能砍到最低价
		BigDecimal min = new BigDecimal(1);// 最少砍掉的价格
		BigDecimal max = new BigDecimal(6);// 最多砍掉的价格
		BigDecimal X = new BigDecimal(46.9);// 砍完之后剩余的价格
		int type = 1;
		if (type == 1) {
			for (int i = 0; i <= 100; i++) {
				// 如果砍完之后剩余的价格等于能砍到的最低低价
				if (X.compareTo(B) == 0) {
					Z = (new BigDecimal(RandomUtils.nextDouble((new BigDecimal(2).multiply(min)).doubleValue(),
							(new BigDecimal(8).multiply(min)).doubleValue()))).multiply(new BigDecimal(-1));
					
				} else {
					// 还可以砍的价格如果比最低可以砍的价低
					if ((X.subtract(B)).compareTo(min) <= 0) {
						Z = X.subtract(B);
						if(Z.compareTo(new BigDecimal(0))==0){
							Z = (new BigDecimal(RandomUtils.nextDouble((new BigDecimal(2).multiply(min)).doubleValue(),
									(new BigDecimal(8).multiply(min)).doubleValue()))).multiply(new BigDecimal(-1));
						}
					} else {
						BigDecimal randomNum = new BigDecimal(
								RandomUtils.nextDouble(min.doubleValue(), max.doubleValue()));
						// 还可以砍的价格除以最多可以砍的价格
						Z = (X.subtract(B)).divide(A.subtract(B), 2).multiply(randomNum);
						if (Z.compareTo(min) < 0) {
							Z = min.add(Z);
							if ((X.subtract(B)).compareTo(Z) < 0) {
								Z = X.subtract(B);
							}
						}
						if (Z.compareTo(max) > 0) {
							Z = max;
						}
						if (i > 10) {
							// 随机三分之一的负数
							if (RandomUtils.nextInt(1, 100) <= 20) {
								Z = Z.multiply(new BigDecimal(-1));
							}

						}
					}
				}
				Z = Z.setScale(2, RoundingMode.HALF_UP);
				X = X.subtract(Z);
				System.out.println(Z + "--------------------------" + X + "=--------------------" + i);
				if (X.compareTo(B) <= 0) {
					System.out.println("总共看次数" + i);
					break;
				}
			}
		} else {
			for (int i = 0; i <= 1000; i++) {
				// 还可以砍的价格如果比最低可以砍的价低
				if ((X.subtract(B)).compareTo(min) <= 0) {
					Z = X.subtract(B);
				} else {
					BigDecimal randomNum = new BigDecimal(
							RandomUtils.nextDouble(min.divide(new BigDecimal(3), 2, RoundingMode.HALF_UP).doubleValue(),
									max.divide(new BigDecimal(3), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(2))
											.doubleValue()));
					// 还可以砍的价格除以最多可以砍的价格
					Z = (X.subtract(B)).divide(A.subtract(B), 2).multiply(randomNum);
					if (Z.compareTo(min) < 0) {
						Z = min.add(Z);
						if ((X.subtract(B)).compareTo(Z) < 0) {
							Z = X.subtract(B);
						}
					}
					if (Z.compareTo(max) > 0) {
						Z = max;
					}
					Z = Z.multiply(new BigDecimal(3));

					if ((X.subtract(B).compareTo(Z) <= 0)) {
						Z = X.subtract(B);
					}
				}

				Z = Z.setScale(2, RoundingMode.HALF_UP);
				X = X.subtract(Z);
				System.out.println(Z + "--------------------------" + X + "=--------------------" + i);
				if (X.compareTo(B) <= 0) {
					System.out.println("总共看次数" + i);
					break;
				}
			}
		}
	}

	public ResponseDTO sendWxMsg(Integer remainCount, String id) {
		ResponseDTO responseDTO = new ResponseDTO();
		TEventBargaining tEventBargaining = eventBargainingDAO.findOne(id);
		BargainCountBean eso = new BargainCountBean();
		eso.setEventBargainId(tEventBargaining.getId());
		eso.setEventTitle(tEventBargaining.getFeventTitle());
		eso.setRemainingStock(remainCount);
		eso.setEventCode("无");
		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		return responseDTO;
	}

	/**
	 * 计算新用户每次帮砍掉多少金额
	 */
	public BigDecimal newCustomerCalculatePrice(TCustomerBargaining tCustomerBargaining, String fHelperId) {
		TEventBargaining tEventBargaining = eventBargainingDAO.findOne(tCustomerBargaining.getFbargainingId());

		BigDecimal Z = BigDecimal.ZERO;// 砍掉价格
		BigDecimal A = tEventBargaining.getFstartPrice();// 原价
		BigDecimal B = tCustomerBargaining.getFdefaultFloorPrice();// 能砍到最低价
		BigDecimal min = tEventBargaining.getFminBargaining();// 最少砍掉的价格
		BigDecimal max = tEventBargaining.getFmaxBargaining();// 最多砍掉的价格
		BigDecimal X = tCustomerBargaining.getFendPrice();// 砍完之后剩余的价格

		// 还可以砍的价格如果比最低可以砍的价低
		if ((X.subtract(B)).compareTo(min) <= 0) {
			Z = X.subtract(B);
		} else {
			BigDecimal randomNum = new BigDecimal(RandomUtils.nextDouble(
					min.divide(new BigDecimal(3), 2, RoundingMode.HALF_UP).doubleValue(),
					max.divide(new BigDecimal(3), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(2)).doubleValue()));
			// 还可以砍的价格除以最多可以砍的价格
			Z = (X.subtract(B)).divide(A.subtract(B), 2).multiply(randomNum);
			if (Z.compareTo(min) < 0) {
				Z = min.add(Z);
				if ((X.subtract(B)).compareTo(Z) < 0) {
					Z = X.subtract(B);
				}
			}
			if (Z.compareTo(max) > 0) {
				Z = max;
			}
			Z = Z.multiply(new BigDecimal(3));

			if ((X.subtract(B).compareTo(Z) <= 0)) {
				Z = X.subtract(B);
			}
		}

		Z = Z.multiply(new BigDecimal(-1)).setScale(2, RoundingMode.HALF_UP);
		tCustomerBargaining.setFendPrice(tCustomerBargaining.getFendPrice().add(Z));
		tCustomerBargaining.setFbargainingCount(tCustomerBargaining.getFbargainingCount() + 1);
		tCustomerBargaining.setFstatus(20);
		tCustomerBargaining = customerBargainingDAO.save(tCustomerBargaining);
		THelpBargainingDetail tHelpBargainingDetail = new THelpBargainingDetail();
		tHelpBargainingDetail.setFbargainingId(tCustomerBargaining.getFbargainingId());
		tHelpBargainingDetail.setFchangeAmount(Z);
		tHelpBargainingDetail.setFchangePrice(tCustomerBargaining.getFendPrice());
		tHelpBargainingDetail.setFcreateTime(new Date());
		tHelpBargainingDetail.setFcustomerBargainingId(tCustomerBargaining.getId());
		tHelpBargainingDetail.setFhelperId(fHelperId);
		tHelpBargainingDetail.setFkykType(1);
		helpBargainingDetailDAO.save(tHelpBargainingDetail);
		return Z;
	}
	
	
	/**
	 * 砍价英雄榜
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public ResponseDTO getbargaining(String customerId,Integer pageSize, Integer offset) {
		ResponseDTO responseDTO = new ResponseDTO();
		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}
		CommonPage page = new CommonPage();
		if (pageSize != null) {
			page.setPageSize(pageSize);
		}
		if (offset != null) {
			page.setOffset(offset);
		}
		StringBuilder hql = new StringBuilder();
		hql.append("select t.id as id,t.fbargainingId as fbargainingId,t.fstartPrice as fstartPrice,t.fendPrice as fendPrice ")
		    .append(",b.feventTitle as feventTitle,b.fpackageDesc as fpackageDesc,e.fimage1 as fimage1 ")
			.append("from TCustomerBargaining t inner join TEventBargaining b on t.fbargainingId = b.id ")
			.append(" inner join TEvent e on e.id = b.feventId where t.fcustomerId=:customerId and t.fstatus <30");

		Map<String, Object> hqlMap = new HashMap<String, Object>();
		hqlMap.put("customerId", customerId);
		hql.append(" order by t.fcreateTime desc");
		commonService.findPage(hql.toString(), page, hqlMap);
		List<Map<String, Object>> list = page.getResult();

		List<Map<String, Object>> bargainingList = Lists.newArrayList();
		for (Map<String, Object> amap : list) {
			Map<String, Object> bargainingMap = new HashMap<String, Object>();

			if (amap.get("id") != null && StringUtils.isNotBlank(amap.get("id").toString())) {
				bargainingMap.put("customerBargainingId", amap.get("id").toString());
			}
			if (amap.get("fbargainingId") != null && StringUtils.isNotBlank(amap.get("fbargainingId").toString())) {
				bargainingMap.put("goodsBargainingId", amap.get("fbargainingId").toString());
			}
			if (amap.get("fendPrice") != null && StringUtils.isNotBlank(amap.get("fendPrice").toString())) {
				bargainingMap.put("endPrice", amap.get("fendPrice").toString());
			}
			if (amap.get("fstartPrice") != null && StringUtils.isNotBlank(amap.get("fstartPrice").toString())) {
				bargainingMap.put("startPrice", "¥"+amap.get("fstartPrice").toString());
			}
			if (amap.get("fimage1") != null && StringUtils.isNotBlank(amap.get("fimage1").toString())) {
				bargainingMap.put("image", fxlService.getImageUrl(amap.get("fimage1").toString(), false));
			}
			if (amap.get("feventTitle") != null && StringUtils.isNotBlank(amap.get("feventTitle").toString())) {
				bargainingMap.put("title", amap.get("feventTitle").toString());
			}
			if (amap.get("fpackageDesc") != null && StringUtils.isNotBlank(amap.get("fpackageDesc").toString())) {
				bargainingMap.put("subTitle", amap.get("fpackageDesc").toString());
			}
			bargainingList.add(bargainingMap);
		}

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("bargainList", bargainingList);
		PageDTO pageDTO = new PageDTO(page.getTotalCount(), page.getPageSize(), page.getOffset());
		returnData.put("page", pageDTO);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("加载砍价英雄榜列表成功");
		return responseDTO;
	}

}