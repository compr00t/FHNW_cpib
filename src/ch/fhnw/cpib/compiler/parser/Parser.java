package ch.fhnw.cpib.compiler.parser;

import ch.fhnw.cpib.compiler.exception.GrammarError;
import ch.fhnw.cpib.compiler.scanner.IToken;
import ch.fhnw.cpib.compiler.scanner.ITokenList;
import ch.fhnw.cpib.compiler.scanner.enums.Terminals;

import ch.fhnw.cpib.compiler.parser.ConcTree.*;
import ch.fhnw.cpib.compiler.scanner.token.*;

public class Parser {

    private final ITokenList tokenList;
    private IToken token;
    private Terminals terminal;

    public Parser(final ITokenList tokenList) {

        this.tokenList = tokenList;
        this.tokenList.reset();

        token = tokenList.nextToken();
        terminal = token.getTerminal();
    }

    private IToken consume(Terminals expectedTerminal) throws GrammarError {

        if (terminal == expectedTerminal) {
            final IToken consumedToken = token;
            if (terminal != Terminals.SENTINEL) {
                token = tokenList.nextToken();
                terminal = token.getTerminal();
            }
            return consumedToken;
        } else {
            throw new GrammarError("terminal expected: " + expectedTerminal + ", terminal found: " + terminal);
        }
    }

    public ConcTree.Program parse() throws GrammarError {
        ConcTree.Program program = program();
        consume(Terminals.SENTINEL);
        return program;
    }

    private ConcTree.Program program() throws GrammarError {
        //System.out.println("program ::= PROGRAM IDENT programParameterList optionalGlobalDeclarations DO blockCmd ENDPROGRAM");
        consume(Terminals.PROGRAM);
        Ident ident = (Ident) consume(Terminals.IDENT);
        ConcTree.ProgramParameterList programParameterList = programParameterList();
        ConcTree.OptionalGlobalDeclarations optionalGlobalDeclarations = optionalGlobalDeclarations();
        consume(Terminals.DO);
        ConcTree.BlockCmd blockCmd = blockCmd();
        consume(Terminals.ENDPROGRAM);
        return new ConcTree.Program(ident, programParameterList, optionalGlobalDeclarations, blockCmd);
    }

    private ConcTree.OptionalGlobalDeclarations optionalGlobalDeclarations() throws GrammarError {
        if (terminal == Terminals.GLOBAL) {
            //System.out.println("optionalGlobalDeclarations ::= GLOBAL declarations");
            consume(Terminals.GLOBAL);
            return new ConcTree.OptionalGlobalDeclarations(declarations());
        } else {
            //System.out.println("optionalGlobalDeclarations ::= epsilon");
            return new ConcTree.OptionalGlobalDeclarationsEpsilon();
        }

    }

    private ConcTree.Declarations declarations() throws GrammarError {
        //System.out.println("declarations ::= declaration repeatingOptionalDeclarations");
        ConcTree.Declaration declaration = declaration();
        ConcTree.RepeatingOptionalDeclarations repeatingOptionalDeclarations = repeatingOptionalDeclarations();
        return new ConcTree.Declarations(declaration, repeatingOptionalDeclarations);

    }

    private ConcTree.RepeatingOptionalDeclarations repeatingOptionalDeclarations() throws GrammarError {
        if (terminal == Terminals.SEMICOLON) {
            //System.out.println("repeatingOptionalDeclarations ::= SEMICOLON declaration repeatingOptionalDeclarations");
            consume(Terminals.SEMICOLON);
            Declaration declaration = declaration();
            RepeatingOptionalDeclarations repeatingOptionalDeclarations = repeatingOptionalDeclarations();
            return new ConcTree.RepeatingOptionalDeclarations(declaration, repeatingOptionalDeclarations);
        } else {
            //System.out.println("repeatingOptionalDeclarations ::= epsilon");
            return new ConcTree.RepeatingOptionalDeclarationsEpsilon();
        }
    }

