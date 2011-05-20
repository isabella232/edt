cd ..

del org.eclipse.edt.compiler\lib\*.eglar
del org.eclipse.edt.compiler\lib\*.mofar

java -classpath .\com.ibm.icu\com.ibm.icu_3.8.1.v20080530.jar;.\org.eclipse.edt.compiler\bin;.\org.eclipse.edt.mof.egl\bin;.\org.eclipse.edt.mof.egl\bin2;.\org.eclipse.edt.mof\bin; org.eclipse.edt.mof.egl.tools.CompileSystemMofParts  
java -classpath .\com.ibm.icu\com.ibm.icu_3.8.1.v20080530.jar;.\org.eclipse.edt.compiler\bin;.\org.eclipse.edt.mof.egl\bin;.\org.eclipse.edt.mof.egl\bin2;.\org.eclipse.edt.mof\bin; org.eclipse.edt.mof.egl.tools.CompileSystemMofParts  

java -classpath .\com.ibm.icu\com.ibm.icu_3.8.1.v20080530.jar;.\org.eclipse.edt.compiler\bin;.\org.eclipse.edt.mof.egl\bin;.\org.eclipse.edt.mof.egl\bin2;.\org.eclipse.edt.mof\bin; org.eclipse.edt.mof.egl.tools.CompileSystemReflectParts  
java -classpath .\com.ibm.icu\com.ibm.icu_3.8.1.v20080530.jar;.\org.eclipse.edt.compiler\bin;.\org.eclipse.edt.mof.egl\bin;.\org.eclipse.edt.mof.egl\bin2;.\org.eclipse.edt.mof\bin; org.eclipse.edt.mof.egl.tools.CompileSystemReflectParts  

java -classpath .\com.ibm.icu\com.ibm.icu_3.8.1.v20080530.jar;.\org.eclipse.edt.compiler\bin;.\org.eclipse.edt.mof.egl\bin;.\org.eclipse.edt.mof.egl\bin2;.\org.eclipse.edt.mof\bin; org.eclipse.edt.mof.egl.tools.CompileSystemEglParts  

cd org.eclipse.edt.compiler\SystemPackageBin

jar -cf ../lib/core.mofar egl/lang/reflect/*.mofxml egl/lang/reflect/mof/*.mofxml egl/lang/reflect/refTypes/*.mofxml
jar -cf ../lib/language.mofar org/eclipse/edt/mof/egl/*.mofxml org/eclipse/edt/mof/egl/sql/*.mofxml

jar -cf ../lib/core.eglar egl/core/*.eglxml egl/io/sql/*.eglxml 
jar -cf ../lib/language.eglar egl/lang/*.eglxml egl/lang/reflect/*.eglxml
jar -cf ../lib/consoleUI.eglar egl/ui/console/*.eglxml
jar -cf ../lib/dli.eglar egl/io/dli/*.eglxml
jar -cf ../lib/file.eglar egl/io/file/*.eglxml
jar -cf ../lib/jasper.eglar egl/ui/jasper/*.eglxml
jar -cf ../lib/mq.eglar egl/io/mq/*.eglxml
jar -cf ../lib/java.eglar egl/java/*.eglxml egl/idl/java/*.eglxml
jar -cf ../lib/platform.eglar egl/platform/*.eglxml  egl/platform/i5os/*.eglxml
jar -cf ../lib/jsf.eglar egl/ui/jsf/*.eglxml
jar -cf ../lib/textUI.eglar egl/ui/text/*.eglxml
jar -cf ../lib/ui.eglar egl/ui/*.eglxml
jar -cf ../lib/vg.eglar egl/vg/*.eglxml egl/ui/webtransaction/*.eglxml
jar -cf ../lib/report.eglar egl/report/birt/*.eglxml egl/report/text/*.eglxml
jar -cf ../lib/javascript.eglar egl/javascript/*.eglxml
jar -cf ../lib/rui.eglar egl/ui/rui/*.eglxml

cd ..
del /s /q SystemPackageBin
rmdir /s /q SystemPackageBin