package eglx.lang;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.egldd.RuntimeDeploymentDesc;

import resources.edt.binding.BindingResourceProcessor;

public class Resources {
	private static BindingResourceProcessor bindingProcessor;

	/**
	 * get the resource binding from the egldd
	 */
	public static Object getResource(String uriStr)  throws AnyException{
		try {
			URI uri = new URI(uriStr);
			if("resource".equals(uri.getScheme())){
				String query = uri.getQuery() != null && uri.getQuery().length() > 0 ? "?" + uri.getQuery() :"";
				String fragment = uri.getFragment() != null && uri.getFragment().length() > 0 ? "#" + uri.getFragment() :"";
				uri = new URI(uri.getSchemeSpecificPart() + query + fragment);
			}
			if("binding".equals(uri.getScheme())){
				return getBindingResourceProcessor().resolve(uri);
			}
		} catch (URISyntaxException e) {
		}
		AnyException ae = new AnyException();
		throw ae.fillInMessage( Message.RESOURCE_NO_PROCESSOR, uriStr );
	}
	
	private static BindingResourceProcessor getBindingResourceProcessor(){
		if(bindingProcessor == null){
			bindingProcessor = new BindingResourceProcessor();
		}
		return bindingProcessor;
	}
	public static void setBindingResourceProcessor(BindingResourceProcessor ideBindingProcessor) {
		bindingProcessor = ideBindingProcessor;
	}
	
	public static interface ResourceLocator {
		public RuntimeDeploymentDesc getDeploymentDesc(URI propertyFileURI);
	}

}
