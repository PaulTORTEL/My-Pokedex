package tortel.fr.mypokedex.manager;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.List;
import java.util.Map;

import tortel.fr.mypokedex.R;
import tortel.fr.mypokedex.bean.Pokemon;
import tortel.fr.mypokedex.utils.PokemonUtils;

public class PokemonManager {
    private static PokemonManager instance;
    private Map<Integer, Pokemon> pokemonMap;

    private PokemonManager() {
    }

    public static synchronized PokemonManager getInstance() {

        if (instance == null) {
            instance = new PokemonManager();
        }
        return instance;
    }

    /**
     * Setup the map of pokemon
     * @param res : the resources of the app
     * @param context : the context of the app
     */
    public void setupPokemonList(Resources res, Context context) {
        if (pokemonMap != null && !pokemonMap.isEmpty()) {
            return;
        }

        // Parsing of the XML file
        pokemonMap = XmlReaderManager.getInstance().parsePokemonXmlFile(R.raw.pokemon_list, res);

        // Setup of the images of the pokemon in the XML
        PokemonUtils.setPokemonBitmapFromResources(pokemonMap, res, context);

    }

    public Map<Integer, Pokemon> getPokemonMap() {
        return pokemonMap;
    }

    public void setPokemonMap(Map<Integer, Pokemon> pokemonMap) {
        this.pokemonMap = pokemonMap;
    }
}
