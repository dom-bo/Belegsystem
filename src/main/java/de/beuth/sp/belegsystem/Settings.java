package de.beuth.sp.belegsystem;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Settings {
	
	public static final String PASSWORD_ENCODING_ALGORITHM = "SHA";
	
	public static final String FLASH_MESSAGE_SESSION_ATTRIBUTE = "flashMessage";
	
	public static final String VALIDATE_REQUIRED = "required";
	
	private static final Logger log = LoggerFactory.getLogger(Settings.class);
	
	private static final File SETTINGS_FILE = new File("settings.xml");
	
	private BelegsystemSettings belegsystemSettings;
	
	private static final Settings INSTANCE = new Settings();
	
	private Settings() {			
		try {
			Unmarshaller unmarshaller = JAXBContext.newInstance(BelegsystemSettings.class).createUnmarshaller();
			belegsystemSettings = (BelegsystemSettings) unmarshaller.unmarshal(SETTINGS_FILE);
		} catch (JAXBException e) {
			// Falls JAXBException (z.b. weil Datei falsch formatiert/nicht auffindbar) wird Belegsystem-Settings mit den Standardwerten neu erstellt und abgespeichert.
			log.warn("Settings konnten nicht geladen werden, Settings werden mit Standardwerten initalisiert.");
			belegsystemSettings = new BelegsystemSettings();
			try {
				Marshaller marshaller = JAXBContext.newInstance(BelegsystemSettings.class).createMarshaller();
				marshaller.marshal(belegsystemSettings, SETTINGS_FILE);
			} catch (JAXBException e1) {
				throw new RuntimeException("Settings-Datei konnte nicht gespeichert werden.");
			}
		} 
	}
	
	public static Settings getInstance() {
		return INSTANCE;
	}
	
	public static int getCourseBookingDays() {
		return getInstance().belegsystemSettings.courseBookingDays;
	}
	
	public static int getDurationInMinutes() {
		return getInstance().belegsystemSettings.durationInMinutes;
	}
	
	public static boolean isCreateDefaultAdmin() {
		return getInstance().belegsystemSettings.createDefaultAdmin;
	}
	public static String getDefaultAdminName() {
		return getInstance().belegsystemSettings.defaultAdminName;
	}
	public static String getDefaultAdminPassword() {
		return getInstance().belegsystemSettings.defaultAdminPassword;
	}	
	
	public static String getDefaultPageParticipant() {
		return getInstance().belegsystemSettings.defaultPageParticipant;
	}
	
	public static String getDefaultPageInstructor() {
		return getInstance().belegsystemSettings.defaultPageInstructor;
	}	

	public static String getDefaultPageAdmin() {
		return getInstance().belegsystemSettings.defaultPageAdmin;
	}	
	
		
	@XmlRootElement
	private static class BelegsystemSettings {
		
		public int courseBookingDays = 28;		
		
		public int durationInMinutes = 90;
		
		public boolean createDefaultAdmin = true;
		public String defaultAdminName = "admin";
		public String defaultAdminPassword = "admin";
		
	    public String defaultPageParticipant = "course/book";
	    public String defaultPageInstructor = "course/instructorcourseview";
	    public String defaultPageAdmin = "course/list";

	}



}
