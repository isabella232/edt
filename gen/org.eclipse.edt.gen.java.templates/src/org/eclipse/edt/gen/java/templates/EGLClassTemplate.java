/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates;

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class EGLClassTemplate extends JavaTemplate {

	public void preGenClassBody(EGLClass part, Context ctx) {
		ctx.invoke(preGenUsedParts, part, ctx);
		ctx.invoke(preGenFields, part, ctx);
		ctx.invoke(preGenFunctions, part, ctx);
		ctx.invoke(preGenAnnotations, part, ctx);
		if (part.getAnnotation(org.eclipse.edt.gen.Constants.AnnotationXMLRootElement) == null) {
			// add an xmlRootElement
			try {
				Annotation annotation = CommonUtilities.getAnnotation(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter
					+ org.eclipse.edt.gen.Constants.AnnotationXMLRootElement);
				annotation.setValue("name", part.getId());
				part.addAnnotation(annotation);
			}
			catch (Exception e) {}
		}
	}

	public void preGenAnnotations(EGLClass part, Context ctx) {
		for (Annotation annot : part.getAnnotations()) {
			ctx.invoke(preGen, annot.getEClass(), ctx, annot, part);
		}
	}

	public void preGenUsedParts(EGLClass part, Context ctx) {
		for (Part item : IRUtils.getReferencedPartsFor(part)) {
			ctx.invoke(preGenUsedPart, part, ctx, item);
		}
	}

	public void preGenUsedPart(EGLClass part, Context ctx, Part arg) {
		ctx.invoke(preGen, arg, ctx);
	}

	public void preGenFields(EGLClass part, Context ctx) {
		for (Field field : part.getFields()) {
			ctx.invoke(preGenField, part, ctx, field);
		}
	}

	public void preGenField(EGLClass part, Context ctx, Field arg) {
		ctx.invoke(preGen, arg, ctx);
	}

	public void preGenFunctions(EGLClass part, Context ctx) {
		for (Function function : part.getFunctions()) {
			ctx.invoke(preGenFunction, part, ctx, function);
		}
	}

	public void preGenFunction(EGLClass part, Context ctx, Function arg) {
		ctx.invoke(preGen, arg, ctx);
	}

	public void genClassHeader(EGLClass part, Context ctx, TabbedWriter out) {
		ctx.invoke(genAnnotations, part, ctx, out);
		out.print("public class ");
		ctx.invoke(genClassName, part, ctx, out);
		out.print(" extends ");
		ctx.invoke(genSuperClass, part, ctx, out);
		ctx.invoke(genImplements, part, ctx, out);
		out.println(" {");
		ctx.invoke(genSerialVersionUID, part, ctx, out);
	}

	public void genImplements(EGLClass part, Context ctx, TabbedWriter out) {
	}
	
	public void genAnnotations(EGLClass part, Context ctx, TabbedWriter out) {
		for (Annotation annot : part.getAnnotations()) {
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot);
		}
	}

	public void genClassBody(EGLClass part, Context ctx, TabbedWriter out) {
		// Add this part's file to the smap files list, so that this part is always the first file listed.
		String file = IRUtils.getQualifiedFileName(part);
		ctx.setCurrentFile(file);
		if (ctx.getSmapFiles().indexOf(file) < 0) {
			ctx.getSmapFiles().add(file);
		}

		ctx.invoke(genFields, part, ctx, out);
		ctx.invoke(genLibraries, part, ctx, out);
		ctx.invoke(genConstructors, part, ctx, out);
		ctx.invoke(genInitializeMethods, part, ctx, out);
		ctx.invoke(genGetterSetters, part, ctx, out);
		ctx.invoke(genLibraryAccessMethods, part, ctx, out);
		ctx.invoke(genFunctions, part, ctx, out);
	}

	public void genFields(EGLClass part, Context ctx, TabbedWriter out) {
		for (Field field : part.getFields()) {
			ctx.invoke(genField, part, ctx, out, field);
			ctx.writeSmapLine();
		}
		// create the type constraint static hashmap, only if there are some parameterized types
		boolean needConstraints = false;
		for (Field field : part.getFields()) {
			// we only want to add constraints for parameterized types
			if (field.getType() instanceof ParameterizedType) {
				needConstraints = true;
				break;
			}
		}
		if (needConstraints) {
			out.println("private static java.util.HashMap<String, TypeConstraints> ezeTypeConstraints = new java.util.HashMap<String, TypeConstraints>();");
			out.println("static {");
			for (Field field : part.getFields()) {
				// we only want to add constraints for parameterized types
				if (field.getType() instanceof ParameterizedType) {
					out.print("ezeTypeConstraints.put(\"");
					ctx.invoke(genName, field, ctx, out);
					out.print("\", new TypeConstraints(");
					ctx.invoke(genRuntimeConstraint, field.getType(), ctx, out);
					out.println("));");
				}
			}
			out.println("}");
		}
	}

	public void genField(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	@SuppressWarnings("unchecked")
	public void genLibraries(EGLClass part, Context ctx, TabbedWriter out) {
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partLibrariesUsed);
		for (Library library : libraries) {
			ctx.invoke(genLibrary, part, ctx, out, library);
			ctx.writeSmapLine();
		}
	}

	public void genLibrary(EGLClass part, Context ctx, TabbedWriter out, Library arg) {
		out.print("public ");
		ctx.invoke(genRuntimeTypeName, (Type) arg, ctx, out, TypeNameKind.EGLImplementation);
		out.println(" " + Constants.LIBRARY_PREFIX + arg.getFullyQualifiedName().replace('.', '_') + ";");
	}

	public void genConstructors(EGLClass part, Context ctx, TabbedWriter out) {
		ctx.invoke(genConstructor, part, ctx, out);
	}

	public void genConstructor(EGLClass part, Context ctx, TabbedWriter out) {}

	public void genInitializeMethods(EGLClass part, Context ctx, TabbedWriter out) {
		out.println("public void ezeInitialize() {");
		ctx.invoke(genInitializeMethodBody, part, ctx, out);
		out.println("}");
	}

	public void genInitializeMethodBody(EGLClass part, Context ctx, TabbedWriter out) {
		List<Field> fields = part.getFields();
		if (fields.size() > 0 && fields.get(0).getInitializerStatements() == null) {
			// Work around JDT issue - if the first stmt in the initializer won't have an SMAP entry then add one anyway,
			// mapped to the part declaration line.
			// Otherwise for some reason the stepIntos skip right over the rest of the function. Only do this if there is at
			// least one initializer statement.
			boolean hasInit = false;
			for (Field field : fields) {
				if (field.getInitializerStatements() != null) {
					hasInit = true;
					break;
				}
			}
			if (hasInit) {
				Annotation annotation = part.getAnnotation(IEGLConstants.EGL_LOCATION);
				if (annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null) {
					ctx.getSmapData().append("" + annotation.getValue(IEGLConstants.EGL_PARTLINE));
					if (ctx.getCurrentFile() != null) {
						if (ctx.getSmapFiles().indexOf(ctx.getCurrentFile()) < 0)
							ctx.getSmapFiles().add(ctx.getCurrentFile());
						ctx.getSmapData().append("#" + (ctx.getSmapFiles().indexOf(ctx.getCurrentFile()) + 1) + ":");
					} else {
						ctx.getSmapData().append("#1:");
					}
					ctx.getSmapData().append("" + out.getLineNumber() + "\n");
				}
			}
		}
		// now process the fields that don't have initializer statements
		for (Field field : fields) {
			if (field.getInitializerStatements() == null) {
				ctx.invoke(genInitializeMethod, part, ctx, out, field);
				ctx.writeSmapLine();
			}
		}
		// now process the fields that do have initializer statements
		for (Field field : fields) {
			if (field.getInitializerStatements() != null) {
				ctx.invoke(genInitializeMethod, part, ctx, out, field);
				ctx.writeSmapLine();
			}
		}
	}

	public void genInitializeMethod(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		if (!(arg instanceof ConstantField)) {
			ctx.invoke(genInitializeStatement, arg, ctx, out);
		}
	}

	public void genGetterSetters(EGLClass part, Context ctx, TabbedWriter out) {
		for (Field field : part.getFields()) {
			ctx.invoke(genGetterSetter, part, ctx, out, field);
			ctx.writeSmapLine();
		}
	}

	// we only generate getter and setters for fields within records or libraries, so do nothing if it gets back here
	public void genGetterSetter(EGLClass part, Context ctx, TabbedWriter out, Field arg) {}

	@SuppressWarnings("unchecked")
	public void genLibraryAccessMethods(EGLClass part, Context ctx, TabbedWriter out) {
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partLibrariesUsed);
		for (Library library : libraries) {
			ctx.invoke(genLibraryAccessMethod, part, ctx, out, library);
			ctx.writeSmapLine();
		}
	}

	public void genLibraryAccessMethod(EGLClass part, Context ctx, TabbedWriter out, Library arg) {
		out.print("public ");
		ctx.invoke(genRuntimeTypeName, (Type) arg, ctx, out, TypeNameKind.EGLImplementation);
		out.println(" " + Constants.LIBRARY_PREFIX + arg.getFullyQualifiedName().replace('.', '_') + "() {");
		out.println("if (" + Constants.LIBRARY_PREFIX + arg.getFullyQualifiedName().replace('.', '_') + " == null) {");
		out.print(Constants.LIBRARY_PREFIX + arg.getFullyQualifiedName().replace('.', '_') + " = (");
		ctx.invoke(genRuntimeTypeName, (Type) arg, ctx, out, TypeNameKind.EGLImplementation);
		out.print(")org.eclipse.edt.javart.Runtime.getRunUnit().loadLibrary(\"");
		if (ctx.mapsToNativeType(arg)) {
			out.print(ctx.getNativeMapping(arg.getFullyQualifiedName()));
		} else {
			out.print(CommonUtilities.fullClassAlias(arg));
		}
		out.println("\");");
		out.println("}");
		out.println("return " + Constants.LIBRARY_PREFIX + arg.getFullyQualifiedName().replace('.', '_') + ";");
		out.println("}");
	}

	public void genFunctions(EGLClass part, Context ctx, TabbedWriter out) {
		for (Function function : part.getFunctions()) {
			ctx.invoke(genFunction, part, ctx, out, function);
			ctx.writeSmapLine();
		}
	}

	public void genFunction(EGLClass part, Context ctx, TabbedWriter out, Function arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genAdditionalConstructorParams(EGLClass part, Context ctx, TabbedWriter out) {}

	public void genAdditionalSuperConstructorArgs(EGLClass part, Context ctx, TabbedWriter out) {}

	public void genDeclaration(EGLClass part, Context ctx, TabbedWriter out) {}

	public void genSuperClass(EGLClass part, Context ctx, TabbedWriter out) {}

	public Integer genFieldTypeClassName(EGLClass part, Context ctx, TabbedWriter out, Integer arrayDimensions) {
		ctx.invoke(genRuntimeTypeName, part, ctx, out, TypeNameKind.EGLImplementation);
		return arrayDimensions;
	}

}
