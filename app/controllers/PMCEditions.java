package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import models.PMCEdition;
import models.PMCException;
import models.Player;
import models.manialive.MLMap;
import models.manialive.Manialive;
import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.With;

/**
 *
 * @author gehef
 */
@With(Secure.class)
public class PMCEditions extends CRUD {

    public static void gotoImport() throws PMCException {
        // récupération de la liste des challenges manialive
        List<MLMap> maps = Manialive.getChallenges();

        // suppression des doublons possibles
        maps = new ArrayList<MLMap>(new HashSet<MLMap>(maps));

        // tri par ordre alphabétique
        Collections.sort(maps, new Comparator<MLMap>() {

            public int compare(MLMap o1, MLMap o2) {
                return o1.name.compareTo(o2.name);
            }
        });

        render("PMCEditions/import.html", maps);
    }

    public static void importPMC(
            @Required @Min(1) Integer number,
            @Required Date date,
            @Required Integer jackpot,
            boolean bonus,
            @Required List<String> maps) throws Exception {

        if (validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            redirect(request.controller + ".gotoImport");
        }

        Map<String, String> unknownLogins = PMCEdition.checkLogins(maps);
        if (unknownLogins != null && unknownLogins.size() > 0) {
            // il manque des joueurs
            List<Player> allPlayers = Player.findAll();
            render("Logins/unknown.html", unknownLogins, allPlayers, number, date, jackpot, bonus, maps);
        } else {
            // tous les joueurs de la log sont déjà identifiés
            PMCEdition.createPMCEdition(number, date, jackpot, bonus, maps);
            Application.pmc(number);
        }
    }
}
