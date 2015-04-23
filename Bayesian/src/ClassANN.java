/* This is Artificial Neural Network using Feed Forward and Back Propagation.
 Made by -
 Parvez (IIT2011187)
 Tarun Sharma (IIT2011212)
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import static java.lang.Math.acos;
import java.util.HashMap;

public class ClassANN {
    /* These the variable used in this ANN */

    String fileName = "D2.txt", fileName2 = "D2.txt", fileName3 = "D2.txt", fileName4 = "D2.txt";
    String classification_for = null;
    String line = null;
    HashMap feat;
    int counter = 0;
    HashMap clas;
    HashMap samp;
    HashMap rclas;
    int indx1 = 0, indx2 = 0;
    int flag = 0;
    int tot_sample = 0;
    float E = (float) 2.71828;
    float PI = (float) acos(-1);

    public static int numEpochs = 5;
    public static int numInputs = 7;
    public static int numHidden = 4;
    public static int numPatterns = 30000;
    public static double inlearn = 0.7;
    public static double outlearn = 0.07;
    public double cls;
    public double per;

    public static int patNum;
    public static double errThisPat;
    public static double outPred;
    public static double RMSerror;

    public static double[][] trainInputs = new double[numPatterns][numInputs];
    public static double[] trainOutput = new double[numPatterns];

    public static double[] hiddenVal = new double[numHidden];

    public static double[][] weightsIH = new double[numInputs][numHidden];
    public static double[] weightsHO = new double[numHidden];

    /* Computes Square of a Number x */
    float sq(float a) {
        return a * a;
    }

    /* Compute the number of TLDs in the string */
    int countTLDs(String s) {
        int ans = 0;
        if (s.contains(".edu.")) {
            ans += 1;
        }
        if (s.contains(".gov.")) {
            ans += 1;
        }
        if (s.contains(".com.")) {
            ans += 1;
        }
        if (s.contains(".org")) {
            ans += 1;
        }
        if (s.contains(".in") && s.contains(".eu")) {
            ans += 2;
        }

        return ans;
    }

    /* Count number of tokens */
    int countTokens(String s) {
        int ctr = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '/' || c == '?' || c == '.' || c == '=' || c == '-' || c == '&') {
                ctr++;
            }
        }

        return ctr;
    }

    /* Returns absolute value of a Number */
    float mod(float a) {
        return a > 0 ? a : -a;
    }

    /* Counts the frequency of a particular character in the string */
    int counter(String sr, char c) {
        int ctr = 0;
        for (int i = 0; i < sr.length(); i++) {
            if (c == sr.charAt(i)) {
                ctr++;
            }
        }
        return ctr;
    }

    /* Counts the number of digits in the string */
    int countDigits(String sr) {
        int ctr = 0;
        for (int i = 0; i < sr.length(); i++) {
            if (sr.charAt(i) <= '9' && sr.charAt(i) >= '0') {
                ctr++;
            }
        }
        return ctr;
    }

    /* This is the constructor of the Main Class */
    public ClassANN(String s) throws IOException {

        initWeights();

        initData();

        for (int j = 0; j <= numEpochs; j++) {
            for (int i = 0; i < numPatterns; i++) {
                patNum = (int) ((Math.random() * numPatterns) - 0.001);
                //System.out.println("patNum " + patNum);
                //patNum = i;
                calcNet();

                WeightChangesHO();
                WeightChangesIH();
            }
            calcOverallError();
        }

        displayResults();
        calcNetOut(s);
    }

    public static void calcNet() {
        for (int i = 0; i < numHidden; i++) {
            hiddenVal[i] = 0.0;

            for (int j = 0; j < numInputs; j++) {
                hiddenVal[i] = hiddenVal[i] + (trainInputs[patNum][j] * weightsIH[j][i]);
            }

            hiddenVal[i] = tanh(hiddenVal[i]);
        }

        outPred = 0.0;

        for (int i = 0; i < numHidden; i++) {
            outPred = outPred + hiddenVal[i] * weightsHO[i];
        }

        errThisPat = outPred - trainOutput[patNum];
        return;
    }

    public void calcNetOut(String s) throws IOException {
        double ar[] = new double[numInputs];
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        ar[0] = s.length() / 100.0;
        ar[1] = counter(s, '/') / 10.0;
        ar[2] = counter(s, '-') / 10.0;
        ar[3] = countDigits(s) / 10.0;
        ar[4] = countTokens(s) / 10.0;
        ar[5] = countTLDs(s) / 10.0;
        double flag2 = (int) countTLDs(s);
        int k = 0;
        if (flag2 > 1) {
            k = 1;
        }
        ar[6] = 1;
        System.out.println("inputs are " + numInputs);
        for (int i = 0; i < numHidden; i++) {
            hiddenVal[i] = 0.0;

            for (int j = 0; j < numInputs; j++) {
                hiddenVal[i] = hiddenVal[i] + (ar[j] * weightsIH[j][i]);
            }

            hiddenVal[i] = tanh(hiddenVal[i]);
        }

        outPred = 0.0;

        for (int i = 0; i < numHidden; i++) {
            outPred = outPred + hiddenVal[i] * weightsHO[i];
        }
        per = outPred;

        //System.out.println("777777777 " + outPred);
        if (outPred > 0) {
            System.out.println("Non_malicious");
            cls = 1;
        } else {
            System.out.println("Malicious");
            cls = 0;
        }
        if (k > 0) {
            cls = 0;
        }
        errThisPat = outPred - trainOutput[patNum];
        //System.out.println("counter leads to " + counter);
    }

    public double getClas() {
        return cls;
    }

    public double getPer() {
        return per;
    }

    public static void WeightChangesHO() {
        for (int k = 0; k < numHidden; k++) {
            double weightChange = outlearn * errThisPat * hiddenVal[k];
            weightsHO[k] = weightsHO[k] - weightChange;
        }
    }

    public static void WeightChangesIH() { 
        for (int i = 0; i < numHidden; i++) {
            for (int k = 0; k < numInputs; k++) {
                double wchange = 1 - (hiddenVal[i] * hiddenVal[i]);
                wchange = wchange * weightsHO[i] * errThisPat * inlearn;
                wchange = wchange * trainInputs[patNum][k];
                weightsIH[k][i] = weightsIH[k][i] - wchange;
            }
        }
    }

    public static void initWeights() {

        for (int j = 0; j < numHidden; j++) {
            double var = Math.random();
            weightsHO[j] = (var - 0.5) / 2;
            for (int i = 0; i < numInputs; i++) {
                var = Math.random();
                weightsIH[i][j] = (var - 0.5) / 5;
            }
        }
    }

    public void initData() throws FileNotFoundException, IOException {
        System.out.println("initialising data");
        FileReader fileReader = new FileReader(fileName);
        FileReader fileReader2 = new FileReader(fileName2);
        FileReader fileReader3 = new FileReader(fileName3);
        FileReader fileReader4 = new FileReader(fileName4);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while ((line = bufferedReader.readLine()) != null) {
            String ob = "";
            indx1 = 0;
            counter++;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == ' ') {
                    if (flag == 0) {
                        if (ob.length() != 0) {
                            classification_for = ob;
                            flag = 1;
                        }
                    } else {
                        if (ob.length() != 0) {
//                                feat.put(ob, indx1++);
                        }
                    }
                    //System.out.println(ob);
                    ob = "";
                } else {
                    ob = ob + line.charAt(i);
                }
            }
