define(["libraries/async/TestArraysMultiDim", "libraries/async/TestHandlers", "libraries/async/TestArrays", "libraries/async/TestExceptionProducer", "libraries/async/TestRecordsFlex", "libraries/async/TestNulls", "libraries/async/TestPrimitives"],function(){
	egl.defineRUILibrary('common', 'utilities',
	{
		'eze$$fileName': 'common/utilities.egl',
		'eze$$runtimePropertiesFile': 'common.utilities',
			"constructor": function() {
				if(egl.common.utilities['$inst']) return egl.common.utilities['$inst'];
				egl.common.utilities['$inst']=this;
				new egl.libraries.async.TestArraysMultiDim();
				new egl.libraries.async.TestHandlers();
				new egl.libraries.async.TestArrays();
				new egl.libraries.async.TestExceptionProducer();
				new egl.libraries.async.TestRecordsFlex();
				new egl.libraries.async.TestNulls();
				new egl.libraries.async.TestPrimitives();
				this.eze$$setInitial();
			}
			,
			"eze$$setEmpty": function() {
				this.LibraryList =  [];
				this.ArrayCase =  [];
				this.runTestArray =  [];
				this.ArrayMul =  [];
				this.runTestArrayMul =  [];
				this.Exp =  [];
				this.runTestExp =  [];
				this.hdl =  [];
				this.runTesthdl =  [];
				this.nul =  [];
				this.runTestnull =  [];
				this.pri =  [];
				this.runTestpri =  [];
				this.rcd =  [];
				this.runTestrcd =  [];
			}
			,
			"eze$$setInitial": function() {
				this.eze$$setEmpty();
				this.LibraryList.appendElement(egl.checkNull("Array"));
				this.LibraryList.appendElement(egl.checkNull("ArraysMultiDim"));
				this.LibraryList.appendElement(egl.checkNull("ExceptionProducer"));
				this.LibraryList.appendElement(egl.checkNull("Handlers"));
				this.LibraryList.appendElement(egl.checkNull("Nulls"));
				this.LibraryList.appendElement(egl.checkNull("Primitives"));
				this.LibraryList.appendElement(egl.checkNull("RecordsFlex"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleIn"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleInout"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleOut"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleAll"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleIntIn"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleIntInout"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleIntOut"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleIntAll"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleBigintIn"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleBigintInout"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleBigintOut"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleBigintAll"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleDecimalIn"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleDecimalInout"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleDecimalOut"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleDecimalAll"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleDateIn"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleDateInout"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleDateOut"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleDateAll"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleTimestampIn"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleTimestampInout"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleTimestampOut"));
				this.ArrayCase.appendElement(egl.checkNull("testSingleTimestampAll"));
				this.ArrayCase.appendElement(egl.checkNull("testSimpleRecordArrayIn"));
				this.ArrayCase.appendElement(egl.checkNull("testSimpleRecordArrayInout"));
				this.ArrayCase.appendElement(egl.checkNull("testSimpleRecordArrayOut"));
				this.ArrayCase.appendElement(egl.checkNull("testSimpleRecordArrayAll"));
				this.ArrayCase.appendElement(egl.checkNull("testArrayContainingRecordArrayIn"));
				this.ArrayCase.appendElement(egl.checkNull("testArrayContainingRecordArrayInout"));
				this.ArrayCase.appendElement(egl.checkNull("testArrayContainingRecordArrayAll"));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleIn)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleInout)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleOut)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleAll)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleIntIn)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleIntOut)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleInout)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleIntAll)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleBigintIn)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleBigintInout)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleBigintOut)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleBigintAll)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleDecimalIn)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleDecimalInout)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleDecimalOut)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleDecimalAll)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleDateIn)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleDateInout)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleDateOut)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleDateAll)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleTimestampIn)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleTimestampInout)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleTimestampOut)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSingleTimestampAll)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSimpleRecordArrayIn)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSimpleRecordArrayInout)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSimpleRecordArrayOut)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testSimpleRecordArrayAll)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testArrayContainingRecordArrayIn)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testArrayContainingRecordArrayInout)));
				this.runTestArray.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArrays['$inst'],egl.libraries.async.TestArrays.prototype.testArrayContainingRecordArrayAll)));
				this.ArrayMul.appendElement(egl.checkNull("testDoubleIn"));
				this.ArrayMul.appendElement(egl.checkNull("testDoubleInout"));
				this.ArrayMul.appendElement(egl.checkNull("testDoubleOut"));
				this.ArrayMul.appendElement(egl.checkNull("testDoubleAll"));
				this.ArrayMul.appendElement(egl.checkNull("testOuterFlexRecordMultiDimIn"));
				this.ArrayMul.appendElement(egl.checkNull("testOuterFlexRecordMultiDimInout"));
				this.ArrayMul.appendElement(egl.checkNull("testOuterFlexRecordMultiDimOut"));
				this.ArrayMul.appendElement(egl.checkNull("testOuterFlexRecordMultiDimAll"));
				this.runTestArrayMul.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArraysMultiDim['$inst'],egl.libraries.async.TestArraysMultiDim.prototype.testDoubleIn)));
				this.runTestArrayMul.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArraysMultiDim['$inst'],egl.libraries.async.TestArraysMultiDim.prototype.testDoubleInout)));
				this.runTestArrayMul.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArraysMultiDim['$inst'],egl.libraries.async.TestArraysMultiDim.prototype.testDoubleOut)));
				this.runTestArrayMul.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArraysMultiDim['$inst'],egl.libraries.async.TestArraysMultiDim.prototype.testDoubleAll)));
				this.runTestArrayMul.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArraysMultiDim['$inst'],egl.libraries.async.TestArraysMultiDim.prototype.testOuterFlexRecordMultiDimIn)));
				this.runTestArrayMul.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArraysMultiDim['$inst'],egl.libraries.async.TestArraysMultiDim.prototype.testOuterFlexRecordMultiDimInout)));
				this.runTestArrayMul.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArraysMultiDim['$inst'],egl.libraries.async.TestArraysMultiDim.prototype.testOuterFlexRecordMultiDimOut)));
				this.runTestArrayMul.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestArraysMultiDim['$inst'],egl.libraries.async.TestArraysMultiDim.prototype.testOuterFlexRecordMultiDimAll)));
				this.Exp.appendElement(egl.checkNull("testExcpetionProducer"));
				this.runTestExp.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestExceptionProducer['$inst'],egl.libraries.async.TestExceptionProducer.prototype.testExcpetionProducer)));
				this.hdl.appendElement(egl.checkNull("testBigint_all"));
				this.hdl.appendElement(egl.checkNull("testBigintArray_all"));
				this.hdl.appendElement(egl.checkNull("testInt_all"));
				this.hdl.appendElement(egl.checkNull("testIntArray_all"));
				this.hdl.appendElement(egl.checkNull("testSmallint_all"));
				this.hdl.appendElement(egl.checkNull("testSmallintArray_all"));
				this.hdl.appendElement(egl.checkNull("testSmallfloat_all"));
				this.hdl.appendElement(egl.checkNull("testSmallfloatArray_all"));
				this.hdl.appendElement(egl.checkNull("testFloat_all"));
				this.hdl.appendElement(egl.checkNull("testFloatArray_all"));
				this.hdl.appendElement(egl.checkNull("testDecimal_all"));
				this.hdl.appendElement(egl.checkNull("testDecimalArray_all"));
				this.hdl.appendElement(egl.checkNull("testString_all"));
				this.hdl.appendElement(egl.checkNull("testStringArray_all"));
				this.hdl.appendElement(egl.checkNull("testTimestamp_all"));
				this.hdl.appendElement(egl.checkNull("testTimestampArray_all"));
				this.hdl.appendElement(egl.checkNull("testBoolean_all"));
				this.hdl.appendElement(egl.checkNull("testBooleanArray_all"));
				this.hdl.appendElement(egl.checkNull("testDate_all"));
				this.hdl.appendElement(egl.checkNull("testDateArray_all"));
				this.hdl.appendElement(egl.checkNull("testOuterHandler_inParm"));
				this.hdl.appendElement(egl.checkNull("testOuterHandler_inoutParm"));
				this.hdl.appendElement(egl.checkNull("testOuterHandler_outParm"));
				this.hdl.appendElement(egl.checkNull("testOuterHandler_allParm"));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testBigint_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testBigintArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testInt_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testIntArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testSmallint_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testSmallintArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testSmallfloat_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testSmallfloatArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testFloat_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testFloatArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testDecimal_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testDecimalArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testString_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testStringArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testTimestamp_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testTimestampArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testBoolean_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testBooleanArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testDate_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testDateArray_all)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testOuterHandler_inParm)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testOuterHandler_inoutParm)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testOuterHandler_outParm)));
				this.runTesthdl.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestHandlers['$inst'],egl.libraries.async.TestHandlers.prototype.testOuterHandler_allParm)));
				this.nul.appendElement(egl.checkNull("testNullStringIn"));
				this.nul.appendElement(egl.checkNull("testNullStringInNull"));
				this.nul.appendElement(egl.checkNull("testNullStringInout"));
				this.nul.appendElement(egl.checkNull("testNullStringOut"));
				this.nul.appendElement(egl.checkNull("testNullStringAll"));
				this.nul.appendElement(egl.checkNull("testNullIntIn"));
				this.nul.appendElement(egl.checkNull("testNullIntInNull"));
				this.nul.appendElement(egl.checkNull("testNullIntInout"));
				this.nul.appendElement(egl.checkNull("testNullIntOut"));
				this.nul.appendElement(egl.checkNull("testNullIntAll"));
				this.nul.appendElement(egl.checkNull("testNullStringArrayIn"));
				this.nul.appendElement(egl.checkNull("testNullStringArrayInNull"));
				this.nul.appendElement(egl.checkNull("testNullStringArrayInout"));
				this.nul.appendElement(egl.checkNull("testNullStringArrayOut"));
				this.nul.appendElement(egl.checkNull("testNullStringArrayAll"));
				this.nul.appendElement(egl.checkNull("testNullRecordItemIn"));
				this.nul.appendElement(egl.checkNull("testNullRecordItemInNull"));
				this.nul.appendElement(egl.checkNull("testNullRecordItemInout"));
				this.nul.appendElement(egl.checkNull("testNullRecordItemOut1"));
				this.nul.appendElement(egl.checkNull("testNullRecordItemAll"));
				this.nul.appendElement(egl.checkNull("testNullRecordIn"));
				this.nul.appendElement(egl.checkNull("testNullRecordInNull"));
				this.nul.appendElement(egl.checkNull("testNullRecordInout"));
				this.nul.appendElement(egl.checkNull("testNullRecordOut"));
				this.nul.appendElement(egl.checkNull("tsetNullRecordAll"));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringIn)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringInNull)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringInout)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringOut)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringAll)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullIntIn)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullIntInNull)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullIntInout)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullIntOut)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullIntAll)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringArrayIn)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringArrayInNull)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringArrayInout)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringArrayOut)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullStringArrayAll)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullRecordItemIn)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullRecordItemInNull)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullRecordItemInout)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullRecordItemOut1)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullRecordItemAll)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullRecordIn)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullRecordInNull)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullRecordInout)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.testNullRecordOut)));
				this.runTestnull.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestNulls['$inst'],egl.libraries.async.TestNulls.prototype.tsetNullRecordAll)));
				this.pri.appendElement(egl.checkNull("testInt_in"));
				this.pri.appendElement(egl.checkNull("testInt_inoutParm"));
				this.pri.appendElement(egl.checkNull("testInt_outParm"));
				this.pri.appendElement(egl.checkNull("testInt_allParm"));
				this.pri.appendElement(egl.checkNull("testBigint_inParm"));
				this.pri.appendElement(egl.checkNull("testBigint_inoutParm"));
				this.pri.appendElement(egl.checkNull("testBigint_outParm"));
				this.pri.appendElement(egl.checkNull("testBigint_allParm"));
				this.pri.appendElement(egl.checkNull("testSmallint_inParm"));
				this.pri.appendElement(egl.checkNull("testSmallint_inoutParm"));
				this.pri.appendElement(egl.checkNull("testSmallint_outParm"));
				this.pri.appendElement(egl.checkNull("testSmallint_allParm"));
				this.pri.appendElement(egl.checkNull("testSmallfloat_inParm"));
				this.pri.appendElement(egl.checkNull("testSmallfloat_inoutParm"));
				this.pri.appendElement(egl.checkNull("testSmallfloat_outParm"));
				this.pri.appendElement(egl.checkNull("testSmallfloat_allParm"));
				this.pri.appendElement(egl.checkNull("testFloat_inParm"));
				this.pri.appendElement(egl.checkNull("testFloat_inoutParm"));
				this.pri.appendElement(egl.checkNull("testFloat_outParm"));
				this.pri.appendElement(egl.checkNull("testFloat_allParm"));
				this.pri.appendElement(egl.checkNull("testDecimal_inParm"));
				this.pri.appendElement(egl.checkNull("testDecimal_inoutParm"));
				this.pri.appendElement(egl.checkNull("testDecimal_outParm"));
				this.pri.appendElement(egl.checkNull("testDecimal_allParm"));
				this.pri.appendElement(egl.checkNull("testString_inParm"));
				this.pri.appendElement(egl.checkNull("testString_inoutParm"));
				this.pri.appendElement(egl.checkNull("testString_outParm"));
				this.pri.appendElement(egl.checkNull("testString_allParm"));
				this.pri.appendElement(egl.checkNull("testTimestamp_inParm"));
				this.pri.appendElement(egl.checkNull("testTimestamp_inoutParm"));
				this.pri.appendElement(egl.checkNull("testTimestamp_outParm"));
				this.pri.appendElement(egl.checkNull("testTimestamp_allParm"));
				this.pri.appendElement(egl.checkNull("testBoolean_inParm"));
				this.pri.appendElement(egl.checkNull("testBoolean_inoutParm"));
				this.pri.appendElement(egl.checkNull("testBoolean_outParm"));
				this.pri.appendElement(egl.checkNull("testBoolean_allParm"));
				this.pri.appendElement(egl.checkNull("testDate_inParm"));
				this.pri.appendElement(egl.checkNull("testDate_inoutParm"));
				this.pri.appendElement(egl.checkNull("testDate_outParm"));
				this.pri.appendElement(egl.checkNull("testDate_allParm"));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testInt_in)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testInt_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testInt_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testInt_allParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testBigint_inParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testBigint_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testBigint_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testBigint_allParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testSmallint_inParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testSmallint_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testSmallint_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testSmallint_allParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testSmallfloat_inParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testSmallfloat_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testSmallfloat_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testSmallfloat_allParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testFloat_inParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testFloat_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testFloat_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testFloat_allParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testDecimal_inParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testDecimal_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testDecimal_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testDecimal_allParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testString_inParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testString_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testString_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testString_allParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testTimestamp_inParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testTimestamp_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testTimestamp_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testTimestamp_allParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testBoolean_inParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testBoolean_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testBoolean_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testBoolean_allParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testDate_inParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testDate_inoutParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testDate_outParm)));
				this.runTestpri.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestPrimitives['$inst'],egl.libraries.async.TestPrimitives.prototype.testDate_allParm)));
				this.rcd.appendElement(egl.checkNull("testBigintFlex_all"));
				this.rcd.appendElement(egl.checkNull("testBigintArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testIntFlex_all"));
				this.rcd.appendElement(egl.checkNull("testIntArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testSmallintFlex_all"));
				this.rcd.appendElement(egl.checkNull("testSmallintArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testSmallfloatFlex_all"));
				this.rcd.appendElement(egl.checkNull("testSmallfloatArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testFloatFlex_all"));
				this.rcd.appendElement(egl.checkNull("testFloatArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testDecimalFlex_all"));
				this.rcd.appendElement(egl.checkNull("testDecimalArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testStringFlex_all"));
				this.rcd.appendElement(egl.checkNull("testStringArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testTimestampFlex_all"));
				this.rcd.appendElement(egl.checkNull("testTimestampArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testBooleanFlex_all"));
				this.rcd.appendElement(egl.checkNull("testBooleanArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testDateFlex_all"));
				this.rcd.appendElement(egl.checkNull("testDateArrayFlex_all"));
				this.rcd.appendElement(egl.checkNull("testOuterFlexRecord_inParm"));
				this.rcd.appendElement(egl.checkNull("testOuterFlexRecord_inoutParm"));
				this.rcd.appendElement(egl.checkNull("testOuterFlexRecord_outParm"));
				this.rcd.appendElement(egl.checkNull("testOuterFlexRecord_allParm"));
				this.rcd.appendElement(egl.checkNull("testRatlc01399882StringRec"));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testBigintFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testBigintArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testIntFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testIntArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testSmallintFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testSmallintArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testSmallfloatFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testSmallfloatArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testFloatFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testFloatArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testDecimalFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testDecimalArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testStringFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testStringArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testTimestampFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testTimestampArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testBooleanFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testBooleanArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testDateFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testDateArrayFlex_all)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testOuterFlexRecord_inParm)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testOuterFlexRecord_inoutParm)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testOuterFlexRecord_outParm)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testOuterFlexRecord_allParm)));
				this.runTestrcd.appendElement(egl.checkNull(new egl.egl.jsrt.Delegate(egl.libraries.async.TestRecordsFlex['$inst'],egl.libraries.async.TestRecordsFlex.prototype.testRatlc01399882StringRec)));
			}
			,
			"eze$$getAnnotations": function() {
				if(this.annotations === undefined){
					this.annotations = {};
					this.annotations["XMLRootElement"] = new egl.eglx.xml.binding.annotation.XMLRootElement("utilities", null, false);
				}
				return this.annotations;
			}
			,
			"eze$$getFieldInfos": function() {
				if(this.fieldInfos === undefined){
					var annotations;
					this.fieldInfos = new Array();
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("LibraryList", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("LibraryList");
					this.fieldInfos[0] =new egl.eglx.services.FieldInfo("LibraryList", "LibraryList", "[S;", String, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("ArrayCase", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("ArrayCase");
					this.fieldInfos[1] =new egl.eglx.services.FieldInfo("ArrayCase", "ArrayCase", "[S;", String, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTestArray", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("runTestArray");
					this.fieldInfos[2] =new egl.eglx.services.FieldInfo("runTestArray", "runTestArray", "[org.eclipse.edt.eunit.runtime.runTestMethod", egl.eglx.lang.AnyDelegate, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("ArrayMul", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("ArrayMul");
					this.fieldInfos[3] =new egl.eglx.services.FieldInfo("ArrayMul", "ArrayMul", "[S;", String, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTestArrayMul", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("runTestArrayMul");
					this.fieldInfos[4] =new egl.eglx.services.FieldInfo("runTestArrayMul", "runTestArrayMul", "[org.eclipse.edt.eunit.runtime.runTestMethod", egl.eglx.lang.AnyDelegate, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("Exp", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("Exp");
					this.fieldInfos[5] =new egl.eglx.services.FieldInfo("Exp", "Exp", "[S;", String, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTestExp", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("runTestExp");
					this.fieldInfos[6] =new egl.eglx.services.FieldInfo("runTestExp", "runTestExp", "[org.eclipse.edt.eunit.runtime.runTestMethod", egl.eglx.lang.AnyDelegate, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("hdl", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("hdl");
					this.fieldInfos[7] =new egl.eglx.services.FieldInfo("hdl", "hdl", "[S;", String, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTesthdl", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("runTesthdl");
					this.fieldInfos[8] =new egl.eglx.services.FieldInfo("runTesthdl", "runTesthdl", "[org.eclipse.edt.eunit.runtime.runTestMethod", egl.eglx.lang.AnyDelegate, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("nul", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("nul");
					this.fieldInfos[9] =new egl.eglx.services.FieldInfo("nul", "nul", "[S;", String, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTestnull", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("runTestnull");
					this.fieldInfos[10] =new egl.eglx.services.FieldInfo("runTestnull", "runTestnull", "[org.eclipse.edt.eunit.runtime.runTestMethod", egl.eglx.lang.AnyDelegate, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("pri", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("pri");
					this.fieldInfos[11] =new egl.eglx.services.FieldInfo("pri", "pri", "[S;", String, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTestpri", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("runTestpri");
					this.fieldInfos[12] =new egl.eglx.services.FieldInfo("runTestpri", "runTestpri", "[org.eclipse.edt.eunit.runtime.runTestMethod", egl.eglx.lang.AnyDelegate, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("rcd", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("rcd");
					this.fieldInfos[13] =new egl.eglx.services.FieldInfo("rcd", "rcd", "[S;", String, annotations);
					annotations = {};
					annotations["XMLStyle"] = new egl.eglx.xml.binding.annotation.XMLElement("runTestrcd", null, false, false);
					annotations["JsonName"] = new egl.eglx.json.JsonName("runTestrcd");
					this.fieldInfos[14] =new egl.eglx.services.FieldInfo("runTestrcd", "runTestrcd", "[org.eclipse.edt.eunit.runtime.runTestMethod", egl.eglx.lang.AnyDelegate, annotations);
				}
				return this.fieldInfos;
			}
			,
			"getLibraryList": function() {
				return LibraryList;
			}
			,
			"setLibraryList": function(ezeValue) {
				this.LibraryList = ezeValue;
			}
			,
			"getArrayCase": function() {
				return ArrayCase;
			}
			,
			"setArrayCase": function(ezeValue) {
				this.ArrayCase = ezeValue;
			}
			,
			"getRunTestArray": function() {
				return runTestArray;
			}
			,
			"setRunTestArray": function(ezeValue) {
				this.runTestArray = ezeValue;
			}
			,
			"getArrayMul": function() {
				return ArrayMul;
			}
			,
			"setArrayMul": function(ezeValue) {
				this.ArrayMul = ezeValue;
			}
			,
			"getRunTestArrayMul": function() {
				return runTestArrayMul;
			}
			,
			"setRunTestArrayMul": function(ezeValue) {
				this.runTestArrayMul = ezeValue;
			}
			,
			"getExp": function() {
				return Exp;
			}
			,
			"setExp": function(ezeValue) {
				this.Exp = ezeValue;
			}
			,
			"getRunTestExp": function() {
				return runTestExp;
			}
			,
			"setRunTestExp": function(ezeValue) {
				this.runTestExp = ezeValue;
			}
			,
			"getHdl": function() {
				return hdl;
			}
			,
			"setHdl": function(ezeValue) {
				this.hdl = ezeValue;
			}
			,
			"getRunTesthdl": function() {
				return runTesthdl;
			}
			,
			"setRunTesthdl": function(ezeValue) {
				this.runTesthdl = ezeValue;
			}
			,
			"getNul": function() {
				return nul;
			}
			,
			"setNul": function(ezeValue) {
				this.nul = ezeValue;
			}
			,
			"getRunTestnull": function() {
				return runTestnull;
			}
			,
			"setRunTestnull": function(ezeValue) {
				this.runTestnull = ezeValue;
			}
			,
			"getPri": function() {
				return pri;
			}
			,
			"setPri": function(ezeValue) {
				this.pri = ezeValue;
			}
			,
			"getRunTestpri": function() {
				return runTestpri;
			}
			,
			"setRunTestpri": function(ezeValue) {
				this.runTestpri = ezeValue;
			}
			,
			"getRcd": function() {
				return rcd;
			}
			,
			"setRcd": function(ezeValue) {
				this.rcd = ezeValue;
			}
			,
			"getRunTestrcd": function() {
				return runTestrcd;
			}
			,
			"setRunTestrcd": function(ezeValue) {
				this.runTestrcd = ezeValue;
			}
			,
			"toString": function() {
				return "[utilities]";
			}
		}
	);
});
