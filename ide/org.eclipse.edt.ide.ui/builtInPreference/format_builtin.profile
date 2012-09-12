<?xml version="1.0" encoding="UTF-8"?>
<egl:format_profiles version="1.0" xmlns:egl="http://www.ibm.com/xmlns/egl/formatting/1.0">
  <defaultProfile name="EGL">
    <preview code="
package eglPackage;		
import a.b;
Program aPrograme type basicProgram
variableName String; 
function main()
end
end"/>
    <controls>
      <egl:control.text class="org.eclipse.swt.widgets.Text" name="textControl"/>
      <egl:control.check class="org.eclipse.swt.widgets.Button" name="checkControl" style="SWT.CHECK"/>
      <egl:control.combo class="org.eclipse.swt.widgets.Combo" name="bracesComboControl" choices="%Same_line,%Next_line,%Next_line_indented"/>
      <egl:control.tree name="wsTreeControl"/>
      <egl:control.combo class="org.eclipse.swt.widgets.Combo" name="wrapComboControl" choices="%Do_not_change,%No_wrap,%Wrap_only_when_necessary,%Wrap_each_element_per_lineforce_split,%Wrap_each_element_per_linewhen_necessary"/>
    </controls>
    <category display="%General_Settings" id="generalSettings">
      <preview code="
/**&#13;
 * Indentation&#13;
 */&#13;
 
 Program myPgm type BasicProgram {}
 
 const j int = 5;
 function main()
 i int;
 for(i from 1 to 10 by 1)
 k int = 5;
 if(j==5)
 k=9;
 end
 end
 end
 
 end"/>
      <group display="%Indentation">
        <pref display="%Tab_policy" id="tabPolicy" value="0">
          <egl:control.combo class="org.eclipse.swt.widgets.Combo" choices="%Spaces_only,%Tabs_only"/>
        </pref>
        <pref display="%Indentation_Size" id="indentationSize" value="4">
          <egl:control.ref ref="textControl"/>
        </pref>
      </group>
      <group display="%EGL_keywords_cases">
        <pref display="%EGL_keywords_case_policy" id="keywordCase" value="3">
          <egl:control.combo class="org.eclipse.swt.widgets.Combo" choices="%Do_not_change,%All_Upper_Case,%All_Lower_Case,%EGL_Preferred_Case"/>
        </pref>
      </group>
    </category>
    <category display="%Blank_Lines" id="blankLines">
      <preview code="
/**&#13;
 * Blank lines&#13;
 */&#13;
 package eglPackage;
 import pkgA.pkgB;&#13;
// Between here...&#13;
&#13;&#13;&#13;&#13;&#13;&#13;&#13;&#13;&#13;&#13; 
// ...and here are 10 blank lines&#13;
 import pkgA.pkgC;
 private program myPgm end
 library myLib
 private a, b, c int;
 const x, y, z int = 10;
 s1, s2 string;
 function foo1() end
 function foo2(a int in, b string out) end
 end
 
"/>
      <group display="%Blank_lines_in_EGL_file">
        <pref display="%Before_package_declaration" id="beforePkg" value="0">
          <egl:control.ref ref="textControl"/>
        </pref>
        <pref display="%Before_import_declaration" id="beforeImport" value="1">
          <egl:control.ref ref="textControl"/>
        </pref>
        <pref display="%Before_EGL_part_declaration" id="beforePartDecl" value="1">
          <egl:control.ref ref="textControl"/>
        </pref>
      </group>
      <group display="%Blank_lines_within_EGL_part_declarations">
        <pref display="%Before_data_declaration" id="beforePartDataDecl" value="0" visible="true">
          <egl:control.ref ref="textControl"/>
        </pref>
        <pref display="%Before_nested_function" id="beforeNestedFunction" value="1">
          <egl:control.ref ref="textControl"/>
        </pref>
      </group>
      <group display="%Existing_blank_lines">
        <pref display="%Number_of_blank_lines_to_preserve" id="preservExisting" value="1">
          <egl:control.ref ref="textControl"/>
        </pref>
      </group>
    </category>
    <category display="%White_Space" id="whiteSpace">
      <group display="">
