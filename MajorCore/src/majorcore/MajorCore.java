package majorcore;

/**
 * Created by chanakya on 7/11/14.
 */

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;

import java.io.*;
import java.util.*;

public class MajorCore {
    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("article.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        String text = "";
        while ((line = reader.readLine()) != null) {
            text += (line);
        }
        Reader read_ = new StringReader(text);
        DocumentPreprocessor doc_pro = new DocumentPreprocessor(read_);
        ArrayList<String> all_sentences = new ArrayList<String>();
        for(List<HasWord> sentence : doc_pro){
            StringBuilder sentence_whole = new StringBuilder();
            for (HasWord ele : sentence) {
                if(sentence_whole.length()>1) {
                    sentence_whole.append(" ");
                }
                sentence_whole.append(ele);
            }
            all_sentences.add(sentence_whole.toString());
        }
        ArrayList<String> all_sentences_copy = new ArrayList<String>();
        all_sentences_copy.addAll(all_sentences);
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        Map<Integer, CorefChain> coref = document.get(CorefChainAnnotation.class);

        for (Map.Entry<Integer, CorefChain> entry : coref.entrySet()) {
            CorefChain chain = entry.getValue();
            if (chain.getMentionsInTextualOrder().size() <= 1)
                continue;

            CorefChain.CorefMention mention = chain.getRepresentativeMention();
            String repr= "";
            List<CoreLabel> tks = document.get(CoreAnnotations.SentencesAnnotation.class).get(mention.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);

            for (int i = mention.startIndex - 1; i < mention.endIndex - 1; i++)
                repr+= tks.get(i).get(CoreAnnotations.TextAnnotation.class) + " ";
            repr= repr.trim();
           for (CorefChain.CorefMention m : chain.getMentionsInTextualOrder()) {
                String mentioned = "";
                tks = document.get(CoreAnnotations.SentencesAnnotation.class).get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
              for (int i = m.startIndex - 1; i < m.endIndex - 1; i++)
                    mentioned += tks.get(i).get(CoreAnnotations.TextAnnotation.class) + " ";
                mentioned = mentioned.trim();
                String to_replace = " "+mentioned+" ";
                String replace_with = " "+repr+" ";
                String temp = all_sentences.get(m.sentNum - 1).toString().replace(to_replace, replace_with);
                all_sentences.remove(m.sentNum - 1);
                all_sentences.add(m.sentNum - 1, temp);
            }
        }
        PrintWriter writer = new PrintWriter("ParseOutput.txt", "UTF-8");
        for(String s : all_sentences)
            writer.println(s);
        writer.close();
    }
}

