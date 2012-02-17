/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.java.jtopen;

import java.io.IOException;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.resources.ExecutableBase;

import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.ServiceProgramCall;

import eglx.jtopen.IBMiConnection;
import eglx.jtopen.JTOpenConnection;
import eglx.lang.InvocationException;

public class IBMiProgramCall {

	private static final long serialVersionUID = 1L;
	public enum ParameterTypeKind{IN, INOUT, OUT};
	public static Integer ezeRunProgram(final String library,
									final String programName, 
									final String procedureName, final 
									boolean isServiceProgram, final 
									boolean hasReturn, ParameterTypeKind[] parameterTypeKinds, 
									final Object[] parameters,
									AS400DataType[] ezeAS400DataConverters,
									final IBMiConnection connection, 
									final String methodName, final 
									ExecutableBase caller){
		if(connection == null || connection.getAS400() == null){
			InvocationException ex = new InvocationException();
			ex.setName(programName);
			ex.setMessage("Host connection is null.");
			throw ex;
		}
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for(int idx = 0; idx < parameters.length; idx++){
			parameterTypes[idx] = parameters[idx].getClass();
		}
		String hostLibrary = library;
		if(connection instanceof JTOpenConnection){
			if(((JTOpenConnection)connection).getLibrary() != null && 
					!((JTOpenConnection)connection).getLibrary().isEmpty()){
				hostLibrary = ((JTOpenConnection)connection).getLibrary();
			}
		}
		String slash = hostLibrary != null && !hostLibrary.isEmpty() ? "/" : "";
		boolean hostLibraryHasSlash = false;
		if(hostLibrary != null && !hostLibrary.isEmpty() &&
				hostLibrary.charAt(hostLibrary.length() - 1) == '/'){
			slash = "";
			hostLibraryHasSlash = true;
		}
		if(programName != null && !programName.isEmpty() &&
				programName.charAt(0) == '/'){
			slash = "";
			if(hostLibraryHasSlash){
				hostLibrary = hostLibrary.substring(0, hostLibrary.length() - 1);
			}
		}
		hostLibrary += slash;
		ProgramCall ezeCall = null;
		try{
			if(isServiceProgram){
				ezeCall = new ServiceProgramCall(connection.getAS400());
				ezeCall.setProgram(hostLibrary + programName);
				((ServiceProgramCall)ezeCall).setProcedureName(procedureName);
			}
			else{
				ezeCall = new ProgramCall(connection.getAS400());
				ezeCall.setProgram(hostLibrary + programName);
			}
			for(int idx = 0; idx < parameters.length; idx++){
				if(parameterTypeKinds[idx] == ParameterTypeKind.IN){
					ezeCall.addParameter(new ProgramParameter(ProgramParameter.PASS_BY_VALUE, ezeAS400DataConverters[idx].toBytes(AS400Converter.convertToObjects(parameters[idx], ezeAS400DataConverters[idx])), 0));
				}
				else if(parameterTypeKinds[idx] == ParameterTypeKind.INOUT){
					ezeCall.addParameter(new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, ezeAS400DataConverters[idx].toBytes(AS400Converter.convertToObjects(parameters[idx], ezeAS400DataConverters[idx])), ezeAS400DataConverters[idx].getByteLength()));
				}
				else{
					ezeCall.addParameter(new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new byte[0], ezeAS400DataConverters[idx].getByteLength()));
				}
			}
		}
		catch(Exception e){
			throwInvocationException(programName, e);
		}

		try{
			if (ezeCall.run() != true)
			{
				AS400Message[] messagelist = ezeCall.getMessageList();
				StringBuilder buf = new StringBuilder();
				for (int idx = 0; idx < messagelist.length; ++idx)
				{
					if(idx>0){
						buf.append("\n");
					}
					buf.append(messagelist[idx].getID());
					buf.append(":");
					buf.append(messagelist[idx].getText());
					buf.append("\n");
					buf.append(messagelist[idx].getHelp());
				}
				InvocationException ex = new InvocationException();
				ex.setName(programName);
				ex.setMessage(buf.toString());
				throw ex;
			}
		} catch (AS400SecurityException e) {
			e.printStackTrace();
			throwInvocationException(programName, e);
		} catch (ErrorCompletingRequestException e) {
			e.printStackTrace();
			throwInvocationException(programName, e);
		} catch (IOException e) {
			e.printStackTrace();
			throwInvocationException(programName, e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throwInvocationException(programName, e);
		} catch (ObjectDoesNotExistException e) {
			e.printStackTrace();
			throwInvocationException(programName, e);
		}
		for(int idx = 0; idx < parameters.length; idx++){
			if(parameterTypeKinds[idx] != ParameterTypeKind.IN){
				if(parameters[idx] instanceof AnyBoxedObject<?>){
					AS400Converter.convertFromAS400(parameters[idx], ezeAS400DataConverters[idx].toObject(ezeCall.getParameterList()[idx].getOutputData()));
				}
				else{
					parameters[idx] = AS400Converter.convertFromAS400(parameters[idx], ezeAS400DataConverters[idx].toObject(ezeCall.getParameterList()[idx].getOutputData()));
				}
			}
		}
		if(hasReturn){
			return (Integer)new AS400Bin4().toObject(((ServiceProgramCall)ezeCall).getReturnValue());
		}
		else{
			return null;
		}
	}

	protected static void throwInvocationException(final String programName, final Exception e){
		InvocationException ex = new InvocationException();
		ex.setName(programName);
		ex.setMessage(e.getClass().getName() + ":" + e.getLocalizedMessage());
		throw ex;
	}
}