//                feat.put(ob, indx1++);

        }
        bufferedReader.close();
        numPatterns = counter;

        BufferedReader bufferedReader3 = new BufferedReader(fileReader3);
        int flag = 0;
        int indx1 = 0;
        int indx2 = 0;
        while ((line = bufferedReader3.readLine()) != null) {
            String stmp = "";
            if (flag == 0) {
                flag = 1;
                continue;
            }

            int i = 0;
            for (; i < line.length(); i++) {
                if (line.charAt(i) != ' ') {
                    stmp += line.charAt(i);
                } else {
                    if (stmp.length() != 0) {
                        break;
                    }
                }
            }
            if (stmp.equals("Non")) {
                trainOutput[indx1] = 1;
            } else {
                trainOutput[indx1] = -1;
            }
//                int pindx = (int)clas.get(stmp);
            int jindx = 0;
            indx2 = 0;
            stmp = "";
            for (; i < line.length(); i++) {
                if (line.charAt(i) != ' ') {
                    stmp += line.charAt(i);
                    if (i + 1 == line.length()) {
                        float val = Float.parseFloat(stmp);
                        if (indx2 == 0) {
                            val = (float) (val / 100.0);
                        } else {
                            val = (float) (val / 10.0);
                        }
                        trainInputs[indx1][indx2] = val;
                        //System.out.println("value of val " + val);

                        indx2++;
                        //    System.out.println("value is here " + val);
                    }
                } else {
                    // System.out.println(stmp);
                    if (stmp.length() != 0) {
                        float val = Float.parseFloat(stmp);
                        //    System.out.println("value is here " + val);
                        if (indx2 == 0) {
                            val = (float) (val / 100.0);
                        } else {
                            val = (float) (val / 10.0);
                        }
                        trainInputs[indx1][indx2] = val;
                        //  System.out.println("value of val " + val);
                        indx2++;
                    }
                    stmp = "";
                }
            }
            trainInputs[indx1][indx2] = 1;
            indx1++;

        }
        bufferedReader3.close();

        /*        for (int i = 0; i < numPatterns; i++) {
         for (int j = 0; j < numInputs; j++) {
         System.out.print("     " + trainInputs[i][j] + "     ++  ");
         }
         System.out.println(" parvez ");
         System.out.println(trainOutput[i]);
         }
    
         */
    }

    public static double tanh(double x) {
        if (x > 20) {
            return 1;
        } else if (x < -20) {
            return -1;
        } else {
            double a = Math.exp(x);
            double b = Math.exp(-x);
            return (a - b) / (a + b);
        }
    }

    public static void displayResults() 
    {
        for (int i = 0; i < numPatterns; i++) {
            patNum = i;
            calcNet();
        }
    }

    public static void calcOverallError() 
    {
        RMSerror = 0.0;
        for (int i = 0; i < numPatterns; i++) {
            patNum = i;
            calcNet();
            RMSerror = RMSerror + (errThisPat * errThisPat);
        }
        RMSerror = RMSerror / numPatterns;
        RMSerror = java.lang.Math.sqrt(RMSerror);
    } 
}
