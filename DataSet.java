import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DataSet {

    private List<List<String>> csv = new ArrayList<>(); //csv as given
    private List<List<Object>> all_cols = new ArrayList<>(); //list of all the columns
    private List<Object> pred_col = new ArrayList<>(); //pred column
    private List<String> attributes = new ArrayList<>(); //list of all attributes
    private List<List<Object>> options = new ArrayList<List<Object>>(); //map with the different options by atribute
    private List<Boolean> isNum = new ArrayList<>(); //list that says for each col if its numeric or not
    public int size;
    public int colnum;

    DataSet(String fileName){
        File file = new File(fileName);

        boolean first_line = true;
        int cur_line = 0;
        size = 0;

        try {
            Scanner sc = new Scanner(file);

            while(sc.hasNext()){
                String line = sc.next();
                String[] values = line.split(",");
                List<String> list = new ArrayList<String>(Arrays.asList(values));
                csv.add(list);
                size++;
                if (first_line) {
                    //atributes = Arrays.asList(values);
                    colnum = csv.get(0).size();
                    for (int i = 0; i < colnum; i++) {
                        List<Object> option = new ArrayList<>();
                        List<Object> attribute = new ArrayList<>();
                        all_cols.add(attribute);
                        options.add(option);
                    }
                    first_line = false;
                }
                else {
                    int cur_row = 0;
                    for (List<Object> cur_atr : options) {
                        String value = csv.get(cur_line).get(cur_row);
                        if (Utility.isNumeric(value)) {
                            double value2 = Double.parseDouble(value);
                            all_cols.get(cur_row).add(value2);
                        }
                        else all_cols.get(cur_row).add(value);

                        if (!cur_atr.contains(value)) 
                            cur_atr.add(value);
                        cur_row++;
                    }
                }
                cur_line++;
            }
            sc.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (String value: csv.get(1)) {
            if (Utility.isNumeric(value)) isNum.add(true);
            else isNum.add(false);
        }
        pred_col = all_cols.get(colnum-1);
        isNum.remove(colnum-1);
        isNum.remove(0);
        all_cols.remove(colnum-1);
        all_cols.remove(0);
        options.remove(0);
        size--;
        colnum-= 2;
        attributes = new ArrayList<>(csv.get(0));
        attributes.remove(0);
    }

    DataSet(DataSet ds, int i, Object opt){
        pred_col = new ArrayList<>(ds.getPredCol());
        isNum = new ArrayList<>(ds.getNum());
        attributes = new ArrayList<>(ds.getAttributes());

        for (List<String> sublist : ds.getCSV()) {
            csv.add(new ArrayList<>(sublist));
        }
        for (List<Object> sublist : ds.getallCols()) {
            all_cols.add(new ArrayList<>(sublist));
        }
        for (List<Object> sublist : ds.getAllOptions()) {
            options.add(new ArrayList<>(sublist));
        }
        

        for (int j = 1; j < csv.size(); j++) {
            if (!csv.get(j).get(i+1).equals(opt)) {
                csv.remove(j);
                pred_col.remove(j-1);
                for (int k = 0; k < all_cols.size(); k++) {
                    all_cols.get(k).remove(j-1);
                }
                j--;
            }
            /*else if (isNum.get(i) && !(ds.getCol(i).get(j-1).toString()).equals(opt)) {
                csv.remove(j);
                pred_col.remove(j-1);
                for (int k = 0; k < all_cols.size(); k++) {
                    all_cols.get(k).remove(j-1);
                }
                j--;
            }*/
            //System.out.println(ds.getCol(i).get(j-1).toString() + ' ' + opt);
        }

        for (int m = 1; m < csv.size(); m++) {
            csv.get(m).remove(i+1);
        }
        options.remove(i);
        isNum.remove(i);
        attributes.remove(i);
        all_cols.remove(i);
        csv.get(0).remove(i+1);
        size = csv.size()-1;
        colnum = all_cols.size();
    }

    public String getMostCommonClass(DataSet ds) {
        HashMap<Object, Integer> count = new HashMap<Object, Integer>();
        for (Object element : pred_col) {
            if (!count.containsKey(element)) count.put(element, 1);
            else count.put(element, count.get(element)+1);
        }
        int max = 0;
        String value = "";
        for (Map.Entry<Object, Integer> set : count.entrySet()) {
            if (set.getValue() > max) value = set.getKey().toString();
        }
        return value;
    }

    
    public List<List<String>> getCSV() {
        return csv;
    }

    public List<List<Object>> getallCols() {
        return all_cols;
    }

    public List<Object> getCol(int i) {
        return all_cols.get(i);
    }

    public List<Object> getPredCol() {
        return pred_col;
    }

    public String getAttribute(int i) {
        return attributes.get(i);
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public List<Object> getPredOptions() {
        List<Object> opt = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (!opt.contains(pred_col.get(i))) opt.add(pred_col.get(i));
        }
        return opt;
    }

    public List<Object> getOptions(int i) {
        return options.get(i);
    }

    public List<List<Object>> getAllOptions() {
        return options;
    }

    public List<Boolean> getNum() {
        return isNum;
    }

    public int getPosNum() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (getPredCol().get(i).equals(getPredOptions().get(0))) {
                count++;
            }
        }
        return count;
    }

    public List<DataSet> split(DataSet ds, int i) {
        List<DataSet> new_datasets = new ArrayList<>();
        for (int j = 0; j < options.get(i).size(); j++) {
            DataSet ds2 = new DataSet(ds, i, options.get(i).get(j));
            new_datasets.add(ds2);
        }
        return new_datasets;
    }

    public void format() {
        for (int j = 0; j < colnum; j++) {
            if (isNum.get(j)) {
                int numIntervals = (int)Math.round(1 + Utility.log2(size));
                double[] inti = Intervals.getIntervals(getCol(j), numIntervals);
                List<Object> formatted = new ArrayList<>();
                List<Object> formattedOpt = new ArrayList<>();
                for (int k = 0; k < all_cols.get(j).size(); k++) {
                    List<String> line = new ArrayList<>(csv.get(k+1));
                    for (int i = 0; i < numIntervals; i++) {
                        if (Utility.toDouble(all_cols.get(j).get(k)) >= inti[i] && Utility.toDouble(all_cols.get(j).get(k)) < inti[i+1]) {
                            Object o = "["+ inti[i] + ',' + inti[i+1] + "[";
                            formatted.add(o);
                            line.set(j+1, o.toString());
                            if (!formattedOpt.contains(o)) {
                                formattedOpt.add(o);
                            }
                        }
                    }
                    csv.set(k+1, line);
                }
                options.set(j, formattedOpt);
                all_cols.set(j, formatted);
            }
        }
    }



    public void printCSV(){
        System.out.println("PRINT CSV");
        for(List<String> line: csv) {
            for (String value: line) {
                System.out.print(value + ' ');
            }
            System.out.println("");
        }
    }

    public void printOptions(){
        System.out.println("PRINT OPTIONS");
        for (List<Object> aa : options){
            for (int i = 0; i < aa.size(); i++) {
                System.out.print(aa.get(i).toString() + ' ');
            }
            System.out.println("");
        }
    }

    public void printAllCollumns(){
        for (List<Object> line: all_cols){
            for (Object value: line) {
                System.out.print(value + " ");
            }
            System.out.println("");
        }
    }

    public void printNum(){
        System.out.println("PRINT NUM OR NOT");
        for (Boolean value: isNum) {
            System.out.print(value + " ");
        }
        System.out.println("");

    }
}
