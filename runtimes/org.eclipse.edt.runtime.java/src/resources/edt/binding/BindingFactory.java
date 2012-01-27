package resources.edt.binding;

import org.eclipse.edt.javart.resources.egldd.Binding;

public abstract class BindingFactory {

	public abstract Object createResource(Binding binding) throws Exception;
}
