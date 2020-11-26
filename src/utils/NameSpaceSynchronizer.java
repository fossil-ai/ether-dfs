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
			rootElement.setAttribute("id", "vmroot");
			rootElement.setIdAttribute("id", true);
			doc.appendChild(rootElement);

			doc = this.buildXMLFromLocalNameSpaces(doc);

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
			doc = walk(entry.getValue().getDoc().getDocumentElement(), doc.getDocumentElement(), doc, minionRootDir);
		}
		return doc;
	};

	private Document walk(Node localNode, Node globalNode, Document doc,  String dirID) {
		if (localNode.getNodeType() == Node.ELEMENT_NODE && globalNode.getNodeType() == Node.ELEMENT_NODE) {
			Element element;
			
			if(localNode.getNodeName().equalsIgnoreCase("file")) 
				element = doc.createElement("file");
			else
				element = doc.createElement("folder");

			String localFileID = ((Element)localNode).getAttribute("id");
			String[] path = localFileID.split(dirID);
			String globalFileID = null;
			
			if(path.length > 1) {
				globalFileID = localFileID.split(dirID)[1];
				if(doc.getElementById(globalFileID) == null) {
					element.setAttribute("id", globalFileID);
		        	element.setIdAttribute("id", true);
		            globalNode.appendChild(element);
				}
			}
			else if(path.length > 0) {
				globalFileID = localFileID.split(dirID)[0];
				if(doc.getElementById(globalFileID) == null) {
					element.setAttribute("id", globalFileID);
		        	element.setIdAttribute("id", true);
		            globalNode.appendChild(element);
				}
			}
			else {
				globalFileID = "/tmp";
				if(doc.getElementById(globalFileID) == null) {
					element.setAttribute("id", globalFileID);
		        	element.setIdAttribute("id", true);
		            globalNode.appendChild(element);
				}
			}
            
			if (localNode.hasChildNodes()) {
				for (int i = 0; i < localNode.getChildNodes().getLength(); i++)
					walk(localNode.getChildNodes().item(i), doc.getElementById(globalFileID), doc, dirID);
			}
		}
		
		return doc;

	}
	
	public static void main(String[] args){
		GlobalNameSpaceManager gManager = new GlobalNameSpaceManager();
		LocalNameSpaceManager lManager_0 = new LocalNameSpaceManager("/tmp/" + "minion_" + 0, "0");
		LocalNameSpaceManager lManager_1 = new LocalNameSpaceManager("/tmp/" + "minion_" + 1, "1");
		NameSpaceSynchronizer syncro = new NameSpaceSynchronizer(gManager);
		syncro.synchronize("0", lManager_0);
		syncro.synchronize("1", lManager_1);
		syncro.buildGlobalNameSpace();
		gManager.rebuildGlobalPath();
		syncro.synchronize("1", lManager_1);
		syncro.buildGlobalNameSpace();
		gManager.rebuildGlobalPath();
		
	}

}
