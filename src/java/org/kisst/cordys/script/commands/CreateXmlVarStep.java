/**
Copyright 2008, 2009 Mark Hooijkaas

This file is part of the RelayConnector framework.

The RelayConnector framework is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The RelayConnector framework is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with the RelayConnector framework.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.kisst.cordys.script.commands;

import org.kisst.cordys.script.CompilationContext;
import org.kisst.cordys.script.ExecutionContext;
import org.kisst.cordys.script.Step;
import org.kisst.cordys.script.expression.XmlExpression;
import org.kisst.cordys.script.xml.ElementAppender;

import com.eibus.xml.nom.Node;

public class CreateXmlVarStep implements Step {
	private final String name;
    private final ElementAppender appender;
    private final XmlExpression expression;
    
    
	public CreateXmlVarStep(CompilationContext compiler, final int node) {
		name =Node.getAttribute(node, "var");
		if (name==null)
			throw new RuntimeException("no var attribute defined in createXmlVarStep ");
		compiler.declareXmlVar(name);
		String expr=Node.getAttribute(node,	"value");
		if (expr==null) {
			appender = new ElementAppender(compiler, node);
			expression=null;
		}
		else {
			expression = new XmlExpression(compiler, expr);
			appender= null;
		}
		if (compiler.getCallContext().debugTraceEnabled())
			compiler.getCallContext().traceDebug("Declaring xml var "+name);
	}

	public void executeStep(final ExecutionContext context) {
		int node;
		if (appender!=null) {
			node=context.getCallContext().getDocument().createElement(name); // TODO: what element name should be used???
			appender.append(context, node);
		}
		else
			// make a deep copy
			node = Node.clone(expression.getNode(context), true);
		if (context.debugTraceEnabled())
			context.traceDebug("setting xml var "+name+" to "+Node.writeToString(node, false));
		context.getCallContext().destroyWhenDone(node);
		context.setXmlVar(name,node);

	}
}
