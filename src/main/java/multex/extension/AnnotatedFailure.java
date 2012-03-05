package multex.extension;

import multex.Failure;

public abstract class AnnotatedFailure extends Failure {

	private static final long serialVersionUID = -7858713429229065562L;

	@Override
	public String getDefaultMessageTextPattern() {		
		if (this.getClass().isAnnotationPresent(ExcMessage.class)) {	
			return this.getClass().getAnnotation(ExcMessage.class).value();
		} else {
			return super.getDefaultMessageTextPattern();
		}
	}	
	
}
