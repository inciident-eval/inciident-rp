
package inciident.formula.structure.transform;

import inciident.formula.structure.Formula;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Compound;
import inciident.formula.structure.compound.Or;
import inciident.util.job.InternalMonitor;


public class CNFDistributiveLawTransformer extends DistributiveLawTransformer {

    public CNFDistributiveLawTransformer() {
        super(Or.class, Or::new);
    }

    @Override
    public Compound execute(Formula formula, InternalMonitor monitor) throws MaximumNumberOfLiteralsExceededException {
        final Compound compound = (formula instanceof And) ? (And) formula : new And(formula);
        return super.execute(compound, monitor);
    }
}
