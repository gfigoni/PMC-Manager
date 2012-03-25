import java.util.List;
import models.manialive.MLMap;
import models.manialive.MLPlayer;
import models.manialive.Manialive;
import org.junit.Test;
import play.test.UnitTest;

public class ManialiveTest extends UnitTest {

    @Test
    public void listChallenges()
            throws Exception {
        List<MLMap> maps = Manialive.getChallenges();
        assertEquals(12, maps.size());
    }
    
    @Test
    public void listPlayers() throws Exception {
        String mapid = "8YVNExPeoBZPXGIxonbvmoCl9Uc";
        List<MLPlayer> players = Manialive.getPlayers(mapid);
        assertEquals(104, players.size());
    }
 
    @Test
    public void undecorate() {
        String name = "$i$fc0S$f00็ĿЯ $fc0็$bbbMc$aaaLAR$999EN$f00F1";
        String undecorateName = "็ĿЯ ็McLARENF1";
        assertEquals(undecorateName, Manialive.undecorate(name));
    }
}
