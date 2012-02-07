set path="C:\IBM\SDP\8013\jdk\bin";
java -Xmx64m -classpath C:\Work\JavaCup java_cup.Main -nonterms -parser Parser -dump_grammar -dump_states -symbols NodeTypes egl.cup 2> egl.cup.dump
java -classpath ..\..\..\..\..\..\..\bin2 org.eclipse.edt.compiler.core.ast.ActionMethodSplitter Parser > temp.file
move temp.file Parser.java
