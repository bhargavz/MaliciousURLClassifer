import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.acos;
import java.util.HashMap;

class bayesian {
    String fileName = "D2.txt", fileName2="D2.txt", fileName3 = "D2.txt", fileName4="D2.txt";
    String classification_for = null;
    String cls = "";
    String per = "";
    String line = null;
    HashMap feat;
    HashMap clas;
    HashMap samp;
    HashMap rclas;
    int indx1 = 0, indx2 = 0;
    int flag = 0;
    int tot_sample = 0;
    float E = (float)2.71828;
    float PI = (float) acos(-1);
    
    float sq(float a) {
        return a * a;
    }

    int countTLDs(String s)
    {
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

    int countTokens(String s)
    {
        int ctr = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '/' || c == '?' || c == '.' || c == '=' || c == '-' || c == '&') {
                ctr++;
            }
        }

        return ctr;
    }

    float mod(float a)
    {
	return a > 0 ? a : -a;
    }

    int counter(String sr, char c) 
    {
        int ctr = 0;
        for (int i = 0; i < sr.length(); i++) {
            if (c == sr.charAt(i)) {
                ctr++;
            }
        }
        return ctr;
    }
    
    int countDigits(String sr) 
    {
        int ctr = 0;
        for (int i = 0; i < sr.length(); i++) {
            if (sr.charAt(i) <= '9' && sr.charAt(i) >= '0') {
                ctr++;
            }
        }
        return ctr;
    }
    
    bayesian(String inp) {
        feat = new HashMap();
        clas = new HashMap();
        samp = new HashMap();
        rclas = new HashMap();
        
        try {
            FileReader fileReader = new FileReader(fileName);
            FileReader fileReader2 = new FileReader(fileName2);
            FileReader fileReader3 = new FileReader(fileName3);
            FileReader fileReader4 = new FileReader(fileName4);
            
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                String ob = "";
                indx1 = 0;
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == ' ') {
                        if (flag == 0) {
                            if (ob.length() != 0) {
                                classification_for = ob;
                                flag = 1;
                            }
                        } else {
                            if (ob.length() != 0) {
                                feat.put(ob, indx1++);
                            }
                        }
                        //System.out.println(ob);
                        ob = "";
                    } else {
                        ob = ob + line.charAt(i);
                    }
                }
                feat.put(ob, indx1++);
                break;
            }	
            bufferedReader.close();
            
            
            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
            flag = 0;
            indx2 = 0;
            while((line = bufferedReader2.readLine()) != null) {
           
                if (flag != 0) {
                    String stmp = "";
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ' ') {
                            if (stmp.length() != 0) break;
                        } else {
                            stmp += line.charAt(i);
                        }
                    }
                    if (samp.containsKey(stmp) == false) {
                        samp.put(stmp, 1);
                    } else {
                        int ij = (int)samp.get(stmp);
                        ij++;
                        samp.remove(stmp);
                        samp.put(stmp, ij);
                    }
                    tot_sample++;
                    if (clas.containsKey(stmp) == false) {
                        clas.put(stmp, indx2++);
                        rclas.put(indx2-1, stmp);
                    }
                }
                flag = 1;
            }	
            bufferedReader2.close();
            
            float a[][][] = new float[clas.size()][feat.size()][3];
            for (int i = 0; i < clas.size(); i++) {
                for (int j = 0; j < clas.size(); j++) {
                    for (int k = 0; k < 3; k++) {
                        a[i][j][k] = 0;
                    }
                }
            }
            // hello world
            BufferedReader bufferedReader3 = new BufferedReader(fileReader3);
            int flag = 0;
            while((line = bufferedReader3.readLine()) != null) {
                String stmp = "";
                if (flag == 0) {
                    flag = 1;
                    continue;
                }
                int i = 0;
                for ( ; i < line.length(); i++) {
                    if (line.charAt(i) != ' ') {
                        stmp += line.charAt(i);
                    } else {
                        if (stmp.length() != 0) {
                            break;
                        }
                    }
                }
                int pindx = (int)clas.get(stmp);
                int jindx = 0;
                stmp = "";
                for ( ; i < line.length(); i++) {
                    if (line.charAt(i) != ' ') {
                        stmp += line.charAt(i);
                        if (i + 1 == line.length()) {
                            float val = Float.parseFloat(stmp);
                            a[pindx][jindx][0] += val;
                            a[pindx][jindx++][2] += 1;
                        }
                    } else {
                       // System.out.println(stmp);
                        if (stmp.length () != 0) {
                            float val = Float.parseFloat(stmp);
                            a[pindx][jindx][0] += val;
                            a[pindx][jindx++][2] += 1;
                        }
                        stmp = "";
                    } 
                }
                
            }	
            bufferedReader3.close();
            
     //       System.out.println("Mean");
            for (int i = 0; i < clas.size(); i++) {
                for (int j = 0; j < feat.size(); j++) {
                    a[i][j][0] /= a[i][j][2];
      //              System.out.print(a[i][j][0] + " ");
                }
                System.out.println("");
            }

            BufferedReader bufferedReader4 = new BufferedReader(fileReader4);
            flag = 0;
            while((line = bufferedReader4.readLine()) != null) {
                String stmp = "";
                if (flag == 0) {
                    flag = 1;
                    continue;
                }
                int i = 0;
                for ( ; i < line.length(); i++) {
                    if (line.charAt(i) != ' ') {
                        stmp += line.charAt(i);
                    } else {
                        if (stmp.length() != 0) {
                            break;
                        }
                    }
                }
                
                int pindx = (int)clas.get(stmp);
                int jindx = 0;
                stmp = "";
                for ( ; i < line.length(); i++) {
                    if (line.charAt(i) != ' ') {
                        stmp += line.charAt(i);
                        if (i + 1 == line.length()) {
                            float val = Float.parseFloat(stmp);
                            a[pindx][jindx][1] += sq(a[pindx][jindx][0]-val);
                            jindx++;
                        }
                    } else {
                        if (stmp.length () != 0) {
                            float val = Float.parseFloat(stmp);
                            a[pindx][jindx][1] += sq(a[pindx][jindx][0]-val);
                            jindx++;
                        }
                        stmp = "";
                    } 
                }
                
            }	
            bufferedReader4.close();
            
       //     System.out.println("Variance");
            for (int i = 0; i < clas.size(); i++) {
                for (int j = 0; j < feat.size(); j++) {
                    int ctr = (int)a[i][j][2];
                    a[i][j][1] /= (ctr > 1) ? (a[i][j][2]-1) : (1);
       //             System.out.print(a[i][j][1] + " ");
                }
                System.out.println("");
            }
            double flag2 = 0;
            float g[] = new float[feat.size()];
            g[0] = inp.length();
            g[1] = counter(inp, '/');
            g[2] = counter(inp, '-');
            g[3] = countDigits(inp);
            g[4] = countTokens(inp);
            g[5] = countTLDs(inp);
            if (g[5] > 1) {
                flag2 = 1;
            }
    //        System.out.println("iput " + g[5]);
            
            float max1 = -(1<<20);
            int cl = -1;
            float tot = 0;      
           
            for (int i = 0; i < clas.size(); i++) {
                float pr_cls = 1;
     //           System.out.print("cls ");
                for (int j = 0; j < feat.size(); j++) {
                    double ans = 1.0 / Math.sqrt(PI*2 * a[i][j][1]);
                    double an = a[i][j][1];
                    if (an < 0.1) {
                        an += 0.1;
                    }
                    
                    ans *= Math.pow(E, -1 * sq(g[j]-a[i][j][0]) / (2*an));
                    //System.out.print(ans + " ");
                    pr_cls *= ans;
                }
                String stmp = (String) rclas.get(i);
                pr_cls *= (int)samp.get(stmp) / (float)tot_sample;
                if (pr_cls > max1) {
                    max1 = pr_cls;
                    cl = i;
                }
   //             System.out.println("pr_cls " + pr_cls);
                tot += pr_cls;
            }
            //System.out.println(rclas.get(cl));
            //System.out.println((max1 / tot) * 100);
            cls = (String)rclas.get(cl);
            if (flag2 > 0) {
                cls = "Mal";
            }
            per = String.valueOf((max1 / tot) * 100);
            
        }   catch(FileNotFoundException ex) {
            System.out.println("error1");
        }   catch(IOException ex) {
            System.out.println("error2");
        }
    }
    
    String getClas() {
        return cls;
    }
    
    String getPer() {
        return per;
    }
}