<!-- comma -->
        <pref altDisplay="%Data.%Data_declarations_class_and_local.%Before_comma" display="%Comma.%Before_comma.%Data_declarations_class_and_local" id="beforeComma.dataDecl" value="false">
          <preview code="
Library myhandler
aText, bText, cText, dText String;  //Class data declaration &#13;
private const c1, c2 int = 4;

function foo()
x, y, z int; //Local data declaration &#13;
const cl1, cl2 int = 5;
end
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Set_statement.%Before_comma" display="%Comma.%Before_comma.%Set_statement" id="beforeComma.setStatement" value="false">
          <preview code="
function foo1()
set myname, yourname cursor , full, &#13;
blink,underline,reverse,modified,defaultcolor,bold,skip,empty; //Set statement&#13;
end
"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
<!-- since we do not let user create Enumeration, let's hide this -->
        <pref altDisplay="%Enumeration_fields.%Before_comma" display="%Comma.%Before_comma.%Enumeration_fields" id="beforeComma.enumeration" value="false" visible="false">
          <preview code="
Enumeration WhitespaceKind{eglIsSystemPart = yes}
preserveWhitespace = 1,replaceWhitespace = 2,collapseWhitespace = 3 //Enumaration fields &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Case_statement.%When_clause.%Before_comma" display="%Comma.%Before_comma.%Case_statement_when_clause" id="beforeComma.when" value="false">
          <preview code="
Function foo9()
case (i) //case statement &#13;
when (i + 5, 7) //when clause in a case statement &#13;
end 
end
"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Foreach.%Before_comma" display="%Comma.%Before_comma.%Foreach" id="beforeComma.forEach" value="false">
          <preview code="
function foo8()
foreach(x from myResultSetID) //into clause in a forEach statement &#13;
end 
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        
        <pref altDisplay="%Array.%Before_comma" display="%Comma.%Before_comma.%Array_element_list" id="beforeComma.array" value="false">
          <preview code="
record recordContents 
item1 String[3] = [ &quot;E&quot;,&quot;H&quot;, &quot;C&quot; ];
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Function_Invocation.%Before_comma" display="%Comma.%Before_comma.%Function_Invocation" id="beforeComma.funcInvoc" value="false">
          <preview ref="whiteSpace.beforeLParen.funcInvoc"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Call_statement.%Before_comma" display="%Comma.%Before_comma.%Call_statement" id="beforeComma.callStmt" value="false">
          <preview ref="whiteSpace.beforeLParen.callStmt"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Named_types.%Before_comma" display="%Comma.%Before_comma.%Named_types" id="beforeComma.namedType" value="false">
          <preview ref="whiteSpace.beforeLParen.namedType"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Parameters_function_and_program.%Function_parameters.%Before_comma" display="%Comma.%Before_comma.%Function_parameters" id="beforeComma.funcParms" value="false">
          <preview ref="whiteSpace.beforeLParen.funcParms"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Parameters_function_and_program.%Program_parameters.%Before_comma" display="%Comma.%Before_comma.%Program_parameters" id="beforeComma.pgmParms" value="false">
          <preview ref="whiteSpace.beforeLParen.pgmParms"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Property_settings.%Before_comma" display="%Comma.%Before_comma.%Property_settings" id="beforeComma.settings" value="false">
          <preview code="
record myRecord  {@xmltype {}, @xmlrootelement {}}  //Property settings &#13;
itemName String;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Implements_clause.%Before_comma" display="%Comma.%Before_comma.%Implements_clause" id="beforeComma.implClause" value="false">
          <preview code="
service myService implements myInterface1, myInterface2, myInterface3 //Implementation clause list &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Use_statement.%Before_comma" display="%Comma.%Before_comma.%Use_statement" id="beforeComma.useStatement" value="false">
          <preview code="
