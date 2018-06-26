package org.mule.consulting.eframework.api.error;

import java.util.HashSet;
import java.util.Set;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public class EframeworkErrorProvider implements ErrorTypeProvider {

	@SuppressWarnings("rawtypes")
	@Override
	public Set<ErrorTypeDefinition> getErrorTypes() {
        HashSet<ErrorTypeDefinition> errors = new HashSet<>();
        errors.add(EframeworkErrors.CircuitBreakerOpen);
        return errors;
	}

}
