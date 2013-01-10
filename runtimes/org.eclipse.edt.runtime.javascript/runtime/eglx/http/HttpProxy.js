/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
egl.defineClass('eglx.http', 'HttpProxy','eglx.http', 'HttpRest',
{
	'eze$$fileName': 'eglx/http/HttpProxy.egl',
	'eze$$runtimePropertiesFile': 'eglx.http.HttpProxy',
		"constructor": function() {
			this.eze$$setInitial();
			this.restType = egl.checkNull(egl.eglx.rest.ServiceType).EglDedicated;
			egl.checkNull(this.request).uri = arguments[0] || null;;
		},
		"eze$$clone": function() {
			var ezert$$1 = this;
			var ezert$$2 = new egl.eglx.http.HttpProxy();
			ezert$$2.request = ezert$$1.request === null ? null : ezert$$1.request.eze$$clone();
			ezert$$2.response = ezert$$1.response === null ? null : ezert$$1.response.eze$$clone();
			ezert$$2.restType = ezert$$1.restType === null ? null : ezert$$1.restType;
			return ezert$$2;
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("HttpProxy", null, false);
			}
			return this.annotations;
		}
		,
		"toString": function() {
			return "[HttpProxy]";
		}
	}
);
