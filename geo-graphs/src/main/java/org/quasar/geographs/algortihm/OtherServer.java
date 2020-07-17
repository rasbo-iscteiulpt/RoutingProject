package org.quasar.geographs.algortihm;

import java.util.List;

import com.graphhopper.routing.AlternativeRoute;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.ShortestWeighting;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphHopperStorage;

/**
 * http://javadox.com/com.graphhopper/graphhopper/0.7.0-RC2/com/graphhopper/routing/AlternativeRoute.html
 * https://discuss.graphhopper.com/t/alternative-routes/424/12
 * @author Rúben Beirão
 *
 */

public class OtherServer {

	public OtherServer() {
		FlagEncoder encoder = new CarFlagEncoder();
		EncodingManager em = new EncodingManager(encoder);
		GraphBuilder gb = new GraphBuilder(em).setLocation("graphhopper").setStore(true);
		GraphHopperStorage graph = gb.create();

		graph.edge(1, 2, 10, false);
		graph.edge(2, 3, 10, false);
		graph.edge(1, 3, 20, false);

		graph.flush();
		graph = gb.load();

		AlternativeRoute alternativeRoute = new AlternativeRoute(graph, new ShortestWeighting(encoder),
				TraversalMode.NODE_BASED);

		// trying to tweak the parameters here
		// alternativeRoute.setMaxPaths(5);
		// alternativeRoute.setMaxExplorationFactor(5);
		// alternativeRoute.setMaxWeightFactor(5);
		// alternativeRoute.setMaxShareFactor(1);
		// alternativeRoute.setMinPlateauFactor(0);

		List<Path> paths = alternativeRoute.calcPaths(1, 3);
		for (Path path : paths) {
			System.out.println(path.toDetailsString());
			System.out.println(path.calcNodes());
			System.out.println(path.calcEdges());
		}
	}
}
