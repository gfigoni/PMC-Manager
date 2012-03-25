package controllers;

import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        PMCEdition pmc = PMCEdition.find("order by date desc").first();
        render(pmc);
    }

    public static void player(String loginName) {
        // Récupération du joueur à partir du login
        Player player = Player.findByLogin(loginName);

        // On itère sur tous les logins du joueur pour récupérer les résultats
        List<PMCResult> results = new ArrayList<PMCResult>();
        for (Login login : player.logins) {
            List<PMCResult> r = PMCResult.findByLogin(login);
            results.addAll(r);
        }

        // tri par date de PMC
        Collections.sort(results, new Comparator<PMCResult>() {

            public int compare(PMCResult o1, PMCResult o2) {
                return o2.pmc.date.compareTo(o1.pmc.date);
            }
        });
        render(player, results);
    }

    public static void players() {
        List<Player> players = Player.find("order by name asc").fetch();
        render(players);
    }

    public static void pmc(Integer number) {
        PMCEdition pmc = PMCEdition.findByNumber(number);
        render(pmc);
    }

    public static void pmcs() {
        List<PMCEdition> pmcs = PMCEdition.find("order by date desc").fetch();
        render(pmcs);
    }
}