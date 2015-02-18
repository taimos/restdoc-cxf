package org.restdoc.cxf.provider;

import org.restdoc.api.GlobalHeader;

public interface IGlobalHeaderProvider {
	
	/**
	 * @return the global header definition
	 */
	GlobalHeader getHeader();
	
}
