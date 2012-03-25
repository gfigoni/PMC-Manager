
import models.PMCEdition;
import org.junit.*;
import play.test.*;

public class PMCTest extends UnitTest {

    @Test
    public void computePoint()
            throws Exception {
        assertEquals(32, PMCEdition.calculatePoint(400000, 28032, 11606));
    }

}
