set path="C:\IBM\SDP\8013\jdk\bin";
java -Xmx128m -classpath C:\Work\JavaCup java_cup.Main -parser ErrorBaseParser -nonterms -expect 134 -symbols ErrorNodeTypes -dump_grammar -dump_states error.cup 2> error.cup.dump
java -classpath ..\..\..\..\..\..\..\..\..\org.eclipse.edt.compiler\bin2 org.eclipse.edt.compiler.core.ast.ActionMethodSplitter ErrorBaseParser > temp.file
move temp.file ErrorBaseParser.java


