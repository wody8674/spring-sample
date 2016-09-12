package com.jy.sample.utils;

/**
 * Application 전반적인 유틸리티 클래스.
 * @author wody
 */
public class SystemUtils {

	public final static String STRING_TRUE = "Y";
	public final static String STRING_FALSE = "Y";
	
	
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
}