    private ConcTree.Declaration declaration() throws GrammarError {
        switch (terminal) {
        case FUN:
            //System.out.println("declaration ::= functionDeclaration");
            return functionDeclaration();
        case PROC:
            //System.out.println("declaration ::= procedureDeclaration");
            return procedureDeclaration();
        case CHANGEMODE:
        case IDENT:
            //System.out.println("declaration ::= storageDeclaration");
            return storageDeclaration();
        default:
            throw new GrammarError("declaration got " + terminal);
        }
    }

    private ConcTree.RepeatingOptionalStorageDeclarations repeatingOptionalStorageDeclarations() throws GrammarError {
        if (terminal == Terminals.SEMICOLON) {
            //System.out.println("repeatingOptionalStorageDeclarations ::= SEMICOLON storageDeclaration repeatingOptionalStorageDeclarations");
            consume(Terminals.SEMICOLON);
            StorageDeclaration storageDeclaration = storageDeclaration();
            RepeatingOptionalStorageDeclarations repeatingOptionalStorageDeclarations = repeatingOptionalStorageDeclarations();
            return new RepeatingOptionalStorageDeclarations(storageDeclaration, repeatingOptionalStorageDeclarations);
        } else {
            //System.out.println("RepeatingOptionalStorageDeclarations ::= epsilon");
            return new RepeatingOptionalStorageDeclarationsEpsilon();
        }
    }

    private ConcTree.StorageDeclaration storageDeclaration() throws GrammarError {
        //System.out.println("storageDeclaration ::= optionalChangeMode typedIdent");
        ConcTree.OptionalChangeMode optionalChangeMode = optionalChangeMode();
        ConcTree.TypedIdent typedIdent = typedIdent();
        return new ConcTree.StorageDeclaration(optionalChangeMode, typedIdent);
    }

    private ConcTree.FunctionDeclaration functionDeclaration() throws GrammarError {
        //System.out.println("funDecl ::= FUN IDENT parameterList RETURNS storageDeclaration optionalGlobalImports optionalLocalStorageDeclarations DO blockCmd ENDFUN");
        consume(Terminals.FUN);
        Ident ident = (Ident) consume(Terminals.IDENT);
        ConcTree.ParameterList parameterList = parameterList();
        consume(Terminals.RETURNS);
        ConcTree.StorageDeclaration storeDecl = storageDeclaration();
        ConcTree.OptionalGlobalImports optionalGlobalImports = optionalGlobalImports();
        ConcTree.OptionalLocalStorageDeclarations optionalLocalStorageDeclarations = optionalLocalStorageDeclarations();
        consume(Terminals.DO);
        ConcTree.BlockCmd blockCmd = blockCmd();
        consume(Terminals.ENDFUN);
        return new ConcTree.FunctionDeclaration(ident, parameterList, storeDecl, optionalGlobalImports,
                optionalLocalStorageDeclarations, blockCmd);
    }

    private ConcTree.ParameterList parameterList() throws GrammarError {
        //System.out.println("parameterList ::= LPAREN optionalParameters RPAREN");
        consume(Terminals.LPAREN);
        OptionalParameters optionalParameters = optionalParameters();
        consume(Terminals.RPAREN);
        return new ParameterList(optionalParameters);
    }

    private ConcTree.OptionalParameters optionalParameters() throws GrammarError {
        if (terminal == Terminals.CHANGEMODE || terminal == Terminals.MECHMODE || terminal == Terminals.IDENT) {
            //System.out.println("optionalParameters ::= parameter repeatingOptionalParameters");
            Parameter parameter = parameter();
            RepeatingOptionalParameters repeatingOptionalParameters = repeatingOptionalParameters();
            return new OptionalParameters(parameter, repeatingOptionalParameters);
        } else {
            //System.out.println("optionalParameters ::= epsilon");
            return new ConcTree.OptionalParametersEpsilon();
        }
    }

