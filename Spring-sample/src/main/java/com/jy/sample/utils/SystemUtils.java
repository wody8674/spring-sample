package com.jy.sample.utils;

/**
 * Application 전반적인 유틸리티 클래스.
 * @author wody
 */
public class SystemUtils {
	
	/** true == Y */
	public final static String STRING_TRUE = "Y";
	
	/** false == N */
	public final static String STRING_FALSE = "N";
	
	/** Exception stacktrace 최대 출력 라인  */
	private final static int STACKTRACE_MAX_LINE = 10;
	
	/** 개행 문자열 */
	private final static String STR_LINE_FEED = "\r\n";
	
	/**
	 * true -> "Y", false -> "N"
	 * @param val
	 * @return
	 */
	public final static String changeStrBoolean(final boolean val) {
		return val == true ? SystemUtils.STRING_TRUE : SystemUtils.STRING_FALSE;
	}
	
	/**
	 * "Y" -> true, "N" -> false
	 * @return
	 */
	public final static boolean changeStrBoolean(final String val) {
		if (SystemUtils.STRING_TRUE.equals(val)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * true -> 1, false -> 2
	 * @param val
	 * @return
	 */
	public final static int changeIntBoolean(final boolean val) {
		return val == true ? 1 : 0;
	}
	
	/**
	 * 1 -> true, 0 -> false
	 * @param val
	 * @return
	 */
	public final static boolean changeIntBoolean(final int val) {
		if (val == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * stacktrace 문자열을 지정된 라인수로 출력
	 * @param e Exception
	 * @return
	 */
	public static String getStringStacktrace(final Exception e) {
		return SystemUtils.getStringStacktrace(e, SystemUtils.STACKTRACE_MAX_LINE);
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
			maxLine = SystemUtils.STACKTRACE_MAX_LINE;
		}
		
		StringBuilder eBuilder = new StringBuilder();
		
		// 예외 메시지 추가.
		if (e.getMessage() != null && e.getMessage().trim().length() > 0) {
			eBuilder.append(e.getMessage());
		}
		eBuilder.append(SystemUtils.STR_LINE_FEED);
		
		// trace 메시지 추가.
		StackTraceElement[] stacks = e.getStackTrace();
		if (stacks != null && stacks.length > 0) {
			int stackSize = Math.min(maxLine, stacks.length); // 가장 적은 값으로 세팅함
			for (int i=0; i<stackSize; i++) {
				eBuilder.append(stacks[i].toString()).append(SystemUtils.STR_LINE_FEED);
			}
		}
		
		return eBuilder.toString();
	}
}
