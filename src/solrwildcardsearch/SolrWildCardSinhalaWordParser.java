package solrwildcardsearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author lahiru
 */
public class SolrWildCardSinhalaWordParser {
    
    private final String sinhalaChars[] = {"අ", "ආ", "ඇ", "ඈ", "ඉ", "ඊ", "උ", "ඌ", "ඍ", "ඎ", "ඏ",
                             "ඐ", "එ", "ඒ", "ඓ", "ඔ", "ඕ", "ඖ", "ක", "ඛ", "ග", "ඝ", "ඞ", "ඟ",
                             "ච", "ඡ", "ජ", "ඣ", "ඤ", "ඥ", "ඦ", "ට", "ඨ", "ඩ", "ඪ", "ණ", "ඬ", "ත", "ථ", "ද",
                             "ධ", "න", "ඳ", "ප", "ඵ", "බ", "භ", "ම", "ඹ", "ය", "ර", "ල", 
                             "ව", "ශ", "ෂ", "ස", "හ", "ළ", "ෆ", "ං" , "\u200d"};
    
    private final String sinhalaVowelSigns[] = {"්", "ා", "ැ", "ෑ", "ි", "ී", "ු", "ූ", "ෘ", "ෙ", "ේ", "ෛ", "ො", "ෝ",
                              "ෞ", "ෟ", "ෲ", "ෳ", "෴" };
    
    private final String visargayaSign = "ඃ";
    
    private final String letterSeparator = "a";
    
    private final SinhalaVowelLetterFixer vowelFixer;
    
    public SolrWildCardSinhalaWordParser() {
        vowelFixer = new SinhalaVowelLetterFixer();
    }
    
    private String fixVowels(String str) {
        str = vowelFixer.fixText(str, false);
        return str;
    }
    
    private int  isASinhalaLetter(String c) {
        if(c.length() > 1) {
            System.out.println("char length should be 1 : " + c);
            System.exit(-1);
        }
        for(int i = 0; i < sinhalaChars.length; ++i) {
            if(c.equals(sinhalaChars[i])) {
                return i;
            }
        }
        return -1;
    }
    
    private int  isASinhalaVowelLetter(String c) {
        if(c.length() > 1) {
            System.out.println("char length should be 1 : " + c);
            System.out.println("Exiting...");
            System.exit(-1);
        }
        for(int i = 0; i < sinhalaVowelSigns.length; ++i) {
            if(c.equals(sinhalaVowelSigns[i])) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean  isSinhalaVisargayaLetter(String c) {
        if(c.length() > 1) {
            System.out.println("char length should be 1 : " + c);
            System.out.println("Exiting...");
            System.exit(-1);
        }
        if(c.equals(visargayaSign)) return true;
        return false;
    }
    
    public String encode(String str) {
        str = fixVowels(str);
        String parts[] = str.split("");
        LinkedList<SinhalaLetter> letterList = new LinkedList<SinhalaLetter>();
        
        for(int i = 1; i < parts.length; ++i) {
            String c = parts[i];
            
            // handle * and ?
            if(c.equals("*")) {
                letterList.addLast(new SinhalaLetter("*"));
                continue;
            } else if(c.equals("?")) {
                letterList.addLast(new SinhalaLetter("?????"));
                continue;
            }
            
            int letter = isASinhalaLetter(c);
            if(letter >= 0) {
                letterList.addLast(new SinhalaLetter(letter));
                continue;
            }
            
            int vowelLetter = isASinhalaVowelLetter(c);
            if(vowelLetter >= 0) {
                if(letterList.isEmpty()) {
                    System.out.println("vowel char at start of the word :" + str);
                    return null;
                }
                SinhalaLetter last = letterList.getLast();
                last.setVowel(vowelLetter);
                continue;
            }
            
            else if(isSinhalaVisargayaLetter(c)) {
                SinhalaLetter last = letterList.getLast();
                last.setVisargayaSign(true);
                continue;
            }
            
            else { // handle non-sinhala chars
                SinhalaLetter nonSinhalaLetter = new SinhalaLetter();
                nonSinhalaLetter.setNonSinhalaChar(c);
                letterList.addLast(nonSinhalaLetter);
                continue;
                /*System.out.println("Error char :" + c);
                System.exit(-1);*/
            }
        }
        
        String encoded = "";
        for(SinhalaLetter letter : letterList) {
            if(letter.isSearchLetter()) {
                encoded += letter.getSearchLetter() + letterSeparator;
            }
            else if(letter.isANonSinhalaChar()) {
                String l = letter.getNonSinhalaChar();
                encoded += (l + l + l + l + l) + letterSeparator;
            }
            else {
                encoded += letter.getValue() + letterSeparator;
            }
        }
        return encoded;
    }
    
    public String decode(String str) {
        if(str == null) return null;
        
        String parts[] = str.split(letterSeparator);
        String decoded = "";
        for(String s : parts) {
            int val;
            
            try {
                val = Integer.parseInt(s);
            } catch(NumberFormatException ex) {
                // s isn't representing a non-sinhala char
                decoded += s.charAt(0);
                continue;
            }
            
            int sinhalaLetter;
            int sinhalaVowelSign;
            
            sinhalaLetter = val / 1000;
            val = val % 1000;
            decoded += sinhalaChars[sinhalaLetter - 1];
            
            sinhalaVowelSign = val /  10;
            if(sinhalaVowelSign > 0) {
                decoded += sinhalaVowelSigns[sinhalaVowelSign - 1];
            }
            val = val % 10;
            
            if(val > 0) decoded += visargayaSign;
        }
        
        return decoded;
    }
    
    public void check(String w, int no) {
        String e = encode(w);
        String d = decode(e);
        if(!w.equals(d)) {
            System.out.println("number : " + (++no));
            System.out.println(w);
            System.out.println(e);
            System.out.println(d);
            System.out.println("matching status : " + (w.equals(d)));
        }
    }
    
    public static void main(String[] args) throws IOException{
        int no = 0;
        SolrWildCardSinhalaWordParser x = new SolrWildCardSinhalaWordParser();
        BufferedReader br = new BufferedReader(new FileReader("/home/lahiru/Desktop/words.txt"));
        String w;
        while((w = br.readLine()) != null) {
            x.check("කේව්‍යාගෝ", ++no);
        }
        
    }
}
