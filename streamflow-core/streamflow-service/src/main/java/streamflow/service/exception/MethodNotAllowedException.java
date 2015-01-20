package streamflow.service.exception;

// TODO: QUESTION: should all exceptions be moved to streamflow-common ?
public class MethodNotAllowedException extends ServiceException {

	public MethodNotAllowedException() {
        super("");
    }

    public MethodNotAllowedException(String message) {
        super(message);
    }
}
