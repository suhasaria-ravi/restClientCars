package per.ravisu.rest.cars;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CustomResponseErrorHandler implements ResponseErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomResponseErrorHandler.class);

	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
		if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
				|| httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
			
			logger.error("\n\n!! Unexpected hasError !! \n Response Header=" + httpResponse.getHeaders().toString());
			logger.error("\nResponse Body=" + getResponseBody(httpResponse.getBody())
					+ "\n\n--------------------------------");
			
		}
		
		return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
				|| httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
	}

	@Override
	public void handleError(ClientHttpResponse httpResponse) throws IOException {
		
		if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
				|| httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
		
			logger.error("\n\n!! Unexpected handleError !! \n Response Header=" + httpResponse.getHeaders().toString());
			logger.error("\nResponse Body=" + getResponseBody(httpResponse.getBody())
					+ "\n\n--------------------------------");
			
		}

		if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
			throw new RestClientException("HttpStatus NOT_FOUND");
		}

	}

	private String getResponseBody(InputStream inp) {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		
		String[] responseBody = null;
		
		try {
			responseBody = mapper.readValue(inp.toString(), String[].class);
		} catch (Exception e) {
			return " -- Unable to parse response body";
		}
		
		return java.util.Arrays.toString(responseBody);
	}

}
