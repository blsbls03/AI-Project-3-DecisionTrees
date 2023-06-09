import java.util.*;

public class Utility {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    
    // turn Object into double
    public static Double toDouble(Object o) {
        if (o instanceof Number) {return ((Number) o).doubleValue();}
        return null;
    }

    // returns log2 of a number
    public static double log2(double n) {
        if (n==0) n = 0.00000000000000000000000000000000001;
        return (Math.log(n) / Math.log(2));
    }

    //checks whether a string is numeric or not
    public static boolean isNumeric(String str) { 
        try {  
            Double.parseDouble(str);  
            return true;
        }
        catch(NumberFormatException e) {  
            return false;  
        }  
    }

    // creates a list with interval bounds given a list of values and the amount of intervals wanted
    public static double[] createIntervals(List<Object> list_of_values, int number_of_intervals) {

        double[] values = new double[list_of_values.size()];
        double[] intervalBounds = new double[number_of_intervals + 1];

        for (int i = 0; i < list_of_values.size(); i++) {
            values[i] = Utility.toDouble(list_of_values.get(i));
        }

        Arrays.sort(values);

        double range = values[values.length - 1] - values[0];
        double intervalSize = range / number_of_intervals;
        for (int i = 0; i < number_of_intervals + 1; i++) {
            intervalBounds[i] = values[0] + (intervalSize * i);
        }

        double scale = Math.pow(10, 3);
        for (int i = 0; i < intervalBounds.length; i++) {
            intervalBounds[i] = Math.round(intervalBounds[i]*scale)/scale;
        }

        return intervalBounds;
    }

}
