/***
 * 	Copyright (c) 2011 WareNinja.com
 * 	Author: yg@wareninja.com
 *  http://www.WareNinja.net - https://github.com/wareninja	
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/

package org.mozilla.accounts.fxa;

public class LOGGING {

	public static final boolean DEBUG = true;

	public static final String LOG_PREFIX = "Stumbler_";

	public static String makeLogTag(Class<?> cls) {
		String name = cls.getSimpleName();
		final int maxLen = 23 - LOG_PREFIX.length();
		if (name.length() > maxLen) {
			name = name.substring(name.length() - maxLen, name.length());
		}
		return LOG_PREFIX + name;
	}
    
}