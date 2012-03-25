package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

/**
 * Trackmania Map
 *
 * @author gehef
 */
@Entity
public class TMMap extends GenericModel {

    @Id
    public String id;
    @Required
    public String name;
    @ManyToOne
    public Login login;
    @Required
    @Enumerated(EnumType.STRING)
    public TMEnvironment environment;

    public TMMap(String id, String name, Login login, TMEnvironment env) {
        super();
        this.id = id;
        this.name = name;
        this.login = login;
        this.environment = env;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isAuthor(Login other) {
        // on compare les joueurs, car un même joueur peut avoir utilisé un login différent pour faire la map.
        Player author = login.player;
        Player player = other.player;
        return author.equals(player);
    }
}
