java -Xmx128m -classpath C:\Work\JavaCup java_cup.Main -parser ErrorBaseParser -nonterms -expect 134 -symbols ErrorNodeTypes -dump_grammar -dump_states error.cup 2> error.cup.dump
java -classpath ..\..\..\..\..\..\..\..\..\com.ibm.etools.edt.core\bin2 com.ibm.etools.edt.core.ast.ActionMethodSplitter ErrorBaseParser > temp.file
move temp.file ErrorBaseParser.java


