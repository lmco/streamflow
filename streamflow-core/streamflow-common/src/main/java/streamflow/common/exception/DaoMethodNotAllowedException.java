package streamflow.common.exception;

public class DaoMethodNotAllowedException extends StreamflowException {

	public DaoMethodNotAllowedException() {
        super("");
    }

    public DaoMethodNotAllowedException(String message) {
        super(message);
    }
}
