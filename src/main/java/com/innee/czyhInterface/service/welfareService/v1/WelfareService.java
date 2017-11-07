package com.innee.czyhInterface.service.welfareService.v1;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.query.internal.QueryImpl;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.mapper.JsonMapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.innee.czyhInterface.dao.CustomerBonusDAO;
import com.innee.czyhInterface.dao.CustomerDAO;
import com.innee.czyhInterface.dao.CustomerInfoDAO;
import com.innee.czyhInterface.dao.CustomerLevelDAO;
import com.innee.czyhInterface.dao.EventBonusDAO;
import com.innee.czyhInterface.dao.OrderBonusDAO;
import com.innee.czyhInterface.dto.coupon.PageDTO;
import com.innee.czyhInterface.dto.coupon.ResponseDTO;
import com.innee.czyhInterface.dto.welfare.WelfareCustomerDTO;
import com.innee.czyhInterface.dto.welfare.WelfareDTO;
import com.innee.czyhInterface.dto.welfare.WelfareDetailDTO;
import com.innee.czyhInterface.dto.welfare.WelfareGoodsDTO;
import com.innee.czyhInterface.entity.TCustomer;
import com.innee.czyhInterface.entity.TCustomerBonus;
import com.innee.czyhInterface.entity.TCustomerInfo;
import com.innee.czyhInterface.entity.TCustomerLevel;
import com.innee.czyhInterface.entity.TEventBonus;
import com.innee.czyhInterface.entity.TOrderBonus;
import com.innee.czyhInterface.impl.welfareImpl.WelfaresService;
import com.innee.czyhInterface.service.CommonService;
import com.innee.czyhInterface.service.FxlService;
import com.innee.czyhInterface.service.couponService.v1.CouponServiceImpl;
import com.innee.czyhInterface.service.push.PushService;
import com.innee.czyhInterface.util.CommonPage;
import com.innee.czyhInterface.util.DictionaryUtil;
import com.innee.czyhInterface.util.NumberUtil;
import com.innee.czyhInterface.util.PropertiesUtil;

@Component
@Transactional
public class WelfareService implements WelfaresService{

	private static final Logger logger = LoggerFactory.getLogger(WelfareService.class);

	private static JsonMapper mapper = new JsonMapper(Include.ALWAYS);

	@Autowired
	private CustomerLevelDAO customerLevelDAO;

	@Autowired
	private CustomerInfoDAO customerInfoDAO;

	@Autowired
	private EventBonusDAO eventBonusDAO;

	@Autowired
	private CouponServiceImpl couponService;

	@Autowired
	private OrderBonusDAO orderBonusDAO;

	@Autowired
	private CustomerBonusDAO customerBonusDAO;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private FxlService fxlService;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private PushService pushService;

