package com.distributedlife.pushflashbang.pronunciation;

public class Word {
    private Integer offset;
    private String word;

    public Word(String word) {
        this.word = word;
        this.offset = 0;
    }

    public void moveTo(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

    public void moveToNext() {
        offset += 1;
    }

    public void shiftRight(Integer amount) {
        offset += amount;
    }

    public Integer getLength() {
        return word.length();
    }

    public String[] getMultibyteArray() {
        return word.split("(?!^)");
    }

//    public String getCurrentChar() {
//        return getMultibyteArray()[offset];
//    }
//
//    public Character getNextChar() {
//        if (atLastCharacter()) {
//            return null;
//        }
//
//        return new Character(getMultibyteArray()[offset + 1]);
//    }
//
//    public Character getPreviousChar() {
//        if (atFirstCharacter()) {
//            return null;
//        }
//
//        return new Character(getMultibyteArray()[offset - 1]);
//    }

    public Character charAt(Integer i) {
        if (i < 0 || i >= getLength()) {
            return null;
        }

        return new Character(getMultibyteArray()[i]);
    }

//    public boolean atFirstCharacter() {
//        return offset == 0;
//    }

    public boolean atLastCharacter() {
        return offset == (getLength() - 1);
    }

    public boolean NotBeyondEnd() {
        return offset < getLength();
    }

    public String toString() {
        return word;
    }

    public boolean charAtRelativePositionIsInvalid(Integer position) {
        return (charAt(offset + position) == null);
    }

    public Character charAtRelativePosition(Integer position) {
        return charAt(offset + position);
    }
}