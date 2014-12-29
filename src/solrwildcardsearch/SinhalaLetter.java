package solrwildcardsearch;

/**
 *
 * @author lahiru
 */
public class SinhalaLetter {
    
    private int sinhalaLetter;
    private int sinhalaVowelSign;
    private String searchLetter;
    private String nonSinhalaChar;
    
    // for search letters *, ?
    public SinhalaLetter(String searchLetter) {
        this.searchLetter = searchLetter;
        nonSinhalaChar = null;
    }

    // for sinhala letters
    public SinhalaLetter(int sinhalaLetter) {
        sinhalaVowelSign = 0;
        this.sinhalaLetter = sinhalaLetter + 1;
        searchLetter = null;
        nonSinhalaChar = null;
    }
    
    // for non-sinhala chars
    public SinhalaLetter() {
        sinhalaLetter = -1;
        sinhalaVowelSign = -1;
        searchLetter = null;
        nonSinhalaChar = "";
    }
    
    public boolean isANonSinhalaChar() {
        return (nonSinhalaChar != null);
    }
    
    public String getNonSinhalaChar() {
        return nonSinhalaChar;
    }
    
    public void setNonSinhalaChar(String nonSinhalaChar) {
        this.nonSinhalaChar = nonSinhalaChar;
    }
    
    public boolean isSearchLetter() {
        return (searchLetter != null);
    }
    
    public String getSearchLetter() {
        return searchLetter;
    }
    
    public void setVowel(int sinhalaVowelSignIndex) {
        this.sinhalaVowelSign = sinhalaVowelSignIndex + 1;
    }
    
    public String getValue() {
        String val = "";
        val = String.format("%02d", sinhalaLetter) + String.format("%02d", sinhalaVowelSign);
        return val;
    }
    
}
