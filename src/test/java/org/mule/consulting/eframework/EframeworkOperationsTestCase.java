package org.mule.consulting.eframework;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.junit.Test;

public class EframeworkOperationsTestCase extends MuleArtifactFunctionalTestCase {

  /**
   * Specifies the mule config xml with the flows that are going to be executed in the tests, this file lives in the test resources.
   */
  @Override
  protected String getConfigFile() {
    return "test-mule-config.xml";
  }

  @Test
  public void executeTest() throws Exception {
//    String payloadValue = ((String) flowRunner("sayHiFlow").run()
//                                      .getMessage()
//                                      .getPayload()
//                                      .getValue());
//    assertThat(payloadValue, is("Hello Mariano Gonzalez!!!"));
	  String payload = null;
  }
}
