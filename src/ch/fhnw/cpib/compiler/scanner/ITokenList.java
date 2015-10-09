package ch.fhnw.cpib.compiler.scanner;

public interface ITokenList {
   
    void add(IToken token);
    void reset();
    IToken nextToken();
    String toString();
}
