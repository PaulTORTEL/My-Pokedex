package tortel.fr.mypokedex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import tortel.fr.mypokedex.adapter.PokemonArrayAdapter;
import tortel.fr.mypokedex.bean.Pokemon;
import tortel.fr.mypokedex.manager.PokemonManager;

public class MainActivity extends AppCompatActivity {

    List<Pokemon> pokemonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Read XML file and set HTTP requests to populate the list of pokemons
        PokemonManager.getInstance().setupPokemonList(getResources(), getApplicationContext());

        Map<Integer, Pokemon> pokemonMap = PokemonManager.getInstance().getPokemonMap();

        for (Map.Entry<Integer, Pokemon> entry : pokemonMap.entrySet()) {
            pokemonList.add(entry.getValue());
        }

        Collections.sort(pokemonList);

        ListView pokemonListView = findViewById(R.id.pokemonList);
        PokemonArrayAdapter pokemonArrayAdapter = new PokemonArrayAdapter(getApplicationContext(), pokemonList);
        pokemonListView.setAdapter(pokemonArrayAdapter);

        // When the user clicks on a pokemon in the list
        pokemonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, PokemonOverviewActivity.class);
                Bundle bundle = new Bundle();

                bundle.putInt("pokeId", pokemonList.get(i).getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
