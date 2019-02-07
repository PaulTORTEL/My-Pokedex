package tortel.fr.mypokedex.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tortel.fr.mypokedex.R;
import tortel.fr.mypokedex.bean.Pokemon;

public class PokemonArrayAdapter extends ArrayAdapter<Pokemon> {

    public PokemonArrayAdapter(@NonNull Context context, List<Pokemon> pokemonList) {
        super(context, 0, pokemonList);
    }

    public View getView(int position, View view, ViewGroup group) {
        return initView(position, view, group);
    }

    private View initView(int position, View view, ViewGroup group) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.pokemon_list_row, group, false);
        }

        TextView name = view.findViewById(R.id.pokemonName);
        ImageView img = view.findViewById(R.id.pokemonImg);
        TextView weight = view.findViewById(R.id.pokemonWeight);
        TextView height = view.findViewById(R.id.pokemonHeight);
        TextView id = view.findViewById(R.id.pokemonId);

        Pokemon pokemon = getItem(position);

        if (pokemon != null) {
            img.setImageBitmap(pokemon.getFrontImg());
            name.setText(pokemon.getName());
            weight.setText(pokemon.getWeight() / 10.D + " kg");
            height.setText(pokemon.getHeight() / 10.D + " m");
            id.setText("Poke-ID: " + pokemon.getId());
        }

        return view;
    }
}