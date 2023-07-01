
package inciident.formula.io.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.ErrorLiteral;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.AtMost;
import inciident.formula.structure.compound.Biimplies;
import inciident.formula.structure.compound.Implies;
import inciident.formula.structure.compound.Not;
import inciident.formula.structure.compound.Or;
import inciident.util.data.Problem;
import inciident.util.io.format.ParseException;
import inciident.util.io.xml.XMLFormat;


public abstract class AbstractXMLFeatureModelFormat<T, U, V> extends XMLFormat<T> {
    protected static final Pattern inputHeaderPattern =
            Pattern.compile("\\A\\s*(<[?]xml\\s.*[?]>\\s*)?<featureModel[\\s>]");
    protected static final String FEATURE_MODEL = "featureModel";
    protected static final String STRUCT = "struct";
    protected static final String CONSTRAINTS = "constraints";
    protected static final String TRUE = "true";
    protected static final String MANDATORY = "mandatory";
    protected static final String FEATURE = "feature";
    protected static final String OR = "or";
    protected static final String ALT = "alt";
    protected static final String AND = "and";
    protected static final String VAR = "var";
    protected static final String IMP = "imp";
    protected static final String EQ = "eq";
    protected static final String NOT = "not";
    protected static final String CONJ = "conj";
    protected static final String DISJ = "disj";
    protected static final String NAME = "name";
    protected static final String RULE = "rule";
    protected static final String ATMOST1 = "atmost1";
    protected static final String COORDINATES = "coordinates";
    protected static final String DESCRIPTION = "description";
    protected static final String PROPERTY = "property";
    protected static final String GRAPHICS = "graphics";
    protected static final String TAGS = "tags";
    public static final String ABSTRACT = "abstract";
    public static final String HIDDEN = "hidden";

    protected abstract U createFeatureLabel(
            String name, U parentFeatureLabel, boolean mandatory, boolean _abstract, boolean hidden)
            throws ParseException;

    protected abstract void addAndGroup(U featureLabel, List<U> childFeatureLabels);

    protected abstract void addOrGroup(U featureLabel, List<U> childFeatureLabels);

    protected abstract void addAlternativeGroup(U featureLabel, List<U> childFeatureLabels);

    protected abstract void addFeatureMetadata(U featureLabel, Element e) throws ParseException;

    protected abstract V createConstraintLabel();

    protected abstract void addConstraint(V constraintLabel, Formula formula) throws ParseException;

    protected abstract void addConstraintMetadata(V constraintLabel, Element e) throws ParseException;

    protected void parseFeatureTree(Element element) throws ParseException {
        parseFeatureTree(element.getChildNodes(), null, false);
    }

    protected ArrayList<U> parseFeatureTree(NodeList nodeList, U parentFeatureLabel, boolean and)
            throws ParseException {
        final ArrayList<U> featureLabels = new ArrayList<>();
        final List<Element> elements = getElements(nodeList);
        if (parentFeatureLabel == null) {
            if (elements.isEmpty()) {
                throw new ParseException("No root feature!");
            }
            if (elements.size() > 1) {
                throw new ParseException("Multiple root features!");
            }
        } else {
            if (elements.isEmpty()) {
                addParseProblem("No feature in group!", null, Problem.Severity.WARNING);
            }
        }
        for (final Element e : elements) {
            final String nodeName = e.getNodeName();
            switch (nodeName) {
                case DESCRIPTION:
                case GRAPHICS:
                case PROPERTY:
                    if (parentFeatureLabel != null) {
                        addFeatureMetadata(parentFeatureLabel, e);
                    } else {
                        addParseProblem("Misplaced metadata element " + nodeName, e, Problem.Severity.WARNING);
                    }
                    break;
                case AND:
                case OR:
                case ALT:
                case FEATURE:
                    featureLabels.add(parseFeature(parentFeatureLabel, e, nodeName, and));
                    break;
                default:
                    break;
            }
        }
        return featureLabels;
    }

    protected U parseFeature(U parentFeatureLabel, final Element e, final String nodeName, boolean and)
            throws ParseException {
        boolean _abstract = false, mandatory = false, hidden = false;
        String name = null;
        if (e.hasAttributes()) {
            final NamedNodeMap nodeMap = e.getAttributes();
            for (int i = 0; i < nodeMap.getLength(); i++) {
                final org.w3c.dom.Node node = nodeMap.item(i);
                final String attributeName = node.getNodeName();
                final String attributeValue = node.getNodeValue();
                if (attributeName.equals(ABSTRACT)) {
                    _abstract = attributeValue.equals(TRUE);
                } else if (attributeName.equals(MANDATORY)) {
                    mandatory = attributeValue.equals(TRUE);
                } else if (attributeName.equals(NAME)) {
                    name = attributeValue;
                } else if (attributeName.equals(HIDDEN)) {
                    hidden = attributeValue.equals(TRUE);
                } else if (attributeName.equals(COORDINATES)) {
                    // Legacy case, for backwards compatibility
                } else {
                    addParseProblem("Unknown feature attribute: " + attributeName, e, Problem.Severity.WARNING);
                }
            }
        }

        U featureLabel = createFeatureLabel(name, parentFeatureLabel, and && mandatory, _abstract, hidden);

        if (e.hasChildNodes()) {
            final ArrayList<U> featureLabels = parseFeatureTree(e.getChildNodes(), featureLabel, nodeName.equals(AND));
            switch (nodeName) {
                case AND:
                    addAndGroup(featureLabel, featureLabels);
                    break;
                case OR:
                    addOrGroup(featureLabel, featureLabels);
                    break;
                case ALT:
                    addAlternativeGroup(featureLabel, featureLabels);
                    break;
                default:
                    break;
            }
        } else if (!"feature".equals(nodeName)) {
            throw new ParseException("Empty group!");
        }

        return featureLabel;
    }

