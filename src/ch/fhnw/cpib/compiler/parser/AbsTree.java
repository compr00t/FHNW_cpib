package ch.fhnw.cpib.compiler.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.fhnw.cpib.compiler.parser.ConcTree.RangeVal;
import ch.fhnw.cpib.compiler.scanner.enums.ModeAttributes;
import ch.fhnw.cpib.compiler.scanner.enums.OperatorAttribute;
import ch.fhnw.cpib.compiler.scanner.enums.Terminals;
import ch.fhnw.cpib.compiler.scanner.enums.TypeAttribute;
import ch.fhnw.cpib.compiler.scanner.token.*;
import ch.fhnw.cpib.compiler.scanner.token.Mode.*;
import ch.fhnw.lederer.virtualmachineFS2015.*;
import ch.fhnw.cpib.compiler.context.GlobImp;
import ch.fhnw.cpib.compiler.context.Procedure;
import ch.fhnw.cpib.compiler.context.Routine;
import ch.fhnw.cpib.compiler.context.Routine.RoutineTypes;
import ch.fhnw.cpib.compiler.context.Scope;
import ch.fhnw.cpib.compiler.context.Store;
import ch.fhnw.cpib.compiler.exception.ContextError;
import ch.fhnw.cpib.compiler.Compiler;

public interface AbsTree {

	public class Program {
		private final Ident ident;
		private final Declaration declaration;
		private final Cmd cmd;
		private final ProgramParameter programParameter;

		public Program(Ident ident, ProgramParameter programParameter, Declaration declaration, Cmd cmd) {
			this.ident = ident;
			this.programParameter = programParameter;
			this.declaration = declaration;
			this.cmd = cmd;
		}

		public String toString(String indent) {
			return indent + "<Program>\n" + ident.toString(indent + '\t')
					+ (programParameter != null ? programParameter.toString(indent + '\t')
							: "\t<noProgramParameter/>\n")
					+ (declaration != null ? declaration.toString(indent + '\t') : "\t<noDeclarations/>\n")
					+ cmd.toString(indent + '\t') + indent + "</Program>\n";
		}

		public String toString() {
			return toString("");
		}

		public Declaration getDeclarations() {
			return declaration;
		}

		public Cmd getCommands() {
			return cmd;
		}

		public Ident getIdent() {
			return ident;
		}

		public ProgramParameter getProgramParameter() {
			return programParameter;
		}

		public int getLine() {
			return 0;
		}

		public void check() throws ContextError {
			if (programParameter != null)
				programParameter.checkDeclaration();
			if (declaration != null)
				declaration.checkDeclaration();
			if (declaration != null)
				declaration.check(-1);
			Compiler.setScope(new Scope(Compiler.getGlobalStoreTable().clone()));
			cmd.check(false);
		}

		// public int code(final int loc) {
		// int loc1 = cmd.code(loc);
		// Compiler.getVM().Stop(loc1);
		// if (declaration != null)
		// loc1 = declaration.code(loc1 + 1);
		// for (Routine routine :
		// Compiler.getRoutineTable().getTable().values()) {
		// routine.codeCalls();
		// }
		// return loc1;
		// }

	}

	public class ProgramParameter {
		private final ChangeMode changeMode;
		private final TypedIdent typedIdent;
		private final ProgramParameter nextProgramParameter;

		public ProgramParameter(ChangeMode changeMode, TypedIdent typedIdent, ProgramParameter next) {
			this.changeMode = changeMode;
			this.typedIdent = typedIdent;
			this.nextProgramParameter = next;
		}

		public String toString(String indent) {
			return indent + "<ProgramParameter>\n" + changeMode.toString(indent + '\t')
					+ typedIdent.toString(indent + '\t') + (nextProgramParameter != null
							? nextProgramParameter.toString(indent + '\t') : "\t<noNextProgramParameter/>\n")
					+ indent + "</ProgramParameter>\n";
		}

		public ChangeMode getChangeMode() {
			return changeMode;
		}

		public TypedIdent getTypedIdent() {
			return typedIdent;
		}

		public ProgramParameter getNextProgramParameter() {
			return nextProgramParameter;
		}

		public Store getStore() {
			return new Store(typedIdent.getIdent().getValue(), typedIdent,
					changeMode.getValue() == ModeAttributes.CONST);
		}

		public void checkDeclaration() throws ContextError {
			Store store = getStore();
			if (!Compiler.getGlobalStoreTable().addStore(store.getIdent(), store)) {
				throw new ContextError("Store already declared: " + typedIdent.getIdent().getValue());
			}
			// if (((TypedIdentType) typedIdent).getType().getValue() ==
			// TypeAttribute.BOOL) {
			// store.setAddress(Compiler.getVM().BoolInitHeapCell());
			// store.setRelative(false);
			// } else {
			// store.setAddress(Compiler.getVM().IntInitHeapCell());
			// store.setRelative(false);
			// }
			if (nextProgramParameter != null)
				nextProgramParameter.checkDeclaration();
		}

		public Store check() throws ContextError {
			Store store = getStore();
			TypedIdent t = store.getType();
			Ident ident = typedIdent.getIdent();
			if (t instanceof TypedIdentIdent) {
				if (!Compiler.getScope().getStoreTable().addStore(ident.getValue(), store)) {
					throw new ContextError("Ident already declared: " + ident.getValue());
				}
			} else {
				if (!Compiler.getScope().getStoreTable().addStore(ident.getValue(), store)) {
					throw new ContextError("Ident already declared: " + ident.getValue());
				}
			}

			return store;
		}
	}

	public abstract class Declaration {
		protected final Declaration nextDecl;

		public Declaration(Declaration next) {
			this.nextDecl = next;
		}

		public String toString(String indent) {
			return indent + "<Decl>\n"
					+ (nextDecl != null ? nextDecl.toString(indent + '\t') : indent + "\t<noNextElement/>\n") + indent
					+ "</Decl>\n";
		}

		public Declaration getNextDecl() {
			return nextDecl;
		}

		public abstract void checkDeclaration() throws ContextError;

		public abstract int check(int locals) throws ContextError;

		// public abstract int code(int loc);
	}

	public class DeclarationFunction extends Declaration {
		private final Ident ident;
		private final Parameter param;
		private final DeclarationStore returnDecl;
		private final GlobalImport globImp;
		private final Declaration dcl;
		private final Declaration nextDecl;
		private final Cmd cmd;

		public DeclarationFunction(Ident ident, Parameter param, DeclarationStore returnDecl, GlobalImport globImp,
				Declaration dcl, Cmd cmd, Declaration nextDecl) {
			super(nextDecl);
			this.ident = ident;
			this.param = param;
			this.returnDecl = returnDecl;
			this.globImp = globImp;
			this.dcl = dcl;
			this.nextDecl = nextDecl;
			this.cmd = cmd;
		}

		public String toString(String indent) {
			return indent + "<DeclFun>\n" + ident.toString(indent + '\t') + param.toString(indent + '\t')
					+ returnDecl.toString(indent + '\t') + globImp.toString(indent + '\t') + dcl.toString(indent + '\t')
					+ cmd.toString(indent + '\t') + super.toString(indent + '\t') + indent + "</DeclFun>\n";
		}

		public Cmd getCmd() {
			return cmd;
		}

		public Ident getIdent() {
			return ident;
		}

		public Declaration getDecl() {
			return dcl;
		}

		public Parameter getParam() {
			return param;
		}

