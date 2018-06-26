package org.mule.consulting.eframework.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.mule.consulting.eframework.api.error.CircuitBreakerOpenException;
import org.mule.consulting.eframework.api.error.EframeworkErrorProvider;
import org.mule.runtime.api.artifact.Registry;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.event.EventContextFactory;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is a container for operations, every public method in this class
 * will be taken as an extension operation.
 */
public class EframeworkOperations {

	public static String NOTIFICATION_FLOWNAME = "eframework.notificationFlow";
	public static String ERROR_FLOWNAME = "eframework.errorTransactionFlow";
	public static String RETRY_FLOWNAME = "eframework.retryTransactionFlow";
	public static String AUDIT_FLOWNAME = "eframework.auditLogFlow";
	public static String RESPONSE_PAYLOAD_FLOWNAME = "eframework.responsePayloadLogFlow";
	public static String REQUEST_PAYLOAD_FLOWNAME = "eframework.requestPayloadLogFlow";

	public static String CIRCUIT_BREAKER_CHECK_FLOWNAME = "eframework.circuitbreaker-check-breaker";	
	public static String CIRCUIT_BREAKER_TRIP_FLOWNAME = "eframework.circuitbreaker-trip";	
	public static String CIRCUIT_BREAKER_RESET_FLOWNAME = "eframework.circuitbreaker-reset";	
	public static String CIRCUIT_BREAKER_AUTO_CHECK_FLOWNAME = "eframework.circuitbreaker-auto-check-breaker";	
	public static String CIRCUIT_BREAKER_AUTO_TRIP_FLOWNAME = "eframework.circuitbreaker-auto-trip";	
	public static String CIRCUIT_BREAKER_AUTO_RESET_FLOWNAME = "eframework.circuitbreaker-auto-reset";	
	
	private final Logger LOGGER = LoggerFactory.getLogger(EframeworkOperations.class);

	@Inject
	private Registry muleRegistry;	
	
	
	/*----------------Utilities------------------*/

	/**
	 * Add the specified key/value pair to the indicated transactionProperties
	 * Map
	 * 
	 * @param key
	 * @param value
	 * @param transactionProperties
	 * @param location
	 * @return
	 */
	@MediaType(value = ANY, strict = false)
	public Map<String, String> put(String key, String value,
			@Optional Map<String, String> transactionProperties, ComponentLocation location) {

		Map<String, String> tempMap = new TreeMap<String, String>();
		if (transactionProperties != null) {
			tempMap.putAll(transactionProperties);
		}
		tempMap.put(key, value);
		return tempMap;
	}

	/**
	 * Add the all specified (newProperties) Map key/value pairs to
	 * the indicated transactionProperties Map
	 * 
	 * @param newProperties
	 * @param transactionProperties
	 * @param location
	 * @return
	 */
	@MediaType(value = ANY, strict = false)
	public Map<String, String> putAll(Map<String, String> newProperties,
			@Optional Map<String, String> transactionProperties, ComponentLocation location) {

		Map<String, String> tempMap = new TreeMap<String, String>();
		if (transactionProperties != null) {
			tempMap.putAll(transactionProperties);
		}
		tempMap.putAll(newProperties);
		return tempMap;
	}
	
	/*----------------Events------------------*/

	/**
	 * Generate a notification event.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	@Alias("sendNotificationEvent")
	public void generateNotificationEvent(String transactionType, String transactionStatus,
			@Optional(defaultValue = "NOTIFICATION: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		createAttributesCallFlow(NOTIFICATION_FLOWNAME, transactionType, transactionStatus, transactionMsg, attributes,
				content, location, config);
	}

	/**
	 * Generate an error event.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	@Alias("sendErrorEvent")
	public void generateErrorEvent(String transactionType, String transactionStatus,
			@Optional(defaultValue = "ERROR: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		createAttributesCallFlow(ERROR_FLOWNAME, transactionType, transactionStatus, transactionMsg, attributes,
				content, location, config);
	}

	/**
	 * Generate a retry event.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	@Alias("sendRetryEvent")
	public void generateRetryEvent(String transactionType, String transactionStatus,
			@Optional(defaultValue = "RETRY: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		createAttributesCallFlow(RETRY_FLOWNAME, transactionType, transactionStatus, transactionMsg, attributes,
				content, location, config);
	}

	/**
	 * Generate a audit event.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	@Alias("sendAuditEvent")
	public void generateAuditEvent(String transactionType, String transactionStatus,
			@Optional(defaultValue = "AUDIT: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		createAttributesCallFlow(AUDIT_FLOWNAME, transactionType, transactionStatus, transactionMsg, attributes,
				content, location, config);
	}
	
	/*----------------PayloadLogging------------------*/

