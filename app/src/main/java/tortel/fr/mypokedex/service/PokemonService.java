package tortel.fr.mypokedex.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tortel.fr.mypokedex.PokemonDetailsActivity;
import tortel.fr.mypokedex.bean.Pokemon;
import tortel.fr.mypokedex.bean.RequestCategoriesEnum;
import tortel.fr.mypokedex.bean.RequestParam;
import tortel.fr.mypokedex.bean.api.EvolutionChain;
import tortel.fr.mypokedex.listener.PokeApiListener;
import tortel.fr.mypokedex.task.DataRequestTask;
import tortel.fr.mypokedex.task.ImageRequestTask;
import tortel.fr.mypokedex.utils.PokemonUtils;

public class PokemonService extends Service implements PokeApiListener {

    private static final String POKEMON_SPECIES_URL = "https://pokeapi.co/api/v2/pokemon-species/";
    private static final String POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/";

    private int[] pokemonsId;

    private final IBinder binder = new PokemonServiceBinder();

    public class PokemonServiceBinder extends Binder {
        public PokemonService getPokemonServiceInstance() {
            return PokemonService.this;
        }
    }

    public PokemonService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return binder;
    }

    /**
     * Load pokemon's details with his evolutions
     * @param pokeId : the pokemon's id
     */
    public void loadPokemonDetails(final int pokeId) {
        sendRequest(POKEMON_SPECIES_URL + pokeId + "/", RequestCategoriesEnum.FROM_SPECIES_TO_EVOLUTIONS);
    }

    @Override
    public void onRequestSuccessful(List<?> results, RequestCategoriesEnum category) {

        ArrayList<JSONObject> data;
        ArrayList<Bitmap> images;

        switch (category) {
            // We got the evolution
            case FROM_SPECIES_TO_EVOLUTIONS:
                try {
                    data = (ArrayList<JSONObject>) results;
                    JSONObject evolutionChain = data.get(0).getJSONObject("evolution_chain");
                    String evolutionChainUrl = evolutionChain.getString("url");
                    sendRequest(evolutionChainUrl, RequestCategoriesEnum.FROM_EVOLUTIONS_TO_SPECIES);
                } catch (JSONException e) {
                    Log.e("error", "Category: " + category.name() + " error: " + e.getMessage());
                }
                break;

             // We got the species
            case FROM_EVOLUTIONS_TO_SPECIES:
                data = (ArrayList<JSONObject>) results;
                EvolutionChain evolutionChain = PokemonUtils.getEvolutionChainFromJson(data.get(0));
                List<String> speciesUrl = PokemonUtils.getUrlFromEvolutionChain(evolutionChain);
                List<String> pokeIds = PokemonUtils.extractPokeIdFromUrls(speciesUrl);
                List<String> urlsToRequest = PokemonUtils.mergePokemonUrlWithId(pokeIds, POKEMON_URL);
                sendRequests(urlsToRequest, RequestCategoriesEnum.FROM_SPECIES_TO_POKEMON);
                break;

            // We got the pokemons
            case FROM_SPECIES_TO_POKEMON:
                data = (ArrayList<JSONObject>) results;
                List<Pokemon> pokemonList = PokemonUtils.extractPokemonFromResults(data, POKEMON_URL);
                pokemonsId = new int[pokemonList.size()];
                for (int i = 0; i < pokemonList.size(); i++) {
                    pokemonsId[i] = pokemonList.get(i).getId();
                }

                PokemonUtils.updatePokemons(pokemonList);

                List<String> urlsImageToRequest = new ArrayList<>();

                for (Pokemon p : pokemonList) {
                    for (String sprite : p.getSprites()) {
                        urlsImageToRequest.add(sprite);
                    }
                }
                sendImageRequests(urlsImageToRequest, RequestCategoriesEnum.FROM_POKEMON_TO_IMAGE);

                break;

            case FROM_POKEMON_TO_IMAGE:

                images = (ArrayList<Bitmap>) results;
                PokemonUtils.updatePokemonImagesFromResult(pokemonsId, images);
                Intent intent = new Intent(PokemonDetailsActivity.INTENT_LISTENER);
                intent.putExtra("pokemons", pokemonsId);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                break;

            case POKEMON_LIST:

                break;
        }

    }

    @Override
    public void onRequestFailure(String error, RequestCategoriesEnum category) {
        Log.e("error", "Category: " + category.name() + " error: " + error);
    }

    /**
     * Send one request
     * @param url : the url to target
     * @param category : the category
     */
    private void sendRequest(final String url, RequestCategoriesEnum category) {
        RequestParam requestParam = new RequestParam(this, getApplicationContext(), url, category);
        DataRequestTask task = new DataRequestTask();
        task.execute(requestParam);
    }

    /**
     * Send multiple requests
     * @param urls : the urls to target
     * @param category : the category of the requests
     */
    private void sendRequests(List<String> urls, RequestCategoriesEnum category) {
        RequestParam[] params = new RequestParam[urls.size()];

        for (int i = 0; i < urls.size(); i++) {
            params[i] = new RequestParam(this, getApplicationContext(), urls.get(i), category);
        }
        DataRequestTask task = new DataRequestTask();
        task.execute(params);
    }

    /**
     * Send multiple image requests
     * @param urls : the urls to target
     * @param category : the category of the requests
     */
    private void sendImageRequests(List<String> urls, RequestCategoriesEnum category) {
        RequestParam[] params = new RequestParam[urls.size()];

        for (int i = 0; i < urls.size(); i++) {
            params[i] = new RequestParam(this, getApplicationContext(), urls.get(i), category);
        }
        ImageRequestTask task = new ImageRequestTask();
        task.execute(params);
    }
}
