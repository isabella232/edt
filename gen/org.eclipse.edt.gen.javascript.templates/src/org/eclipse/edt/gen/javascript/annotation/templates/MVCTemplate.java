/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascript.annotation.templates;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.PatternType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class MVCTemplate extends JavaScriptTemplate {

	private static final String MVC_PACKAGE = "org.eclipse.edt.rui.mvc.";
	private static final String MVC_NAMESPACE = "egl."+MVC_PACKAGE;
	
	private static final String PROPERTY_INPUTREQUIRED = MVC_PACKAGE + "inputRequired";
//	private static final String PROPERTY_VALIDVALUESMSGKEY = MVC_PACKAGE + "validValuesMsgKey";
//	private static final String PROPERTY_VALIDVALUES = MVC_PACKAGE + "validValues";
	private static final String PROPERTY_INPUTREQUIREDMSGKEY = MVC_PACKAGE + "inputRequiredMsgKey";
	private static final String PROPERTY_ISHEXDIGIT = MVC_PACKAGE + "isHexDigit";
	private static final String PROPERTY_TYPECHKMSGKEY = MVC_PACKAGE + "typeChkMsgKey";
	private static final String PROPERTY_ISDECIMALDIGIT = MVC_PACKAGE + "isDecimalDigit";
	private static final String PROPERTY_MINIMUMINPUT = MVC_PACKAGE + "minimumInput";
	private static final String PROPERTY_MINIMUMINPUTMSGKEY = MVC_PACKAGE + "minimumInputMsgKey";
	
	private static final String PROPERTY_DATEFORMAT = MVC_PACKAGE + "dateFormat";
	private static final String PROPERTY_TIMEFORMAT = MVC_PACKAGE + "timeFormat";
	private static final String PROPERTY_UPPERCASE = MVC_PACKAGE + "upperCase";
	private static final String PROPERTY_LOWERCASE = MVC_PACKAGE + "lowerCase";
	private static final String PROPERTY_TIMESTAMPFORMAT = MVC_PACKAGE + "timeStampFormat";
	private static final String PROPERTY_ISBOOLEAN = MVC_PACKAGE + "isBoolean";
	private static final String PROPERTY_ZEROFORMAT = MVC_PACKAGE + "zeroFormat";
	private static final String PROPERTY_FILLCHARACTER = MVC_PACKAGE + "fillCharacter";
	private static final String PROPERTY_SIGN = MVC_PACKAGE + "sign";
	private static final String PROPERTY_CURRENCY = MVC_PACKAGE + "currency";
	private static final String PROPERTY_CURRENCYSYMBOL = MVC_PACKAGE + "currencySymbol";
	private static final String PROPERTY_NUMERICSEPARATOR = MVC_PACKAGE + "numericSeparator";
	private static final String MVC_VIEW_ANNOTATION = "eglx.ui.rui.MVCView";
	private static final String PROPERTY_VALIDATIONPROPERTIESLIBRARY = MVC_PACKAGE + "validationPropertiesLibrary";


	/**
	 * preGen
	 * 
	 * @param aType
	 * @param ctx
	 * @param annot
	 * @param field
	 */
	public void preGen(AnnotationType aType, Context ctx, Annotation annot, Field field) {
		if ("MVC".equals(annot.getEClass().getCaseSensitiveName())) {
			Expression model = (Expression)annot.getValue("model");
			Member modelMember = null;
			if ( model instanceof MemberName )
			{
				modelMember = ((MemberName)model).getMember();
			}
			else if ( model instanceof MemberAccess )
			{
				modelMember = ((MemberAccess)model).getMember();
			}
			else if ( model instanceof Field )
			{
				modelMember = (Field)model;
			}
			
			if (modelMember != null) {
				List<Part> implicitReferencedParts = getImplicitReferencedParts(modelMember, ctx);
				for (Part referencedPart : implicitReferencedParts) {
					boolean found = false;
					List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partLibrariesUsed);
					for (Library lib : libraries) {
						if (referencedPart.getTypeSignature().equalsIgnoreCase(lib.getTypeSignature())) {
							found = true;
							break;
						}
					}
					if (!found) {
						libraries.add((Library) referencedPart);
					}
				}
			}
		}
	}
	
	/**
	 * genAnnotation
	 * 
	 * @param aType
	 * @param ctx
	 * @param out
	 * @param annot
	 */
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot) {
	}
	
	
	/**
	 * genAnnotation
	 * 
	 * @param aType
	 * @param ctx
	 * @param out
	 * @param annot
	 * @param field
	 * @param contextTemplateMethod
	 */
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Field field, String contextTemplateMethod) {
		if ("MVC".equals(annot.getEClass().getCaseSensitiveName()) && genInitializeMethod.equals(contextTemplateMethod)) {
			if ( !field.isImplicit() /* NOGO sbg neither of these checks work as they should?!   && field.getType() instanceof Member && field.hasSetValuesBlock() */ ) {
				
				String controllerName  = field.getCaseSensitiveName();
				Expression view = (Expression)annot.getValue("view"); 
				Expression model = (Expression)annot.getValue("model"); 

				if (view == null || model == null) {
					// We need both.
					return;
				}
				
				List<String> validators = new ArrayList<String>(); // format: "context, func[, param1[, param2...]]"
				List<String> properties = new ArrayList<String>();
				Library library = null;
				
				Member modelMember = null;
				if ( model instanceof MemberName )
				{
					modelMember = ((MemberName)model).getMember();
				}
				else if ( model instanceof MemberAccess )
				{
					modelMember = ((MemberAccess)model).getMember();
				}
				else if ( model instanceof Field )
				{
					modelMember = (Field)model;
				}
	
				if (modelMember != null && (annot = getAnnotation(modelMember, PROPERTY_VALIDATIONPROPERTIESLIBRARY)) != null) {
					if (annot.getValue() instanceof PartName) {
						Part p = ((PartName) annot.getValue()).getPart();
						if (p instanceof Library) {
							library = (Library)p;
							
							ctx.invoke(genInstantiation, library, ctx, out);
							out.println(';');
						}
					}
				}
				
				String formatter = setFormatterProperties(ctx, modelMember, properties, false);
				if (formatter != null) {
					out.println("new egl." + formatter + "();");
					formatter = "egl." + formatter;
				}
				
				String validator = setValidators(ctx, library, modelMember, validators, false);
				if (validator != null) {
					out.println("new egl." + validator + "();");
				}
				ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
				out.println(".eze$$parent = this;");

/* NOGO sbg Not sure I need this, given the way I'm generating				
				// Add 'no this please' - we have a different qualifier.
				addNoThisPlease(model);
				addNoThisPlease(view);
*/				
				
				//function commitHelper(s String in) model = s; end
				genCommitHelper(ctx, model, controllerName, field, out);

				//function retrieveModelHelper() returns(String) return (model); end
				genRetrieveHelper(ctx, model, controllerName, field, out);
			
				
				//use mvcview annotation to determine getters and setters method of widgets.
				Type widgetView = view.getType();
				Annotation annoMvcView = widgetView.getAnnotation(MVC_VIEW_ANNOTATION);
				Name PublishMethodName = null;
				Name retrieveViewHelperMethodName = null;
				Name retrieveValidStateHelperMethodName = null;
				Name publishMessageHelperMethodName = null;
				Name showErrorStateName = null;
				
				if(null != annoMvcView){
					PublishMethodName = (Name) annoMvcView.getValue("publishHelper");			
					retrieveViewHelperMethodName = (Name) annoMvcView.getValue("retrieveViewHelper");
					retrieveValidStateHelperMethodName = (Name) annoMvcView.getValue("retrieveValidStateHelper");
					publishMessageHelperMethodName = (Name) annoMvcView.getValue("publishMessageHelper");
					showErrorStateName = (Name) annoMvcView.getValue("showErrorState");
				}

				//function publishHelper(s String in) view.setText(s);	end
				ctx.invoke(genQualifier, field, ctx, out); 	 ctx.invoke(genAccessor, field, ctx, out);
				out.print( ".publishHelper = new egl.egl.jsrt.Delegate(");
				ctx.invoke(genQualifier, field, ctx, out); 	 ctx.invoke(genAccessor, field, ctx, out);
				out.println(", function(/*String*/ s ) { try { " );
				genWithQualifier(ctx, view, out);

				if(null != annoMvcView && null != PublishMethodName){
					out.print(".");
					ctx.invoke(genName, PublishMethodName.getNamedElement(), ctx, out);  // NOGO sbg Verify with test;  may need to alias the qualifier
//					PublishMethodName.getMember().accept( context.getAliaser() );
					out.println("(s);" );
				}else{
					out.println(".setText(s);" );
				}
				genCatchBlock(ctx, "publishHelper", model, out);
				
				
				//function function retrieveViewHelper() returns(String) return (view.getText()); end
				ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
				out.print( ".retrieveViewHelper = new egl.egl.jsrt.Delegate(");
				ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
				out.println(", function() { try { " );
				out.print( "return " );
				
				genWithQualifier(ctx, view, out);

				//if the widget use annotation the retrieveViewHelper will be set by the annotation
				//else use getText
				if(null != annoMvcView && null != retrieveViewHelperMethodName){
					out.print(".");
					ctx.invoke(genName, retrieveViewHelperMethodName.getNamedElement(), ctx, out); // NOGO sbg Verify with test; possibly alias qualifier     retrieveViewHelperMethodName.getMember().accept( context.getAliaser() );
					out.println("();");
				}else{
					out.println(".getText();");
				}
				genCatchBlock(ctx, "retrieveViewHelper", model, out);
				
				
				//function retriveViewValidStateHelper() returns(string?) return (view.Annotation) end
				if(null != annoMvcView && null != retrieveValidStateHelperMethodName){
					ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
					out.print( ".retrieveValidStateHelper = new egl.egl.jsrt.Delegate(");
					ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
					out.println(", function() { try { " );
					out.print( "return " );
					genWithQualifier(ctx, view, out);			
					out.print( "." );
					ctx.invoke(genName, retrieveValidStateHelperMethodName.getNamedElement(), ctx, out);  // NOGO sbg Verify with test; possibly alias qualifier     retrieveValidStateHelperMethodName.getMember().accept( context.getAliaser() );
					out.println( "();" );
					genCatchBlock(ctx, "retrieveValidStateHelper", model, out);
				}

				
				//function publishViewMessageHelper(String in) view.Annotation end
				if(null != annoMvcView && null != publishMessageHelperMethodName){
					ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
					out.print( ".publishMessageHelper = new egl.egl.jsrt.Delegate(");
					ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
					out.println(", function(/*String*/ s ) { try { " );
					genWithQualifier(ctx, view, out);
					out.print(".");
					ctx.invoke(genName, publishMessageHelperMethodName.getNamedElement(), ctx, out); // NOGO sbg Verify with test; possibly alias qualifier     publishMessageHelperMethodName.getMember().accept(context.getAliaser());
					out.println( "(s);" );
					genCatchBlock(ctx, "publishMessageHelper", model, out);
				}
				
				//function showErrorState(Boolean in) end
				if (null != annoMvcView && null != showErrorStateName) {
					ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
					out.print( ".showErrorState = new egl.egl.jsrt.Delegate(");
					ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
					out.println(", function(/*Boolean*/ b ) { try { " );
					genWithQualifier(ctx, view, out);
					out.print(".");
					ctx.invoke(genName, showErrorStateName.getNamedElement(), ctx, out); // NOGO sbg Verify with test; possibly alias qualifier     publishMessageHelperMethodName.getMember().accept(context.getAliaser());
					out.println( "(b);" );
					genCatchBlock(ctx, "showErrorState", model, out);
				}
				
/* NOGO sbg Not sure I need this, given the way I'm generating				
				// Remove 'no this please' - setMode/View do need the qualifier.
				removeNoThisPlease(model);
				removeNoThisPlease(view);
*/
				
				//controller.model = model
				ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
				out.print( ".setModel(" );
				ctx.invoke(genExpression, model, ctx, out); 
				out.println( ");" );
				
				//controller.view = view
				ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
				out.print( ".setView(" );
				ctx.invoke(genExpression, view, ctx, out); 
				out.println( ");" );
				
				for (Iterator<String> it = validators.iterator(); it.hasNext();) {
					String var = ctx.nextTempName();
					out.println("var " + var + " = new "+MVC_NAMESPACE + "internal.ValidatorWrapper(" + it.next() + ");");
					ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
					out.println(".internalValidators.appendElement(new egl.egl.jsrt.Delegate(" + var + ", " + var + ".validate));");
				}

				if (formatter != null) {
					String var = ctx.nextTempName();
					out.print("var " + var + " = new "+MVC_NAMESPACE + "internal.FormatterWrapper(" + formatter + ".$inst");
					for (int i = 0; i < properties.size(); i++) {
						out.print(", " + (String)properties.get(i));
					}
					
					// We always pass the TypeChkMsgKey, even if unspecified.
					String msgKey = getValidationPropertiesLibraryString(ctx, modelMember, library, PROPERTY_TYPECHKMSGKEY);
					out.println(", " + msgKey + ");");
					
					ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
					out.println( ".internalFormatters.appendElement(new egl.egl.jsrt.Delegate(" + var + ", " + var + ".format));");
					ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
					out.println( ".internalUnformatters.appendElement(new egl.egl.jsrt.Delegate(" + var + ", " + var + ".unformat));");
				}
			}
		}
	}
	
	/**
	 * genRetrieveHelper
	 * 
	 * @param ctx
	 * @param model
	 * @param controllerName
	 * @param field
	 * @param out
	 */
	private void genRetrieveHelper(Context ctx, Expression model, String controllerName, Field field, TabbedWriter out) {
		ctx.invoke(genQualifier, field, ctx, out); 	ctx.invoke(genAccessor, field, ctx, out);
		out.print( ".retrieveModelHelper = new egl.egl.jsrt.Delegate(");
		ctx.invoke(genQualifier, field, ctx, out); 	ctx.invoke(genAccessor, field, ctx, out);
		out.println(", function() { try { " );
		
		ctx.putAttribute(ctx.getClass(), Constants.QUALIFIER_ALIAS, "this.eze$$parent."); // NOGO sbg Should probably be more targeted
		out.print("return ");
		
		// retriever returns a non-null string. Null values cannot be converted to non-nullable strings, jsgen will throw a NullValueException.
		// Do manual checking for null and return blank if so.
		if (model.isNullable()) {
			ctx.invoke(genExpression, model, ctx, out);
			out.print(" == null ? \"\" : ");
		}
		
		if (!TypeUtils.Type_STRING.equals(model.getType().getClassifier())) {
			AsExpression asExpr;
			if (TypeUtils.Type_TIMESTAMP.equals(model.getType().getClassifier())) {
				// We want it to use the full, default pattern, not what's defined on the field.
				Expression withoutPattern = (Expression)model.clone();
				if (withoutPattern instanceof Field) {
					((Field)withoutPattern).setType(withoutPattern.getType().getClassifier());
				}
				else if (withoutPattern instanceof MemberName) {
					((MemberName)withoutPattern).getMember().setType(withoutPattern.getType().getClassifier());
				}
				else if (withoutPattern instanceof MemberAccess) {
					((MemberAccess)withoutPattern).getMember().setType(withoutPattern.getType().getClassifier());
				}
				asExpr = IRUtils.createAsExpression(withoutPattern, TypeUtils.Type_STRING);
			}
			else {
				asExpr = IRUtils.createAsExpression(model, TypeUtils.Type_STRING);
			}
			ctx.invoke(genExpression, asExpr, ctx, out);
		}
		else {
			ctx.invoke(genExpression, model, ctx, out);
		}
		ctx.putAttribute(ctx.getClass(), Constants.QUALIFIER_ALIAS, null);   // NOGO sbg Should probably be more targeted

		
		out.println(";");
		genCatchBlock(ctx, "retrieveModelHelper", model, out);
	}

	/**
	 * genCommitHelper
	 * 
	 * @param ctx
	 * @param model
	 * @param controllerName
	 * @param field
	 * @param out
	 */
	private void genCommitHelper(Context ctx, Expression model, String controllerName, Field field, TabbedWriter out) {
		ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
		out.print(".commitHelper = new egl.egl.jsrt.Delegate(");
		ctx.invoke(genQualifier, field, ctx, out);	ctx.invoke(genAccessor, field, ctx, out);
		out.println(", function(/*String*/ s ) { try { " );
		genWithQualifier(ctx, model, out);
		out.print( " = " );
		
		Type type = model.getType();
		Type classifier = type.getClassifier();
		if (TypeUtils.Type_BOOLEAN.equals(classifier)) {
			out.print("typeof s === \"string\" ? s.toUpperCase() == \"TRUE\" : ");
		}
		else if (TypeUtils.Type_DATE.equals(classifier)) {
			// Special case code for DATE/TIME/TIMESTAMP when the input is blank.
			// If nullable, assign null, else assign current date/time/timestamp.
			// If we were to let the blank value be assigned via dateValue() and
			// the like, a runtime error would occur.
			out.print( "s == \"\" ? " );
			if (model.isNullable()) {
				out.print( "null" );
			}
			else {
				out.print("new Date()");
			}
			out.print( " : " );
		}		
		else if (TypeUtils.Type_TIME.equals(classifier)) {
			out.print( "s == \"\" ? " );
			if (model.isNullable()) {
				out.print( "null" );
			}
			else {
				out.print("new Time()");
			}
			out.print( " : " ); 
		}
		else if (TypeUtils.Type_TIMESTAMP.equals(classifier)) {
			out.print( "s == \"\" ? " );
			if (model.isNullable()) {
				out.print( "null" );
			}
			else {
				ctx.invoke(genDefaultValue, type, ctx, out);
			}
			out.print( " : " );
		}
		else if (TypeUtils.Type_STRING.equals(classifier)) {
			// If blank input and model is null, let it remain null.
			if (model.isNullable()) {
				out.print( "s == \"\" && " );
				ctx.invoke(genExpression, model, ctx, out); //genWithQualifier(model, eg);
				out.print( " == null ? null : " );
			}
		}
		else {
			if (model.isNullable()) {
				out.print( "s == \"\" ? null : " );
			}
		}
			
		Field field2 = factory.createField();
		field2.setName("s");
		field2.setType(TypeUtils.Type_STRING);
		field2.setIsNullable(true);
		MemberName nameExpression = factory.createMemberName();
		nameExpression.setMember(field2);
		nameExpression.setId(field2.getCaseSensitiveName());
	
		if (!nameExpression.getType().equals(type)) {
			// Timestamp needs to use the classifier to match what's expected by the internal formatter.
			Type asType = type;
			if (TypeUtils.Type_TIMESTAMP.equals(classifier)) {
				asType = classifier;
			}
			
			AsExpression asExpr = IRUtils.createAsExpression(nameExpression, asType);
			ctx.invoke(genExpression, asExpr, ctx, out);
		}
		else {
			ctx.invoke(genExpression, nameExpression, ctx, out);
		}
		out.println( ";" );
		genCatchBlock(ctx, "commitHelper", model, out);
	}
	
	/**
	 * genCatchBlock
	 * 
	 * @param ctx
	 * @param location
	 * @param model
	 * @param out
	 */
	private void genCatchBlock(Context ctx, String location, Expression model, TabbedWriter out){
		out.print( "} catch(e) { throw egl.createRuntimeException( \"CRRUI2002E\", [ \""+location+" ");
		ctx.invoke(genExpression, model, ctx, out);
		out.println("\", e.message, egl.lastFunctionEntered ] ); }; });" );
	}
	
	
	/**
	 * Generates 'x' and its required qualifier. If it's defined in the current
	 * function container, we generate 'this.eze$$parent.' as the qualifier,
	 * otherwise the ExpressionGenerator will generate any required
	 * qualifiers (such as for fields within Libraries).
	 *
	 * @param x
	 */
	private void genWithQualifier(Context ctx, Expression x, TabbedWriter out)
	{
		Expression expr = x;
		while (expr instanceof MemberAccess) {
			expr = ((MemberAccess)expr).getQualifier();
		}
		
		boolean genExpr = true;
		if (expr instanceof MemberName){
			Part currentPart = (Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated);
			Container cnr = ((MemberName) expr).getMember().getContainer();
			if ((cnr != null) && (cnr instanceof Part)
			  && ( ((Part)cnr).getFullyQualifiedName().equals(currentPart.getFullyQualifiedName()))){
				// Change qualifier from "this." to "this.eze$$parent."
				Object saved = ctx.getAttribute(ctx.getClass(), Constants.QUALIFIER_ALIAS);
				ctx.putAttribute(ctx.getClass(), Constants.QUALIFIER_ALIAS, "this.eze$$parent.");
				ctx.invoke(genExpression, x, ctx, out);
				ctx.putAttribute(ctx.getClass(), Constants.QUALIFIER_ALIAS, saved);
				genExpr = false;
			}
		}
		
		if (genExpr)
		{
			ctx.invoke(genExpression, x, ctx, out);
		}
	}
	

	/**
	 * Get the annotation for a property for a field.  
	 * If the field is a fixed or flexible record item, the
	 * field argument should be a WrapperedField.  In this
	 * case, this method will call getAnnotationOrPropertyOverride
	 * to get the annotation from the property override, if one
	 * exists.  If it does not exist, this method will return
	 * the annotation from the field itself.  For other kinds of
	 * fields, there is no property override so this methods will
	 * get the annotation for the property directly from the field.
	 * 
	 * @param field		The field from which to get the annotation
	 * 					Should be a WrapperedField for fixed/flex rec items
	 * @param property	The property to look for
	 * @return			The annotation (if ones exists )from the property 
	 * 					override or	from the field 
	 */
	public static Annotation getAnnotation( Member field, String property )
	{
		Annotation annot = null;
/* TODO sbg Skipping WrapperedField for now
 		if ( field instanceof WrapperedField )
		{
			annot = getAnnotationOrPropertyOverride( (WrapperedField)field, property );
		}
 */
		if ( annot == null )
		{
			annot = field.getAnnotation( property );
		}
		return annot;
	}
	
	
	/**
	 * @param fastCheck  Flag to indicate we just want to know the validation library, if any.
	 *                   Should not be set to true when generating the field's declaration. Prevents
	 *                   slowing down the generator or duplicating code.
	 */
	private static String setValidators(Context ctx, Library library, Member modelMember, List<String> validators, boolean fastCheck) {
		if (modelMember == null) {
			return null;
		}
		
		Annotation annot;
		boolean usedValidationLib = false;
		
		if ((annot = getAnnotation(modelMember, PROPERTY_INPUTREQUIRED)) != null
				&& ((Boolean)annot.getValue()).booleanValue()) {
			usedValidationLib = true;
			
			if (!fastCheck) {
				String validationNLSLibrary = getValidationPropertiesLibraryString(ctx, modelMember, library, PROPERTY_INPUTREQUIREDMSGKEY);
				validators.add(MVC_NAMESPACE + "InternalValidators.$inst, "+MVC_NAMESPACE + "InternalValidators.$inst.isNotEmpty, "
								+ validationNLSLibrary);
			}
		}
		
		if (isCharacterType(modelMember) && (annot = getAnnotation(modelMember, PROPERTY_ISHEXDIGIT)) != null
				&& ((Boolean)annot.getValue()).booleanValue()) {
			usedValidationLib = true;
			
			if (!fastCheck) {
				String validationNLSLibrary = getValidationPropertiesLibraryString(ctx, modelMember, library, PROPERTY_TYPECHKMSGKEY);
				
				validators.add(MVC_NAMESPACE + "InternalValidators.$inst, "+MVC_NAMESPACE + "InternalValidators.$inst.isHexDigits, " + validationNLSLibrary);
			}
		}
		
		if (isCharacterType(modelMember) && (annot = getAnnotation(modelMember, PROPERTY_ISDECIMALDIGIT)) != null
				&& ((Boolean)annot.getValue()).booleanValue()) {
			usedValidationLib = true;
			
			if (!fastCheck) {
				String validationNLSLibrary = getValidationPropertiesLibraryString(ctx, modelMember, library, PROPERTY_TYPECHKMSGKEY);
				validators.add(MVC_NAMESPACE + "InternalValidators.$inst, "+MVC_NAMESPACE + "InternalValidators.$inst.isDecimalDigits, " + validationNLSLibrary);
			}
		}
		
		if ((annot = getAnnotation(modelMember, PROPERTY_MINIMUMINPUT)) != null) {
			int minInput = ((Integer)annot.getValue()).intValue();
			if (minInput > 0) {
				usedValidationLib = true;
				
				if (!fastCheck) {
					String validationNLSLibrary = getValidationPropertiesLibraryString(ctx, modelMember, library, PROPERTY_MINIMUMINPUTMSGKEY);
					validators.add(MVC_NAMESPACE + "InternalValidators.$inst, "+MVC_NAMESPACE + "InternalValidators.$inst.checkMinimumInput, " + minInput + ", " + validationNLSLibrary);
				}
			}
		}
		
		//TODO valid values not currently supported
//		if ((annot = getAnnotation(modelMember, PROPERTY_VALIDVALUES)) != null) {
//			usedValidationLib = true;
//			if (!fastCheck) {
//				// Convert validvalues to any[][].
//				IValidValuesElement[] values = (IValidValuesElement[])annot.getValue();
//				List<Expression> entries = new ArrayList<Expression>(values.length);
//				
//				for (int i = 0; values != null && i < values.length; i++) {
//					IValidValuesElement next = (IValidValuesElement)values[i];
//					List<Expression> subentries = new ArrayList<Expression>();
//					
//					if (next.isRange()) {
//						IValidValuesElement[] rangeElements = next.getRangeElements();
//						subentries.add(literalForValidValue( rangeElements[0] ));
//						subentries.add(literalForValidValue( rangeElements[1] ));
//					}
//					else {
//						subentries.add(literalForValidValue(next));
//					}
//					ArrayLiteral arrayLiteral = IrFactory.INSTANCE.createArrayLiteral();
//					arrayLiteral.getEntries().addAll(subentries);
//					entries.add(i, arrayLiteral);
//				}
//				ArrayLiteral literal = IrFactory.INSTANCE.createArrayLiteral();
//				literal.getEntries().addAll(entries);
//				
//				String literalValue = generateMemberToString(ctx, literal);
//				String validationNLSLibrary = getValidationPropertiesLibraryString(ctx, modelMember, library, PROPERTY_VALIDVALUESMSGKEY);
//				validators.add(MVC_NAMESPACE + "InternalValidators.$inst, "+MVC_NAMESPACE + "InternalValidators.$inst.checkValidValues, " + literalValue + ", " + validationNLSLibrary);
//			}
//		}
		
		return usedValidationLib ? MVC_PACKAGE + "InternalValidators" : null;
	}

	
	/**
	 * generateMemberToString
	 * 
	 * @param context
	 * @param member
	 * @return
	 */
	private static String generateMemberToString(Context context, Expression member) {
		StringWriter sw = new StringWriter(100);
		TabbedWriter tw = new TabbedWriter(sw);
		context.invoke(genExpression, member, context, tw);
		tw.close();
		return sw.toString().trim();
	}

	/**
	 * generateMemberToString
	 * 
	 * @param m
	 * @return
	 */
	private static boolean isCharacterType( Member m )
	{
		if ( m instanceof TypedElement )
		{
			Type type = ((TypedElement)m).getType();  
			return (TypeUtils.isTextType(type));
		}
		return false;
	}
	
	
	/**
	 * getValidationPropertiesLibraryString
	 * 
	 * @param ctx
	 * @param field
	 * @param nlsLibrary
	 * @param msgKey
	 * @return
	 */
	private static String getValidationPropertiesLibraryString(Context ctx, Member field, Library nlsLibrary, String msgKey) 
	{
		String returnValue = "\"\"";
		if (nlsLibrary != null) {
			Annotation annot = getAnnotation(field, msgKey);
			if (annot != null) {
				String memberName = (String)annot.getValue();
				
				Field f = nlsLibrary.getField(memberName);
				if (f != null) {
					String libName = generateAliasToString(ctx, nlsLibrary);
					returnValue = libName + ".getMessage(" + libName + ".eze$$getValueWithNormalizedKey(\""
							+ memberName.toLowerCase() + "\") " + ", [].setType( \"[S;\" ))";
				}
			}
		}
		
		return returnValue;
	}
	
	
	/**
	 * generateAliasToString
	 * 
	 * @param context
	 * @param member
	 * @return
	 */
	private static String generateAliasToString(Context context, Element member) {
		StringWriter sw = new StringWriter(100);
		TabbedWriter tw = new TabbedWriter(sw);
		context.invoke(genAccessor, member, context, tw);
		tw.close();
		return sw.toString().trim();
	}


	
