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
package org.eclipse.edt.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class CommandProcessor {

	private List<String> templatePath = new ArrayList<String>();
	private List<String> nativeTypePath = new ArrayList<String>();
	private List<String> primitiveTypePath = new ArrayList<String>();
	private List<String> messagePath = new ArrayList<String>();
	private Map<String, CommandParameter> parameterMapping = new HashMap<String, CommandParameter>();
	private Map<String, String> aliasMapping = new HashMap<String, String>();
	private List<String> supportedPartTypes = new ArrayList<String>();
	private List<String> supportedStereotypes = new ArrayList<String>();

	public CommandProcessor() {
	}

	public boolean processBase(String[] args) {
		try {
			// apply the command line overrides
			installOverrides(args, true);
			return true;
		}
		catch (PromptQueryException e) {
			System.out.println(e.getMessage());
			return false;
		}
		catch (UnknownParameterException e) {
			System.out.println("This parameter is unknown: " + e.getMessage());
			return false;
		}
		catch (MissingParameterValueException e) {
			System.out.println("This value for this parameter is missing: " + e.getMessage());
			return false;
		}
		catch (InvalidParameterValueException e) {
			System.out.println("This value for this parameter is incorrect: " + e.getMessage());
			return false;
		}
	}

	public boolean processUser(String[] args) {
		try {
			// apply the command line overrides
			installOverrides(args, false);
			return true;
		}
		catch (PromptQueryException e) {
			System.out.println(e.getMessage());
			return false;
		}
		catch (UnknownParameterException e) {
			System.out.println("This parameter is unknown: " + e.getMessage());
			return false;
		}
		catch (MissingParameterValueException e) {
			System.out.println("This value for this parameter is missing: " + e.getMessage());
			return false;
		}
		catch (InvalidParameterValueException e) {
			System.out.println("This value for this parameter is incorrect: " + e.getMessage());
			return false;
		}
	}

	private void installOverrides(String[] args, boolean baseMode) throws PromptQueryException, UnknownParameterException, InvalidParameterValueException,
		MissingParameterValueException {
		// process only if in base mode
		if (baseMode) {
			// clean out any previous contributions
			templatePath.clear();
			nativeTypePath.clear();
			primitiveTypePath.clear();
			messagePath.clear();
			supportedPartTypes.clear();
			supportedStereotypes.clear();
			// now go through every existing option and clear the value
			for (Entry<String, CommandParameter> entry : parameterMapping.entrySet()) {
				CommandParameter parameter = entry.getValue();
				parameter.setValue(null);
			}
		}
		// process only if in user mode
		if (!baseMode) {
			// check to see if we have any prompt queries, that provide help for the various parameters. if there are some,
			// generate the resulting text and use that as the message for a PromptQueryException. This allows us to prevent
			// the generation to proceed
			String prompt = "";
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("?")) {
					// we need to give the prompts for all parameters
					for (Entry<String, CommandParameter> entry : parameterMapping.entrySet()) {
						CommandParameter parameter = entry.getValue();
						// get the prompt value and append
						prompt = prompt + parameter.getPromptText() + "\n";
					}
				} else if (args[i].startsWith("?")) {
					// we need to get the prompt for this parameter
					String alias = args[i].substring(1);
					// we have the alias, so check to make sure it exists
					String internalName = getAlias(alias);
					if (internalName == null)
						throw new UnknownParameterException(alias);
					// we now have the internal name, which points at the command parameter, make sure everything is okay
					CommandParameter parameter = getParameter(internalName);
					if (parameter == null)
						throw new UnknownParameterException(internalName);
					// get the prompt value and append
					prompt = prompt + parameter.getPromptText() + "\n";
				}
			}
			if (prompt.length() > 0)
				throw new PromptQueryException(prompt);
		}
		// override with any command line options
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				// split the option using the = sign as the splitter
				String alias = args[i].substring(1);
				// we have the alias, so check to make sure it exists
				String internalName = getAlias(alias);
				// if we are processing in base mode, skip unknown arguments
				if (internalName == null && baseMode)
					continue;
				// if we didn't find this alias, then throw an error
				if (internalName == null)
					throw new UnknownParameterException(alias);
				// we now have the internal name, which points at the command parameter, make sure everything is okay
				CommandParameter parameter = getParameter(internalName);
				if (parameter == null)
					throw new UnknownParameterException(internalName);
				// make sure the value is acceptible, based on the definitions.
				if (parameter.getPossibleValues() instanceof Boolean[]) {
					// here we are processing either a Boolean[], which means the argument is part of the -x value, or simply
					// -x
					boolean valid = false;
					Boolean replacementValue = new Boolean(true);
					String value = "true";
					if (i + 1 < args.length && !args[i + 1].startsWith("-") && !args[i + 1].startsWith("?")) {
						value = args[i + 1];
						i++;
					}
					Object[] possibleValues = parameter.getPossibleValues();
					for (Object possibleValue : possibleValues) {
						if (possibleValue instanceof Boolean) {
							if ((Boolean) possibleValue && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))) {
								valid = true;
								replacementValue = new Boolean(true);
							} else if (!((Boolean) possibleValue) && (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))) {
								valid = true;
								replacementValue = new Boolean(false);
							}
						}
					}
					// did we get a match
					if (!valid)
						throw new InvalidParameterValueException(value);
					// replace the value of the command parameter
					parameter.setValue(replacementValue);
				} else if (parameter.getPossibleValues() instanceof String[]) {
					// here we are processing either a String[], which means the argument is part of the -x value, or simply
					// -x
					boolean valid = false;
					String replacementValue = "";
					String value = "";
					if (i + 1 < args.length && !args[i + 1].startsWith("-") && !args[i + 1].startsWith("?")) {
						value = args[i + 1];
						i++;
					}
					Object[] possibleValues = parameter.getPossibleValues();
					for (Object possibleValue : possibleValues) {
						// null means that any value is allowed
						if (possibleValue == null) {
							valid = true;
							replacementValue = value;
						} else if (possibleValue instanceof String) {
							if (value.equalsIgnoreCase((String) possibleValue)) {
								valid = true;
								replacementValue = value;
							}
						}
					}
					// did we get a match
					if (!valid)
						throw new InvalidParameterValueException(value);
					// replace the value of the command parameter
					parameter.setValue(replacementValue);
				} else {
					// we are processing an Object[], which means that we want to accept all of the trailing arguments until
					// the next -argument, and validate each against the criteria, and append to an Object[] of values
					List<String> replacementValue = new ArrayList<String>();
					while (i + 1 < args.length) {
						if (args[i + 1].startsWith("-"))
							break;
						// accept this parameter, validate and add to array
						boolean valid = false;
						Object[] possibleValues = parameter.getPossibleValues();
						for (Object possibleValue : possibleValues) {
							// null means that any value is allowed
							if (possibleValue == null)
								valid = true;
							else if (possibleValue instanceof String) {
								if (args[i + 1].equalsIgnoreCase((String) possibleValue))
									valid = true;
							}
						}
						// did we get a match
						if (valid)
							replacementValue.add(args[i + 1]);
						else
							throw new InvalidParameterValueException(args[i + 1]);
						// increment index
						i++;
					}
					// replace the value of the command parameter
					parameter.setValue(replacementValue.toArray());
				}
			}
		}
		// process only if in user mode
		if (!baseMode) {
			// now go through every option and make sure that all required options have been defined by the user
			for (Entry<String, CommandParameter> entry : parameterMapping.entrySet()) {
				CommandParameter parameter = entry.getValue();
				if (parameter.isRequired() && parameter.getValue() == null)
					throw new MissingParameterValueException(entry.getKey());
			}
		}
	}

	public void installParameter(boolean required, String internalName, String[] aliases, Object[] possibleValues, String promptText) {
		// required indicates that this option must have specified a value from the user (or defaulted to the 1st
		// possibleValue if not specified as null), and if it hasn't been defined, then an exception will be thrown at the
		// end of the install override method
		//
		// parameters are defined with 3 different possibilities for possibles values, defined by the Object[] type that
		// comes in. For the Object[] types, the description is below:
		// 1) Boolean[] means that only true or false, yes or no are accepted and the value will be set to a Boolean object
		// 2) String[] provides a list of possible string values that can be specified (case insensitive), or null which
		// means any value. Exactly 1 String will be returned when querying this parameter, which could be a null value.
		// 3) Object[] means that the value(s) that follow the command in the override, will be inserted into a list of 0 or
		// more Strings, depending on how many are provided. Each value is checked against the String values provided in the
		// list.
		//
		// For example the command override line is "-abc value1 value2 value3 -path c:\Eclipse" and the parameter is:
		// this.installParameter(false, Constants.parameter_list, new String[] { "list" }, new Object[] { null },
		// "List of values");
		// then the Object[] of 1 null entry means that any string value is accepted and the resulting value on a getValue()
		// method call will contain a String[] of "value1", "value2", "value3". The -path operand would not be included as
		// the hyphen denotes the next parameter
		//
		// For example the command override line is "-abc value1 value2 -path c:\Eclipse" and the parameter is:
		// this.installParameter(false, Constants.parameter_list, new String[] { "list" }, new Object[] { "value1", "value2"
		// }, "List of values");
		// then the Object[] of 2 entries means that only those string values that match are accepted and the resulting value
		// on a getValue() method call will contain a String[] of "value1", "value2". The -path operand would not be included
		// as the hyphen denotes the next parameter
		//
		// For example the command override line is "-abc value1 value2 value3 -path c:\Eclipse" and the parameter is:
		// this.installParameter(false, Constants.parameter_list, new String[] { "list" }, new Object[] { "value1", "value2"
		// }, "List of values");
		// then an exception will be thrown because the "value3" didn't match any entry in the Object[] definition

		// define the internal name key, with the command parameter information
		if (getParameter(internalName) != null)
			return;
		CommandParameter parameter = new CommandParameter(required, possibleValues, possibleValues[0], promptText);
		parameterMapping.put(internalName, parameter);
		// define each of the aliases to point at the internal name key
		if (aliases != null) {
			for (String alias : aliases) {
				if (alias != null) {
					if (getAlias(alias) != null) {
						System.out.println("This alias already exists: " + alias + ". It has been ignored.");
						return;
					}
					aliasMapping.put(alias.toLowerCase(), internalName);
				}
			}
		}
	}

	private String getAlias(String aliasName) {
		return this.aliasMapping.get(aliasName.toLowerCase());
	}

	public CommandParameter getParameter(String internalName) {
		return this.parameterMapping.get(internalName);
	}

	public Map<String, CommandParameter> getParameterMapping() {
		return parameterMapping;
	}

	public List<String> getTemplatePath() {
		return templatePath;
	}

	public List<String> getNativeTypePath() {
		return nativeTypePath;
	}

	public List<String> getPrimitiveTypePath() {
		return primitiveTypePath;
	}

	public List<String> getMessagePath() {
		return messagePath;
	}
	
	public List<String> getSupportedPartTypes() {
		return supportedPartTypes;
	}
	
	public List<String> getSupportedStereotypes() {
		return supportedStereotypes;
	}
}
