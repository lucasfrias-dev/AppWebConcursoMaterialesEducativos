package ar.edu.unnoba.appweb_concurso_materiales_educativos.utils;

import java.util.HashMap;
import java.util.Map;

public class RomanNumerals {
    private static final Map<Character, Integer> values = new HashMap<Character, Integer>() {{
        put('I', 1);
        put('V', 5);
        put('X', 10);
        put('L', 50);
        put('C', 100);
        put('D', 500);
        put('M', 1000);
    }};

    private static final int[]    numbers = { 1000,  900,  500,  400,  100,   90,   50,   40,   10,    9,    5,    4,    1 };
    private static final String[] letters = { "M",  "CM",  "D",  "CD", "C",  "XC", "L",  "XL", "X",  "IX", "V",  "IV", "I" };

    public static int toInteger(String roman) {
        int result = 0;
        for (int i = 0; i < roman.length(); i++) {
            if (i > 0 && values.get(roman.charAt(i)) > values.get(roman.charAt(i - 1))) {
                result += values.get(roman.charAt(i)) - 2 * values.get(roman.charAt(i - 1));
            } else {
                result += values.get(roman.charAt(i));
            }
        }
        return result;
    }

    public static String convert(int number) {
        StringBuilder roman = new StringBuilder();
        for (int i = 0; i < numbers.length; i++) {
            while (number >= numbers[i]) {
                roman.append(letters[i]);
                number -= numbers[i];
            }
        }
        return roman.toString();
    }
}
