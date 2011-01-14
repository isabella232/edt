del *.java
gawk -f scr.awk EGLPropertiesHandler.txt
unix2dos *.java
rem gawk -f noNotePrinter.awk *.java