library myLib
use form1, form2, form3, pkga.pkgb.form6; //Use statement&#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Data.%Data_declarations_class_and_local.%After_comma" display="%Comma.%After_comma.%Data_declarations_class_and_local" id="afterComma.dataDecl" value="true">
          <preview ref="whiteSpace.beforeComma.dataDecl"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Set_statement.%After_comma" display="%Comma.%After_comma.%Set_statement" id="afterComma.setStatement" value="true">
          <preview ref="whiteSpace.beforeComma.setStatement"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
<!-- since we do not let user create Enumeration, let's hide this -->
        <pref altDisplay="%Enumeration_fields.%After_comma" display="%Comma.%After_comma.%Enumeration_fields" id="afterComma.enumeration" value="true" visible="false">
          <preview ref="whiteSpace.beforeComma.enumeration"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Case_statement.%When_clause.%After_comma" display="%Comma.%After_comma.%Case_statement_when_clause" id="afterComma.when" value="true">
          <preview ref="whiteSpace.beforeComma.when"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Foreach.%After_comma" display="%Comma.%After_comma.%Foreach" id="afterComma.forEach" value="true">
          <preview ref="whiteSpace.beforeComma.forEach"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Array.%After_comma" display="%Comma.%After_comma.%Array_element_list" id="afterComma.array" value="true">
          <preview ref="whiteSpace.beforeComma.array"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Function_Invocation.%After_comma" display="%Comma.%After_comma.%Function_Invocation" id="afterComma.funcInvoc" value="true">
          <preview ref="whiteSpace.beforeLParen.funcInvoc"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Call_statement.%After_comma" display="%Comma.%After_comma.%Call_statement" id="afterComma.callStmt" value="true">
          <preview ref="whiteSpace.beforeLParen.callStmt"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Named_types.%After_comma" display="%Comma.%After_comma.%Named_types" id="afterComma.namedType" value="true">
          <preview ref="whiteSpace.beforeLParen.namedType"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Parameters_function_and_program.%Function_parameters.%After_comma" display="%Comma.%After_comma.%Function_parameters" id="afterComma.funcParms" value="true">
          <preview ref="whiteSpace.beforeLParen.funcParms"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Parameters_function_and_program.%Program_parameters.%After_comma" display="%Comma.%After_comma.%Program_parameters" id="afterComma.pgmParms" value="true">
          <preview ref="whiteSpace.beforeLParen.pgmParms"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Property_settings.%After_comma" display="%Comma.%After_comma.%Property_settings" id="afterComma.settings" value="true">
          <preview ref="whiteSpace.beforeComma.settings"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Implements_clause.%After_comma" display="%Comma.%After_comma.%Implements_clause" id="afterComma.implClause" value="true">
          <preview ref="whiteSpace.beforeComma.implClause"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Use_statement.%After_comma" display="%Comma.%After_comma.%Use_statement" id="afterComma.useStatement" value="true">
          <preview ref="whiteSpace.beforeComma.useStatement"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
<!-- semicolon -->
        <pref altDisplay="%Statements.%Before_semicolon" display="%Semicolon.%Statements" id="beforeSemicolon.statements" value="false">
          <preview code="function foo() a int; a = 5+6;return;end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
<!-- braces -->
        <pref altDisplay="%Property_settings.%Before_opening_brace_" display="%Brace_.%Property_settings.%Before_opening_brace_" id="beforeLCurly.settings" value="false">
          <preview ref="whiteSpace.beforeComma.settings"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Property_settings.%After_opening_brace_" display="%Brace_.%Property_settings.%After_opening_brace_" id="afterLCurly.settings" value="false">
          <preview ref="whiteSpace.beforeLCurly.settings"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Property_settings.%Before_closing_brace_" display="%Brace_.%Property_settings.%Before_closing_brace_" id="beforeRCurly.settings" value="false">
          <preview ref="whiteSpace.beforeLCurly.settings"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
