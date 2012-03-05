package de.beuth.sp.belegsystem.tapestry.services;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import multex.Exc;
import multex.MultexUtil;
import multex.extension.ExcMessagesToProperties;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.hibernate.HibernateSymbols;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Advise;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.ResponseRenderer;
import org.slf4j.Logger;

import de.beuth.sp.belegsystem.Settings;
import de.beuth.sp.belegsystem.db.dao.AdminDAO;
import de.beuth.sp.belegsystem.db.dao.CourseDAO;
import de.beuth.sp.belegsystem.db.dao.DAO;
import de.beuth.sp.belegsystem.db.dao.InstructorDAO;
import de.beuth.sp.belegsystem.db.dao.LessonDAO;
import de.beuth.sp.belegsystem.db.dao.ParticipantDAO;
import de.beuth.sp.belegsystem.db.dao.ProgramDAO;
import de.beuth.sp.belegsystem.db.dao.TermDAO;
import de.beuth.sp.belegsystem.db.dao.TimeSlotDAO;
import de.beuth.sp.belegsystem.db.dao.UserDAO;
import de.beuth.sp.belegsystem.db.dao.hibernate.AdminDAOImpl;
import de.beuth.sp.belegsystem.db.dao.hibernate.CourseDAOImpl;
import de.beuth.sp.belegsystem.db.dao.hibernate.DAOImpl;
import de.beuth.sp.belegsystem.db.dao.hibernate.InstructorDAOImpl;
import de.beuth.sp.belegsystem.db.dao.hibernate.LessonDAOImpl;
import de.beuth.sp.belegsystem.db.dao.hibernate.ParticipantDAOImpl;
import de.beuth.sp.belegsystem.db.dao.hibernate.ProgramDAOImpl;
import de.beuth.sp.belegsystem.db.dao.hibernate.TermDAOImpl;
import de.beuth.sp.belegsystem.db.dao.hibernate.TimeSlotDAOImpl;
import de.beuth.sp.belegsystem.db.dao.hibernate.UserDAOImpl;
import de.beuth.sp.belegsystem.db.manager.CourseManager;
import de.beuth.sp.belegsystem.db.manager.TimeSlotManager;
import de.beuth.sp.belegsystem.db.manager.impl.CourseManagerImpl;
import de.beuth.sp.belegsystem.db.manager.impl.TimeSlotManagerImpl;
import de.beuth.sp.belegsystem.exceptions.ExcUtil;
import de.beuth.sp.belegsystem.exceptions.ExcUtil.InsufficientRightsExc;
import de.beuth.sp.belegsystem.lg.Admin;
import de.beuth.sp.belegsystem.lg.TimeSlot;
import de.beuth.sp.belegsystem.lg.TimeSlot.DayOfWeek;
import de.beuth.sp.belegsystem.lg.User;

/**
 * This module is automatically included as part of the Tapestry IoC Registry, it's a good place to
 * configure and extend Tapestry, or to place your own service definitions.
 */
public class AppModule
{
    public static void bind(final ServiceBinder binder)
    {
    	//DAO-Klassen
    	binder.bind(DAO.class, DAOImpl.class);
        binder.bind(UserDAO.class, UserDAOImpl.class);
        binder.bind(TermDAO.class, TermDAOImpl.class);
        binder.bind(InstructorDAO.class, InstructorDAOImpl.class);
        binder.bind(ParticipantDAO.class, ParticipantDAOImpl.class);
        binder.bind(ProgramDAO.class, ProgramDAOImpl.class);
        binder.bind(CourseDAO.class, CourseDAOImpl.class);
        binder.bind(LessonDAO.class, LessonDAOImpl.class);
        binder.bind(TimeSlotDAO.class, TimeSlotDAOImpl.class);
        binder.bind(AdminDAO.class, AdminDAOImpl.class);     
    	
        //spezielle Manager
        binder.bind(CourseManager.class, CourseManagerImpl.class);
        binder.bind(TimeSlotManager.class, TimeSlotManagerImpl.class);

    	// binder.bind(MyServiceInterface.class, MyServiceImpl.class);
        
        // Make bind() calls on the binder object to define most IoC services.
        // Use service builder methods (example below) when the implementation
        // is provided inline, or requires more initialization than simply
        // invoking the constructor.    	
    	
    }
    
