package org.gingolph.tm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ InMemoryTMAPIEqualityTests.class, InMemorySAMEqualityTests.class, JsonSAMEqualityTests.class} )
public class GingolphTMAPITests {

}
