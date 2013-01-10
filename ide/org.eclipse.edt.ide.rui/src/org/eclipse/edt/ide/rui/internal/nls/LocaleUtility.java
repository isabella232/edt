/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.nls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LocaleUtility {
	private static String[] descriptionArray = null;
	
	public static HashMap DEFAULT_HANDLER_LOCALE_CODES = new HashMap();
	static {
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_English, ILocaleConstants.Locale_Key_English);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Arabic_Egypt, ILocaleConstants.Locale_Key_Arabic_Egypt);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Arabic_Saudi_Arabia, ILocaleConstants.Locale_Key_Arabic_Saudi_Arabia);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Brazilian, ILocaleConstants.Locale_Key_Brazilian);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Chinese_Simplified, ILocaleConstants.Locale_Key_Chinese_Simplified);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Chinese_Taiwan, ILocaleConstants.Locale_Key_Chinese_Taiwan);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Chinese_Hong_Kong, ILocaleConstants.Locale_Key_Chinese_Hong_Kong);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Czech, ILocaleConstants.Locale_Key_Czech);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_French, ILocaleConstants.Locale_Key_French);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_German, ILocaleConstants.Locale_Key_German);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Hungarian, ILocaleConstants.Locale_Key_Hungarian);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Italian, ILocaleConstants.Locale_Key_Italian);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Japanese, ILocaleConstants.Locale_Key_Japanese);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Korean, ILocaleConstants.Locale_Key_Korean);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Polish, ILocaleConstants.Locale_Key_Polish);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Russian, ILocaleConstants.Locale_Key_Russian);
		DEFAULT_HANDLER_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Spanish, ILocaleConstants.Locale_Key_Spanish);
	};
	public static HashMap DEFAULT_RUNTIME_LOCALE_CODES = new HashMap();
	static {
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_English, ILocaleConstants.Locale_Key_English_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Arabic, ILocaleConstants.Locale_Key_Arabic_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Brazilian, ILocaleConstants.Locale_Key_Brazilian_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Chinese_Simplified, ILocaleConstants.Locale_Key_Chinese_Simplified_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Chinese_Taiwan, ILocaleConstants.Locale_Key_Chinese_Taiwan_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Chinese_Hong_Kong, ILocaleConstants.Locale_Key_Chinese_Hong_Kong_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Czech, ILocaleConstants.Locale_Key_Czech_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_French, ILocaleConstants.Locale_Key_French_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_German, ILocaleConstants.Locale_Key_German_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Hungarian, ILocaleConstants.Locale_Key_Hungarian_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Italian, ILocaleConstants.Locale_Key_Italian_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Japanese, ILocaleConstants.Locale_Key_Japanese_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Korean, ILocaleConstants.Locale_Key_Korean_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Polish, ILocaleConstants.Locale_Key_Polish_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Russian, ILocaleConstants.Locale_Key_Russian_Runtime);
		DEFAULT_RUNTIME_LOCALE_CODES.put(ILocaleConstants.Locale_Description_Spanish, ILocaleConstants.Locale_Key_Spanish_Runtime);
	};
	public static HashMap DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES = new HashMap();
	static {
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_English, ILocaleConstants.Locale_Key_English_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Arabic_Egypt, ILocaleConstants.Locale_Key_Arabic_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Arabic_Saudi_Arabia, ILocaleConstants.Locale_Key_Arabic_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Brazilian, ILocaleConstants.Locale_Key_Brazilian_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Chinese_Simplified, ILocaleConstants.Locale_Key_Chinese_Simplified_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Chinese_Taiwan, ILocaleConstants.Locale_Key_Chinese_Taiwan_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Chinese_Hong_Kong, ILocaleConstants.Locale_Key_Chinese_Hong_Kong_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Czech, ILocaleConstants.Locale_Key_Czech_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_French, ILocaleConstants.Locale_Key_French_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_German, ILocaleConstants.Locale_Key_German_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Hungarian, ILocaleConstants.Locale_Key_Hungarian_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Italian, ILocaleConstants.Locale_Key_Italian_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Japanese, ILocaleConstants.Locale_Key_Japanese_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Korean, ILocaleConstants.Locale_Key_Korean_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Polish, ILocaleConstants.Locale_Key_Polish_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Russian, ILocaleConstants.Locale_Key_Russian_Runtime);
		DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.put(ILocaleConstants.Locale_Key_Spanish, ILocaleConstants.Locale_Key_Spanish_Runtime);
	};
	
	public static String getHandlerDescriptionForCode(String localeCode) {
		String result = ""; //$NON-NLS-1$
		for (Iterator iterator = DEFAULT_HANDLER_LOCALE_CODES.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if (entry.getValue().equals(localeCode)) {
				result = (String)entry.getKey();
			}
		}
		return result;
	}
	
	public static String getRuntimeDescriptionForCode(String localeCode) {
		String result = ""; //$NON-NLS-1$
		for (Iterator iterator = DEFAULT_RUNTIME_LOCALE_CODES.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if (entry.getValue().equals(localeCode)) {
				result = (String)entry.getKey();
			}
		}
		return result;
	}
	
	public static String getRuntimeCodeForDescription(String description) {
		return (String)DEFAULT_RUNTIME_LOCALE_CODES.get(description);
	}
	
	public static String getDefaultRuntimeCodeForHandlerCode(String handlerCode) {
		return (String)DEFAULT_HANDLER_CODES_TO_RUNTIME_CODES.get(handlerCode);
	}
		
	public static String[] getRuntimeDescriptionsArray() {
		if (descriptionArray == null) {
			List descriptions = new ArrayList();
			for ( Iterator iterator = DEFAULT_RUNTIME_LOCALE_CODES.entrySet().iterator(); iterator.hasNext(); ) {
				Map.Entry mapEntry = ( Map.Entry ) iterator.next();
				String description = ( String )mapEntry.getKey();
				descriptions.add( description );
			}
			descriptionArray = ( String[] )descriptions.toArray( new String[descriptions.size()] );
	        Arrays.sort(descriptionArray); 
		}
		return descriptionArray;
	}
	
	/**
	 * Returns the locale code that the workbench is running with
	 * 
	 * @return Workbench locale code
	 */
	public static final String getWorkbenchLocale() {
		return System.getProperty("osgi.nl");
	}
	
	/**
	 * Returns the default handler locale that should be used. 
	 * 
	 * @return
	 */
	public static final Locale getDefaultHandlerLocale() {
		return getDefaultLocale(LocalesList.getLocalesList().getLocales());
	}
	
	/**
	 * Returns the default runtime locale that should be used
	 * 
	 * @return
	 */
	public static final Locale getDefaultRuntimeLocale() {
		/**
		 * got to build the default list as the one that the LocalesList has may contain user defined locales
		 * and runtime can not use those
		 */
		List localesList = new ArrayList();
		for (Iterator iterator = LocaleUtility.DEFAULT_RUNTIME_LOCALE_CODES.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			Locale locale = new Locale((String)mapEntry.getValue(), (String)mapEntry.getKey(), (String)mapEntry.getValue());
			localesList.add(locale);
		}
		return getDefaultLocale(localesList);
	}
	
	/**
	 * Returns the best default locale from the search list of locales passed. The locale that the workbench has been
	 * set to is the target to which we make the best match we can
	 * 
	 * @param searchList
	 * @return
	 */
	private static final Locale getDefaultLocale(List searchList) {
		Locale bestMatch = null;
		Locale exactMatch = null;
		Locale englishLocale = null;
		String workbench_locale = getWorkbenchLocale();
		/**
		 * be defensive. The locale code ought to be at least 2 chars long. If it is then use the 
		 * first 2 characters... there ought to be at least two characters. Otherwise grab the single character
		 */
		String best_match_search = workbench_locale;
		if (workbench_locale.length() > 1) {
			best_match_search = workbench_locale.substring(0, 2);
		}
		for (Iterator iterator = searchList.iterator(); iterator.hasNext() && exactMatch == null;) {
			Locale locale = (Locale) iterator.next();
			if (locale.getCode().equals(ILocaleConstants.Locale_Key_English)) {
				englishLocale = locale;
			}
			if (locale.getCode().equals(workbench_locale)) {
				exactMatch = locale;
			} else {
				/**
				 * RATLC01379114 
				 * Looks like we have to have an special case for the Chinese language. Normally a locale code is
				 * ll_cc where ll = language code and cc = country code. The short locale code is just ll. Normally when
				 * we just have the short code we will look for an exact match and if we don't find one then just use
				 * an arbitrary best match on the language and any old country code. However, now we have to specially
				 * cater for the case where 'zh' has been specified and an exact match could either be 'zh' or it can now
				 * also be 'zh_CN'
				 * 
				 * The workbench code could be "zh" or "zh_CN". The runtime locale is "zh" and the default handler locale
				 * is "zh_CN". We have to make sure that "zh" will be an exact match against "zh_CN" and that "zh_CN" will
				 * be an exact match against "zh".
				 */
				if (workbench_locale.equals(ILocaleConstants.Locale_Key_Chinese_Simplified_Runtime) && 
						locale.getCode().equals(ILocaleConstants.Locale_Key_Chinese_Simplified) ||
					workbench_locale.equals(ILocaleConstants.Locale_Key_Chinese_Simplified) &&
						locale.getCode().equals(ILocaleConstants.Locale_Key_Chinese_Simplified_Runtime)) {
					exactMatch = locale;
				} else {
					/**
					 * The first two characters are the language code. If the code is greater than 2 characters then 
					 * the next character should be an underscore followed by a two character country code. Our best
					 * match is going to be matching the language code and ignoring the country code
					 */
					if (locale.getCode().startsWith(best_match_search)) {
						bestMatch = locale;
					}
				}
			}
		}
		if (exactMatch != null) {
			/**
			 * got an exact match...yipee
			 */
			return exactMatch;
		} else {
			if (bestMatch != null) {
				/**
				 * got a partial match
				 */
				return bestMatch;
			} else {
				if (englishLocale != null) {
					/**
					 * no matches so if we have found the English locale default to it
					 */
					return englishLocale;
				} else {
					if(searchList.size() > 0) {
						/**
						 * no match, no English locale so if we have ANY locales pass the first one back... getting desperate.
						 */
						return (Locale)searchList.get(0);
					} else {
						/**
						 * wow, we are screwed..... so fudge up a Locale object and pass that back
						 */
						return new Locale(ILocaleConstants.Locale_Key_English, ILocaleConstants.Locale_Description_English, ILocaleConstants.Locale_Key_English);
					}
				}
			}
		}
	}
}
