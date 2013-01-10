define(["org/eclipse/edt/eunit/runtime/TestListMgr", "common/utilities", "org/eclipse/edt/eunit/runtime/ServiceBindingType"],function(){
	egl.defineRUILibrary('common', 'clientlocal',
	{
		'eze$$fileName': 'common/clientlocal.egl',
		'eze$$runtimePropertiesFile': 'common.clientlocal',
			"constructor": function() {
				if(egl.common.clientlocal['$inst']) return egl.common.clientlocal['$inst'];
				egl.common.clientlocal['$inst']=this;
				new egl.org.eclipse.edt.eunit.runtime.TestListMgr();
				new egl.common.utilities();
				this.eze$$setInitial();
			}
			,
			"eze$$setEmpty": function() {
			}
			,
			"eze$$setInitial": function() {
				this.eze$$setEmpty();
			}
			,
			"eze$$getAnnotations": function() {
				if(this.annotations === undefined){
					this.annotations = {};
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("clientlocal", null, false);
				}
				return this.annotations;
			}
			,
			"eze$$getFieldInfos": function() {
				if(this.fieldInfos === undefined){
					var annotations;
					this.fieldInfos = new Array();
				}
				return this.fieldInfos;
			}
			,
			"InitTestLibary": function(i) {
				egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testLibName = egl.checkNull(egl.checkNull(egl.common.utilities['$inst']).ArrayCase)[egl.checkNull(egl.common.utilities['$inst']).ArrayCase.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : egl.inferSignature(i)}, false) - 1)];
			}
			,
			"testService": function(tests, serviceBinding) {
				egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).bindingType = serviceBinding;
				{
					var idx = 0;
					idx = 1;
					while (true) {
						var eze$Temp1 = false;
						var eze$Temp2;
						eze$Temp2 = ((idx <= tests.getSize()));
						if (eze$Temp2) {
							eze$Temp1 = true;
						}
						if (!eze$Temp1) break;
						do {
							this.InitTestMethod("Array");
							this.InitrunTestMethod("Array");
							egl.checkNull(egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds)[egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.checkIndex(1 - 1)]();
						}
						while (false);
						if (!eze$Temp1) break;
						idx = ((idx + 1));
					}
				}
			}
			,
			"InitTestMethod": function(lib) {
				egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.removeAll();
				{
					EzeLabel_eze_CaseLabel_0: if (((lib) == "Array")) {
						egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).ArrayCase);
					}
					else {
						if (((lib) == "ArraysMultiDim")) {
							egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).ArrayMul);
						}
						else {
							if (((lib) == "ExceptionProducer")) {
								egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).Exp);
							}
							else {
								if (((lib) == "Handlers")) {
									egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).hdl);
								}
								else {
									if (((lib) == "Nulls")) {
										egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).nul);
									}
									else {
										if (((lib) == "Primitives")) {
											egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).pri);
										}
										else {
											if (((lib) == "RecordsFlex")) {
												egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).rcd);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			,
			"InitrunTestMethod": function(lib) {
				egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.removeAll();
				{
					EzeLabel_eze_CaseLabel_1: if (((lib) == "Array")) {
						egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestArray);
					}
					else {
						if (((lib) == "ArraysMultiDim")) {
							egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestArrayMul);
						}
						else {
							if (((lib) == "ExceptionProducer")) {
								egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestExp);
							}
							else {
								if (((lib) == "Handlers")) {
									egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTesthdl);
								}
								else {
									if (((lib) == "Nulls")) {
										egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestnull);
									}
									else {
										if (((lib) == "Primitives")) {
											egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestpri);
										}
										else {
											if (((lib) == "RecordsFlex")) {
												egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestrcd);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			,
			"toString": function() {
				return "[clientlocal]";
			}
		}
	);
});