    private ConcTree.RepeatingOptionalParameters repeatingOptionalParameters() throws GrammarError {
        if (terminal == Terminals.COMMA) {
            //System.out.println("repeatingOptionalParameters ::= COMMA parameter repeatingOptionalParameters");
            consume(Terminals.COMMA);
            Parameter parameter = parameter();
            RepeatingOptionalParameters repeatingOptionalParameters = repeatingOptionalParameters();
            return new RepeatingOptionalParameters(parameter, repeatingOptionalParameters);
        } else {
            //System.out.println("repeatingProgramParameters ::= epsilon");
            return new RepeatingOptionalParametersEpsilon();
        }
    }

    private ConcTree.ProcedureDeclaration procedureDeclaration() throws GrammarError {
        //System.out.println("procedureDeclaration ::= PROC IDENT parameterList optionalGlobalDeclarations optionalLocalStorageDeclarations DO blockCmd ENDPROC");
        consume(Terminals.PROC);
        Ident ident = (Ident) consume(Terminals.IDENT);
        ConcTree.ParameterList parameterList = parameterList();
        ConcTree.OptionalGlobalImports optionalGlobalImports = optionalGlobalImports();
        ConcTree.OptionalLocalStorageDeclarations optionalLocalStorageDeclarations = optionalLocalStorageDeclarations();
        consume(Terminals.DO);
        ConcTree.BlockCmd blockCmd = blockCmd();
        consume(Terminals.ENDPROC);
        return new ConcTree.ProcedureDeclaration(ident, parameterList, optionalGlobalImports,
                optionalLocalStorageDeclarations, blockCmd);
    }

    private ConcTree.OptionalLocalStorageDeclarations optionalLocalStorageDeclarations() throws GrammarError {
        if (terminal == Terminals.LOCAL) {
            //System.out.println("optionalLocalStorageDeclarations ::= LOCAL storageDeclaration repeatingOptionalStorageDeclarations");
            consume(Terminals.LOCAL);
            StorageDeclaration storageDeclaration = storageDeclaration();
            RepeatingOptionalStorageDeclarations repeatingOptionalStorageDeclarations = repeatingOptionalStorageDeclarations();
            return new ConcTree.OptionalLocalStorageDeclarations(storageDeclaration, repeatingOptionalStorageDeclarations);
        } else {
            //System.out.println("optionalLocalStorageDeclarations ::= epsilon");
            return new ConcTree.OptionalLocalStorageDeclarationsEpsilon();
        }
    }

    private ConcTree.ProgramParameterList programParameterList() throws GrammarError {
        //System.out.println("programParameterList ::= LPAREN optionalProgramParameters RPAREN");
        consume(Terminals.LPAREN);
        ConcTree.OptionalProgramParameters optionalProgramParameters = optionalProgramParameters();
        consume(Terminals.RPAREN);
        return new ConcTree.ProgramParameterList(optionalProgramParameters);
    }

    private ConcTree.OptionalProgramParameters optionalProgramParameters() throws GrammarError {
        if (terminal != Terminals.RPAREN) {
            //System.out.println("optionalProgramParameters ::= optionalFLOWMODE optionalCHANGEMODE, typedIdent, repeatingOptionalProgramParameters");
            ConcTree.OptionalChangeMode optionalChangeMode = optionalChangeMode();
            ConcTree.TypedIdent typedIdent = typedIdent();
            ConcTree.RepeatingOptionalProgramParameters repeatingOptionalProgramParameters = repeatingOptionalProgramParameters();
            return new ConcTree.OptionalProgramParameters(optionalChangeMode, typedIdent, repeatingOptionalProgramParameters);
        } else {
            //System.out.println("optionalProgramParameters ::= epsilon");
            return new ConcTree.OptionalProgramParametersEpsilon();
        }
    }

    private ConcTree.TypedIdent typedIdent() throws GrammarError {
        //System.out.println("typedIdent ::= IDENT COLON typeDeclaration");
        Ident ident = (Ident) consume(Terminals.IDENT);
        consume(Terminals.COLON);
        TypeDeclaration typeDeclaration = typeDeclaration();
        return new ConcTree.TypedIdent(ident, typeDeclaration);
    }