		public GlobalImport getGlobImp() {
			return globImp;
		}

		public DeclarationStore getReturnDecl() {
			return returnDecl;
		}

		public void checkDeclaration() throws ContextError {
			if (nextDecl != null)
				nextDecl.checkDeclaration();
		}

		public int check(int locals) throws ContextError {
			return 0;
		}

		public int code(int loc) {
			return 0;
		}
	}

	public class DeclarationProcedure extends Declaration {
		private final Ident ident;
		private final Parameter param;
		private final GlobalImport globalImport;
		private final Declaration decl;
		private final Declaration nextDecl;
		private final Cmd cmd;
		private int countDecls = 0;

		public DeclarationProcedure(Ident ident, Parameter parameter, GlobalImport globalImport, Declaration decl,
				Cmd cmd, Declaration nextDecl) {
			super(nextDecl);
			this.ident = ident;
			this.param = parameter;
			this.globalImport = globalImport;
			this.decl = decl;
			this.nextDecl = nextDecl;
			this.cmd = cmd;

			Declaration tmpDecl = decl;
			while (tmpDecl != null) {
				++countDecls;
				tmpDecl = tmpDecl.getNextDecl();
			}
		}

		public String toString(final String indent) {
			return indent + "<DeclarationProcedure>\n" + ident.toString(indent + '\t') + param.toString(indent + '\t')
					+ (globalImport != null ? globalImport.toString(indent + '\t') : "")
					+ (decl != null ? decl.toString(indent + '\t') : "") + cmd.toString(indent + '\t')
					+ super.toString(indent + '\t') + indent + "</DeclarationProcedure>\n";
		}

		public int getCount() {
			return countDecls;
		}

		public Ident getIdent() {
			return ident;
		}

		public Parameter getParam() {
			return param;
		}

		public GlobalImport getGlobImp() {
			return globalImport;
		}

		public Declaration getDecl() {
			return decl;
		}

		public Cmd getCmd() {
			return cmd;
		}

		public void checkDeclaration() throws ContextError {
			Procedure procedure = new Procedure(ident.getValue());
			Compiler.setScope(procedure.getScope());

			if (!Compiler.getRoutineTable().addRoutine(procedure)) {
				throw new ContextError("Routine already declared: " + ident.getValue());
			}

			param.check(procedure);
			Compiler.setScope(null);
			if (nextDecl != null)
				nextDecl.checkDeclaration();
		}

		@Override
		public int check(final int locals) throws ContextError {
			if (locals >= 0) {
				throw new ContextError("Function declarations are only allowed globally!");
			}
			Routine routine = Compiler.getRoutineTable().getRoutine(ident.getValue());
			Compiler.setScope(routine.getScope());
			if (globalImport != null)
				globalImport.check(routine);
			int localsCount = param.calculateAddress(routine.getParamList().size(), 0);

			if (decl != null)
				decl.check(localsCount);

			cmd.check(false);
			Compiler.setScope(null);
			return -1;
		}

		// public int code(final int loc) {
		// int loc1 = loc;
		// Routine routine =
		// Compiler.getRoutineTable().getRoutine(ident.getValue());
		// Compiler.setScope(routine.getScope());
		// routine.setAddress(loc1);
		// Compiler.getVM().Enter(loc1++, routine.getInOutCopyCount() +
		// getCount(), 0);
		// loc1 = param.codeIn(loc1, routine.getParamList().size(), 0);
		// loc1 = cmd.code(loc1);
		// loc1 = param.codeOut(loc1, routine.getParamList().size(), 0);
		// Compiler.getVM().Return(loc1++, 0);
		// Compiler.setScope(null);
		// return loc1;
		// // return (nextDecl!=null?nextDecl.code(loc1):loc1);
		// }
	}

	public class DeclarationStore extends Declaration {
		private final ChangeMode changeMode;
		private final TypedIdent typedIdent;
		private final Declaration nextDeclaration;

		public DeclarationStore(ChangeMode changeMode, TypedIdent typedIdent, Declaration nextDeclaration) {
			super(nextDeclaration);
			this.changeMode = changeMode;
			this.typedIdent = typedIdent;
			this.nextDeclaration = nextDeclaration;
		}

		public String toString(final String indent) {
			return indent + "<DeclStore>\n" + changeMode.toString(indent + '\t') + typedIdent.toString(indent + '\t')
					+ indent + "</DeclStore>\n" + super.toString(indent + '\t');
		}

		public ChangeMode getChangeMode() {
			return changeMode;
		}

		public TypedIdent getTypedIdent() {
			return typedIdent;
		}

		public Store getStore() {
			return new Store(typedIdent.getIdent().getValue(), typedIdent, changeMode.getValue() == ModeAttributes.CONST);
		}

		@Override
		public int check(final int locals) throws ContextError {
			if (locals < 0) {
				return -1;
			} else {
				Store store = check();
				store.setAddress(2 + locals + 1);
				store.setRelative(true);
				store.setReference(false);
				return locals + 1;
			}
		}

		public void checkDeclaration() throws ContextError {
			if (this.getTypedIdent() instanceof TypedIdentIdent) {
				if (Compiler.getGlobalStoreTable().containsIdent(typedIdent.getIdent().getValue())) {
					throw new ContextError("Store already declared: " + typedIdent.getIdent().getValue());
				}

				Compiler.getGlobalStoreTable().addStore(typedIdent.getIdent().getValue(), getStore());

			} else {
				Store store = getStore();
				if (!Compiler.getGlobalStoreTable().addStore(store.getIdent(), store)) {
					throw new ContextError("Store already declared: " + typedIdent.getIdent().getValue());
				}
			}
			if (nextDeclaration != null)
				nextDeclaration.checkDeclaration();
		}

		// @Override
		// public void checkDeclaration() throws ContextError {
		// if (this.getTypedIdent() instanceof TypedIdentIdent) {
		// Store store = getStore();
		// if (!Compiler.getGlobalStoreTable().addStore(store.getIdent(),
		// store)) {
		// throw new ContextError("Store already declared: " +
		// typedIdent.getIdent().getValue());
		// }
		// }
		// if (nextDeclaration != null)
		// nextDeclaration.checkDeclaration();
		// }

		public Store check() throws ContextError {
			Store store = getStore();
			TypedIdent t = store.getType();
			Ident ident = typedIdent.getIdent();
			if (t instanceof TypedIdentIdent) {
				if (!Compiler.getScope().getStoreTable().addStore(ident.getValue(), store)) {
					throw new ContextError("Ident already declared: " + ident.getValue());
				}
			} else {
				if (!Compiler.getScope().getStoreTable().addStore(ident.getValue(), store)) {
					throw new ContextError("Ident already declared: " + ident.getValue());
				}
			}

			return store;
		}

		public int code(final int loc) {
			return loc;
		}
	}

	public class Parameter {
		private final MechMode mechMode;
		private final DeclarationStore declarationStorage;
		private final Parameter nextParam;
		private Store store;

		public Parameter(MechMode mechMode, DeclarationStore declarationStorage, Parameter nextParam) {
			this.mechMode = mechMode;
			this.declarationStorage = declarationStorage;
			this.nextParam = nextParam;
		}

		public String toString(String indent) {
			return indent + "<Parameter>\n" + mechMode.toString(indent + '\t')
					+ declarationStorage.changeMode.toString(indent + '\t')
					+ declarationStorage.typedIdent.toString(indent + '\t')
					+ (nextParam != null ? nextParam.toString(indent + '\t') : "") + indent + "</Parameter>\n";
		}

