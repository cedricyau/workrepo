//import org.junit.Test;
//
//import mockit.Expectations;
//import mockit.Injectable;
//import mockit.Mock;
//import mockit.MockUp;
//import mockit.Mocked;
//import mockit.Verifications;
//import my.coding.challenge.BinaryAddition;
//
//public class BinaryAdditionMockitTest {
//   // Mocked instances (rather than conventional "mock objects") will be
//   // automatically created and assigned to annotated mock fields:
//   @Mocked
//   BinaryAddition binaryAdditionMock; // all current and future instances are mocked
//   
//   @Test
//   public void aTestMethod(@Mocked final BinaryAddition binaryAdditionMock)
//   {
//	// Record phase: expectations on mocks are recorded; empty if nothing to record.
//      new Expectations() {{
//    	  binaryAdditionMock.add("10", "10"); 
//    	  returns("100");
//      }};
//
//      
//
//      // In the verify phase, we may optionally verify expected invocations to
//      // "MyCollaborator" objects.
//      new Verifications() {{
//          // Verify the "MyCollaborator#doSomething()" method was executed at least once:
//          mock.doSomething();
//
//          // Even constructor invocations can be verified:
//          new MyCollaborator(); times = 0; // verifies there were no matching invocations
//
//          // Another verification, which allows up to three matching invocations:
//          mock.someOtherMethod(anyBoolean, any, withInstanceOf(Xyz.class)); maxTimes = 3;
//       }};
//   }
//   
//   @Test
//   public void fakingExample()
//   {
//      new MockUp<MyCollaborator>() {
//         @Mock
//         boolean doSomething(int n, String s, ComplexData otherData)
//         {
//            assertEquals(1, n);
//            assertNotNull(otherData);
//            ...
//            // Return (if non-void) or throw the result we want to produce for
//            // this invocation of the mocked method:
//            return otherData.isValid();
//         }
//
//         // Other mock or regular methods, if needed...
//      };
//
//      // Exercise code under test normally; calls to MyCollaborator#doSomething will
//      // execute the mock method above.
//      ...
//   }
//   
//}
//
