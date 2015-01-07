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
public class WildCardQuery {
    
    String serverUrl;

    public WildCardQuery() {
        serverUrl = SysProperty.getProperty("solrServerURL");
    }
    
    // do searching using the encoded stirng. Vowel sign problems won't occur
    public LinkedList<String> wildCardSearchEncoded(String word, String core) {
        String encoded = new WordParser().encode(word);
        String query = "select?q=encoded:" + encoded + "&fl=content&rows=1400000";
        LinkedList<String> wordList = execQuery(query, core);
        return wordList;
    }
    
    // simple wildcard search using solr
    public LinkedList<String> wildCardSearch(String word, String core) {
        String query = "select?q=content:" + word + "&fl=content&rows=1400000";
        LinkedList<String> wordList = execQuery(query, core);
        return wordList;
    }
    
    // execute given query and return result word list
    private LinkedList<String> execQuery(String q, String core) {
        LinkedList<String> matchingList = new LinkedList<String>();
        long time = -1;
        try {
            // create connection and query to Solr Server
            URL query = new URL(serverUrl + "solr/" + core + "/" + q);
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
            Logger.getLogger(WildCardQuery.class.getName()).log(Level.SEVERE, null, ex);
        } catch(MalformedURLException ex) {
            Logger.getLogger(WildCardQuery.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex) {
            Logger.getLogger(WildCardQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return matchingList;
    }
    
    public static void main(String[] args) throws Exception {
        String word = "මහ*";
        WildCardQuery x = new WildCardQuery();
//        LinkedList<String> list = x.wildCardSearchEncoded(word, "collection1");
//        System.out.println("word: " + word);
//        //System.out.println("encoded: " + new SolrWildCardSinhalaWordParser().encode(word));
//        System.out.println("count: " + list.size());
//        for(String s : list) {
//            System.out.println(s);
//        }
        
//        System.out.println(x.encodeSearchingWord("මහ??"));
        String s = new WordParser().encode("මහ*");
        System.out.println(s);
        System.out.println(new WordParser().decode(s));
       
    }
}

