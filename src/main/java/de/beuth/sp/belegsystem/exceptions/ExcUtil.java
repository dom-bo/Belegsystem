package de.beuth.sp.belegsystem.exceptions;

import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import multex.Exc;
import multex.Msg;
import multex.MultexUtil;
import multex.extension.AnnotatedExc;
import multex.extension.ExcMessage;
import multex.extension.ExcMessagesToProperties;

import org.apache.tapestry5.ioc.Messages;

import de.beuth.sp.belegsystem.lg.Course;
import de.beuth.sp.belegsystem.lg.Program;

/**
 * Util-Klasse für Exception-Handling. Enthält Methoden für mehrfach zu
 * überprüfende Fehlerbedingungen sowie die dazugehörigen Exceptions. Außerdem
 * Methoden die für Exceptions die passende Exception-Message zurückgeben sowie
 * die auslösende Exception zurückgeben.
 * 
 * 
 */
public abstract class ExcUtil {

	public static void checkForStartDateAfterEndDateExc(final Calendar startDate, final Calendar endDate)
			throws StartDateAfterEndDateExc {
		if (startDate != null && endDate!=null && startDate.after(endDate)) {
			throw MultexUtil.create(StartDateAfterEndDateExc.class, startDate.getTime(), endDate.getTime());
		}
	}

	@ExcMessage("Das Startdatum ({0,date,short}) ist nach dem Enddatum ({1,date,short}).")
	public static final class StartDateAfterEndDateExc extends AnnotatedExc {
		private static final long serialVersionUID = 5255370005203296661L;
	}

	public static void checkForLevels(final Program program, final Integer programLevels, final Course course, final Integer courseLevel) throws ProgramLevelBelowCourseLevelExc {
		if (program==null || programLevels == null || courseLevel == null) return; //kein Test möglich und nötig wenn die zu vergleichenden Sachen (noch) null sind
		if (programLevels < courseLevel) {
			throw MultexUtil.create(ProgramLevelBelowCourseLevelExc.class, program.getName(), programLevels, course.getCourseIdentifier(), courseLevel);
		}
	}

	@ExcMessage("Semesterzahl für Studiengang {0} " +
			   "mit {1,number,integer} zu niedrig für " +
			   "Kurs {2} der im {3,number,integer}. Fachsemester stattfinden soll.")
	public static final class ProgramLevelBelowCourseLevelExc extends AnnotatedExc {
		private static final long serialVersionUID = -5162055257353861356L;
	}
	
	@ExcMessage("Unzureichende Rechte um diese Operation durchführen zu können.")
	public static final class InsufficientRightsExc extends AnnotatedExc {
		private static final long serialVersionUID = 8895382849070265903L;		
	}	
	
    public static String generateExceptionMessage(Throwable exception, Messages messages) {
    	String exceptionMessage = null;		
		exception = getCausingThrowable(exception);
		if (Exc.class.isAssignableFrom(exception.getClass())) {				
			final StringBuffer stringBuffer = new StringBuffer();
			ResourceBundle resourceBundle = null;
			try {
				resourceBundle = ResourceBundle.getBundle(ExcMessagesToProperties.EXC_PROPERTIES_NAME);
			} catch (final MissingResourceException e) {
				//do nothing, resourceBundle will be empty so it should use the ExcMessage - Annotation-value
			}
			Msg.printMessages(stringBuffer, exception, resourceBundle);
			exceptionMessage = stringBuffer.toString();
		} else {    									
			if (messages.contains(exception.getClass().getName())) {
				exceptionMessage = messages.get(exception.getClass().getName());
			} else {
				exceptionMessage = 	exception.getMessage();
			}		
		}	
		return exceptionMessage;
    }	

    public static final Throwable getCausingThrowable(Throwable exception) {
		while (exception.getCause()!=null) {
			exception = exception.getCause();
		}    
		return exception;
    }

}
