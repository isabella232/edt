package org.eclipse.edt.gen.egldoc.templates;

import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;

public class FunctionTemplate extends EGLDocTemplate {

	public void genSyntax(Function function, Context ctx, TabbedWriter out) {
		// process the function
		/*
		<dt class="dt dlterm"><a name="compat"></a>Compatibility</dt>
		<dd class="dd"><p class="p"></p>
		<div class="tablenoborder"><table cellpadding="4" cellspacing="0" summary=""
		id="compat_table" class="table" frame="border" border="1"
		rules="all">
		<thead class="thead" align="left">
		<tr class="row" valign="bottom">
		<th class="entry" valign="bottom" width="20%">Target</th>
		<th class="entry" valign="bottom" width="80%">Issue</th></tr>
		</thead>
		<tbody class="tbody">
		<tr id="compat_java" class="row">
		<td class="entry" valign="top" width="20%">Java</td>
		<td class="entry" valign="top" width="80%">No issues.</td>
		</tr>
		<tr id="compat_javascript" class="row">
		<td class="entry" valign="top" width="20%">JavaScript</td>
		<td class="entry" valign="top" width="80%">No issues.</td>
		</tr>
		</tbody>
		</table>
		</div>
		</dd>
		</dl>
		</div>
		*/
		
		

			
		
		// ???  out.println(function.getId());
		// out.println("Syntax");
		List<FunctionParameter> parameters = function.getParameters();
		
		int numberOfParmsLeft = parameters.size() - 1;
		
		for (FunctionParameter functionParameter : parameters) {
			String parmName = functionParameter.getName().trim();
			String parmType = functionParameter.getType().getTypeSignature().toString();
			
			
			/** TODO.  Identify the url for the field-type doc*/
			String typeDocLocation = "unknownParmType";
					
			int lastPeriod = parmType.lastIndexOf('.');
			
			if (lastPeriod != 0) {
	           parmType = parmType.substring(lastPeriod + 1);
			}
			
			out.println(parmName + " <a href=\"" + typeDocLocation + "\">" +  parmType + "</a>");			
						
			if (numberOfParmsLeft > 0) {
				
				out.println(", ");
				numberOfParmsLeft--;
			}
		}
	}

	public void genDeclaration(Function function, org.eclipse.edt.gen.egldoc.Context ctx, TabbedWriter out) {
		String functionName = function.getName();
		String functionFirstPara = "What a function!";
				
		out.println("<tr id=\"" + functionName + "\" class=\"row\">");
		out.println("<td class=\"entry\" valign=\"top\">" + functionName + " (");
		ctx.invoke(genSyntax, function, ctx, out);
		out.println(")<p></p>");
		out.println(functionFirstPara + "</td></tr>");
		
		//ctx.invoke(genFunctionExampleUse, function, ctx, out);
		//ctx.invoke(genComments()
		//ctx.invoke(genExample()
		//ctx.invoke(genCompatibility))
	}
}
