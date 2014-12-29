package solrwildcardsearch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.XMLOutputFactory2;

/**
 *
 * @author lahiru
 */
public class SolrXMLCreator {

    private final int MAX_WORD_OF_FILE = 100000;
    private OMFactory factory;
    private OMElement root;
    private OMElement add;
    private int fileCount;
    public int rejectedWordsCount;
    public int acceptedWordCount;
    private WordParser parser;
    
    private boolean debug = true; // set true for debugging
    
    public SolrXMLCreator() {
        parser = new solrwildcardsearch.WordParser();
    }
    
    private void initDoc() {
        factory = OMAbstractFactory.getOMFactory();
        root    = factory.createOMElement(new QName("root"));
        add     = factory.createOMElement(new QName("add"));
    }
    
    /*
     * create solr supported xml files which consist of words which were extracted
     * from a given text file.
     */
    public LinkedList<String> parseToXMLs(String file) throws IOException {
        
        initDoc(); //  reset the XML dom
        fileCount          = 0;
        rejectedWordsCount = 0;
        acceptedWordCount  = 0;
        
        int count = 0;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        LinkedList<String> rejectedWords = new LinkedList<String>();
        
        while((line = br.readLine()) != null) {
            try{
                // tokenize and read data from file
                line = line.replaceAll(" ", "");
                line = line.replaceAll("\"", "");
                String parts[] = line.split(",");
                String id   = parts[0];
                String word = parts[1];
                String freq = parts[2];

                addWord(id, word, freq);
                ++count;
                acceptedWordCount++;

                // create doc of 'MAX_WORD_OF_FILE' or less words
                if(count > MAX_WORD_OF_FILE) {
                    try {
                        if(debug) System.out.println("wrote" + fileCount);
                        writeToFile("/home/lahiru/Desktop/parsed/temp" + fileCount + ".xml");
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(SolrXMLCreator.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (XMLStreamException ex) {
                        Logger.getLogger(SolrXMLCreator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    fileCount++;
                    count = 0;
                    initDoc();
                }
            } catch(Exception e) {
                if(debug) System.out.println("exception at line:" + line);
                rejectedWordsCount++;
                rejectedWords.addLast(line);
            }
        }
        
        // emptying the buffered data by writing them to a file
        if(count != 0) {
            try {
                if(debug) System.out.println("exception at line:" + line);
                writeToFile("/home/lahiru/Desktop/parsed/temp" + fileCount + ".xml");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SolrXMLCreator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (XMLStreamException ex) {
                Logger.getLogger(SolrXMLCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        br.close();
        if(debug) {
            System.out.println("total accepted words: " + acceptedWordCount);
            System.out.println("total rejected words: " + rejectedWordsCount);
        }
        return rejectedWords;
    }
    
    // add a word to current XML doc
    private void addWord(String id, String word, String freq) {
        String encoded = parser.encode(word);
        OMElement doc = factory.createOMElement(new QName("doc"));
        OMElement idField = factory.createOMElement(new QName("field"));
        idField.addAttribute("name", "id", null);
        idField.setText(id);

        OMElement contentField = factory.createOMElement(new QName("field"));
        contentField.addAttribute("name", "content", null);
        contentField.setText(new QName(word));
        
        OMElement encodedField = factory.createOMElement(new QName("field"));
        encodedField.addAttribute("name", "encoded", null);
        encodedField.setText(new QName(encoded));
        
        OMElement frequencyField = factory.createOMElement(new QName("field"));
        frequencyField.addAttribute("name", "frequency", null);
        frequencyField.setText(new QName(freq));
        
        doc.addChild(idField);
        doc.addChild(contentField);
        doc.addChild(encodedField);
        doc.addChild(frequencyField);
        add.addChild(doc);
    }
    
    // write the current root doc to XML file
    private void writeToFile(String fileName) throws FileNotFoundException, XMLStreamException {
        root.addChild(add);
        OutputStream out = new FileOutputStream(fileName);
        XMLStreamWriter writer = XMLOutputFactory2.newInstance().createXMLStreamWriter(out);
        root.serialize(writer);
        writer.flush();
    }
    
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException, IOException {
        SolrXMLCreator x = new SolrXMLCreator();
        LinkedList<String> rejected = x.parseToXMLs("/home/lahiru/Desktop/words.csv");
        for(String s : rejected) {
            System.out.println(s);
        }
    }
}
