import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import ch.fhnw.cpib.compiler.scanner.*;

public class run {
    public static void main(String[] args) {

        try {
            InputStreamReader source = new InputStreamReader(new FileInputStream("res/code.iml"));
            run.compile(new BufferedReader(source));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void compile(BufferedReader source) throws IOException {
        
        System.out.println("Start compiling the following program:");
        String currentLine = "";
        StringBuilder program = new StringBuilder();
        
        while ((currentLine = source.readLine()) != null) {
            System.out.println(currentLine);
            program.append(currentLine+"\n");
        }
        
        System.out.println("Scanning:");
        
        ITokenList tokenList = null;
        Scanner scanner = new Scanner();
        try {
            tokenList = scanner.scan(new BufferedReader(new StringReader(program.toString())));
        } catch(Exception e) {
            System.out.println("ERROR! " + e.getMessage());
        }
        
    }
}
