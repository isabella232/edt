set path="C:\Program Files\Java\jdk1.5.0_04\bin"
java -Xmx64m -classpath C:\Work\JavaCup java_cup.Main -parser TempParser -dump_grammar -dump_states egl.cup 2> egl.cup.dump
del sym.java
del TempParser.java
