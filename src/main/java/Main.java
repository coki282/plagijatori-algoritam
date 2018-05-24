import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static final int GROUPS = 8;

    static final int zero_minus = 0;
    static final int zero_plus = 1;
    static final int a_minus = 2;
    static final int a_plus = 3;
    static final int b_minus = 4;
    static final int b_plus = 5;
    static final int ab_minus = 6;
    static final int ab_plus = 7;

    static final int ID = 0;
    static final int FREQUENCY = 1;
    static final int LAST_DONATION = 2;
    static final int BLOOD_GROUP = 3;
    static final int SEX = 4;
    static final int DISTANCE = 5;

    static public int days_past = 14;

    static int[] currentSupplies = {83,224,103,172,60,30,15,23};
    //static int[] currentSupplies = {50,130,60,150,50,30,8,20};
    static int[] minimumSupplies = {38,115,46,100,38,23,8,16};
    static int[] maximumSupplies = {78,240,96,210,82,50,18,36};
    static int[] optimalSupplies = {58,177,71,155,60,36,13,26};
    static int[] weeklyCosumption = {35,105,42,91,35,21,7,14};
    static int[] collectedBlood = new int[GROUPS];
    static int[] nextWeekSupplies = new int[GROUPS];

    public static ArrayList<String> listOfLines = new ArrayList<>();
    public static ArrayList<String> finalList = new ArrayList<>();
    public static ArrayList<Donator> donatorslList = new ArrayList<>();
    public static ArrayList<Integer> idsToCall = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //init("donori.txt");
        init("nextweekdonors.txt");

        doAlgorithm();
    }

    public static void init(String filepath) throws IOException {
        BufferedReader bufReader = new BufferedReader(new FileReader(filepath));
        String line = bufReader.readLine();
        System.out.println(line);
        int firstLineFlag = 0;
        while (line != null) {
            if(firstLineFlag != 0) {
                listOfLines.add(line);
            }
            firstLineFlag = 1;
            line = bufReader.readLine();
        }
        bufReader.close();
    }



    public static double lossFunction(int i) {
        int Omin = minimumSupplies[i];
        int Omax = maximumSupplies[i];
        int x = currentSupplies[i];
        double Oz = (Omin + Omax)/2.0;
        int r = maximumSupplies[i]- minimumSupplies[i];

        if(x <= Omin) {
            return (200./r)*(Omin-x)+35;
        } else if(Omin < x && x <= ((r/3.) + Omin)) {
            return (100./r)*(Omin + r/3.  - x);
        } else if(((r/3.) + Omin) < x && x <= (2*(r/3.) + Omin)) {
            return  0;
        } else if((2*(r/3.) + Omin) < x && x <= Omax){
            return (100./r)*(x-(2./3)*r - Omin);
        } else if(x > Omax) {
            return (200./r)*(x - Omax) + 35;
        }
        return 0;
    }

    public static void parseLines() {
        for(String listLine : listOfLines) {
            String[] lineComponents = listLine.split(",");
            String id = lineComponents[ID];
            String sex = lineComponents[SEX];
            String bloodGroup = lineComponents[BLOOD_GROUP];
            Integer distance = Integer.parseInt(lineComponents[DISTANCE]);
            String frequency = lineComponents[FREQUENCY];
            String lastDonation = lineComponents[LAST_DONATION];

            if(lineComponents[SEX].equals("M")) {
                if((Integer.parseInt(lineComponents[LAST_DONATION]) + days_past) < 90) {
                    continue;
                } else {
                    finalList.add(listLine);
                    donatorslList.add(new Donator(id, sex, lastDonation, bloodGroup, frequency, distance));
                }
            }
            if(lineComponents[SEX].equals("Z")) {
                if((Integer.parseInt(lineComponents[LAST_DONATION]) + days_past) < 120) {
                    continue;
                } else {
                    finalList.add(listLine);
                    donatorslList.add(new Donator(id, sex, lastDonation, bloodGroup, frequency, distance));
                }
            }
        }
    }

    public static void sortByDistance() {
        donatorslList.sort((o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));
    }

    public double totaLoss() {
        double total = 0;
        for(int i = 0; i < GROUPS; i++) {
            total += lossFunction(i);
        }
        return total;
    }

    public void collect() {

    }

    public void afterCollect(){
        for(int i = 0; i < GROUPS; i++) {
            currentSupplies[i] = currentSupplies[i] + collectedBlood[i] - weeklyCosumption[i];
        }
    }

    public void doCorrection() {
        for(int i = 0; i < GROUPS; i++) {
            if(currentSupplies[i] < minimumSupplies[i] || currentSupplies[i] > maximumSupplies[i]) {
                continue;
            } else {
                if (currentSupplies[ab_plus] < optimalSupplies[ab_plus]) {
                    mixBlood(ab_plus, ab_minus);
                    mixBlood(ab_plus, b_plus);
                    mixBlood(ab_plus, b_minus);
                    mixBlood(ab_plus, a_plus);
                    mixBlood(ab_plus, a_minus);
                    mixBlood(ab_plus, zero_plus);
                    mixBlood(ab_plus, zero_minus);
                }
                if (currentSupplies[ab_minus] < optimalSupplies[ab_minus]) {
                    mixBlood(ab_minus, b_minus);
                    mixBlood(ab_minus, a_minus);
                    mixBlood(ab_minus, zero_minus);
                }
                if (currentSupplies[b_plus] < optimalSupplies[b_plus]) {
                    mixBlood(b_plus, zero_minus);
                    mixBlood(b_plus, b_minus);
                    mixBlood(b_plus, zero_plus);
                }
                if (currentSupplies[b_minus] < optimalSupplies[b_minus]) {
                    mixBlood(b_minus, zero_minus);
                }
                if (currentSupplies[a_plus] < optimalSupplies[a_plus]) {
                    mixBlood(a_plus, zero_minus);
                    mixBlood(a_plus, a_minus);
                    mixBlood(a_plus, zero_plus);
                }
                if (currentSupplies[a_minus] < optimalSupplies[a_minus]) {
                    mixBlood(a_minus, zero_minus);
                }
                if (currentSupplies[zero_plus] < optimalSupplies[zero_plus]) {
                    mixBlood(zero_plus, zero_minus);
                }
            }
        }
    }

    public static void createOutputFile() throws IOException {
        int br = 0;
        System.out.println(donatorslList.size());
        //higher freqs
        for(Donator donator : donatorslList) {
            int group = switchGroup(donator);
            double r = (maximumSupplies[group] - minimumSupplies[group])/3.;
            double freq = Double.parseDouble(donator.getFrequency());
            if((currentSupplies[group] < (optimalSupplies[group] + 1.5*r + days_past*r + weeklyCosumption[group])) && freq >= 2) {
                currentSupplies[group]++;
                idsToCall.add(Integer.parseInt(donator.getId()));
                br++;
            }
        }
        //lower freqs
        for(Donator donator : donatorslList) {
            int group = switchGroup(donator);
            double r = (maximumSupplies[group] - minimumSupplies[group])/3.;
            double freq = Double.parseDouble(donator.getFrequency());
            if((currentSupplies[group] < (optimalSupplies[group] + 1.5*r + days_past*r + weeklyCosumption[group])) && freq < 2) {
                currentSupplies[group]++;
                idsToCall.add(Integer.parseInt(donator.getId()));
                br++;
            }
        }

        System.out.println(br);

        String filePrint = "";
        String inputPrint = "";


        Model model = new Model(idsToCall, days_past);
        List<Integer> calledIds = evaluate(model);

        for(Integer id : idsToCall) {
            filePrint += id + ",";
        }
        for(Integer id : calledIds) {
            inputPrint += id + ",";
        }
        //remove last comma
        inputPrint = inputPrint.substring(0, inputPrint.length() - 1);
        filePrint = filePrint.substring(0, filePrint.length() - 1);

        try (PrintWriter out = new PrintWriter("outputdonators.txt")) {
            out.println(filePrint);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try (PrintWriter out = new PrintWriter("inputdonors.txt")) {
            out.println(filePrint);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void doAlgorithm(){
        parseLines();
        sortByDistance();
        try {
            createOutputFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            updateDonators();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateDonators() throws IOException {
        File fout = new File("nextweekdonors.txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        ArrayList<String> printingLines = new ArrayList<>();

        Model model = new Model(idsToCall, days_past);
        List<Integer> calledIds = evaluate(model);
        //System.out.println(model.getInput_ids());

        for(String line : listOfLines) {
            //System.out.println(line);
            String[] linecomps = line.split(",");
            if(calledIds.contains(Integer.parseInt(linecomps[0]))) {
                linecomps[LAST_DONATION] = "0";
            }
            String temp = "";
            for(String comp : linecomps) {
                temp += comp + ",";
            }
            temp = temp.substring(0, temp.length() -1);
            printingLines.add(temp);
        }
        String printme = "id,frequency,last_donation,blood_group,sex,distance\n";
        for(String line : printingLines) {
            printme += line + "\n";
        }
        //System.out.println(printme);
        bw.write(printme.toString());
        bw.close();

    }

    public static void prepareNextWeek() {
        //evaluateIds();
        //doCollection():
        //afterCollection();
    }

    public static void mixBlood(int dest, int src) {
        if(currentSupplies[dest] >= optimalSupplies[dest]) return;
        if(currentSupplies[src] <= optimalSupplies[src]) return;
        while(currentSupplies[src] > optimalSupplies[src]) {
            currentSupplies[dest]++;
            currentSupplies[src]--;
        }
    }

    public static int switchGroup(Donator donator) {
        int group = 0;
        switch (donator.getBloodGroup()) {
            case "A+" : group = a_plus; break;
            case "A-" : group = a_minus; break;
            case "B+" : group = b_plus; break;
            case "B-" : group = b_minus; break;
            case "AB+" : group = ab_plus; break;
            case "AB-" : group = ab_minus; break;
            case "0+" : group = zero_plus; break;
            case "0-" : group = zero_minus; break;
            default: break;
        }
        return group;
    }

    public static List<Integer> evaluate(Model model) throws IOException {
        String postUrl = "http://hackaton.westeurope.cloudapp.azure.com/api/evaluate";// put in your url
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(gson.toJson(model));//gson.tojson() converts your pojo to json
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        List<Integer> list = new ArrayList<Integer>();
        for(String string : responseString.split(",")){
            list.add(Integer.parseInt(string));
        };

        return list;
    }


}