		public MechMode getMechMode() {
			return mechMode;
		}

		public DeclarationStore getDeclarationStore() {
			return declarationStorage;
		}

		public ChangeMode getChangeMode() {
			return declarationStorage.getChangeMode();
		}

		public Parameter getNextParam() {
			return nextParam;
		}

		public TypedIdent getTypedIdent() {
			return declarationStorage.getTypedIdent();
		}

		public void check(final Routine routine) throws ContextError {
			store = declarationStorage.check();

			Mode changeMode = new Mode(Terminals.CHANGEMODE, ModeAttributes.CONST);

			if (!store.isConst()) {
				changeMode = new Mode(Terminals.CHANGEMODE, ModeAttributes.VAR);
			}

			routine.addParam(new ch.fhnw.cpib.compiler.context.Parameter(getMechMode(), changeMode, store.getType()));
			if (nextParam != null)
				nextParam.check(routine);
		}

		public void checkInit() throws ContextError {
			if (nextParam != null)
				nextParam.checkInit();
		}

		public int calculateAddress(final int count, final int locals) {
			int locals1 = locals;
			if (mechMode.getValue() == ModeAttributes.REF) {
				store.setAddress(-count);
				store.setRelative(true);
				if (mechMode.getValue() == ModeAttributes.REF) {
					store.setReference(true);
				} else {
					store.setReference(false);
				}
			} else {
				store.setAddress(2 + ++locals1);
				store.setRelative(true);
				store.setReference(false);
			}

			return (nextParam != null ? nextParam.calculateAddress(count - 1, locals1) : locals1);
		}

		// public int codeIn(final int loc, final int count, final int locals) {
		// int locals1 = locals;
		// int loc1 = loc;
		// if (mechMode.getValue() == ModeAttributes.COPY) {
		// Compiler.getVM().CopyIn(loc1++, -count, 3 + locals1);
		// locals1++;
		// }
		// return (nextParam != null ? nextParam.codeIn(loc1, count - 1,
		// locals1) : loc);
		// }

		// public int codeOut(final int loc, final int count, final int locals)
		// {
		// int locals1 = locals;
		// int loc1 = loc;
		// if (mechMode.getValue() == ModeAttributes.COPY) {
		// Compiler.getVM().CopyOut(loc1++, 2 + ++locals1, -count);
		// }
		// return (nextParam != null ? nextParam.codeOut(loc1, count - 1,
		// locals1) : loc);
		// }
	}

	public class ParameterList {
		private final Parameter parameter;
		private final ParameterList parameterList;

		public ParameterList(Parameter parameter, ParameterList parameterList) {
			this.parameter = parameter;
			this.parameterList = parameterList;
		}

		public String toString(String indent) {
			return indent + "<ParameterList>\n" + parameter.toString(indent + '\t')
					+ (parameterList != null ? parameterList.toString(indent + '\t') : "") + indent
					+ "</ParameterList>\n";
		}

		public Parameter getParameter() {
			return parameter;
		}

		public ParameterList getParameterList() {
			return parameterList;
		}
	}

	public abstract class Cmd {
		public abstract Cmd getNextCmd();

		public abstract int getLine();

		public abstract String toString(final String ident);

		public abstract void check(boolean canInit) throws ContextError;

		// public abstract int code(int loc);
	}

	public class CmdSkip extends Cmd {

		private final Cmd nextCmd;

		public CmdSkip(Cmd nextCmd) {
			this.nextCmd = nextCmd;
		}

		@Override
		public String toString(final String indent) {
			return indent + "<CmdSkip>\n"
					+ (nextCmd != null ? nextCmd.toString(indent + '\t') : indent + "\t<noNextElement/>\n") + indent
					+ "</CmdSkip>\n";
		}

		@Override
		public Cmd getNextCmd() {
			return nextCmd;
		}

		@Override
		public int getLine() {
			return -1;
		}

		@Override
		public void check(final boolean canInit) throws ContextError {
			if (nextCmd != null)
				nextCmd.check(canInit);
		}

		// @Override
		// public int code(int loc) {
		// return (nextCmd != null ? nextCmd.code(loc) : loc);
		// }
	}

	public class CmdAssi extends Cmd {
		private Expression targetExpression;
		private Expression sourceExpression;
		private final Cmd nextCmd;

		public CmdAssi(Expression targetExpression, Expression sourceExpression, Cmd nextCmd) {
			this.targetExpression = targetExpression;
			this.sourceExpression = sourceExpression;
			this.nextCmd = nextCmd;
		}

		@Override
		public String toString(final String indent) {
			return indent + "<CmdAssi>\n" + targetExpression.toString(indent + '\t')
					+ sourceExpression.toString(indent + '\t')
					+ (nextCmd != null ? nextCmd.toString(indent + '\t') : indent + "\t<noNextElement/>\n") + indent
					+ "</CmdAssi>\n";
		}

		public Expression getTargetExpression() {
			return targetExpression;
		}

		public Expression getSourceExpression() {
			return sourceExpression;
		}

		@Override
		public Cmd getNextCmd() {
			return nextCmd;
		}

		@Override
		public int getLine() {
			return targetExpression.getLine();
		}

		@Override
		public void check(final boolean canInit) throws ContextError {
			
			Type typeL;
			Object tmp = targetExpression.checkL(canInit);
			if (tmp instanceof TypedIdentArr) {
				typeL = new Type(TypeAttribute.INT64);
			} else if (tmp instanceof TypedIdentType) {
				typeL = ((TypedIdentType) tmp).getType();
			} else {
				typeL = (Type) tmp;
			}

			Type typeR;
			tmp = sourceExpression.checkR();
			if (tmp instanceof TypedIdentArr) {
				typeR = new Type(TypeAttribute.INT64);
			} else if (tmp instanceof TypedIdentType) {
				typeR = ((TypedIdentType) tmp).getType();
			} else {
				typeR = (Type) tmp;
			}

			// if (typeR.getValue() != typeL.getValue()) {
			// throw new ContextError("Types in assignemt don't match!");
			// }

			if (nextCmd != null)
				nextCmd.check(canInit);
		}

		// @Override
		// public int code(final int loc) {
		// if (sourceExpression instanceof ExprDyadic
		// && ((ExprDyadic) sourceExpression).getOperator().getValue() ==
		// OperatorAttribute.DOT) {
		// sourceExpression = (ExprStore) ((ExprDyadic)
		// sourceExpression).getExpr1();
		// }
		//
		// if (targetExpression instanceof ExprDyadic
		// && ((ExprDyadic) targetExpression).getOperator().getValue() ==
		// OperatorAttribute.DOT) {
		// targetExpression = (ExprStore) ((ExprDyadic)
		// targetExpression).getExpr1();
		// }
		//
		// int loc1 = sourceExpression.code(loc);
		// if (!(targetExpression instanceof ExprStore)) {
		// loc1 = targetExpression.code(loc1);
		// } else {
		// loc1 = ((ExprStore) targetExpression).codeRef(loc1);
		// Compiler.getVM().Store(loc1++);
		// }
		// return (nextCmd != null ? nextCmd.code(loc1) : loc1);
		// }
	}

	public class CmdCond extends Cmd {
		private final Expression expression;
		private final Cmd ifCmd;
		private final Cmd elseCmd;
		private final Cmd nextCmd;

