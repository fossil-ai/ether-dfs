package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class NameSpaceSynchronizer {

	private GlobalNameSpaceManager globalManager;
	private TreeMap<String, LocalNameSpaceManager> localManagerMap;

	public NameSpaceSynchronizer(GlobalNameSpaceManager globalManager) {
		this.globalManager = globalManager;
		this.localManagerMap = new TreeMap<String, LocalNameSpaceManager>();
	}

	/*
	 * Ensure the global namespace can see every file and folder
	 */
	public void synchronize(String id, LocalNameSpaceManager nsManager) {
		this.localManagerMap.put(id, nsManager);
	}

	public void buildGlobalNameSpace() {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("root");
			rootElement.setAttribute("id", "/tmp");
			rootElement.setIdAttribute("id", true);
			doc.appendChild(rootElement);

			this.buildXMLFromLocalNameSpaces(doc);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(this.globalManager.GLOBAL_NS_FILENAME));
			transformer.transform(source, result);
			System.out.println("Global namespace updated!");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Document buildXMLFromLocalNameSpaces(Document doc) {
		for (Map.Entry<String, LocalNameSpaceManager> entry : this.localManagerMap.entrySet()) {
			String minionID = entry.getKey();
			String minionRootDir = "/tmp/minion_" + minionID;
			walk(entry.getValue().getDoc().getDocumentElement(), doc.getDocumentElement(), doc, minionRootDir);
		}
		return doc;
	};

	private void walk(Node localNode, Node globalNode, Document doc,  String dirID) {
		if (localNode.getNodeType() == Node.ELEMENT_NODE && globalNode.getNodeType() == Node.ELEMENT_NODE) {
			Element element;
			
			if(localNode.getNodeName().equalsIgnoreCase("file")) 
				element = doc.createElement("file");
			else
				element = doc.createElement("folder");
			
			String localFileID = ((Element)localNode).getAttribute("id");
			String globalFileID = localFileID.split(dirID)[1];
			element.setAttribute("id", globalFileID);
        	element.setIdAttribute("id", true);
            globalNode.appendChild(element);
            
			if (localNode.hasChildNodes()) {
				for (int i = 0; i < localNode.getChildNodes().getLength(); i++)
					walk(localNode.getChildNodes().item(i), element, doc, dirID);
			}
		}

	}

}
