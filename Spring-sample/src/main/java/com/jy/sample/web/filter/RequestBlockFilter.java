package com.jy.sample.web.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.jy.sample.constant.SystemConstant;
import com.jy.sample.utils.SystemUtils;

/**
 * <pre>
 * 해당 필터는 지정된 블록 요청을 체크하여
 * 연속된 url에 대하여 블록처리함.
 * </pre>
 * 
 * @author wody
 */
public class RequestBlockFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final String CONFIG_LOCATION = "configLocation";
	
	private final String PROP_USE_BLOCK = "useBlock";
	private final String PROP_EXCLUDE_PATTERN = "excludePattern";
	private final String PROP_LIMIT_TIME = "limitTime";
	private final String PROP_BLOCK_TIME = "blockTime";
	private final String PROP_BLOCK_RESPONSE_CODE = "blockResponseCode";
	private final String PROP_BLOCK_RESPONSE_MESSAGE = "blockResponseMessage";
	
	private Properties config = new Properties(); // config object.
	
	// default values.
	private boolean useBlock = false;
	private List<String> excludeUrlList = null;
	private int limitTime = 100;
	private int blockTime = 5000;
	private int blockResponseCode = -1;
	private String blockResponseMessage = "request limit! wait 5 second!";
	
	
	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		//---------------------------------------------------------------------------------------------------------------
		// 1. 요청 경로 획득.
		//---------------------------------------------------------------------------------------------------------------
		String uri = req.getRequestURI();
		HttpSession session = req.getSession(true);
		String referer = (String) session.getAttribute("referer");
		
		//---------------------------------------------------------------------------------------------------------------
		// 2. 해당 요청이 블록할 요청이 아니라면 다음 필터로 넘어감.
		//---------------------------------------------------------------------------------------------------------------
		if (!this.useBlock || !this.isBlockUri(uri)) {
			filterChain.doFilter(request, response);
		} 
		//---------------------------------------------------------------------------------------------------------------
		// 3. 해당 요청 블록 처리.
		//---------------------------------------------------------------------------------------------------------------
		else {
			// 최종 접근 시간 및 현재 시간 정보 획득
			long currAccessTime = Calendar.getInstance().getTimeInMillis();
			long lastAccessTime = Long.valueOf(0);
			if (session.getAttribute(SystemConstant.SESSION.REQ_LAST_ACCESS_TIME) != null) {
				lastAccessTime = (Long) session.getAttribute(SystemConstant.SESSION.REQ_LAST_ACCESS_TIME);
			}
			
			// 기존 블록 여부 값 조회.
			boolean useBlock = false;
			if (session.getAttribute(SystemConstant.SESSION.REQ_USE_BLOCK) != null) {
				useBlock = SystemUtils.changeStrBoolean((String) session.getAttribute(SystemConstant.SESSION.REQ_USE_BLOCK));
			}
			
			// 기존에 블록되어있는 상태에서 블록 시간이 지났으면 블록 해제
			if (useBlock && (currAccessTime - lastAccessTime >= this.blockTime)) {
				useBlock = false;
			}
			
			// 마지막 접근 시간으로부터 제한시간 안쪽으로 요청이 들어오면 블록함.
			if (currAccessTime - lastAccessTime <= this.limitTime) {
				useBlock = true;
			}
			
			// 세션 저장.
			session.setAttribute(SystemConstant.SESSION.REQ_LAST_ACCESS_TIME, currAccessTime);
			session.setAttribute(SystemConstant.SESSION.REQ_USE_BLOCK, SystemUtils.changeStrBoolean(useBlock));
			session.setAttribute(SystemConstant.SESSION.REFERER, referer);
			
			// 기존 요청과 동일한 (반복되는) 요청인경우 에러처리함.
			if (useBlock && StringUtils.equals(referer, uri)) {
				logger.error("This request is blocked!");
				if (logger.isDebugEnabled()) {
					logger.debug("lastAccessTime : {}, currAccessTime : {}, useBlock : {}, request uri : {}, referer : {}", ImmutableList.of(lastAccessTime, currAccessTime, useBlock, uri, referer));
				}
				
				res.sendError(this.blockResponseCode, this.blockResponseMessage);
			} else {
				filterChain.doFilter(request, response);
			}
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String configLocation = filterConfig.getInitParameter(this.CONFIG_LOCATION);
		
		if (StringUtils.isNotEmpty(configLocation)) {
			
			try {
				//---------------------------------------------------------------------------------------------------------------
				// 1. 설정파일의 정보를 로드함.
				//---------------------------------------------------------------------------------------------------------------
				this.config.loadFromXML(new FileInputStream(filterConfig.getServletContext().getRealPath("/") + configLocation));
				
				//---------------------------------------------------------------------------------------------------------------
				// 2. 로드한 정보를 지역변수에 담음.
				//---------------------------------------------------------------------------------------------------------------
				String[] excludeUrlInfos = StringUtils.split(config.getProperty(this.PROP_EXCLUDE_PATTERN), ",");
				if (excludeUrlInfos != null) {
					this.excludeUrlList = new ArrayList<String>();
					for (String exclude : excludeUrlInfos) {
						this.excludeUrlList.add(StringUtils.trim(exclude));
					}
				}
				
				this.useBlock = SystemUtils.changeStrBoolean(this.config.getProperty(this.PROP_USE_BLOCK));
				this.limitTime = Integer.parseInt(this.config.getProperty(this.PROP_LIMIT_TIME));
				this.blockTime = Integer.parseInt(this.config.getProperty(this.PROP_BLOCK_TIME));
				this.blockResponseCode = Integer.parseInt(this.config.getProperty(this.PROP_BLOCK_RESPONSE_CODE));
				this.blockResponseMessage = this.config.getProperty(this.PROP_BLOCK_RESPONSE_MESSAGE);
				
			} catch (Exception e) {
				logger.error(SystemUtils.getStringStacktrace(e));
				
				//---------------------------------------------------------------------------------------------------------------
				// 3. 예외처리!. 기본값으로 config값 세팅함.
				//---------------------------------------------------------------------------------------------------------------
				this.config.setProperty(this.PROP_USE_BLOCK, SystemUtils.changeStrBoolean(this.useBlock));
				this.config.setProperty(this.PROP_EXCLUDE_PATTERN, "");
				this.config.setProperty(this.PROP_LIMIT_TIME, String.valueOf(this.limitTime));
				this.config.setProperty(this.PROP_BLOCK_TIME, String.valueOf(this.blockTime));
				this.config.setProperty(this.PROP_BLOCK_RESPONSE_CODE, String.valueOf(this.blockResponseCode));
				this.config.setProperty(this.PROP_BLOCK_RESPONSE_MESSAGE, this.blockResponseMessage);
			}
		}
	}
	
	
	/**
	 * 요청 경로가 block대상인지 판단.
	 * @param uri
	 * @return
	 */
	private boolean isBlockUri(String uri){
		if (CollectionUtils.isEmpty(this.excludeUrlList)) {
			return false;
		} else {
			for (String s : this.excludeUrlList) {
				if (StringUtils.startsWith(uri, s)) {
					return false;
				}
			}
			return true;
		}
	}
}
