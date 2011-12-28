package org.eclipse.edt.gen.java.templates.eglx.jtopen;

public interface Constants {
	//Gen methods
	static final String genArrayResize = "genArrayResize";
	static final String genDecimals = "genDecimals";
	static final String genLength = "genLength";
	static final String genPattern = "genPattern";
	static final String preGenAS400Annotation = "preGenAS400Annotation"; 
	static final String getAS400FunctionParameterAnnotation = "getAS400FunctionParameterAnnotation"; 
	
	//signatures
	static final String signature_IBMiProgram = "eglx.jtopen.annotations.IBMiProgram";
	static final String signature_ExternalName = "eglx.lang.ExternalName";
	static final String signature_AS400Array = "eglx.jtopen.annotations.AS400Array";
	static final String signature_AS400Date = "eglx.jtopen.annotations.AS400Date";
	static final String signature_AS400DecimalFloat = "eglx.jtopen.annotations.AS400DecimalFloat";
	static final String signature_AS400DecimalPacked = "eglx.jtopen.annotations.AS400DecimalPacked";
	static final String signature_AS400DecimalZoned = "eglx.jtopen.annotations.AS400DecimalZoned";
	static final String signature_AS400Time = "eglx.jtopen.annotations.AS400Time";
	static final String signature_AS400Text = "eglx.jtopen.annotations.AS400Text";
	static final String signature_AS400Timestamp = "eglx.jtopen.annotations.AS400Timestamp";
	
	//Annotation fields
	static final String subKey_connectionMethod = "connectionMethod";
	static final String subKey_parameterAnnotations = "parameterAnnotations";
	static final String subKey_ibmiFormat = "ibmiFormat";
	static final String subKey_ibmiSeperator = "ibmiSeperator";
	static final String subKey_eglPattern = "eglPattern";
	static final String subKey_connectionResource = "connectionResource";
	static final String subKey_isServiceProgram = "isServiceProgram";
	static final String subKey_libraryName = "libraryName";
	static final String subKey_programName = "programName";
	public static final String subKey_encoding = "encoding";
	public static final String subKey_length = "length";
	public static final String subKey_preserveTrailingSpaces = "preserveTrailingSpaces";
	public static final String subKey_decimals = "decimals";
	public static final String subKey_elementCount = "elementCount";
	public static final String subKey_validElementCountVariable = "validElementCountVariable";
	public static final String subKey_elementTypeAS400Annotation = "elementTypeAS400Annotation";
	
	//Misc constants
	public static final String as400ConnectionName = "ezeAS400Conn";
	static final String PART_HAS_IBMI_FUNCTION = "key_PartHasIbmiFunction";
}