//	/**
//	 * literalForValidValue
//	 * 
//	 * @param value
//	 * @return
//	 */
//	private static Literal literalForValidValue(IValidValuesElement value) {
//		Literal result = null;
//		if (value.isString()) {
//			StringLiteral literal = IrFactory.INSTANCE.createStringLiteral(); 
//			literal.setValue(value.getStringValue());
//			result = literal;
//		}
//		if (value.isInt()) {
//			IntegerLiteral literal = IrFactory.INSTANCE.createIntegerLiteral();
//			literal.setType(IRUtils.getEGLPrimitiveType(MofConversion.Type_Int));
//			literal.setValue(String.valueOf(value.getLongValue()));
//			result = literal;
//		}
//		if (value.isFloat()) {
//			FloatingPointLiteral literal = IrFactory.INSTANCE.createFloatingPointLiteral();
//			literal.setType(IRUtils.getEGLPrimitiveType(MofConversion.Type_Float));
//			literal.setValue(String.valueOf(value.getFloatValue()));
//			result = literal;
//		}
//		return result;
//	}

	/**
	 * @param member      The field.
	 * @param properties  List to store the properties.
	 * @param fastCheck  Flag to indicate we just want to know the formatting library, if any.
	 *                   Should not be set to true when generating the field's declaration. Prevents
	 *                   slowing down the generator or duplicating code.
	 * @return the formatter library (its generated javascript class). If no properties were
	 *         specified, the formatter will be null.
	 */
	private static String setFormatterProperties(Context ctx, Member member, List<String> properties, boolean fastCheck) {
		if (member == null) {
			return null;
		}
		
		if (member instanceof TypedElement) {
			Type type = ((TypedElement)member).getType();
			if (TypeUtils.Type_STRING.equals(type.getClassifier())) {
				return setCharacterFormatterProperties(ctx, member, properties, fastCheck );
			}
			else if ((TypeUtils.Type_DATE.equals(type))) {
				return setDateFormatterProperties(ctx, member, properties, fastCheck );
			}
			else if ((TypeUtils.Type_TIME.equals(type))) {
				return setTimeFormatterProperties(ctx, member, properties, fastCheck );
			}
			else if ((TypeUtils.Type_TIMESTAMP.equals(type.getClassifier()))) {
				return setTimestampFormatterProperties(ctx, member, properties, fastCheck );
			}
			else if (TypeUtils.isNumericType(type)) {
				return setNumericFormatterProperties(ctx, member, properties, fastCheck );
			}
		}
		return null;
	}
	
	
	/**
	 * @param fastCheck  Flag to indicate we just want to know the formatting library, if any.
	 *                   Should not be set to true when generating the field's declaration. Prevents
	 *                   slowing down the generator or duplicating code.
	 */
	private static String setCharacterFormatterProperties(Context ctx, Member member, List<String> properties, boolean fastCheck) {
		Annotation annot;
		boolean hasExplicitPropertySet = false;
		
		if ((annot = getAnnotation(member, PROPERTY_DATEFORMAT)) != null) {
			hasExplicitPropertySet = true;
			if (!fastCheck) {
				properties.add(getDatetimeFormat(ctx, annot.getValue()));
			}
		}
		else {
			if (!fastCheck) {
				properties.add("\"\"");
			}
		}
		
		if ((annot = getAnnotation(member, PROPERTY_TIMEFORMAT)) != null) {
			hasExplicitPropertySet = true;
			if (!fastCheck) {
				properties.add(getDatetimeFormat(ctx, annot.getValue()));
			}
		}
		else {
			if (!fastCheck) {
				properties.add("\"\"");
			}
		}
		
		if ((annot = getAnnotation(member, PROPERTY_UPPERCASE)) != null) {
			hasExplicitPropertySet = true;
			if (!fastCheck) {
				if (((Boolean)annot.getValue()).booleanValue()) {
					properties.add("true");
				}
				else {
					properties.add("false");
				}
			}
		}
		else {
			if (!fastCheck) {
				properties.add("false");
			}
		}
		
		if ((annot = getAnnotation(member, PROPERTY_LOWERCASE)) != null) {
			hasExplicitPropertySet = true;
			if (!fastCheck) {
				if (((Boolean)annot.getValue()).booleanValue()) {
					properties.add("true");
				}
				else {
					properties.add("false");
				}
			}
		}
		else {
			if (!fastCheck) {
				properties.add("false");
			}
		}
		
		return hasExplicitPropertySet ? MVC_PACKAGE + "InternalCharFormatter" : null;
	}
	
	/**
	 * @param fastCheck  Flag to indicate we just want to know the formatting library, if any.
	 *                   Should not be set to true when generating the field's declaration. Prevents
	 *                   slowing down the generator or duplicating code.
	 */
	private static String setDateFormatterProperties(Context ctx, Member member, List<String> properties, boolean fastCheck) {
		if (!fastCheck) {
			Annotation annot;
			if ((annot = getAnnotation(member, PROPERTY_DATEFORMAT)) != null) {
				properties.add(getDatetimeFormat(ctx, annot.getValue()));
			}
			else {
				properties.add("\"\"");
			}
		}
		
		return MVC_PACKAGE + "InternalDateFormatter";
	}
	
	private static String getDatetimeFormat(Context ctx, Object annotValue) {
		if (annotValue instanceof MemberAccess) {
			// strlib.defaultTimeFormat, defaultDateFormat, defaultTimestampFormat
			return generateMemberToString(ctx, (MemberAccess)annotValue);
		}
		else {
			// some literal or field reference
			String name = "";
			if (annotValue instanceof Name)
			{
				name = ((Name)annotValue).getId();
			}
			else if (annotValue instanceof String)
			{
				name = (String)annotValue;
			}
			
			return  "\"" + name + "\"";
		}
	}


	/**
	 * @param fastCheck  Flag to indicate we just want to know the formatting library, if any.
	 *                   Should not be set to true when generating the field's declaration. Prevents
	 *                   slowing down the generator or duplicating code.
	 */
	private static String setTimeFormatterProperties(Context ctx, Member member, List<String> properties, boolean fastCheck) {
		if (!fastCheck) {
			Annotation annot;
			if ((annot = getAnnotation(member, PROPERTY_TIMEFORMAT)) != null) {
				properties.add(getDatetimeFormat(ctx, annot.getValue()));
			}
			else {
				properties.add("\"\"");
			}
		}
		
		return MVC_PACKAGE + "InternalTimeFormatter";
	}
	
	/**
	 * @param fastCheck  Flag to indicate we just want to know the formatting library, if any.
	 *                   Should not be set to true when generating the field's declaration. Prevents
	 *                   slowing down the generator or duplicating code.
	 */
	private static String setTimestampFormatterProperties(Context ctx, Member member, List<String> properties, boolean fastCheck) {
		if (!fastCheck) {
			Annotation annot;
			if ((annot = getAnnotation(member, PROPERTY_TIMESTAMPFORMAT)) != null) {
				properties.add(getDatetimeFormat(ctx, annot.getValue()));
			}
			else {
				Type type = member.getType();
				String pattern = null;
				if (type instanceof PatternType) {
					pattern = ((PatternType)type).getPattern();
				}
				properties.add(pattern == null ? "\"\"" : "egl.formatTimeStampPattern(\"" + pattern + "\")");
			}
		}
		
		return MVC_PACKAGE + "InternalTimestampFormatter";
	}
	
	/**
	 * @param fastCheck  Flag to indicate we just want to know the formatting library, if any.
	 *                   Should not be set to true when generating the field's declaration. Prevents
	 *                   slowing down the generator or duplicating code.
	 */
	private static String setNumericFormatterProperties(Context ctx, Member member, List<String> properties, boolean fastCheck) {
		Type rootType = member instanceof TypedElement ? ((TypedElement)member).getType() : null;
/* TODO sbg !070		boolean isMoney = rootType != null && rootType.getTypeKind() == Type.MONEY; */
		Annotation annot;
		
		if (!fastCheck) {
			if ((annot = getAnnotation(member, PROPERTY_ISBOOLEAN)) != null) {
				if (((Boolean)(annot.getValue())).booleanValue()) {
					properties.add("true");
				}
				else {
					properties.add("false");
				}
			}
			else {
				properties.add("false");
			}
			
			if ((annot = getAnnotation(member, PROPERTY_ZEROFORMAT)) != null) {
				if (((Boolean)(annot.getValue())).booleanValue()) {
					properties.add("true");
				}
				else {
					properties.add("false");
				}
			}
			/* TODO sbg !070			else if (isMoney) {
				// Money defaults to true
				properties.add("true");
			} */
			else {
				properties.add("false");
			}
			
			if ((annot = getAnnotation(member, PROPERTY_FILLCHARACTER)) != null) {
				properties.add("\"" + (String)annot.getValue() + "\"");
			}
			else {
				// blank is the default for numbers
				properties.add("\" \"");
			}
			
			String signkind = "LEADING";  // signkind.leading is the default
			if ((annot = getAnnotation(member, PROPERTY_SIGN)) != null) {
				Object value = annot.getValue();
				if (value instanceof EnumerationEntry) {
					signkind = ((EnumerationEntry)value).getCaseSensitiveName();
				}
			}
			PartName pn = IrFactory.INSTANCE.createPartName();
			pn.setPackageName("eglx.ui");
			pn.setId("signkind");
			MemberAccess access = IrFactory.INSTANCE.createMemberAccess();
			access.setId(signkind);  
			access.setQualifier(pn);
			properties.add(generateMemberToString(ctx, access));
			
			
			if ((annot = getAnnotation(member, PROPERTY_CURRENCY)) != null) {
				if (((Boolean)(annot.getValue())).booleanValue()) {
					properties.add("true");
				}
				else {
					properties.add("false");
				}
			}
			/* TODO sbg !070			else if (isMoney) {
				// Money defaults to true
				properties.add("true");
			} */
			else {
				properties.add("false");
			}
			
			if ((annot = getAnnotation(member, PROPERTY_CURRENCYSYMBOL)) != null) {
				properties.add("\"" + (String)annot.getValue() + "\"");
			}
			else {
				properties.add("\"\"");
			}
			
			if ((annot = getAnnotation(member, PROPERTY_NUMERICSEPARATOR)) != null) {
				if (((Boolean)(annot.getValue())).booleanValue()) {
					properties.add("true");
				}
				else {
					properties.add("false");
				}
			}
/* TODO sbg !070			else if (isMoney) {
				// Money defaults to true
				properties.add("true");
			} */
			else {
				properties.add("false");
			}
			
			// Length, decimals, dataitem type
			if (rootType instanceof FixedPrecisionType) {
				properties.add("" + ((FixedPrecisionType)rootType).getLength());
				properties.add("" + ((FixedPrecisionType)rootType).getDecimals());
			}
			else {
				// FLOAT and SMALLFLOAT
				properties.add("0");
				properties.add("0");
			}
			properties.add("\"" + rootType.getTypeSignature() + "\"");
		}
		
		return MVC_PACKAGE + "InternalNumericFormatter";
	}
	
	/**
	 * getImplicitReferencedParts
	 * 
	 * @param field
	 * @param context
	 * @return
	 */
	public static List<Part> getImplicitReferencedParts(Member field, Context context) {
		List<Part> parts = new ArrayList<Part>();
		List<String> dummyList = new ArrayList<String>();
		
		String formatter = setFormatterProperties( context, field, dummyList, true );
		if (formatter != null) {
			Part part = findInternalPart(context, formatter);
			if (part != null) {
				parts.add(part);
			}
		}
		
		String validator = setValidators(context, null, field, dummyList, true );
		if (validator != null) {
			Part part = findInternalPart(context, validator);
			if (part != null) {
				parts.add(part);
			}
		}
		
		return parts;
	}
	
	
	/**
	 * findInternalPart
	 * 
	 * @param ctx
	 * @param qualifiedPartName
	 * @return
	 */
	private static Part findInternalPart(Context ctx, String qualifiedPartName) {
		return (Part)IRUtils.getEGLType(qualifiedPartName);
	}
}
