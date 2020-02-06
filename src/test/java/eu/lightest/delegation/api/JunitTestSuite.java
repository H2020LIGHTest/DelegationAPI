package eu.lightest.delegation.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
   DelegationApiTest.class,
   DelegationBuilderTest.class,
   DpServiceIntegratonTest.class
})

public class JunitTestSuite {   
}  