    private ConcTree.TypeDeclaration typeDeclaration() throws GrammarError {
        if (terminal == Terminals.IDENT) {
            //System.out.println("typeDeclaration ::= IDENT");
            Ident ident = (Ident) consume(Terminals.IDENT);
            return new TypeDeclarationIdent(ident);

        } else if (terminal == Terminals.TYPE) {
            //System.out.println("typeDeclaration ::= TYPE");
            Type type = (Type) consume(Terminals.TYPE);
            return new TypeDeclarationType(type);
        } else {
            throw new GrammarError("terminal expected: IDENT | TYPE, terminal found: " + terminal);
        }
    }

    private ConcTree.RepeatingOptionalProgramParameters repeatingOptionalProgramParameters() throws GrammarError {
        if (terminal == Terminals.COMMA) {
            //System.out.println("repeatingOptionalProgramParameters ::= COMMA optionalFlowMode optionalChangeMode typedIdent repeatingOptionalProgramParameters");
            consume(Terminals.COMMA);
            ConcTree.OptionalChangeMode optionalChangeMode = optionalChangeMode();
            ConcTree.TypedIdent typedIdent = typedIdent();
            ConcTree.RepeatingOptionalProgramParameters repeatingOptionalProgramParameters = repeatingOptionalProgramParameters();
            return new ConcTree.RepeatingOptionalProgramParameters(optionalChangeMode, typedIdent,
                    repeatingOptionalProgramParameters);
        } else {
            System.out.println("repeatingOptionalProgramParameters ::= epsilon");
            return new ConcTree.RepeatingOptionalProgramParametersEpsilon();
        }
    }

    private ConcTree.Parameter parameter() throws GrammarError {
        //System.out.println("parameter ::= optionalFlowMode optionalMechMode optionalChangeMode typedIdent");
        ConcTree.OptionalMechMode optionalMechMode = optionalMechMode();
        ConcTree.StorageDeclaration storageDeclaration = storageDeclaration();
        return new ConcTree.Parameter(optionalMechMode, storageDeclaration);
    }

    private ConcTree.Cmd cmd() throws GrammarError {
        ConcTree.Cmd ret = null;

        switch (terminal) {
        case SKIP:
            System.out.println("cmd ::= SKIP");
            consume(Terminals.SKIP);
            ret = new ConcTree.CmdSkip();
            break;
        case IF:
            //System.out.println("cmd ::= IF expression THEN blockCmd ELSE blockCmd ENDIF");
            consume(Terminals.IF);
            ConcTree.Expression ifExpression = expression();
            consume(Terminals.THEN);
            ConcTree.BlockCmd ifCmd = blockCmd();
            consume(Terminals.ELSE);
            ConcTree.BlockCmd elseCmd = blockCmd();
            consume(Terminals.ENDIF);
            ret = new ConcTree.CmdIf(ifExpression, ifCmd, elseCmd);
            break;
        case CALL:
            //System.out.println("cmd ::= CALL IDENT expressionList optionalGlobalInits");
            consume(Terminals.CALL);
            Ident ident = (Ident) consume(Terminals.IDENT);
            ConcTree.ExpressionList expressionList = expressionList();
            ConcTree.OptionalGlobalInits optionalGlobalInits = optionalGlobalInits();
            ret = new ConcTree.CmdCall(ident, expressionList, optionalGlobalInits);
            break;
        case WHILE:
            //System.out.println("cmd ::= WHILE expression DO blockCmd ENDWHILE");
            consume(Terminals.WHILE);
            ConcTree.Expression whileExpr = expression();
            consume(Terminals.DO);
            ConcTree.BlockCmd whileCmd = blockCmd();
            consume(Terminals.ENDWHILE);
            ret = new ConcTree.CmdWhile(whileExpr, whileCmd);
            break;
        case DEBUGIN:
            //System.out.println("cmd ::= DEBUGIN expression");
            consume(Terminals.DEBUGIN);
            ConcTree.Expression debugInExpression = expression();
            ret = new ConcTree.CmdDebugIn(debugInExpression);
            break;
        case DEBUGOUT:
            //System.out.println("cmd ::= DEBUGOUT expression");
            consume(Terminals.DEBUGOUT);
            ConcTree.Expression debugOutExpression = expression();
            ret = new ConcTree.CmdDebugOut(debugOutExpression);
            break;
        case IDENT:
            //System.out.println("cmd ::= expression BECOMES expression");
            ConcTree.Expression expression = expression();
            consume(Terminals.BECOMES);
            ConcTree.Expression nextExpression = expression();
            ret = new ConcTree.CmdExpression(expression, nextExpression);
            break;
        default:
            throw new GrammarError("terminal expected: SKIP | IF | CALL | WHILE | DEBUGIN | DEBUGOUT | IDENT, terminal found: " + terminal + " in a command");
        }
        return ret;
    }

