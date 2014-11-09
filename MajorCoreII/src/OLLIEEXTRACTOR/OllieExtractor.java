package OLLIEEXTRACTOR;

/**
 * Created by chanakya on 9/11/14.
 */

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;

import edu.knowitall.ollie.Ollie;
import edu.knowitall.ollie.OllieExtraction;
import edu.knowitall.ollie.OllieExtractionInstance;
import edu.knowitall.tool.parse.MaltParser;
import edu.knowitall.tool.parse.graph.DependencyGraph;

public class OllieExtractor {
    private Ollie ollie;
    private MaltParser maltParser;
    private static final String MALT_PARSER_FILENAME = "engmalt.linear-1.7.mco";

    public OllieExtractor() throws MalformedURLException {
        scala.Option<File> nullOption = scala.Option.apply(null);
        maltParser = new MaltParser(new File(MALT_PARSER_FILENAME));
        ollie = new Ollie();
    }

    public Iterable<OllieExtractionInstance> extract(String sentence) {
        DependencyGraph graph = maltParser.dependencyGraph(sentence);
        Iterable<OllieExtractionInstance> extrs = scala.collection.JavaConversions.asJavaIterable(ollie.extract(graph));
        return extrs;
    }

    public static void main(String args[]) throws MalformedURLException, FileNotFoundException, UnsupportedEncodingException {
        System.out.println(OllieExtractor.class.getResource("/logback.xml"));
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("ParseOutput.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        ArrayList<String> text = new ArrayList<String>();
        try {
            while ((line = reader.readLine()) != null) {
                text.add(line.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter("OllieOutput.txt", "UTF-8");
        OllieExtractor ollieWrapper = new OllieExtractor();
        for(String sentence : text) {
            Iterable<OllieExtractionInstance> extrs = ollieWrapper.extract(sentence);
            for (OllieExtractionInstance inst : extrs) {
                OllieExtraction extr = inst.extr();
                writer.println(extr.arg1().text() + "\t" + extr.rel().text() + "\t" + extr.arg2().text());
            }
        }
        writer.close();
    }
}
