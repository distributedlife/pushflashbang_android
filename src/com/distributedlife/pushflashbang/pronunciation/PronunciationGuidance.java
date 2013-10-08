package com.distributedlife.pushflashbang.pronunciation;

import java.util.*;

public class PronunciationGuidance {
    public static final String SPACE = " ";
    private LinkedHashMap<String, Object> reference;

    public PronunciationGuidance(LinkedHashMap<String, Object> helperText) {
        List<Map.Entry<String, Object>> entries = new ArrayList<Map.Entry<String, Object>>(helperText.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> a, Map.Entry<String, Object> b) {
                return ((Integer)b.getKey().length()).compareTo(a.getKey().length());
            }
        });

        reference = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> entry : entries) {
            reference.put(entry.getKey(), entry.getValue());
        }
    }

    public PronunciationExplanation getExplanation(String stringToGetGuidanceFor) {
        List<String> guidance = new ArrayList() {};

        Word word = new Word(stringToGetGuidanceFor);
        while (word.NotBeyondEnd()) {
            boolean matched = false;
            boolean updated = false;

            for (String key : reference.keySet()) {
                if ((word.getOffset() + key.length()) > word.getLength()) {
                    continue;
                }

                Word helper = new Word(key);

                Map<String, Object> helperTextHash = (Map<String, Object>) reference.get(key);
                String defaultValue = (String) helperTextHash.get("default");
                List<Map<String, Object>> variations = (List<Map<String, Object>>) helperTextHash.get("variations");

                Integer index = 0;
                for (String c : helper.getMultibyteArray()) {
                    Character element = new Character(c);
                    if (!word.charAt(word.getOffset() + index).equals(element)) {
                        break ;
                    }

                    helper.moveTo(index);

                    index++;

                    if (isVariationAllowed(helper, variations)) {
                        guidance.add(resolveHelperTextFromRules(variations, word, defaultValue));

                        word.moveToNext();
                        matched = true;
                        updated = true;
                        break;
                    }

                    if (index < (helper.getLength() - 1)) {
                        continue;
                    }

                    if (helper.atLastCharacter()) {
                        matched = true;
                    }
                }

                if (matched && !updated) {
                    guidance.add(defaultValue);

                    word.shiftRight(helper.getLength());
                }

                if (matched) {
                    break;
                }
            }

            if (!matched) {
                Character original_character = word.charAt(word.getOffset());
                if (!original_character.toString().trim().isEmpty()) {
                    guidance.add(original_character.toString());
                }

                word.moveToNext();
            }
        }

        return new PronunciationExplanation(guidance);
    }

    private String resolveHelperTextFromRules (List<Map<String, Object>> rules, Word word, String defaultValue) {
        for(Map<String, Object> rule : rules) {

            List<Map<String, Object>> conditions = (List<Map<String, Object>>) rule.get("when");
            if (!ruleMatches(word, conditions)) {
                continue;
            }

            return (String) rule.get("use");
        }

        return defaultValue;
    }

    private boolean ruleMatches(Word word, List<Map<String, Object>> conditions) {
        for (Map<String, Object> condition : conditions) {
            Integer position = getNeighbourRelativePosition((String) condition.get("rule"));

            if (word.charAtRelativePositionIsInvalid(position)) {
                return false;
            }

            List<String> conditionValues = (List<String>) condition.get("value");
            String[] conditionValuesAsArray = conditionValues.toArray(new String[conditionValues.size()]);

            Character neighbour = word.charAtRelativePosition(position);
            if (!Character.IsInSet(conditionValuesAsArray, neighbour.toString())) {
                return false;
            }
        }

        return true;
    }

    private Integer getNeighbourRelativePosition(String rule) {
        if (rule.equals("previous")) {
            return -1;
        }
        if (rule.equals("previousprevious")) {
            return -2;
        }
        if (rule.equals("next")) {
            return 1;
        }
        if (rule.equals("nextnext")) {
            return 2;
        }

        return 0;
    }

    private boolean isVariationAllowed(Word helper, List<Map<String, Object>> variations) {
        return helper.getLength() == 1 && variations != null;
    }
}