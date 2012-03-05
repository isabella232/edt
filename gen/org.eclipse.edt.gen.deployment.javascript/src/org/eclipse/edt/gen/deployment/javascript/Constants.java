/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.deployment.javascript;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	// EGL message id's
	public static final String EGLMESSAGE_GENERATION_FAILED = "9990"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GENERATION_FAILED_HEADERMSG = "9991"; //$NON-NLS-1$
	public static final String EGLMESSAGE_COMPILE_FAILED_HEADERMSG = "9992"; //$NON-NLS-1$
	public static final String PROPERTIES_FOLDER_NAME = "properties"; //$NON-NLS-1$
	public static final String RUNTIME_FOLDER_NAME = "runtime"; //$NON-NLS-1$
	public static final String RUNTIME_MESSAGES_DEPLOYMENT_FOLDER_NAME = "egl/messages"; //$NON-NLS-1$

	public static final String USES_SERVICELIB_BINDSERVICE_FUNCTION = "Uses ServiceLib.bindService function";
	
	/**
	 * The following files are found in the org.eclipse.edt.runtime.javascript\runtime directory.
	 * The files found in this list are automatically linked to an HTML file.
	 * Do NOT add a file to this list if you do not want its contents to be added to EVERY HTML file.
	 * Also note that files are linked in the order they appear here.  Be aware of dependencies!
	 */
	public static final String RUI_MESSAGE_FILE = "RuiMessages"; //$NON-NLS-1$
	public static final String RUI_RUNTIME_BOOTSTRAP_FILE = "edt_core.js"; //$NON-NLS-1$
	public static final String RUI_RUNTIME_LOADER_FILE = "dojo.js"; //$NON-NLS-1$