    private ConcTree.BlockCmd blockCmd() throws GrammarError {
        //System.out.println("blockCmd ::= cmd repeatingOptionalCmds");
        ConcTree.Cmd cmd = cmd();
        ConcTree.RepeatingOptionalCmds repeatingOptionalCmds = repeatingOptionalCmds();
        return new ConcTree.BlockCmd(cmd, repeatingOptionalCmds);
    }

    private ConcTree.RepeatingOptionalCmds repeatingOptionalCmds() throws GrammarError {
        if (terminal == Terminals.SEMICOLON) {
            //System.out.println("repCmd ::= SEMICOLON cmd repeatingOptionalCmds");
            consume(Terminals.SEMICOLON);
            ConcTree.Cmd cmd = cmd();
            ConcTree.RepeatingOptionalCmds repeatingOptionalCmds = repeatingOptionalCmds();
            return new ConcTree.RepeatingOptionalCmds(cmd, repeatingOptionalCmds);
        } else {
            //System.out.println("repeatingCmds ::= epsilon");
            return new ConcTree.RepeatingCmdsEpsilon();
        }
    }

    private ConcTree.OptionalGlobalInits optionalGlobalInits() throws GrammarError {
        if (terminal == Terminals.INIT) {
            //System.out.println("optionalGlobalInits ::= INIT idents");
            consume(Terminals.INIT);
            ConcTree.Idents idents = idents();
            return new ConcTree.OptionalGlobalInits(idents);
        } else {
            //System.out.println("optionalGlobalInits ::= epsilon");
            return new ConcTree.OptionalGlobalInitsEpsilon();
        }
    }

    private ConcTree.Idents idents() throws GrammarError {
        //System.out.println("idents ::= IDENT repeatingOptionalIdents");
        Ident ident = (Ident) consume(Terminals.IDENT);
        ConcTree.RepeatingOptionalIdents repeatingOptionalIdents = repeatingOptionalIdents();
        return new ConcTree.Idents(ident, repeatingOptionalIdents);
    }

    private ConcTree.RepeatingOptionalIdents repeatingOptionalIdents() throws GrammarError {
        if (terminal != Terminals.COMMA) {
            //System.out.println("repeatingOptionalIdents ::= epsilon");
            return new ConcTree.RepeatingOptionalIdentsEpsilon();
        } else {
            //System.out.println("repeatingOptionalIdents ::= COMMA IDENT idents");
            consume(Terminals.COMMA);
            Ident ident = (Ident) consume(Terminals.IDENT);
            ConcTree.Idents idents = idents();
            return new ConcTree.RepeatingOptionalIdents(ident, idents);
        }
    }

    private ConcTree.Expression expression() throws GrammarError {
        //System.out.println("expression ::= term1 repBOOLOPRterm1");
        ConcTree.Term1 term1 = term1();
        ConcTree.RepBOOLOPRterm1 repBOOLOPRterm1 = repBOOLOPRterm1();
        return new ConcTree.Expression(term1, repBOOLOPRterm1);
    }

    private ConcTree.RepBOOLOPRterm1 repBOOLOPRterm1() throws GrammarError {
        if (terminal == Terminals.BOOLOPR) {
            //System.out.println("repTerm1 ::= BOOLOPR term1 repBOOLOPRterm1");
            Operator.BoolOpr boolOpr = (Operator.BoolOpr) consume(Terminals.BOOLOPR);
            ConcTree.Term1 term1 = term1();
            ConcTree.RepBOOLOPRterm1 repBOOLOPRterm1 = repBOOLOPRterm1();
            return new ConcTree.RepBOOLOPRterm1(boolOpr, term1, repBOOLOPRterm1);
        } else {
            //System.out.println("repBOOLOPRterm1 ::= epsilon");
            return new ConcTree.RepBOOLOPRterm1Epsilon();
        }
    }

