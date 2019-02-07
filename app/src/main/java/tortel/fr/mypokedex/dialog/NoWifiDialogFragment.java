package tortel.fr.mypokedex.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import tortel.fr.mypokedex.PokemonDetailsActivity;
import tortel.fr.mypokedex.R;


public class NoWifiDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final PokemonDetailsActivity pokemonDetailsActivity = (PokemonDetailsActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = "Oops! It seems that the Wifi is disabled...";

        builder.setMessage(title)
                .setPositiveButton("Enable WIFI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                        TextView detailsLoading = pokemonDetailsActivity.findViewById(R.id.detailsLoading);
                        detailsLoading.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        pokemonDetailsActivity.goBackToOverview();
                    }
                });

        return builder.create();
    }

}
