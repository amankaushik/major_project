major_project
=============

Major Project (2014-2015)
Generation of concept maps for natural language text.

MajorCore:

Uses the StanfordCoreNLP library to remove corefernces from a peice of text(read from article.txt). Resolves the coreferences and write the new text to a file(ParseOutput.txt).

MajorCoreII:

Uses the OLLIE Extractor on the ParseOutput.txt, extracts relationships form each node and store them in a file(OllieOutput.txt). Each record in OllieOutput.txt is of the format <entity>\t<relation>\t<entity> ("\t" as the delimiter).

add_to_graph.py:

Writes the records in OllieOutput.txt to the neo4j data base. 
**I don't have neo4j configured on my system, so the file is untested. Run it on your system to check. **

