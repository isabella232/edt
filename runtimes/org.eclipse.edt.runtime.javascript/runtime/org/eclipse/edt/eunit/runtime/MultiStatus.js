/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
	egl.defineClass('org.eclipse.edt.eunit.runtime', 'MultiStatus',
	{
		'eze$$fileName': 'org/eclipse/edt/eunit/runtime/MultiStatus.egl',
		'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.MultiStatus',
			"constructor": function() {
				new egl.org.eclipse.edt.eunit.runtime.LogResult();
				new egl.org.eclipse.edt.eunit.runtime.ConstantsLib();
				new egl.eglx.rbd.StrLib();
			}
			,
			"eze$$setEmpty": function() {
				new egl.org.eclipse.edt.eunit.runtime.LogResult();
				new egl.org.eclipse.edt.eunit.runtime.ConstantsLib();
				new egl.eglx.rbd.StrLib();
				this.testCnt = 0;
				this.expectedCnt = 0;
				this.passedCnt = 0;
				this.failedCnt = 0;
				this.errCnt = 0;
				this.badCnt = 0;
				this.notRunCnt = 0;
				this.firstFailedTestName = "";
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
			"eze$$clone": function() {
				var ezert$$1 = this;
				var ezert$$2 = new egl.org.eclipse.edt.eunit.runtime.MultiStatus();
				ezert$$2.testCnt = ezert$$1.testCnt;
				ezert$$2.expectedCnt = ezert$$1.expectedCnt;
				ezert$$2.passedCnt = ezert$$1.passedCnt;
				ezert$$2.failedCnt = ezert$$1.failedCnt;
				ezert$$2.errCnt = ezert$$1.errCnt;
				ezert$$2.badCnt = ezert$$1.badCnt;
				ezert$$2.notRunCnt = ezert$$1.notRunCnt;
				ezert$$2.firstFailedTestName = ezert$$1.firstFailedTestName === null ? null : ezert$$1.firstFailedTestName;
				return ezert$$2;
			}
			,
			"eze$$getAnnotations": function() {
				if(this.annotations === undefined){
					this.annotations = {};
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("MultiStatus", null, false);
				}
				return this.annotations;
			}
			,
			"eze$$getFieldInfos": function() {
				if(this.fieldInfos === undefined){
					var annotations;
					this.fieldInfos = new Array();
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("testCnt", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("testCnt");
					this.fieldInfos[0] =new egl.eglx.services.FieldInfo("testCnt", "testCnt", "I;", Number, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("expectedCnt", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("expectedCnt");
					this.fieldInfos[1] =new egl.eglx.services.FieldInfo("expectedCnt", "expectedCnt", "I;", Number, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("passedCnt", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("passedCnt");
					this.fieldInfos[2] =new egl.eglx.services.FieldInfo("passedCnt", "passedCnt", "I;", Number, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("failedCnt", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("failedCnt");
					this.fieldInfos[3] =new egl.eglx.services.FieldInfo("failedCnt", "failedCnt", "I;", Number, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("errCnt", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("errCnt");
					this.fieldInfos[4] =new egl.eglx.services.FieldInfo("errCnt", "errCnt", "I;", Number, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("badCnt", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("badCnt");
					this.fieldInfos[5] =new egl.eglx.services.FieldInfo("badCnt", "badCnt", "I;", Number, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("notRunCnt", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("notRunCnt");
					this.fieldInfos[6] =new egl.eglx.services.FieldInfo("notRunCnt", "notRunCnt", "I;", Number, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("firstFailedTestName", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("firstFailedTestName");
					this.fieldInfos[7] =new egl.eglx.services.FieldInfo("firstFailedTestName", "firstFailedTestName", "S;", String, annotations);
				}
				return this.fieldInfos;
			}
			,
			"addStatus": function(testId) {
				try { egl.enter("addStatus",this,arguments);
					egl.addLocalFunctionVariable("testId", testId, "eglx.lang.EString", "testId");
					var s = null;
					egl.addLocalFunctionVariable("s", s, "org.eclipse.edt.eunit.runtime.Status", "s");
					egl.atLine(this.eze$$fileName,33,979,21, this);
					s = egl.eglx.lang.AnyValue.ezeCopyTo(egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].getStatus(), s);
					egl.setLocalFunctionVariable("s", s, "org.eclipse.edt.eunit.runtime.Status");
					var msg;
					egl.addLocalFunctionVariable("msg", msg, "eglx.lang.EString", "msg");
					egl.atLine(this.eze$$fileName,34,1017,24, this);
					msg = ((((testId) + ": ")) + s.reason);
					egl.setLocalFunctionVariable("msg", msg, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,35,1045,25, this);
					egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].logStdOut(msg);
					egl.atLine(this.eze$$fileName,37,1078,13, this);
					this.testCnt = ((this.testCnt + 1));
					{
						egl.atLine(this.eze$$fileName,39,1097,255, this);
						egl.atLine(this.eze$$fileName,39,1097,255, this);
						EzeLabel_eze_CaseLabel_0: if (((s.code == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SPASSED))) {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,41,1145,15, this);
								this.passedCnt = ((this.passedCnt + 1));
							}finally{egl.exitBlock();}
						}
						else {
							try{egl.enterBlock();
								egl.atLine(this.eze$$fileName,42,1164,47, this);
								if (((s.code == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SFAILED))) {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,43,1196,15, this);
										this.failedCnt = ((this.failedCnt + 1));
									}finally{egl.exitBlock();}
								}
								else {
									try{egl.enterBlock();
										egl.atLine(this.eze$$fileName,44,1215,43, this);
										if (((s.code == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SERROR))) {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,45,1246,12, this);
												this.errCnt = ((this.errCnt + 1));
											}finally{egl.exitBlock();}
										}
										else {
											try{egl.enterBlock();
												egl.atLine(this.eze$$fileName,46,1262,48, this);
												if (((s.code == egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SNOT_RUN))) {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,47,1295,15, this);
														this.notRunCnt = ((this.notRunCnt + 1));
													}finally{egl.exitBlock();}
												}
												else {
													try{egl.enterBlock();
														egl.atLine(this.eze$$fileName,49,1330,12, this);
														this.badCnt = ((this.badCnt + 1));
													}finally{egl.exitBlock();}
												}
											}finally{egl.exitBlock();}
										}
									}finally{egl.exitBlock();}
								}
							}finally{egl.exitBlock();}
						}
					}
					var eze_compound_1;
					egl.atLine(this.eze$$fileName,52,1361,79, this);
					eze_compound_1 = ((s.code != egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SPASSED));
					egl.atLine(this.eze$$fileName,52,1361,79, this);
					if (eze_compound_1) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,52,1361,79, this);
							eze_compound_1 = ((egl.eglx.rbd.StrLib['$inst'].characterLen(this.firstFailedTestName) == 0));
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,52,1358,122, this);
					if (eze_compound_1) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,53,1445,29, this);
							this.firstFailedTestName = testId;
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,31,902,585, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"toString": function() {
				return "[MultiStatus]";
			}
			,
			"eze$$getName": function() {
				return "MultiStatus";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "LogResult", value : egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'], type : "org.eclipse.edt.eunit.runtime.LogResult", jsName : "egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']"},
				{name: "ConstantsLib", value : egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'], type : "org.eclipse.edt.eunit.runtime.ConstantsLib", jsName : "egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']"},
				{name: "StrLib", value : egl.eglx.rbd.StrLib['$inst'], type : "eglx.rbd.StrLib", jsName : "egl.eglx.rbd.StrLib['$inst']"},
				{name: "testCnt", value : eze$$parent.testCnt, type : "eglx.lang.EInt", jsName : "testCnt"},
				{name: "expectedCnt", value : eze$$parent.expectedCnt, type : "eglx.lang.EInt", jsName : "expectedCnt"},
				{name: "passedCnt", value : eze$$parent.passedCnt, type : "eglx.lang.EInt", jsName : "passedCnt"},
				{name: "failedCnt", value : eze$$parent.failedCnt, type : "eglx.lang.EInt", jsName : "failedCnt"},
				{name: "errCnt", value : eze$$parent.errCnt, type : "eglx.lang.EInt", jsName : "errCnt"},
				{name: "badCnt", value : eze$$parent.badCnt, type : "eglx.lang.EInt", jsName : "badCnt"},
				{name: "notRunCnt", value : eze$$parent.notRunCnt, type : "eglx.lang.EInt", jsName : "notRunCnt"},
				{name: "firstFailedTestName", value : eze$$parent.firstFailedTestName, type : "eglx.lang.EString", jsName : "firstFailedTestName"}
				];
			}
		}
	);
