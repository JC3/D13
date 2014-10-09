package d13.apps.offline;

import java.io.FileReader;

public class DisengageEmailToJava {

    public static void main(String[] args) throws Exception {

        FileReader in = new FileReader("2014_disengage_email.txt");
        StringBuilder piece = new StringBuilder();
        int ch;
        
        while ((ch = in.read()) != -1) {
            if ((ch == '<') && (piece.length() > 0)) {
                outputPiece(piece.toString());
                piece = new StringBuilder();
            }
            piece.append((char)ch);
        }
        
        if (piece.length() > 0)
            outputPiece(piece.toString());
        
    }
    
    static void outputPiece (String piece) {
        System.out.println("s.append(\"" + piece.replaceAll("\"", "\\\\\"") + "\");");
    }

}
