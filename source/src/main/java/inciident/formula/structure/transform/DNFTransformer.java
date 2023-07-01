
package inciident.formula.structure.transform;

import inciident.formula.structure.Formula;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Or;
import inciident.formula.structure.transform.NormalForms.NormalForm;
import inciident.util.job.InternalMonitor;
import inciident.util.tree.Trees;


public class DNFTransformer implements Transformer {

    private final DistributiveLawTransformer distributiveLawTransformer;

    public DNFTransformer() {
        distributiveLawTransformer = new DistributiveLawTransformer(And.class, And::new);
    }

    public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
        distributiveLawTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
    }

    @Override
    public Formula execute(Formula formula, InternalMonitor monitor)
            throws DistributiveLawTransformer.MaximumNumberOfLiteralsExceededException {
        final NFTester nfTester = NormalForms.getNFTester(formula, NormalForm.DNF);
        if (nfTester.isNf) {
            if (!nfTester.isClausalNf()) {
                return NormalForms.toClausalNF(Trees.cloneTree(formula), NormalForm.DNF);
            } else {
                return Trees.cloneTree(formula);
            }
        } else {
            formula = NormalForms.simplifyForNF(Trees.cloneTree(formula));
            formula = distributiveLawTransformer.execute(
                    (formula instanceof Or) ? (Or) formula : new Or(formula), monitor);
            formula = NormalForms.toClausalNF(formula, NormalForm.DNF);
            return formula;
        }
    }
}
