How to run:
1. Copy backup.resultLib.egl into egl.rest.utilities
2. Update the egl files in libraries.async
3. Replace all the LogResult to resultLib
4. Use "Ctrl+Shift+O" to organize the imports
5. Add egl.rest and org.eclipse.edt.rui.widgets_0.8.1 into java build path of egl.rest.client
6. Run egl.rest.client.client.TestClient.egl in preview/Debug/deploy mode to verify whether all the service can be successfully invoked.

Because the LogResult of EUnit can NOT print result into console or handler, 
so we replace it with a new resultLib to print the result of the service function invocation.

If the function assert successfully a "OK" will be printed in the RUI handler
