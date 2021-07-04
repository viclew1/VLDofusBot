package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.model.maps.DofusCoordinate

enum class Zaap(val coordinate: DofusCoordinate, val altWorld: Boolean = false) {

    AMAKNA_FORET_MALEFIQUE(DofusCoordinate(-1, 13)),
    AMAKNA_CHATEAU(DofusCoordinate(3, -5)),
    AMAKNA_COIN_BOUFTOUS(DofusCoordinate(5, 7)),
    AMAKNA_MONTAGNE_CRAQUELEURS(DofusCoordinate(-5, -8)),
    AMAKNA_PLAINES_SCARAFEUILLES(DofusCoordinate(-1, 24)),
    AMAKNA_PORT_MADRESTAM(DofusCoordinate(7, -4)),
    AMAKNA_VILLAGE(DofusCoordinate(-2, 0)),
    ASTRUB(DofusCoordinate(5, -18)),
    SUFOKIA_RIVAGE(DofusCoordinate(10, 22)),
    SUFOKIA_VILLAGE(DofusCoordinate(13, 26)),
    SUFOKIA_TEMPLE_ALLIANCES(DofusCoordinate(13, 35)),
    BONTA(DofusCoordinate(-32, -56)),
    BRAKMAR(DofusCoordinate(-26, 35)),
    OTOMAI_VILLAGE_COTIER(DofusCoordinate(-46, 18)),
    OTOMAI_VILLAGE_CANOPEE(DofusCoordinate(-54, 16), true),
    FRIGOST_BOURGADE(DofusCoordinate(-78, -41)),
    FRIGOST_VILLAGE_ENSEVELI(DofusCoordinate(-77, -73)),
    MOON_PLAGE_TORTUE(DofusCoordinate(35, 12)),
    WABBITS_CAWOTTE(DofusCoordinate(25, -4)),
    LANDRES_SIDIMOTE(DofusCoordinate(-25, 12)),
    MONTAGNE_KOALAKS(DofusCoordinate(-16, 1)),
    CANIA_CHAMPS_CANIA(DofusCoordinate(-27, -36)),
    CANIA_LAC_CANIA(DofusCoordinate(-3, -42)),
    CANIA_MASSIF_CANIA(DofusCoordinate(-13, -28)),
    CANIA_PLAINE_PORKASS(DofusCoordinate(-5, -23)),
    CANIA_PLAINES_ROCHEUSES(DofusCoordinate(-17, -47)),
    CANIA_ROUTES_ROCAILLEUSES(DofusCoordinate(-20, -20)),
    SAHARACH(DofusCoordinate(15, -58)),
    TAINELA(DofusCoordinate(1, -32));
}