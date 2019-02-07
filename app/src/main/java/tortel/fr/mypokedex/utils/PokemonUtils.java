package tortel.fr.mypokedex.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tortel.fr.mypokedex.bean.Pokemon;
import tortel.fr.mypokedex.bean.PokemonStatsEnum;
import tortel.fr.mypokedex.bean.PokemonTypesEnum;
import tortel.fr.mypokedex.bean.api.Chain;
import tortel.fr.mypokedex.bean.api.EvolutionChain;
import tortel.fr.mypokedex.bean.api.Species;
import tortel.fr.mypokedex.manager.PokemonManager;

public class PokemonUtils {

    /**
     * Creates and returns a List of types (list of enum) based on a List of their name (string)
     * @param stringList : the list containing the strings of the types
     * @return a list of types (enum)
     */
    public static List<PokemonTypesEnum> getTypeListFromStringList(List<String> stringList) {
        List<PokemonTypesEnum> typesList = new ArrayList<>();
        for (String type : stringList) {
            typesList.add(PokemonTypesEnum.valueOf(type.toUpperCase()));
        }
        return typesList;
    }

    /**
     * Creates and return a List of Pairs of stat/value based on a list of Pairs of string/string
     * @param stringList : the list containing for each stat (string) its value (string)
     * @return a list containing pairs of PokemonStatsEnum and the value associated
     */
    public static List<Pair<PokemonStatsEnum, Integer>> getStatListFromStringList(List<Pair<String,String>> stringList) {
        List<Pair<PokemonStatsEnum, Integer>> statsList = new ArrayList<>();
        for (Pair<String, String> stat : stringList) {
            String type = stat.first.replace("-", "_");
            PokemonStatsEnum pokemonStatsEnum = PokemonStatsEnum.valueOf(type.toUpperCase());
            int value = Integer.parseInt(stat.second);
            statsList.add(new Pair<>(pokemonStatsEnum, value));
        }
        return statsList;
    }

    /**
     * Set the bitmaps for the pokemon in the list if they have an image stored in the drawable folder
     * @param pokemonMap : the map of pokemons
     * @param res : the resources
     * @param context : the context
     */
    public static void setPokemonBitmapFromResources(Map<Integer, Pokemon> pokemonMap, Resources res, Context context) {
        for (Map.Entry<Integer, Pokemon> entry : pokemonMap.entrySet()) {
            final int resourceId = res.getIdentifier(entry.getValue().getName().toLowerCase(), "drawable", context.getPackageName());
            if (resourceId != 0) {
                Bitmap img = BitmapFactory.decodeResource(res, resourceId);
                entry.getValue().setFrontImg(img);
            }
        }
    }

    /**
     * Return a string containing all pokemon's types separated by commas
     * @param pokemon : the pokemon
     * @return a string of all types
     */
    public static String formatPokemonTypeToString(Pokemon pokemon) {
        StringBuilder sb = new StringBuilder();

        for (PokemonTypesEnum type : pokemon.getTypes()) {

            sb.append(StringUtils.capitalize(type.name()));
            sb.append(", ");
        }

        return sb.substring(0, sb.length() - 2);
    }

    /**
     * Return the stat of a given pokemon for the stat desired
     * @param pokemon : the pokemon
     * @param statsEnum : the stat desired
     * @return the stat of the pokemon
     */
    public static Integer getPokemonSpecificStat(Pokemon pokemon, PokemonStatsEnum statsEnum) {
        for (Pair<PokemonStatsEnum, Integer> stat : pokemon.getStats()) {
            if (stat.first.equals(statsEnum)) {
                return stat.second;
            }
        }
        return 0;
    }

