package models;

import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import models.manialive.MLMap;
import models.manialive.MLPlayer;
import models.manialive.Manialive;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PMCEdition extends Model {

    private static final Integer BONUS_MAPPER = 1000;
    private static final Integer BONUS_FIRST = 1000;
    private static final Integer BONUS_SECOND = 500;
    private static final Integer BONUS_THIRD = 250;
    @Required
    public Integer number;
    @Required
    public Date date;
    @Required
    public Integer jackpot;
    @OneToMany(mappedBy = "pmc", cascade = CascadeType.ALL)
    public List<PMCRace> races = new ArrayList<PMCRace>();
    @OneToMany(mappedBy = "pmc", cascade = CascadeType.ALL)
    public List<PMCResult> results;

    public PMCEdition(Integer num, Date date, Integer jackpot) {
        super();
        this.number = num;
        this.date = date;
        this.jackpot = jackpot;
    }

    @Override
    public String toString() {
        return number.toString();
    }

    public PMCRace addRace(TMMap map) {
        PMCRace race = new PMCRace(this, map).save();
        this.races.add(race);
        this.save();
        return race;
    }

    public static PMCEdition findByNumber(Integer number) {
        return find("byNumber", number).first();
    }

    public List<PMCRace> findRaces() {
        return PMCRace.find("byPmc", this).fetch();
    }

    /**
     * Méthode permettant de vérifier que tous les logins déclarés dans la base
     * manialive pour les challenges passés en paramètre sont identifiés dans
     * l'application.
     *
     * @return la liste des logins non déclarés, avec le nom par défaut du
     * joueur à proposer.
     */
    public static Map<String, String> checkLogins(List<String> mapIds) throws PMCException {
        Map<String, String> logins = new HashMap<String, String>();

        for (String mapId : mapIds) {
            // auteur
            MLMap map = Manialive.getChallenge(mapId);
            Login login = Login.findByName(map.authorId);
            if (login == null) {
                logins.put(map.authorId, map.authorId);
            }

            // joueurs
            List<MLPlayer> players = Manialive.getPlayers(mapId);
            for (MLPlayer player : players) {
                login = Login.findByName(player.login);
                if (login == null) {
                    logins.put(player.login, player.name);
                }
            }
        }

        return logins;
    }

    /**
     * Création d'un nouvelle édition de la PMC.
     *
     * @param num numéro de l'édition
     * @param date date de l'édition
     * @param jackpot dotation en coppers pour cette édition
     * @param mapIds liste des id de maps jouées
     * @return la nouvelle édition
     * @throws UnknownLoginException exception levée si un joueur n'est pas
     * référencé dans la base
     * @throws PMCException
     */
    public static PMCEdition createPMCEdition(Integer num, Date date, Integer jackpot, Boolean useBonus, List<String> mapIds)
            throws UnknownLoginException, PMCException {
        PMCEdition pmc = new PMCEdition(num, date, jackpot).save();
        Set<Login> logins = new HashSet<Login>(); // liste des joueurs de l'édition
        Set<PMCRace> races = new HashSet<PMCRace>(); // liste des courses de l'édition
        PMCRace race; // current race
        Integer points = 0; // total des points
        Map<String, Login> mappers = new HashMap<String, Login>(); // liste des mappeurs par course

        // résultats par course
        for (String mapId : mapIds) {
            // recherche de la map courante
            TMMap map = TMMap.findById(mapId);

            // Si la map n'existe pas dans la base, il faut la créer
            if (map == null) {
                // recherche des caractéristiques de la map dans manialive
                MLMap mlmap = Manialive.getChallenge(mapId);
                Login author = Login.findByName(mlmap.authorId);
                if (author == null) {
                    throw new UnknownLoginException(mlmap.authorId);
                }
                mappers.put(mapId, author);
                map = new TMMap(mlmap.id, mlmap.name, author, TMEnvironment.getEnvironment(mlmap.environment)).save();
            }

            // création de la course
            race = pmc.addRace(map);
            races.add(race);

            // participants classés dans l'ordre d'arrivée
            List<MLPlayer> mlplayers = Manialive.getPlayers(mapId);
            int index = 0;
            for (MLPlayer mlplayer : mlplayers) {
                Login login = Login.findByName(mlplayer.login);
                if (login == null) {
                    throw new UnknownLoginException(mlplayer.login);
                }
                logins.add(login);
                index++;
                int rank = index;

                // le mappeur n'obtient pas de points
                int score;
                if (map.isAuthor(login)) {
                    score = 0;
                } else {
                    score = getRaceScore(rank, mlplayers.size());
                }
                new PMCRaceResult(race, login, rank, score).save();
                points += score;
            }
        }

        // calcul du bonus total distribué aux joueurs / mappeurs
        Integer bonus = 0;
        if (useBonus) {
            // on considère qu'il y a systématiquement plus de 3 joueurs, et donc
            // que l'ensemble des bonus podium sont redistribués
            bonus += BONUS_FIRST + BONUS_SECOND + BONUS_THIRD;
        }
        // on compte un bonus pour chaque mappeur ayant participé à l'édition uniquement
        // on enlève les map/login pour les mappeurs n'ayant participé à aucune course
        Map<String, Login> playingMappers = new HashMap<String, Login>(); // liste des mappeurs par course ayant participé à l'édition
        for (String mapId : mappers.keySet()) {
            Login author = mappers.get(mapId);
            if (logins.contains(author)) {
                playingMappers.put(mapId, author);
            }
        }
        if (useBonus) {
            bonus += BONUS_MAPPER * playingMappers.size();
        }
        
        // calcul de la valeur du point pour les gains.
        double pointGain = calculatePoint(jackpot, bonus, points);

        // résultats de l'édition
        List<PMCResult> results = new ArrayList<PMCResult>();
        for (Login login : logins) {
            PMCResult result = new PMCResult(pmc, login);
            int score = 0;
            int nbRace = 0;
            int mapperBonus = 0;

            for (PMCRace r : races) {
                PMCRaceResult pr = r.findResult(login);
                if (pr != null) {
                    score += pr.score;
                    nbRace++;
                }
            }
            result.score = score;
            result.nbRaces = nbRace;
            if (useBonus) {
                for (String mapId : playingMappers.keySet()) {
                    Login author = mappers.get(mapId);
                    if (login.equals(author)) {
                        mapperBonus += BONUS_MAPPER;
                    }
                }
            }
            result.mapperBonus = mapperBonus;
            result.gain = ((int) (Math.floor(score * pointGain))) + mapperBonus;
            results.add(result);
        }

        // tri des resultats
        Collections.sort(results);

        // affectation des rangs
        int nbAtTheSameRank = 1;
        int currentRank = 1;
        PMCResult previous = null;
        for (PMCResult current : results) {
            if (previous == null) {
                // 1er joueur
                current.rank = currentRank;
                currentRank++;
            } else {
                // autres joueurs
                if (previous.compareTo(current) == 0) {
                    // cas égalité entre le joueur courant et le précédent
                    current.rank = currentRank - 1;
                    nbAtTheSameRank++;
                } else {
                    // autres cas... la liste étant déjà triée
                    current.rank = currentRank + nbAtTheSameRank - 1;
                    currentRank += nbAtTheSameRank;
                    nbAtTheSameRank = 1;
                }
            }
            previous = current;
            // ajout du bonus de rang
            Integer playerBonus = 0;
            if (useBonus) {
                playerBonus = getBonus(current.rank);
            }
            current.podiumBonus = playerBonus;
            current.gain = current.gain + playerBonus;
            current.save();
        }

        return pmc;
    }

    private static int getBonus(int rank) {
        int bonus = 0;
        switch (rank) {
            case 1:
                bonus = BONUS_FIRST;
                break;
            case 2:
                bonus = BONUS_SECOND;
                break;
            case 3:
                bonus = BONUS_THIRD;
                break;
            default:
        }
        return bonus;
    }

    /**
     * Le nombre de points pour une map équivaut au classement du joueur et est
     * fonction du nb de joueurs. Il y a un bonus pour le podium : +15/10/5
     */
    private static int getRaceScore(int rank, int nbPlayers) {
        int score = nbPlayers - rank + 1;
        switch (rank) {
            case 1:
                score += 15;
                break;
            case 2:
                score += 10;
                break;
            case 3:
                score += 5;
                break;
            default:
        }
        return score;
    }

    /**
     * Calcul le gain correspondant à la valeur du point.
     *
     * @param jackpot le total des gains mis en jeu
     * @param bonus le total des bonus divers distribués aux joueurs
     * @param points le nombre total de points pour l'ensemble des joueurs de
     * l'édition
     * @return la valeur du point
     */
    public static double calculatePoint(int jackpot, int bonus, int points) {
        // on soustraie les bonus
        double total = jackpot - bonus;

        // on divise par le nombre de points pour obtenir la valeur du point.
        return total / points;
    }
}
