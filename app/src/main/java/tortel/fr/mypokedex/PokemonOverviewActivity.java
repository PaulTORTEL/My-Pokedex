package tortel.fr.mypokedex;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tortel.fr.mypokedex.bean.Pokemon;
import tortel.fr.mypokedex.bean.PokemonStatsEnum;
import tortel.fr.mypokedex.manager.PokemonManager;
import tortel.fr.mypokedex.utils.PokemonUtils;

public class PokemonOverviewActivity extends AppCompatActivity {

    private int pokeId = 0;
    private Pokemon pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_overview);

        Bundle bundle = getIntent().getExtras();
        pokeId = bundle.getInt("pokeId");
        pokemon = PokemonManager.getInstance().getPokemonMap().get(pokeId);
        setupActionBar();
        setupOverview();

        Button moreInfoButton = findViewById(R.id.moreInfoBtn);
        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PokemonOverviewActivity.this, PokemonDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("pokeId", pokeId);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                pokeId = data.getIntExtra("pokeId", 0);
                pokemon = PokemonManager.getInstance().getPokemonMap().get(pokeId);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.homeMenu:
                Intent intentMainActiv = new Intent(PokemonOverviewActivity.this, MainActivity.class);
                startActivity(intentMainActiv);
                finish();
                return true;
        }
        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Overview of " + pokemon.getName());
    }

    private void setupOverview() {
        TextView typeTextView = findViewById(R.id.pokemonType);
        TextView weightTextView = findViewById(R.id.pokemonWeight);
        TextView heightTextView = findViewById(R.id.pokemonHeight);
        ImageView imgImageView = findViewById(R.id.pokemonImg);
        TextView nameTextView = findViewById(R.id.pokemonName);
        TextView attTextView = findViewById(R.id.pokemonAttack);
        TextView defTextView = findViewById(R.id.pokemonDefense);
        TextView hpTextView = findViewById(R.id.pokemonHp);
        TextView speedTextView = findViewById(R.id.pokemonSpeed);
        TextView speAttTextView = findViewById(R.id.pokemonSpeAtt);
        TextView speDefTextView = findViewById(R.id.pokemonSpeDef);

        typeTextView.setText(PokemonUtils.formatPokemonTypeToString(pokemon));
        weightTextView.setText(pokemon.getWeight() / 10.D + " kg");
        heightTextView.setText(pokemon.getHeight() / 10.D + " m");
        imgImageView.setImageBitmap(pokemon.getFrontImg());
        nameTextView.setText(pokemon.getName());
        attTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.ATTACK)));
        defTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.DEFENSE)));
        hpTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.HP)));
        speedTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.SPEED)));
        speAttTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.SPECIAL_ATTACK)));
        speDefTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.SPECIAL_DEFENSE)));
    }
}
