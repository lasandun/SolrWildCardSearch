/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package solrwildcardsearch;

/**
 *
 * @author lahiru
 */
public class Test {
    
    public static void main(String[] args) {
        WordParser x = new WordParser();
        String word = "කෘතිය(2)";
        System.out.println(x.encode(word));
        System.out.println(x.decode(x.encode(word)));
//        System.out.println(x.isPossibleToParse(word));
    }
    
}
