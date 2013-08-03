/**
 * Class to save the data that is parsed by the InfoboxParser class.
 * 
 * @author Sven Mattauch
 */
public class InfoboxData {
	
	private String name;
	private double latitude = 0;
	private double longitude = 0;
	private double area = 0;
	private double populationDensity = 0;
	private double population = 0;
	private double distance = 0;
	private String newCoordinate = Double.toString(latitude) + " N, " + Double.toString(longitude) + " E";
	
	

	/**
	 * If the area isn't set, the method will calculate the area with the population and the density.
	 */
	public void calcAreaWithPop() {
		if(area == 0 && population != 0 && populationDensity != 0){
			double tmp = population/populationDensity;
			this.setArea(tmp);
		}
	}
	
	/**
	 * Calculate the Distance from the Midpoint of the area to the vertex. 
	 */
	public void calcDistance(){
		double a = Math.sqrt(area * 0.5);
		double b = a/2;
		
		double x = Math.sqrt(Math.pow(b, 2)+ Math.pow(b, 2));
		x = (x * 1000);
		this.setDistance(x);
	}
	
	
	@Override
	public String toString() {
		return "InfoboxData [name=" + name + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", newCoordinate="
				+ newCoordinate + "]";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * 
	 * @param distance the distance to set
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	/**
	 * 
	 * @return return the newCoordinates
	 */
	public String getNewCoordinate() {
		return newCoordinate;
	}

	/**
	 * 
	 * @param newCoordinate the new Coordinates to set
	 */
	public void setNewCoordinate(String newCoordinate) {
		this.newCoordinate = newCoordinate;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * @return the area
	 */
	public double getArea() {
		return area;
	}
	
	/**
	 * @param area the area to set
	 */
	public void setArea(double area) {
		this.area = area;
	}
	
	/**
	 * @return the populationDensity
	 */
	public double getPopulationDensity() {
		return populationDensity;
	}
	
	/**
	 * @param populationDensity the populationDensity to set
	 */
	public void setPopulationDensity(double populationDensity) {
		this.populationDensity = populationDensity;
	}
	
	/**
	 * @return the population
	 */
	public double getPopulation() {
		return population;
	}
	
	/**
	 * @param population the population to set
	 */
	public void setPopulation(double population) {
		this.population = population;
	}

}
