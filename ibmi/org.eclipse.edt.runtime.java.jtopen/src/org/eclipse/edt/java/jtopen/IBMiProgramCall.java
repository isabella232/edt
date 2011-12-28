package org.eclipse.edt.java.jtopen;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.json.Json;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.services.FunctionSignature;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.ServiceProgramCall;

import eglx.lang.InvocationException;

public class IBMiProgramCall {

	private static final long serialVersionUID = 1L;
	public enum ParameterTypeKind{IN, INOUT, OUT};
	public static Integer ezeRunProgram(final String programName, final String procedureName, final boolean isServiceProgram, final boolean hasReturn, ParameterTypeKind[] parameterTypeKinds, final Object[] parameters, final AS400 as400, final String methodName, final ExecutableBase caller){
		AS400DataType[] ezeAS400DataConverters = new AS400DataType[parameters.length];
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for(int idx = 0; idx < parameters.length; idx++){
			parameterTypes[idx] = parameters[idx].getClass();
		}
		ProgramCall ezeCall = null;
		try{
			List<List<Annotation>> annotations = getParameterAnnotations(methodName, parameterTypes, caller);
			if(isServiceProgram){
				ezeCall = new ServiceProgramCall(as400);
				ezeCall.setProgram(programName);
				((ServiceProgramCall)ezeCall).setProcedureName(procedureName);
			}
			else{
				ezeCall = new ProgramCall(as400);
				ezeCall.setProgram(programName);
			}
			for(int idx = 0; idx < parameters.length; idx++){
				ezeAS400DataConverters[idx] = AS400Converter.createAS400DataType(parameters[idx], annotations.get(idx), as400);
				if(parameterTypeKinds[idx] == ParameterTypeKind.IN){
					ezeCall.addParameter(new ProgramParameter(ProgramParameter.PASS_BY_VALUE, ezeAS400DataConverters[idx].toBytes(AS400Converter.convertToObjects(parameters[idx])), 0));
				}
				else if(parameterTypeKinds[idx] == ParameterTypeKind.INOUT){
					ezeCall.addParameter(new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, ezeAS400DataConverters[idx].toBytes(AS400Converter.convertToObjects(parameters[idx])), ezeAS400DataConverters[idx].getByteLength()));
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
					buf.append(messagelist[idx]);
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

	private static List<List<Annotation>> getParameterAnnotations(final String methodName, Class<?>[] parameterTypes, final ExecutableBase caller)throws NoSuchMethodException {
		Method method = caller.getClass().getMethod(methodName, parameterTypes);
		//get the annotations defined on the parameter
		//then get the json annotations defined function's FunctionSignature
		//pass these to the AS400 converter
		Annotation[][] parametersAnnotations = method.getParameterAnnotations();
		FunctionSignature functionSignature = method.getAnnotation(FunctionSignature.class);
		List<List<Annotation>> annotations = new ArrayList<List<Annotation>>();
		for(int idx = 0; idx < parametersAnnotations.length; idx++){
			List<Annotation> parameterAnnotations = new ArrayList<Annotation>(Arrays.asList(parametersAnnotations[idx]));
			annotations.add(parameterAnnotations);
			if(functionSignature != null){
				Json json = functionSignature.parameters()[idx].jsonInfo();
				if(json != null){
					parameterAnnotations.add(json);
				}
			}
		}
		return annotations;
	}
	protected static void throwInvocationException(final String programName, final Exception e){
		InvocationException ex = new InvocationException();
		ex.setName(programName);
		ex.setMessage(e.getClass().getName() + ":" + e.getLocalizedMessage());
		throw ex;
	}
}
