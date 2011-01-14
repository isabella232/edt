set path="D:\j2sdk1.4.2_06\bin";
java -Xmx128m -classpath D:\Work\JavaCup java_cup.Main -nonterms -parser DLIParser -dump_grammar -dump_states -symbols DLINodeTypes dli.cup 2> dli.cup.dump