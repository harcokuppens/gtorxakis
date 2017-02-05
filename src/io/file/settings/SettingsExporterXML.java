package io.file.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import core.Session;
import core.SessionSettings;

public class SettingsExporterXML {
	
	public static void exportSettings(SessionSettings settings, String path){
		File file = new File(path);
		Element settingsElement = new Element("settings");
		//Create sections
		Element versionElement = new Element("version");
		versionElement.addContent(Session.PROGRAM_VERSION.toString());
		settingsElement.addContent(versionElement);
		
		settingsElement.addContent(createSettingsElement(settings, SessionSettings.HOST));
		settingsElement.addContent(createSettingsElement(settings, SessionSettings.PORT));
		settingsElement.addContent(createSettingsElement(settings, SessionSettings.ITERATIONS));
		settingsElement.addContent(createSettingsElement(settings, SessionSettings.MODEL));
		settingsElement.addContent(createSettingsElement(settings, SessionSettings.CONNECTION));
		settingsElement.addContent(createSettingsElement(settings, SessionSettings.TORXAKIS_DIRECTORY));
		settingsElement.addContent(createSettingsElement(settings, SessionSettings.TORXAKIS_TYPE));
		
		Document settingsExport = new Document(settingsElement);
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			FileOutputStream output = new FileOutputStream(file);
			outputter.output(settingsExport, output);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static Element createSettingsElement(SessionSettings settings, String cmd){
		Element element = new Element(cmd);
		element.addContent(settings.getAttribute(cmd));
		return element;
	}

}
