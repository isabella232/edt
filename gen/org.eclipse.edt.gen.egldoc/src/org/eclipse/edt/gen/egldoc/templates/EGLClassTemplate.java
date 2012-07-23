package org.eclipse.edt.gen.egldoc.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.edt.gen.egldoc.Context;
import org.eclipse.edt.gen.egldoc.NamedElementComparator;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Part;

public class EGLClassTemplate extends EGLDocTemplate {
	
	NamedElementComparator comparator = new NamedElementComparator();
	
	public void genClassContent(Part part, Context ctx, TabbedWriter out) {
		ctx.invoke(genStereotypeName, part, ctx, out);
		// ctx.invokeSuper(this, genClassContent, part, ctx);
		ctx.invoke(genConstants, part, ctx, out);
		ctx.invoke(genFields, part, ctx, out);
		ctx.invoke(genFunctions, part, ctx, out);
	}

	public void genConstants(EGLClass part, Context ctx, TabbedWriter out) {
		List<ConstantField> constantFields = new ArrayList<ConstantField>();
		for (Field field : part.getFields()) {
			if (field instanceof ConstantField) {
				constantFields.add((ConstantField) field);
			}
		}
		if (constantFields.size() > 0) {
			out.println("Constants");
		} else {
			
			
			Collections.sort(constantFields, comparator);			
			
			for (ConstantField constantField : constantFields) {
				ctx.invoke(genConstant, part, ctx, out, constantField);
			}
		}
	}

	public void genConstant(EGLClass part, Context ctx, TabbedWriter out,
			ConstantField arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genFields(EGLClass part, Context ctx, TabbedWriter out) {

		String fieldContainerType = (String) ctx.get("fieldContainerType");

		List<Field> nonConstantFields = new ArrayList<Field>();
		for (Field field : part.getFields()) {
			if (!(field instanceof ConstantField)) {
				nonConstantFields.add((Field) field);
			}
		}

		/*
		 * SBM. show section in any case if(nonConstantFields.size() > 0){
		 * out.println(part.getEClass().getName() + " fields"); }
		 */
		// part.getEClass().getName()
		out.println("<dt class=\"dt dlterm\"><a name=\"recordfields\"></a>"
				+ fieldContainerType + " fields</dt>");
		if (nonConstantFields.size() == 0) {

			out.println("<dd> <p class=\"p\">None.</p><p class=\"p\"></p></dd></dt>");
			/*
			 * out.println("<dd class=\"dd\">"); out.println("None.");
			 * out.println("<p class=\"p\"></p>"); out.println("</dd>");
			 */
		} else {
			// FieldComparator comparator = new FieldComparator();
			Collections.sort(nonConstantFields, comparator);

			out.println("<dd class=\"dd\"><dl class=\"dl parml\"><p class=\"p\"></p>");

			for (Field field : nonConstantFields) {
				ctx.invoke(genField, part, ctx, out, field);
				out.println("<p class=\"p\"></p>");
			}
			out.println("<p class=\"p\"></p></dd>");
		}

	}

	public void genField(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);

	}

	public void genFunctions(EGLClass part, Context ctx, TabbedWriter out) {

		out.println("<dt class=\"dt dlterm\"><a name=\"functions\"></a>Functions</dt>");
		out.println("<dd class=\"dd\"><p class=\"p\"></p>");

		if (part.getFunctions().size() == 0) {
			out.println("<dd> <p class=\"p\">None.</p><p class=\"p\"></p></dd></dt>");
		} else {
			out.println("<dd class=\"dd\"><dl class=\"dl parml\"><p class=\"p\"></p>");
			out.println("<div class=\"tablenoborder\">");
			out.println("<table cellpadding=\"4\" cellspacing=\"0\" id=\"function_table\"");
			out.println("class=\"table\" frame=\"border\" border=\"1\" rules=\"all\">");
			out.println("<thead class=\"thead\" align=\"left\">");
			out.println("<tr class=\"row\" valign=\"bottom\">");
			// out.println("<th class=\"entry\" valign=\"bottom\">Name</th>");
			// out.println("<th class=\"entry\" valign=\"bottom\" width=\"70%\">Description</th>)
			out.println("</tr></thead>");
			out.println("<tbody class=\"tbody\">");

			List<Function> theFunctions = new ArrayList<Function>();
			for (Function theFunction : part.getFunctions()) {
				theFunctions.add((Function) theFunction);
			}

			// FunctionComparator comparator = new FunctionComparator();
			Collections.sort(theFunctions, comparator);

			for (Function function : theFunctions) {
				ctx.invoke(genFunction, part, ctx, out, function);
			}

			out.println("</tbody></table></div></dd></dl></div>");
		}

	}

	public void genFunction(EGLClass part, Context ctx, TabbedWriter out,
			Function arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
	}
}
