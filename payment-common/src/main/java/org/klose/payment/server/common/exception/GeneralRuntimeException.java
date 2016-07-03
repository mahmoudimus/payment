package org.klose.payment.server.common.exception;

public class GeneralRuntimeException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public GeneralRuntimeException(String errorCode) {
    super(errorCode);

  }

  public GeneralRuntimeException(String errorCode, Throwable tx) {
    super(errorCode, tx);

  }
  public GeneralRuntimeException(Throwable tx) {
	    super(tx);

	  }
}
