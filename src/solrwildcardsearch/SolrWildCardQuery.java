package solrwildcardsearch;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

/**
 *
 * @author lahiru
 */
public class SolrWildCardQuery {
    
    String serverUrl;

    public SolrWildCardQuery() {
        serverUrl = SysProperty.getProperty("solrServerURL");
    }
    
    LinkedList<String> wildCardSearchEncoded(String word, String collection) {
        String encoded = new SolrWildCardSinhalaWordParser().encode(word);
        String query = "select?q=encoded:" + encoded + "&fl=content&rows=1400000";
        LinkedList<String> wordList = execQuery(query, collection);
        return wordList;
    }
    
    LinkedList<String> wildCardSearch(String word, String collection) {
        String query = "select?q=content:" + word + "&fl=content&rows=1400000";
        LinkedList<String> wordList = execQuery(query, collection);
        return wordList;
    }
    
    LinkedList<String> execQuery(String q, String collection) {
        LinkedList<String> matchingList = new LinkedList<String>();
        long time = -1;
        try {
            // create connection and query to Solr Server
            URL query = new URL(serverUrl + "solr/" + collection + "/" + q);
            time = System.nanoTime();
            URLConnection connection = query.openConnection();
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            String content = "";
            // read the result to a string
            while ((line = inputStream.readLine()) != null) {
                content += line;
            }
            inputStream.close();
            time = System.nanoTime() - time;
            // read the query time from the xml file
            OMElement documentElement = AXIOMUtil.stringToOM(content);
            OMElement resultDoc = documentElement.getFirstChildWithName(new QName("result"));
            Iterator childElem = resultDoc.getChildElements();
            int count = 0;
            while(childElem.hasNext()) {
                OMElement strDoc = (OMElement) childElem.next();
                ++count;
                
                // add word to list
                Iterator strIter = strDoc.getChildElements();
                OMElement word = (OMElement) strIter.next();
                String w = word.getText();
                matchingList.addLast(w);
            }
        } catch (XMLStreamException ex) {
            Logger.getLogger(SolrWildCardQuery.class.getName()).log(Level.SEVERE, null, ex);
        } catch(MalformedURLException ex) {
            Logger.getLogger(SolrWildCardQuery.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex) {
            Logger.getLogger(SolrWildCardQuery.class.getName()).log(Level.SEVERE, null, ex);
        } 
        //System.out.println("time: " + (time / 1000000));
        return matchingList;
    }
    
    private String encordeWildcardSyntaxToTURL(String word) {
        String parts[] = word.split("\\?");
        if(parts.length == 1) {
            if(parts[0].length() == word.length()) {
                try {
                    return URLEncoder.encode(parts[0], "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(SolrWildCardQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
                try {
                    return (URLEncoder.encode(parts[0], "UTF-8") + "?");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(SolrWildCardQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        }
        
        String converted = "";
        for(int i = 0; i < parts.length; ++i) {
            try {
                converted += URLEncoder.encode(parts[i], "UTF-8") + "?";
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SolrWildCardQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        converted = converted.substring(0, converted.length() - 1);
        return converted;
    }
    
    
    public static void main(String[] args) throws Exception {
        String word = "මහ*";
        SolrWildCardQuery x = new SolrWildCardQuery();
        LinkedList<String> list = x.wildCardSearchEncoded(word, "collection1");
        System.out.println("word: " + word);
        //System.out.println("encoded: " + new SolrWildCardSinhalaWordParser().encode(word));
        System.out.println("count: " + list.size());
        for(String s : list) {
            System.out.println(s);
        }
       
    }
}

