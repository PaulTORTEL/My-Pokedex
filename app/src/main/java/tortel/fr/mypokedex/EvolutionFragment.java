package tortel.fr.mypokedex;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import tortel.fr.mypokedex.bean.Pokemon;
import tortel.fr.mypokedex.bean.PokemonStatsEnum;
import tortel.fr.mypokedex.manager.PokemonManager;
import tortel.fr.mypokedex.utils.PokemonUtils;
import tortel.fr.mypokedex.utils.StringUtils;


public class EvolutionFragment extends Fragment {

    private int[] pokemonIdsFetched;
    private int position = 0;
    private Pokemon pokemon;

    public EvolutionFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static EvolutionFragment newInstance(int[] pokemonIdsFetched, int position) {
        EvolutionFragment evolutionFragment = new EvolutionFragment();
        Bundle args = new Bundle();
        args.putIntArray("pokemonIdsFetched", pokemonIdsFetched);
        args.putInt("position", position);
        evolutionFragment.setArguments(args);
        return evolutionFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        pokemonIdsFetched = bundle.getIntArray("pokemonIdsFetched");
        position = bundle.getInt("position");
        pokemon = PokemonManager.getInstance().getPokemonMap().get(pokemonIdsFetched[position]);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_evolution, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView name = view.findViewById(R.id.pokemonName);
        name.setText(pokemon.getName());
        ImageView frontImg = view.findViewById(R.id.frontImg);
        ImageView backImg = view.findViewById(R.id.backImg);

        frontImg.setImageBitmap(pokemon.getFrontImg());
        backImg.setImageBitmap(pokemon.getBackImg());

        TextView abilities = view.findViewById(R.id.abilities);
        abilities.setText(StringUtils.toStringList(pokemon.getAbilities()));

        setupMovesTable(view);

        Button learnMoreBtn = view.findViewById(R.id.learnMoreBtn);
        learnMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("pokeId", pokemon.getId());
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
        learnMoreBtn.setText("Learn more about " + pokemon.getName());

        setupStats(view);
    }

    private void setupStats(View view) {
        TextView attTextView = view.findViewById(R.id.pokemonAttack);
        TextView defTextView = view.findViewById(R.id.pokemonDefense);
        TextView hpTextView = view.findViewById(R.id.pokemonHp);
        TextView speedTextView = view.findViewById(R.id.pokemonSpeed);
        TextView speAttTextView = view.findViewById(R.id.pokemonSpeAtt);
        TextView speDefTextView = view.findViewById(R.id.pokemonSpeDef);

        attTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.ATTACK)));
        defTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.DEFENSE)));
        hpTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.HP)));
        speedTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.SPEED)));
        speAttTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.SPECIAL_ATTACK)));
        speDefTextView.setText(String.valueOf(PokemonUtils.getPokemonSpecificStat(pokemon, PokemonStatsEnum.SPECIAL_DEFENSE)));
    }

    private void setupMovesTable(View view) {
        TableLayout table = view.findViewById(R.id.movesTable);

        TableRow row = null;

        for (int i = 0; i < pokemon.getMoves().size(); i++) {

            if (i % 3 == 0) {
                row = new TableRow(view.getContext());
                row.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                table.addView(row);
            }

            TextView moveTv = new TextView(view.getContext());
            moveTv.setText(pokemon.getMoves().get(i));
            moveTv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            moveTv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(moveTv);// add the column to the table row here
        }
    }
}
