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
package org.eclipse.edt.gen.generator.example;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.gen.generator.java.EGL2Java;
import org.eclipse.edt.gen.java.JavaGenerator;

public class EGL2Example extends EGL2Java {

	// this is an example on how to extend the distributed EGL2Java command. As part of this example, we will also show how
	// to extend 3 items:
	// 1) the EGL assignment statement, which we will add a comment to the beginning of the line
	// 2) the EGL type INT, which we will add a comment to the beginning of the definition
	// 3) the EGL system function writeStdOut, which we will add a comment to the beginning of its usage
	//
	// then we will add a new system type, implemented as a java library
	//
	// of course, the above are very basic examples of extending the language, but should provide at least enough information
	// to do the job
	//
	// let's begin by first extending the distributed command processor (EGL2Java) and making our own (EGL2Example) command
	// processor that has added an additional command processor parameter (-extendComments) which takes a value of true or
	// yes, or false or no. This new parameter will be accessed in the 3 extensions we are making, and if true, will cause
	// the comments we are adding to come out
	//
	// the generation command will become this:
	// EGL2Example -o C:\myExample -r C:\myExample -p myExampleProgram -extendComments true
	//
	// we need to first create the EGL2Example (of course it can be called anything you desire) command processor. This is
	// the example of that. You will notice that it needs to extend the existing command processor you wish to use as a base.
	// In this example, we are extending EGL2Java, so the above class definition for EGL2Example needs to extend EGL2Java,
	// and EGL2Java needs to be on the classpath for this command processor

	public EGL2Example() {
		// bring in the EGL2Java's parameters first
		super();
		// define our additional parameters here. In this case we want to add a boolean type of parameter. Its name will be
		// extendComments and we will allow a short form (ec). These parameter names are always case insensitive. Its default
		// will be false (the 1st entry in the accepted values list { false, true }).
		this.installParameter(false, Constants.parameter_extendComments, new String[] { "extendcomments", "ec" }, new Boolean[] { false, true },
			"ExtendComments must be defined as true or false");
		//
		// the installParameter method defines each parameter that is accepted and used by the command processor.
		//
		// The first argument (false/true) defines whether the parameter must be provided or not. If specified as true, and
		// the parameter is not given by the user on the command line, an error is given and the command processor does not
		// start up the generation.
		//
		// The second parameter defines the internal name of the parameter. This has nothing to do with what the user
		// specifies on the command line, but instead represents the name of the parameter in any java code that you program,
		// when you wish to interrogate the parameter's value. For example, to get a value you would code this:
		// (when the parameter has been defined as a boolean type)
		// (Boolean) ctx.getParameter(Constants.parameter_checkOverflow)
		// (when the parameter has been defined as a string type)
		// (String) ctx.getParameter(Constants.parameter_part)
		// (when the parameter has been defined as a object type)
		// (String[]) ctx.getParameter(Constants.parameter_locations)
		//
		// The third parameter defines a String array of accepted aliases for the parameter. At least 1 alias must be given
		// and any number of them may be specified. Each alias must be unique in the environment, so keep in mind that you
		// are also including the ones defined by the generator you are extending (EGL2Java). These aliases are always case
		// insensitive. A hyphen is always used on the command line to access the parameter and set its value, however the
		// hyphen is never defined in the String array's definition.
		//
		// The fourth parameter defines the type of parameter this is, along with its default value. The 1st entry in the
		// definition defines the default. The entries also define acceptible values that can be specified, where null means
		// any value. There are 3 types of parameters:
		// 1) boolean type parameters accept a value of true or yes, false or no. If the user specifies the parameter, but
		// not a value, then they get a default value of true.
		// These are examples of specifying a boolean parameter on the command line. The first 3 all define the parameter to
		// be true, internally within the code.
		// -xyz
		// -xyz true
		// -xyz yes
		// -xyz false
		// -xyz no
		//
		// 2) string type parameters accept a single string value. This value is provided after the
		// parameter in the command line. If spaces are allowed, then the parameter's value must be in quotes.
		// For example, you wish to make a parameter that is required (1st argument), and can only be a value of here, there
		// or anywhere. The parameter's name would be location. This would be the code to define the parameter:
		// this.installParameter(true, Constants.parameter_location, new String[] { "location" }, new String[] { "here",
		// "there", "anywhere" }, "Location must identify the location and be one of here, there or anywhere");
		// For example, you wish to make a parameter that is not required (1st argument), and can be any value. The
		// parameter's name would be place. This would be the code to define the parameter:
		// this.installParameter(false, Constants.parameter_place, new String[] { "place" }, new String[] { null },
		// "Place must identify the place");
		// For example, you wish to make a parameter that is not required (1st argument), and can be any value, but has a
		// default value of apple. The parameter's name would be fruit. This would be the code to define the parameter:
		// this.installParameter(false, Constants.parameter_fruit, new String[] { "fruit" }, new String[] { "apple", null },
		// "Fruit must identify the name of the fruit");
		// These are examples of specifying a string parameter on the command line.
		// -xyz hello
		// -xyz "good day"
		//
		// 3) object type parameters accept 0 or more string values from the command line. The value returned to the java
		// code will be a String[] of 0 or more entries. This is useful for getting a list from the command line.
		// For example: -colors green red purple blue
		// The java call for the above parameter would return a String[] of 4 elements.
		// this.installParameter(true, Constants.parameter_colors, new String[] { "colors", "color" }, new Object[] { null },
		// "Colors must identify the colors to be used");
		// Spaces can be imbedded as well, by using quotes. For example: -xyz "monkey see" "monkey do"
		// The java call for the above parameter would return a String[] of 2 elements.
		//
		// The fifth parameter defines the comment that is to be printed, if the user enters a ? instead of a - in front of
		// the parameter. If any ? is entered, then no generation occurs, but simply the printing of the comment is given.
		// EGL2Example ?
		// The above command will print out all possible parameter comments.
		// EGL2Example ?colors
		// The above command will print out only the parameter comment for the colors parameter.
		// EGL2Example ?colors ?part
		// The above command will print out only the parameter comments for the colors parameter and the part parameter.
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options. The new EGL2Example() call will cause
		// the constructor for this class to get executed, and it will invoke the super() method call to set up the EGL2Java
		// defaults. The generate(args) method call will pass the command line arguments along, and set the user's
		// values based on the parameters that have been defined. Once the parameters have been analyzed and accepted,
		// generation can begin. The new JavaGenerator(genPart) defines the generator to use. You can use the default one, or
		// provide your own generator that extends the default.
		EGL2Example genPart = new EGL2Example();
		genPart.generate(args, new JavaGenerator(genPart), null, null);
	}