    @Match("*Manager")
    public static void adviseTransactions(final HibernateTransactionAdvisor advisor, final MethodAdviceReceiver receiver)
    {
        advisor.addTransactionCommitAdvice(receiver);
    }
    
    
    public static void contributeApplicationDefaults(
            final MappedConfiguration<String, String> configuration)
    {
        // Contributions to ApplicationDefaults will override any contributions to
        // FactoryDefaults (with the same key). Here we're restricting the supported
        // locales to just "en" (English). As you add localised message catalogs and other assets,
        // you can extend this list of locales (it's a comma separated series of locale names;
        // the first locale name is the default when there's no reasonable match).
        
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "de, en");

        // The factory default is true but during the early stages of an application
        // overriding to false is a good idea. In addition, this is often overridden
        // on the command line as -Dtapestry.production-mode=false
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");

        // The application version number is incorprated into URLs for some
        // assets. Web browsers will cache assets because of the far future expires
        // header. If existing assets are changed, the version number should also
        // change, to force the browser to download new versions.
        configuration.add(SymbolConstants.APPLICATION_VERSION, "1.0.0-SNAPSHOT");
        
        // Hibernate-Configuration
        configuration.add(HibernateSymbols.EARLY_START_UP, "true");
        configuration.add(HibernateSymbols.ENTITY_SESSION_STATE_PERSISTENCE_STRATEGY_ENABLED, "true");
        