    protected void parseConstraints(Element constraintsElement, VariableMap map) throws ParseException {
        for (final Element child : getElements(constraintsElement.getChildNodes())) {
            final String nodeName = child.getNodeName();
            if (nodeName.equals(RULE)) {
                try {
                    V constraintLabel = createConstraintLabel();
                    final List<Formula> parsedConstraints =
                            parseConstraints(child.getChildNodes(), constraintLabel, map);
                    if (parsedConstraints.size() == 1) {
                        addConstraint(constraintLabel, parsedConstraints.get(0));
                        if (child.hasAttributes()) {
                            final NamedNodeMap nodeMap = child.getAttributes();
                            for (int i = 0; i < nodeMap.getLength(); i++) {
                                final Node node = nodeMap.item(i);
                                final String attributeName = node.getNodeName();
                                if (attributeName.equals(COORDINATES)) {
                                    // Legacy case, for backwards compatibility
                                } else {
                                    addParseProblem(
                                            "Unknown constraint attribute: " + attributeName,
                                            node,
                                            Problem.Severity.WARNING);
                                }
                            }
                        }
                    } else {
                        addParseProblem("could not parse constraint node " + nodeName, child, Problem.Severity.WARNING);
                    }
                } catch (final Exception exception) {
                    addParseProblem(exception.getMessage(), child, Problem.Severity.WARNING);
                }
            } else {
                addParseProblem("Encountered unknown node " + nodeName, child, Problem.Severity.WARNING);
            }
        }
    }

    protected List<Formula> parseConstraints(NodeList nodeList, V parentConstraintLabel, VariableMap map)
            throws ParseException {
        final List<Formula> nodes = new ArrayList<>();
        List<Formula> children;
        final List<Element> elements = getElements(nodeList);
        for (final Element e : elements) {
            final String nodeName = e.getNodeName();
            switch (nodeName) {
                case DESCRIPTION:
                case GRAPHICS:
                case PROPERTY:
                case TAGS:
                    if (parentConstraintLabel != null) {
                        addConstraintMetadata(parentConstraintLabel, e);
                    } else {
                        addParseProblem("Misplaced metadata element " + nodeName, e, Problem.Severity.WARNING);
                    }
                    break;
                case DISJ:
                    nodes.add(new Or(parseConstraints(e.getChildNodes(), null, map)));
                    break;
                case CONJ:
                    nodes.add(new And(parseConstraints(e.getChildNodes(), null, map)));
                    break;
                case EQ:
                    children = parseConstraints(e.getChildNodes(), null, map);
                    if (children.size() == 2) {
                        nodes.add(biImplies(children.get(0), children.get(1)));
                    } else {
                        addParseProblem("unexpected number of operands for equivalence", e, Problem.Severity.WARNING);
                    }
                    break;
                case IMP:
                    children = parseConstraints(e.getChildNodes(), null, map);
                    if (children.size() == 2) {
                        nodes.add(implies(children.get(0), children.get(1)));
                    } else {
                        addParseProblem("unexpected number of operands for implication", e, Problem.Severity.WARNING);
                    }
                    break;
                case NOT:
                    children = parseConstraints(e.getChildNodes(), null, map);
                    if (children.size() == 1) {
                        nodes.add(new Not(children.get(0)));
                    } else {
                        addParseProblem("unexpected number of operands for negation", e, Problem.Severity.WARNING);
                    }
                    break;
                case ATMOST1:
                    nodes.add(atMostOne(parseConstraints(e.getChildNodes(), null, map)));
                    break;
                case VAR:
                    nodes.add(map.getLiteral(e.getTextContent())
                            .map(l -> (Literal) l)
                            .orElseGet(() -> new ErrorLiteral(nodeName)));
                    break;
                default:
                    addParseProblem("Unknown constraint type: " + nodeName, e, Problem.Severity.WARNING);
            }
        }
        return nodes;
    }

    protected Formula atMostOne(List<? extends Formula> parseFeatures) {
        return new AtMost(parseFeatures, 1);
    }

    protected Formula biImplies(Formula a, Formula b) {
        return new Biimplies(a, b);
    }

    protected Formula implies(Literal a, Formula b) {
        return new Implies(a, b);
    }

    protected Formula implies(Formula a, Formula b) {
        return new Implies(a, b);
    }

    protected Formula implies(Literal f, List<? extends Formula> parseFeatures) {
        return parseFeatures.size() == 1 ? new Implies(f, parseFeatures.get(0)) : new Implies(f, new Or(parseFeatures));
    }
}
