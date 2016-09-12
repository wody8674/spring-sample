package com.jy.sample.utils;

/**
 * 유틸리티 ExceptionUtils 
 * @author wody
 *
 */
public class ExceptionUtils {
	
	/** Exception stacktrace 최대 출력 라인  */
	public final static int STACKTRACE_MAX_LINE = 10;
	
	/** 개행 문자열 */
	public final static String STR_LINE_FEED = "\r\n";
	
	/**
	 * stacktrace 문자열을 지정된 라인수로 출력
	 * @param e Exception
	 * @return
	 */
	public static String getStringStacktrace(final Exception e) {
		return ExceptionUtils.getStringStacktrace(e, ExceptionUtils.STACKTRACE_MAX_LINE);
	}
	/**
	 * stacktrace 문자열을 지정된 라인수로 출력
	 * @param e Exception
	 * @param maxLine max line value.
	 * @return
	 */
	public static String getStringStacktrace(final Exception e, int maxLine) {
		if (e == null) {
			return "";
		}
		if (maxLine == 0) {
			maxLine = ExceptionUtils.STACKTRACE_MAX_LINE;
		}
		
		StringBuilder eBuilder = new StringBuilder();
		
		// 예외 메시지 추가.
		if (e.getMessage() != null && e.getMessage().trim().length() > 0) {
			eBuilder.append(e.getMessage());
		}
		eBuilder.append(ExceptionUtils.STR_LINE_FEED);
		
		// trace 메시지 추가.
		StackTraceElement[] stacks = e.getStackTrace();
		if (stacks != null && stacks.length > 0) {
			int stackSize = Math.min(maxLine, stacks.length); // 가장 적은 값으로 세팅함
			for (int i=0; i<stackSize; i++) {
				eBuilder.append(stacks[i].toString()).append(ExceptionUtils.STR_LINE_FEED);
			}
		}
		
		return eBuilder.toString();
	}
	
	
}