<!-- brackets -->
        <pref altDisplay="%Array.%Before_opening_bracket_" display="%Bracket_.%Array.%Before_opening_bracket_" id="beforeLBracket.array" value="false">
          <preview code="
 function foo()
  myrec.a[1][2][3][4] = 3;
  a[1][2][3][4] = 3;
 end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Array.%After_opening_bracket_" display="%Bracket_.%Array.%After_opening_bracket_" id="afterLBracket.array" value="false">
          <preview ref="whiteSpace.beforeLBracket.array"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Array.%Before_closing_bracket_" display="%Bracket_.%Array.%Before_closing_bracket_" id="beforeRBracket.array" value="false">
          <preview ref="whiteSpace.beforeLBracket.array"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
<!-- parenthesis -->
        <pref altDisplay="%Parameters_function_and_program.%Function_parameters.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Function_parameters" id="beforeLParen.funcParms" value="false">
          <preview code="
function foo5(a int , b int in, c int in, d string out) //Function parameters &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Parameters_function_and_program.%Program_parameters.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Program_parameters" id="beforeLParen.pgmParms" value="false">
          <preview code="
program myPgm1 type BasicProgram(parm1 int, parm2 string, parm3 double) //Program parameters &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Return.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Return" id="beforeLParen.return" value="false">
          <preview code="function foo1() returns (int) //Return declaration &#13;
return (4); //Return statement &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%If.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%If" id="beforeLParen.if" value="false">
          <preview code="
function foo2(a int, i int)
if (i == 5) //If statement &#13;
a = i;end 
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%While.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%While" id="beforeLParen.while" value="false">
          <preview code="
function foo3(i int)
while (i &lt; 5) continue; //While statement &#13;
end 
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%For.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%For" id="beforeLParen.for" value="false">
          <preview code="
function foo4(i int, a int)
for (i from 1 to 10 by 1) //For statement &#13;
a+=i;
end 
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Foreach.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Foreach" id="beforeLParen.forEach" value="false">
          <preview code="
function foo5(i int in)
forEach (j from selectEmp) //ForEach statement &#13;
i = i + 1;
continue;
end 
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Call_statement.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Call_statement" id="beforeLParen.callStmt" value="false">
          <preview code="
function foo7()
call &quot;testproj&quot; (&quot;abc&quot;, &quot;xyz&quot;); //Call statement &#13;
call MYPGM(myInt, myChar, myInt, myInt); //Call statement &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Case_statement.%When_clause.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Case_statement_when_clause" id="beforeLParen.when" value="false">
          <preview ref="whiteSpace.beforeComma.when"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Try_onException_block.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Try_onException_block" id="beforeLParen.onException" value="false">
          <preview code="
function foo10()
try
a=8;
onexception(e MyException) //OnException caluse in try statement &#13;
a=0;
end
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Named_types.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Named_types" id="beforeLParen.namedType" value="false">
          <preview code="
function foo11()
a Text = new Text(&quot;label&quot;); //Named type &#13;
myType myType = new myType(a, b, 5); //Named type &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Case_statement.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Case_statement" id="beforeLParen.case" value="false">
          <preview ref="whiteSpace.beforeComma.when"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Exit_statement.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Exit_statement" id="beforeLParen.exit" value="false">
          <preview code="
program pgm 
function main()
b int;
case (b)
when (6)
exit program (b+1); //exit statement &#13;
when (7)
exit rununit (b+2); //exit statement &#13;
end 
end 
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Parenthesized_expression.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Parenthesized_expression" id="beforeLParen.parentExpr" value="false">
          <preview code="
program pgm 
function main()
stringParmFunc(( cf.name ) ); //Parenthesised expression &#13;&#13;
if(( mystring ) not null ) //Parenthesised expression &#13; end &#13;&#13;
for( i from( 1 + 3 ) to( 3 * 4 ) by( i / 2 ) ) //Parenthesised expression &#13; end 
end 
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Function_Invocation.%Before_opening_parenthesis_" display="%Parenthesis_.%Before_opening_parenthesis_.%Function_Invocation" id="beforeLParen.funcInvoc" value="false">
          <preview code="
