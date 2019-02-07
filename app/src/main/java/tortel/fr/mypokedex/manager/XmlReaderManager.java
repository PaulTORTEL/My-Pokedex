package tortel.fr.mypokedex.manager;

import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tortel.fr.mypokedex.bean.Pokemon;
import tortel.fr.mypokedex.utils.PokemonUtils;

public class XmlReaderManager {

    private static XmlReaderManager instance;
    private static String ns = null;

    // Private constructor
    private XmlReaderManager() {}

    public static synchronized XmlReaderManager getInstance() {

        if (instance == null) {
            instance = new XmlReaderManager();
        }
        return instance;
    }

    /**
     * Parse the XML File containing pokemons and returns a list of Pokemons
     * @param resource : the XML File
     * @param res : the resources
     * @return a map of pokemon
     */
    public Map<Integer, Pokemon> parsePokemonXmlFile(final int resource, final Resources res) {

        List<Pokemon> pokemonList = new ArrayList<>();

        try {
            pokemonList = parse(res.openRawResource(resource));
        } catch (XmlPullParserException | IOException e) {
            Log.e("error", e.getMessage());
        }
        Map<Integer, Pokemon> pokemonMap = new HashMap<>();
        for (Pokemon p : pokemonList) {
            pokemonMap.put(p.getId(), p);
        }
        return pokemonMap;
    }

    // Source: https://developer.android.com/training/basics/network-ops/xml#java
    private List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Pokemon> pokemons = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "pokemon-list");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            // We add each pokemon in the list
            pokemons.add(readPokemon(parser));
        }

        return pokemons;
    }

    private Pokemon readPokemon(XmlPullParser parser) throws XmlPullParserException, IOException {
        // We make sure that we have a starting tag "pokemon"
        parser.require(XmlPullParser.START_TAG, ns, "pokemon");
        Pokemon pokemon = new Pokemon();

        // For each parameters of the pokemon
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String param = parser.getName();
            switch(param) {
                case "id":
                    pokemon.setId(Integer.parseInt(readPokemonText(parser, "id")));
                    break;

                case "name":
                    pokemon.setName(readPokemonText(parser, "name"));
                    break;

                case "url":
                    pokemon.setUrl(readPokemonText(parser, "url"));
                    break;

                case "sprites":
                    pokemon.setSprites(readPokemonList(parser, "sprites", "sprite", null));
                    break;

                case "weight":
                    pokemon.setWeight(Integer.parseInt(readPokemonText(parser, "weight")));
                    break;

                case "height":
                    pokemon.setHeight(Integer.parseInt(readPokemonText(parser, "height")));
                    break;

                case "types":
                    List<String> stringTypes = readPokemonList(parser, "types", "type", null);
                    pokemon.setTypes(PokemonUtils.getTypeListFromStringList(stringTypes));
                    break;

                case "stats":
                    List<Pair<String, String>> stringStats = readPokemonList(parser, "stats", "stat", "type");
                    pokemon.setStats(PokemonUtils.getStatListFromStringList(stringStats));
                    break;
            }
        }
        return pokemon;
    }

    /**
     * Returns the value of the tag
     * @param parser : the XML parser
     * @param tag : the tag required to be read
     * @return the value of the tag
     * @throws XmlPullParserException
     * @throws IOException
     */
    private String readPokemonText(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return value;
    }

    // Source: https://developer.android.com/training/basics/network-ops/xml#choose
    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Return the attribute of the current tag
     * @param parser : the XML parser
     * @param attribute : the name attribute to read
     * @return the value of the attribute
     */
    private String readAttribute(XmlPullParser parser, String attribute) {
        return parser.getAttributeValue(null, attribute);
    }

    /**
     * Returns the list inside a tag
     * @param parser : the parser
     * @param tag : the tag representing the list
     * @param subtag : the tag of the items of the list
     * @return a list of String containing the values inside the tag
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List readPokemonList(XmlPullParser parser, String tag, String subtag, String subAttribute) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        List values = readList(parser, subtag, subAttribute);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return values;
    }

    /**
     * Reads a list of tags and returns their values
     * @param parser : the XML parser
     * @param subtag : the tags that we will read
     * @param subAttribute : the name of the attribute to be retrieved (can be null)
     * @return a List of String if there is no attribute, or a List of Pairs of attribute (string) / value (string) if there is an attribute
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List readList(XmlPullParser parser, String subtag, String subAttribute) throws XmlPullParserException, IOException {
        List list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            parser.require(XmlPullParser.START_TAG, ns, subtag);

            if (subAttribute != null) {
                String attr = readAttribute(parser, subAttribute);
                String value = readText(parser);

                list.add(new Pair<>(attr, value));

            } else {
                list.add(readText(parser));
            }
        }

        return list;
    }
}