		public CmdCond(Expression expression, Cmd ifCmd, Cmd elseCmd, Cmd nextCmd) {
			this.expression = expression;
			this.ifCmd = ifCmd;
			this.elseCmd = elseCmd;
			this.nextCmd = nextCmd;
		}

		@Override
		public String toString(String indent) {
			return indent + "<CmdCond>\n" + expression.toString(indent + '\t') + ifCmd.toString(indent + '\t')
					+ elseCmd.toString(indent + '\t')
					+ (nextCmd != null ? nextCmd.toString(indent + '\t') : indent + "\t<noNextElement/>\n") + indent
					+ "</CmdCond>\n";
		}

		public Expression getExpression() {
			return expression;
		}

		public Cmd getIfCmd() {
			return ifCmd;
		}

		public Cmd getElseCmd() {
			return elseCmd;
		}

		@Override
		public Cmd getNextCmd() {
			return nextCmd;
		}

		@Override
		public int getLine() {
			return expression.getLine();
		}

		@Override
		public void check(final boolean canInit) throws ContextError {
			Object tmp = expression.checkR();
			Type type;

			if (tmp instanceof TypedIdentArr) {
				type = new Type(TypeAttribute.INT64);
			} else if (tmp instanceof TypedIdentType) {
				type = ((TypedIdentType) tmp).getType();
			} else {
				type = (Type) tmp;
			}

			if (type.getValue() != TypeAttribute.BOOL) {
				throw new ContextError("IF condition must be a boolean! ");
			}

			Scope parentScope = Compiler.getScope();
			Scope ifScope = new Scope(parentScope.getStoreTable().clone());
			Scope elseScope = new Scope(parentScope.getStoreTable().clone());
			Compiler.setScope(ifScope);
			ifCmd.check(canInit);
			Compiler.setScope(elseScope);
			elseCmd.check(canInit);

			Set<String> keys = parentScope.getStoreTable().getTable().keySet();
			for (String key : keys) {
				Store store = (Store) parentScope.getStoreTable().getStore(key);
				if (!store.isInitialized()) {
					Store storeIf = (Store) ifScope.getStoreTable().getStore(store.getIdent());
					Store storeElse = (Store) elseScope.getStoreTable().getStore(store.getIdent());
					if (storeIf.isInitialized() != storeElse.isInitialized()) {
						throw new ContextError("Initialization must happen in both branches of an IF command! Ident: "
								+ store.getIdent());
					}

					if (storeIf.isInitialized()) {
						store.initialize();
					}
				}
			}

			Compiler.setScope(parentScope);
			if (nextCmd != null)
				nextCmd.check(canInit);
		}

		// @Override
		// public int code(final int loc) {
		// int loc1 = expression.code(loc);
		// int loc2 = ifCmd.code(loc1 + 1);
		// Compiler.getVM().CondJump(loc1, loc2 + 1);
		// int loc3 = elseCmd.code(loc2 + 1);
		// Compiler.getVM().UncondJump(loc2, loc3);
		// return (nextCmd != null ? nextCmd.code(loc3) : loc3);
		// }
	}

	public class CmdWhile extends Cmd {
		private final Expression expression;
		private final Cmd cmd;
		private final Cmd nextCmd;

		public CmdWhile(Expression expression, Cmd cmd, Cmd nextCmd) {
			this.expression = expression;
			this.cmd = cmd;
			this.nextCmd = nextCmd;
		}

		@Override
		public String toString(final String indent) {
			return indent + "<CmdWhile>\n" + expression.toString(indent + '\t') + cmd.toString(indent + '\t')
					+ (nextCmd != null ? nextCmd.toString(indent + '\t') : indent + "\t<noNextElement/>\n") + indent
					+ "</CmdWhile>\n";
		}

		public Expression getExpression() {
			return expression;
		}

		public Cmd getCmd() {
			return cmd;
		}

		@Override
		public Cmd getNextCmd() {
			return nextCmd;
		}

		@Override
		public int getLine() {
			return expression.getLine();
		}

		@Override
		public void check(final boolean canInit) throws ContextError {
			Object tmp = expression.checkR();
			Type type;

			if (tmp instanceof TypedIdentArr) {
				type = new Type(TypeAttribute.INT64);
			} else if (tmp instanceof TypedIdentType) {
				type = ((TypedIdentType) tmp).getType();
			} else {
				type = (Type) tmp;
			}
			if (type.getValue() != TypeAttribute.BOOL) {
				throw new ContextError("WHILE condition must be a boolean! ");
			}
			cmd.check(true);
			if (nextCmd != null)
				nextCmd.check(canInit);
		}

		// @Override
		// public int code(final int loc) {
		// int loc1 = expression.code(loc);
		// int loc2 = cmd.code(loc1 + 1);
		// Compiler.getVM().CondJump(loc1, loc2 + 1);
		// Compiler.getVM().UncondJump(loc2, loc);
		// return (nextCmd != null ? nextCmd.code(loc2 + 1) : (loc2 + 1));
		// }
	}

	public class CmdProcCall extends Cmd {

		private final RoutineCall routineCall;
		private final GlobalInit globalInit;
		private final Cmd nextCmd;

		public CmdProcCall(RoutineCall routineCall, GlobalInit globalInit, Cmd nextCmd) {
			this.routineCall = routineCall;
			this.globalInit = globalInit;
			this.nextCmd = nextCmd;
		}

		@Override
		public String toString(final String indent) {
			return indent + "<ExprCall>\n" + routineCall.toString(indent + '\t')
					+ (globalInit != null ? globalInit.toString(indent + '\t') : "\t<noGlobalInit/>\n")
					+ (nextCmd != null ? nextCmd.toString(indent + '\t') : indent + "\t<noNextElement/>\n") + indent
					+ "</ExprCall>\n";
		}

		public RoutineCall getRoutineCall() {
			return routineCall;
		}

		public GlobalInit getGlobalInit() {
			return globalInit;
		}

		@Override
		public Cmd getNextCmd() {
			return nextCmd;
		}

		public void check(boolean canInit) throws ContextError {
			Ident ident = routineCall.getIdent();
			RoutineTypes type = Compiler.getRoutineTable().getType(ident.getValue());
			if (type == null) {
				throw new ContextError("Ident " + ident.getValue() + " not declared");
			} else if (type != RoutineTypes.PROCEDURE) {
				throw new ContextError("Function call " + ident.getValue() + " found in left part of an assignement");
			}

			Routine routine = Compiler.getRoutineTable().getRoutine(ident.getValue());

			List<ch.fhnw.cpib.compiler.context.Parameter> paramList = new ArrayList<ch.fhnw.cpib.compiler.context.Parameter>(
					routine.getParamList());
			Set<String> aliasList = new HashSet<String>();
			routineCall.getExprList().check(paramList, aliasList, canInit);

			Set<String> globInits;
			if (globalInit != null)
				globInits = globalInit.check(new HashSet<String>());
			else
				globInits = null;

			for (GlobImp globImp : routine.getGlobImpList()) {
				if (globInits.contains(globImp.getIdent())) {
					((Store) Compiler.getScope().getStoreTable().getStore(globImp.getIdent())).initialize();
					globInits.remove(globImp.getIdent());
				}

				if (aliasList.contains(globImp.getIdent())) {
					throw new ContextError(
							"Global import is already used as a parameter! Ident: " + globImp.getIdent());
				}
			}

			if (globInits != null && globInits.size() > 0) {
				throw new ContextError("Global init is not importet! Ident: " + globInits.iterator().next());
			}

			if (nextCmd != null)
				nextCmd.check(canInit);
		}

