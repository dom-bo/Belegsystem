package de.beuth.sp.belegsystem.tapestry.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import de.beuth.sp.belegsystem.lg.Lesson;

/**
 * Tapestry-Komponente zum Editieren von Start- und Enddatum einer Lesson
 * 
 *
 */
@Import(library = "js/FilterableCalendar.js")
public class LessonCalendar {	

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    
	@Parameter(required = true)
    private Lesson lesson;	
	
	@Inject
	private Request	request;
	
	private Calendar startDate;
	
	private Calendar endDate;
	
	public String getStartDate() {
		if (lesson.getStartDate()!=null) {
			return dateFormat.format(lesson.getStartDate().getTime());
		}
    	return null;
    }
    
    public void setStartDate(String dateAsString) throws ParseException {
    	if (dateAsString!=null) {    		
            startDate = Calendar.getInstance();
    		startDate.setTime(dateFormat.parse(dateAsString));
    	} else {
    		startDate = null;
    	}
    }
    
	public String getEndDate() {
		if (lesson.getEndDate()!=null) {
			return dateFormat.format(lesson.getEndDate().getTime());
		}
    	return null;
    }
    
    public void setEndDate(String dateAsString) throws ParseException {    	
    	if (dateAsString!=null) {    		
    		endDate = Calendar.getInstance();
    		endDate.setTime(dateFormat.parse(dateAsString));
    	} else {
    		endDate = null;
    	}
    	
    	//und jetzt übertragen in die Lesson
    	lesson.setStartAndEndDate(startDate, endDate);
    }    
        
    public String getExcludedWeekdays() {
    	String exclude = "";
		if (lesson.getTimeSlot() != null) {
			exclude = ", 0, 1, 2, 3, 4, 5, 6";
			exclude = exclude.replace(", " + lesson.getTimeSlot().getDayOfWeek().ordinal(), "");				
		}
		return exclude;
    }
    
    public String getLanguage() {    	
    	return "'"+request.getLocale().getLanguage()+"'";
    }
    

}