package utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

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

public class NameSpaceSynchronizer {

	private GlobalNameSpaceManager globalManager;
	private TreeMap<String, FileTree> localDirectoryTrees;

	public NameSpaceSynchronizer(GlobalNameSpaceManager globalManager) {
		this.globalManager = globalManager;
		this.localDirectoryTrees = new TreeMap<String, FileTree>();
	}

	public void update(String id, LocalNameSpaceManager nsManager) {
		this.localDirectoryTrees.put(id, nsManager.getTreeData());
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
		for (Map.Entry<String, FileTree> entry : this.localDirectoryTrees.entrySet()) {
			String minionID = entry.getKey();
			String minionRootDir = "/tmp/minion_" + minionID;
			FileTree tree = entry.getValue();
			Document doc_ = null;
			try {
				doc_ = this.extractDocFromByte(tree.getData());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			doc = walk(doc_.getDocumentElement(), doc.getDocumentElement(), doc, minionRootDir);
		}
		return doc;
	};

	private Document extractDocFromByte(byte[] data) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(data));
	}

	private Document walk(Node localNode, Node globalNode, Document doc, String dirID) {
		if (localNode.getNodeType() == Node.ELEMENT_NODE && globalNode.getNodeType() == Node.ELEMENT_NODE) {
			Element element;

			if (localNode.getNodeName().equalsIgnoreCase("file"))
				element = doc.createElement("file");
			else
				element = doc.createElement("folder");

			String localFileID = ((Element) localNode).getAttribute("id");
			String[] path = localFileID.split(dirID);
			String globalFileID = null;

			if (path.length > 1) {
				globalFileID = localFileID.split(dirID)[1];
				if (doc.getElementById(globalFileID) == null) {
					element.setAttribute("id", globalFileID);
					element.setIdAttribute("id", true);
					globalNode.appendChild(element);
				}
			} else if (path.length > 0) {
				globalFileID = localFileID.split(dirID)[0];
				if (doc.getElementById(globalFileID) == null) {
					element.setAttribute("id", globalFileID);
					element.setIdAttribute("id", true);
					globalNode.appendChild(element);
				}
			} else {
				globalFileID = "/tmp";
				if (doc.getElementById(globalFileID) == null) {
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

}
