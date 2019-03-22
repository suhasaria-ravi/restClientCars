package per.ravisu.rest.cars;

import java.util.List;

public class CarsResponseArr {
	private List<CarsResponse> carResponse;

	public List<CarsResponse> getCarResponse() {
		return carResponse;
	}

	public void setCarResponse(List<CarsResponse> carResponse) {
		this.carResponse = carResponse;
	}

	@Override
	public String toString() {
		return "[carResponse=" + carResponse + "]";
	}
}
