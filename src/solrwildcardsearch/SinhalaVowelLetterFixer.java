package solrwildcardsearch;

import java.util.Hashtable;

/**
 *
 * @author lahiru
 */

//
// Use fixText() to fix incorrect vowel usages of a text
//
public class SinhalaVowelLetterFixer {
    
    private String fixedText;
    private String lastLetter;
    private String lastVowelSign;
    
    private final boolean debug;
    
    private static final String sinhalaChars[] = {"අ", "ආ", "ඇ", "ඈ", "ඉ", "ඊ", "උ", "ඌ", "ඍ", "ඎ", "ඏ",
                             "ඐ", "එ", "ඒ", "ඓ", "ඔ", "ඕ", "ඖ", "ක", "ඛ", "ග", "ඝ", "ඞ", "ඟ",
                             "ච", "ඡ", "ජ", "ඣ", "ඤ", "ඥ", "ඦ", "ට", "ඨ", "ඩ", "ඪ", "ණ", "ඬ", "ත", "ථ", "ද",
                             "ධ", "න", "ඳ", "ප", "ඵ", "බ", "භ", "ම", "ඹ", "ය", "ර", "ල", 
                             "ව", "ශ", "ෂ", "ස", "හ", "ළ", "ෆ", "ං", "ඃ" , "\u200d" };
    
    private static final String sinhalaVowelSigns[] = {"්", "ා", "ැ", "ෑ", "ි", "ී", "ු", "ූ", "ෘ", "ෙ", "ේ", "ෛ", "ො", "ෝ",
                              "ෞ", "ෟ", "ෲ", "ෳ", "෴" };
    
    private final Hashtable<String, String> vowelSignMap;
    
    // Default - false. Will be enabled for tokenizing for
    // wildcard search using solr
    private boolean appendUnresolvedConsecutiveVowelChars; 

    public SinhalaVowelLetterFixer() {
        fixedText = "";
        lastVowelSign = "";
        lastLetter = "";
        vowelSignMap = new Hashtable<String, String>();
        initVowelSignMap();
        appendUnresolvedConsecutiveVowelChars = true;
        debug = SysProperty.getProperty("debug").equals("yes");
    }
    
    private void initVowelSignMap() {
        vowelSignMap.put("ෙ" + "්", "ේ");
        vowelSignMap.put("්" + "ෙ", "ේ");
        
        vowelSignMap.put("ෙ" + "ා", "ො");
        vowelSignMap.put("ා" + "ෙ", "ො");
        
        vowelSignMap.put("ේ" + "ා", "ෝ");
        vowelSignMap.put("ො" + "්", "ෝ");
        
        vowelSignMap.put("ෙෙ", "ෛ");
        
        vowelSignMap.put("ෘෘ", "ෲ");
        
        vowelSignMap.put("ෙ" + "ෟ", "ෞ");
        vowelSignMap.put("ෟ" + "ෙ", "ෞ");
        
        vowelSignMap.put("ි" + "ී", "ී");
        vowelSignMap.put("ී" + "ි", "ී");
        
        
        // duplicating same symbol
        vowelSignMap.put("ේ" + "්", "ේ");
        vowelSignMap.put("ේ" + "ෙ", "ේ");
        
        vowelSignMap.put("ො" + "ා", "ො");
        vowelSignMap.put("ො" + "ෙ", "ො");
        
        vowelSignMap.put("ෝ" + "ා", "ෝ");
        vowelSignMap.put("ෝ" + "්", "ෝ");
        vowelSignMap.put("ෝ" + "ෙ", "ෝ");
        vowelSignMap.put("ෝ" + "ේ", "ෝ");
        vowelSignMap.put("ෝ" + "ො", "ෝ");
        
        vowelSignMap.put("ෞ" + "ෟ", "ෞ");
        vowelSignMap.put("ෞ" + "ෙ", "ෞ");
        
        
        // special cases - may be typing mistakes
        //ො + ෟ
        vowelSignMap.put("ො" + "ෟ", "ෞ");
        vowelSignMap.put("ෟ" + "ො", "ෞ");
    }
    
    private boolean isSinhalaLetter(String c) {
        for(String s : sinhalaChars) {
            if(s.equals(c)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSinhalaVowelSign(String c) {
        for(String s : sinhalaVowelSigns) {
            if(s.equals(c)) {
                return true;
            }
        }
        return false;
    }
    
    private void appendChar(String c) {
        if(c.length() > 1) {
            if(debug) System.out.println("Char length should be 1 : " + c);
            System.exit(-1);
        }
        
        if(isSinhalaLetter(c)) {
            fixedText += lastLetter + lastVowelSign;
            lastLetter = c;
            lastVowelSign = "";
        }
        else if(isSinhalaVowelSign(c)) {
            if(lastLetter.equals("")) {
                if(debug) System.out.println("Error : First letter can't be a vowel sign : " + c);
                return;
            }
            if(lastVowelSign.equals("")) {
                lastVowelSign = c;
            }
            else {
                 String fixedVowel = addVoewlSigh(c);
                 if(fixedVowel == null) {
                     if(c.equals(lastVowelSign)) { // consecutive 2 same vowel symbol
                         return;
                     }
                     else {
                         if(appendUnresolvedConsecutiveVowelChars) {
                             lastVowelSign += c;
                         }
                         else {
                             if(debug) System.out.println("Error : can't fix " + lastVowelSign + " + " + c);
                             return;
                         }
                         return;
                     }
                 }
                 lastVowelSign = fixedVowel;
            }
        } else {
            fixedText += lastLetter + lastVowelSign + c;
            lastVowelSign = "";
            lastLetter = "";
        }
    }
    
    private String addVoewlSigh(String c) {
        String connected = lastVowelSign + c;
        return vowelSignMap.get(connected);
    }
    
    private void appendText(String str) {
        for(int i = 0; i < str.length(); ++i) {
            String c = str.charAt(i) + "";
            appendChar(c);
        }
        flush();
    }
    
    private void flush() {
        fixedText += lastLetter + lastVowelSign;
        lastLetter = "";
        lastVowelSign = "";
    }
    
    private String getFixedText() {
        flush();
        return fixedText;
    }
    
    private void clear() {
        fixedText = "";
        lastVowelSign = "";
        lastLetter = "";
    }
    
    // take only first vowel sign if consecutive unsolvable vowel signs present
    private void setAppendUnresolvedConsecutiveVowelChars(boolean val) {
        appendUnresolvedConsecutiveVowelChars = val;
    }
    
    //
    // returns the fixed text
    // use setAppendUnresolvedConsecutiveVowelChars=true (Eg : ්ි -> ්ි),
    // unless only the first vowel letter from consecutive unsolvable vowel letters
    // is required be used (Eg : ්ි -> ්).
    //
    public String fixText(String text, boolean appendUnresolvedConsecutiveVowelChars) {
        setAppendUnresolvedConsecutiveVowelChars(appendUnresolvedConsecutiveVowelChars);
        clear();
        appendText(text);
        return getFixedText();
    }
    
}
