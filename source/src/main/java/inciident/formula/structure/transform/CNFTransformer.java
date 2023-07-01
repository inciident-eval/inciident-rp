
package inciident.formula.structure.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.Formulas;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Compound;
import inciident.formula.structure.transform.DistributiveLawTransformer.MaximumNumberOfLiteralsExceededException;
import inciident.formula.structure.transform.NormalForms.NormalForm;
import inciident.formula.structure.transform.TseytinTransformer.Substitute;
import inciident.util.job.InternalMonitor;
import inciident.util.job.NullMonitor;
import inciident.util.tree.Trees;

public class CNFTransformer implements Transformer {

    public final boolean useMultipleThreads = false;

    protected final List<Formula> distributiveClauses;
    protected final List<Substitute> tseytinClauses;
    protected boolean useDistributive;
    protected int maximumNumberOfLiterals = Integer.MAX_VALUE;

    protected VariableMap variableMap = null;

    public CNFTransformer() {
        if (useMultipleThreads) {
            distributiveClauses = Collections.synchronizedList(new ArrayList<>());
            tseytinClauses = Collections.synchronizedList(new ArrayList<>());
        } else {
            distributiveClauses = new ArrayList<>();
            tseytinClauses = new ArrayList<>();
        }
    }

    public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
        this.maximumNumberOfLiterals = maximumNumberOfLiterals;
    }

    @Override
    public Formula execute(Formula orgFormula, InternalMonitor monitor) {
        useDistributive = (maximumNumberOfLiterals > 0);
        final NFTester nfTester = NormalForms.getNFTester(orgFormula, NormalForm.CNF);
        if (nfTester.isNf) {
            if (!nfTester.isClausalNf()) {
                return NormalForms.toClausalNF(Trees.cloneTree(orgFormula), NormalForm.CNF);
            } else {
                return Trees.cloneTree(orgFormula);
            }
        }
        variableMap = orgFormula.getVariableMap().map(VariableMap::clone).orElseGet(VariableMap::new);
        Formula formula = NormalForms.simplifyForNF(Trees.cloneTree(orgFormula));
        if (formula instanceof And) {
            final List<Formula> children = ((And) formula).getChildren();
            if (useMultipleThreads) {
                children.parallelStream().forEach(this::transform);
            } else {
                children.forEach(this::transform);
            }
        } else {
            transform(formula);
        }

        formula = new And(getTransformedClauses());
        formula = NormalForms.toClausalNF(formula, NormalForm.CNF);
        formula = Formulas.manipulate(formula, new VariableMapSetter(variableMap));
        return formula;
    }

    protected List<? extends Formula> getTransformedClauses() {
        final List<Formula> transformedClauses = new ArrayList<>();

        transformedClauses.addAll(distributiveClauses);

        if (!tseytinClauses.isEmpty()) {
            variableMap = variableMap.clone();
            final HashMap<Substitute, Substitute> combinedTseytinClauses = new HashMap<>();
            for (final Substitute tseytinClause : tseytinClauses) {
                Substitute substitute = combinedTseytinClauses.get(tseytinClause);
                if (substitute == null) {
                    combinedTseytinClauses.put(tseytinClause, tseytinClause);
                    final Variable variable = tseytinClause.getVariable();
                    if (variable != null) {
                        variable.rename(variableMap.addBooleanVariable().getName());
                    }
                } else {
                    final Variable variable = substitute.getVariable();
                    if (variable != null) {
                        tseytinClause.getVariable().rename(variable.getName());
                    }
                }
            }
            for (final Substitute tseytinClause : combinedTseytinClauses.keySet()) {
                for (final Formula formula : tseytinClause.getClauses()) {
                    transformedClauses.add(Formulas.manipulate(formula, new VariableMapSetter(variableMap)));
                }
            }
        }
        return transformedClauses;
    }

    private void transform(Formula child) {
        final Formula clonedChild = Trees.cloneTree(child);
        if (Formulas.isCNF(clonedChild)) {
            if (clonedChild instanceof And) {
                distributiveClauses.addAll(((And) clonedChild).getChildren());
            } else {
                distributiveClauses.add(clonedChild);
            }
        } else {
            if (useDistributive) {
                try {
                    distributiveClauses.addAll(
                            distributive(clonedChild, new NullMonitor()).getChildren());
                    return;
                } catch (final MaximumNumberOfLiteralsExceededException e) {
                }
            }
            tseytinClauses.addAll(tseytin(clonedChild, new NullMonitor()));
        }
    }

    protected Compound distributive(Formula child, InternalMonitor monitor)
            throws MaximumNumberOfLiteralsExceededException {
        final CNFDistributiveLawTransformer cnfDistributiveLawTransformer = new CNFDistributiveLawTransformer();
        cnfDistributiveLawTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
        return cnfDistributiveLawTransformer.execute(child, monitor);
    }

    protected List<Substitute> tseytin(Formula child, InternalMonitor monitor) {
        final TseytinTransformer tseytinTransformer = new TseytinTransformer();
        tseytinTransformer.setVariableMap(new VariableMap());
        return tseytinTransformer.execute(child, monitor);
    }
}
