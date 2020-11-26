package utils;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class GlobalNameSpaceManager {

	public final String GLOBAL_NS_FILENAME = "resources/globalnamespace.xml";
	private FileNode root;

	public GlobalNameSpaceManager() {

	}
	
	public void rebuildGlobalPath() {
		File globalNameSpaceFile = new File(this.GLOBAL_NS_FILENAME);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(globalNameSpaceFile);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xmlToFileNode(doc);
	};
	
	public FileNode getRoot(){
		return this.root;
	}

	private void xmlToFileNode(Document doc) {
		doc.getDocumentElement().normalize();
		root = new FileNode();
		root = walk(root, null, doc.getDocumentElement().getFirstChild(), "/");
	}

	private static FileNode walk(FileNode fileNode, FileNode parentNode, Node node, String path) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			if(element.getAttribute("id").equalsIgnoreCase("/tmp")){
				path = element.getAttribute("id");
			}
			else {
				path = "/tmp" + element.getAttribute("id");
			}
			System.out.println(path);
			
			String[] path_split =  element.getAttribute("id").split("/");
			fileNode.filename = path_split[path_split.length - 1];
			
			System.out.println(fileNode.filename);
			fileNode.path = path;
			fileNode.parent = parentNode;

			if (parentNode != null)
				parentNode.children.put(path, fileNode);

			if (node.hasChildNodes()) {
				fileNode.isDir = true;
				fileNode.children = new TreeMap<String, FileNode>();
				for (int i = 0; i < node.getChildNodes().getLength(); i++) {
					walk(new FileNode(), fileNode, node.getChildNodes().item(i), path);
				}
			}
		}
		return fileNode;
	}

}
