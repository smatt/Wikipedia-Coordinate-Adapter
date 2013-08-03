import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Connects to a wikipedia-page and search for coordinates, area, population and population density. 
 * Used jsoup for the parsing.
 * 
 * @author Sven Mattauch
 *
 */

public class InfoboxParser {
	
	//Set the Numberformat. Need it to parse the Number correctly from Wikipedia.
	private static DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance(Locale.GERMAN);
	
	public static void main(String[] args) {
		
		ArrayList<InfoboxData> boxArray = new ArrayList<InfoboxData>();
		try {
			//The Wikipedia URL that should parsed
			
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Deutschland"));
			
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Bayern"));
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Saarland"));
			
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Berlin"));
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Münster_(Westfalen)"));
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Konstanz"));
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Arnis"));
			
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/M%C3%BCritz"));
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Rieselfeld_(Freiburg_im_Breisgau)"));
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Sylt"));
			boxArray.add(wikiParser("http://de.wikipedia.org/wiki/Th%C3%BCringer_Wald"));
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(InfoboxData k : boxArray){
			System.out.println(k.toString());
		}
	}
	
	/**
	 * Parse the location information (latitude and longitude) and the area information
	 *  (if there is no area information, it parses the population and the population density to calculate the area)
	 * from a (German) Wikipedia page.
	 * Save the gathered information in a InfoboxData object.
	 * 
	 * @param url is the URL to the wikipedia page.
	 * @return returns a InfoboxData with the Data for the parsed Wikipedia-Page and the new Coordinates that describe the area.
	 * @throws IOException
	 * @throws ParseException
	 */
	public static InfoboxData wikiParser(String url) throws IOException, ParseException{
		//Create a Document for the Jsoup HTML-Parser
		Document doc;
		//Parse the Wikipage to the doc.
		doc = Jsoup.connect(url).get();
		
		InfoboxData ibData = new InfoboxData();
		//Get the Name of the Wikipedia page.
		String title = cleanTitle(doc.title());
		ibData.setName(title);
		
		//Get the latitude and the longitude from the Wikipedia page.
		double latitude = Double.parseDouble(doc.select("span.latitude").text());
		ibData.setLatitude(latitude);
		double longitude = Double.parseDouble(doc.select("span.longitude").text()); 
		ibData.setLongitude(longitude);
		

		/* The tables with the area information doesn't have a unique ID,
		 * so that i need to check all tables for the information i want to get from the page. 			
		*/
		Elements table = doc.select("table");
		
		/*
		 * Search in the table-Elements for the "Fläche"-, "Einwohner"- and "Bevölkerungsdichte"-information.
		 * The tables we looking for only contain a size of two.
		 * In the first column is the name of the information we search.
		 * In the second the information we want to get.
		 * If we find the right row, we use the elementToDouble-Methode,
		 *  to parse the number in a formt we can work with.
		 */
		for (Element row : table.select("tr")) {
            Elements tds = row.select("td");
            if (tds.size() == 2) {
            	// Parse the area if it is tagged as "Fläche" ...
            	if(Pattern.matches("Fläche", tds.get(0).text())) {
            		double area = elementToDouble(tds.get(1).text()); // Assumption: value as km²
            		ibData.setArea(area);
            	}
            	// or if it is tagged as "Fläche:"
            	if(Pattern.matches("Fläche:", tds.get(0).text())) {
            		double area = elementToDouble(tds.get(1).text()); // Assumption: value as km²
            		ibData.setArea(area);
            	}
            	if(Pattern.matches("Einwohner:", tds.get(0).text())) {
            		double population = elementToDouble(tds.get(1).text()); // Assumption: value as as integer
            		ibData.setPopulation(population);
            	}
            	if(Pattern.matches("Bevölkerungsdichte:", tds.get(0).text())) {
            		double population_density = elementToDouble(tds.get(1).text()); //Assumption: value as km²
            		ibData.setPopulationDensity(population_density);
            	}
            }
            
		}
		// Add missing values to InfoboxData
		ibData.calcAreaWithPop();
		ibData.calcDistance();
		ibData.setNewCoordinate(CoordinateAdapter.coordinateAdapter(ibData.getLatitude(), ibData.getLongitude(), ibData.getDistance()));
		
		
		return ibData;
	}
	
	
	/**
	 * elementToDouble Transform the String that is a number to a double.
	 * The number e.g. "3.000,13" is transformed to "3000.13".
	 * 
	 * @param s is the number as a String.
	 * @return double returns the number without the dots and changed the comma to a dot.
	 * @throws ParseException throws a exception when the String can't be parsed.
	 */
	private static double elementToDouble(String s) throws ParseException{
		String tmp = s;
		Number numTmp = df.parse(tmp); //Here the dots and the comma are transformed
		double num = numTmp.doubleValue(); //Save the value as a double
		return num;
	}
	
	/**
	 * Remove the "– Wikipedia" from the Webpage title.
	 * 
	 * @param s is the full title of the page
	 * @return a String without the "– Wikipedia" tag.
	 */
	private static String cleanTitle(String s){
		Pattern tag = Pattern.compile("– Wikipedia");
        Matcher mtag = tag.matcher(s);
        s = mtag.replaceAll("");
        return s;	
	}
	
}
