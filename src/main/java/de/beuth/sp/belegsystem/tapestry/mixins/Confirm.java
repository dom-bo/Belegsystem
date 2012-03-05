package de.beuth.sp.belegsystem.tapestry.mixins;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * Ein simples Mixin um eine Bestätigungsdialog einzubauen beim onClick-Event
 * einer Komponente die ClientElement implementiert.
 * 
 */
@Import (library="confirm.js")
public class Confirm {

    @Parameter(value = "Sicher?", defaultPrefix = BindingConstants.LITERAL)
    private String message;
    
    @Parameter(value = "true")
    private boolean confirmCheckValue;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @InjectContainer
    private ClientElement element;

    @AfterRender
    public void afterRender() {
    	if (confirmCheckValue) {
    		javaScriptSupport.addScript("new Confirm('%s', '%s');", element.getClientId(), message);
    	}
    }

}