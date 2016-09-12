package com.jy.sample.web.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jy.sample.utils.ExceptionUtils;
import com.jy.sample.utils.SystemUtils;

/**
 * 요청시 필터처리 샘플.
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
	private String[] excludeUrl = null;
	private int limitTime = 100;
	private int blockTime = 5000;
	private int blockResponseCode = -1;
	private String blockResponseMessage = "request limit! wait 5 second!";
	
	
	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String configLocation = filterConfig.getInitParameter(this.CONFIG_LOCATION);
		
		if (configLocation != null && configLocation.trim().length() > 0) {
			
			try {
				this.config.loadFromXML(new FileInputStream(filterConfig.getServletContext().getRealPath("/") + configLocation));
			} catch (Exception e) {
				logger.error(ExceptionUtils.getStringStacktrace(e));
				
				this.config.setProperty(this.PROP_USE_BLOCK, SystemUtils.changeStrBoolean(this.useBlock));
				this.config.setProperty(this.PROP_EXCLUDE_PATTERN, "");
				this.config.setProperty(this.PROP_LIMIT_TIME, String.valueOf(this.limitTime));
				this.config.setProperty(this.PROP_BLOCK_TIME, String.valueOf(this.blockTime));
				this.config.setProperty(this.PROP_BLOCK_RESPONSE_CODE, String.valueOf(this.blockResponseCode));
				this.config.setProperty(this.PROP_BLOCK_RESPONSE_MESSAGE, this.blockResponseMessage);
			}
		}
		
	}

}
