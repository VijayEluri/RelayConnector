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

package org.kisst.cordys.as400;

import org.kisst.cordys.script.CompilationContext;
import org.kisst.cordys.script.ExecutionContext;
import org.kisst.cordys.script.Step;
import org.kisst.cordys.script.expression.Expression;
import org.kisst.cordys.script.expression.ExpressionParser;

import com.eibus.xml.nom.Node;

public class As400PoolStep implements Step {

	private final Expression expr;
	
	public As400PoolStep(CompilationContext compiler, final int node) {
		expr=ExpressionParser.parse(compiler, Node.getAttribute(node, "value"));
		compiler.declareTextVar(As400Module.AS400_POOL_NAME_KEY);
	}

	public void executeStep(ExecutionContext context) {
		context.setTextVar(As400Module.AS400_POOL_NAME_KEY, expr.getString(context));
	}

}