		@Override
		public int getLine() {
			return 0;
		}

		// @Override
		// public int code(final int loc) {
		// int loc1 = loc;
		// loc1 = routineCall.getExprList().code(loc1);
		// Compiler.getRoutineTable().getRoutine(routineCall.getIdent().getValue()).addCall(loc1++);
		// return (nextCmd != null ? nextCmd.code(loc1) : loc1);
		// }
	}

	public class CmdInput extends Cmd {
		private Expression expr;
		private final Cmd nextCmd;
		private Type type;

		public CmdInput(Expression expr, Cmd nextCmd) {
			this.expr = expr;
			this.nextCmd = nextCmd;
		}

		@Override
		public String toString(String indent) {
			return indent + "<CmdInput>\n" + expr.toString(indent + '\t')
					+ (nextCmd != null ? nextCmd.toString(indent + '\t') : indent + "\t<noNextElement/>\n") + indent
					+ "</CmdInput>\n";
		}

		public Expression getExpr() {
			return expr;
		}

		@Override
		public Cmd getNextCmd() {
			return nextCmd;
		}

		@Override
		public int getLine() {
			return expr.getLine();
		}

		@Override
		public void check(final boolean canInit) throws ContextError {

			Object tmp = expr.checkL(canInit);

			if (tmp instanceof TypedIdentArr) {
				type = new Type(TypeAttribute.INT64);
			} else if (tmp instanceof TypedIdentType) {
				type = ((TypedIdentType) tmp).getType();
			} else {
				type = (Type) tmp;
			}

			if (!(expr instanceof ExprArray) && !(expr instanceof ExprStore) && !((expr instanceof ExprDyadic)
					&& ((ExprDyadic) expr).getOperator().getValue() == OperatorAttribute.DOT
					&& (((ExprDyadic) expr).getExpr2() instanceof ExprStore))) {
				throw new ContextError("Input needs to be assigned to a store!");
			}
			if (nextCmd != null)
				nextCmd.check(canInit);
		}

		// @Override
		// public int code(final int loc) {
		// int loc1;
		// if (expr instanceof ExprDyadic && ((ExprDyadic)
		// expr).getOperator().getValue() ==
		// OperatorAttribute.DOT) {
		// ExprStore expr1 = (ExprStore) ((ExprDyadic) expr).getExpr1();
		// expr = expr1;
		// loc1 = ((ExprStore) expr).codeRef(loc);
		// } else {
		// loc1 = ((ExprStore) expr).codeRef(loc);
		// }
		//
		// if (type.getValue() == TypeAttribute.BOOL) {
		// Compiler.getVM().BoolInput(loc1++, ((ExprStore)
		// expr).getIdent().getValue());
		// } else {
		// Compiler.getVM().IntInput(loc1++, ((ExprStore)
		// expr).getIdent().getValue());
		// }
		// return (nextCmd != null ? nextCmd.code(loc1) : loc1);
		// }
	}

	public class CmdOutput extends Cmd {
		private Expression expr;
		private final Cmd nextCmd;
		private Type type;

		public CmdOutput(Expression expr, Cmd nextCmd) {
			this.expr = expr;
			this.nextCmd = nextCmd;
		}

		@Override
		public String toString(String indent) {
			return indent + "<CmdOutput>\n" + expr.toString(indent + '\t')
					+ (nextCmd != null ? nextCmd.toString(indent + '\t') : indent + "\t<noNextElement/>\n") + indent
					+ "</CmdOutput>\n";
		}

		public Expression getExpr() {
			return expr;
		}

		@Override
		public Cmd getNextCmd() {
			return nextCmd;
		}

		@Override
		public int getLine() {
			return expr.getLine();
		}

		@Override
		public void check(final boolean canInit) throws ContextError {
			Object tmp = expr.checkR();

			if (tmp instanceof TypedIdentArr) {
				type = new Type(TypeAttribute.INT64);
			} else if (tmp instanceof TypedIdentType) {
				type = ((TypedIdentType) tmp).getType();
			} else {
				type = (Type) tmp;
			}

			if (!(expr instanceof ExprStore) && !((expr instanceof ExprDyadic)
					&& ((ExprDyadic) expr).getOperator().getValue() == OperatorAttribute.DOT
					&& (((ExprDyadic) expr).getExpr2() instanceof ExprStore))) {
				throw new ContextError("Output needs to be a store!");
			}

			if (nextCmd != null)
				nextCmd.check(canInit);
		}

		// @Override
		// public int code(final int loc) {
		// int loc1;
		// if (expr instanceof ExprDyadic && ((ExprDyadic)
		// expr).getOperator().getValue() ==
		// OperatorAttribute.DOT) {
		// ExprStore expr1 = (ExprStore) ((ExprDyadic) expr).getExpr1();
		// ExprStore expr2 = (ExprStore) ((ExprDyadic) expr).getExpr2();
		// Store store = (Store) Compiler.getGlobalStoreTable()
		// .getStore(expr2.getIdent().getValue() + "." +
		// expr1.getIdent().getValue());
		// expr1.setIdent(store.getType().getIdent());
		// expr = expr1;
		// loc1 = expr.code(loc);
		// } else {
		// loc1 = ((ExprStore) expr).code(loc);
		// }
		//
		// if (type.getValue() == TypeAttribute.BOOL) {
		// Compiler.getVM().BoolOutput(loc1++, ((ExprStore)
		// expr).getIdent().getValue());
		// } else {
		// Compiler.getVM().IntOutput(loc1++, ((ExprStore)
		// expr).getIdent().getValue());
		// }
		// return (nextCmd != null ? nextCmd.code(loc1) : loc1);
		// }
	}

	public abstract class TypedIdent<T> {

		public abstract String toString(String indent);

		public abstract Ident getIdent();

		public abstract T getType();

	}

	public class TypedIdentArr extends TypedIdent {
		private final TypedIdent ti;
		private final CmdAssi cmdAssi;
		//private final RangeVal rangeval;

		public TypedIdentArr(TypedIdent ti, CmdAssi cmdAssi) {
			this.ti = ti;
			this.cmdAssi = cmdAssi;
			//this.rangeval = rangeval;

		}

		@Override
		public String toString(String indent) {
			return indent + "<TypedArr>\n" + ti.toString(indent + '\t') + cmdAssi.toString(indent + '\t') + indent
					+ "</TypedArr>\n";
		}

		@Override
		public Ident getIdent() {
			return ti.getIdent();
		}

		@Override
		public Object getType() {
			return ti.getType();
		}

	}

	public class TypedIdentIdent extends TypedIdent<Ident> {

		private final Ident firstIdent;
		private final Ident ident;

		public TypedIdentIdent(Ident firstIdent, Ident ident) {
			this.firstIdent = firstIdent;
			this.ident = ident;
		}

		public String toString(String indent) {
			return indent + "<TypedIdentIdent>\n" + firstIdent.toString(indent + '\t') + ident.toString(indent + '\t')
					+ indent + "</TypedIdentIdent>\n";
		}

		public Ident getIdent() {
			return firstIdent;
		}

		public Ident getType() {
			return ident;
		}
	}

	public class TypedIdentType extends TypedIdent<Type> {
		private final Ident ident;
		private final Type type;

		public TypedIdentType(Ident ident, Type type) {
			this.ident = ident;
			this.type = type;
		}

