/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package solrwildcardsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lahiru
 */
public class XMLUploader {
    
    private final String java;
    private final String solrPostJarPath;
    private final String xmlDir;
    
    private final boolean debug = true;
    
    public XMLUploader() {
        java            = SysProperty.getProperty("java");
        solrPostJarPath = SysProperty.getProperty("solrPostJarPath");
        //xmlDir          = SysProperty.getProperty("parsedXMLPath");
        xmlDir = "/home/lahiru/Desktop/post/";
    }
    
    public boolean uploadXMLs(String core) throws IOException {
        String sysVariable = " -Durl=http://localhost:8983/solr/" + core + "/update "; // check -h of post.jar
        String command = java + sysVariable + " -jar " + solrPostJarPath + " " + Util.refactorDirPath(xmlDir) + "*.xml";
        if(debug) System.out.println("command: " + command);
        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
        InputStream solrInputStream = p.getInputStream();
        BufferedReader solrStreamReader = new BufferedReader(new InputStreamReader(solrInputStream));
        String line = "";
        
        // reading output from post.jar to find the status of operation
        while ((line = solrStreamReader.readLine ()) != null) {
            if(line.startsWith("No files or directories matching")) {
                if(debug) System.out.println("Error while uploading.");
                return false;
            }
        }
        return true;
    }
    
    public static void main(String[] args) {
        try {
            XMLUploader test = new XMLUploader();
            test.uploadXMLs("academic");
        } catch (IOException ex) {
            Logger.getLogger(XMLUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
