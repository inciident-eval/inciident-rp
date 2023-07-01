
package inciident.formula.io.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import inciident.formula.structure.AuxiliaryRoot;
import inciident.formula.structure.Formula;
import inciident.formula.structure.Formulas;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Not;
import inciident.formula.structure.compound.Or;
import inciident.formula.structure.transform.CNFTransformer;
import inciident.formula.structure.transform.DeMorganVisitor;
import inciident.formula.structure.transform.TreeSimplifier;
import inciident.formula.structure.transform.VariableMapSetter;
import inciident.util.io.format.ParseException;
import inciident.util.job.Executor;
import inciident.util.tree.Trees;


public class XMLFeatureModelCNFFormat extends XMLFeatureModelFormat {
    @Override
    public XMLFeatureModelCNFFormat getInstance() {
        return new XMLFeatureModelCNFFormat();
    }

    @Override
    public String getName() {
        return "FeatureIDECNF";
    }

    @Override
    protected Formula parseDocument(Document document) throws ParseException {
        final Element featureModelElement = getDocumentElement(document, FEATURE_MODEL);
        parseFeatureTree(getElement(featureModelElement, STRUCT));
        Optional<Element> constraintsElement = getOptionalElement(featureModelElement, CONSTRAINTS);
        if (constraintsElement.isPresent()) {
            parseConstraints(constraintsElement.get(), variableMap);
        }
        return Trees.cloneTree(simplify(new And(constraints)));
    }

    @Override
    protected void addConstraint(Boolean constraintLabel, Formula formula) throws ParseException {
        Formula transformedFormula = Executor.run(new CNFTransformer(), formula)
                .orElseThrow(p -> new ParseException("failed to transform " + formula));
        transformedFormula = Formulas.manipulate(transformedFormula, new VariableMapSetter(variableMap)); // todo: this
        // is a
        // workaround
        // for weird
        // variableMap
        // shenanigans
        super.addConstraint(constraintLabel, transformedFormula);
    }

    @Override
    protected Formula atMostOne(List<? extends Formula> parseFeatures) {
        return new And(groupElements(
                parseFeatures.stream().map(Not::new).collect(Collectors.toList()), 1, parseFeatures.size()));
    }

    @Override
    protected Formula biImplies(Formula a, Formula b) {
        return new And(new Or(new Not(a), b), new Or(new Not(b), a));
    }

    @Override
    protected Formula implies(Literal a, Formula b) {
        return new Or(a.flip(), b);
    }

    @Override
    protected Formula implies(Formula a, Formula b) {
        return new Or(new Not(a), b);
    }

    @Override
    protected Formula implies(Literal f, List<? extends Formula> parseFeatures) {
        final ArrayList<Formula> list = new ArrayList<>(parseFeatures.size() + 1);
        list.add(f.flip());
        list.addAll(parseFeatures);
        return new Or(list);
    }

    private List<Formula> groupElements(List<? extends Formula> elements, int k, final int n) {
        final List<Formula> groupedElements = new ArrayList<>();
        final Formula[] clause = new Formula[k + 1];
        final int[] index = new int[k + 1];

        // the position that is currently filled in clause
        int level = 0;
        index[level] = -1;

        while (level >= 0) {
            // fill this level with the next element
            index[level]++;
            // did we reach the maximum for this level
            if (index[level] >= (n - (k - level))) {
                // go to previous level
                level--;
            } else {
                clause[level] = elements.get(index[level]);
                if (level == k) {
                    final Formula[] clonedClause = new Formula[clause.length];
                    Arrays.copyOf(clause, clause.length);
                    for (int i = 0; i < clause.length; i++) {
                        clonedClause[i] = clause[i];
                    }
                    groupedElements.add(new Or(clonedClause));
                } else {
                    // go to next level
                    level++;
                    // allow only ascending orders (to prevent from duplicates)
                    index[level] = index[level - 1];
                }
            }
        }
        return groupedElements;
    }

    private static Formula simplify(Formula formula) {
        final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(formula);
        Trees.traverse(auxiliaryRoot, new DeMorganVisitor());
        Trees.traverse(auxiliaryRoot, new TreeSimplifier());
        return (Formula) auxiliaryRoot.getChild();
    }
}
