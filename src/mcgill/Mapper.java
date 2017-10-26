package mcgill;
//
//import java.awt.Color;
//import java.io.File;
//import java.io.IOException;
//
//import org.geotools.data.FileDataStore;
//import org.geotools.data.FileDataStoreFinder;
//import org.geotools.data.simple.SimpleFeatureSource;
//import org.geotools.feature.DefaultFeatureCollection;
//import org.geotools.feature.simple.SimpleFeatureBuilder;
//import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
//import org.geotools.geometry.jts.JTSFactoryFinder;
//import org.geotools.map.FeatureLayer;
//import org.geotools.map.Layer;
//import org.geotools.map.MapContent;
//import org.geotools.referencing.crs.DefaultGeographicCRS;
//import org.geotools.styling.SLD;
//import org.geotools.styling.Style;
//import org.geotools.swing.JMapFrame;
//import org.geotools.swing.data.JFileDataStoreChooser;
//import org.opengis.feature.simple.SimpleFeature;
//import org.opengis.feature.simple.SimpleFeatureType;
//
//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.Point;
//
////import java.io.File;
////import java.io.IOException;
////
////import org.geotools.data.FileDataStore;
////import org.geotools.data.FileDataStoreFinder;
////import org.geotools.data.simple.SimpleFeatureSource;
////import org.geotools.map.FeatureLayer;
////import org.geotools.map.Layer;
////import org.geotools.map.MapContent;
////import org.geotools.styling.SLD;
////import org.geotools.styling.Style;
////import org.geotools.swing.JMapFrame;
////import org.geotools.swing.data.JFileDataStoreChooser;
//
//public class Mapper  {
//	
//	public Mapper(){
//		
//	}
//	
//	static void addPoint(double lat, double lng) throws IOException{
//
//		File file = JFileDataStoreChooser.showOpenFile("shp", null);
//		if (file == null){ return ; }
//		FileDataStore store = FileDataStoreFinder.getDataStore(file);
//		SimpleFeatureSource featureSource = store.getFeatureSource();
////		@SuppressWarnings("deprecation")
////		MapContext map = new DefaultMapContext();
//		MapContent map = new MapContent();
//		map.setTitle("Fog Locations");
//		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
//		b.setName("MyFeatureType");
//		b.setCRS(DefaultGeographicCRS.WGS84);
//		b.add("location", Point.class);
//		// Building the type
//		final SimpleFeatureType TYPE = b.buildFeatureType();
//		
//		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
//		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
//		Point point = geometryFactory.createPoint(new Coordinate(-155.27, 67.3623));
//		featureBuilder.add(point);
//		SimpleFeature feature = featureBuilder.buildFeature(null);
//		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", TYPE);
//		featureCollection.add(feature);
//		Style style = SLD.createSimpleStyle(TYPE, Color.red);
//		
//		Layer layer = new FeatureLayer(featureCollection, style);
////		return layer;
//		
//		//Layer player = addPoint(-155.27, 67.3623);
//		map.addLayer(layer);
//		JMapFrame.showMap(map);
//		
////		 // display a data store file chooser dialog for shapefiles
////        File file = JFileDataStoreChooser.showOpenFile("shp", null);
////        if (file == null) {
////            return;
////        }
////
////        FileDataStore store = FileDataStoreFinder.getDataStore(file);
////        SimpleFeatureSource featureSource = store.getFeatureSource();
////
////        // Create a map content and add our shapefile to it
////        MapContent map = new MapContent();
////        map.setTitle("Quickstart");
////        
////        Style style = SLD.createSimpleStyle(featureSource.getSchema());
////        Layer layer = new FeatureLayer(featureSource, style);
////        map.addLayer(layer);
////
////        // Now display the map
////        JMapFrame.showMap(map);
//		
//		
//	}
//
//}
//
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
public class Mapper{
	


//	public class Main {

	   private static int count = 0;

	   public static void main(String[] args) throws IOException {
	        System.out.println("Hello World!");

	       // give the path to the folder where the submissions are
	        try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\Olamilekan\\Desktop\\PhD2017\\Fall\\COMP206\\A2\\Ass2\\"))) {
	            paths.filter(Files::isRegularFile).forEach(path -> {
	                try {
	                    renameFile(path);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            });
	        }
	    }

	   private static void renameFile(Path path) throws IOException {
	        File file = path.toFile();

	       String fileName = file.getName();
	        int indexOfFirstName = fileName.indexOf(" - ");

	       if (indexOfFirstName != -1){
	            fileName = fileName.substring(indexOfFirstName + 3);
	        }


	       if (fileName.equals("index.html")){
	            fileName = "index_" + count + ".html";
	            count++;
	        }

	       System.out.println(fileName);
	        //Path to store the renamed file
	        File file2 = new File("C:\\Users\\Olamilekan\\Desktop\\PhD2017\\Fall\\COMP206\\A2\\New\\" + fileName);

	       if (file2.exists())
	            throw new java.io.IOException("file exists");

	       boolean success = file.renameTo(file2);
	        if (!success) {
	            System.out.println("ERROR..." + fileName);
	        }
	    }


//	}
}
