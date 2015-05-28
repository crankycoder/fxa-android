package org.mozilla.accounts.fxa;

public class LoggerUtil {
	public static final String LOG_PREFIX = "FxA_";
	public static String makeLogTag(Class<?> cls) {
		String name = cls.getSimpleName();
		final int maxLen = 23 - LOG_PREFIX.length();
		if (name.length() > maxLen) {
			name = name.substring(name.length() - maxLen, name.length());
		}
		return LOG_PREFIX + name;
	}
}