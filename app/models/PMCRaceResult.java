package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PMCRaceResult extends Model implements Comparable<PMCRaceResult>  {

    @Required
    @ManyToOne
    public Login login;
    
    @Required
    @ManyToOne
    public PMCRace race;

    @Required
    public Integer rank;
    
    @Required
    public Integer score;

    public PMCRaceResult(PMCRace race, Login login, Integer rank, Integer score) {
        super();
        this.race = race;
        this.login = login;
        this.rank = rank;
        this.score = score;
    }
    
    @Override
    public int compareTo(PMCRaceResult r) {
        return rank.compareTo(r.rank);
    }
}
