# Mule4 eFramework Extension

The Mule 4 eFramework extension provides an easy way to invoke standardized workflows that process different event type.  The flows could be invoked directly by the Mule application, but by using the eFramework extension, the invocation of the event is separated from the implementation of the event logic. This makes it easier to use different standardized workflow logic in different Mule applications, while using a standard coding method for indicating an event has occurred.

This is a summary of the opertions, the eFramework document contains more detailed information. 

## transactionProperties ##
eFramework is intended to work with a logging framework (such as Minimal Logging) 
where a flow variable (for instance a Map<String,String> transactionProperties) is used to hold the values that will be included in log messages. 

As such, eFramework will pass a transactionProperties map to the workflow implementations it invokes. The Minimal Logging min-log:new, min-log:new-job, min-log:new-record, min-log:put and min-log:putAll operations can all be used to add properties to the transactionProperties that the eFramework uses. Here are examples:

To generate a unique x-transactio-id when accepting an API request use:

```
<min-log:new doc:name="Set transaction properties" target="transactionProperties" headers="#[attributes.headers]" />
```

To generate a unique x-job-id at the start of a batch job use:

```
<min-log:new-job target="transactionProperties" transactionProperties="#[vars.transactionProperties]" doc:name="new-job" />
```

To generate a unique x-record-id at the start of processing a batch record use:
```
<min-log:new-record target="transactionProperties"
			transactionProperties="#[vars.transactionProperties]" doc:name="new-record" />
```

Once transactionProperties is loaded with values, it can be used with the eFramework operations. Here are some examples:

To generate a progress event:

```
<eframework:progress doc:name="Progress" config-ref="Eframework_Config" stage="READ" detailText='#["pulled record " ++ payload ++ " from batch"]' recordDescriptor="#[payload]" attributes="#[vars.transactionProperties]"/>
```

To generate a business event:

```
<eframework:business-event doc:name="Business event" config-ref="Eframework_Config" eventType="Batch Job" eventStatus="FINISHED" eventMsg="BUSINESS EVENT: Batch is finished" attributes="#[vars.transactionProperties]"/>
```

To generate a system event:

```
<eframework:system-event doc:name="System event" config-ref="Eframework_Config" eventType="Batch Job" eventStatus="FINISHED"  eventMsg="SYSTEM EVENT: batch finished" attributes="#[vars.transactionProperties]"/>
```
## Sample workflow for Progress Event ##

The following workflow is invoked by an eFramework Progress operation. It is using Minimal Logging to print a log message:

```
	<flow name="eframework.progressFlow">
		<min-log:info doc:name="Info" msg='#["eframework.progressFlow "]' transactionProperties="#[attributes]"/>
	</flow>
```

## Sample workflow for Business Event ##

The following workflow is invoked by an eFramework Busines Event operation. It is using Minimal Logging to print a log message:

```
	<flow name="eframework.businessEventFlow">
		<min-log:info doc:name="Info" msg='#["eframework.businessEventFlow "]' transactionProperties="#[attributes]"/>
	</flow>
```

## Sample workflow for System Event ##

The following workflow is invoked by an eFramework System Event operation. It is using the standard Mule Logger component to print a log message:

```
	<flow name="eframework.systemEventFlow">
		<logger level="INFO" doc:name="Logger" message="#[&quot;eframework.systemEventFlow &quot; ++ attributes.'eventMsg']" />
	</flow>
```

## Configuring the Mule Application ##

Add this dependency to your application's pom.xml

```
<groupId>org.mule.consulting</groupId>
<artifactId>mule4-eframework</artifactId>
<version>1.2.0</version>
```
