package org.mule.consulting.eframework.api;

public enum ProgressStage {
	MILESTONE, READ, VALIDATE, ENRICH, WRITE, XFORM,	

	READ_SRC,
	
	XFORM_CDM,
	XFORM_TGT,
	
	ENR_SRC,
	ENR_TGT,
	
	PUT_CACHE,
	GET_CACHE,
	
	PUB_EVT,
	PUB_INT,
	
	RECV_EVT,
	RECV_INT,
	
	WRITE_MRK,
	WRITE_TGT,
	
	ACK_INT,
	ACK_EVT
}
