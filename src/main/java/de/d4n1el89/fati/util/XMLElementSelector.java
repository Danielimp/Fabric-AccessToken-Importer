package de.d4n1el89.fati.util;

import org.jdom2.Element;

import java.util.List;

public interface XMLElementSelector {
    List<Element> getChildren(Element parent);
}
