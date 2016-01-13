package ch.fhnw.cpib.compiler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;

import ch.fhnw.cpib.compiler.context.RoutineTable;
import ch.fhnw.cpib.compiler.context.Scope;
import ch.fhnw.cpib.compiler.context.StoreTable;
import ch.fhnw.cpib.compiler.context.Range;
import ch.fhnw.cpib.compiler.exception.ContextError;
import ch.fhnw.cpib.compiler.exception.GrammarError;
import ch.fhnw.cpib.compiler.parser.AbsTree;
import ch.fhnw.cpib.compiler.parser.ConcTree;
import ch.fhnw.cpib.compiler.parser.Parser;
import ch.fhnw.cpib.compiler.scanner.ITokenList;
import ch.fhnw.cpib.compiler.scanner.Scanner;
import ch.fhnw.lederer.virtualmachineFS2015.*;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray.CodeTooSmallError;
import ch.fhnw.lederer.virtualmachineFS2015.IVirtualMachine.ExecutionError;

public final class Compiler {
    
    private static final int STORE_SIZE = 1024;

    private static RoutineTable routineTable = new RoutineTable();
    private static StoreTable globalStoreTable = new StoreTable();
    private static StoreTable arrayRangeTable = new StoreTable();
    private static HashMap<String,Integer> identTable = new HashMap<String,Integer>();
    private static Scope scope = null;
    private static IVirtualMachine vm /*= new VirtualMachine(null, STORE_SIZE)*/; 
    private static CodeArray codeArray = new CodeArray(STORE_SIZE);
    
    public static IVirtualMachine getVM() {
        return vm;
    }
    
    public static StoreTable getArrayStoreTable() {
    	return arrayRangeTable;
    }
    
    public static void addIdentTable(String name, int i) {
        identTable.put(name, new Integer(i));
    }
    
    public static HashMap<String, Integer> getIdentTable() {
        return identTable;
    }
    
    public static void addArrayStoreTable(String name, Range range) {
    	arrayRangeTable.addStore(name, range);
    }
    
    public static StoreTable getGlobalStoreTable() {
        return globalStoreTable;
    }

    public static RoutineTable getRoutineTable() {
        return routineTable;
    }

    public static Scope getScope() {
        return scope;
    }
    
    public static ICodeArray getcodeArray() {
        return codeArray;
    }

    public static void setScope(final Scope scope) {
        Compiler.scope = scope;
    }

    private Compiler() {
        throw new AssertionError("Instantiating utility class...");
    }

    public static void compile(BufferedReader source) throws IOException, GrammarError, ContextError, ExecutionError, CodeTooSmallError {

        System.out.println("> Code: \n");
        String currentLine = "";
        StringBuilder program = new StringBuilder();

        while ((currentLine = source.readLine()) != null) {
            System.out.println(currentLine);
            program.append(currentLine + "\n");
        }

        System.out.println("\n > Scanning:\n");

        ITokenList tokenList = null;
        Scanner scanner = new Scanner();
        try {
            tokenList = scanner.scan(new BufferedReader(new StringReader(program.toString())));
            System.out.println(tokenList.toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("\n > Parsing:\n");

        final Parser parser = new Parser(tokenList);
        final ConcTree.Program concTree = parser.parse();
        System.out.println(concTree.toString());
        
        System.out.println(" > Converter:\n");
        
        final AbsTree.Program absTree = concTree.toAbstract();
        System.out.println(absTree.toString());
        
        System.out.println(" > Checker:\n");
        absTree.check();
        
        System.out.println("No error found! \n");
        
        System.out.println(" > Generating Code:\n");
        absTree.code(0);
        System.out.println("\n > Maschine Code: \n");
        codeArray.resize();
        vm = new VirtualMachine(codeArray, STORE_SIZE);
    }

    public static void main(String[] args) {

        try {
        	//InputStreamReader source = new InputStreamReader(new FileInputStream("res/codeVM.iml"));
        	//InputStreamReader source = new InputStreamReader(new FileInputStream("res/codeDebugIn.iml"));
            InputStreamReader source = new InputStreamReader(new FileInputStream("res/code.iml"));
            //InputStreamReader source = new InputStreamReader(new FileInputStream("res/varia.iml"));
            //InputStreamReader source = new InputStreamReader(new FileInputStream("res/demo1.iml"));
            Compiler.compile(new BufferedReader(source));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
