package main.utils;

public class BitInformationConverter {

    /**Class used to convert boolean arrays to more readable formats
     */

    /**Method used to convert an array of booleans to a bit representation.
     * Eg: [true, true, false, true] -> 1101
     *
     * @param data - the boolean array to convert
     * @return - a bit string representation of the data
     */
    public static String convertBoolToBits(boolean[] data) {
        String msg = "";
        for (boolean bool : data) {
            if (bool) {
                msg += "1";
            }
            else {
                msg += "0";
            }
        }
        return msg;
    }

}