		// Auf true setzen um die Generierung von properties- &
		// jar-Datei aus den AnnotatedExc-Klassen zu aktivieren
        configuration.add("exc.generate-messages", "false");        
    }
    
    public static void contributeHibernateEntityPackageManager(final Configuration<String> configuration) {
    	configuration.add("de.beuth.sp.belegsystem.lg");
    }
    
	/**
	 * Type Coercion is the conversion of one type of object to a new object of
	 * a different type with similar content.
	 * http://tapestry.apache.org/typecoercer-service.html
	 */
	@SuppressWarnings("rawtypes")
	public static void contributeTypeCoercer(final Configuration<CoercionTuple> configuration) {
		final Coercion<String, UUID> stringToUUID = new Coercion<String, UUID>() {
			@Override
			public UUID coerce(final String input) {
				return UUID.fromString(input);
			}
		};
		configuration.add(new CoercionTuple<String, UUID>(String.class, UUID.class, stringToUUID));
		
		final Coercion<GregorianCalendar, Date> gregorianCalendarToDate = new Coercion<GregorianCalendar, Date>() {
			@Override
			public Date coerce(final GregorianCalendar input) {
				return input.getTime();
			}
		};
		configuration.add(new CoercionTuple<GregorianCalendar, Date>(GregorianCalendar.class, Date.class, gregorianCalendarToDate));
	}

    /**
     * This is a service definition, the service will be named "TimingFilter". The interface,
     * RequestFilter, is used within the RequestHandler service pipeline, which is built from the
     * RequestHandler service configuration. Tapestry IoC is responsible for passing in an
     * appropriate Logger instance. Requests for static resources are handled at a higher level, so
     * this filter will only be invoked for Tapestry related requests.
     * 
     * <p>
     * Service builder methods are useful when the implementation is inline as an inner class
     * (as here) or require some other kind of special initialization. In most cases,
     * use the static bind() method instead. 
     * 
     * <p>
     * If this method was named "build", then the service id would be taken from the 
     * service interface and would be "RequestFilter".  Since Tapestry already defines
     * a service named "RequestFilter" we use an explicit service id that we can reference
     * inside the contribution method.
     */    
    public RequestFilter buildTimingFilter(final Logger log)
    {
        return new RequestFilter()
        {
            @Override
			public boolean service(final Request request, final Response response, final RequestHandler handler)
                    throws IOException
            {
                final long startTime = System.currentTimeMillis();

                try
                {
                    // The responsibility of a filter is to invoke the corresponding method
                    // in the handler. When you chain multiple filters together, each filter
                    // received a handler that is a bridge to the next filter.
                    
                    return handler.service(request, response);
                }
                finally
                {
                    final long elapsed = System.currentTimeMillis() - startTime;

                    log.info(String.format("Request time: %d ms", elapsed));
                }
            }
        };
    }

    /**
     * This is a contribution to the RequestHandler service configuration. This is how we extend
     * Tapestry using the timing filter. A common use for this kind of filter is transaction
     * management or security. The @Local annotation selects the desired service by type, but only
     * from the same module.  Without @Local, there would be an error due to the other service(s)
     * that implement RequestFilter (defined in other modules).
     */
    public void contributeRequestHandler(final OrderedConfiguration<RequestFilter> configuration,
            @Local final RequestFilter filter) {
        // Each contribution to an ordered configuration has a name, When necessary, you may
        // set constraints to precisely control the invocation order of the contributed filter
        // within the pipeline.
        
        configuration.add("Timing", filter);
    }
    
    /* Wird aktuell nicht mehr benötigt
    @Contribute(ValidatorMacro.class)
    public static void combinePasswordValidators(MappedConfiguration<String, String> configuration) {
          configuration.add("password","required,minlength=5,maxlength=15");
    }
    */
    
	public RequestExceptionHandler decorateRequestExceptionHandler(final Logger logger,
																   final ResponseRenderer renderer, 
																   final ComponentSource componentSource,
																   final RequestGlobals requestGlobals, 
																   final Messages messages, 
																   @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) {
		if (!productionMode) {
			return null;
		}

		return new RequestExceptionHandler() {
			@Override
			public void handleRequestException(final Throwable exception) throws IOException {
				
				if (!Exc.class.isAssignableFrom(ExcUtil.getCausingThrowable(exception).getClass())) {
					//keine fachliche Exception (Exc), also geben wir sie auch an den Logger weiter
					logger.error("Exception: " + exception.getMessage(), exception);
				}

				requestGlobals.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				
				if (requestGlobals.getRequest().isXHR()) {
			        String rawMessage = ExcUtil.generateExceptionMessage(exception, messages);
			        String encoded = URLEncoder.encode(rawMessage, "ISO-8859-1").replace("+", "%20"); //enkodiert zu ISO-8859-1 da sonst Darstellungsfehler
			        requestGlobals.getResponse().setHeader("X-Tapestry-ErrorMessage", encoded);
				} else {
			        final Component page = componentSource.getActivePage();
					((ExceptionReporter)page).reportException(exception);
					renderer.renderPageMarkupResponse(page.getComponentResources().getPageName());
				}
			}
		};
	}  
	
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public static void contributeComponentRequestHandler(OrderedConfiguration configuration) {
		configuration.addInstance(AccessFilter.class.getSimpleName(), AccessFilter.class);
	}
	
	@Advise
	@Match({"*Manager"})
	public static void adviseSecurityCheck(final MethodAdviceReceiver receiver, final ApplicationStateManager applicationStateManager) {
		MethodAdvice advice = new MethodAdvice() {
			public void advise(MethodInvocation invocation) {
				User user = applicationStateManager.getIfExists(User.class);
				if (!AccessFilter.checkAccess(user, receiver.getMethodAnnotation(invocation.getMethod(), Restricted.class))) {
					invocation.setCheckedException(MultexUtil.create(InsufficientRightsExc.class));
					invocation.rethrow();
				} else {
					invocation.proceed();
				}
			}
		};
		for (Method method : receiver.getInterface().getMethods()) {		
			if (method.isAnnotationPresent(Restricted.class)) {
				receiver.adviseMethod(method, advice);
			}
		}		
	}

	@Startup
	public void writeAnnotatedExcDefaultMessagePatternsToPropertiesFile(@Symbol("exc.generate-messages") final boolean generate, final Logger logger) {		
		if (generate) {
			try {			
				final File webInf = new File("src/main/webapp/WEB-INF/");
				ExcMessagesToProperties.write(new File(webInf.getPath() + "/" + ExcMessagesToProperties.PROP_FILE_NAME), false, "de.beuth.sp.belegsystem");			
				ExcMessagesToProperties.createJarAndAddToClasspath(new File(webInf.getPath() + "/" + ExcMessagesToProperties.JAR_FILE_NAME), webInf.listFiles(new FileFilter() {				
					@Override
					public boolean accept(final File pathname) {
						if (!pathname.isDirectory() && pathname.exists() && pathname.getName().startsWith("exc") && pathname.getName().endsWith(".properties")) {
							return true;
						}
						return false;
					}
				}));
			} catch (final IOException e) {
				logger.error("IOException: Could not create properties-file for Exception-MessagePatterns", e);
			}
		}
	}
	
	@Startup 
	public void generateTimeSlotsForBHT(@Inject final TimeSlotDAO timeSlotDAO) {
		boolean timeslotsGenerated = false;
		final int durationInMinutes = Settings.getDurationInMinutes();
		for (int dayOfWeek = 1; dayOfWeek <= 6; dayOfWeek++) {	
			timeslotsGenerated = generateTimeSlot(timeSlotDAO, dayOfWeek, 8, 0, durationInMinutes)==false ? timeslotsGenerated : true;
			timeslotsGenerated = generateTimeSlot(timeSlotDAO, dayOfWeek, 10, 0, durationInMinutes)==false ? timeslotsGenerated : true;
			timeslotsGenerated = generateTimeSlot(timeSlotDAO, dayOfWeek, 12, 15, durationInMinutes)==false ? timeslotsGenerated : true;
			timeslotsGenerated = generateTimeSlot(timeSlotDAO, dayOfWeek, 14, 15, durationInMinutes)==false ? timeslotsGenerated : true;
			timeslotsGenerated = generateTimeSlot(timeSlotDAO, dayOfWeek, 16, 0, durationInMinutes)==false ? timeslotsGenerated : true;
			timeslotsGenerated = generateTimeSlot(timeSlotDAO, dayOfWeek, 17, 45, durationInMinutes)==false ? timeslotsGenerated : true;
			timeslotsGenerated = generateTimeSlot(timeSlotDAO, dayOfWeek, 19, 30, durationInMinutes)==false ? timeslotsGenerated : true;
			timeslotsGenerated = generateTimeSlot(timeSlotDAO, dayOfWeek, 21, 0, durationInMinutes)==false ? timeslotsGenerated : true;
		}
		if (timeslotsGenerated) {
			timeSlotDAO.commitTransaction();
		}
	}
	
	private boolean generateTimeSlot(final TimeSlotDAO timeSlotDAO, final int dayOfWeek, final int hourOfDay, final int minuteOfHour, final int durationInMinutes) {
		if (timeSlotDAO.getTimeSlot(DayOfWeek.values()[dayOfWeek], hourOfDay, minuteOfHour) == null) {
			TimeSlot timeSlot = new TimeSlot();
			timeSlot.setDayOfWeek(DayOfWeek.values()[dayOfWeek]);
			timeSlot.setHourOfDay(hourOfDay);
			timeSlot.setMinuteOfHour(minuteOfHour);		
			timeSlot.setDurationInMinutes(durationInMinutes);
			timeSlotDAO.saveOrUpdate(timeSlot);
			return true;
		}
		return false;
	}
	
	@Startup
	public void generateStandardAdminUser(@Inject final AdminDAO adminDAO, @Inject final UserDAO userDAO) {
		if (Settings.isCreateDefaultAdmin()) {
			User adminUser = userDAO.getUserByUsername(Settings.getDefaultAdminName());
			if (adminUser == null) {
				adminUser = new User();
				adminUser.setUsername(Settings.getDefaultAdminName());
				adminUser.setPassword(Settings.getDefaultAdminPassword());
				adminUser.addRole(new Admin());
			} else {
				if (!adminUser.hasRoleOfType(Admin.class)) {
					adminUser.addRole(new Admin());
				}
				if (!adminUser.getRoleOfType(Admin.class).isSuperAdmin()) {
					adminUser.getRoleOfType(Admin.class).setSuperAdmin(true);
				}
			}
			userDAO.saveOrUpdate(adminUser);
			userDAO.commitTransaction();
		}
	}	
}
