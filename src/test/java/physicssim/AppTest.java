/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package physicssim;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test public void testValidSetup() {
        assertTrue("Cannot simulate negative time frame: " + Setup.SIM_T_S, Setup.SIM_T_S > 0);
    }
}
