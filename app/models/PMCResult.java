package models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.Model;


@Entity
public class PMCResult extends Model implements Comparable<PMCResult> {

    @Required
    @ManyToOne
    public Login login;

    @Required
    @ManyToOne
    public PMCEdition pmc;

    @Required
    public Integer rank;

    @Required
    public Integer score;
    
    @Required
    public Integer gain;

    public Integer podiumBonus;
    
    public Integer mapperBonus;
    
    @Required
    public Integer nbRaces;

    public PMCResult(PMCEdition pmc, Login login) {
        super();
        this.pmc = pmc;
        this.login = login;
    }

    public int compareTo(PMCResult r) {
        return r.score.compareTo(this.score);
    }
    
    public static List<PMCResult> findByLogin(Login login) {
        return find("byLogin", login).fetch();
    }
}
