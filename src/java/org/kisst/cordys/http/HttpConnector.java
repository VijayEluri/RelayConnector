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

package org.kisst.cordys.http;

import org.kisst.cordys.relay.RelayConnector;

public class HttpConnector extends RelayConnector {
    public  static final String CONNECTOR_NAME = "HttpConnector";
	private HttpModule httpModule= new HttpModule();

    public HttpConnector(){
    	addModule(httpModule);
    }
    
    protected String getManagedComponentType() { return CONNECTOR_NAME; }
    protected String getManagementName() { return CONNECTOR_NAME; }
}
