package route;

/*• Brigitta feladat. A Petra feladat  megoldása Java-ban!
Nem hegylakó feladat, 90 pont.
        Címkék: City, Map, routing.

        Programm kap 2 kordináta párt , hozzájuk legközelebb lévő nodeokhoz routeingot végez .

http://wiki.openstreetmap.org/wiki/OSM_tags_for_routing/Telenav

Kordináták :
http://www.openstreetmap.org/#map=15/47.54531/21.64166

 "47.54531 21.64166 47.52783 21.61440"  Főnix csarmok /Segner tér :
 "47.54317 21.63922 47.56063 21.62926"  Lovarda /Klinika

*/
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;

public class routing {
	public static void main(String [] args) throws IOException{
		if(args.length != 4){
			System.out.println("Nincs elég koordináta! -Dexec.args=\"Lat1 Lon1 Lat2 Lon2\"");
		}
		GraphHopper hopper = new GraphHopper().forServer();
		hopper.setInMemory(true);
		hopper.setOSMFile("map.osm");
		// where to store graphhopper files
		hopper.setGraphHopperLocation("YOlo2");
		hopper.setEncodingManager(new EncodingManager("foot"));
        // 	hopper.setEncodingManager(new EncodingManager("foot","car"));

		// now this can take minutes if it imports or a few seconds for loading
		// of course this is dependent on the area you import
		hopper.importOrLoad();

		// simple configuration of the request object, see the GraphHopperServlet classs for more possibilities.
		GHRequest req = new GHRequest(Double.parseDouble(args[0]) ,Double.parseDouble(args[1]) 
				, Double.parseDouble(args[2]) ,Double.parseDouble(args[3]) ).
		    setWeighting("fastest").
		    setVehicle("foot");
		GHResponse rsp = hopper.route(req);

		// first check for errors
		if(rsp.hasErrors()) {
		System.out.println("Hiba!");		   
		return;
		}

		// route was found? e.g. if disconnected areas (like island) 
		// no route can ever be found
		if(!rsp.isFound()) {
		System.out.println("Nincs út!");		   
		return;
		}

		// points, distance in meters and time in millis of the full path
		PointList pointList = rsp.getPoints();
		double distance = rsp.getDistance();
		long millis = rsp.getMillis();
		//TranslationMap trMap = new TranslationMap();

		// get the turn instructions for the path
		InstructionList il = rsp.getInstructions();
		//Translation tr = trMap.getWithFallBack(Locale.US);

		// or get the result as gpx entries:
		List<GPXEntry> list = il.createGPXList();
		
		FileWriter fw = new FileWriter("ki");
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (GPXEntry gpxEntry : list) {
			System.out.println(gpxEntry.lat+"\t"+gpxEntry.lon);
			bw.write(gpxEntry.lat+"\t"+gpxEntry.lon+"\n");
		}
		bw.close();
		fw.close();

		
	}
}
