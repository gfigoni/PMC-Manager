package models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PMCRace extends Model {

    @Required
    @ManyToOne
    public PMCEdition pmc;

    @Required
    @ManyToOne
    public TMMap map;
    
    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL)
    public List<PMCRaceResult> results;

    public PMCRace(PMCEdition pmc, TMMap map) {
        super();
        this.pmc = pmc;
        this.map = map;
    }
    
    public List<PMCRaceResult> findResults() {
        return PMCRaceResult.find("byRace", this).fetch();
    }
    
    public PMCRaceResult findResult(Login login) {
        return PMCRaceResult.find("race = ? and login = ?", this, login).first();
    }
}
