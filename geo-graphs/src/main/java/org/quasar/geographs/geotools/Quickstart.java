package org.quasar.geographs.geotools;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.MapContent;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;

/**
 * Prompts the user for a shapefile and displays the contents on the screen in a
 * map frame.
 *
 * <p>
 * This is the GeoTools Quickstart application used in documentationa and
 * tutorials. *
 */
public class Quickstart
{

	/**
	 * GeoTools Quickstart demo application. Prompts the user for a shapefile and
	 * displays its contents on the screen in a map frame
	 */
	public static void main(String[] args) throws Exception
	{
		// Create a map content and add our shapefile to it
		MapContent map = new MapContent();
		map.setTitle("ShapeFile Map Test");

		// // display a data store file chooser dialog for shapefiles
		// File file = JFileDataStoreChooser.showOpenFile("shp", null);
		// if (file == null)
		// {
		// return;
		// }

		Path dir = Paths.get("./src/main/resources/CentralLisbon-shp/shape/");
		try (DirectoryStream<Path> paths = Files.newDirectoryStream(dir, "*.shp"))
		{
			for (Path aPath : paths)
				map.addLayer(createLayer(aPath.toFile()));
		}

		JMapFrame.showMap(map);
	}

	
	private static Layer createLayer(File file) throws IOException
	{
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();

		Style style = SLD.createSimpleStyle(featureSource.getSchema());
		return new FeatureLayer(featureSource, style);
	}

}
