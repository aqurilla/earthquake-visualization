package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Nitin Suresh
 * Date: July 11, 2019
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			// map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleTerrainProvider());
			map = new UnfoldingMap(this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			// earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    //Call createMarker to create a new SimplePointMarker for each PointFeature in 
	    // earthquakes.  Then add each new SimplePointMarker to the 
	    // List markers
	    for (PointFeature eq : earthquakes) {
	    	SimplePointMarker eqMarker = createMarker(eq);
	    	markers.add(eqMarker);
	    }
	    // Add the markers to the map so that they are displayed
	    map.addMarkers(markers);
	}
		
	/* createMarker: Method that takes in an earthquake 
	 * feature and returns a SimplePointMarker for that earthquake
	 *  
	 * 
	 * Add code to this method so that it adds the proper 
	 * styling to each marker based on the magnitude of the earthquake.  
	*/
	private SimplePointMarker createMarker(PointFeature feature)
	{  
		// Create a new SimplePointMarker at the location given by the PointFeature
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());
		
		// Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.  
	    int yellow = color(255, 255, 0);
	    int blue = color(0, 0, 255);
	    int red = color(255, 0, 0);
		
		// Style the marker's size and color based on magnitude
	    
	    if (mag < THRESHOLD_LIGHT) {
	    	marker.setColor(blue);
	    	marker.setRadius(5);
	    } else if (mag >= THRESHOLD_LIGHT && mag < THRESHOLD_MODERATE) {
	    	marker.setColor(yellow);
	    	marker.setRadius(10);
	    } else {
	    	marker.setColor(red);
	    	marker.setRadius(15);
	    }
	    
	    // Finally return the marker
	    return marker;
	}
	
	public void draw() {
	    background(200, 200, 200);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
		fill(0, 150, 100);
		rect(25, 100, 150, 250, 7);
		fill(0);
		textSize(16);
		text("Earthquake Key:", 35, 125);
		// Draw ellipses for different markers
		// blue
		fill(0, 0, 255);
		ellipse(50, 150, 5, 5);
		// yellow
		fill(255, 255, 0);
		ellipse(50, 200, 10, 10);
		// red
		fill(255, 0, 0);
		ellipse(50, 250, 15, 15);
		
		// Fill in corresponding text
		fill(255, 255, 255);
		textSize(14);
		text(" : Light", 60, 155);
		text(" : Moderate", 60, 205);
		text(" : Strong", 60, 255);
	}
}
