egl.defineRUILibrary('org.eclipse.edt.eunit.runtime', 'LogResult',
{
	'eze$$fileName': 'org/eclipse/edt/eunit/runtime/LogResult.egl',
	'eze$$runtimePropertiesFile': 'org.eclipse.edt.eunit.runtime.LogResult',
		"constructor": function() {
			if(egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']) return egl.org.eclipse.edt.eunit.runtime.LogResult['$inst'];
			egl.org.eclipse.edt.eunit.runtime.LogResult['$inst']=this;
			this.jsrt$SysVar = new egl.egl.core.SysVar();
			new egl.org.eclipse.edt.eunit.runtime.ConstantsLib();
			this.eze$$setInitial();
		}
		,
		"eze$$setEmpty": function() {
			this.outR = new egl.org.eclipse.edt.eunit.runtime.Log();
			this.s = new egl.org.eclipse.edt.eunit.runtime.Status();
			this.ACTUALHEADER = "";
			this.EXPECTEDHEADER = "";
			this.ACTUALSIZEHEADER = "";
			this.EXPECTEDSIZEHEADER = "";
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
				this.fieldInfos[0] =new egl.eglx.services.FieldInfo("outR", "outR", "A;", egl.org.eclipse.edt.eunit.runtime.Log, annotations);
				annotations = {};
				annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("s", null, false, false);
				annotations["JsonName"] = new egl.eglx.json.JsonName("s");
				this.fieldInfos[1] =new egl.eglx.services.FieldInfo("s", "s", "A;", egl.org.eclipse.edt.eunit.runtime.Status, annotations);
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
			this.outR.msg = ((this.outR.msg) + egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].NEWLINE);
			this.outR.msg = ((this.outR.msg) + logmsg);
		}
		,
		"passed": function(str) {
			this.s.code = egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].SPASSED;
			if ((((egl.egl.lang.NullType.equals({eze$$value : str.ezeUnbox(), eze$$signature : "S;"}, null)) || ((str.ezeUnbox()) == "")))) {
				str.ezeCopy("OK");
			}
			this.s.reason = str.ezeUnbox();
		}
		,
		"failed": function(str) {
			this.s.code = egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].SFAILED;
			if ((((egl.egl.lang.NullType.equals({eze$$value : str.ezeUnbox(), eze$$signature : "S;"}, null)) || ((str.ezeUnbox()) == "")))) {
				str.ezeCopy("FAILED");
			}
			this.s.reason = str.ezeUnbox();
		}
		,
		"error": function(str) {
			this.s.code = egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].SERROR;
			if ((((egl.egl.lang.NullType.equals({eze$$value : str.ezeUnbox(), eze$$signature : "S;"}, null)) || ((str.ezeUnbox()) == "")))) {
				str.ezeCopy("ERROR");
			}
			this.s.reason = str.ezeUnbox();
		}
		,
		"skipped": function(str) {
			this.s.code = egl.org.eclipse.edt.eunit.runtime.ConstantsLib['$inst'].SNOT_RUN;
			if ((((egl.egl.lang.NullType.equals({eze$$value : str.ezeUnbox(), eze$$signature : "S;"}, null)) || ((str.ezeUnbox()) == "")))) {
				str.ezeCopy("SKIPPED");
			}
			this.s.reason = str.ezeUnbox();
		}
		,
		"assertTrue": function(failedReason, testCondition) {
			if (testCondition) {
				var eze$Temp6 = null;
				eze$Temp6 = egl.egl.lang.EglAny.ezeWrap("OK");
				this.passed(eze$Temp6);
			}
			else {
				var eze$Temp7 = null;
				eze$Temp7 = egl.egl.lang.EglAny.ezeWrap(failedReason);
				this.failed(eze$Temp7);
				failedReason = eze$Temp7.ezeUnbox();
				var eze$Temp8 = null;
				{
					var eze$SettingTarget1 = null;
					eze$SettingTarget1 = new egl.org.eclipse.edt.eunit.runtime.AssertionFailedException();
					eze$SettingTarget1.message = this.s.reason;
					eze$Temp8 = eze$SettingTarget1;
				}
				throw eze$Temp8;
			}
		}
		,
		"assertTrue1": function(testCondition) {
			var eze$Temp9 = "";
			eze$Temp9 = "";
			this.assertTrue(eze$Temp9, testCondition);
		}
		,
		"assertBigIntEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = ((expected.compareTo(actual) == 0));
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "B;"}, {eze$$value : actual, eze$$signature : "B;"}, isEqual);
		}
		,
		"assertBigIntEqual1": function(expected, actual) {
			var eze$Temp10 = "";
			eze$Temp10 = "";
			this.assertBigIntEqual(eze$Temp10, expected, actual);
		}
		,
		"assertStringEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = ((expected) == actual);
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "S;"}, {eze$$value : actual, eze$$signature : "S;"}, isEqual);
		}
		,
		"assertStringEqual1": function(expected, actual) {
			var eze$Temp11 = "";
			eze$Temp11 = "";
			this.assertStringEqual(eze$Temp11, expected, actual);
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
						if (((egl.checkNull(expected)[expected.checkIndex(i - 1)]) != egl.checkNull(actual)[actual.checkIndex(i - 1)])) {
							if (!(isArrayEqual)) {
								failedHeader = ((failedHeader) + ", ");
							}
							isArrayEqual = false;
							failedHeader = ((failedHeader) + egl.egl.lang.EString.fromEInt32(i));
						}
						expectedValues = ((expectedValues) + egl.checkNull(expected)[expected.checkIndex(i - 1)]);
						actualValues = ((actualValues) + egl.checkNull(actual)[actual.checkIndex(i - 1)]);
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
				failedReason = (((((((((((((((("Failed: ") + this.EXPECTEDSIZEHEADER)) + "'")) + egl.egl.lang.EString.fromEInt32(expectedSize))) + "' ")) + this.ACTUALSIZEHEADER)) + "'")) + egl.egl.lang.EString.fromEInt32(actualSize))) + "' ");
			}
			if ((((egl.egl.lang.NullType.notEquals({eze$$value : message, eze$$signature : "S;"}, null)) && ((message) != "")))) {
				;
				failedReason = ((((message) + " - ")) + failedReason);
			}
			this.assertTrue(failedReason, isArrayEqual);
		}
		,
		"assertStringArrayEqual1": function(expected, actual) {
			var eze$Temp18 = "";
			eze$Temp18 = "";
			this.assertStringArrayEqual(eze$Temp18, expected, actual);
		}
		,
		"assertDateEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = (egl.egl.lang.EDate.equals(ezeProgram, expected, actual));
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "K;"}, {eze$$value : actual, eze$$signature : "K;"}, isEqual);
		}
		,
		"assertDateEqual1": function(expected, actual) {
			var eze$Temp19 = "";
			eze$Temp19 = "";
			this.assertDateEqual(eze$Temp19, expected, actual);
		}
		,
		"assertTimestampEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = (egl.egl.lang.ETimestamp.equals(ezeProgram, expected, actual));
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "J'null';"}, {eze$$value : actual, eze$$signature : "J'null';"}, isEqual);
		}
		,
		"assertTimestampEqual1": function(expected, actual) {
			var eze$Temp20 = "";
			eze$Temp20 = "";
			this.assertTimestampEqual(eze$Temp20, expected, actual);
		}
		,
		"assertDecimalEqual": function(message, expected, actual) {
			var isEqual = false;
			isEqual = ((expected.compareTo(actual) == 0));
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "d;"}, {eze$$value : actual, eze$$signature : "d;"}, isEqual);
		}
		,
		"assertDecimalEqual1": function(expected, actual) {
			var eze$Temp21 = "";
			eze$Temp21 = "";
			this.assertDecimalEqual(eze$Temp21, expected, actual);
		}
		,
		"assertFloatEqual": function(message, expected, actual) {
			var normalExpected = 0;
			var normalActual = 0;
			var delta = 0;
			var mantissaExpected = 0;
			var mantissaActual = 0;
			var signExpected = "";
			var signActual = "";
			var deltaLimit = 0;
			deltaLimit = 1E-14;
			var eze$Temp22 = egl.egl.lang.EglAny.ezeWrap(0);
			var eze$Temp23 = egl.egl.lang.EglAny.ezeWrap("");
			normalExpected = this.normalFloat(expected, eze$Temp22, eze$Temp23);
			mantissaExpected = eze$Temp22.ezeUnbox();
			signExpected = eze$Temp23.ezeUnbox();
			var eze$Temp24 = egl.egl.lang.EglAny.ezeWrap(0);
			var eze$Temp25 = egl.egl.lang.EglAny.ezeWrap("");
			normalActual = this.normalFloat(actual, eze$Temp24, eze$Temp25);
			mantissaActual = eze$Temp24.ezeUnbox();
			signActual = eze$Temp25.ezeUnbox();
			delta = ((normalExpected - normalActual));
			delta = egl.eglx.lang.MathLib.abs(delta);
			var isEqual = false;
			isEqual = ((((((signExpected) == signActual) && ((mantissaExpected == mantissaActual)))) && ((delta.compareTo(deltaLimit) < 0))));
			this.expectAssertTrue(message, {eze$$value : expected, eze$$signature : "F;"}, {eze$$value : actual, eze$$signature : "F;"}, isEqual);
		}
		,
		"assertFloatEqual1": function(expected, actual) {
			var eze$Temp26 = "";
			eze$Temp26 = "";
			this.assertFloatEqual(eze$Temp26, expected, actual);
		}
		,
		"expectAssertTrue": function(message, expected, actual, isEqual) {
			var failedReason = "";
			failedReason = this.expect(expected, actual);
			if ((((egl.egl.lang.NullType.notEquals({eze$$value : message, eze$$signature : "S;"}, null)) && ((message) != "")))) {
				failedReason = ((((message) + " - ")) + failedReason);
			}
			this.assertTrue(failedReason, isEqual);
		}
		,
		"expect": function(expected, actual) {
			var standardMsg = "";
			standardMsg = (((((((((((((((("Failed: ") + this.EXPECTEDHEADER)) + "'")) + egl.egl.lang.EString.ezeCast(expected))) + "' ")) + this.ACTUALHEADER)) + "'")) + egl.egl.lang.EString.ezeCast(actual))) + "' ");
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
					mantissa.ezeCopy(((mantissa.ezeUnbox() - 1)));
				}
				while (((afloat.compareTo(10) >= 0))) {
					afloat = (egl.divide(afloat,10));
					mantissa.ezeCopy(((mantissa.ezeUnbox() + 1)));
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
		"setACTUALHEADER": function(ezeValue) {
			this.ACTUALHEADER = ezeValue;
		}
		,
		"getEXPECTEDHEADER": function() {
			return EXPECTEDHEADER;
		}
		,
		"setEXPECTEDHEADER": function(ezeValue) {
			this.EXPECTEDHEADER = ezeValue;
		}
		,
		"getACTUALSIZEHEADER": function() {
			return ACTUALSIZEHEADER;
		}
		,
		"setACTUALSIZEHEADER": function(ezeValue) {
			this.ACTUALSIZEHEADER = ezeValue;
		}
		,
		"getEXPECTEDSIZEHEADER": function() {
			return EXPECTEDSIZEHEADER;
		}
		,
		"setEXPECTEDSIZEHEADER": function(ezeValue) {
			this.EXPECTEDSIZEHEADER = ezeValue;
		}
		,
		"toString": function() {
			return "[LogResult]";
		}
	}
);
