package com.innee.czyhInterface.util;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.innee.czyhInterface.service.ConfigurationService;
import com.innee.czyhInterface.service.FxlService;
import com.innee.czyhInterface.util.asynchronoustasks.AsynchronousTasksManager;
import com.innee.czyhInterface.util.sms.SmsUtil;

/**
 * 一个初始化的servlet类，在应用启动的时候，做一些初始化的工作。
 */
public class InitServlet extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(InitServlet.class);

	private static final long serialVersionUID = 1L;

	private AsynchronousTasksManager asynchronousTasksManager = null;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		Constant.init();
		logger.warn("上传文件路径设置完成！");
		logger.warn("短信网关初始化完成！");
		logger.warn("IM网关初始化完成！");
		logger.warn("ping++支付参数始化完成！");
		FxlService fxlService = SpringContextHolder.getBean(FxlService.class);
		fxlService.initDictionary();
		//fxlService.initEventCategory();
		logger.warn("数据字典缓存初始化完成！");
		// eventService.initCalendarDay();
		// logger.warn("活动日历表初始化完成！");
		// eventService.initEventStockCache();
		// logger.warn("活动库存缓存初始化完成！");
		asynchronousTasksManager = new AsynchronousTasksManager();
		asynchronousTasksManager.start();
		logger.warn("用户异步日志任务线程池启动成功！");
		SmsUtil.init();
		HttpClientUtil.init();
		logger.warn("Http客户端类初始化完成！");
		// RankingListUtil.init();
		// logger.warn("嘉年华工具类初始化完成！");
		ConfigurationService configurationService = SpringContextHolder.getBean(ConfigurationService.class);
		configurationService.initConfigurationMap();
		logger.warn("配置表缓存完成！");
		PromptInfoUtil.init();
		logger.warn("提示信息初始化完成！");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	@Override
	public void destroy() {
		// CarnivalService carnivalService =
		// SpringContextHolder.getBean(CarnivalService.class);
		// carnivalService.savingCarnivalUserCache();
		// logger.warn("同步了嘉年华用户积分缓存到数据库！");
		DictionaryUtil.clear();
		asynchronousTasksManager.interrupt();
		HttpClientUtil.destroy();
		logger.warn("Http客户端类卸载完成！");
		super.destroy();
	}
}