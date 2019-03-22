package per.ravisu.rest.cars;

import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import per.ravisu.rest.cars.CarsResponse;

@SpringBootApplication
public class RestClientApplication implements CommandLineRunner{

	private static final Logger log = LoggerFactory.getLogger(RestClientApplication.class);

	@Value("${url.endpoint}")
	private String urlEndPoint;
	
	@Autowired
	RestTemplate restTemplate;

	public static void main(String args[]) {
		log.info("Logging start ..");
		SpringApplication.run(RestClientApplication.class);
		log.info("Logging end ..");
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.errorHandler(new CustomResponseErrorHandler()).build();
	}
	
	public void run(String... args){					
		
			log.info("Connecting to URL :" + urlEndPoint);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("User-Agent", "Mozilla/5.0"); //MyClientLibrary/2.0

			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

			ResponseEntity<String> responseEntity = null;
			try {

				responseEntity = restTemplate.exchange(urlEndPoint, HttpMethod.GET, entity, String.class);

			} catch (HttpStatusCodeException ex) {
				log.error("Response body is:" + ex.getResponseBodyAsString());
				log.error("HttpStatusCodeException:" + ex.getMessage() + "\n Exception Trace:" + ex.toString());
			} catch (RestClientException ex) {
				log.error("RestClientException msg:" + ex.getMessage() + "RestClientException root:" + ex.getRootCause());
			}
			
			//first checking if responseEntity exists and non-empty then parsing it
			if (responseEntity == null) {

				log.error("Response is null");

			} else if ((!responseEntity.hasBody()) || (responseEntity.getBody().isEmpty())) {

				log.error("Response has empty body");

			} else if (responseEntity.getStatusCode().is2xxSuccessful()) {

				log.info("Output responseEntity =" + responseEntity.toString());

				ObjectMapper mapper = new ObjectMapper();
				mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
				mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
				CarsResponse[] carResponseArr;
				try {

					carResponseArr = mapper.readValue(responseEntity.getBody(), CarsResponse[].class);
					log.info("CarsResponse Array=" + carResponseArr + "\nCarsResponse size=" + carResponseArr.length);									

					CarsResponse[] sortedCarResponse = new CarsResponse[carResponseArr.length];
	
					int i = 0;
	
					//sort the response Cars for each CarShow
					for (CarsResponse cars : carResponseArr) {
						ArrayList<Car> sortedCars = sortCars(cars);
						sortedCarResponse[i] = new CarsResponse();
						sortedCarResponse[i].setCars(sortedCars);
						sortedCarResponse[i].setName(cars.getName());
						i++;
					}
	
					System.out.println("\n\n*****SORTED OUTPUT STARTS************ \n\n");
					
					//display the sorted cars and their show names.
					for (CarsResponse allCars : sortedCarResponse) {
						ArrayList<Car> allSortedCars = (ArrayList<Car>) allCars.getCars();
						for (Car c : allSortedCars) {
							System.out.println("--------------------------");
							System.out.println(c.getMake());
							System.out.println("    " + c.getModel());
							System.out.println("       " + allCars.getName());
							System.out.println("--------------------------");
						} 
	
					}
				} catch (Exception e) {
					//in case of not able to parse, log error and return
					log.error("Could not parse JSON String::" + responseEntity.getBody());
					return;
				}

				System.out.println("\n\n*****SORTED OUTPUT ENDS************ \n\n");

			} else {
				log.error("\n\n !!! Unexpected Response !!! \n\n =" + responseEntity.toString() + "\n\n");
			}
	

	}

	private ArrayList<Car> sortCars(CarsResponse cars) {
		ArrayList<Car> carList = (ArrayList<Car>) cars.getCars();
		carList.sort(new MakeSorter());
		return carList;
	}

}