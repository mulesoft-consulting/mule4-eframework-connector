package org.mule.consulting.eframework.api;

public enum ProgressStatus {
	SUCCESS, FAILURE, SKIPPED, 
	SYSTEM_FAILURE, DATA_FAILURE, ENDPT_FAILURE, 
	ENDPT_RETRY, RETRY_QUEUE
}
