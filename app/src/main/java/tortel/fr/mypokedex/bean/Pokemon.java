package tortel.fr.mypokedex.bean;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tortel.fr.mypokedex.utils.StringUtils;

public class Pokemon implements Serializable, Comparable<Pokemon> {

    private int id;
    private String name;
    private String url;
    private List<String> sprites = new ArrayList<>(); // Links of images
    private Bitmap frontImg; // Image cached
    private Bitmap backImg; // Image cached
    private int weight;
    private int height;
    private List<PokemonTypesEnum> types = new ArrayList<>();
    private List<Pair<PokemonStatsEnum, Integer>> stats = new ArrayList<>();
    private List<Pair<Integer, Pokemon>> evolutions = new ArrayList<>();
    private List<String> abilities = new ArrayList<>();
    private List<String> moves = new ArrayList<>();

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public List<String> getMoves() {
        return moves;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.capitalize(name);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getSprites() {
        return sprites;
    }

    public void setSprites(List<String> sprites) {
        this.sprites = sprites;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<PokemonTypesEnum> getTypes() {
        return types;
    }

    public void setTypes(List<PokemonTypesEnum> types) {
        this.types = types;
    }

    public List<Pair<PokemonStatsEnum, Integer>> getStats() {
        return stats;
    }

    public void setStats(List<Pair<PokemonStatsEnum, Integer>> stats) {
        this.stats = stats;
    }

    public Bitmap getFrontImg() {
        return frontImg;
    }

    public Bitmap getBackImg() {
        return backImg;
    }

    public void setFrontImg(Bitmap img) {
        this.frontImg = img;
    }

    public void setBackImg(Bitmap img) {
        this.backImg = img;
    }

    public List<Pair<Integer, Pokemon>> getEvolutions() {
        return evolutions;
    }

    public void setEvolutions(List<Pair<Integer, Pokemon>> evolutions) {
        this.evolutions = evolutions;
    }

    public void updatePokemon(Pokemon refPokemon) {

        if (this.getId() != refPokemon.getId()) {
            return;
        }

        this.setName(refPokemon.getName());
        this.setTypes(refPokemon.getTypes());
        this.setStats(refPokemon.getStats());
        this.setHeight(refPokemon.getHeight());
        //this.setUrl(refPokemon.getUrl());
        this.setSprites(refPokemon.getSprites());
        this.setWeight(refPokemon.getWeight());
        this.setEvolutions(refPokemon.getEvolutions());
        this.setAbilities(refPokemon.getAbilities());
        this.setMoves(refPokemon.getMoves());
    }

    @Override
    public int compareTo(Pokemon comparePokemon) {

        if (getId() > comparePokemon.getId()) {
            return 1;
        }
        else if (getId() < comparePokemon.getId()) {
            return -1;
        }
        else {
            return 0;
        }
    }

    @Override
    public String toString() {
        List<Pair<PokemonStatsEnum, Integer>> stats = this.getStats();
        List<Pair<Integer, Pokemon>> evols = this.getEvolutions();
        List<String> sprites = this.getSprites();
        List<String> abilities = this.getAbilities();
        List<String> moves = this.getMoves();

        StringBuilder sbStats = new StringBuilder();
        StringBuilder sbEvols = new StringBuilder();
        StringBuilder sbSprites = new StringBuilder();
        StringBuilder sbAbilities = new StringBuilder();
        StringBuilder sbMoves = new StringBuilder();

        for (Pair<PokemonStatsEnum, Integer> s : stats) {
            sbStats.append(s.first + " => " + s.second);
            sbStats.append(", ");
        }

        for (Pair<Integer, Pokemon> p : evols) {
            sbEvols.append("Evol n°" + p.first + ": " + p.second.getName());
            sbEvols.append(", ");
        }

        for (String s : sprites) {
            sbSprites.append(s);
            sbSprites.append(", ");
        }

        for (String s : abilities) {
            sbAbilities.append(s);
            sbAbilities.append(", ");
        }

        for (String s : moves) {
            sbMoves.append(s);
            sbMoves.append(", ");
        }

        StringBuilder result = new StringBuilder();
        result.append("Pokemon: " +  this.getName() + "\n");
        result.append("ID: " + this.getId() + "\n");
        result.append("Height: " + this.getHeight() + "\n");
        result.append("Weight: " + this.getWeight() + "\n");
        result.append("URL: " + this.getUrl() + "\n");

        if (stats.size() > 0) {
            result.append( "Stats: " + sbStats.substring(0, sbStats.length() - 2) + "\n");
        }

        if (evols.size() > 0) {
            result.append( "Evols: " + sbEvols.substring(0, sbEvols.length() - 2) + "\n");
        }

        if (sprites.size() > 0) {
            result.append( "Sprites: " + sbSprites.substring(0, sbSprites.length() - 2) + "\n");
        }

        if (abilities.size() > 0) {
            result.append( "Abilities: " + sbAbilities.substring(0, sbAbilities.length() - 2) + "\n");
        }

        if (moves.size() > 0) {
            result.append( "Moves: " + sbMoves.substring(0, sbMoves.length() - 2) + "\n");
        }

        return result.toString();
    }

    public boolean isPokemonAndEvolsComplete() {
        if (!hasPokemonDetails()) {
            return false;
        } else if (!isEvolutionsComplete()) {
            return false;
        }
        return true;
    }

    private boolean hasPokemonDetails() {
        if (frontImg == null || backImg == null) {
            return false;
        } else if (getMoves() == null || getMoves().size() == 0) {
            return false;
        }
        return true;
    }

    private boolean isEvolutionsComplete() {
        for (Pair<Integer, Pokemon> pair : getEvolutions()) {
            if (pair.second.getId() != getId()) {
                if (!pair.second.hasPokemonDetails()) {
                    return false;
                }
            }
        }
        return true;
    }
}