//	public static final String RUI_RUNTIME_LOADER_FILE = "dojo.js.uncompressed.js"; //$NON-NLS-1$
	public static final String RUI_RUNTIME_JAVASCRIPT_ALL_IN_ONE_FILE = "edt_runtime_all.js"; //$NON-NLS-1$
	public static final List<String> RUI_RUNTIME_JAVASCRIPT_FILES = new ArrayList<String>(); //$NON-NLS-1$
	static{
		RUI_RUNTIME_JAVASCRIPT_FILES.add(RUI_RUNTIME_BOOTSTRAP_FILE);  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add(RUI_RUNTIME_LOADER_FILE);  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add(RUI_RUNTIME_JAVASCRIPT_ALL_IN_ONE_FILE);  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("egl.js");  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("egl_mathcontext.js");  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("egl_bigdecimal.js");  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/Constants.js");  //$NON-NLS-1$		
		RUI_RUNTIME_JAVASCRIPT_FILES.add("edt_runtime.js");  //$NON-NLS-1$
//TODO waiting CQ approval bugzilla 5300		RUI_RUNTIME_JAVASCRIPT_FILES.add("webtoolkit.base64.js");  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("egl/jsrt/BaseTypesAndRuntimes.js");  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/Dictionary.js");  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/AnyException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/DynamicAccessException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/Enumeration.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/InvalidArgumentException.js");  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/InvalidIndexException.js");  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/InvalidPatternException.js");  //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/InvocationException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/NullValueException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/NumericOverflowException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/TypeCastException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/rest/ServiceType.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/http/Request.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/http/Response.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/http/HttpRest.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/http/HttpProxy.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/http/HttpLib.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/http/HttpMethod.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/java/JavaObjectException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/javascript/JavaScriptObjectException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/javascript/Job.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/javascript/RuntimeException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/StringLib.js");
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/json/Json.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/json/JsonName.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/json/JSONParser.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/DateTimeLib.js"); 
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/MathLib.js");
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/SysLib.js");
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/lang/OrderingKind.js");
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/rbd/StrLib.js");
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/rest/RestRuntime.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/services/Encoding.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/services/FieldInfo.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/services/ServiceBinder.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/services/ServiceInvocationException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/services/ServiceKind.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/services/ServiceLib.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/services/ServiceRuntimes.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/ui/SignKind.js");
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/ui/rui/RUILib.js");
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/ui/rui/Widget.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/ui/rui/Document.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/ui/rui/Event.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/ui/rui/View.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/ui/rui/FilterKind.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/ui/rui/MappingKind.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/ui/rui/PurposeKind.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/xml/binding/annotation/Xml.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/xml/binding/annotation/XMLStructureKind.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("eglx/xml/Xml.js"); //$NON-NLS-1$
		//These files are needed for EUnit test framework
		RUI_RUNTIME_JAVASCRIPT_FILES.add("org/eclipse/edt/eunit/runtime/AssertionFailedException.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("org/eclipse/edt/eunit/runtime/ConstantsLib.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("org/eclipse/edt/eunit/runtime/Log.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("org/eclipse/edt/eunit/runtime/LogResult.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("org/eclipse/edt/eunit/runtime/MultiStatus.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("org/eclipse/edt/eunit/runtime/Status.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("org/eclipse/edt/eunit/runtime/ServiceBindingType.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("org/eclipse/edt/eunit/runtime/targetLangKind.js"); //$NON-NLS-1$
		RUI_RUNTIME_JAVASCRIPT_FILES.add("org/eclipse/edt/eunit/runtime/TestListMgr.js"); //$NON-NLS-1$
		
	}
	
	public static final List<String> RUI_DEVELOPMENT_JAVASCRIPT_FILES = new ArrayList<String>();
	static{
		RUI_DEVELOPMENT_JAVASCRIPT_FILES.add("egl_development.js");  //$NON-NLS-1$
	};
	
	public final static String RUI_HANDLER = "eglx.ui.rui.RUIHandler";
	public final static String RUI_WIDGET = "eglx.ui.rui.RUIWidget";	
	
	public static String Locale_Key_English = "en_US"; //$NON-NLS-1$
	public static String Locale_Key_English_Runtime = "en_US"; //$NON-NLS-1$
	
	public static String Locale_Key_Arabic_Runtime = "ar"; //$NON-NLS-1$

	public static String Locale_Key_Arabic_Egypt = "ar_EG"; //$NON-NLS-1$
	
	public static String Locale_Key_Arabic_Saudi_Arabia = "ar_SA"; //$NON-NLS-1$

	public static String Locale_Key_Brazilian = "pt_BR"; //$NON-NLS-1$
	public static String Locale_Key_Brazilian_Runtime = "pt_BR"; //$NON-NLS-1$
	
	public static String Locale_Key_Chinese_Simplified = "zh_CN"; //$NON-NLS-1$
	public static String Locale_Key_Chinese_Simplified_Runtime = "zh"; //$NON-NLS-1$
	public static String Locale_Key_Chinese_Taiwan = "zh_TW"; //$NON-NLS-1$
	public static String Locale_Key_Chinese_Taiwan_Runtime = "zh_TW"; //$NON-NLS-1$
	
	public static String Locale_Key_Chinese_Hong_Kong = "zh_HK"; //$NON-NLS-1$
	public static String Locale_Key_Chinese_Hong_Kong_Runtime = "zh_HK"; //$NON-NLS-1$
	
	public static String Locale_Key_Czech = "cs_CZ"; //$NON-NLS-1$
	public static String Locale_Key_Czech_Runtime = "cs"; //$NON-NLS-1$
	
	public static String Locale_Key_French = "fr_FR"; //$NON-NLS-1$
	public static String Locale_Key_French_Runtime = "fr"; //$NON-NLS-1$
	
	public static String Locale_Key_German = "de_DE"; //$NON-NLS-1$
	public static String Locale_Key_German_Runtime = "de"; //$NON-NLS-1$
	
	public static String Locale_Key_Hungarian = "hu_HU"; //$NON-NLS-1$
	public static String Locale_Key_Hungarian_Runtime = "hu"; //$NON-NLS-1$
	
	public static String Locale_Key_Italian = "it_IT"; //$NON-NLS-1$
	public static String Locale_Key_Italian_Runtime = "it"; //$NON-NLS-1$
	
	public static String Locale_Key_Japanese = "ja_JP"; //$NON-NLS-1$
	public static String Locale_Key_Japanese_Runtime = "ja"; //$NON-NLS-1$
	
	public static String Locale_Key_Korean = "ko_KR"; //$NON-NLS-1$
	public static String Locale_Key_Korean_Runtime = "ko"; //$NON-NLS-1$
	
	public static String Locale_Key_Polish = "pl_PL"; //$NON-NLS-1$
	public static String Locale_Key_Polish_Runtime = "pl"; //$NON-NLS-1$
	
	public static String Locale_Key_Russian = "ru_RU"; //$NON-NLS-1$
	public static String Locale_Key_Russian_Runtime = "ru"; //$NON-NLS-1$
	
	public static String Locale_Key_Spanish = "es_ES"; //$NON-NLS-1$
	public static String Locale_Key_Spanish_Runtime = "es"; //$NON-NLS-1$

}
