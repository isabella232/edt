/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
load("runtime", "eglx.lang", ["AnyException", "DynamicAccessException", "Enumeration",
                             "InvalidArgumentException", "InvalidIndexException", "InvalidPatternException",
                             "InvocationException", "NullValueException",
                             "NumericOverflowException", "TypeCastException", ]);
load("runtime", "eglx.lang", ["Constants", "DateTimeLib", "Dictionary", "OrderingKind", "MathLib", "Resources", "StringLib", "SysLib"]);
load("runtime", "eglx.ui.rui", ["Widget", "Document", "Event", "View"]);
load("runtime", "egl.ui.gateway", ["UIGatewayRecord"]);

load("runtime", "", ["edt_runtime"]);
load("runtime", "eglx.javascript", ["Job", "JavaScriptObjectException", "RuntimeException"]);
load("runtime", "eglx.java", ["JavaObjectException"]);
load("runtime", "eglx.rbd", ["StrLib"]);
load("runtime", "eglx.services", ["ServiceKind", "ServiceBinder","ServiceInvocationException", "ServiceLib", "ServiceRuntimes"]);
load("runtime", "eglx.json", ["Json", "JSONParser"]);
load("runtime", "eglx.xml.binding.annotation", ["Xml"]);
load("runtime", "eglx.xml", ["Xml"]);
load("runtime", "eglx.http", ["Http"]);

