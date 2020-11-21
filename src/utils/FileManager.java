package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;

public class FileManager {

	private Map<String, List<MinionLocation>> fileLocationMap;
	private Map<String, MinionLocation> filePrimaryMinionMap;

	public FileManager(){
		this.fileLocationMap = new HashMap<String, List<MinionLocation>>();
		this.filePrimaryMinionMap = new HashMap<String, MinionLocation>();
		
		File globalNameSpaceFile = new File("resources/globalnamespace.xml");
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
	    //doc.getDocumentElement().normalize();
	    
	    
	    System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
	    NodeList list = doc.getDocumentElement().getChildNodes();
	    for(int i = 0; i < list.getLength(); i++) {
	    	NodeList list_new = list.item(i).getChildNodes();
	    	for(int j = 0; j < list_new.getLength(); j++) {
	    		
                Node nNode = list_new.item(j);
                
                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                	Element eElement = (Element) nNode;
               	 
    	    		System.out.println(eElement.getAttribute("id"));
                }
	    	}
	    }


	}
	

	public void assignPrimaryMinionToFile(String filename, int primaryMinionIndex,
			List<MinionLocation> minionLocations) {
		filePrimaryMinionMap.put(filename, minionLocations.get(primaryMinionIndex));
	}

	public void assignSelectedMinionsToFile(String filename, List<MinionLocation> selectedMinions) {
		fileLocationMap.put(filename, selectedMinions);
	}

	public MinionLocation getPrimaryFileLocation(String fileName) {
		return filePrimaryMinionMap.get(fileName);
	}

	public List<MinionLocation> getAllFileLocation(String fileName) {
		return fileLocationMap.get(fileName);
	}

	public static void main(String argv[]) {

		FileManager filemanager = new FileManager();

	}

}
