package de.beuth.sp.belegsystem.lg;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import de.beuth.sp.belegsystem.TestUtil;
import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.lg.TimeSlot.DayOfWeek;

public class CreateDummies {
	enum LESSON {
		_1(DayOfWeek.MONDAY, 90, 10, 0),
		_2(DayOfWeek.MONDAY, 90, 14, 15),
		_3(DayOfWeek.THURSDAY, 90, 10, 0),
		_4(DayOfWeek.WEDNESDAY, 60, 9, 35),
		_5(DayOfWeek.TUESDAY, 90, 10, 0);
		
		private DayOfWeek dayofweek;
		private int duration;
		private int hourofday;
		private int minute;

		LESSON(DayOfWeek dayofweek, int duration, int hourofday, int minute){
			this.dayofweek = dayofweek;
			this.duration = duration;
			this.hourofday = hourofday;
			this.minute = minute;
		}
	}
	public static void  main(String[] p){
		for(LESSON eLesson:LESSON.values()){
			TimeSlot timeslot = new TimeSlot();
			timeslot.setDayOfWeek(eLesson.dayofweek);
			timeslot.setDurationInMinutes(eLesson.duration);
			timeslot.setHourOfDay(eLesson.hourofday);
			timeslot.setMinuteOfHour(eLesson.minute);

			//TimeSlotDAO timeslotDAO = TestUtil.getService(TimeSlotDAO.class);
			//timeslotDAO.saveOrUpdateAndCommit(timeslot);
			
			Calendar startdate = new GregorianCalendar();
			startdate.set(Calendar.DAY_OF_MONTH, 26);
			startdate.set(Calendar.MONTH, Calendar.SEPTEMBER);
			startdate.set(Calendar.YEAR, 2011);
			Lesson lesson = new Lesson();
			
			Calendar enddate = new GregorianCalendar();
			enddate.set(Calendar.DAY_OF_MONTH, 6);
			enddate.set(Calendar.MONTH, Calendar.FEBRUARY);
			enddate.set(Calendar.YEAR, 2012);
			lesson.setStartAndEndDate(startdate, enddate);
			
			lesson.setRepetitionInWeeks(1);
			lesson.setRoom("B111");
			
			
			lesson.setTimeSlot(timeslot);
			//LessonDAO lessonDAO = TestUtil.getService(LessonDAO.class);
			//lessonDAO.saveOrUpdateAndCommit(lesson);
					
			CourseDAO courseDAO = TestUtil.getService(CourseDAO.class);
			Course course = courseDAO.find(UUID.fromString("4cd23acc-3ed3-441f-9962-506e6920829c"));
			course.addLesson(lesson);
			courseDAO.saveOrUpdate(course);
			courseDAO.commitTransaction();
		}
	}
}