		public String toString(String indent) {
			return indent + "<TypedIdentType>\n" + ident.toString(indent + '\t') + type.toString(indent + '\t') + indent
					+ "</TypedIdentType>\n";
		}

		public Ident getIdent() {
			return ident;
		}

		public Type getType() {
			return type;
		}
	}

	public abstract class Expression<T> {
		public abstract String toString(String indent);

		abstract T checkR() throws ContextError;

		abstract T checkL(boolean canInit) throws ContextError;

		// abstract int code(int loc);

		abstract int getLine();
	}

	public class ExprArray extends Expression {
		private final Ident ident;
		private final Expression expression;
		private final boolean isInit = true;

		public ExprArray(Ident ident, Expression expression2) {
			this.ident = ident;
			this.expression = expression2;
		}

		@Override
		public String toString(String indent) {
			return indent + "<ExprArray>\n" + expression.toString(indent + '\t') + indent + "\t<ArrayName>\n"
					+ ident.toString(indent + '\t' + '\t') + indent + "\t</ArrayName>\n" + indent + "</ExprArray>\n";
		}
		
		@Override
		public TypedIdentType checkR() throws ContextError {
			TypedIdent type = Compiler.getScope().getType(ident.getValue());

			if (type == null) {
				throw new ContextError("Ident " + ident.getValue() + " not declared");
			}

			if (type instanceof TypedIdentIdent) {
				throw new ContextError("Records cannot be used here. " + " Record " + ident.getValue());
			}

			if (isInit && !(type instanceof TypedIdentArr)) {
				throw new ContextError(
						"Initialization of " + ident.getValue() + " found in right part of an assignement");
			}

			// if (!((Store)
			// Compiler.getScope().getStoreTable().getStore(ident.getValue())).isInitialized())
			// {
			// throw new ContextError("Store " + ident.getValue() + " is not
			// initialized");
			// }

			return new TypedIdentType(type.getIdent(), (Type) type.getType());
		}

		@Override
		public TypedIdent checkL(final boolean canInit) throws ContextError {

			TypedIdent type = Compiler.getScope().getType(ident.getValue());

			if (type == null) {
				throw new ContextError("Ident " + ident.getValue() + " not declared");
			}

			Store store = (Store) Compiler.getScope().getStoreTable().getStore(ident.getValue());

			if (isInit) {

				if (canInit) {
					throw new ContextError("Store can not be initialized here " + "(loop)!");
				}

				if (store.isInitialized()) {
					throw new ContextError("Store " + ident.getValue() + " is already initialized");
				}

				store.initialize();

			} else if (!store.isInitialized()) {
				throw new ContextError("Store " + ident.getValue() + " is not initialized");
			} else if (!store.isWriteable()) {
				throw new ContextError("Store " + ident.getValue() + " is not writeable");
			}

			return type;
		}

		@Override
		public int getLine() {
			// TODO Auto-generated method stub
			return 0;
		}

		// @Override
		// int code(int loc) {
		// // TODO Auto-generated method stub
		// return 0;
		// }

	}

	public class ExprLiteral extends Expression<Type> {
		private final Literal literal;

		public ExprLiteral(Literal literal) {
			this.literal = literal;
		}

		public String toString(String indent) {
			return indent + "<ExprLiteral>\n" + literal.toString(indent + '\t') + indent + "</ExprLiteral>\n";
		}

		public Literal getLiteral() {
			return literal;
		}

		@Override
		public Type checkR() throws ContextError {
			return literal.getType();
		}

		@Override
		public Type checkL(final boolean canInit) throws ContextError {
			throw new ContextError("Found literal " + literal.getLiteral() + "in the left part of an assignement");
		}

		@Override
		int getLine() {
			return 0;
		}

		// @Override
		// public int code(final int loc) {
		// Compiler.getVM().IntLoad(loc, literal.getLiteral());
		// return loc + 1;
		// }
	}

	public class ExprStore extends Expression<TypedIdent> {
		private Ident ident;
		private final boolean isInit;

		public ExprStore(Ident ident, boolean isInit) {
			this.ident = ident;
			this.isInit = isInit;
		}

		public String toString(String indent) {
			return indent + "<ExprStore>\n" + ident.toString(indent + '\t') + indent + "\t<IsInit>" + isInit
					+ "</IsInit>\n" + indent + "</ExprStore>\n";
		}

		public Ident getIdent() {
			return ident;
		}

		public void setIdent(Ident ident) {
			this.ident = ident;
		}

		public boolean isInit() {
			return isInit;
		}

		public Store getStore() {
			return (Store) Compiler.getGlobalStoreTable().getStore(ident.getValue());
		}

		@Override
		public TypedIdentType checkR() throws ContextError {
			TypedIdent type = Compiler.getScope().getType(ident.getValue());

			if (type == null) {
				throw new ContextError("Ident " + ident.getValue() + " not declared");
			}

			if (type instanceof TypedIdentIdent) {
				throw new ContextError("Records cannot be used here. " + " Record " + ident.getValue());
			}

			if (isInit) {
				throw new ContextError(
						"Initialization of " + ident.getValue() + " found in right part of an assignement");
			}

			// if (!((Store)
			// Compiler.getScope().getStoreTable().getStore(ident.getValue())).isInitialized())
			// {
			// throw new ContextError("Store " + ident.getValue() + " is not
			// initialized");
			// }
			
			if (type instanceof TypedIdentArr) {
				throw new ContextError("Arrays cannot be passed in procs yet!");
			}

			return (TypedIdentType) type;
		}

		@Override
		public TypedIdent checkL(final boolean canInit) throws ContextError {

			TypedIdent type = Compiler.getScope().getType(ident.getValue());

			if (type == null) {
				throw new ContextError("Ident " + ident.getValue() + " not declared");
			}

			Store store = (Store) Compiler.getScope().getStoreTable().getStore(ident.getValue());

			if (isInit) {

				if (canInit) {
					throw new ContextError("Store can not be initialized here " + "(loop)!");
				}

				if (store.isInitialized()) {
					throw new ContextError("Store " + ident.getValue() + " is already initialized");
				}

				store.initialize();

			} else if (!store.isInitialized()) {
				throw new ContextError("Store " + ident.getValue() + " is not initialized");
			} else if (!store.isWriteable()) {
				throw new ContextError("Store " + ident.getValue() + " is not writeable");
			}

			return type;
		}

		@Override
		int getLine() {
			// TODO Auto-generated method stub
			return 0;
		}

		// @Override
		// public int code(final int loc) {
		// Store store = (Store)
		// Compiler.getScope().getStoreTable().getStore(ident.getValue());
		// return ((store != null) ? store.codeLoad(loc) : loc);
		// }
		//
		// public int codeRef(final int loc) {
		// Store store = (Store)
		// Compiler.getScope().getStoreTable().getStore(ident.getValue());
		// return ((store != null) ? store.codeRef(loc) : loc);
		// }
	}

	public class ExprFunCall extends Expression {
		private final RoutineCall routineCall;

		public ExprFunCall(RoutineCall routineCall) {
			this.routineCall = routineCall;
		}

		public String toString(String indent) {
			return indent + "<ExprCall>\n" + routineCall.toString(indent + '\t') + indent + "</ExprCall>\n";
		}

		public RoutineCall getRoutineCall() {
			return routineCall;
		}

		@Override
		Object checkR() throws ContextError {
			return null;
		}

		@Override
		Object checkL(boolean canInit) throws ContextError {
			return null;
		}

		// @Override
		// int code(int loc) {
		// return 0;
		// }