function foo()
myService.foo(a, this, &quot;x&quot;, 3, new Text); //Function invocation&#13;
myPgm.foo1(b, c); //Function invocation &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Parameters_function_and_program.%Function_parameters.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Function_parameters" id="afterLParen.funcParms" value="false">
          <preview ref="whiteSpace.beforeLParen.funcParms"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Parameters_function_and_program.%Program_parameters.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Program_parameters" id="afterLParen.pgmParms" value="false">
          <preview ref="whiteSpace.beforeLParen.pgmParms"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Return.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Return" id="afterLParen.return" value="false">
          <preview ref="whiteSpace.beforeLParen.return"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%If.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%If" id="afterLParen.if" value="false">
          <preview ref="whiteSpace.beforeLParen.if"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%While.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%While" id="afterLParen.while" value="false">
          <preview ref="whiteSpace.beforeLParen.while"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%For.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%For" id="afterLParen.for" value="false">
          <preview ref="whiteSpace.beforeLParen.for"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Foreach.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Foreach" id="afterLParen.forEach" value="false">
          <preview ref="whiteSpace.beforeLParen.forEach"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Call_statement.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Call_statement" id="afterLParen.callStmt" value="false">
          <preview ref="whiteSpace.beforeLParen.callStmt"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Case_statement.%When_clause.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Case_statement_when_clause" id="afterLParen.when" value="false">
          <preview ref="whiteSpace.beforeLParen.when"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Try_onException_block.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Try_onException_block" id="afterLParen.onException" value="false">
          <preview ref="whiteSpace.beforeLParen.onException"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Named_types.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Named_types" id="afterLParen.namedType" value="false">
          <preview ref="whiteSpace.beforeLParen.namedType"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Case_statement.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Case_statement" id="afterLParen.case" value="false">
          <preview ref="whiteSpace.beforeLParen.case"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Exit_statement.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Exit_statement" id="afterLParen.exit" value="false">
          <preview ref="whiteSpace.beforeLParen.exit"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Parenthesized_expression.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Parenthesized_expression" id="afterLParen.parentExpr" value="false">
          <preview ref="whiteSpace.beforeLParen.parentExpr"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Function_Invocation.%After_opening_parenthesis_" display="%Parenthesis_.%After_opening_parenthesis_.%Function_Invocation" id="afterLParen.funcInvoc" value="false">
          <preview ref="whiteSpace.beforeLParen.funcInvoc"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Parameters_function_and_program.%Function_parameters.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Function_parameters" id="beforeRParen.funcParms" value="false">
          <preview ref="whiteSpace.beforeLParen.funcParms"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Parameters_function_and_program.%Program_parameters.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Program_parameters" id="beforeRParen.pgmParms" value="false">
          <preview ref="whiteSpace.beforeLParen.pgmParms"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Return.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Return" id="beforeRParen.return" value="false">
          <preview ref="whiteSpace.beforeLParen.return"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%If.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%If" id="beforeRParen.if" value="false">
          <preview ref="whiteSpace.beforeLParen.if"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%While.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%While" id="beforeRParen.while" value="false">
          <preview ref="whiteSpace.beforeLParen.while"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%For.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%For" id="beforeRParen.for" value="false">
          <preview ref="whiteSpace.beforeLParen.for"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Foreach.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Foreach" id="beforeRParen.forEach" value="false">
          <preview ref="whiteSpace.beforeLParen.forEach"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Call_statement.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Call_statement" id="beforeRParen.callStmt" value="false">
          <preview ref="whiteSpace.beforeLParen.callStmt"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Case_statement.%When_clause.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Case_statement_when_clause" id="beforeRParen.when" value="false">
          <preview ref="whiteSpace.beforeLParen.when"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Try_onException_block.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Try_onException_block" id="beforeRParen.onException" value="false">
          <preview ref="whiteSpace.beforeLParen.onException"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Named_types.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Named_types" id="beforeRParen.namedType" value="false">
          <preview ref="whiteSpace.beforeLParen.namedType"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Case_statement.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Case_statement" id="beforeRParen.case" value="false">
          <preview ref="whiteSpace.beforeLParen.case"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Exit_statement.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Exit_statement" id="beforeRParen.exit" value="false">
          <preview ref="whiteSpace.beforeLParen.exit"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Parenthesized_expression.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Parenthesized_expression" id="beforeRParen.parentExpr" value="false">
          <preview ref="whiteSpace.beforeLParen.parentExpr"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Function_Invocation.%Before_closing_parenthesis_" display="%Parenthesis_.%Before_closing_parenthesis_.%Function_Invocation" id="beforeRParen.funcInvoc" value="false">
          <preview ref="whiteSpace.beforeLParen.funcInvoc"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
