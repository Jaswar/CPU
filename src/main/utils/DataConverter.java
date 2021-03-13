package main.utils;

public class DataConverter {

    /**Class used to convert data to different formats.
     */

    /**Method used to convert an array of booleans to a bit representation.
     * Arrays are reversed.
     * Eg: [true, true, false, true] -> 1011
     *
     * @param data - the boolean array to convert
     * @return - a bit string representation of the data
     */
    public static String convertBoolToBin(boolean[] data) {
        String msg = "";
        for (int i = data.length - 1; i >= 0; i--) {
            if (data[i]) {
                msg += "1";
            }
            else {
                msg += "0";
            }
        }
        return msg;
    }

    /**Method to convert binary number to a list of booleans.
     * Arrays are reversed.
     * Eg: 01001 -> [true, false, false, true, false]
     *
     * @param bits - the binary data
     * @return - the boolean array generated from the binary representation
     */
    public static boolean[] convertBinToBool(String bits) {
        boolean[] bool = new boolean[bits.length()];
        for (int i = bits.length() - 1; i >= 0; i--) {
            int inx = bits.length() - i - 1;
            if (bits.charAt(i) == '0') {
                bool[inx] = false;
            }
            else {
                bool[inx] = true;
            }
        }
        return bool;
    }

    /**Convert binary number to decimal unsigned notation (always positive integer).
     *
     * @param binary - the binary number
     * @return - unsigned integer base 10, converted from the binary number
     */
    public static int convertBinToUnsignedDec(String binary) {
        int sum = 0;
        int mul = 1;
        for (int i = binary.length() - 1; i >= 0; i--) {
            if (binary.charAt(i) == '1') {
                sum += mul;
            }
            mul *= 2;
        }
        return sum;
    }

    /**Convert unsigned integer (positive base 10 integer) to binary representation.
     *
     * @param decimal - the unsigned integer
     * @return - binary representation of the integer
     */
    public static String convertUnsignedDecToBin(int decimal) {
        if (decimal == 0) {
            return "0";
        }
        String binary = "";
        while (decimal > 0) {
            if (decimal % 2 == 1) {
                binary = "1" + binary;
            }
            else {
                binary = "0" + binary;
            }
            decimal /= 2;
        }
        return binary;
    }

    /**Convert boolean array to unsigned integer. Array is reversed.
     *
     * @param data - the boolean array to convert
     * @return - the unsigned integer compute from the input data
     */
    public static int convertBoolToUnsignedDec(boolean[] data) {
        String binary = DataConverter.convertBoolToBin(data);
        return DataConverter.convertBinToUnsignedDec(binary);
    }

    /**Convert unsigned integer to boolean array. Array is reversed.
     *
     * @param decimal - the unsigned integer
     * @return - the generated boolean array
     */
    public static boolean[] convertUnsignedDecToBool(int decimal) {
        String binary = DataConverter.convertUnsignedDecToBin(decimal);
        return DataConverter.convertBinToBool(binary);
    }

    /**Convert binary number to decimal signed notation (positive or negative integer -> 2C notation).
     *
     * @param binary - the binary number
     * @param wordSize - what is the word size the number should be converted from
     * @return - signed integer base 10, converted from the binary number
     */
    public static int convertBinToSignedDec(String binary, int wordSize) {
        if (binary.charAt(0) == '1' && binary.length() == wordSize) {
            String reversed = "";
            for (int i = 0; i < binary.length(); i++) {
                if (binary.charAt(i) == '0') {
                    reversed += "1";
                }
                else {
                    reversed += "0";
                }
            }

            int decimal = DataConverter.convertBinToUnsignedDec(reversed);
            return -(decimal + 1);
        }
        else {
            return DataConverter.convertBinToUnsignedDec(binary);
        }

    }

    /**Convert signed integer (positive or negative base 10 integer) to binary representation (2C notation).
     *
     * @param decimal - the signed integer
     * @param wordSize - what is the word size that the number should be converted from
     * @return - binary representation of the integer
     */
    public static String convertSignedDecToBin(int decimal, int wordSize) {
        if (decimal == 0) {
            return "0";
        }
        if (decimal < 0) {
            decimal += 1;
        }
        String unsignedBin = DataConverter.convertUnsignedDecToBin(Math.abs(decimal));
        String padded = "";
        for (int i = 0; i < wordSize; i++) {
            if (i < unsignedBin.length()) {
                padded = unsignedBin.charAt(unsignedBin.length() - i - 1) + padded;
            }
            else {
                padded = "0" + padded;
            }
        }
        if (decimal < 0) {
            String negated = "";
            for (int i = 0; i < wordSize; i++) {
                if (padded.charAt(i) == '1') {
                    negated += "0";
                }
                else {
                    negated += "1";
                }
            }
            return negated;
        }
        else {
            return padded;
        }
    }

    /**Convert boolean array to signed integer. Array is reversed.
     *
     * @param data - the boolean array to convert
     * @param wordSize - what is the word size the number is in
     * @return - the signed integer compute from the input data
     */
    public static int convertBoolToSignedDec(boolean[] data, int wordSize) {
        String binary = DataConverter.convertBoolToBin(data);
        return DataConverter.convertBinToSignedDec(binary, wordSize);
    }

    /**Convert signed integer to boolean array. Array is reversed.
     *
     * @param decimal - the signed integer
     * @param wordSize - what is the word size the number is in
     * @return - the generated boolean array
     */
    public static boolean[] convertSignedDecToBool(int decimal, int wordSize) {
        String binary = DataConverter.convertSignedDecToBin(decimal, wordSize);
        return DataConverter.convertBinToBool(binary);
    }

}
