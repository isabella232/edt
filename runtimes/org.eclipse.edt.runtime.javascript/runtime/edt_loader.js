/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2008, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */
function load(path, pkg, parts){
	for (var i=0; i<parts.length; i++){
		var fname = ((path.length==0) ? "." : path) + "/" + pkg.replace(/\./g, "/") + "/" + parts[i] + ".js";
		var jsfile=document.createElement('script');
		jsfile.src = fname; 		
		document.getElementsByTagName("head")[0].appendChild(jsfile);
	}
}


egl__debugg=false;
egl__enableEditing=false;
egl__contextAware=true;
egl__defaultRuntimeMessagesLocale="en_US";
egl__contextRoot="EDT";
egl__htmlLocale="en_US";


egl = function() { };
egl.eze$$rscBundles = {};
egl.eze$$runtimeProperties = {};


load("messages", "", ["RuiMessages-en_US"]);
load("runtime", "",  ["egl", "egl_development", "egl_mathcontext", "egl_bigdecimal"]);
load("runtime", "egl.jsrt", ["BaseTypesAndRuntimes"]);
load("runtime", "egl.lang", ["AnyException", /*, "HttpRequest", "HttpResponse" */
                             , "InvalidIndexException", "InvocationException", "JavaObjectException"
                             , "DynamicAccessException", "NullValueException", "TypeCastException"
                             , "InvalidPatternException", "NumericOverflowException"]);
load("runtime", "eglx.ui.rui", ["Widget", "Document", "Event", "View"]);
load("runtime", "egl.ui.gateway", ["UIGatewayRecord"]);

load("runtime", "", ["edt_runtime", "edt_runtime_fixups"]);
load("runtime", "eglx.javascript", ["Job", "JavaScriptObjectException"]);
load("runtime", "eglx.java", ["JavaObjectException"]);
load("runtime", "eglx.rbd", ["StrLib"]);
load("runtime", "eglx.services", ["ServiceBinder","ServiceBindingException","ServiceInvocationException", "ServiceLib", "ServiceRuntimes"]);
load("runtime", "eglx.json", ["Json", "JSONParser"]);
load("runtime", "eglx.xml.binding.annotation", ["Xml"]);
load("runtime", "eglx.xml", ["Xml"]);
load("runtime", "eglx.http", ["Http"]);

