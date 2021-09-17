package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.model.maps.DofusCoordinates

enum class Zaap(private val coordinates: DofusCoordinates, private val altWorld: Boolean = false) : ITravelElement {

    AMAKNA_FORET_MALEFIQUE(DofusCoordinates(-1, 13)),
    AMAKNA_CHATEAU(DofusCoordinates(3, -5)),
    AMAKNA_COIN_BOUFTOUS(DofusCoordinates(5, 7)),
    AMAKNA_MONTAGNE_CRAQUELEURS(DofusCoordinates(-5, -8)),
    AMAKNA_PLAINES_SCARAFEUILLES(DofusCoordinates(-1, 24)),
    AMAKNA_PORT_MADRESTAM(DofusCoordinates(7, -4)),
    AMAKNA_VILLAGE(DofusCoordinates(-2, 0)),
    ASTRUB(DofusCoordinates(5, -18)),
    SUFOKIA_RIVAGE(DofusCoordinates(10, 22)),
    SUFOKIA_VILLAGE(DofusCoordinates(13, 26)),
    SUFOKIA_TEMPLE_ALLIANCES(DofusCoordinates(13, 35)),
    BONTA(DofusCoordinates(-32, -56)),
    BRAKMAR(DofusCoordinates(-26, 35)),
    OTOMAI_VILLAGE_COTIER(DofusCoordinates(-46, 18)),
    OTOMAI_VILLAGE_CANOPEE(DofusCoordinates(-54, 16), true),
    FRIGOST_BOURGADE(DofusCoordinates(-78, -41)),
    FRIGOST_VILLAGE_ENSEVELI(DofusCoordinates(-77, -73)),
    MOON_PLAGE_TORTUE(DofusCoordinates(35, 12)),
    WABBITS_CAWOTTE(DofusCoordinates(25, -4)),
    LANDRES_SIDIMOTE(DofusCoordinates(-25, 12)),
    MONTAGNE_KOALAKS(DofusCoordinates(-16, 1)),
    CANIA_CHAMPS_CANIA(DofusCoordinates(-27, -36)),
    CANIA_LAC_CANIA(DofusCoordinates(-3, -42)),
    CANIA_MASSIF_CANIA(DofusCoordinates(-13, -28)),
    CANIA_PLAINE_PORKASS(DofusCoordinates(-5, -23)),
    CANIA_PLAINES_ROCHEUSES(DofusCoordinates(-17, -47)),
    CANIA_ROUTES_ROCAILLEUSES(DofusCoordinates(-20, -20)),
    SAHARACH(DofusCoordinates(15, -58)),
    TAINELA(DofusCoordinates(1, -32));

    override fun getCoordinates(): DofusCoordinates {
        return coordinates
    }

    override fun isAltWorld(): Boolean {
        return altWorld
    }
}