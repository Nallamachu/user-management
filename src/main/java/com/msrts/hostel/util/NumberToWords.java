package com.msrts.hostel.util;

import org.springframework.stereotype.Component;

@Component
public class NumberToWords {
    private static final String[] units = {
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine"
    };
    private static final String[] twoDigits = {
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen"
    };
    private static final String[] tenMultiples = {
            "",
            "",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };
    private static final String[] placeValues = {
            "",
            " thousand",
            " lakh",
            " crore",
            " arab",
            " kharab"
    };

    public static String convertNumber(long number) {
        StringBuilder word = new StringBuilder();
        int index = 0;
        boolean firstIteration = true;
        int divisor;
        do {
            divisor = firstIteration ? 1000 : 100;
            // take 3 or 2 digits based on iteration
            int num = (int) (number % divisor);
            if (num != 0) {
                String str = ConversionForUptoThreeDigits(num);
                word.insert(0, str + placeValues[index]);
            }
            index++;
            // next batch of digits
            number = number / divisor;
            firstIteration = false;
        } while (number > 0);
        return word.toString();
    }

    private static String ConversionForUptoThreeDigits(int number) {
        String word = "";
        int num = number % 100;
        if (num < 10) {
            word = word + units[num];
        } else if (num < 20) {
            word = word + twoDigits[num % 10];
        } else {
            word = tenMultiples[num / 10] + units[num % 10];
        }

        word = (number / 100 > 0) ? units[number / 100] + " hundred" + word : word;
        return word;
    }
}
