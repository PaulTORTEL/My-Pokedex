package tortel.fr.mypokedex.listener;

import org.json.JSONObject;

import java.util.List;

import tortel.fr.mypokedex.bean.RequestCategoriesEnum;

public interface PokeApiListener {
    void onRequestSuccessful(final List<?> results, final RequestCategoriesEnum category);
    void onRequestFailure(final String error, final RequestCategoriesEnum category);
}
