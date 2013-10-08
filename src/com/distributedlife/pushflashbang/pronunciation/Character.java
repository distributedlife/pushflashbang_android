package com.distributedlife.pushflashbang.pronunciation;

public class Character {
    private final String character;
    public static final String[] A_VARIATIONS = new String[]{"ā", "á", "ǎ", "à", "a"};
    public static final String[] E_VARIATIONS = new String[]{"e", "ē", "é", "ě", "è"};
    public static final String[] I_VARIATIONS = new String[]{"ī", "í", "ǐ", "ì", "i"};
    public static final String[] O_VARIATIONS = new String[]{"o", "ō", "ó", "ǒ", "ò"};
    public static final String[] U_VARIATIONS = new String[]{"u", "ū", "ú", "ǔ", "ù"};

    public Character(String character) {
        this.character = character;
    }

    public boolean isVariationOfA() {
        return IsInSet(A_VARIATIONS, character);
    }

    public boolean isVariationOfE() {
        return IsInSet(E_VARIATIONS, character);
    }

    public boolean isVariationOfI() {
        return IsInSet(I_VARIATIONS, character);
    }

    public boolean isVariationOfO() {
        return IsInSet(O_VARIATIONS, character);
    }

    public boolean isVariationOfU() {
        return IsInSet(U_VARIATIONS, character);
    }

    public static boolean IsInSet(String[] variations, String c) {
        for(String variation : variations) {
            if (variation.equals(c)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return character;
    }

    public boolean equals(Character other_character) {
        return (character.equals(other_character.toString()) ||
                (isVariationOfA() && other_character.isVariationOfA()) ||
                (isVariationOfE() && other_character.isVariationOfE())) ||
                (isVariationOfI() && other_character.isVariationOfI()) ||
                (isVariationOfO() && other_character.isVariationOfO()) ||
                (isVariationOfU() && other_character.isVariationOfU());
    }

    public boolean isEmpty() {
        return character.isEmpty();
    }
}