    private ConcTree.Term1 term1() throws GrammarError {
        //System.out.println("term1 ::= term2 repRELOPRterm2");
        ConcTree.Term2 term2 = term2();
        ConcTree.RepRELOPRterm2 repRELOPRterm2 = repRELOPRterm2();
        return new ConcTree.Term1(term2, repRELOPRterm2);
    }

    private ConcTree.RepRELOPRterm2 repRELOPRterm2() throws GrammarError {
        if (terminal == Terminals.RELOPR) {
            //System.out.println("repRELOPRterm2 ::= RELOPR term2 repRELOPRterm2");
            Operator.RelOpr relOpr = (Operator.RelOpr) consume(Terminals.RELOPR);
            ConcTree.Term2 term2 = term2();
            ConcTree.RepRELOPRterm2 repRELOPRterm2 = new ConcTree.RepRELOPRterm2Epsilon();
            return new ConcTree.RepRELOPRterm2(relOpr, term2, repRELOPRterm2);
        } else {
            //System.out.println("repRELOPRterm2 ::= epsilon");
            return new ConcTree.RepRELOPRterm2Epsilon();
        }
    }

    private ConcTree.Term2 term2() throws GrammarError {
        //System.out.println("term2 ::= term3 repADDOPRterm3");
        ConcTree.Term3 term3 = term3();
        ConcTree.RepADDOPRterm3 repADDOPRterm3 = repADDOPRterm3();
        return new ConcTree.Term2(term3, repADDOPRterm3);
    }

    private ConcTree.RepADDOPRterm3 repADDOPRterm3() throws GrammarError {
        if (terminal == Terminals.ADDOPR) {
            //System.out.println("repADDOPRterm3 ::= ADDOPR term3 repADDOPRterm3");
            Operator.AddOpr addOpr = (Operator.AddOpr) consume(Terminals.ADDOPR);
            ConcTree.Term3 term3 = term3();
            ConcTree.RepADDOPRterm3 repADDOPRterm3 = repADDOPRterm3();
            return new ConcTree.RepADDOPRterm3(addOpr, term3, repADDOPRterm3);
        } else {
            //System.out.println("repADDOPRterm3 ::= epsilon");
            return new ConcTree.RepADDOPRterm3Epsilon();
        }
    }

    private ConcTree.Term3 term3() throws GrammarError {
        //System.out.println("term3 ::= term4 repMULTOPRterm4");
        ConcTree.Term4 term4 = term4();
        ConcTree.RepMULTOPRterm4 repMULTOPRterm4 = repMULTOPRterm4();
        return new ConcTree.Term3(term4, repMULTOPRterm4);
    }

    private ConcTree.RepMULTOPRterm4 repMULTOPRterm4() throws GrammarError {
        if (terminal == Terminals.MULTOPR) {
            //System.out.println("repMULTOPRterm4 ::= MULTOPR term4 repMULTOPRterm4");
            Operator.MultOpr multOpr = (Operator.MultOpr) consume(Terminals.MULTOPR);
            ConcTree.Term4 term4 = term4();
            ConcTree.RepMULTOPRterm4 repMULTOPRterm4 = repMULTOPRterm4();
            return new ConcTree.RepMULTOPRterm4(multOpr, term4, repMULTOPRterm4);
        } else {
            //System.out.println("repMULTOPRterm4 ::= epsilon");
            return new ConcTree.RepMULTOPRterm4Epsilon();
        }
    }

    private ConcTree.Term4 term4() throws GrammarError {
        //System.out.println("term4 ::= factor repDOTOPRfactor");
        ConcTree.Factor factor = factor();
        ConcTree.RepDOTOPRfactor repDOTOPRfactor = repDOTOPRfactor();
        return new ConcTree.Term4(factor, repDOTOPRfactor);

    }

