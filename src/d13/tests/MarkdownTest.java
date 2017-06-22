package d13.tests;

import org.markdown4j.Markdown4jProcessor;

public class MarkdownTest {

    public static void main (String[] args) throws Exception {

        Markdown4jProcessor md = new Markdown4jProcessor();
        
        String markdown = 
                 "This is a test.\n\n"
                +"Some *italic*, **bold**, ***both***.\n\n"
                +"Header\n===\n\n"
                +"Header 2\n---\n\n"
                +"- list\n"
                +"- list\n"
                +"- list\n\nAwesome.\n\n"
                +"1. list\n"
                +"2. list\n"
                +"3. list\n\n"
                +"[Link](http://www.google.com)\n"
                +"[Ref Link][1]\n\n"
                +"  [1]: http://www.catoftheday.com\n";
        
        String html = md.process(markdown);
        System.out.println(html);
                

    }

}