<!-- operator -->
        <pref altDisplay="%Statements.%Assignment.%Before_operator" display="%Operator.%Before_operator.%Assignment" id="beforeOperator.assignment" value="true">
          <preview code="
Function foo9(i int in)
d myDataItem{align = none, fillCharacter = nullFill};
a int = 6;
a += i;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Binary.%Before_operator" display="%Operator.%Before_operator.%Binary" id="beforeOperator.binary" value="true">
          <preview code="
Function foo7(a int, b int, c int)
a = b + c+d+e+f+g+h+i;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Assignment.%After_operator" display="%Operator.%After_operator.%Assignment" id="afterOperator.assignment" value="true">
          <preview ref="whiteSpace.beforeOperator.assignment"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Unary___.%After_operator" display="%Operator.%After_operator.%Unary___" id="afterOperator.unary" value="false">
          <preview code="
Function foo8()
a int = -1;
b boolean = !true;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Binary.%After_operator" display="%Operator.%After_operator.%Binary" id="afterOperator.binary" value="true">
          <preview ref="whiteSpace.beforeOperator.binary"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Data.%Nullable_fields.%Before_question_mark" display="%Question_mark.%Nullable_fields" id="beforeQuestion.nullableFields" value="false">
          <preview code="Function foo()
i int?; // Field &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Data.%Nullable_parameters.%Before_question_mark" display="%Question_mark.%Nullable_parameters" id="beforeQuestion.nullableParameters" value="false">
          <preview code="Function foo(i int?)  // Parameter &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Data.%Nullable_returns.%Before_question_mark" display="%Question_mark.%Nullable_returns" id="beforeQuestion.nullableReturns" value="false">
          <preview code="Function foo() returns(int?) // Returns &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
<!-- colon -->
        <pref altDisplay="%Array.%Sub_string_access.%Before_colon_" display="%Colon.%Before_colon_.%Sub_string_access" id="beforeColon.subString" value="true">
          <preview code="Function foo()
a String;
a[3:5]=&quot;abc&quot;;      //Sub string access&#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Statements.%Label_statement.%Before_colon_" display="%Colon.%Before_colon_.%Label_statement" id="beforeColon.labelStmt" value="false">
          <preview code="function foo1()
sharedLabel:     //Label statement&#13;
goto sharedLabel;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref altDisplay="%Array.%Sub_string_access.%After_colon_" display="%Colon.%After_colon_.%Sub_string_access" id="afterColon.subString" value="true">
          <preview ref="whiteSpace.beforeColon.subString"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%View_by_" id="sortBy" value="0">
          <egl:control.combo choices="%Sort_by_white_space_position,%Sort_by_EGL_syntax_element"/>
        </pref>
      </group>
    </category>
    <category display="%Braces" id="braces">
      <preview code="
/**&#13;
 * Braces&#13;
 */

record myRecord {@xmltype {},@xmlrootelement {}}
	aField String{};
