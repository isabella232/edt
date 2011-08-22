package org.eclipse.edt.javart.services.servlet;

import java.lang.reflect.Method;

import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.json.ArrayNode;
import org.eclipse.edt.javart.json.JsonParser;
import org.eclipse.edt.javart.json.JsonUtilities;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.services.FunctionParameterKind;
import org.eclipse.edt.javart.services.FunctionSignature;
import org.eclipse.edt.runtime.java.egl.lang.EDictionary;
import org.eclipse.edt.runtime.java.egl.lang.EglList;

import egl.lang.AnyException;
import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.http.HttpUtilities;
import eglx.json.JsonLib;
import eglx.services.ServiceInvocationException;
import eglx.services.ServiceKind;
import eglx.services.ServiceUtilities;

public class JsonRpcInvoker extends LocalServiceInvoker {
	private static String JSON_RPC_METHOD_ID = "method";
	private static String JSON_RPC_PARAMETER_ID = "params";
	private static String JSON_RPC_RESULT_ID = "result";

	public JsonRpcInvoker(RunUnit rununit, String serviceClassName, ServiceKind serviceKind) {
		super(rununit, serviceClassName, serviceKind);
	}

	public HttpResponse invoke(HttpRequest request)throws AnyException
	{
		if (trace()){
			tracer().put("invoking " + getServiceClassName() + " using " + ServiceUtilities.convert(getServiceKind()));
		}
		HttpResponse response = new HttpResponse();
		traceElapsedTime( true );

		try{
			ObjectNode jsonRequest = JsonParser.parse(request.getBody());
			if (trace()){
				tracer().put( "  invoking function " + JsonUtilities.getValueNode(jsonRequest, JSON_RPC_METHOD_ID).toJava() );
			}
			String result = wrapperProxyReturn( invokeEglService(jsonRequest) );
			traceElapsedTime( false );
			if (trace()){
				tracer().put( "return data from dedicated service:" + result == null ? "null" : result );
			}
			response.setBody( result );
			boolean failed = resultContainsError( result );
			response.setStatus( failed ? HttpUtilities.HTTP_STATUS_FAILED: HttpUtilities.HTTP_STATUS_OK );
			response.setStatusMessage(failed ? HttpUtilities.HTTP_STATUS_MSG_FAILED : HttpUtilities.HTTP_STATUS_MSG_OK);
		}
		catch(ServiceInvocationException sie){
			response.setBody(JsonLib.convertToJSON(sie));
			response.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
			response.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_FAILED);
		} catch (Throwable t) {
			response.setBody(JsonLib.convertToJSON(
				ServiceUtilities.buildServiceInvocationException(getRunUnit(), Message.SOA_E_EGL_SERVICE_INVOCATION, new String[]{request.getBody(), getServiceClassName()}, t, getServiceKind())));
			response.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
			response.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_FAILED);
		}
		return response;
	}
	public String invokeEglService( final ObjectNode jsonRequest ) throws ServiceInvocationException
	{
		String returnVal = null;
		String methodName = JsonUtilities.getValueNode(jsonRequest, JSON_RPC_METHOD_ID).toJava();
		try
		{
			Method method = getMethod(methodName);
			Object[] parameters = eglx.json.JsonUtilities.getParameters(method, (ArrayNode)JsonUtilities.getValueNode(jsonRequest, JSON_RPC_PARAMETER_ID));
			Object ret = method.invoke(getService(), parameters);
			returnVal = convertToJson(method, parameters, ret);
			getRunUnit().endRunUnit(getService());
		}
		catch (ServiceInvocationException sie)
		{
			throw sie;
		}
		catch (Throwable t)
		{
			throw ServiceUtilities.buildServiceInvocationException( getRunUnit(), Message.SOA_E_WS_PROXY_REST, new String[] {getServiceClassName(), methodName}, t, getServiceKind() );
		}
		finally
		{
		}
		return returnVal;
	}
	
/*	private Object[] populateRequestParameters(ServiceParameter[] serviceParameters, ArrayNode jsonParameters){
		final EglList<Object> inParameterArray = new EglList<Object>();
		for(ServiceParameter serviceParameter : serviceParameters){
			if(ServiceParameter.Kind.IN.equals(serviceParameter.getParameterKind()) ||
					ServiceParameter.Kind.INOUT.equals(serviceParameter.getParameterKind())){
				inParameterArray.add(serviceParameter.getParameter());
			}
		}
		AnyValue eglObject = new AnyValue() {
			EglList<Object> params = inParameterArray;
			public void ezeSetEmpty() {}
			public <T extends egl.lang.AnyValue> T ezeNewValue(Object... args)throws AnyException {
				return null;
			}
			public void ezeCopy(egl.lang.AnyValue source) {}
			public void ezeCopy(Object source) {}
		};
		ObjectNode inParameterObject = new ObjectNode();
		inParameterObject.addPair(new NameValuePairNode(new StringNode(JSON_RPC_PARAMETER_ID, false), jsonParameters));
		JsonLib.convertToEgl(eglObject, inParameterObject);
		List<Object> parameters = new ArrayList<Object>();
		int inParameterArrayIdx = 0;
		for(ServiceParameter serviceParameter : serviceParameters){
			if(ServiceParameter.Kind.IN.equals(serviceParameter.getParameterKind()) ||
					ServiceParameter.Kind.INOUT.equals(serviceParameter.getParameterKind())){
				parameters.add(inParameterArray.get(inParameterArrayIdx++));
			}
			else if(ServiceParameter.Kind.OUT.equals(serviceParameter.getParameterKind())){
				parameters.add(serviceParameter.getParameter());
			}
		}
		return parameters.toArray(new  Object[parameters.size()]);
	}*/
	private String convertToJson(Method method, Object[] parameters, Object ret){
		FunctionSignature signature = method.getAnnotation(FunctionSignature.class);
		final EglList<Object> responseParameters = new EglList<Object>();
		for(int idx = 0; idx < signature.parameters().length; idx++){
			if(!FunctionParameterKind.IN.equals(signature.parameters()[idx].kind())){
				responseParameters.add(parameters);
			}
		}
		if(ret != null){
			responseParameters.add(ret);
		}
		Object response;
		if(responseParameters.size() == 0){
			response = null;
		}
		else if(responseParameters.size() == 1){
			response = responseParameters.get(0);
		}
		else{
			response = responseParameters;
		}
		EDictionary responseObject = new EDictionary();
		responseObject.put(JSON_RPC_RESULT_ID, response);
		return JsonLib.convertToJSON(responseObject);
	}
/*	private Class<?>[] getMethodSignature(ServiceParameter[] serviceParameters){
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for(ServiceParameter serviceParameter : serviceParameters){
			if(!ServiceParameter.Kind.RETURN.equals(serviceParameter.getParameterKind()) &&
					!ServiceParameter.Kind.OUT.equals(serviceParameter.getParameterKind())){
				classes.add(serviceParameter.getParameter().getClass());
			}
		}
		return classes.toArray(new  Class<?>[classes.size()]);
	}*/
	private String wrapperProxyReturn( Object returnVal )
	{
		return returnVal == null ? "{}" : returnVal.toString();
	}

	protected boolean resultContainsError( String result )
	{
		return result.indexOf( "{\"error\" : {" ) != -1;
	}
	@Override
	public ServiceKind getServiceKind(HttpRequest innerRequest) {
		return getServiceKind();
	}
}
