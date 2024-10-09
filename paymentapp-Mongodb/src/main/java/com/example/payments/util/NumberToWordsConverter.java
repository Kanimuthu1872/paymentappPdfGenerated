package com.example.payments.util;

public class NumberToWordsConverter {

    private static final String[] units = {
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", 
        "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static final String[] scales = {
        "", "Thousand", "Million", "Billion"
    };

    public static String convert(int number) {
        if (number == 0) return "Zero";
        return convertToWords(number).trim();
    }

    private static String convertToWords(int number) {
        String words = "";
        
        if (number >= 1000) {
            words += convertToWords(number / 1000) + " " + scales[1] + " ";
            number %= 1000;
        }
        if (number >= 100) {
            words += convertToWords(number / 100) + " Hundred ";
            number %= 100;
        }
        if (number >= 20) {
            words += tens[number / 10] + " ";
            number %= 10;
        }
        if (number > 0) {
            words += units[number] + " ";
        }

        return words;
    }
}
