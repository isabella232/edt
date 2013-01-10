define(["org/eclipse/edt/eunit/runtime/LogResult"],function(){
	if (egl.eze$$userLibs) egl.eze$$userLibs.push('common.resultLib');
	else egl.eze$$userLibs = ['common.resultLib'];
	egl.defineRUILibrary('common', 'resultLib',
	{
		'eze$$fileName': 'common/resultLib.egl',
		'eze$$runtimePropertiesFile': 'common.resultLib',
			"constructor": function() {
				if(egl.common.resultLib['$inst']) return egl.common.resultLib['$inst'];
				egl.common.resultLib['$inst']=this;
				new egl.org.eclipse.edt.eunit.runtime.LogResult();
				this.ACTUALHEADER = "";
				egl.atLine(this.eze$$fileName,6,90,17, this);
				this.ACTUALHEADER = "Actual value = ";
				this.EXPECTEDHEADER = "";
				egl.atLine(this.eze$$fileName,7,151,19, this);
				this.EXPECTEDHEADER = "Expected value = ";
				this.ACTUALSIZEHEADER = "";
				egl.atLine(this.eze$$fileName,8,216,22, this);
				this.ACTUALSIZEHEADER = "Actual array size = ";
				this.EXPECTEDSIZEHEADER = "";
				egl.atLine(this.eze$$fileName,9,286,24, this);
				this.EXPECTEDSIZEHEADER = "Exepcted array size = ";
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
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("resultLib", null, false);
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
			"assertBigIntEqual": function(message, expected, actual) {
				try { egl.enter("assertBigIntEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EBigint", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EBigint", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,12,419,20, this);
					isEqual = (egl.eglx.lang.EInt64.equals(expected,actual));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,13,443,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"B;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"B;"), isEqual);
					egl.atLine(this.eze$$fileName,11,315,186, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertBigIntEqual1": function(expected, actual) {
				try { egl.enter("assertBigIntEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EBigint", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EBigint", "actual");
					egl.atLine(this.eze$$fileName,17,573,40, this);
					this.assertBigIntEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,16,505,113, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertStringEqual": function(message, expected, actual) {
				try { egl.enter("assertStringEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EString", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EString", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,21,728,20, this);
					isEqual = ((expected) == actual);
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,22,752,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"S;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"S;"), isEqual);
					egl.atLine(this.eze$$fileName,20,624,186, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertStringEqual1": function(expected, actual) {
				try { egl.enter("assertStringEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EString", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EString", "actual");
					egl.atLine(this.eze$$fileName,26,882,40, this);
					this.assertStringEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,25,814,113, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertStringArrayEqual": function(message, expected, actual) {
				try { egl.enter("assertStringArrayEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EList<eglx.lang.EString>", "!expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EList<eglx.lang.EString>", "!actual");
					var isArrayEqual;
					egl.addLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean", "isArrayEqual");
					egl.atLine(this.eze$$fileName,30,1051,4, this);
					isArrayEqual = true;
					egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
					var expectedSize;
					egl.addLocalFunctionVariable("expectedSize", expectedSize, "eglx.lang.EInt", "expectedSize");
					egl.atLine(this.eze$$fileName,31,1078,18, this);
					expectedSize = expected.getSize();
					egl.setLocalFunctionVariable("expectedSize", expectedSize, "eglx.lang.EInt");
					var actualSize;
					egl.addLocalFunctionVariable("actualSize", actualSize, "eglx.lang.EInt", "actualSize");
					egl.atLine(this.eze$$fileName,32,1119,16, this);
					actualSize = actual.getSize();
					egl.setLocalFunctionVariable("actualSize", actualSize, "eglx.lang.EInt");
					var failedReason = "";
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.atLine(this.eze$$fileName,35,1163,877, this);
					if (((expectedSize == actualSize))) {
						try{egl.enterBlock();
							var failedHeader;
							egl.addLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString", "failedHeader");
							egl.atLine(this.eze$$fileName,36,1219,20, this);
							failedHeader = "Array element No.[";
							egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
							var expectedValues;
							egl.addLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString", "expectedValues");
							egl.atLine(this.eze$$fileName,37,1268,20, this);
							expectedValues = ((this.EXPECTEDHEADER) + "[");
							egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
							var actualValues;
							egl.addLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString", "actualValues");
							egl.atLine(this.eze$$fileName,38,1315,18, this);
							actualValues = ((this.ACTUALHEADER) + "[");
							egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,39,1338,380, this);
							{
								try{egl.enterBlock();
									var i = 0;
									egl.addLocalFunctionVariable("i", i, "eglx.lang.EInt", "i");
									for (i = 1; ((i <= expectedSize)); i = ((i + 1))) {
										egl.setLocalFunctionVariable("i", i, "eglx.lang.EInt");
										try{egl.enterBlock();
											egl.atLine(this.eze$$fileName,40,1376,171, this);
											if (((egl.checkNull(expected, "expected")[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]) != egl.checkNull(actual, "actual")[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)])) {
												try{egl.enterBlock();
													egl.atLine(this.eze$$fileName,41,1410,54, this);
													if (!(isArrayEqual)) {
														try{egl.enterBlock();
															egl.atLine(this.eze$$fileName,42,1434,21, this);
															failedHeader = ((failedHeader) + ", ");
															egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
														}finally{egl.exitBlock();}
													}
													egl.atLine(this.eze$$fileName,44,1471,21, this);
													isArrayEqual = false;
													egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
													egl.atLine(this.eze$$fileName,45,1503,28, this);
													failedHeader = ((failedHeader) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32,i));
													egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
												}finally{egl.exitBlock();}
											}
											egl.atLine(this.eze$$fileName,48,1560,30, this);
											expectedValues = ((expectedValues) + egl.checkNull(expected, "expected")[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
											egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
											egl.atLine(this.eze$$fileName,49,1595,26, this);
											actualValues = ((actualValues) + egl.checkNull(actual, "actual")[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
											egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
											egl.atLine(this.eze$$fileName,50,1626,85, this);
											if (((i != expectedSize))) {
												try{egl.enterBlock();
													egl.atLine(this.eze$$fileName,51,1653,23, this);
													expectedValues = ((expectedValues) + ", ");
													egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
													egl.atLine(this.eze$$fileName,52,1682,21, this);
													actualValues = ((actualValues) + ", ");
													egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
												}finally{egl.exitBlock();}
											}
										}finally{egl.exitBlock();}
										egl.atLine(this.eze$$fileName,39,1338,380, this);
									}
								}
								finally{egl.exitBlock();}
							}
							egl.atLine(this.eze$$fileName,55,1722,30, this);
							failedHeader = ((failedHeader) + "] differs; ");
							egl.setLocalFunctionVariable("failedHeader", failedHeader, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,56,1756,24, this);
							expectedValues = ((expectedValues) + "]; ");
							egl.setLocalFunctionVariable("expectedValues", expectedValues, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,57,1784,21, this);
							actualValues = ((actualValues) + "] ");
							egl.setLocalFunctionVariable("actualValues", actualValues, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,58,1809,60, this);
							failedReason = ((((failedHeader) + expectedValues)) + actualValues);
							egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,60,1880,21, this);
							isArrayEqual = false;
							egl.setLocalFunctionVariable("isArrayEqual", isArrayEqual, "eglx.lang.EBoolean");
							egl.atLine(this.eze$$fileName,61,1906,120, this);
							failedReason = (((((((((((((((("Failed: ") + this.EXPECTEDSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32,expectedSize))) + "' ")) + this.ACTUALSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32,actualSize))) + "' ");
							egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,63,2043,93, this);
					if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,63,2079,1, this);
							;
							egl.atLine(this.eze$$fileName,64,2084,46, this);
							failedReason = ((((message) + " - ")) + failedReason);
							egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,66,2139,39, this);
					this.assertTrue(failedReason, isArrayEqual);
					egl.atLine(this.eze$$fileName,29,933,1250, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertStringArrayEqual1": function(expected, actual) {
				try { egl.enter("assertStringArrayEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EList<eglx.lang.EString>", "!expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EList<eglx.lang.EString>", "!actual");
					egl.atLine(this.eze$$fileName,70,2265,45, this);
					this.assertStringArrayEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,69,2188,127, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertDateEqual": function(message, expected, actual) {
				try { egl.enter("assertDateEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EDate", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EDate", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,74,2418,20, this);
					isEqual = (egl.eglx.lang.EDate.equals(expected, actual));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,75,2442,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"K;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"K;"), isEqual);
					egl.atLine(this.eze$$fileName,73,2320,180, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertDateEqual1": function(expected, actual) {
				try { egl.enter("assertDateEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EDate", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EDate", "actual");
					egl.atLine(this.eze$$fileName,79,2566,38, this);
					this.assertDateEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,78,2504,105, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTimestampEqual": function(message, expected, actual) {
				try { egl.enter("assertTimestampEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ETimestamp", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ETimestamp", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,83,2727,20, this);
					isEqual = (egl.eglx.lang.ETimestamp.equals(expected, actual));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,84,2751,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"yyyyMMddhhmmss","J;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"yyyyMMddhhmmss","J;"), isEqual);
					egl.atLine(this.eze$$fileName,82,2614,197, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTimestampEqual1": function(expected, actual) {
				try { egl.enter("assertTimestampEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ETimestamp", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ETimestamp", "actual");
					egl.atLine(this.eze$$fileName,88,2892,43, this);
					this.assertTimestampEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,87,2815,125, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertDecimalEqual": function(message, expected, actual) {
				try { egl.enter("assertDecimalEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EDecimal", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EDecimal", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,92,3053,20, this);
					isEqual = (egl.eglx.lang.EDecimal.equals(expected,actual));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,93,3077,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"d;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"d;"), isEqual);
					egl.atLine(this.eze$$fileName,91,2946,193, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertDecimalEqual1": function(expected, actual) {
				try { egl.enter("assertDecimalEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EDecimal", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EDecimal", "actual");
					egl.atLine(this.eze$$fileName,97,3214,41, this);
					this.assertDecimalEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,96,3143,117, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertFloatEqual": function(message, expected, actual) {
				try { egl.enter("assertFloatEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EFloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EFloat", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,101,3366,30, this);
					isEqual = this.isFloatEqual(expected, actual);
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,102,3400,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"F;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"F;"), isEqual);
					egl.atLine(this.eze$$fileName,100,3265,193, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertFloatEqual1": function(expected, actual) {
				try { egl.enter("assertFloatEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EFloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EFloat", "actual");
					egl.atLine(this.eze$$fileName,106,3527,39, this);
					this.assertFloatEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,105,3462,109, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"isFloatEqual": function(expected, actual) {
				try { egl.enter("isFloatEqual",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EFloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EFloat", "actual");
					var normalExpected = 0;
					egl.addLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.EFloat", "normalExpected");
					var normalActual = 0;
					egl.addLocalFunctionVariable("normalActual", normalActual, "eglx.lang.EFloat", "normalActual");
					var delta = 0;
					egl.addLocalFunctionVariable("delta", delta, "eglx.lang.EFloat", "delta");
					var mantissaExpected = 0;
					egl.addLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt", "mantissaExpected");
					var mantissaActual = 0;
					egl.addLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt", "mantissaActual");
					var signExpected = "";
					egl.addLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString", "signExpected");
					var signActual = "";
					egl.addLocalFunctionVariable("signActual", signActual, "eglx.lang.EString", "signActual");
					var deltaLimit;
					egl.addLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat", "deltaLimit");
					egl.atLine(this.eze$$fileName,114,3826,5, this);
					deltaLimit = 1E-14;
					egl.setLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat");
					var eze$Temp7 = 0;
					var eze$Temp8;
					egl.atLine(this.eze$$fileName,115,3852,53, this);
					eze$Temp8 = egl.eglx.lang.EAny.ezeWrap(eze$Temp7);
					var eze$Temp9 = "";
					var eze$Temp10;
					egl.atLine(this.eze$$fileName,115,3852,53, this);
					eze$Temp10 = egl.eglx.lang.EAny.ezeWrap(eze$Temp9);
					egl.atLine(this.eze$$fileName,115,3835,71, this);
					normalExpected = this.normalFloat(expected, eze$Temp8, eze$Temp10);
					egl.setLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.EFloat");
					egl.atLine(this.eze$$fileName,115,3852,53, this);
					mantissaExpected = eze$Temp8.ezeUnbox();
					egl.setLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,115,3852,53, this);
					signExpected = eze$Temp10.ezeUnbox();
					egl.setLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString");
					var eze$Temp11 = 0;
					var eze$Temp12;
					egl.atLine(this.eze$$fileName,116,3924,47, this);
					eze$Temp12 = egl.eglx.lang.EAny.ezeWrap(eze$Temp11);
					var eze$Temp13 = "";
					var eze$Temp14;
					egl.atLine(this.eze$$fileName,116,3924,47, this);
					eze$Temp14 = egl.eglx.lang.EAny.ezeWrap(eze$Temp13);
					egl.atLine(this.eze$$fileName,116,3909,63, this);
					normalActual = this.normalFloat(actual, eze$Temp12, eze$Temp14);
					egl.setLocalFunctionVariable("normalActual", normalActual, "eglx.lang.EFloat");
					egl.atLine(this.eze$$fileName,116,3924,47, this);
					mantissaActual = eze$Temp12.ezeUnbox();
					egl.setLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,116,3924,47, this);
					signActual = eze$Temp14.ezeUnbox();
					egl.setLocalFunctionVariable("signActual", signActual, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,117,3975,38, this);
					delta = ((normalExpected - normalActual));
					egl.setLocalFunctionVariable("delta", delta, "eglx.lang.EFloat");
					egl.atLine(this.eze$$fileName,118,4016,27, this);
					delta = egl.eglx.lang.MathLib.abs(delta);
					egl.setLocalFunctionVariable("delta", delta, "eglx.lang.EFloat");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,119,4064,103, this);
					isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,123,4177,16, this);
					if (!egl.debugg) egl.leave();
					return isEqual;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertSmallFloatEqual": function(message, expected, actual) {
				try { egl.enter("assertSmallFloatEqual",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ESmallfloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ESmallfloat", "actual");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,127,4322,35, this);
					isEqual = this.isSmallFloatEqual(expected, actual);
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,128,4361,53, this);
					this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"f;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"f;"), isEqual);
					egl.atLine(this.eze$$fileName,126,4206,213, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertSmallFloatEqual1": function(expected, actual) {
				try { egl.enter("assertSmallFloatEqual1",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ESmallfloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ESmallfloat", "actual");
					egl.atLine(this.eze$$fileName,132,4503,44, this);
					this.assertSmallFloatEqual("", expected, actual);
					egl.atLine(this.eze$$fileName,131,4423,129, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"isSmallFloatEqual": function(expected, actual) {
				try { egl.enter("isSmallFloatEqual",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.ESmallfloat", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.ESmallfloat", "actual");
					var normalExpected = 0;
					egl.addLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.ESmallfloat", "normalExpected");
					var normalActual = 0;
					egl.addLocalFunctionVariable("normalActual", normalActual, "eglx.lang.ESmallfloat", "normalActual");
					var delta = 0;
					egl.addLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat", "delta");
					var mantissaExpected = 0;
					egl.addLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt", "mantissaExpected");
					var mantissaActual = 0;
					egl.addLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt", "mantissaActual");
					var signExpected = "";
					egl.addLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString", "signExpected");
					var signActual = "";
					egl.addLocalFunctionVariable("signActual", signActual, "eglx.lang.EString", "signActual");
					var deltaLimit;
					egl.addLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat", "deltaLimit");
					egl.atLine(this.eze$$fileName,140,4827,4, this);
					deltaLimit = 1E-4;
					egl.setLocalFunctionVariable("deltaLimit", deltaLimit, "eglx.lang.EFloat");
					var eze$Temp15 = 0;
					var eze$Temp16;
					egl.atLine(this.eze$$fileName,141,4852,58, this);
					eze$Temp16 = egl.eglx.lang.EAny.ezeWrap(eze$Temp15);
					var eze$Temp17 = "";
					var eze$Temp18;
					egl.atLine(this.eze$$fileName,141,4852,58, this);
					eze$Temp18 = egl.eglx.lang.EAny.ezeWrap(eze$Temp17);
					egl.atLine(this.eze$$fileName,141,4835,76, this);
					normalExpected = this.normalSmallFloat(expected, eze$Temp16, eze$Temp18);
					egl.setLocalFunctionVariable("normalExpected", normalExpected, "eglx.lang.ESmallfloat");
					egl.atLine(this.eze$$fileName,141,4852,58, this);
					mantissaExpected = eze$Temp16.ezeUnbox();
					egl.setLocalFunctionVariable("mantissaExpected", mantissaExpected, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,141,4852,58, this);
					signExpected = eze$Temp18.ezeUnbox();
					egl.setLocalFunctionVariable("signExpected", signExpected, "eglx.lang.EString");
					var eze$Temp19 = 0;
					var eze$Temp20;
					egl.atLine(this.eze$$fileName,142,4929,52, this);
					eze$Temp20 = egl.eglx.lang.EAny.ezeWrap(eze$Temp19);
					var eze$Temp21 = "";
					var eze$Temp22;
					egl.atLine(this.eze$$fileName,142,4929,52, this);
					eze$Temp22 = egl.eglx.lang.EAny.ezeWrap(eze$Temp21);
					egl.atLine(this.eze$$fileName,142,4914,68, this);
					normalActual = this.normalSmallFloat(actual, eze$Temp20, eze$Temp22);
					egl.setLocalFunctionVariable("normalActual", normalActual, "eglx.lang.ESmallfloat");
					egl.atLine(this.eze$$fileName,142,4929,52, this);
					mantissaActual = eze$Temp20.ezeUnbox();
					egl.setLocalFunctionVariable("mantissaActual", mantissaActual, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,142,4929,52, this);
					signActual = eze$Temp22.ezeUnbox();
					egl.setLocalFunctionVariable("signActual", signActual, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,143,4985,38, this);
					delta = ((normalExpected - normalActual));
					egl.setLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat");
					egl.atLine(this.eze$$fileName,144,5026,27, this);
					delta = egl.eglx.lang.MathLib.abs(delta);
					egl.setLocalFunctionVariable("delta", delta, "eglx.lang.ESmallfloat");
					var isEqual;
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					egl.atLine(this.eze$$fileName,145,5074,103, this);
					isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
					egl.setLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean");
					egl.atLine(this.eze$$fileName,149,5187,16, this);
					if (!egl.debugg) egl.leave();
					return isEqual;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"expectAssertTrue": function(message, expected, actual, isEqual) {
				try { egl.enter("expectAssertTrue",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EAny", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EAny", "actual");
					egl.addLocalFunctionVariable("isEqual", isEqual, "eglx.lang.EBoolean", "isEqual");
					var failedReason;
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.atLine(this.eze$$fileName,152,5343,44, this);
					failedReason = this.buildFailedReason(message, expected, actual);
					egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,153,5391,34, this);
					this.assertTrue(failedReason, isEqual);
					egl.atLine(this.eze$$fileName,151,5214,218, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTrue": function(failedReason, testCondition) {
				try { egl.enter("assertTrue",this,arguments);
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.addLocalFunctionVariable("testCondition", testCondition, "eglx.lang.EBoolean", "testCondition");
					egl.atLine(this.eze$$fileName,156,5506,55, this);
					this.assertTrueException(failedReason, testCondition, true);
					egl.atLine(this.eze$$fileName,155,5434,132, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"assertTrueException": function(failedReason, testCondition, throwsFailException) {
				try { egl.enter("assertTrueException",this,arguments);
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.addLocalFunctionVariable("testCondition", testCondition, "eglx.lang.EBoolean", "testCondition");
					egl.addLocalFunctionVariable("throwsFailException", throwsFailException, "eglx.lang.EBoolean", "throwsFailException");
					egl.atLine(this.eze$$fileName,159,5690,180, this);
					if (testCondition) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,160,5711,25, this);
							egl.eglx.lang.SysLib.writeStdout("OK");
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,162,5747,21, this);
							egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].failed(failedReason);
							egl.atLine(this.eze$$fileName,163,5772,92, this);
							if (throwsFailException) {
								try{egl.enterBlock();
								}finally{egl.exitBlock();}
							}
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,158,5569,306, this);
					if (!egl.debugg) egl.leave();
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"buildFailedReason": function(message, expected, actual) {
				try { egl.enter("buildFailedReason",this,arguments);
					egl.addLocalFunctionVariable("message", message, "eglx.lang.EString", "message");
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EAny", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EAny", "actual");
					var failedReason;
					egl.addLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString", "failedReason");
					egl.atLine(this.eze$$fileName,169,6005,24, this);
					failedReason = this.expect(expected, actual);
					egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,170,6033,93, this);
					if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,171,6074,46, this);
							failedReason = ((((message) + " - ")) + failedReason);
							egl.setLocalFunctionVariable("failedReason", failedReason, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,173,6129,22, this);
					if (!egl.debugg) egl.leave();
					return failedReason;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"expect": function(expected, actual) {
				try { egl.enter("expect",this,arguments);
					egl.addLocalFunctionVariable("expected", expected, "eglx.lang.EAny", "expected");
					egl.addLocalFunctionVariable("actual", actual, "eglx.lang.EAny", "actual");
					var standardMsg;
					egl.addLocalFunctionVariable("standardMsg", standardMsg, "eglx.lang.EString", "standardMsg");
					egl.atLine(this.eze$$fileName,176,6263,88, this);
					standardMsg = (((((((((((((((("Failed: ") + this.EXPECTEDHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast(expected, false))) + "' ")) + this.ACTUALHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast(actual, false))) + "' ");
					egl.setLocalFunctionVariable("standardMsg", standardMsg, "eglx.lang.EString");
					egl.atLine(this.eze$$fileName,177,6361,21, this);
					if (!egl.debugg) egl.leave();
					return standardMsg;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"normalFloat": function(afloat, mantissa, sign) {
				try { egl.enter("normalFloat",this,arguments);
					egl.addLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat", "afloat");
					egl.addLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt", "mantissa");
					egl.addLocalFunctionVariable("sign", sign, "eglx.lang.EString", "sign");
					egl.atLine(this.eze$$fileName,181,6502,13, this);
					mantissa.ezeCopy(0);
					egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,182,6524,122, this);
					if (((afloat >= 0))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,183,6552,11, this);
							sign.ezeCopy("+");
							egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,185,6589,11, this);
							sign.ezeCopy("-");
							egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,186,6613,21, this);
							afloat = ((afloat * -1));
							egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,188,6655,297, this);
					if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((afloat != 0))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,189,6701,112, this);
							while (((afloat < 1))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,190,6735,21, this);
									afloat = ((afloat * 10));
									egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
									egl.atLine(this.eze$$fileName,191,6773,24, this);
									mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
									egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
								}finally{egl.exitBlock();}
								egl.atLine(this.eze$$fileName,189,6701,112, this);
							}
							egl.atLine(this.eze$$fileName,193,6826,114, this);
							while (((afloat >= 10))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,194,6862,21, this);
									afloat = (egl.divide(afloat ,10));
									egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.EFloat");
									egl.atLine(this.eze$$fileName,195,6900,24, this);
									mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
									egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
								}finally{egl.exitBlock();}
								egl.atLine(this.eze$$fileName,193,6826,114, this);
							}
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,198,6961,15, this);
					if (!egl.debugg) egl.leave();
					return afloat;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"normalSmallFloat": function(afloat, mantissa, sign) {
				try { egl.enter("normalSmallFloat",this,arguments);
					egl.addLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat", "afloat");
					egl.addLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt", "mantissa");
					egl.addLocalFunctionVariable("sign", sign, "eglx.lang.EString", "sign");
					egl.atLine(this.eze$$fileName,202,7112,13, this);
					mantissa.ezeCopy(0);
					egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
					egl.atLine(this.eze$$fileName,203,7134,122, this);
					if (((afloat >= 0))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,204,7162,11, this);
							sign.ezeCopy("+");
							egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
						}finally{egl.exitBlock();}
					}
					else {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,206,7199,11, this);
							sign.ezeCopy("-");
							egl.setLocalFunctionVariable("sign", sign, "eglx.lang.EString");
							egl.atLine(this.eze$$fileName,207,7223,21, this);
							afloat = ((afloat * -1));
							egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,209,7265,297, this);
					if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((afloat != 0))))) {
						try{egl.enterBlock();
							egl.atLine(this.eze$$fileName,210,7311,112, this);
							while (((afloat < 1))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,211,7345,21, this);
									afloat = ((afloat * 10));
									egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
									egl.atLine(this.eze$$fileName,212,7383,24, this);
									mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
									egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
								}finally{egl.exitBlock();}
								egl.atLine(this.eze$$fileName,210,7311,112, this);
							}
							egl.atLine(this.eze$$fileName,214,7436,114, this);
							while (((afloat >= 10))) {
								try{egl.enterBlock();
									egl.atLine(this.eze$$fileName,215,7472,21, this);
									afloat = (egl.divide(afloat ,10));
									egl.setLocalFunctionVariable("afloat", afloat, "eglx.lang.ESmallfloat");
									egl.atLine(this.eze$$fileName,216,7510,24, this);
									mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
									egl.setLocalFunctionVariable("mantissa", mantissa, "eglx.lang.EInt");
								}finally{egl.exitBlock();}
								egl.atLine(this.eze$$fileName,214,7436,114, this);
							}
						}finally{egl.exitBlock();}
					}
					egl.atLine(this.eze$$fileName,219,7571,15, this);
					if (!egl.debugg) egl.leave();
					return afloat;
				} finally {
					if (!egl.debugg){
					} else { egl.leave(); }
				}
			}
			,
			"getACTUALHEADER": function() {
				return ACTUALHEADER;
			}
			,
			"getEXPECTEDHEADER": function() {
				return EXPECTEDHEADER;
			}
			,
			"getACTUALSIZEHEADER": function() {
				return ACTUALSIZEHEADER;
			}
			,
			"getEXPECTEDSIZEHEADER": function() {
				return EXPECTEDSIZEHEADER;
			}
			,
			"toString": function() {
				return "[resultLib]";
			}
			,
			"eze$$getName": function() {
				return "resultLib";
			}
			,
			"eze$$getChildVariables": function() {
				var eze$$parent = this;
				return [
				{name: "LogResult", value : egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'], type : "org.eclipse.edt.eunit.runtime.LogResult", jsName : "egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']"},
				{name: "ACTUALHEADER", value : eze$$parent.ACTUALHEADER, type : "eglx.lang.EString", jsName : "ACTUALHEADER"},
				{name: "EXPECTEDHEADER", value : eze$$parent.EXPECTEDHEADER, type : "eglx.lang.EString", jsName : "EXPECTEDHEADER"},
				{name: "ACTUALSIZEHEADER", value : eze$$parent.ACTUALSIZEHEADER, type : "eglx.lang.EString", jsName : "ACTUALSIZEHEADER"},
				{name: "EXPECTEDSIZEHEADER", value : eze$$parent.EXPECTEDSIZEHEADER, type : "eglx.lang.EString", jsName : "EXPECTEDSIZEHEADER"}
				];
			}
		}
	);
});
