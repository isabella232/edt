/\/\/\$NON-NLS-1\$/ {
	propName = $1;
	gsub("\"", "", propName);
	gsub(",", "", propName);
	gsub("//.*", "", propName);
	allAnnotations[propName] = 1;
	upperNameToPropName[toupper(propName)] = propName;
}

/^default/ {
	upperName = $2;
	sub("^default [A-Z]+ ", "", $0);
	upperNamesToDefaults[upperName] = $0;
}

/new EGLPropertyRule[^\[].../ {
	shortName = $2;
	split($0, arr, "(");
	upperName = arr[2];
	gsub("^ *", "", upperName);
	gsub(" .*", "", upperName);
	gsub(", *", "", upperName);
	gsub("IEGLConstants.PROPERTY_", "", upperName);
	shortNameToUpperName[shortName] = upperName;
	
	gsub("^.*new EGLPropertyRule *. *IEGLConstants.[A-Z_]+,", "", $0);
	gsub(", *null *, *true", "", $0);
	gsub(" *, *", ",", $0);
	gsub(" *{ *", "{", $0);
	gsub(" *} *", "}", $0);	
	gsub(");.*", "", $0);
	gsub("^ *", "", $0);
	gsub(" *$", "", $0);
	upperNameToTypeString[upperName] = $0;
}

/EGLPropertyRule [a-zA-Z]+ = new EGLPropertyRule\($/ {
	complexAnnotationName = $2;
	isComplexAnnotation[complexAnnotationName] = 1;
	getline;
	getline;
	getline;
	while($1 != "}") {
		numTokens = split($0, arr, ",");
		for(i=1; i <= numTokens; i++) {
			gsub(" ", "", arr[i]);
			gsub("\t", "", arr[i]);
			if(arr[i] != "") {
				inComplexAnnotation[arr[i]] = complexAnnotationName;
			}
		}
		getline;
	}
	shortNameToUpperName[complexAnnotationName] = toupper(complexAnnotationName);
}

/.*\.put\(.*,.*\);.*$/ {
	split($0, arr, ".");
	mapName = arr[1];
	gsub(" ", "", mapName);
	gsub("\t", "", mapName);
	
	numTokens = split($0, arr, ",");
	propName = arr[numTokens];
	gsub(" ", "", propName);
	gsub("\t", "", propName);
	gsub(")", "", propName);
	gsub(";", "", propName);
	
	propName = getPropName(propName);
	
	if(propName in inMap) {
		inMap[propName] = inMap[propName] "," mapName;
	}
	else {
		inMap[propName] = mapName;
	}
}

function getPropName(shortName) {
	return upperNameToPropName[shortNameToUpperName[shortName]];
}

function printAnnotationClass(annotationName) {
	className = firstupper(annotationName) "AnnotationTypeBinding";
	filename = className ".java";

	concatToFile(filename, "package org.eclipse.edt.compiler.binding.annotationType;");
	concatToFile(filename, "");
	concatToFile(filename, "import org.eclipse.edt.compiler.internal.core.utils.InternUtil;");
	concatToFile(filename, "import org.eclipse.edt.compiler.binding.IBinding;");
	concatToFile(filename, "import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.Boolean;");	
	concatToFile(filename, "");
	superClass = getSuperClass(upperNameToTypeString[toupper(annotationName)]);
	if(isComplexAnnotation[annotationName]) {
		concatToFile(filename, "public class " className " extends ComplexAnnotationTypeBinding {");
	}
	else if(superClass == 0) {
		concatToFile(filename, "//PROPNOTE: value type not determined (" upperNameToTypeString[toupper(annotationName)] ")");
		concatToFile(filename, "public class " className " extends AnnotationTypeBinding {");
	}
	else {
		concatToFile(filename, "public class " className " extends " superClass " {");
	}
	concatToFile(filename, "\tpublic static final String name = InternUtil.intern(" "\\\"" annotationName "\\\"" ");");
	concatToFile(filename, "\t");
	concatToFile(filename, "\tprivate static " className " INSTANCE = new " className "();");
	concatToFile(filename, "\t");
	concatToFile(filename, "\tprivate " className "() {");
	concatToFile(filename, "\t\tsuper(name);");
	concatToFile(filename, "\t}");
	concatToFile(filename, "\t");
	concatToFile(filename, "\tpublic static " className " getInstance() {");
	concatToFile(filename, "\t\treturn INSTANCE;");
	concatToFile(filename, "\t}");
	concatToFile(filename, "\t");
	concatToFile(filename, "\tpublic boolean isApplicableFor(IBinding binding) {");

	needReturnFalse = 1;
	if(annotationName in inComplexAnnotation) {
		if(!(annotationName in inMap)) {
			needReturnFalse = 0;
			concatToFile(filename, "\t\treturn binding.isAnnotationBinding() && ((AnnotationBinding) binding).getAnnotationType() == " firstupper(inComplexAnnotation[annotationName]) "AnnotationTypeBinding.getInstance();");
		}
		else {
			concatToFile(filename, "\t\t//PROPNOTE: in complex annotation " inComplexAnnotation[annotationName] ".");
		}
	}	
	if(annotationName in inMap) {
		if(split(inMap[annotationName], arr, ",") == 1) {
			isApplicableText = getIsApplicableTextForSingleMap(inMap[annotationName]);
			if(isApplicableText != 0) {
				needReturnFalse = 0;
				concatToFile(filename, "\t\treturn " isApplicableText ";");
			}
			else {
				isApplicableText = getIsApplicableTextForMultipleMaps(inMap[annotationName]);
				if(isApplicableText != 0) {
					needReturnFalse = 0;
					concatToFile(filename, "\t\treturn " isApplicableText ";");
				}
				else {		
					concatToFile(filename, "\t\t//PROPNOTE: in map: " inMap[annotationName] ".");
				}
			}
		}
		else {
			isApplicableText = getIsApplicableTextForMultipleMaps(inMap[annotationName]);
			if(isApplicableText != 0) {
				needReturnFalse = 0;
				concatToFile(filename, "\t\treturn " isApplicableText ";");
			}
			else {
				concatToFile(filename, "\t\t//PROPNOTE: in maps: " inMap[annotationName] ".");
			}
		}
	}
	if(needReturnFalse) {
		concatToFile(filename, "\t\treturn false;");
	}
	concatToFile(filename, "\t}");
	concatToFile(filename, "\t");
	
	if(upperNamesToDefaults[toupper(annotationName)] != 0) {
		defaultString = getDefaultString(upperNamesToDefaults[toupper(annotationName)]);
		if(defaultString != "noDefault") {
			concatToFile(filename, "\tpublic Object getDefaultValue() {");		
			if(defaultString == 0) {
				concatToFile(filename, "\t\t//PROPNOTE: " upperNamesToDefaults[toupper(annotationName)]);			
				concatToFile(filename, "\t\treturn null;");
			}
			else {
				concatToFile(filename, "\t\treturn " defaultString ";");
			}
			concatToFile(filename, "\t}");
			concatToFile(filename, "\t");
		}
	}
	
	concatToFile(filename, "\tprivate Object readResolve() {");
	concatToFile(filename, "\t\treturn INSTANCE;");
	concatToFile(filename, "\t}");
	
	concatToFile(filename, "}");
}

function getSuperClass(str) {
	if(str == "quotedValueValid") {
		return "StringValueAnnotationTypeBinding";
	}
	if(str == "integerValueValid") {
		return "IntegerValueAnnotationTypeBinding";
	}
	if(str == "specificValueValid,yesOrNo") {
		return "BooleanValueAnnotationTypeBinding";
	}
	if(str == "specificValueValid,yesOrNo,true") {
		return "BooleanValueAnnotationTypeBinding";
	}
	if(str == "new int[]{specificValue},new String []{IEGLConstants.MNEMONIC_SINGLEBYTE,IEGLConstants.MNEMONIC_DOUBLEBYTE}") {
		return "EnumerationValueAnnotationTypeBinding";
	}
	if(str ~ "^ *new int *..{specificValue} *, *new String *.. *{IEGLConstants") {
		return "EnumerationValueAnnotationTypeBinding";
	}
	if(str ~ "^ *specificValueValid *, *new String *.. *{IEGLConstants") {
		return "EnumerationValueAnnotationTypeBinding";
	}
	
	return 0;
}

function getDefaultString(str) {
	if(str == "false") {
		return "noDefault";
	}
	if(str == "true") {
		return "Boolean.YES";
	}
	if(str == "new int[] {24, 80}") {
		return "new Integer[] {new Integer(24), new Integer(80)}";
	}
	if(str == "new int[] {1, 1}") {
		return "new Integer[] {new Integer(1), new Integer(1)}";
	}
	if(str == "1") {
		return "new Integer(1)";
	}
	
	return 0;
}

function getIsApplicableTextForSingleMap(str) {
	if(str == "SQLRecordProperties") {
		return "binding.isAnnotationBinding() && ((AnnotationBinding) binding).getAnnotationType() == SQLRecordAnnotationTypeBinding.getInstance()";
	}
	if(str == "MQRecordProperties") {
		return "binding.isAnnotationBinding() && ((AnnotationBinding) binding).getAnnotationType() == MQRecordAnnotationTypeBinding.getInstance()";
	}
	if(str == "consoleFormProperties") {
		return "binding.isAnnotationBinding() && ((AnnotationBinding) binding).getAnnotationType() == ConosleFormAnnotationTypeBinding.getInstance()";
	}
	if(str == "nativeLibraryProperties") {
		return "binding.isAnnotationBinding() && ((AnnotationBinding) binding).getAnnotationType() == NativeLibraryAnnotationTypeBinding.getInstance()";
	}
	if(str == "openUIProperties") {
		return "false";
	}
	if(str == "dataTableProperties") {
		return "binding.isTypeBinding() && ((ITypeBinding) binding).getKind() == ITypeBinding.DATATABLE_BINDING";	
	}	
	if(str == "formGroupProperties") {
		return "binding.isTypeBinding() && ((ITypeBinding) binding).getKind() == ITypeBinding.FORMGROUP_BINDING";	
	}
	if(str == "pageHandlerProperties") {
		return "binding.isTypeBinding() && ((ITypeBinding) binding).getKind() == ITypeBinding.PAGEHANDLER_BINDING";
	}
	if(str == "sqlItemProperties") {
		return "binding.isDataBinding() && canHaveSQLItemAnnotation((IDataBinding) binding)";
	}	
	if(str == "pageItemProperties") {
		return "binding.isDataBinding() && canHavePageItemAnnotation((IDataBinding) binding)";
	}
		
	return 0;
}

function getIsApplicableTextForMultipleMaps(str) {
	split(str, arr, ",");
	result = "";
	for(i in arr) {
		if(arr[i] == "uiItemProperties") {
			result = concatOr(result, "takesUIItemAnnotation(binding)");
		}
		else if(arr[i] == "formattingProperties") {
			result = concatOr(result, "takesFormattingAnnotation(binding)");
		}
		else if(arr[i] == "validationProperties") {
			result = concatOr(result, "takesValidationAnnotation(binding)");
		}
		else {
			return 0;
		}
	}
	if(result != "") {
		return result;
	}
	return 0;
}

function concatOr(str1, str2) {
	if(str1 == "") {
		return str2;
	}
	return str1 " || " str2;
}

END {
	for(zz in allAnnotations) {
		printAnnotationClass(zz);
		
		print "annotationTypeMap.put(" firstupper(zz) "AnnotationTypeBinding.name, " firstupper(zz) "AnnotationTypeBinding.getInstance());";
	}
}

function firstupper(str) {
	return toupper(substr(str, 1, 1)) substr(str, 2);
}

function concatToFile(filename, str) {
	cmd = "echo \"" str "\" >> " filename;
	system( cmd );
}