end"/>
      <group display="%Open_curly_braces_position">
        <pref display="%Property_settings" id="openCurlyPosition" value="0">
          <egl:control.ref ref="bracesComboControl"/>
        </pref>
      </group>
    </category>
    <category display="%Line_Wrapping" id="lineWrapping">
      <preview code=""/>
      <group display="%Line_width_and_indentation_levels">
        <pref display="%Maximum_line_width" id="maxLineWidth" value="80">
          <egl:control.ref ref="textControl"/>
        </pref>
        <pref display="%Default_number_of_indentations_for_wrapped_lines" id="numOfIndent4WrappedLines" value="2">
          <egl:control.ref ref="textControl"/>
        </pref>
      </group>
      <group display="%Wrapping_policy">
        <pref display="%EGL_language.%Property_settings" id="settingsBlock" value="1">
          <preview ref="whiteSpace.beforeComma.settings"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Initialization_expression" id="initExpr" value="1">
          <preview ref="whiteSpace.beforeComma.dataDecl"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Binary_expression" id="binaryExpr" value="2">
          <preview ref="whiteSpace.beforeOperator.binary"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Use_statement" id="useStmt" value="1">
          <preview ref="whiteSpace.beforeComma.useStatement"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
       
        <pref display="%EGL_language.%For_statement" id="forStmt" value="1">
          <preview ref="whiteSpace.beforeLParen.for"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Call_statement" id="callStmt" value="2">
           <preview code="
function fooCallStmts()
&#13;&#13;//Call statement &#13;
call svr1.foo(param1, param2) returning to myCallbackFunc onException myErrorCallbackFunc {timeout=15000};
end"/>
           <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%IO_statement" id="ioStmt" value="2">
          <preview code="
function fooIOStmts()

&#13;&#13;//Add statement &#13;
add offering to ds;

&#13;&#13;//Delete statement &#13;
delete customer from ds;

&#13;&#13;//Execute statement &#13;
execute from fromclause;

&#13;&#13;//Get statement &#13;
get action_target1 from ds;

&#13;&#13;//Open statement &#13;
open sqlstatement from ds; 

&#13;&#13;//Prepare statement &#13;
prepare sqlStatement from ds with sqlLiteral;

end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Data_declarations_class_and_local" id="varDelcaration" value="1">
          <preview ref="whiteSpace.beforeComma.dataDecl"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Parameters_function_and_program" id="parameters" value="1">
          <preview code="
program myPgm1 type BasicProgram(parm1 int, parm2 string, parm3 double) //Program parameter list &#13;
end
function foo5(a int , b int in, c int in, d string out) //Function parameter list &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Expressions_list" id="exprs" value="1">
          <preview code="
Function foo2()
case (i) 
when (i + 5, 7) //Expression list in when clause in a case statement &#13;
end&#13;&#13;

end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Arguments_list" id="args" value="2">
          <preview code="
Function foo()
myService.foo(a, this, &quot;x&quot;, 3, new Text); //Function invocation&#13;
call foo4(4, 5, a); //Call statement &#13;
myType myType = new myType(a, b, 5); //New expression &#13;
end"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Array_element_list" id="array" value="2">
          <preview ref="whiteSpace.beforeComma.array"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Set_statement" id="setStmt" value="2">
          <preview ref="whiteSpace.beforeComma.setStatement"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%EGL_language.%Implements_clause" id="implClause" value="1">
          <preview ref="whiteSpace.beforeComma.implClause"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
<!-- since we do not let user create Enumeration, let's hide this -->
        <pref display="%EGL_language.%Enumeration_fields" id="enumFields" value="1" visible="false">
          <preview ref="whiteSpace.beforeComma.enumeration"/>
          <egl:control.ref ref="wsTreeControl"/>
        </pref>
        <pref display="%Setting_for_wrapping_policy" id="wrappingPolicyChoice" value="0">
          <egl:control.ref ref="wrapComboControl"/>
        </pref>
      </group>
    </category>
  </defaultProfile>
</egl:format_profiles>