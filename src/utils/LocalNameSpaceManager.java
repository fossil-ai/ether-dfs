package utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LocalNameSpaceManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6719694067564124068L;
	private String directory;
	private String minion_id;
	private Document doc;
	private FileTree tree;
	private Map<String, Integer> filesMap;

	public LocalNameSpaceManager(String directory, String id) {
		this.directory = directory;
		this.minion_id = id;
		tree = new FileTree(Integer.parseInt(this.minion_id));
		filesMap = new TreeMap<String, Integer>();
		this.buildTreeFromDir();
	}

	public void buildTreeFromDir() {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			this.doc = docBuilder.newDocument();
			Element rootElement = this.doc.createElement("root");
			rootElement.setAttribute("id", "/tmp");
			rootElement.setIdAttribute("id", true);
			this.doc.appendChild(rootElement);

			this.doc = this.walkDirectoryToDoc(this.doc, this.directory);

			Source source = new DOMSource(this.doc);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Result result = new StreamResult(out);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			this.tree.setData(out.toByteArray());

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

	};

	public FileTree getTreeData() {
		return this.tree;
	}

	public boolean hasFile(String filePath) {
		return this.filesMap.containsKey(filePath);
	}

	public Document walkDirectoryToDoc(Document doc, String directory) {
		try (Stream<Path> filePathStream = Files.walk(Paths.get(directory))) {
			filePathStream.forEach(filePath -> {

				Element parentElement = doc.getElementById(filePath.getParent().toString());
				this.filesMap.put(filePath.toString(), 1);
				if (Files.isRegularFile(filePath)) {
					Element fileElement = doc.createElement("file");
					fileElement.setAttribute("id", filePath.toString());
					fileElement.setIdAttribute("id", true);
					parentElement.appendChild(fileElement);
				} else if (Files.isDirectory(filePath)) {
					Element folderElement = doc.createElement("folder");
					folderElement.setAttribute("id", filePath.toString());
					folderElement.setIdAttribute("id", true);
					parentElement.appendChild(folderElement);
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	public Document getDoc() {
		return this.doc;
	}

}