		@Override
		int getLine() {
			return 0;
		}
	}

	public class ExprMonadic extends Expression {
		private final Operator operator;
		private final Expression expr;

		public ExprMonadic(Operator operator, Expression expr) {
			this.operator = operator;
			this.expr = expr;
		}

		public String toString(String indent) {
			return indent + "<ExprMonadic>\n" + operator.toString(indent + '\t') + expr.toString(indent + '\t') + indent
					+ "</ExprMonadic>\n";
		}

		public Operator getOperator() {
			return operator;
		}

		public Expression getExpr() {
			return expr;
		}

		@Override
		Object checkR() throws ContextError {
			return null;
		}

		Object checkL(boolean canInit) throws ContextError {
			return null;
		}

		// @Override
		// int code(int loc) {
		// // TODO Auto-generated method stub
		// return 0;
		// }

		@Override
		int getLine() {
			return 0;
		}
	}

	public final class ExprDyadic extends Expression<Type> {
		private final Operator operator;
		private Expression expr1;
		private Expression expr2;
		private ContextError error;

		public ExprDyadic(Operator operator, Expression expr1, Expression expr2) {
			this.operator = operator;
			this.expr1 = expr1;
			this.expr2 = expr2;
		}

		public String toString(String indent) {
			return indent + "<ExprDyadic>\n" + operator.toString(indent + '\t') + expr1.toString(indent + '\t')
					+ expr2.toString(indent + '\t') + indent + "</ExprDyadic>\n";
		}

		public Operator getOperator() {
			return operator;
		}

		public Expression getExpr1() {
			return expr1;
		}

		public Expression getExpr2() {
			return expr2;
		}

		@Override
		public Type checkR() throws ContextError {
			if (error != null) {
				throw error;
			}

			/**
			 * Check if dyadic expression is of operator DOT
			 */
			if (operator.getValue() == OperatorAttribute.DOT) {
				ExprStore es1 = ((ExprStore) expr1);
				ExprStore es2 = ((ExprStore) expr2);
				String storeName = es2.getIdent().getValue() + "." + es1.getIdent().getValue();
				Ident i = new Ident(storeName);
				ExprStore esRecord = new ExprStore(i, es1.isInit());
				Type type = esRecord.checkR().getType();
				return type;
			} else {
				if (expr1 instanceof ExprDyadic
						&& ((ExprDyadic) expr1).getOperator().getValue() == OperatorAttribute.DOT) {
					ExprStore es1 = (ExprStore) ((ExprDyadic) expr1).getExpr1();
					ExprStore es2 = (ExprStore) ((ExprDyadic) expr1).getExpr2();
					Store store = (Store) Compiler.getGlobalStoreTable()
							.getStore(es2.getIdent().getValue() + "." + es1.getIdent().getValue());
					es1.setIdent(store.getType().getIdent());
					expr1 = es1;
				}

				if (expr2 instanceof ExprDyadic
						&& ((ExprDyadic) expr2).getOperator().getValue() == OperatorAttribute.DOT) {
					ExprStore es1 = (ExprStore) ((ExprDyadic) expr2).getExpr1();
					ExprStore es2 = (ExprStore) ((ExprDyadic) expr2).getExpr2();
					Store store = (Store) Compiler.getGlobalStoreTable()
							.getStore(es2.getIdent().getValue() + "." + es1.getIdent().getValue());
					es1.setIdent(store.getType().getIdent());
					expr2 = es1;
				}

				Type type1;
				Object tmp = expr1.checkR();
				if (tmp instanceof TypedIdentArr) {
					type1 = new Type(TypeAttribute.INT64);
				} else if (tmp instanceof TypedIdentType) {
					type1 = ((TypedIdentType) tmp).getType();
				} else {
					type1 = (Type) tmp;
				}

				Type type2;
				tmp = expr2.checkR();
				if (tmp instanceof TypedIdentArr) {
					type2 = new Type(TypeAttribute.INT64);
				} else if (tmp instanceof TypedIdentType) {
					type2 = ((TypedIdentType) tmp).getType();
				} else {
					type2 = (Type) tmp;
				}

				switch (operator.getValue()) {
				case PLUS:
				case MINUS:
				case TIMES:
				case DIV:
				case MOD:
					if (type1.getValue() == TypeAttribute.INT64 && type2.getValue() == TypeAttribute.INT64) {
						return new Type(TypeAttribute.INT64);
					} else {
						throw new ContextError("Type error in Operator " + operator.getValue());
					}
				case EQ:
				case NE:
					if (type1.getValue() == TypeAttribute.BOOL && type2.getValue() == TypeAttribute.BOOL) {
						return new Type(TypeAttribute.BOOL);
					}
				case GT:
				case LT:
				case GE:
				case LE:
					if (type1.getValue() == TypeAttribute.INT64 && type2.getValue() == TypeAttribute.INT64) {
						return new Type(TypeAttribute.BOOL);
					} else {
						throw new ContextError("Type error in Operator " + operator.getValue());
					}
				case CAND:
				case COR:
					if (type1.getValue() == TypeAttribute.BOOL && type2.getValue() == TypeAttribute.BOOL) {
						return new Type(TypeAttribute.BOOL);
					} else {
						throw new ContextError("Type error in Operator " + operator.getValue());
					}
				default:
					throw new RuntimeException();
				}
			}
		}

		@Override
		public Type checkL(final boolean canInit) throws ContextError {
			if (error != null) {
				throw error;
			}

			switch (operator.getValue()) {
			case DOT:
				TypedIdent type = (TypedIdent) expr2.checkL(canInit);
				if (!Compiler.getGlobalStoreTable().containsIdent(type.getIdent().getValue())) {
					throw new ContextError("Ident " + type.getIdent().getValue() + "not declared");
				}
				Store store = (Store) Compiler.getGlobalStoreTable()
						.getStore(type.getIdent().getValue() + "." + ((ExprStore) expr1).getIdent().getValue());
				((ExprStore) expr1).setIdent(store.getType().getIdent());
				expr1.checkL(canInit);
				return ((TypedIdentType) store.getType()).getType();
			default:
				throw new ContextError("Found operator " + operator.getValue() + "in the left part of an assignement");
			}
		}

		// @Override
		// public int code(final int loc) {
		// int loc1 = expr1.code(loc);
		//
		// if (operator.getValue() != OperatorAttribute.CAND &&
		// operator.getValue() !=
		// OperatorAttribute.COR) {
		// loc1 = expr2.code(loc1);
		//
		// switch (operator.getValue()) {
		// case DOT:
		// break;
		// case PLUS:
		// Compiler.getVM().IntAdd(loc1);
		// break;
		// case MINUS:
		// Compiler.getVM().IntSub(loc1);
		// break;
		// case TIMES:
		// Compiler.getVM().IntMult(loc1);
		// break;
		// case DIV:
		// Compiler.getVM().IntDiv(loc1);
		// break;
		// case MOD:
		// Compiler.getVM().IntMod(loc1);
		// break;
		// case EQ:
		// Compiler.getVM().IntEQ(loc1);
		// break;
		// case NE:
		// Compiler.getVM().IntNE(loc1);
		// break;
		// case GT:
		// Compiler.getVM().IntGT(loc1);
		// break;
		// case LT:
		// Compiler.getVM().IntLT(loc1);
		// break;
		// case GE:
		// Compiler.getVM().IntGE(loc1);
		// break;
		// case LE:
		// Compiler.getVM().IntLE(loc1);
		// break;
		// default:
		// throw new RuntimeException();
		// }
		//
		// return loc1 + 1;
		// } else if (operator.getValue() == OperatorAttribute.CAND) {
		// int loc2 = expr2.code(loc1 + 1);
		// Compiler.getVM().UncondJump(loc2++, loc2 + 1);
		// Compiler.getVM().CondJump(loc1, loc2);
		// Compiler.getVM().IntLoad(loc2++, 0);
		// return loc2;
		// } else {
		// int loc2 = expr2.code(loc1 + 2);
		// Compiler.getVM().UncondJump(loc2++, loc2 + 1);
		// Compiler.getVM().CondJump(loc1, loc1 + 2);
		// Compiler.getVM().UncondJump(loc1 + 1, loc2);
		// Compiler.getVM().IntLoad(loc2++, 1);
		// return loc2;
		// }
		// }