	@Transactional(readOnly = true)
	public ResponseDTO welfareGoodsList(String customerId) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}

		List<WelfareDTO> welfareDTODTOList = Lists.newArrayList();
		List<WelfareGoodsDTO> welfareGoodsDTOList = null;

		TCustomerInfo tCustomerInfo = customerInfoDAO.getByCustomerId(customerId);

		// 查询所有积分商品
		List<TEventBonus> tEventBonusList = eventBonusDAO.getByCustomerLevelList();
		// 查询所有会员积分等级
		List<TCustomerLevel> tCustomerLevelList = customerLevelDAO.getByCustomerLevelList();

		WelfareDTO welfareDTO = null;
		WelfareGoodsDTO welfareGoodsDTO = null;
		for (TCustomerLevel tCustomerLevel : tCustomerLevelList) {
			welfareDTO = new WelfareDTO();
			if (tCustomerInfo.getFgrowthValue().intValue() >= tCustomerLevel.getFgrowthValue().intValue()) {
				welfareDTO.setGrowthValue(tCustomerLevel.getFgrowthValue());
				welfareDTO.setLevel(tCustomerLevel.getFlevel());
				welfareDTO.setLevelType(true);
			} else {
				welfareDTO.setGrowthValue(tCustomerLevel.getFgrowthValue());
				welfareDTO.setLevel(tCustomerLevel.getFlevel());
				welfareDTO.setLevelType(false);
			}
			welfareGoodsDTOList = Lists.newArrayList();
			for (TEventBonus tEventBonus : tEventBonusList) {
				if (tEventBonus.getFlevel().intValue() == tCustomerLevel.getFlevel().intValue()) {
					welfareGoodsDTO = new WelfareGoodsDTO();
					welfareGoodsDTO.setGoodsId(tEventBonus.getId());
					welfareGoodsDTO.setGoodsbouns(tEventBonus.getFbonus());
					welfareGoodsDTO.setGoodsbounsLevel(tEventBonus.getFlevel());
					welfareGoodsDTO.setGoodsImage(fxlService.getImageUrl(tEventBonus.getTEvent().getFimage1(), false));
					welfareGoodsDTO.setGoodsTitle(tEventBonus.getTEvent().getFtitle());

					boolean isCovert = false;
					if (tEventBonus.getFlimitation() > 0) {
						List<TOrderBonus> orderBonuList = orderBonusDAO.findByCunstomerAndEvent(customerId,
								tEventBonus.getId());
						if (orderBonuList.size() >= tEventBonus.getFlimitation()) {
							isCovert = true;
						}
					}

					if (isCovert) {
						welfareGoodsDTO.setWelfareGoodstype(1);// 已兑换
					} else {
						if (tEventBonus.getFstock() != null) {
							if (tEventBonus.getFstock() <= 0) {
								welfareGoodsDTO.setWelfareGoodstype(2);// 已抢完
							} else {
								if (tCustomerInfo.getFpoint().intValue() < tEventBonus.getFbonus()) {
									welfareGoodsDTO.setWelfareGoodstype(3);// 积分不足
								} else {
									welfareGoodsDTO.setWelfareGoodstype(4);// 立即兑换
								}
							}
						} else {
							welfareGoodsDTO.setWelfareGoodstype(2);// 已抢完
						}
					}
					welfareGoodsDTOList.add(welfareGoodsDTO);
				}
			}
			welfareDTO.setWelfareGoodsDTOList(welfareGoodsDTOList);
			welfareDTODTOList.add(welfareDTO);
		}

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("welfareDTODTOList", welfareDTODTOList);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("福利社商品列表加载成功！");
		return responseDTO;
	}

	@Transactional(readOnly = true)
	public ResponseDTO welfareGoodsListhtml(String customerId) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}

		TCustomerInfo tCustomerInfo = customerInfoDAO.getByCustomerId(customerId);

		TCustomer tCustomer = customerDAO.findCustomerIdByName(customerId);

		// 查询所有积分商品
		List<TEventBonus> tEventBonusList = eventBonusDAO.getByCustomerLevelList();

		WelfareCustomerDTO welfareCustomerDTO = new WelfareCustomerDTO();
		welfareCustomerDTO.setName(tCustomer.getFname());
		welfareCustomerDTO.setImageUrl(tCustomer.getFphoto());
		welfareCustomerDTO.setLevel(tCustomerInfo.getFlevel());
		welfareCustomerDTO.setBouns(tCustomerInfo.getFpoint());

		WelfareGoodsDTO welfareGoodsDTO = null;

		StringBuilder hql = new StringBuilder();
		hql.append(
				"select t.id as id,t.TEvent.ftitle as ftitle,t.TEvent.fimage1 as fimage from TEventBonus t where t.fstatus =20 order by t.fbonus desc ");
		Query q = commonService.createQuery(hql.toString());
		q.unwrap(QueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if (tEventBonusList.size() > 8) {
			q.setFirstResult(0).setMaxResults(8);
		} else {
			q.setFirstResult(0).setMaxResults(tEventBonusList.size());
		}

		List<Map<String, Object>> list = q.getResultList();
		List<WelfareGoodsDTO> welfareGoodsDTOList = Lists.newArrayList();
		for (Map<String, Object> amap : list) {

			welfareGoodsDTO = new WelfareGoodsDTO();
			if (amap.get("id") != null && StringUtils.isNotBlank(amap.get("id").toString())) {
				welfareGoodsDTO.setGoodsId(amap.get("id").toString());
			}
			if (amap.get("ftitle") != null && StringUtils.isNotBlank(amap.get("ftitle").toString())) {
				welfareGoodsDTO.setGoodsTitle(amap.get("ftitle").toString());
			}
			if (amap.get("fimage") != null && StringUtils.isNotBlank(amap.get("fimage").toString())) {
				welfareGoodsDTO.setGoodsImage(fxlService.getImageUrl(amap.get("fimage").toString(), false));
			}
			welfareGoodsDTOList.add(welfareGoodsDTO);
		}

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("welfareGoodsDTOList", welfareGoodsDTOList);
		returnData.put("WelfareCustomerDTO", welfareCustomerDTO);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("h5福利社商品列表加载成功！");
		return responseDTO;
	}

	/**
	 * 兑换商城商品
	 * 
	 * @return
	 */
	public ResponseDTO convertOrder(String customerId, String bonusGoodsId, String name, String phone, String address,
			String ip) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}
		if (StringUtils.isBlank(bonusGoodsId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(202);
			responseDTO.setMsg("bonusGoodsId参数不能为空，请检查bonusGoodsId的传递参数值！");
			return responseDTO;
		}

		// 校验是否限购
		TEventBonus teventBonus = eventBonusDAO.findOne(bonusGoodsId);
		if (teventBonus.getFlimitation().intValue() > 0) {
			List<TOrderBonus> orderBonuList = orderBonusDAO.findByCunstomerAndEvent(customerId, teventBonus.getId());
			if (orderBonuList.size() >= teventBonus.getFlimitation().intValue()) {
				responseDTO.setSuccess(false);
				responseDTO.setStatusCode(105);
				responseDTO.setMsg("你已兑换过该商品");
				return responseDTO;
			}
		}
		// 校验库存
		if (teventBonus.getFstock().intValue() <= 0) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(103);
			responseDTO.setMsg("该商品已兑换完");
			return responseDTO;
		}
		// 校验积分是否满足兑换
		TCustomerInfo customerInfo = customerInfoDAO.getByCustomerId(customerId);
		Integer count = 0;
		if (customerInfo != null) {
			count = customerInfo.getFpoint();
		}
		if (count.intValue() < teventBonus.getFbonus().intValue()) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(104);
			responseDTO.setMsg("你的积分不足！");
			return responseDTO;
		}
		Date now = new Date();

		Map<String, Object> returnData = Maps.newHashMap();
		// 保存兑换订单
		if (teventBonus.getFdeliveryId() != null) {
			ResponseDTO DTO = couponService.receiveCouponByBonus(teventBonus.getFdeliveryId(), customerId);
			if (!DTO.isSuccess()) {
				responseDTO.setSuccess(false);
				responseDTO.setStatusCode(105);
				responseDTO.setMsg("该优惠券已被领完！");
				return responseDTO;
			}
		}
		TOrderBonus tOrderBonus = new TOrderBonus();
		tOrderBonus.setFcreateTime(now);
		tOrderBonus.setFcustomerId(customerId);
		tOrderBonus.setFcustomerName(name);
		tOrderBonus.setFcustomerPhone(phone);
		tOrderBonus.setFexpress(address);
		tOrderBonus.setFremark("");
		tOrderBonus.setTEventBonus(teventBonus);
		tOrderBonus.setForderNum(NumberUtil.getOtherOrderNum(1, "JF"));
		tOrderBonus.setFstatus(10);
		this.subtractStock(teventBonus, now, tOrderBonus);
		
		if(customerInfo!=null && customerInfo.getFregisterDeviceTokens()!=null){
			pushService.bonusConsumption(teventBonus.getFbonus().toString(), teventBonus.getTEvent().getFtitle(),
					customerInfo.getFregisterDeviceTokens(),customerId, name);
		}
		
		tOrderBonus = orderBonusDAO.save(tOrderBonus);
		returnData.put("orderWelfareId", tOrderBonus.getId());
		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setData(returnData);
		responseDTO.setMsg("兑换商品成功");
		return responseDTO;
	}

	@Transactional(readOnly = true)
	public ResponseDTO welfareOrderGoodsList(String customerId, Integer pageSize, Integer offset) {
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
		hql.append(
				"select t.id as orderBounsId,t.TEventBonus.TEvent.id as eventId,t.TEventBonus.TEvent.ftitle as ftitle, ")
				.append(" t.TEventBonus.TEvent.fimage1 as fimage1,t.TEventBonus.fprice as fprice,t.TEventBonus.fbonus as fbonus,")
				.append(" t.fexpress as faddress,t.fcreateTime as fcreateTime,t.fcustomerName as fcustomerName,t.fcustomerPhone as fcustomerPhone,")
				.append(" t.fremark as fremark,t.freply as fnote,t.fstatus as fstatus,t.TEventBonus.fdeal as fbonusPrice from TOrderBonus t where t.fstatus < 999");
		Map<String, Object> hqlMap = new HashMap<String, Object>();
		hql.append(" and t.fcustomerId = :fcustomerId");
		hqlMap.put("fcustomerId", customerId);
		hql.append(" order by t.fcreateTime desc ");
		commonService.findPage(hql.toString(), page, hqlMap);
		List<Map<String, Object>> list = page.getResult();

		List<Map<String, Object>> bonusOrderList = Lists.newArrayList();

		for (Map<String, Object> amap : list) {
			Map<String, Object> bonusOrderMap = new HashMap<String, Object>();
			if (amap.get("orderBounsId") != null && StringUtils.isNotBlank(amap.get("orderBounsId").toString())) {
				bonusOrderMap.put("orderWelfareId", amap.get("orderBounsId"));
			}
			if (amap.get("eventId") != null && StringUtils.isNotBlank(amap.get("eventId").toString())) {
				bonusOrderMap.put("goodsId", amap.get("eventId"));
			}
			if (amap.get("ftitle") != null && StringUtils.isNotBlank(amap.get("ftitle").toString())) {
				bonusOrderMap.put("title", amap.get("ftitle"));
			}
			if (amap.get("fbonus") != null && StringUtils.isNotBlank(amap.get("fbonus").toString())) {
				bonusOrderMap.put("bonus", amap.get("fbonus"));
			}
			// if (amap.get("fbonusPrice") != null &&
			// StringUtils.isNotBlank(amap.get("fbonusPrice").toString())) {
			// bonusOrderMap.put("bonusPrice", amap.get("fbonusPrice"));
			// } else {
			// bonusOrderMap.put("bonusPrice", StringUtils.EMPTY);
			// }
			// if (amap.get("fprice") != null &&
			// StringUtils.isNotBlank(amap.get("fprice").toString())) {
			// bonusOrderMap.put("price", amap.get("fprice"));
			// }
			if (amap.get("fimage1") != null && StringUtils.isNotBlank(amap.get("fimage1").toString())) {
				bonusOrderMap.put("imageUrl", fxlService.getImageUrl(amap.get("fimage1").toString(), false));
			}
			// if (amap.get("faddress") != null &&
			// StringUtils.isNotBlank(amap.get("faddress").toString())) {
			// bonusOrderMap.put("address", amap.get("faddress"));
			// }
			if (amap.get("fcreateTime") != null && StringUtils.isNotBlank(amap.get("fcreateTime").toString())) {
				bonusOrderMap.put("createTime", DateFormatUtils.format((Date) amap.get("fcreateTime"), "yyyy-MM-dd"));
			}
			// if (amap.get("fcustomerName") != null &&
			// StringUtils.isNotBlank(amap.get("fcustomerName").toString())) {
			// bonusOrderMap.put("customerName", amap.get("fcustomerName"));
			// } else {
			// bonusOrderMap.put("customerName", StringUtils.EMPTY);
			// }
			// if (amap.get("fcustomerPhone") != null &&
			// StringUtils.isNotBlank(amap.get("fcustomerPhone").toString())) {
			// bonusOrderMap.put("customerPhone", amap.get("fcustomerPhone"));
			// } else {
			// bonusOrderMap.put("customerPhone", StringUtils.EMPTY);
			// }
			// if (amap.get("fremark") != null &&
			// StringUtils.isNotBlank(amap.get("fremark").toString())) {
			// bonusOrderMap.put("remark", amap.get("fremark"));
			// } else {
			// bonusOrderMap.put("remark", StringUtils.EMPTY);
			// }
			// if (amap.get("fnote") != null &&
			// StringUtils.isNotBlank(amap.get("fnote").toString())) {
			// bonusOrderMap.put("note", amap.get("fnote"));
			// } else {
			// bonusOrderMap.put("note", StringUtils.EMPTY);
			// }
			if (amap.get("fstatus") != null && StringUtils.isNotBlank(amap.get("fstatus").toString())) {
				bonusOrderMap.put("status", DictionaryUtil.getString(DictionaryUtil.OrderBonusType,
						Integer.parseInt(amap.get("fstatus").toString())));
			}
			bonusOrderMap.put("count", 1);
			bonusOrderList.add(bonusOrderMap);
		}

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("welfareOrderGoodsList", bonusOrderList);
		PageDTO pageDTO = new PageDTO(page.getTotalCount(), page.getPageSize(), page.getOffset());
		returnData.put("page", pageDTO);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("积分兑换列表成功！");
		return responseDTO;
	}

	public void subtractStock(TEventBonus teventBonus, Date now, TOrderBonus tOrderBonus) {
		// 减去对应积分商城库存
		teventBonus.setFstock(teventBonus.getFstock() - 1);
		eventBonusDAO.save(teventBonus);
		// 添加兑换订单消耗积分记录
		TCustomerBonus tCustomerBonus = new TCustomerBonus();
		tCustomerBonus.setFcreateTime(now);
		tCustomerBonus.setFbonus(-teventBonus.getFbonus());
		tCustomerBonus.setFcustermerId(tOrderBonus.getFcustomerId());
		tCustomerBonus.setFobject(tOrderBonus.getId());
		tCustomerBonus.setFtype(4);
		customerBonusDAO.save(tCustomerBonus);
		// 更改用户总积分
		customerInfoDAO.updatePointAndUsePoint(tOrderBonus.getFcustomerId(), -teventBonus.getFbonus(),
				teventBonus.getFbonus());
	}

	@Transactional(readOnly = true)
	public ResponseDTO welfareBonusDeail(String customerId, Integer pageSize, Integer offset) {

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
		hql.append(
				"select t.id as id, t.fbonus as fbonus,t.fcreateTime as fcreateTime,t.ftype as ftype from TCustomerBonus t ")
				.append("where t.fcustermerId = :customerId order by fcreateTime desc");
		Map<String, Object> hqlMap = new HashMap<String, Object>();
		hqlMap.put("customerId", customerId);
		commonService.findPage(hql.toString(), page, hqlMap);
		List<Map<String, Object>> list = page.getResult();
		List<Map<String, Object>> bonusList = Lists.newArrayList();
		int i = 1;
		for (Map<String, Object> aMap : list) {
			Map<String, Object> bonusMap = new HashMap<String, Object>();
			bonusMap.put("welfareBonusId", aMap.get("id"));
			bonusMap.put("bonus", aMap.get("fbonus"));
			// if (aMap.get("username") != null &&
			// StringUtils.isNotBlank(aMap.get("username").toString())) {
			// bonusMap.put("userName", aMap.get("username").toString());
			// } else {
			// bonusMap.put("userName", "系统发放");
			// }
			if (((Integer) aMap.get("fbonus")).intValue() > 0) {
				bonusMap.put("plus", true);
			} else {
				bonusMap.put("plus", false);
			}
			// if (aMap.get("photo") != null &&
			// StringUtils.isNotBlank(aMap.get("photo").toString())) {
			// bonusMap.put("photo",
			// HeadImageUtil.getHeadImage(aMap.get("photo").toString(), 46));
			// } else {
			// bonusMap.put("photo", Constant.defaultHeadImgUrl);
			// }
			bonusMap.put("type",
					DictionaryUtil.getString(DictionaryUtil.BonusType, Integer.parseInt(aMap.get("ftype").toString())));
			bonusMap.put("createTime", DateFormatUtils.format((Date) aMap.get("fcreateTime"), "yyyy-MM-dd"));
			bonusMap.put("number", page.getOffset() + (i++));
			bonusList.add(bonusMap);
		}

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("bonusList", bonusList);
		PageDTO pageDTO = new PageDTO(page.getTotalCount(), page.getPageSize(), page.getOffset());
		returnData.put("page", pageDTO);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("我的积分加载成功");
		return responseDTO;
	}

	@Transactional(readOnly = true)
	public ResponseDTO welfareGoodsDetail(String customerId, String goodsId) {

		ResponseDTO responseDTO = new ResponseDTO();
		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}

		if (StringUtils.isBlank(goodsId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("goodsId参数不能为空，请检查goodsId的传递参数值！");
			return responseDTO;
		}

		TCustomerInfo tCustomerInfo = customerInfoDAO.getByCustomerId(customerId);
		TEventBonus eventBonus = eventBonusDAO.findOne(goodsId);

		WelfareDetailDTO welfareDetailDTO = new WelfareDetailDTO();
		welfareDetailDTO.setGoodsId(eventBonus.getId());
		welfareDetailDTO.setGoodsTitle(eventBonus.getTEvent().getFtitle());
		welfareDetailDTO.setGoodsbouns(eventBonus.getFbonus());
		welfareDetailDTO.setGoodsbounsLevel(eventBonus.getFlevel());
		welfareDetailDTO.setDetailHtmlUrl(new StringBuilder(PropertiesUtil.getProperty("fileServerUrl"))
				.append(PropertiesUtil.getProperty("htmlRootPath")).append(eventBonus.getTEvent().getFdetailHtmlUrl())
				.toString());
		welfareDetailDTO.setImageUrls(fxlService.getImageUrls(eventBonus.getTEvent().getFimage2(), false));
		welfareDetailDTO.setSpec(eventBonus.getTEvent().getFspec());

		boolean isCovert = false;
		if (eventBonus.getFlimitation() > 0) {
			List<TOrderBonus> orderBonuList = orderBonusDAO.findByCunstomerAndEvent(customerId, eventBonus.getId());
			if (orderBonuList.size() >= eventBonus.getFlimitation()) {
				isCovert = true;
			}
		}

		if (isCovert) {
			welfareDetailDTO.setCovert(false);// 已兑换
		} else {
			if (eventBonus.getFstock() != null) {
				if (eventBonus.getFstock() <= 0) {
					welfareDetailDTO.setCovert(false);// 已抢完
				} else {
					if (tCustomerInfo.getFpoint().intValue() < eventBonus.getFbonus()) {
						welfareDetailDTO.setCovert(false);
						;// 积分不足
					} else {
						welfareDetailDTO.setCovert(true);// 立即兑换
					}
				}
			} else {
				welfareDetailDTO.setCovert(false);// 已抢完
			}
		}

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("welfareDetailDTO", welfareDetailDTO);
		responseDTO.setData(returnData);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("福利社商品详情加载成功！");
		return responseDTO;
	}

}