package solrwildcardsearch;

/**
 *
 * @author lahiru
 */
public class SinhalaLetter {
    
    private boolean visargaya;
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
        visargaya = false;
        sinhalaVowelSign = 0;
        this.sinhalaLetter = sinhalaLetter + 1;
        searchLetter = null;
        nonSinhalaChar = null;
    }
    
    // for non-sinhala chars
    public SinhalaLetter() {
        visargaya = false;
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
    
    public void setVisargayaSign(boolean visargaya) {
        this.visargaya = visargaya;
    }
    
    public String getValue() {
        String val = "";
        int visargayaVal = visargaya ? 1 : 0;
        val = String.format("%02d", sinhalaLetter) + String.format("%02d", sinhalaVowelSign) + visargayaVal;
        return val;
    }
    
}
