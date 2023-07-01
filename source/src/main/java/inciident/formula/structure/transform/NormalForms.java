
package inciident.formula.structure.transform;

import inciident.formula.structure.AuxiliaryRoot;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Or;
import inciident.util.data.Result;
import inciident.util.job.Executor;
import inciident.util.tree.Trees;


public final class NormalForms {

    private NormalForms() {}

    public enum NormalForm {
        CNF,
        DNF
    }

    public static Formula simplifyForNF(Formula formula) {
        final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(formula);
        Trees.traverse(auxiliaryRoot, new EquivalenceVisitor());
        Trees.traverse(auxiliaryRoot, new DeMorganVisitor());
        Trees.traverse(auxiliaryRoot, new TreeSimplifier());
        return (Formula) auxiliaryRoot.getChild();
    }

    public static Result<Formula> toNF(Formula root, Transformer transformer) {
        return Executor.run(transformer, root);
    }

    public static boolean isNF(Formula formula, NormalForm normalForm, boolean clausal) {
        final NFTester tester = getNFTester(formula, normalForm);
        return clausal ? tester.isClausalNf() : tester.isNf();
    }

    static NFTester getNFTester(Formula formula, NormalForm normalForm) {
        NFTester tester;
        switch (normalForm) {
            case CNF:
                tester = new CNFTester();
                break;
            case DNF:
                tester = new DNFTester();
                break;
            default:
                throw new IllegalStateException(String.valueOf(normalForm));
        }
        Trees.traverse(formula, tester);
        return tester;
    }

    public static Formula toClausalNF(Formula formula, NormalForm normalForm) {
        switch (normalForm) {
            case CNF:
                if (formula instanceof Literal) {
                    formula = new And(new Or(formula));
                } else if (formula instanceof Or) {
                    formula = new And(formula);
                } else {
                    formula.mapChildren(child -> (child instanceof Literal ? new Or((Literal) child) : child));
                }
                break;
            case DNF:
                if (formula instanceof Literal) {
                    formula = new Or(new And(formula));
                } else if (formula instanceof And) {
                    formula = new Or(new And(formula));
                } else {
                    formula.mapChildren(child -> (child instanceof Literal ? new And((Literal) child) : child));
                }
                break;
            default:
        }
        return formula;
    }
}