    private ConcTree.RepDOTOPRfactor repDOTOPRfactor() throws GrammarError {
        if (terminal == Terminals.DOTOPR) {
            //System.out.println("repDOTOPRfactor ::= DOTOPR factor repDOTOPRfactor");
            Operator.DotOpr dotOpr = (Operator.DotOpr) consume(Terminals.DOTOPR);
            ConcTree.Factor factor = factor();
            ConcTree.RepDOTOPRfactor repDOTOPRfactor = repDOTOPRfactor();
            return new ConcTree.RepDOTOPRfactor(dotOpr, factor, repDOTOPRfactor);
        } else {
            //System.out.println("repDOTOPRfactor ::= epsilon");
            return new ConcTree.RepDOTOPRfactorEpsilon();
        }
    }

    private ConcTree.Factor factor() throws GrammarError {
        ConcTree.Factor ret = null;
        switch (terminal) {
        case LITERAL:
            //System.out.println("factor ::= LITERAL");
            ret = new ConcTree.FactorLiteral((Literal) consume(Terminals.LITERAL));
            break;
        case IDENT:
            //System.out.println("factor ::= IDENT optionalIdent");
            Ident ident = (Ident) consume(Terminals.IDENT);
            ConcTree.OptionalIdent optionalIdent = optionalIdent();
            ret = new ConcTree.FactorIdent(ident, optionalIdent);
            break;
        case LPAREN:
            //System.out.println("factor ::= LPAREN expression RPAREN");
            consume(Terminals.LPAREN);
            ConcTree.Expression expression = expression();
            consume(Terminals.RPAREN);
            ret = new ConcTree.FactorExpression(expression);
            break;
        default:
            //System.out.println("factor ::= monadicOperator factor");
            ConcTree.MonadicOperator monadicOperator = monadicOperator();
            ConcTree.Factor factor = factor();
            ret = new ConcTree.FactorMonadicOperator(monadicOperator, factor);
        }
        return ret;
    }

    private ConcTree.OptionalIdent optionalIdent() throws GrammarError {
        ConcTree.OptionalIdent ret = null;
        switch (terminal) {
        case INIT:
            //System.out.println("optionalIdent ::= INIT");
            consume(Terminals.INIT);
            ret = new ConcTree.OptionalIdentInit();
            break;
        case LPAREN:
            //System.out.println("optionalIdent ::= expressionList");
            ConcTree.ExpressionList expressionList = expressionList();
            ret = new ConcTree.OptionalIdentExpressionList(expressionList);
            break;
        default:
            //System.out.println("optionalIdent::= epsilon");
            ret = new ConcTree.OptionalIdentEpsilon();
        }
        return ret;
    }

    private ConcTree.ExpressionList expressionList() throws GrammarError {
        //System.out.println("expressionList ::= LPAREN optionalExpressions RPAREN");
        consume(Terminals.LPAREN);
        ConcTree.OptionalExpressions optionalExpressions = optionalExpressions();
        consume(Terminals.RPAREN);
        return new ConcTree.ExpressionList(optionalExpressions);
    }

    private ConcTree.OptionalExpressions optionalExpressions() throws GrammarError {
        if (terminal == Terminals.RPAREN) {
            //System.out.println("optionalExpressions ::= epsilon");
            return new ConcTree.OptionalExpressionsEpsilon();
        } else {
            //System.out.println("optionalExpressions ::= expression repeatingOptionalExpression");
            ConcTree.Expression expression = expression();
            ConcTree.RepeatingOptionalExpressions repeatingOptionalExpressions = repeatingOptionalExpressions();
            return new ConcTree.OptionalExpressions(expression, repeatingOptionalExpressions);
        }
    }

