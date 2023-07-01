
package inciident.formula.structure;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import inciident.formula.io.textual.FormulaFormat;
import inciident.formula.structure.ValueVisitor.UnknownVariableHandling;
import inciident.formula.structure.atomic.Assignment;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;
import inciident.formula.structure.transform.CNFTransformer;
import inciident.formula.structure.transform.DNFTransformer;
import inciident.formula.structure.transform.NormalForms;
import inciident.formula.structure.transform.NormalForms.NormalForm;
import inciident.formula.structure.transform.VariableMapSetter;
import inciident.util.data.Result;
import inciident.util.io.IO;
import inciident.util.tree.Trees;
import inciident.util.tree.visitor.TreeDepthCounter;
import inciident.util.tree.visitor.TreePrinter;
import inciident.util.tree.visitor.TreeVisitor;

public final class Formulas {

    private Formulas() {}

    public static String printTree(Formula formula) {
        final TreePrinter visitor = new TreePrinter();
        visitor.setFilter(n -> (!(n instanceof Variable)));
        return Trees.traverse(formula, visitor).orElse("");
    }

    public static String printFormula(Formula formula) {
        try (final ByteArrayOutputStream s = new ByteArrayOutputStream()) {
            IO.save(formula, s, new FormulaFormat());
            return s.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static Optional<Object> evaluate(Formula expression, Assignment assignment) {
        final ValueVisitor visitor = new ValueVisitor(assignment);
        visitor.setUnknown(UnknownVariableHandling.ERROR);
        return Trees.traverse(expression, visitor);
    }

    public static boolean isCNF(Formula formula) {
        return NormalForms.isNF(formula, NormalForm.CNF, false);
    }

    public static boolean isDNF(Formula formula) {
        return NormalForms.isNF(formula, NormalForm.DNF, false);
    }

    public static boolean isClausalCNF(Formula formula) {
        return NormalForms.isNF(formula, NormalForm.CNF, true);
    }

    public static Result<Formula> toCNF(Formula formula) {
        return NormalForms.toNF(formula, new CNFTransformer());
    }

    public static Result<Formula> toCNF(Formula formula, int maximumNumberOfLiterals) {
        final CNFTransformer transformer = new CNFTransformer();
        transformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
        return NormalForms.toNF(formula, transformer);
    }

    public static Result<Formula> toDNF(Formula formula) {
        return NormalForms.toNF(formula, new DNFTransformer());
    }

    public static Formula manipulate(Formula node, TreeVisitor<Void, Formula> visitor) {
        final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(Trees.cloneTree(node));
        Trees.traverse(auxiliaryRoot, visitor);
        return auxiliaryRoot.getChild();
    }

    public static int getMaxDepth(Formula expression) {
        return Trees.traverse(expression, new TreeDepthCounter()).get();
    }

    public static Stream<Variable> getVariableStream(Formula node) {
        final Stream<Variable> stream =
                Trees.preOrderStream(node).filter(n -> n instanceof Variable).map(n -> (Variable) n);
        return stream.distinct();
    }

    public static List<Variable> getVariables(Formula node) {
        return getVariableStream(node).collect(Collectors.toList());
    }

    public static List<String> getVariableNames(Formula node) {
        return getVariableStream(node).map(Variable::getName).collect(Collectors.toList());
    }

    public static <T extends Formula> T create(Function<VariableMap, T> fn) {
        return fn.apply(new VariableMap());
    }

    
    public static <T, U extends Formula> T compose(Function<List<U>, T> fn, List<U> expressions) {
        return fn.apply(cloneWithSharedVariableMap(expressions));
    }

    @SafeVarargs
    public static <T, U extends Formula> T compose(Function<List<U>, T> fn, U... expressions) {
        return compose(fn, Arrays.asList(expressions));
    }

    
    @SuppressWarnings("unchecked")
    public static <T extends Formula> List<T> cloneWithSharedVariableMap(List<T> children) {
        final List<VariableMap> maps = children.stream()
                .map(f -> f.getVariableMap().orElseGet(VariableMap::new))
                .collect(Collectors.toList());
        VariableMap composedMap = VariableMap.merge(maps);
        final List<T> collect = children.stream()
                .map(f -> (T) Formulas.manipulate(f, new VariableMapSetter(composedMap)))
                .collect(Collectors.toList());
        return collect;
    }
}
