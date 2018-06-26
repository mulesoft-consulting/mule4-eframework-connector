package org.mule.consulting.eframework.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.mule.runtime.api.artifact.Registry;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.event.EventContextFactory;
import org.mule.runtime.dsl.api.component.config.DefaultComponentLocation;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;

/**
 * This class is a container for operations, every public method in this class
 * will be taken as an extension operation.
 */
public class EframeworkOperations {

	public static String NOTIFICATION_FLOWNAME = "eframework.notificationFlow";
	public static String ERROR_FLOWNAME = "eframework.errorTransactionFlow";
	public static String RETRY_FLOWNAME = "eframework.retryTransactionFlow";
	public static String AUDIT_FLOWNAME = "eframework.auditLogFlow";

	private final Logger LOGGER = LoggerFactory.getLogger(EframeworkOperations.class);

	@Inject
	private Registry muleRegistry;	
	

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
	public void notification(String transactionType, String transactionStatus,
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
	public void error(String transactionType, String transactionStatus,
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
	public void retry(String transactionType, String transactionStatus,
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
	public void audit(String transactionType, String transactionStatus,
			@Optional(defaultValue = "AUDIT: ") String transactionMsg,
			@Optional(defaultValue = "#[{}]") @ParameterDsl(allowInlineDefinition = false) Map<String, String> attributes,
			@Content Object content, ComponentLocation location,
			@Config EframeworkConfiguration config) {
		
		createAttributesCallFlow(AUDIT_FLOWNAME, transactionType, transactionStatus, transactionMsg, attributes,
				content, location, config);
	}

	private void createAttributesCallFlow(String flowName, String transactionType, String transactionStatus,
			String transactionMsg, Map<String, String> attributes, Object content,
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
			LOGGER.error("Error during " + transactionType, ex);
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
