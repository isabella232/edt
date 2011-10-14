egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'LogResult',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/LogResult.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.LogResult',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']) return egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'];
			egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']=this;
			new egl.org.eclipse.edt.eunit.runtime.ConstantsLib();
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
			this.outR = new egl.org.eclipse.edt.eunit.runtime.Log();
			this.s = new egl.org.eclipse.edt.eunit.runtime.Status();
		}
		,
		"eze$$setInitial": function() {
			this.eze$$setEmpty();
		}
		,
		"eze$$getAnnotations": function() {
			if(this.annotations === undefined){
				this.annotations = {};
				this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("LogResult", null, false);
			}
			return this.annotations;
		}
		,
		"eze$$getFieldInfos": function() {
			if(this.fieldInfos === undefined){
				var annotations;
				this.fieldInfos = new Array();
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("outR", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("outR");
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("outR", "outR", "Torg/eclipse/edt/eunit/runtime/log;", egl.org.eclipse.edt.eunit.runtime.Log, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("s", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("s");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("s", "s", "Torg/eclipse/edt/eunit/runtime/status;", egl.org.eclipse.edt.eunit.runtime.Status, annotations);
			}
			return this.fieldInfos;
		}
		,
		"clearResults": function() {
			this.outR.msg = "";
			this.s.code = -1;
			this.s.reason = "";
		}
		,
		"getStatus": function() {
			return this.s;
		}
		,
		"getLog": function() {
			return this.outR;
		}
		,
		"logStdOut": function(logmsg) {
			this.outR.msg = ((this.outR.msg) + egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).NEWLINE);
			this.outR.msg = ((this.outR.msg) + logmsg);
		}
		,
		"passed": function(str) {
			this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SPASSED;
			if ((((egl.eglx.lang.NullType.equals({eze$$value : egl.eglx.lang.EAny.unbox(str), eze$$signature : "S;"}, null)) || ((egl.eglx.lang.EAny.unbox(str)) == "")))) {
				str.ezeCopy("OK");
			}
			this.s.reason = egl.eglx.lang.EAny.unbox(str);
		}
		,
		"failed": function(str) {
			this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SFAILED;
			str.ezeCopy((("FAILED - ") + egl.eglx.lang.EAny.unbox(str)));
			this.s.reason = egl.eglx.lang.EAny.unbox(str);
		}
		,
		"error": function(str) {
			this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SERROR;
			str.ezeCopy((("ERROR - ") + egl.eglx.lang.EAny.unbox(str)));
			this.s.reason = egl.eglx.lang.EAny.unbox(str);
		}
		,
		"skipped": function(str) {
			this.s.code = egl.checkNull(egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst']).SNOT_RUN;
			str.ezeCopy((("SKIPPED - ") + egl.eglx.lang.EAny.unbox(str)));
			this.s.reason = egl.eglx.lang.EAny.unbox(str);
		}
		,
		"assertTrueException": function(failedReason, testCondition, throwsFailException) {
			if (testCondition) {
				var eze$Temp3 = null;
				eze$Temp3 = egl.eglx.lang.EAny.ezeWrap("OK");
				this.passed(eze$Temp3);
			}
			else {
				var eze$Temp4 = null;
				eze$Temp4 = egl.eglx.lang.EAny.ezeWrap(failedReason);
				this.failed(eze$Temp4);
				failedReason = eze$Temp4.ezeUnbox();
				if (throwsFailException) {
					var eze$Temp6 = null;
					{
						var eze$SettingTarget1 = null;
						eze$SettingTarget1 = new egl.org.eclipse.edt.eunit.runtime.AssertionFailedException();
						egl.checkNull(eze$SettingTarget1).message = this.s.reason;
						eze$Temp6 = eze$SettingTarget1;
					}
					throw eze$Temp6;
				}
			}
		}
		,
		"assertTrue": function(failedReason, testCondition) {
			this.assertTrueException(failedReason, testCondition, true);
		}
		,
		"assertTrue1": function(testCondition) {
			this.assertTrue("", testCondition);
		}
		,
		"assertBigIntEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = ((expected.compareTo(actual) == 0));
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "B;"}, {eze$$value : actual, eze$$signature : "B;"}, isEqual);
		}
		,
		"assertBigIntEqual1": function(expected, actual) {
			this.assertBigIntEqual("", expected, actual);
		}
		,
		"assertStringEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = ((expected) == actual);
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "S;"}, {eze$$value : actual, eze$$signature : "S;"}, isEqual);
		}
		,
		"assertStringEqual1": function(expected, actual) {
			this.assertStringEqual("", expected, actual);
		}
		,
		"assertStringArrayEqual": function(message, expected, actual) {
			var isArrayEqual = false;
			isArrayEqual = true;
			var expectedSize = 0;
			expectedSize = expected.getSize();
			var actualSize = 0;
			actualSize = actual.getSize();
			var failedReason = "";
			if (((expectedSize == actualSize))) {
				var failedHeader = "";
				failedHeader = "Array element No.[";
				var expectedValues = "";
				expectedValues = ((this.EXPECTEDHEADER) + "[");
				var actualValues = "";
				actualValues = ((this.ACTUALHEADER) + "[");
				{
					var i = 0;
					for (i = 1; i <= expectedSize; i += 1) {
						if (((egl.checkNull(expected)[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"},false) - 1)]) != egl.checkNull(actual)[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"},false) - 1)])) {
							if (!(isArrayEqual)) {
								failedHeader = ((failedHeader) + ", ");
							}
							isArrayEqual = false;
							failedHeader = ((failedHeader) + egl.eglx.lang.EString.fromEInt32(i));
						}
						expectedValues = ((expectedValues) + egl.checkNull(expected)[expected.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"},false) - 1)]);
						actualValues = ((actualValues) + egl.checkNull(actual)[actual.checkIndex(egl.eglx.lang.EInt32.ezeCast({eze$$value : i, eze$$signature : "I;"},false) - 1)]);
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
				failedReason = (((((((((((((((("Failed: ") + this.EXPECTEDSIZEHEADER)) + "'")) + egl.eglx.lang.EString.fromEInt32(expectedSize))) + "' ")) + this.ACTUALSIZEHEADER)) + "'")) + egl.eglx.lang.EString.fromEInt32(actualSize))) + "' ");
			}
			if ((((egl.eglx.lang.NullType.notEquals({eze$$value : message, eze$$signature : "S;"}, null)) && ((message) != "")))) {
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
			var isEqual = false;
			isEqual = (egl.eglx.lang.EDate.equals(expected, actual));
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "K;"}, {eze$$value : actual, eze$$signature : "K;"}, isEqual);
		}
		,
		"assertDateEqual1": function(expected, actual) {
			this.assertDateEqual("", expected, actual);
		}
		,
		"assertTimestampEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = (egl.eglx.lang.ETimestamp.equals(expected, actual));
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "J;"}, {eze$$value : actual, eze$$signature : "J;"}, isEqual);
		}
		,
		"assertTimestampEqual1": function(expected, actual) {
			this.assertTimestampEqual("", expected, actual);
		}
		,
		"assertDecimalEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = ((expected.compareTo(actual) == 0));
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "d;"}, {eze$$value : actual, eze$$signature : "d;"}, isEqual);
		}
		,
		"assertDecimalEqual1": function(expected, actual) {
			this.assertDecimalEqual("", expected, actual);
		}
		,
		"assertFloatEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = this.isFloatEqual(expected, actual);
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "F;"}, {eze$$value : actual, eze$$signature : "F;"}, isEqual);
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
			var deltaLimit = 0;
			deltaLimit = 1E-14;
			var eze$Temp13 = egl.eglx.lang.EAny.ezeWrap(0);
			var eze$Temp14 = egl.eglx.lang.EAny.ezeWrap("");
			normalExpected = this.normalFloat(expected, eze$Temp13, eze$Temp14);
			mantissaExpected = eze$Temp13.ezeUnbox();
			signExpected = eze$Temp14.ezeUnbox();
			var eze$Temp15 = egl.eglx.lang.EAny.ezeWrap(0);
			var eze$Temp16 = egl.eglx.lang.EAny.ezeWrap("");
			normalActual = this.normalFloat(actual, eze$Temp15, eze$Temp16);
			mantissaActual = eze$Temp15.ezeUnbox();
			signActual = eze$Temp16.ezeUnbox();
			delta = ((normalExpected - normalActual));
			delta = egl.eglx.lang.MathLib.abs(delta);
			var isEqual = false;
			isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta.compareTo(deltaLimit) < 0))));
			return isEqual;
		}
		,
		"expectAssertTrue": function(message, expected, actual, isEqual) {
			var failedReason = "";
			failedReason = this.buildFailedReason(message, expected, actual);
			this.assertTrue(failedReason, isEqual);
		}
		,
		"buildFailedReason": function(message, expected, actual) {
			var failedReason = "";
			failedReason = this.expect(expected, actual);
			if ((((egl.eglx.lang.NullType.notEquals({eze$$value : message, eze$$signature : "S;"}, null)) && ((message) != "")))) {
				failedReason = ((((message) + " - ")) + failedReason);
			}
			return failedReason;
		}
		,
		"expect": function(expected, actual) {
			var standardMsg = "";
			standardMsg = (((((((((((((((("Failed: ") + this.EXPECTEDHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast(expected,false))) + "' ")) + this.ACTUALHEADER)) + "'")) + egl.eglx.lang.EString.ezeCast(actual,false))) + "' ");
			return standardMsg;
		}
		,
		"normalFloat": function(afloat, mantissa, sign) {
			mantissa.ezeCopy(0);
			if (((afloat.compareTo(0) >= 0))) {
				sign.ezeCopy("+");
			}
			else {
				sign.ezeCopy("-");
				afloat = ((afloat * -1));
			}
			if ((!(afloat.compareTo(0)))) {
				while (((afloat.compareTo(1) < 0))) {
					afloat = ((afloat * 10));
					mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) - 1)));
				}
				while (((afloat.compareTo(10) >= 0))) {
					afloat = (egl.divide(afloat,10));
					mantissa.ezeCopy(((egl.eglx.lang.EAny.unbox(mantissa) + 1)));
				}
			}
			return afloat;
		}
		,
		"getOutR": function() {
			return outR;
		}
		,
		"setOutR": function(ezeValue) {
			this.outR = ezeValue;
		}
		,
		"getS": function() {
			return s;
		}
		,
		"setS": function(ezeValue) {
			this.s = ezeValue;
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
			return "[LogResult]";
		}
	}
);
