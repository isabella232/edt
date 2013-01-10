define(["org/eclipse/edt/eunit/runtime/TestListMgr", "common/utilities", "org/eclipse/edt/eunit/runtime/ServiceBindingType"],function(){
	if (egl.eze$$userLibs) egl.eze$$userLibs.push('common.clientlocal');
	else egl.eze$$userLibs = ['common.clientlocal'];
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
				try { egl.enter("<init>",this,arguments);
					this.eze$$setEmpty();
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
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
				try { egl.enter("InitTestLibary",this,arguments);
					egl.addLocalFunctionVariable("i", i, "eglx.lang.EInt", "i");
					egl.atLine(this.eze$$fileName,7,108,39, this);
					egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testLibName = egl.checkNull(egl.checkNull(egl.common.utilities['$inst']).ArrayCase, "ArrayCase")[egl.checkNull(egl.common.utilities['$inst']).ArrayCase.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : egl.inferSignature(i)}, false) - 1)];
					egl.atLine(this.eze$$fileName,6,72,80, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"testService": function(tests, serviceBinding) {
				try { egl.enter("testService",this,arguments);
					egl.addLocalFunctionVariable("tests", tests, "eglx.lang.EList<eglx.lang.EString>", "!tests");
					egl.addLocalFunctionVariable("serviceBinding", serviceBinding, "org.eclipse.edt.eunit.runtime.ServiceBindingType", "serviceBinding");
					egl.atLine(this.eze$$fileName,11,237,41, this);
					egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).bindingType = serviceBinding;
					egl.atLine(this.eze$$fileName,13,284,826, this);
					{
						try{egl.enterBlock();
							var idx = 0;
							egl.addLocalFunctionVariable("idx", idx, "eglx.lang.EInt", "idx");
							egl.atLine(this.eze$$fileName,13,284,826, this);
							idx = 1;
							egl.setLocalFunctionVariable("idx", idx, "eglx.lang.EInt");
							while (true) {
								var eze$Temp1 = false;
								var eze$Temp2;
								egl.atLine(this.eze$$fileName,13,284,826, this);
								eze$Temp2 = ((idx <= tests.getSize()));
								egl.atLine(this.eze$$fileName,13,284,826, this);
								if (eze$Temp2) {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,13,284,826, this);
										eze$Temp1 = true;
									}finally{egl.exitBlock();}
								}
								if (!eze$Temp1) break;
								do {
									egl.setLocalFunctionVariable("idx", idx, "eglx.lang.EInt");
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,16,378,24, this);
										this.InitTestMethod("Array");
										egl.atLine(this.eze$$fileName,17,408,27, this);
										this.InitrunTestMethod("Array");
										egl.atLine(this.eze$$fileName,37,1075,29, this);
										egl.checkNull(egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds, "runTestMtds")[egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.checkIndex(1 - 1)]();
									}finally{egl.exitBlock();}
									egl.atLine(this.eze$$fileName,13,284,826, this);
								}
								while (false);
								if (!eze$Temp1) break;
								egl.atLine(this.eze$$fileName,13,284,826, this);
								idx = ((idx + 1));
								egl.setLocalFunctionVariable("idx", idx, "eglx.lang.EInt");
							}
						}
						finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,10,156,959, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"InitTestMethod": function(lib) {
				try { egl.enter("InitTestMethod",this,arguments);
					egl.addLocalFunctionVariable("lib", lib, "eglx.lang.EString", "lib");
					egl.atLine(this.eze$$fileName,43,1159,40, this);
					egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.removeAll();
					{
						egl.atLine(this.eze$$fileName,44,1202,518, this);
						EzeLabel_eze_CaseLabel_0: egl.atLine(this.eze$$fileName,44,1202,518, this);
						if (((lib) == "Array")) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,46,1235,49, this);
								egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).ArrayCase);
							}finally{egl.exitBlock();}
						}
						else {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,47,1288,75, this);
								if (((lib) == "ArraysMultiDim")) {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,48,1315,48, this);
										egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).ArrayMul);
									}finally{egl.exitBlock();}
								}
								else {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,49,1367,73, this);
										if (((lib) == "ExceptionProducer")) {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,50,1397,43, this);
												egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).Exp);
											}finally{egl.exitBlock();}
										}
										else {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,51,1444,64, this);
												if (((lib) == "Handlers")) {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,52,1465,43, this);
														egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).hdl);
													}finally{egl.exitBlock();}
												}
												else {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,53,1512,61, this);
														if (((lib) == "Nulls")) {
															try{egl.enterBlock();
																egl.atLine(this.eze$$fileName,54,1530,43, this);
																egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).nul);
															}finally{egl.exitBlock();}
														}
														else {
															try{egl.enterBlock();
																egl.atLine(this.eze$$fileName,55,1577,66, this);
																if (((lib) == "Primitives")) {
																	try{egl.enterBlock();
																		egl.atLine(this.eze$$fileName,56,1600,43, this);
																		egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).pri);
																	}finally{egl.exitBlock();}
																}
																else {
																	try{egl.enterBlock();
																		egl.atLine(this.eze$$fileName,57,1647,67, this);
																		if (((lib) == "RecordsFlex")) {
																			try{egl.enterBlock();
																				egl.atLine(this.eze$$fileName,58,1671,43, this);
																				egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).testMethodNames.appendAll(egl.checkNull(egl.common.utilities['$inst']).rcd);
																			}finally{egl.exitBlock();}
																		}
																	}finally{egl.exitBlock();}
																}
															}finally{egl.exitBlock();}
														}
													}finally{egl.exitBlock();}
												}
											}finally{egl.exitBlock();}
										}
									}finally{egl.exitBlock();}
								}
							}finally{egl.exitBlock();}
						}
					}
					egl.atLine(this.eze$$fileName,42,1121,604, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"InitrunTestMethod": function(lib) {
				try { egl.enter("InitrunTestMethod",this,arguments);
					egl.addLocalFunctionVariable("lib", lib, "eglx.lang.EString", "lib");
					egl.atLine(this.eze$$fileName,63,1770,36, this);
					egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.removeAll();
					{
						egl.atLine(this.eze$$fileName,64,1809,536, this);
						EzeLabel_eze_CaseLabel_1: egl.atLine(this.eze$$fileName,64,1809,536, this);
						if (((lib) == "Array")) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,66,1842,48, this);
								egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestArray);
							}finally{egl.exitBlock();}
						}
						else {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,67,1894,78, this);
								if (((lib) == "ArraysMultiDim")) {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,68,1921,51, this);
										egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestArrayMul);
									}finally{egl.exitBlock();}
								}
								else {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,69,1976,76, this);
										if (((lib) == "ExceptionProducer")) {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,70,2006,46, this);
												egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestExp);
											}finally{egl.exitBlock();}
										}
										else {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,71,2056,67, this);
												if (((lib) == "Handlers")) {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,72,2077,46, this);
														egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTesthdl);
													}finally{egl.exitBlock();}
												}
												else {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,73,2127,65, this);
														if (((lib) == "Nulls")) {
															try{egl.enterBlock();
																egl.atLine(this.eze$$fileName,74,2145,47, this);
																egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestnull);
															}finally{egl.exitBlock();}
														}
														else {
															try{egl.enterBlock();
																egl.atLine(this.eze$$fileName,75,2196,69, this);
																if (((lib) == "Primitives")) {
																	try{egl.enterBlock();
																		egl.atLine(this.eze$$fileName,76,2219,46, this);
																		egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestpri);
																	}finally{egl.exitBlock();}
																}
																else {
																	try{egl.enterBlock();
																		egl.atLine(this.eze$$fileName,77,2269,70, this);
																		if (((lib) == "RecordsFlex")) {
																			try{egl.enterBlock();
																				egl.atLine(this.eze$$fileName,78,2293,46, this);
																				egl.checkNull(egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']).runTestMtds.appendAll(egl.checkNull(egl.common.utilities['$inst']).runTestrcd);
																			}finally{egl.exitBlock();}
																		}
																	}finally{egl.exitBlock();}
																}
															}finally{egl.exitBlock();}
														}
													}finally{egl.exitBlock();}
												}
											}finally{egl.exitBlock();}
										}
									}finally{egl.exitBlock();}
								}
							}finally{egl.exitBlock();}
						}
					}
					egl.atLine(this.eze$$fileName,62,1729,621, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"toString": function() {
				return "[clientlocal]";
			}
			,
			"eze$$getName": function() {
				return "clientlocal";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "TestListMgr", value : egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst'], type : "org.eclipse.edt.eunit.runtime.TestListMgr", jsName : "egl.org.eclipse.edt.eunit.runtime.TestListMgr['$inst']"},
				{name: "utilities", value : egl.common.utilities['$inst'], type : "common.utilities", jsName : "egl.common.utilities['$inst']"}
				];
			}
		}
	);
});