	/**
	 * Log a response payload.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	public void logResponsePayload(String transactionType, String transactionStatus,
			@Optional(defaultValue = "RESPONSE Payload: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		TreeMap<String, String> tempMap = createAttributes(transactionType, transactionStatus,
				transactionMsg, attributes, location, config);
		tempMap.put("payloadType", "RESPONSE");
		callFlow(RESPONSE_PAYLOAD_FLOWNAME, tempMap, content, location, config);
	}

	/**
	 * Log a response payload.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	public void logRequestPayload(String transactionType, String transactionStatus,
			@Optional(defaultValue = "REQUEST Payload: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		TreeMap<String, String> tempMap = createAttributes(transactionType, transactionStatus,
				transactionMsg, attributes, location, config);
		tempMap.put("payloadType", "REQUEST");
		callFlow(REQUEST_PAYLOAD_FLOWNAME, tempMap, content, location, config);
	}
	
	/*----------------CircuitBreakers------------------*/

	/**
	 * Check circuit breaker.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	@Throws(EframeworkErrorProvider.class)
	public void circuitBreakerOpenError(String transactionType, String transactionStatus,
			@Optional(defaultValue = "Circuit Breaker Open Error: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		TreeMap<String, String> tempMap = createAttributes(transactionType, transactionStatus,
				transactionMsg, attributes, location, config);
		throw new CircuitBreakerOpenException(tempMap.get("transactionMsg"));
	}

	/**
	 * Check circuit breaker.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	public void circuitBreakerCheck(String transactionType, String transactionStatus,
			@Optional(defaultValue = "Check Circuit Breaker: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		createAttributesCallFlow(CIRCUIT_BREAKER_CHECK_FLOWNAME, transactionType, transactionStatus, transactionMsg, attributes,
				content, location, config);
	}

	/**
	 * Trip circuit breaker.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	@Throws(EframeworkErrorProvider.class)
	public void circuitBreakerTrip(String transactionType, String transactionStatus,
			@Optional(defaultValue = "Trip Circuit Breaker: ") String transactionMsg,
			@Optional(defaultValue = "TRUE") boolean throwError,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		TreeMap<String, String> tempMap = createAttributes(transactionType, transactionStatus,
				transactionMsg, attributes, location, config);
		callFlow(CIRCUIT_BREAKER_TRIP_FLOWNAME, tempMap, content, location, config);
		if (throwError) {
			throw new CircuitBreakerOpenException(tempMap.get("transactionMsg"));
		}
	}

	/**
	 * Reset circuit breaker.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	public void circuitBreakerReset(String transactionType, String transactionStatus,
			@Optional(defaultValue = "Reset Circuit Breaker: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		createAttributesCallFlow(CIRCUIT_BREAKER_RESET_FLOWNAME, transactionType, transactionStatus, transactionMsg, attributes,
				content, location, config);
	}

	/**
	 * Check automatic circuit breaker.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	public void circuitBreakerAutoCheck(String transactionType, String transactionStatus,
			@Optional(defaultValue = "Check Auto Circuit Breaker: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		createAttributesCallFlow(CIRCUIT_BREAKER_AUTO_CHECK_FLOWNAME, transactionType, transactionStatus, transactionMsg, attributes,
				content, location, config);
	}

	/**
	 * Trip automatic circuit breaker.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	@Throws(EframeworkErrorProvider.class)
	public void circuitBreakerAutoTrip(String transactionType, String transactionStatus,
			@Optional(defaultValue = "Trip Auto Circuit Breaker: ") String transactionMsg,
			@Optional(defaultValue = "TRUE") boolean throwError,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		TreeMap<String, String> tempMap = createAttributes(transactionType, transactionStatus,
				transactionMsg, attributes, location, config);
		callFlow(CIRCUIT_BREAKER_AUTO_TRIP_FLOWNAME, tempMap, content, location, config);
		if (throwError) {
			throw new CircuitBreakerOpenException(tempMap.get("transactionMsg"));
		}
	}

	/**
	 * Reset automatic circuit breaker.
	 * 
	 * @param transactionType
	 * @param transactionStatus
	 * @param transactionMsg
	 * @param attributes
	 * @param content
	 *            is the inbound payload
	 * @param location
	 *            is injected
	 */
	public void circuitBreakerAutoReset(String transactionType, String transactionStatus,
			@Optional(defaultValue = "Reset Auto Circuit Breaker: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		createAttributesCallFlow(CIRCUIT_BREAKER_AUTO_RESET_FLOWNAME, transactionType, transactionStatus, transactionMsg, attributes,
				content, location, config);
	}
	
