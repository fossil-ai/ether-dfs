package utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class LocalNameSpaceManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6719694067564124068L;
	private String local_ns_filename;
	private String directory;
	private String minion_id;
	private Document doc;

	public LocalNameSpaceManager(String directory, String id) {
		this.directory = directory;
		this.minion_id = id;
		this.local_ns_filename = "resources/minion_namespaces/minion_namespace_" + this.minion_id + ".xml";
		this.buildXMLFromDir();
	}

	public void buildXMLFromDir() {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			this.doc = docBuilder.newDocument();
			Element rootElement = this.doc.createElement("root");
			rootElement.setAttribute("id", "/tmp");
			rootElement.setIdAttribute("id", true);
			this.doc.appendChild(rootElement);

			this.walkDirectoryToXML(this.doc, this.directory);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(this.doc);
			StreamResult result = new StreamResult(new File(this.local_ns_filename));
			transformer.transform(source, result);
			System.out.println("File saved!");
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

	public Document walkDirectoryToXML(Document doc, String directory) {
		try (Stream<Path> filePathStream = Files.walk(Paths.get(directory))) {
			filePathStream.forEach(filePath -> {

				Element parentElement = doc.getElementById(filePath.getParent().toString());

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
