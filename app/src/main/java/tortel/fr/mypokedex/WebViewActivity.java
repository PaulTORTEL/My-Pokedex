package tortel.fr.mypokedex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import tortel.fr.mypokedex.bean.Pokemon;
import tortel.fr.mypokedex.manager.PokemonManager;

public class WebViewActivity extends AppCompatActivity {

    private Pokemon pokemon;
    private int pokeId;
    private int position;
    private final String BASE_URL = "https://www.pokemon.com/us/pokedex/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Bundle bundle = getIntent().getExtras();
        pokeId = bundle.getInt("pokeId");
        position = bundle.getInt("position");
        pokemon = PokemonManager.getInstance().getPokemonMap().get(pokeId);

        setupActionBar();
        setupWebView();
    }

    private void setupWebView() {
        final WebView webView = findViewById(R.id.pokemonWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(BASE_URL + pokemon.getName().toLowerCase());
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
                Intent intent = new Intent();
                intent.putExtra("pokeId", pokeId);
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.homeMenu:
                Intent intentMainActiv = new Intent(WebViewActivity.this, MainActivity.class);
                startActivity(intentMainActiv);
                finish();
                return true;

        }
        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Pokemon - " + pokemon.getName());
    }
}
