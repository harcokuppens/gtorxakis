package io.file.settings;

import gui.dialogs.RunDialog.TorXakisType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import core.SessionSettings;
import core.Version;

public class SettingsImporterXML {

	public static SessionSettings importSettings(String path) {
		SAXBuilder SAXBuilder = new SAXBuilder();
		File file = new File(path);
		if(!file.exists()) return SessionSettings.getDefaultSettings();
		try{
			Version v;
			Document document = (Document) SAXBuilder.build(file);
			Element settingsElement = document.getRootElement();
			Element versionElement = settingsElement.getChild("version");
			
			v = new Version(versionElement.getText());
			System.out.println("Found version: " + v.toString());
			
			int port = Integer.valueOf(settingsElement.getChild(SessionSettings.PORT).getText());
			int iterations = Integer.valueOf(settingsElement.getChild(SessionSettings.ITERATIONS).getText());
			
			String host = settingsElement.getChild(SessionSettings.HOST).getText();
			String model = settingsElement.getChild(SessionSettings.MODEL).getText();
			String connection = settingsElement.getChild(SessionSettings.CONNECTION).getText();
			String directory = settingsElement.getChild(SessionSettings.TORXAKIS_DIRECTORY).getText();
			
			TorXakisType type = TorXakisType.valueOf(settingsElement.getChild(SessionSettings.TORXAKIS_TYPE).getText());
			
			return new SessionSettings(port, iterations, host, model, connection, type, directory);
		}catch(Exception e){
			return SessionSettings.getDefaultSettings();
		}
	}
}