	/*----------------private------------------*/
	
	private void createAttributesCallFlow(String flowName, String transactionType, String transactionStatus,
			String transactionMsg, Map<String, String> attributes, Object content,
			ComponentLocation location,
			EframeworkConfiguration config) {

		TreeMap<String, String> tempMap = createAttributes(transactionType, transactionStatus,
				transactionMsg, attributes, location, config);
		callFlow(flowName, tempMap, content, location, config);
	}

	private TreeMap<String, String> createAttributes(String transactionType, String transactionStatus,
			String transactionMsg, Map<String, String> attributes,
			ComponentLocation location,
			EframeworkConfiguration config) {

		TreeMap<String, String> tempMap = new TreeMap<String, String>();
		if (attributes != null) {
			for (String item : attributes.keySet()) {
				tempMap.put(item, attributes.get(item));
			}
		}

		addLocation(tempMap, location);
		tempMap.put("applicationId", config.getApplicationId());
		tempMap.put("transactionType", transactionType);
		tempMap.put("transactionStatus", transactionStatus);
		String strMsg = formatMsg(transactionMsg, tempMap);
		tempMap.put("transactionMsg", strMsg);
		
		return tempMap;
	}

	private void callFlow(String flowName, TreeMap<String, String> tempMap, Object content,
			ComponentLocation location,
			EframeworkConfiguration config) {

		try {
			Flow flow = lookupFlow(flowName);
			if (flow != null) {
				Message msg = Message.builder().value(content).attributesValue(tempMap).build();

				CoreEvent event = CoreEvent.builder(EventContextFactory.create(flow, location)).message(msg).build();

				flow.process(event);
			} else {
				LOGGER.warn(flowName + " does not exist: ");
			}
		} catch (MuleException ex) {
			LOGGER.error("Error during " + tempMap.get("transactionType"), ex);
		}
	}

	/*
	 * Add component location values to the transactionProperties
	 */
	private void addLocation(Map<String, String> transactionProperties, ComponentLocation location) {
		if (location != null) {			
			/* add the current location */
			java.util.Optional<String> fileName = location.getFileName();
			java.util.Optional<Integer> lineNumber = location.getLineInFile();
			transactionProperties.put("event.flow", location.getRootContainerName());
			if (fileName.isPresent()) {
				transactionProperties.put("event.fileName", fileName.get());
			}
			if (lineNumber.isPresent()) {
				transactionProperties.put("event.lineNumber", lineNumber.get().toString());
			}
		} else {
			LOGGER.debug("Missing location information");
		}
	}

	/*
	 * Create a message by adding the transactionProperties to the message as a
	 * JSON payload
	 */
	private String formatMsg(String msg, Map<String, String> transactionProperties) {
		ObjectMapper mapper = new ObjectMapper();
		String payload = "";
		try {
			if (transactionProperties != null) {
				payload = mapper.writeValueAsString(transactionProperties);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(msg).append(" ").append(payload);
		return sb.toString();
	}

	private Flow lookupFlow(String flowName) {
		return (Flow) muleRegistry.lookupByName(flowName).orElse(null);
	}
}
