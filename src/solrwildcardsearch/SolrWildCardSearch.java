package solrwildcardsearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * @author lahiru
 */
public class SolrWildCardSearch {
    
    /**
     * Creates solr syntaxed xml files from given words.csv file.
     * Output XML file directory path property - solrWildcardXMLPath 
     * Input CSV file path property - solrWildcardWordListPath
     * @return Returns a list of rejected words
     * @throws IOException 
     */
    public LinkedList<String> createXMLs() throws IOException {
        // delete all xml files from xml directory before start creating xml files
        Util.deleteAllXMLs(SysProperty.getProperty("solrWildcardXMLPath"));
        
        // create xml files
        XMLCreator x = new XMLCreator();
        LinkedList<String> rejectedWords = x.parseToXMLs();
        return rejectedWords;
    }
    
    /**
     * Uploads xml files at directory property - parsedXMLPath
     * Uses support of solr/example/post.jar - solrPostJarPath
     * Summary of post.jar written to summary file - solrWildcardUploadSummaryFile
     * @param solrCore core the data to be sent
     * @throws IOException 
     */
    public void uploadXMLsToSolr(String solrCore) throws IOException {
        // creates summary file for appending
        File summaryFile = new File(SysProperty.getProperty("solrWildcardUploadSummaryFile"));
        PrintWriter writer = new PrintWriter(new FileOutputStream(summaryFile, true));
        XMLUploader uploader = new XMLUploader();
        writer.write("----------------------------------------------");
        writer.write("\nuploading starts...\ntime: " + new Date().toString() + "\n\n");
        
        String summary = uploader.uploadXMLs(solrCore); // upload xml files
        
        writer.write(summary);
        writer.flush();
        writer.close();
    }
    
    /**
     * 
     * @param word searching word (may include ? or * signs)
     * @param core the solr core the query to be sent
     * @param useEncoded use encoded search if the true. Else do search on sinhala word
     * @return list of matching words
     */
    public LinkedList<String> searchWord(String word, String core, boolean useEncoded) {
        WildCardQuery query = new WildCardQuery();
        
        if(useEncoded) return query.wildCardSearchEncoded(word, core);
        else           return query.wildCardSearch(word, core);
    }
    
    public static void main(String[] args) throws IOException {
        SolrWildCardSearch x = new SolrWildCardSearch();
        //x.uploadXMLsToSolr("academic");
        //x.createXMLs();
        
    }
}
