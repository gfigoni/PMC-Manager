package models.manialive;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import models.DBUtils;
import models.PMCException;

/**
 *
 * @author gehef
 */
public class Manialive {

    /**
     * Liste des maps de la base manialive
     */
    public static List<MLMap> getChallenges() throws PMCException {
        List<MLMap> maps = new ArrayList<MLMap>();
        PreparedStatement stmt = null;
        Connection con = null;
        try {
            String query = "select challenge_uid,challenge_nameStripped,challenge_environment,challenge_author from exp_maps";
            con = DBUtils.getConnection();
            stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("challenge_nameStripped");
                String id = rs.getString("challenge_uid");
                String author = rs.getString("challenge_author");
                String environment = rs.getString("challenge_environment");

                MLMap map = new MLMap();
                map.id = id;
                map.name = name;
                map.authorId = author;
                map.environment = environment;

                maps.add(map);
            }
        } catch (SQLException e) {
            throw new PMCException(e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                throw new PMCException(e);
            }
        }
        return maps;
    }

    /**
     * Récupération d'une map
     */
    public static MLMap getChallenge(String mapId) throws PMCException {
        MLMap map = null;
        PreparedStatement stmt = null;
        Connection con = null;
        try {
            String query = "select challenge_uid,challenge_nameStripped,challenge_environment,challenge_author from exp_maps where challenge_uid = ?";
            con = DBUtils.getConnection();
            stmt = con.prepareStatement(query);
            stmt.setString(1, mapId);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                String name = rs.getString("challenge_nameStripped");
                String id = rs.getString("challenge_uid");
                String author = rs.getString("challenge_author");
                String environment = rs.getString("challenge_environment");

                map = new MLMap();
                map.id = id;
                map.name = name;
                map.authorId = author;
                map.environment = environment;
            }
        } catch (SQLException e) {
            throw new PMCException(e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                throw new PMCException(e);
            }
        }
        return map;
    }

    /**
     * Liste des joueurs ayant un record sur une map donnée.
     * La liste est ordonnée selon le record (du plus rapide au plus lent)
     */
    public static List<MLPlayer> getPlayers(String mapId) throws PMCException {
        List<MLPlayer> players = new ArrayList<MLPlayer>();
        PreparedStatement stmt = null;
        Connection con = null;
        try {
            String query = "select lr.record_playerlogin, p.player_nickname from exp_records lr, exp_players p where lr.record_challengeuid = ? and p.player_login = lr.record_playerlogin order by lr.record_score asc";
            con = DBUtils.getConnection();
            stmt = con.prepareStatement(query);
            stmt.setString(1, mapId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String login = rs.getString("lr.record_playerlogin");
                String name = undecorate(rs.getString("p.player_nickname"));
                MLPlayer player = new MLPlayer(login, name);
                players.add(player);
            }
        } catch (SQLException e) {
            throw new PMCException(e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                throw new PMCException(e);
            }
        }
        return players;
    }
    
 
    /**
     * Fonction supprimant tous les caractères de décoration de TM.
     */
    public static String undecorate(String name) {
        /*
    str_replace("\0", '$$',
     preg_replace('/\\$[hlp](.*?)(?:\\[.*?\\](.*?))*(?:\\$[hlp]|$)/ixu', '$1$2',
      preg_replace('/\\$(?:[0-9a-f][^$][^$]|[0-9a-f][^$]|[^][hlp]|(?=[][])|$)/ixu', '',
      str_replace('$$', "\0", tm_substr($str))
      )
     )
    );
   Notes:
- ça marche dans quasi tous les cas, mais le jeu est trop permissif de manière imprevisible, et certains cas très spéciaux ne passent pas comme il faut.
- tm_substr() est une fonction à moi qui filtre les utf8 foireux ou non compatibles tm, en théorie pas besoin, encore que ça peut pas faire de mal (débilement le jeu permet de copier/coller un utf8 illégal dans le nickname ou le nom de map...) 
- le remplacement des '$$' en premier lieu avait été la seule solution trouvée pour éviter des faux positifs sur les codes $... , cette solution s'est faite petit à petit de manière plus ou moins collégiale entre xymph, moi et quelques autres sur tm-forum.
- concernant preg_replace, c'est du regex posix du type utilisé par perl.
         */
        String s = name;
        s = s.replace("$$", "\u0000");
        s = s.replaceAll("(?ixu)\\$(?:[0-9a-f][^$][^$]|[0-9a-f][^$]|[^hlp]|$)", "");
        s = s.replaceAll("(?ixu)\\$[hlp](.*?)(?:\\[.*?\\](.*?))*(?:\\$[hlp]|$)", "$1$2");
        s = s.replace("\u0000", "$$");
        
        return s;
    }
}
