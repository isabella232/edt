define(["org/eclipse/edt/eunit/runtime/LogResult"],function(){
	egl.defineRUILibrary('common', 'resultLib',
	{
		'eze$$fileName': 'common/resultLib.egl',
		'eze$$runtimePropertiesFile': 'common.resultLib',
			"constructor": function() {
				if(egl.common.resultLib['$inst']) return egl.common.resultLib['$inst'];
				egl.common.resultLib['$inst']=this;
				new egl.org.eclipse.edt.eunit.runtime.LogResult();
				this.ACTUALHEADER = "";
				this.ACTUALHEADER = "Actual value = ";
				this.EXPECTEDHEADER = "";
				this.EXPECTEDHEADER = "Expected value = ";
				this.ACTUALSIZEHEADER = "";
				this.ACTUALSIZEHEADER = "Actual array size = ";
				this.EXPECTEDSIZEHEADER = "";
				this.EXPECTEDSIZEHEADER = "Exepcted array size = ";
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
				var isEqual;
				isEqual = (egl.eglx.lang.EInt64.equals(expected,actual));
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"B;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"B;"), isEqual);
			}
			,
			"assertBigIntEqual1": function(expected, actual) {
				this.assertBigIntEqual("", expected, actual);
			}
			,
			"assertStringEqual": function(message, expected, actual) {
				var isEqual;
				isEqual = ((expected) == actual);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"S;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"S;"), isEqual);
			}
			,
			"assertStringEqual1": function(expected, actual) {
				this.assertStringEqual("", expected, actual);
			}
			,
			"assertStringArrayEqual": function(message, expected, actual) {
				var isArrayEqual;
				isArrayEqual = true;
				var expectedSize;
				expectedSize = expected.getSize();
				var actualSize;
				actualSize = actual.getSize();
				var failedReason = "";
				if (((expectedSize == actualSize))) {
					var failedHeader;
					failedHeader = "Array element No.[";
					var expectedValues;
					expectedValues = ((this.EXPECTEDHEADER) + "[");
					var actualValues;
					actualValues = ((this.ACTUALHEADER) + "[");
					{
						var i = 0;
						for (i = 1; ((i <= expectedSize)); i = ((i + 1))) {
							if (((egl.checkNull(expected)[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]) != egl.checkNull(actual)[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)])) {
								if (!(isArrayEqual)) {
									failedHeader = ((failedHeader) + ", ");
								}
								isArrayEqual = false;
								failedHeader = ((failedHeader) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32,i));
							}
							expectedValues = ((expectedValues) + egl.checkNull(expected)[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
							actualValues = ((actualValues) + egl.checkNull(actual)[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"}, false) - 1)]);
							if (((i != expectedSize))) {
								expectedValues = ((expectedValues) + ", ");
								actualValues = ((actualValues) + ", ");
							}
						}
					}
					failedHeader = ((failedHeader) + "] differs; ");
					expectedValues = ((expectedValues) + "]; ");
					actualValues = ((actualValues) + "] ");
					failedReason = ((((failedHeader) + expectedValues)) + actualValues);
				}
				else {
					isArrayEqual = false;
					failedReason = (((((((((((((((("Failed: ") + this.EXPECTEDSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32,expectedSize))) + "' ")) + this.ACTUALSIZEHEADER)) + "'")) + egl.eglx.lang.convert(egl.eglx.lang.EString.fromEInt32,actualSize))) + "' ");
				}
				if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
					;
					failedReason = ((((message) + " - ")) + failedReason);
				}
				this.assertTrue(failedReason, isArrayEqual);
			}
			,
			"assertStringArrayEqual1": function(expected, actual) {
				this.assertStringArrayEqual("", expected, actual);
			}
			,
			"assertDateEqual": function(message, expected, actual) {
				var isEqual;
				isEqual = (egl.eglx.lang.EDate.equals(expected, actual));
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"K;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"K;"), isEqual);
			}
			,
			"assertDateEqual1": function(expected, actual) {
				this.assertDateEqual("", expected, actual);
			}
			,
			"assertTimestampEqual": function(message, expected, actual) {
				var isEqual;
				isEqual = (egl.eglx.lang.ETimestamp.equals(expected, actual));
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"yyyyMMddhhmmss","J;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"yyyyMMddhhmmss","J;"), isEqual);
			}
			,
			"assertTimestampEqual1": function(expected, actual) {
				this.assertTimestampEqual("", expected, actual);
			}
			,
			"assertDecimalEqual": function(message, expected, actual) {
				var isEqual;
				isEqual = (egl.eglx.lang.EDecimal.equals(expected,actual));
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"d;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"d;"), isEqual);
			}
			,
			"assertDecimalEqual1": function(expected, actual) {
				this.assertDecimalEqual("", expected, actual);
			}
			,
			"assertFloatEqual": function(message, expected, actual) {
				var isEqual;
				isEqual = this.isFloatEqual(expected, actual);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"F;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"F;"), isEqual);
			}
			,
			"assertFloatEqual1": function(expected, actual) {
				this.assertFloatEqual("", expected, actual);
			}
			,
			"isFloatEqual": function(expected, actual) {
				var normalExpected = 0;
				var normalActual = 0;
				var delta = 0;
				var mantissaExpected = 0;
				var mantissaActual = 0;
				var signExpected = "";
				var signActual = "";
				var deltaLimit;
				deltaLimit = 1E-14;
				var eze$Temp7 = 0;
				var eze$Temp8;
				eze$Temp8 = egl.eglx.lang.EAny.ezeWrap(eze$Temp7);
				var eze$Temp9 = "";
				var eze$Temp10;
				eze$Temp10 = egl.eglx.lang.EAny.ezeWrap(eze$Temp9);
				normalExpected = this.normalFloat(expected, eze$Temp8, eze$Temp10);
				mantissaExpected = eze$Temp8.ezeUnbox();
				signExpected = eze$Temp10.ezeUnbox();
				var eze$Temp11 = 0;
				var eze$Temp12;
				eze$Temp12 = egl.eglx.lang.EAny.ezeWrap(eze$Temp11);
				var eze$Temp13 = "";
				var eze$Temp14;
				eze$Temp14 = egl.eglx.lang.EAny.ezeWrap(eze$Temp13);
				normalActual = this.normalFloat(actual, eze$Temp12, eze$Temp14);
				mantissaActual = eze$Temp12.ezeUnbox();
				signActual = eze$Temp14.ezeUnbox();
				delta = ((normalExpected - normalActual));
				delta = egl.eglx.lang.MathLib.abs(delta);
				var isEqual;
				isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
				return isEqual;
			}
			,
			"assertSmallFloatEqual": function(message, expected, actual) {
				var isEqual;
				isEqual = this.isSmallFloatEqual(expected, actual);
				this.expectAssertTrue(message, egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,expected,"f;"), egl.eglx.lang.convert(egl.eglx.lang.EAny.fromEAny,actual,"f;"), isEqual);
			}
			,
			"assertSmallFloatEqual1": function(expected, actual) {
				this.assertSmallFloatEqual("", expected, actual);
			}
			,
			"isSmallFloatEqual": function(expected, actual) {
				var normalExpected = 0;
				var normalActual = 0;
				var delta = 0;
				var mantissaExpected = 0;
				var mantissaActual = 0;
				var signExpected = "";
				var signActual = "";
				var deltaLimit;
				deltaLimit = 1E-4;
				var eze$Temp15 = 0;
				var eze$Temp16;
				eze$Temp16 = egl.eglx.lang.EAny.ezeWrap(eze$Temp15);
				var eze$Temp17 = "";
				var eze$Temp18;
				eze$Temp18 = egl.eglx.lang.EAny.ezeWrap(eze$Temp17);
				normalExpected = this.normalSmallFloat(expected, eze$Temp16, eze$Temp18);
				mantissaExpected = eze$Temp16.ezeUnbox();
				signExpected = eze$Temp18.ezeUnbox();
				var eze$Temp19 = 0;
				var eze$Temp20;
				eze$Temp20 = egl.eglx.lang.EAny.ezeWrap(eze$Temp19);
				var eze$Temp21 = "";
				var eze$Temp22;
				eze$Temp22 = egl.eglx.lang.EAny.ezeWrap(eze$Temp21);
				normalActual = this.normalSmallFloat(actual, eze$Temp20, eze$Temp22);
				mantissaActual = eze$Temp20.ezeUnbox();
				signActual = eze$Temp22.ezeUnbox();
				delta = ((normalExpected - normalActual));
				delta = egl.eglx.lang.MathLib.abs(delta);
				var isEqual;
				isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta < deltaLimit))));
				return isEqual;
			}
			,
			"expectAssertTrue": function(message, expected, actual, isEqual) {
				var failedReason;
				failedReason = this.buildFailedReason(message, expected, actual);
				this.assertTrue(failedReason, isEqual);
			}
			,
			"assertTrue": function(failedReason, testCondition) {
				this.assertTrueException(failedReason, testCondition, true);
			}
			,
			"assertTrueException": function(failedReason, testCondition, throwsFailException) {
				if (testCondition) {
					egl.eglx.lang.SysLib.writeStdout("OK");
				}
				else {
					egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'].failed(failedReason);
					if (throwsFailException) {
					}
				}
			}
			,
			"buildFailedReason": function(message, expected, actual) {
				var failedReason;
				failedReason = this.expect(expected, actual);
				if ((((egl.eglx.lang.NullType.notEquals(message, null)) && ((message) != "")))) {
					failedReason = ((((message) + " - ")) + failedReason);
				}
				return failedReason;
			}
			,
			"expect": function(expected, actual) {
				var standardMsg;
				standardMsg = (((((((((((((((("Failed: ") + this.EXPECTEDHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast(expected, false))) + "' ")) + this.ACTUALHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast(actual, false))) + "' ");
				return standardMsg;
			}
			,
			"normalFloat": function(afloat, mantissa, sign) {
				mantissa.ezeCopy(0);
				if (((afloat >= 0))) {
					sign.ezeCopy("+");
				}
				else {
					sign.ezeCopy("-");
					afloat = ((afloat * -1));
				}
				if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((afloat != 0))))) {
					while (((afloat < 1))) {
						afloat = ((afloat * 10));
						mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
					}
					while (((afloat >= 10))) {
						afloat = (egl.divide(afloat ,10));
						mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
					}
				}
				return afloat;
			}
			,
			"normalSmallFloat": function(afloat, mantissa, sign) {
				mantissa.ezeCopy(0);
				if (((afloat >= 0))) {
					sign.ezeCopy("+");
				}
				else {
					sign.ezeCopy("-");
					afloat = ((afloat * -1));
				}
				if ((((egl.eglx.lang.NullType.notEquals(afloat, null)) && ((afloat != 0))))) {
					while (((afloat < 1))) {
						afloat = ((afloat * 10));
						mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
					}
					while (((afloat >= 10))) {
						afloat = (egl.divide(afloat ,10));
						mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
					}
				}
				return afloat;
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
		}
	);
});