    public static EvolutionChain getEvolutionChainFromJson(JSONObject json) {
        EvolutionChain evolutionChain = new EvolutionChain();
        try {
            evolutionChain.setId(json.getInt("id"));
            evolutionChain.setChain(getChainFromJson(json.getJSONObject("chain")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return evolutionChain;
    }

    public static Chain getChainFromJson(JSONObject json) {
        Gson gson = new Gson();
        Chain chain = new Chain();
        chain.setEvolvesTo(new ArrayList<Chain>());

        Species species;
        try {
            species = gson.fromJson(json.getJSONObject("species").toString(), Species.class);
            chain.setSpecies(species);

            for (int i = 0; i < json.getJSONArray("evolves_to").length(); i++) {
                JSONObject evol = (JSONObject)json.getJSONArray("evolves_to").get(i);
                chain.getEvolvesTo().add(getChainFromJson(evol));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chain;
    }

    public static List<String> getUrlFromEvolutionChain(EvolutionChain evolutionChain) {
        ArrayList<String> urls = new ArrayList<>();
        urls.addAll(getUrlFromChain(evolutionChain.getChain()));

        return urls;
    }

    public static List<String> getUrlFromChain(Chain chain) {
        ArrayList<String> urls = new ArrayList<>();

        if (chain == null) {
            return urls;
        } else if (chain.getSpecies() == null) {
            return urls;
        }

        urls.add(chain.getSpecies().getUrl());

        for (int i = 0; i < chain.getEvolvesTo().size(); i++) {
            Chain subChain = chain.getEvolvesTo().get(i);
            urls.addAll(getUrlFromChain(subChain));
        }
        return urls;
    }

    public static List<String> extractPokeIdFromUrls(List<String> urls) {
        List<String> pokeIds = new ArrayList<>();

        for (String s : urls) {
            pokeIds.add(s.substring(42, s.length() - 1));
        }
        return pokeIds;
    }

    public static List<String> mergePokemonUrlWithId(List<String> pokeIds, final String base) {
        List<String> urls = new ArrayList<>();

        for (String pokeId : pokeIds) {
            urls.add(base + pokeId + "/");
        }

        return urls;
    }

    /**
     * Extract pokemons details from JSONObjects
     * @param results : the Pokemons' details into JSON Objects
     * @param baseUrl : the base url of pokemons
     * @return a list of pokemons filled
     */
    public static List<Pokemon> extractPokemonFromResults(List<JSONObject> results, final String baseUrl) {
        List<Pokemon> pokemons = new ArrayList<>();

        for (JSONObject result : results) {
            Pokemon p = new Pokemon();

            try {
                p.setWeight(result.getInt("weight"));

                JSONObject sprites = result.getJSONObject("sprites");
                List<String> spriteList = new ArrayList<>();
                spriteList.add(sprites.getString("front_default"));
                spriteList.add(sprites.getString("back_default"));
                p.setSprites(spriteList);

                p.setUrl(baseUrl + result.getInt("id") + "/");
                p.setId(result.getInt("id"));
                p.setName(result.getString("name"));
                p.setHeight(result.getInt("height"));

                JSONArray statsArray = result.getJSONArray("stats");
                JSONObject statAtt = (JSONObject)statsArray.get(4);
                JSONObject statDef = (JSONObject)statsArray.get(3);
                JSONObject statHp = (JSONObject)statsArray.get(5);
                JSONObject statSpeed = (JSONObject)statsArray.get(0);
                JSONObject statSpeAtt = (JSONObject)statsArray.get(2);
                JSONObject statSpeDef = (JSONObject)statsArray.get(1);

                Pair<String, String> pAtt = new Pair<>("attack", String.valueOf(statAtt.getInt("base_stat")));
                Pair<String, String> pDef = new Pair<>("defense", String.valueOf(statDef.getInt("base_stat")));
                Pair<String, String> pHp = new Pair<>("hp", String.valueOf(statHp.getInt("base_stat")));
                Pair<String, String> pSpeAtt = new Pair<>("special-attack", String.valueOf(statSpeAtt.getInt("base_stat")));
                Pair<String, String> pSpeDef = new Pair<>("special-defense", String.valueOf(statSpeDef.getInt("base_stat")));
                Pair<String, String> pSpeed = new Pair<>("speed", String.valueOf(statSpeed.getInt("base_stat")));

                List<Pair<String, String>> stats = new ArrayList<>();
                stats.add(pAtt);
                stats.add(pDef);
                stats.add(pHp);
                stats.add(pSpeAtt);
                stats.add(pSpeDef);
                stats.add(pSpeed);

                p.setStats(getStatListFromStringList(stats));

                JSONArray typesArray = result.getJSONArray("types");
                List<String> types = new ArrayList<>();

                for (int i = typesArray.length() - 1; i >= 0; i--) {
                    JSONObject type = typesArray.getJSONObject(i);
                    types.add(type.getJSONObject("type").getString("name"));
                }

                p.setTypes(getTypeListFromStringList(types));

                JSONArray abilitiesArray = result.getJSONArray("abilities");

                for (int i  = 0; i < abilitiesArray.length(); i++) {
                    JSONObject ability = abilitiesArray.getJSONObject(i).getJSONObject("ability");
                    p.getAbilities().add(StringUtils.clean(ability.getString("name")));
                }

                JSONArray movesArray = result.getJSONArray("moves");

                for (int i  = 0; i < movesArray.length(); i++) {
                    JSONObject ability = movesArray.getJSONObject(i).getJSONObject("move");
                    p.getMoves().add(StringUtils.clean(ability.getString("name")));
                }

                pokemons.add(p);
            } catch (JSONException e) {
                Log.e("error", e.getMessage());
            }
        }
        return pokemons;
    }

    public static void updatePokemons(List<Pokemon> pokemonsToSave) {
        Map<Integer, Pokemon> pokemonMap = PokemonManager.getInstance().getPokemonMap();

        for (Pokemon p : pokemonsToSave) {
            Pokemon pokemonToUpdate = pokemonMap.get(p.getId());
            if (pokemonToUpdate == null) {
                pokemonMap.put(p.getId(), p);
            } else {
                pokemonToUpdate.updatePokemon(p);
            }
        }
    }

    public static int getIdPositionInArray(int[] ids, int idToFind) {
        for (int i = 0; i < ids.length; i++) {
            if (idToFind == ids[i]) {
                return i;
            }
        }
        return 0;
    }

    public static void updatePokemonImagesFromResult(final int[] pokemonIds, List<Bitmap> sprites) {

        // 2 images per pokemon (per id)
        if (2 * pokemonIds.length != sprites.size()) {
            return;
        }

        Map<Integer, Pokemon> pokemonMap = PokemonManager.getInstance().getPokemonMap();
        int imagePos = 0;

        for (int id : pokemonIds) {
            pokemonMap.get(id).setFrontImg(sprites.get(imagePos));
            imagePos++;
            pokemonMap.get(id).setBackImg(sprites.get(imagePos));
            imagePos++;
        }
    }

    public static void saveEvolutionsForPokemons(final int[] pokemonIds) {

        for (int i = 0; i < pokemonIds.length; i++) {
            Pokemon p = PokemonManager.getInstance().getPokemonMap().get(pokemonIds[i]);
            List<Pair<Integer, Pokemon>> evols = new ArrayList<>();

            for (int j = 0; j < pokemonIds.length; j++) {
                Pair<Integer, Pokemon> pair = new Pair<>((j+1), PokemonManager.getInstance().getPokemonMap().get(pokemonIds[j]));
                evols.add(pair);
            }

            p.setEvolutions(evols);
        }
    }

    public static int[] getSortedPokemonIdArray(Pokemon basePokemon) {
        int[] list = new int[basePokemon.getEvolutions().size()];

        for (int i = 0; i < basePokemon.getEvolutions().size(); i++) {
            list[basePokemon.getEvolutions().get(i).first-1] = basePokemon.getEvolutions().get(i).second.getId();
        }
        return list;
    }
}
