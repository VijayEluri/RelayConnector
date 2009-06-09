package org.kisst.cordys.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.kisst.cfg4j.Props;
import org.kisst.cordys.script.CompilationContext;
import org.kisst.cordys.script.ExecutionContext;
import org.kisst.cordys.script.expression.XmlExpression;

import com.eibus.xml.nom.Node;

public class HttpBase {
    private static final HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
    
    public static void reset() {
    	// TODO: better way to clear all. This will not affect active connections
    	client.getHttpConnectionManager().closeIdleConnections(0);
    	client.getParams().setAuthenticationPreemptive(true);
    }
	
    private final XmlExpression body;
    private final boolean prettyPrint;
    private final int timeout;
    protected final Props props;
	private boolean ignoreSoapFault;
	
	public HttpBase(CompilationContext compiler, final int node) {
		props=compiler.getProps();
		ignoreSoapFault=HttpSettings.ignoreReturnCode.get(props);
		prettyPrint = compiler.getSmartBooleanAttribute(node, "prettyPrint", false);
		timeout = compiler.getSmartIntAttribute(node, "timeout", HttpSettings.timeout.get(props));
		body=new XmlExpression(compiler, Node.getAttribute(node, "body", "/input/../.."));
	}

	protected int createBody(final ExecutionContext context) {
		// Note: this method always clones the XML, so it may be modified, and should always be deleted
		// It might be more efficient, to not clone, but then it becomes tricky if the node needs to be deleted
		return Node.clone(body.getNode(context), true);
	}

	protected PostMethod createPostMethod(String url, int bodyNode) {
    	String xml=Node.writeToString(bodyNode, prettyPrint);
	    PostMethod method = new PostMethod(url); // TODO: handle slashes /
	    method.getParams().setSoTimeout(timeout);
		try {
			method.setRequestEntity(new StringRequestEntity(xml, "text/xml", "UTF-8"));
		}
		catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
		return method;
	}
	
	private HttpResponse retrieveResponse(PostMethod method, int statusCode) {
		try {
			HttpResponse result=new HttpResponse(statusCode, method.getResponseBody());
			if (statusCode >= 300 && ! ignoreSoapFault) {
				throw new HttpSoapFaultException(result);
			}
			return result;
		}
	    catch (IOException e) {  throw new RuntimeException(e); }
	}

	protected HttpResponse httpCall(final PostMethod method, HttpState state) {
	    try {
	    	//int statusCode = client.executeMethod(method.getHostConfiguration(), method, state);
	    	int statusCode = client.executeMethod(null, method, state);
	    	return retrieveResponse(method, statusCode);
	    }
	    catch (HttpException e) { throw new RuntimeException(e); } 
	    catch (IOException e) {  throw new RuntimeException(e); }
	    finally {
	    	method.releaseConnection(); // TODO: what if connection not yet borrowed?
	    }
	}
}
