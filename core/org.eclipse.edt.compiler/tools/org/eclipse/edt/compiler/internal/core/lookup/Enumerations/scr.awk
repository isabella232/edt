{
	enumName = $1;
	package = $2;
	filename = enumName ".java";
	quote = 0x2;

concatToFile(filename, "package org.eclipse.edt.compiler.internal.core.lookup.Enumerations;");
concatToFile(filename, "");
concatToFile(filename, "import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;");
concatToFile(filename, "import org.eclipse.edt.compiler.binding.SystemEnumerationDataBinding;");
concatToFile(filename, "import org.eclipse.edt.compiler.binding.SystemEnumerationTypeBinding;");
concatToFile(filename, "import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironment;");
concatToFile(filename, "import org.eclipse.edt.compiler.internal.core.utils.InternUtil;");
concatToFile(filename, "");
concatToFile(filename, "public class " enumName " extends Enumeration{");
concatToFile(filename, "\tpublic final static int TYPE_CONSTANT = " toupper(enumName) ";");
concatToFile(filename, "");
for(i = 3; i <= NF; i++) {
concatToFile(filename, "\tpublic final static int " toupper($i) "_CONSTANT = " (i-2) ";");
}
concatToFile(filename, "");
concatToFile(filename, "\tpublic final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironment." package ", InternUtil.intern(" "\\\"" enumName "\\\"" "), " toupper(enumName) ");");
for(i = 3; i <= NF; i++) {
concatToFile(filename, "\tpublic final static SystemEnumerationDataBinding " toupper($i) " = new SystemEnumerationDataBinding(InternUtil.intern(" "\\\"" $i "\\\"" "), null, TYPE, " toupper($i) "_CONSTANT);");
}
concatToFile(filename, "");
concatToFile(filename, "\tstatic {");
concatToFile(filename, "\t\tTYPE.setValid(true);");
for(i = 3; i <= NF; i++) {
concatToFile(filename, "\t\tTYPE.addEnumeration(" toupper($i) ");");
}
concatToFile(filename, "\t};");
concatToFile(filename, "}");
}

function concatToFile( filename, str ) {
	cmd = "echo \"" str "\" >> " filename;
	system( cmd );
}
