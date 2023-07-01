
package inciident.util.io.xml;

import java.util.ArrayDeque;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class PositionalXMLHandler extends DefaultHandler {

    public static final String LINE_NUMBER_KEY_NAME = "lineNumber";

    private final ArrayDeque<Element> elementStack = new ArrayDeque<>();
    private final StringBuilder textBuffer = new StringBuilder();

    private final Document doc;

    private Locator locator;

    public PositionalXMLHandler(Document doc) {
        super();
        this.doc = doc;
    }

    @Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
            throws SAXException {
        addTextIfNeeded();
        final Element el = doc.createElement(qName);
        for (int i = 0; i < attributes.getLength(); i++) {
            el.setAttribute(attributes.getQName(i), attributes.getValue(i));
        }
        el.setUserData(LINE_NUMBER_KEY_NAME, locator.getLineNumber(), null);
        elementStack.push(el);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) {
        addTextIfNeeded();
        final Element closedEl = elementStack.pop();
        if (elementStack.isEmpty()) {
            doc.appendChild(closedEl);
        } else {
            elementStack.peek().appendChild(closedEl);
        }
    }

    @Override
    public void characters(final char ch[], final int start, final int length) throws SAXException {
        textBuffer.append(ch, start, length);
    }

    private void addTextIfNeeded() {
        if (textBuffer.length() > 0) {
            elementStack.peek().appendChild(doc.createTextNode(textBuffer.toString()));
            textBuffer.delete(0, textBuffer.length());
        }
    }
}
