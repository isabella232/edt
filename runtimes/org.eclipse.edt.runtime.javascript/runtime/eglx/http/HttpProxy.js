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
