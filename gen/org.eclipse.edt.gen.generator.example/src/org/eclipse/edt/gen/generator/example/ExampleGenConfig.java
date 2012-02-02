/*******************************************************************************
 * Copyright Â© 2011 IBM Corporation and others.
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

import org.eclipse.edt.gen.CommandOption;
import org.eclipse.edt.gen.CommandParameter;
import org.eclipse.edt.gen.Configurable;
import org.eclipse.edt.gen.Configurator;

// this is an example on how to extend the distributed EGLGen command. As part of this example, we will also show how
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
// let's begin by first extending the distributed command processor (EGLGen) and adding our own command processor 
// parameter (-extendComments) which takes a value of true or yes, or false or no. This new parameter will be 
// accessed in the 3 extensions we are making, and if true, will cause the comments we are adding to come out
//
// the generation command will become this:
// EGLGen -o C:\myExample -r C:\myExample -p myExampleProgram -extendComments true
//
// we need to first create the ExampleGenConfig (of course it can be called anything you desire) configurator. This is
// the example of that. It will be loaded (and any others) by the -c command line option on the EGLGen command. For example:
// EGLGen -c org.eclipse.edt.gen.generator.example.ExampleGenConfig org.eclipse.edt.gen.java.JavaGenConfig
//
// it is not necessary to extend another configurator. it will be included vie the -c option
//
public class ExampleGenConfig implements Configurator {
	static final CommandOption[] commandOptions;
	static final String[] templatePath;
	static final String[] nativeTypePath;
	static final String[] primitiveTypePath;
	static final String[] messagePath;
	// define the list of command options for this generator
	static {
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
		commandOptions = new CommandOption[] { 
			// define our additional parameters here. In this case we want to add a boolean type of parameter. Its name will be
			// extendComments and we will allow a short form (ec). These parameter names are always case insensitive. Its default
			// will be false (the 1st entry in the accepted values list { false, true }).
			new CommandOption(Constants.parameter_extendComments, new String[] { "extendcomments", "ec" },
				new CommandParameter(false, new Boolean[] { false, true }, false, "ExtendComments must be defined as true or false"))
		};
	}
	// define the list of template directories for this generator
	static {
		templatePath = new String[] { 
			"org.eclipse.edt.gen.generator.example.templates.templates"
		};
	}
	// define the list of native type directories for this generator
	static {
		// as we are going to add our own system type, implemented as a java library, we need to provide the details in the
		// nativeTypes.properties file located in our directory. To tell the command processor about it, we need to add the
		// properties file to the nativeTypes list. If we were adding a new base type, or overriding an existing one, this is
		// where we would do that change as well.
		nativeTypePath = new String[] { 
			"org.eclipse.edt.gen.generator.example.nativeTypes"
		};
	}
	// define the list of primitive type directories for this generator
	static {
		primitiveTypePath = new String[] { 
		};
	}
	// define the list of message directories for this generator
	static {
		messagePath = new String[] { 
		};
	}

	public void configure(Configurable generator) {
		// register the array of command options for this configuration
		// if you don't have any, then register the empty array
		generator.registerCommandOptions(commandOptions);
		// register the array of template path directories for this configuration
		// if you don't have any, then register the empty array
		generator.registerTemplatePath(templatePath);
		// register the array of native type path directories for this configuration
		// if you don't have any, then register the empty array
		generator.registerNativeTypePath(nativeTypePath);
		// register the array of template path directories for this configuration
		// if you don't have any, then register the empty array
		generator.registerPrimitiveTypePath(primitiveTypePath);
		// register the array of template path directories for this configuration
		// if you don't have any, then register the empty array
		generator.registerMessagePath(messagePath);
	}
}
