package org.klose.payment.server.common.utils;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.klose.payment.server.common.exception.GeneralRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONHelper {
	private static Logger logger = LoggerFactory.getLogger(JSONHelper.class);
	
	public static Object parse(String jsonStr) {
		Object result = null;
		
		if(jsonStr != null) {
			try {
				result = new JSONParser().parse(jsonStr);
			} catch (ParseException e) {
				logger.error(e.getMessage());
				throw new GeneralRuntimeException(e);
			}
		}
		
		return result;
	}
	

}