		public void setError(final ContextError contextError) {
			error = contextError;
		}

		@Override
		int getLine() {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public class RoutineCall {
		private final Ident ident;
		private final ExpressionList expressionList;

		public RoutineCall(Ident ident, ExpressionList expressionList) {
			this.ident = ident;
			this.expressionList = expressionList;
		}

		public String toString(String indent) {
			return indent + "<RoutineCall>\n" + ident.toString(indent + '\t') + expressionList.toString(indent + '\t')
					+ indent + "</RoutineCall>\n";
		}

		public Ident getIdent() {
			return ident;
		}

		public ExpressionList getExprList() {
			return expressionList;
		}

	}

	public class ExpressionList {
		@SuppressWarnings("rawtypes")
		private final Expression expression;
		private final ExpressionList expressionList;
		private ch.fhnw.cpib.compiler.context.Parameter param;

		@SuppressWarnings("rawtypes")
		public ExpressionList(Expression expression, ExpressionList expressionList) {
			this.expression = expression;
			this.expressionList = expressionList;
		}

		public String toString(String indent) {
			return indent + "<ExprList>\n" + expression.toString(indent + '\t')
					+ (expressionList != null ? expressionList.toString(indent + '\t') : "") + indent + "</ExprList>\n";
		}

		@SuppressWarnings("rawtypes")
		public Expression getExpression() {
			return expression;
		}

		public ExpressionList getExpressionList() {
			return expressionList;
		}

		public void check(final List<ch.fhnw.cpib.compiler.context.Parameter> paramList, final Set<String> aliasList,
				final boolean canInit) throws ContextError {
			if (paramList.size() <= 0) {
				throw new ContextError("Routione takes less parameters than provided. ");
			}
			param = paramList.get(0);
			paramList.remove(0);
			Type type;
			if (param.getMechMode().getValue() == ModeAttributes.COPY) {
				Object tmp = expression.checkR();
				if (tmp instanceof TypedIdentArr) {
					type = new Type(TypeAttribute.INT64);
				} else if (tmp instanceof TypedIdentType)
					type = ((TypedIdentType) tmp).getType();
				else
					type = (Type) tmp;
			} else {
				Object tmp = expression.checkL(false);
				if (tmp instanceof TypedIdentArr) {
					type = new Type(TypeAttribute.INT64);
				} else if (tmp instanceof TypedIdentType)
					type = ((TypedIdentType) tmp).getType();
				else
					type = (Type) tmp;
				if (!(expression instanceof ExprStore)) {
					throw new ContextError("Only stores can be used as IN REF parameter!");
				}
				if (aliasList.contains(((ExprStore) expression).getStore().getIdent())) {
					throw new ContextError("Store is already used a parameter!");
				}
				aliasList.add(((ExprStore) expression).getStore().getIdent());
			}

			if (type.getValue() != ((TypedIdentType) param.getType()).getType().getValue()) {
				throw new ContextError("Wrong paramter type!");
			}

			if (expressionList != null)
				expressionList.check(paramList, aliasList, canInit);
		}

		// public int code(final int loc) {
		// int loc1;
		// if (param.getMechMode().getValue() == ModeAttributes.COPY) {
		// loc1 = expression.code(loc);
		// } else {
		// loc1 = ((ExprStore) expression).codeRef(loc);
		// }
		//
		// return (expressionList != null ? expressionList.code(loc1) : loc1);
		// }
	}

	public final class GlobalInit {
		private final Ident ident;
		private final GlobalInit globalInit;

		public GlobalInit(Ident ident, GlobalInit globalInit) {
			this.ident = ident;
			this.globalInit = globalInit;
		}

		public String toString(String indent) {
			return indent + "<GlobInit>\n" + ident.toString(indent + '\t')
					+ (globalInit != null ? globalInit.toString(indent + '\t') : "<noNextElement/>\n") + indent
					+ "</GlobInit>\n";
		}

		public int getLine() {
			return 0;
		}

		public Ident getIdent() {
			return ident;
		}

		public GlobalInit getGlobalInit() {
			return globalInit;
		}

		public Set<String> check(final Set<String> initList) throws ContextError {
			if (initList.contains(ident.getValue())) {
				throw new ContextError("Global init already declared!" + " Ident: " + ident.getValue());
			} else {
				initList.add(ident.getValue());
			}
			return globalInit.check(initList);
		}
	}

	public class GlobalImport {
		private final ChangeMode changeMode;
		private final Ident ident;
		private final GlobalImport nextGlobalImport;

		public GlobalImport(ChangeMode changeMode, Ident ident, GlobalImport globalImport) {
			this.changeMode = changeMode;
			this.ident = ident;
			this.nextGlobalImport = globalImport;
		}

		public String toString(final String indent) {
			return indent + "<GlobalImport>\n" + changeMode.toString(indent + '\t') + ident.toString(indent + '\t')
					+ nextGlobalImport.toString(indent + '\t') + indent + "</GloablImport>\n";
		}

		public ChangeMode getChangeMode() {
			return changeMode;
		}

		public Ident getIdent() {
			return ident;
		}

		public GlobalImport getNextGlobalImport() {
			return nextGlobalImport;
		}

		public int getLine() {
			return 0;
		}

		public void check(final Routine routine) throws ContextError {
			Store globalStore = (Store) Compiler.getGlobalStoreTable().getStore(ident.getValue());

			if (globalStore == null) {
				throw new ContextError("Global import is not declared! Ident: " + ident.getValue());
			}

			if (globalStore.isConst() && changeMode.getValue() != ModeAttributes.CONST) {
				throw new ContextError("Cannot import global constant as variable! Ident: " + ident.getValue());
			}

			Store localStore = new Store(globalStore.getIdent(), globalStore.getType(),
					changeMode.getValue() == ModeAttributes.CONST);
			localStore.setAddress(globalStore.getAddress());
			localStore.setReference(false);
			localStore.setRelative(false);

			if (!Compiler.getScope().getStoreTable().addStore(localStore.getType().getIdent().getValue(), localStore)) {
				throw new ContextError("Global ident already used! Ident: " + ident.getValue());
			}

			localStore.initialize();

			routine.addGlobImp(new GlobImp(getChangeMode(), ident.getValue()));
			nextGlobalImport.check(routine);
		}

		public void checkInit() throws ContextError {
			if (!((Store) Compiler.getScope().getStoreTable().getStore(ident.getValue())).isInitialized()) {
				throw new ContextError("global import is never initialized! Ident: " + ident.getValue());
			}
			nextGlobalImport.checkInit();
		}
	}

}
