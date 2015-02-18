package org.restdoc.cxf.provider;

public interface IClassesProvider {
	
	/**
	 * @return the array of JAX-RS annotated classes to include in this RestDoc
	 */
	Class<?>[] getClasses();
	
}
