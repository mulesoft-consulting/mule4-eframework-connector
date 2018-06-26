package org.mule.consulting.eframework.api.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.exception.ModuleException;

@SuppressWarnings("serial")
public class CircuitBreakerOpenException extends ModuleException {

	public CircuitBreakerOpenException(String message) {
		super(message, EframeworkErrors.CircuitBreakerOpen);
	}

}