	public String[] getNativeTypePath() {
		// as we are going to add our own system type, implemented as a java library, we need to provide the details in the
		// nativeTypes.properties file located in our directory. To tell the command processor about it, we need to add the
		// properties file to the nativeTypes list. If we were adding a new base type, or overriding an existing one, this is
		// where we would do that change as well.
		List<String> nativeTypes = new ArrayList<String>();
		// In this example, we stated that we are going to override 3 EGL2Java templates (AssignmentStatement, INT type and
		// system function writeStdOut). These 3 templates are defined in 3 different areas in EGL2Java and we could mirror
		// that and provide 3 different EGL2Example template file locations, if we wanted to. However, there is no reason to
		// do that, other than to keep things organized in the same fashion as EGL2Java does. We could just place all 3
		// overriding definitions in a single EGL2Example template directory instead, and that is what we are going to do.
		// So, let's only define 1 overriding template located in our current project. The name defines the location and the
		// last .example defines the template file's name (.properties is automatically added). This file can be called
		// anything you like.
		nativeTypes.add("org.eclipse.edt.gen.generator.example.nativeTypes");
		// Now add the ones we are extending
		String[] others = super.getNativeTypePath();
		for (String other : others) {
			nativeTypes.add(other);
		}
		return (String[]) nativeTypes.toArray(new String[nativeTypes.size()]);
	}

	// we could also extend the primitives defined in the command processor here, but as this example doesn't need to, we
	// will not add any code. This code, if it was used, would be very similar to the getNativeTypePath logic.
	// public String[] getPrimitiveTypePath() {
	//		
	// }

	// we could also extend the EGLMessages defined in the command processor here, but as this example doesn't need to, we
	// will not add any code. This code, if it was used, would be very similar to the getNativeTypePath logic.
	// public String[] getEGLMessagePath() {
	//		
	// }

	public String[] getTemplatePath() {
		// This method provides an array of template locations. First we need to define our templates and then pick up the
		// ones defined by the command processor we are extending. The reason we need to define ours first, is because the
		// order is important. Only the 1st definition for a template is accepted. If we are overriding some of the ones
		// defined by the generator we are extending, then ours must come first.
		List<String> templates = new ArrayList<String>();
		// In this example, we stated that we are going to override 3 EGL2Java templates (AssignmentStatement, INT type and
		// system function writeStdOut). These 3 templates are defined in 3 different areas in EGL2Java and we could mirror
		// that and provide 3 different EGL2Example template file locations, if we wanted to. However, there is no reason to
		// do that, other than to keep things organized in the same fashion as EGL2Java does. We could just place all 3
		// overriding definitions in a single EGL2Example template directory instead, and that is what we are going to do.
		// So, let's only define 1 overriding template located in our current project. The name defines the location and the
		// last .templates defines the template file's name (.properties is automatically added). This file can be called
		// anything you like.
		templates.add("org.eclipse.edt.gen.generator.example.templates.templates");
		// Now add the ones we are extending
		String[] others = super.getTemplatePath();
		for (String other : others) {
			templates.add(other);
		}
		return (String[]) templates.toArray(new String[templates.size()]);
	}
}
