/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.deployment.javascript.templates;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.interfaces.IEGLMessageContributor;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.deployment.javascript.Constants;
import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.gen.deployment.util.WorkingCopyGenerationResult;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.javart.resources.LocalizedText;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;

public class RUITemplate extends JavaScriptTemplate {	
	
	private static final String DEFAULT_THEME = "claro";
	
	private static final Map<String, String> JAVASCRIPT_NOT_SUPPORTED_STRINGS = new HashMap<String, String>();
	static{
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Arabic_Runtime, "JavaScript \u063a\u064a\u0631 \u0645\u062f\u0639\u0645 \u062d\u0627\u0644\u064a\u0627 \u0623\u0648 \u0644\u0645 \u064a\u062a\u0645 \u0627\u062a\u0627\u062d\u062a\u0647 \u0628\u0648\u0627\u0633\u0637\u0629 \u0628\u0631\u0646\u0627\u0645\u062c \u0627\u0644\u0627\u0633\u062a\u0639\u0631\u0627\u0636 \u0647\u0630\u0627. \u0628\u0631\u062c\u0627\u0621 \u0627\u062a\u0627\u062d\u0629 JavaScript \u0644\u0644\u062d\u0635\u0648\u0644 \u0639\u0644\u0649 \u0627\u0644\u0648\u0638\u0627\u0626\u0641 \u0628\u0627\u0644\u0643\u0627\u0645\u0644.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_English, "JavaScript is currently not supported or enabled by this browser.  Please enable JavaScript for full functionality.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Brazilian, "O JavaScript n\u00e3o \u00e9 atualmente suportado ou n\u00e3o est\u00e1 ativado por este navegador.  Ative o JavaScript para funcionalidade completa.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Chinese_Simplified, "\u6b64\u6d4f\u89c8\u5668\u5f53\u524d\u4e0d\u652f\u6301\u6216\u8005\u672a\u542f\u7528 JavaScript\u3002\u8bf7\u542f\u7528 JavaScript \u4ee5\u83b7\u53d6\u6240\u6709\u529f\u80fd\u3002");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Chinese_Taiwan, "\u9019\u500b\u700f\u89bd\u5668\u76ee\u524d\u4e0d\u652f\u63f4\u6216\u672a\u555f\u7528 JavaScript\u3002\u8acb\u555f\u7528 JavaScript\uff0c\u4ee5\u53d6\u5f97\u5b8c\u6574\u529f\u80fd\u3002");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Chinese_Hong_Kong, "\u9019\u500b\u700f\u89bd\u5668\u76ee\u524d\u4e0d\u652f\u63f4\u6216\u672a\u555f\u7528 JavaScript\u3002\u8acb\u555f\u7528 JavaScript\uff0c\u4ee5\u53d6\u5f97\u5b8c\u6574\u529f\u80fd\u3002");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Czech, "Moment\u00e1ln\u011b nen\u00ed v tomto prohl\u00ed\u017ee\u010di podporov\u00e1n nebo povolen JavaScript.  Pros\u00edm povolte JavaScript pro \u00faplnou funk\u010dnost.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_French, "JavaScript n'est pas actuellement accept\u00e9 par ce navigateur ou il n'est pas activ\u00e9. Activez JavaScript afin de b\u00e9n\u00e9ficier de toutes les fonctionnalit\u00e9s.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_German, "JavaScript wird gegenw\u00e4rtig von diesem Browser nicht unterst\u00fctzt oder ist nicht aktiviert. Bitte aktivieren Sie JavaScript, um eine vollst\u00e4ndige Funktionalit\u00e4t zu erzielen.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Hungarian, "A b\u00f6ng\u00e9sz\u0151 pillanatnyilag nem t\u00e1mogatja vagy enged\u00e9lyezi a JavaScript futtat\u00e1s\u00e1t. A teljes funkcionalit\u00e1s \u00e9rdek\u00e9ben enged\u00e9lyezze a JavaScript futtat\u00e1s\u00e1t.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Italian, "JavaScript attualmente non \u00e8 supportato o abilitato da questo browser. Abilitare JavaScript per ottenere la completa funzionalit\u00e0.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Japanese, "\u3053\u306e\u30d6\u30e9\u30a6\u30b6\u30fc\u3067\u306f\u3001\u73fe\u5728 JavaScript \u304c\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u306a\u3044\u304b\u3001\u4f7f\u7528\u53ef\u80fd\u306b\u306a\u3063\u3066\u3044\u307e\u305b\u3093\u3002JavaScript \u306e\u5168\u6a5f\u80fd\u3092\u4f7f\u7528\u53ef\u80fd\u306b\u3057\u3066\u304f\u3060\u3055\u3044\u3002");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Korean, "JavaScript\ub294 \ud604\uc7ac \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uac70\ub098 \uc774 \ube0c\ub77c\uc6b0\uc800\uc5d0\uc11c \uc0ac\uc6a9\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4. \uc804\uccb4 \uae30\ub2a5\uc5d0 JavaScript\ub97c \uc0ac\uc6a9\ud558\uc2ed\uc2dc\uc624.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Polish, "JavaScript nie jest obecnie obs\u0142ugiwany lub w\u0142\u0105czony w tej przegl\u0105darce. W\u0142\u0105cz obs\u0142ug\u0119 JavaScript, aby zapewni\u0107 pe\u0142n\u0105 funkcjonalno\u015b\u0107.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Russian, "\u042d\u0442\u043e\u0442 \u0431\u0440\u0430\u0443\u0437\u0435\u0440 \u0432 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442 JavaScript, \u0438\u043b\u0438 \u044d\u0442\u0430 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0430. \u0414\u043b\u044f \u0434\u043e\u0441\u0442\u0443\u043f\u0430 \u043a\u043e \u0432\u0441\u0435\u043c \u0444\u0443\u043d\u043a\u0446\u0438\u043e\u043d\u0430\u043b\u044c\u043d\u044b\u043c \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044f\u043c \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u0432\u043a\u043b\u044e\u0447\u0438\u0442\u044c JavaScript.");
		JAVASCRIPT_NOT_SUPPORTED_STRINGS.put(Constants.Locale_Key_Spanish, "Actualmente este buscador no soporta ni habilita JavaScript.  Habilite JavaScript para obtener todas las funciones.");
	}	
	
	protected void genHTML(boolean isDevelopment, Handler handler, Context ctx, TabbedWriter out,
			List<String> egldds, HashMap<String, String> eglParameters, String userMsgLocale,
			String runtimeMsgLocale, boolean enableEditing, boolean contextAware, boolean isDebug) {
		
		out.println( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "+ //$NON-NLS-1$
				"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"); //$NON-NLS-1$
		preGenComment(out);
		out.println( "<html>" ); //$NON-NLS-1$
		out.println( "<head>" );		
		out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>");
		generateTitle(handler, out);
		out.println("<script type=\"text/javascript\" src=\"" + Constants.RUNTIME_FOLDER_NAME + "/" + Constants.RUI_RUNTIME_BOOTSTRAP_FILE + "\" ></script>");
		out.println("<script type=\"text/javascript\" src=\"" + Constants.RUNTIME_FOLDER_NAME + "/" + Constants.RUI_RUNTIME_LOADER_FILE + "\" ></script>");
		out.println("<script type=\"text/javascript\">"); //$NON-NLS-1$
		if(isDevelopment){
			generateHeader(handler, out, enableEditing, contextAware, isDebug);
		}		
		generateEGLParameters(out, eglParameters, userMsgLocale, runtimeMsgLocale);
		out.println("</script>");//$NON-NLS-1$		
		out.println("</head>");
		out.println("<body class=\"" + getTheme(handler) + "\">");		
		generateNoJavaScriptCheck(runtimeMsgLocale, out);
		out.println("<script type=\"text/javascript\">"); //$NON-NLS-1$
		generateStartupInit(handler, ctx, out, userMsgLocale, isDevelopment, isDebug, egldds, enableEditing, contextAware, eglParameters, runtimeMsgLocale);
		out.println("</script>");//$NON-NLS-1$		
		out.println("</body>");		
		out.println("</html>");	
	}
	
	public void genDevelopmentHTML(Handler handler, Context ctx, TabbedWriter out, List<String> egldds, HashMap<String, String> eglParameters, String userMsgLocale, String runtimeMsgLocale, Boolean enableEditing, Boolean contextAware, Boolean isDebug){
		genHTML(true, handler, ctx, out, egldds, eglParameters, userMsgLocale, runtimeMsgLocale, enableEditing, contextAware, isDebug);		
	}	
	
	public void preGenComment(TabbedWriter out){
		long startTime = System.currentTimeMillis();
		out.println( "<!-- Generated at " + new Date( startTime ) + " by EGL " + " -->" );
	}
	
	private void generateTitle(Part part, TabbedWriter out) {
		String title = null;
		Annotation annot = part.getAnnotation(Constants.RUI_HANDLER);
		if (annot != null) {
			title = (String)annot.getValue("title");
		}
		if (title == null || title.length() == 0) {
			title = part.getCaseSensitiveName();
		}
		out.print( "<title>" ); //$NON-NLS-1$
		out.print( title );
		out.println( "</title>" ); //$NON-NLS-1$
	}
	
	private void generateNoJavaScriptCheck(String runtimeMsgLocale, TabbedWriter out){
		String result = (String)JAVASCRIPT_NOT_SUPPORTED_STRINGS.get(runtimeMsgLocale);
		if(result == null){
			result = "<noscript>Your browser does not support JavaScript!</noscript>";
		}else{
			result = "<noscript>" + result + "</noscript>";
		}
		out.println(result);
	}
	
	private void generateHeader(Handler handler, TabbedWriter out, boolean enableEditing, boolean contextAware, boolean isDebug) {
		out.println("egl__debugg=" + isDebug + ";"); //$NON-NLS-1$
		out.println("egl__enableEditing=" + enableEditing + ";"); //$NON-NLS-1$
		out.println("egl__contextAware=" + contextAware + ";"); //$NON-NLS-1$
	}
	
	private void generateEGLParameters(TabbedWriter out, HashMap<String, String> eglParameters, String userMsgLocale, String runtimeMsgLocale) {
		/**
		 * output all the passed egl parameters
		 */
		String params = "\"" + eglParameters.get("egl__contextRoot") + "\", \"" + eglParameters.get("egl__defaultDeploymentDescriptor") + "\", \"" + userMsgLocale + "\", \"" + runtimeMsgLocale;
		Locale locale;
		int idx = userMsgLocale.indexOf('_');
		if (idx == -1) {
			locale = new Locale(userMsgLocale);
		}
		else {
			locale = new Locale(userMsgLocale.substring(0, idx), userMsgLocale.substring(idx+1));
		}
		
		String shortMask = getLongGregorianDateMask( DateFormat.SHORT, locale );
		String mediumMask = getLongGregorianDateMask( DateFormat.MEDIUM, locale );
		String longMask = getLongGregorianDateMask( DateFormat.LONG, locale );
		
		params = params + "\", \"" + getNLSCode(locale) + "\", \"" + shortMask + "\", \"" + mediumMask + "\", \"" + longMask;		
		
		NumberFormat formatter = NumberFormat.getNumberInstance(locale);
		if (formatter instanceof DecimalFormat) {
			DecimalFormatSymbols symbols = ((DecimalFormat)formatter).getDecimalFormatSymbols();
			params = params + "\", \"" + symbols.getCurrencySymbol() + "\", \"" + symbols.getDecimalSeparator() + "\", \"" + symbols.getGroupingSeparator() + "\"";
		}
		
		out.println("egl.initParams(" + params + ");");
	}
	
	private void genModuleName(Handler part, boolean isDevelopment, StringBuilder buf) {
		buf.append("\"");
		String pkg = part.getCaseSensitivePackageName();
		if (pkg.length() > 0) {
			buf.append(JavaScriptAliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}
		buf.append(JavaScriptAliaser.getAlias(part.getCaseSensitiveName()));
		if(isDevelopment){
			buf.append(".js?contextKey=\" + egl__contextKey");
		}else{
			buf.append("\"");
		}		
	}
	
	private void generateStartupInit(Handler part, Context ctx, TabbedWriter out, String userMsgLocale, boolean isDevelopment, boolean isDebug, 
			List<String> egldds, boolean enableEditing, boolean contextAware, HashMap<String, String> eglParameters, 
			String runtimeMsgLocale) {		
		StringBuilder buf = new StringBuilder();
		genModuleName(part, isDevelopment, buf);
		out.print("require([");
		out.print("\"" + Constants.RUNTIME_FOLDER_NAME + "/" + Constants.RUI_RUNTIME_JAVASCRIPT_ALL_IN_ONE_FILE + "\"");
		if(isDevelopment){
			for (String runtimeFile : Constants.RUI_DEVELOPMENT_JAVASCRIPT_FILES) {
				if(!Constants.RUI_RUNTIME_JAVASCRIPT_ALL_IN_ONE_FILE.equals(runtimeFile)){
					out.print(", \"" + Constants.RUNTIME_FOLDER_NAME + "/" + runtimeFile + "\"");
				}
			}
		}
		out.println("], function() {");	
		out.print("require([");	
		out.print(buf.toString());
		out.print(", \"" + Constants.RUNTIME_FOLDER_NAME + "/" + Constants.RUNTIME_MESSAGES_DEPLOYMENT_FOLDER_NAME + "/" + 
				Constants.RUI_MESSAGE_FILE  + "-" + runtimeMsgLocale + "\"");
		// Gen bnd file
		if (egldds != null && egldds.size() > 0) {
			@SuppressWarnings("unchecked")
			List<Type> processedParts = (List<Type>)ctx.get(genBindFiles);
			if(processedParts == null){
				processedParts = new ArrayList<Type>();
				ctx.put(genBindFiles, processedParts);
			}
			processedParts.add(part);
			try {
				for ( int i = 0; i < egldds.size(); i ++ ) {
					String next = (String)egldds.get(i);
					if (next != null && next.length() > 0) {
						out.print(", ");
						if(isDevelopment)
							out.print("egl__href + ");
						out.print("\"" + next + "-bnd.js\"");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		if(isDevelopment){
			out.println("], egl.startupInitCallback = function() {");	
		}else{
			out.println("], function() {");	
		}		
		out.print("egl.init(");
		out.println("function(){");
		out.println("if(egl." + getFullPartName(part) + "){");
		if(isDevelopment){
			generateDevelopmentRootHandler(part, out, isDebug);
		}else{
			generateRootHandler(part,out);
		}
		out.println("}else{");
		out.println("egl.reportHandlerLoadError(\"egl." + getFullPartName(part) + "\");");
		out.println("}");
		out.println("}");
		out.println(");");
		out.println("});");
		out.println("});");
	}

	private String getFullPartName(Handler part) {
		String packageName = part.getCaseSensitivePackageName().replace('/', '.').toLowerCase();
		if(packageName != null && !(packageName.isEmpty())){
			packageName +=".";
		}else{
			packageName = "";
		}
		return packageName + part.getCaseSensitiveName();
	}
	
	private void generateRootHandler( Handler part, TabbedWriter out ) {
		out.println("			egl.rootHandler = new egl." + getFullPartName(part) + "();");
		out.println( "  		egl.rootHandler.setParent(egl.Document);" ); //$NON-NLS-1$
		out.println( "  		egl.startup();" ); //$NON-NLS-1$
	}
	
	public void genErrorHTML(Handler handler, Context ctx, TabbedWriter out , String msg) {
		WorkingCopyGenerationResult problemRequestor = (WorkingCopyGenerationResult)(ctx.getMessageRequestor());
		out.println( "<html>");
		
		EGLMessage message = EGLMessage.createEGLMessage(ctx.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, org.eclipse.edt.gen.deployment.javascript.Constants.EGLMESSAGE_GENERATION_FAILED,
				null, null, null);
		out.println( "<body>");
		out.println( "<h2>" + message.getBuiltMessage() + "</h2>");
		out.println( msg + "<br>");
		out.println( "<hr/>");
		out.println("<div style=\"color:red\">Generation Error</div>");
		for(Iterator<?> iter = problemRequestor.getMessages().iterator(); iter.hasNext();) {
			EGLMessage nextMsg = (EGLMessage) iter.next();
			String colorStart = "", colorEnd = "";
			String onClickStart = "", onClickEnd = "";
			if(nextMsg.isError()) {	
				colorStart = onClickStart + "<strong><font color=\"red\">";
				colorEnd = "</font></strong>" + onClickEnd;
			}
			else if(nextMsg.isWarning()) {
				colorStart = "<strong><font color=\"yellow\">";
				colorEnd = "</font></strong>";
			}
			int lineNumber = nextMsg.getStartLine();
			if (lineNumber != 0) {
				Object messageContributor = nextMsg.getMessageContributor();
				if(messageContributor != null) {
					IEGLMessageContributor msgContributor = (IEGLMessageContributor) messageContributor;
					String resourceName = msgContributor.getResourceName();		
					// TODO Need to understand it
//					if(resourceName.startsWith(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString())){
//						resourceName = resourceName.substring(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString().length());
//					}
					
					onClickStart = "<A href=\"javascript:selectInEditor('" + 
						resourceName.replaceAll("\\\\", "/") + "'," + lineNumber + ");\">";
					onClickEnd = "</A>";
				}
			}
			colorStart = onClickStart + colorStart;
			colorEnd = colorEnd + onClickEnd;
			out.println( colorStart + nextMsg.getBuiltMessage() + colorEnd + "<br/>");
		}
		out.println( "</body>");
		out.println("<script type=\"text/javascript\">"); //$NON-NLS-1$
		out.println("egl__debugg=false;");
		out.println("egl__contextAware=true;"); //$NON-NLS-1$
		out.println("egl__enableEditing=false");
		out.println("</script>"); //$NON-NLS-1$
		out.println( "<script type=\"text/javascript\" src=\"egl.js\"></script>");
		out.println( "<script type=\"text/javascript\" src=\"egl_development.js\"></script>");
		out.println( "<script type=\"text/javascript\">");
		out.println( "selectInEditor = function(file, line) {");
		out.println( "	egl.loadIDEURL(\"___openFile?file=\"+file+\"&line=\"+line);");
		out.println( "}");
		out.println( "</script>");
		out.println( "</html>");
	}
	
	public void genCompileErrorHTML(Handler handler, Context ctx, TabbedWriter out, String message){
		out.println("<html>");
		out.println("<body>");
		out.println(message);
		out.println("</body>");
		out.println("</html>");
	}		
	
	private void generateDevelopmentRootHandler( Handler part, TabbedWriter out, boolean isDebug) {
		if (isDebug) {
			out.println("			egl.startHandleIDEEvent();");
		}
		out.println("			egl.rootHandler = new egl." + getFullPartName(part) + "();");
		out.println("			if ( egl.rootHandler.targetWidget || !egl.rootHandler.egl$isWidget ) {");
		out.println("				egl.rootHandler.setParent(egl.Document);");
		out.println("			} else {");
		out.println("				var package = egl.rootHandler.eze$$package;");
		out.println("				var typename = egl.rootHandler.eze$$typename;");
		out.println("				egl.rootHandler = egl.Document;");
		out.println("				egl.rootHandler.eze$$package = package;");
		out.println("				egl.rootHandler.eze$$typename = typename;");
		out.println("			}");
		out.println("			egl.startup();");
	}

	private String getTheme(Handler handler) {
		String theTheme = "";
		Annotation a = handler.getAnnotation( Constants.RUI_HANDLER );
		if( a != null && a.getValue( IEGLConstants.PROPERTY_THEME ) != null){
			theTheme = (String) a.getValue( IEGLConstants.PROPERTY_THEME );
		}
		if ( theTheme != null && theTheme.length() > 0 ){
			return theTheme;
		}else{
			return DEFAULT_THEME;
		}		
	}		
	
	private String getLongGregorianDateMask( int style, Locale locale ) {
		DateFormat formatter = DateFormat.getDateInstance( style, locale );
		// The default value.
		String def = "MM-dd-yyyy";

		// Cast it to a SimpleDateFormat.
		SimpleDateFormat sdf;
		try
		{
			sdf = (SimpleDateFormat)formatter;
		}
		catch ( ClassCastException cce )
		{
			// If we can't get a SimpleDateFormat for our Locale, there's
			// nothing we can do but return the default mask.
			return def;
		}

		// Get the formatter's pattern and turn it into a date mask.
		String mask = LocalizedText.parseDateFormatPattern( sdf.toPattern(), false );
		if ( LocalizedText.isGregorianDateMask( mask, false ) )
		{
			return mask;
		}
		else
		{
			// It's not valid, so return the default value.
			return def;
		}
	}
	
	private String getNLSCode(Locale locale) {
		String language = locale.getLanguage();
		String country = locale.getCountry();

		if ( language.equals( "en" ) )
			return "ENU";
		else if ( language.equals( "de" ) )
		{
			if ( country.equals( "CH" ) )
				return "DES";
			else
				return "DEU";
		}
		else if ( language.equals( "es" ) )
			return "ESP";
		else if ( language.equals( "pt" ) )
			return "PTB";
		else if ( language.equals( "ko" ) )
			return "KOR";
		else if ( language.equals( "fr" ) )
			return "FRA";
		else if ( language.equals( "it" ) )
			return "ITA";
		else if ( language.equals( "ja" ) )
			return "JPN";
		else if ( language.equals( "zh" ) )
		{
			// Use Traditional Chinese for Taiwan and Hong Kong, and Simplified
			// Chinese for all other Chinese locales. 
			if ( country.equals( "TW" ) || country.equals( "HK" ) )
				return "CHT";
			else
				return "CHS";
		}
		else if ( language.equals( "ru" ) )
			return "RUS";
		else if ( language.equals( "pl" ) )
			return "PLK";
		else if ( language.equals( "hu" ) )
			return "HUN";
		else if ( language.equals( "cs" ) )
			return "CZE";
		else
			return "ENU";
	}
}
