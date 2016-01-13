package ch.fhnw.cpib.compiler.context;

import ch.fhnw.cpib.compiler.parser.AbsTree.TypedIdent;
import ch.fhnw.cpib.compiler.parser.AbsTree.TypedIdentIdent;
import ch.fhnw.cpib.compiler.Compiler;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray.*;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions.*;

public final class Store extends Symbol {
    private boolean initialized;
    private boolean isConst;
    private boolean writeable;
    private int address;
    private final String ident;
    private final TypedIdent typedIdent;
    private boolean relative = false;
    private boolean reference = false;

    public Store(final String ident, final TypedIdent typedIdent, final boolean isConst) {
        super(ident, typedIdent);
        this.ident = ident;
        this.typedIdent = typedIdent;
        this.writeable = true;
        this.initialized = false;
        this.isConst = isConst;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isWriteable() {
        return writeable;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize() {
        initialized = true;
        if (isConst)
            writeable = false;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(final int address) {
        this.address = address;
    }

    public void setRelative(final boolean relative) {
        this.relative = relative;
    }

    public void setReference(final boolean reference) {
        this.reference = reference;
    }

    public int codeLoad(final int loc) throws CodeTooSmallError {  //TODO auflösen und in codeRef handlen?
        int loc1 = codeRef(loc, true, false); 
        //Compiler.getVM().Deref(loc1++);
        Compiler.getcodeArray().put(loc1++, new Deref());
        return loc1;
    }

    public int codeRef(final int loc, boolean rel, boolean ref) throws CodeTooSmallError {
        int loc1 = loc;
        
        this.setRelative(rel);
        this.setReference(ref);

        if (relative) {
            //Compiler.getVM().LoadRel(loc1++, address);
            Compiler.getcodeArray().put(loc1++, new LoadAddrRel(Compiler.getIdentTable().get(ident)));
        } else {
            //Compiler.getVM().IntLoad(loc1++, address);
            Compiler.getcodeArray().put(loc1++, new LoadImInt(address));
        }

        if (reference) {
            //Compiler.getVM().Deref(loc1++);
            Compiler.getcodeArray().put(loc1++, new Deref());
        }

        return loc1;
    }

    public Store clone() {
        Store store = new Store(this.getIdent(), this.getType(), this.isConst);
        store.address = this.address;
        store.initialized = this.initialized;
        store.writeable = this.writeable;
        return store;
    }

}