    private ConcTree.RepeatingOptionalExpressions repeatingOptionalExpressions() throws GrammarError {
        if (terminal != Terminals.COMMA) {
            //System.out.println("repeatingOptionalExpressions ::= epsilon");
            return new ConcTree.RepeatingOptionalExpressionsEpsilon();
        } else {
            //System.out.println("repeatingOptionalExpressions ::= COMMA expression optionalExpressions");
            consume(Terminals.COMMA);
            ConcTree.Expression expression = expression();
            ConcTree.RepeatingOptionalExpressions repeatingOptionalExpressions = repeatingOptionalExpressions();
            return new ConcTree.RepeatingOptionalExpressions(expression, repeatingOptionalExpressions);
        }
    }

    private ConcTree.MonadicOperator monadicOperator() throws GrammarError {
        ConcTree.MonadicOperator ret = null;
        switch (terminal) {
        case NOTOPR:
            //System.out.println("monadicOpr ::= NOT");
            ret = new ConcTree.MonadicOperator((Operator) consume(Terminals.NOTOPR));
            break;
        case ADDOPR:
            //System.out.println("monadicOpr ::= ADDOPR");
            ret = new ConcTree.MonadicOperator((Operator) consume(Terminals.ADDOPR));
            break;
        default:
            throw new GrammarError("terminal expected: NOT | ADDOPR, terminal found: " + terminal);
        }
        return ret;
    }

    private ConcTree.OptionalMechMode optionalMechMode() throws GrammarError {
        ConcTree.OptionalMechMode ret = null;
        if (terminal == Terminals.MECHMODE) {
            //System.out.println("optionalMechMode ::= MECHMODE");
            ret = new ConcTree.OptionalMechMode((Mode.MechMode) consume(Terminals.MECHMODE));
        } else {
            //System.out.println("optionalMechMode ::= epsilon");
            ret = new ConcTree.OptionalMechModeEpsilon();
        }
        return ret;
    }

    private ConcTree.OptionalChangeMode optionalChangeMode() throws GrammarError {
        ConcTree.OptionalChangeMode ret = null;
        if (terminal == Terminals.CHANGEMODE) {
            //System.out.println("optionalChangeMode ::= CHANGEMODE");
            ret = new ConcTree.OptionalChangeMode((Mode.ChangeMode) consume(Terminals.CHANGEMODE));
        } else {
            //System.out.println("optionalChangeMode ::= epsilon");
            ret = new ConcTree.OptionalChangeModeEpsilon();
        }
        return ret;
    }

    private ConcTree.OptionalGlobalImports optionalGlobalImports() throws GrammarError {
        if (terminal == Terminals.GLOBAL) {
            //System.out.println("optionalGlobalImports ::= GLOBAL globalImport repeatingGlobalImports");
            consume(Terminals.GLOBAL);
            GlobalImport globalImport = globalImport();
            RepeatingOptionalGlobalImports repeatingOptionalGlobalImports = repeatingOptionalGlobalImports();
            return new ConcTree.OptionalGlobalImports(globalImport, repeatingOptionalGlobalImports);
        } else {
            //System.out.println("optionalGlobalImports :== espilon");
            return new ConcTree.OptionalGlobalImportsEpsilon();
        }
    }

    private ConcTree.GlobalImport globalImport() throws GrammarError {
        //System.out.println("globalImport ::= optionalFLOWMODE optionalCHANGEMODE IDENT");
        OptionalChangeMode optionalChangeMode = optionalChangeMode();
        Ident ident = (Ident) consume(Terminals.IDENT);
        return new ConcTree.GlobalImport(optionalChangeMode, ident);
    }

    private ConcTree.RepeatingOptionalGlobalImports repeatingOptionalGlobalImports() throws GrammarError {
        if (terminal == Terminals.COMMA) {
            //System.out.println("repeatingOptionalGlobalImports ::= COMMA globalImport repeatingOptionalGlobalImports");
            consume(Terminals.COMMA);
            GlobalImport globalImport = globalImport();
            RepeatingOptionalGlobalImports repeatingOptionalGlobalImports = repeatingOptionalGlobalImports();
            return new RepeatingOptionalGlobalImports(globalImport, repeatingOptionalGlobalImports);
        } else {
            //System.out.println("repeatingOptionalGlobalImports ::= epsilon");
            return new RepeatingOptionalGlobalImportsEpsilon();
        }
    }

}
