package tortel.fr.mypokedex;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.rd.PageIndicatorView;
import com.rd.draw.controller.DrawController;

import tortel.fr.mypokedex.bean.Pokemon;
import tortel.fr.mypokedex.dialog.NoWifiDialogFragment;
import tortel.fr.mypokedex.manager.DataRequester;
import tortel.fr.mypokedex.manager.PokemonManager;
import tortel.fr.mypokedex.service.PokemonService;
import tortel.fr.mypokedex.utils.PokemonUtils;

public class PokemonDetailsActivity extends AppCompatActivity {

    public static final String INTENT_LISTENER = "poke.details.texts";

    private int pokeId = 0;
    private Pokemon pokemon;

    private boolean wifiEnabled = false;

    private int NUM_PAGES = 3;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private PokemonService pokemonService;
    private boolean pokemonServiceStarted = false;

    private int[] pokemonIdsFetched;

    RequestQueue.RequestFilter requestFilter = new RequestQueue.RequestFilter() {
        @Override
        public boolean apply(Request<?> request) {
            return true;
        }
    };

    PageIndicatorView pageIndicatorView;

    IntentFilter filter;
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            if (bundle == null) {
                return;
            }

            pokemonIdsFetched = (int[])bundle.getSerializable("pokemons");
            PokemonUtils.saveEvolutionsForPokemons(pokemonIdsFetched);
            LinearLayout layout = findViewById(R.id.progressBarLayout);
            layout.setVisibility(View.GONE);
            pageIndicatorView = findViewById(R.id.pageIndicatorView);
            pageIndicatorView.setVisibility(View.VISIBLE);
            NUM_PAGES = pokemonIdsFetched.length;
            setupEvolutionTabs(PokemonUtils.getIdPositionInArray(pokemonIdsFetched, pokeId));
        }
    };

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                int networkType = intent.getIntExtra(
                        android.net.ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
                if (ConnectivityManager.TYPE_WIFI == networkType) {
                    NetworkInfo networkInfo = intent
                            .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (networkInfo != null) {
                        if (networkInfo.isConnected()) {
                            TextView detailsLoading = PokemonDetailsActivity.this.findViewById(R.id.detailsLoading);
                            detailsLoading.setVisibility(View.GONE);
                            pokemonService.loadPokemonDetails(pokemon.getId());
                        } else {
                            // No wifi
                        }
                    }
                }
            }
        }
    };

    LocalBroadcastManager bManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);

        Bundle bundle = getIntent().getExtras();
        pokeId = bundle.getInt("pokeId");
        pokemon = PokemonManager.getInstance().getPokemonMap().get(pokeId);
        setupActionBar();
        bManager = LocalBroadcastManager.getInstance(this);
        filter = new IntentFilter();
        filter.addAction(INTENT_LISTENER);
        bManager.registerReceiver(receiver, filter);

        if (bundle.getInt("position") != 0) {
            setupEvolutionTabs(bundle.getInt("position"));
        }

        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setVisibility(View.GONE);
        pageIndicatorView.setClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                pageIndicatorView.setSelected(position);
                setupEvolutionTabs(position);
            }
        });

        wifiEnabled = isConnectedViaWifi();

        if (!wifiEnabled && !pokemon.isPokemonAndEvolsComplete()) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(wifiReceiver, intentFilter);
            FragmentManager fm = getSupportFragmentManager();
            NoWifiDialogFragment wifiDialog = new NoWifiDialogFragment();
            wifiDialog.setCancelable(false);
            wifiDialog.show(fm, "WIFI_FRAGMENT");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void setupEvolutionTabs(int tabPosition) {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        if (tabPosition < NUM_PAGES) {
            mPager.setCurrentItem(tabPosition);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!pokemon.isPokemonAndEvolsComplete()) {
            Intent converterIntent = new Intent(this, PokemonService.class);
            bindService(converterIntent, pokemonServiceConnection, BIND_AUTO_CREATE);
            pokemonServiceStarted = true;
        } else {
            NUM_PAGES = pokemon.getEvolutions().size();
            pokemonIdsFetched = PokemonUtils.getSortedPokemonIdArray(pokemon);
            LinearLayout layout = findViewById(R.id.progressBarLayout);
            layout.setVisibility(View.GONE);
            pageIndicatorView = findViewById(R.id.pageIndicatorView);
            pageIndicatorView.setVisibility(View.VISIBLE);

            setupEvolutionTabs(PokemonUtils.getIdPositionInArray(pokemonIdsFetched, pokeId));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(pokemonServiceStarted) {
            unbindService(pokemonServiceConnection);
            pokemonServiceStarted = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bManager.unregisterReceiver(receiver);
        try {
            // Maybe not be registered
            this.unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException e) {
            // Nothing
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Details of " + pokemon.getName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBackToOverview();
                break;

            case R.id.homeMenu:
                DataRequester.getInstance(this).getRequestQueue().cancelAll(requestFilter);
                Intent intent = new Intent(PokemonDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return true;
    }

    public void goBackToOverview() {
        DataRequester.getInstance(this).getRequestQueue().cancelAll(requestFilter);
        Intent intent = new Intent();
        intent.putExtra("pokeId", pokeId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mPager == null || mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return EvolutionFragment.newInstance(pokemonIdsFetched, position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private ServiceConnection pokemonServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            pokemonServiceStarted = true;

            pokemonService = ((PokemonService.PokemonServiceBinder) service).getPokemonServiceInstance();

            if (wifiEnabled) {
                pokemonService.loadPokemonDetails(pokemon.getId());
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            pokemonService = null;
            pokemonServiceStarted = false;
        }

    };

    /**
     * Return true if the wifi is enabled
     * @return a boolean indicating the current state of the wifi
     */
    private boolean isConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

}
