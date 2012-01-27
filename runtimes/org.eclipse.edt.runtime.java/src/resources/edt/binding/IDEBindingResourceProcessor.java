package resources.edt.binding;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.edt.javart.ide.IDEResourceLocator;
import org.eclipse.edt.javart.messages.Message;

import eglx.java.JavaObjectException;
import eglx.lang.AnyException;

public class IDEBindingResourceProcessor extends BindingResourceProcessor {

	private URI defaultDD;
	
	public IDEBindingResourceProcessor(IDEResourceLocator resourceLocator) {
		this.resourceLocator = resourceLocator;
	}
	@Override
	protected URI getDefaultDDURI() {
		return defaultDD;
	}
	public void setDefaultDD(String dd) throws AnyException {
		try {
			this.defaultDD = createFileURI(dd);
		} catch (URISyntaxException e) {
			JavaObjectException jox = new JavaObjectException();
			jox.exceptionType = URI.class.getName();
			jox.initCause( e );
			throw jox.fillInMessage( Message.RESOURCE_URI_EXCEPTION, defaultDD );
		}
	}
	public IDEResourceLocator getResourceLocator() {
		return (IDEResourceLocator)resourceLocator;
	}
}
