package per.ravisu.rest.cars;

import java.util.Comparator;

public class MakeSorter implements Comparator<Car> {
	@Override
    public int compare(Car o1, Car o2) {
        return o1.getMake().compareToIgnoreCase(o2.getMake());
    }
}
