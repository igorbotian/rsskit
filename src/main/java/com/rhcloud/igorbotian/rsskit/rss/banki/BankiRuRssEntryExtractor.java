package com.rhcloud.igorbotian.rsskit.rss.banki;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class BankiRuRssEntryExtractor {

    private final Map<String, Element> linksOfBankRelatedItems;

    public BankiRuRssEntryExtractor(URL url) throws Exception {
        this.linksOfBankRelatedItems = Collections.unmodifiableMap(
                listBankRelatedItems(parseFeed(url))
        );
    }

    public boolean hasBankNameOrId(String itemLink) {
        return linksOfBankRelatedItems.containsKey(itemLink);
    }

    public String getBankName(String itemLink) {
        if(hasBankNameOrId(itemLink)) {
            Element item = linksOfBankRelatedItems.get(itemLink);
            return getChildValue(item, "bank_name");
        }

        return null;
    }

    public String getBankId(String itemLink) {
        if(hasBankNameOrId(itemLink)) {
            Element item = linksOfBankRelatedItems.get(itemLink);
            return getChildValue(item, "bank_id");
        }

        return null;
    }

    private Document parseFeed(URL url) throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(url.toURI().toString());
    }

    private XPathExpression bankRelatedItemXPath() throws XPathExpressionException {
        return XPathFactory.newInstance().newXPath().compile("//*/item[bank_id]");
    }

    private Map<String, Element> listBankRelatedItems(Document doc) throws XPathExpressionException {
        Map<String, Element> result = new HashMap<>();
        NodeList items = (NodeList) bankRelatedItemXPath().evaluate(doc, XPathConstants.NODESET);

        for(int i = 0; i < items.getLength(); i++) {
            Node node = items.item(i);

            if(node instanceof Element) {
                Element item = (Element) node;
                String link = getChildValue(item, "link");

                if(StringUtils.isNotEmpty(link)) {
                    result.put(link, item);
                }
            }
        }

        return result;
    }

    private String getChildValue(Element node, String childNodeName) {
        NodeList children = node.getChildNodes();

        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if(child instanceof Element && StringUtils.equals(child.getNodeName(), childNodeName)) {
                return child.getTextContent();
            }
        }

        return null;
    }
}
