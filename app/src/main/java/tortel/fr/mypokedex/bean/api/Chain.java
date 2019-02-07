package tortel.fr.mypokedex.bean.api;

import java.util.ArrayList;

public class Chain {
    private ArrayList<Chain> evolvesTo;
    private Species species;

    public ArrayList<Chain> getEvolvesTo() {
        return evolvesTo;
    }

    public void setEvolvesTo(ArrayList<Chain> evolvesTo) {
        this.evolvesTo = evolvesTo;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }
}
