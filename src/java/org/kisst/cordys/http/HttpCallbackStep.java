package org.kisst.cordys.http;

import org.apache.commons.httpclient.methods.PostMethod;
import org.kisst.cordys.script.CompilationContext;
import org.kisst.cordys.script.ExecutionContext;
import org.kisst.cordys.script.Step;
import org.kisst.cordys.util.NomUtil;
import org.kisst.cordys.util.SoapUtil;

import com.eibus.xml.nom.Node;

public class HttpCallbackStep extends HttpBase2 implements Step {
	public static final String defaultWrapperElementNamespace = "http://kisst.org/cordys/http";
	public static final String defaultWrapperElementName = "CallbackWrapper";
	
	private final String wrappperElementName;
	private final String wrappperElementNamespace;
	
	public HttpCallbackStep(CompilationContext compiler, final int node) {
		super(compiler, node);
		wrappperElementName     =compiler.getSmartAttribute(node, "wrapperName", defaultWrapperElementName);
		wrappperElementNamespace=compiler.getSmartAttribute(node, "wrapperNamespace", defaultWrapperElementNamespace);
	}
	
	public void executeStep(final ExecutionContext context) {
	    int bodyNode= createBody(context);
	    int httpResponse = 0;
	    try {
	    	int header=NomUtil.getElement(bodyNode, SoapUtil.soapNamespace, "Header");
	    	int wrapper=NomUtil.getElement(header,  wrappperElementNamespace, wrappperElementName);
	    	int address=NomUtil.getElementByLocalName(wrapper, "Address");
	    	String url=Node.getData(address);
	    	PostMethod method=createPostMethod(url, bodyNode);
		    		
	    	HttpResponse response=httpCall(method, null);
			if (xmlResponse) {
				httpResponse = response.getResponseXml(context.getCallContext().getDocument());
				int cordysResponse=context.getXmlVar("output");
				SoapUtil.mergeResponses(httpResponse, cordysResponse);
			}
	    }
	    finally {
	    	Node.delete(httpResponse);
	    }
	}
}
