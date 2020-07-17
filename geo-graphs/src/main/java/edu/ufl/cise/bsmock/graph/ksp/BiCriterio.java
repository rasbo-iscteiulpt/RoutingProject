package edu.ufl.cise.bsmock.graph.ksp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ufl.cise.bsmock.graph.Edge;
import edu.ufl.cise.bsmock.graph.Graph;
import edu.ufl.cise.bsmock.graph.ksp.test.TestYen;
import edu.ufl.cise.bsmock.graph.util.PathUtil;

public class BiCriterio {

	private Yen yen_a;
	private Yen yen_b;
	private Graph graph_a;
	private Graph graph_b;

	private Double aGreat;
	private Double bGreat;
	private int k = 20;
	private List<PathUtil> best_paths;
	
	private boolean bonificated;

	public BiCriterio(String file_criterio_a, String file_criterio_b) {

		yen_a = new Yen();
		yen_b = new Yen();

		graph_a = new Graph(file_criterio_a);
		graph_b = new Graph(file_criterio_b);

	}

	public void start(String node_source, String node_target) {
		System.out.println("Starting bicriteria...");
		long timeStart = System.currentTimeMillis();

		List<PathUtil> list_ksp_a = yen_a.ksp(graph_a, node_source, node_target, k);
		list_ksp_a = apply_bonus(list_ksp_a);
		PathUtil a_path_great = list_ksp_a.get(0);
		System.out.println("melhor path a: " + a_path_great);
		aGreat = list_ksp_a.get(0).getTotalCost();

		List<PathUtil> list_ksp_b = yen_b.ksp(graph_b, node_source, node_target, 1);
		PathUtil b_path_great = list_ksp_b.get(0);
		System.out.println("melhor path b: " + b_path_great);
		bGreat = b_path_great.getTotalCost();

		// valores de cada criteria no melhor caminho da criteria oposto
		double fa_pb = f(graph_a, b_path_great, true);
		double fb_pa = f(graph_b, a_path_great, false);

		System.out.println("fa_pb: " + fa_pb);
		System.out.println("fb_pa: " + fb_pa);

		double Ma = aGreat;
		double mb = fb_pa;
		System.out.println("Ma: " + Ma);
		System.out.println("mb: " + mb);

		List<PathUtil> potencial_paths = new ArrayList<PathUtil>();
		List<PathUtil> efficient_paths = new ArrayList<PathUtil>();

		List<PathUtil> paths = list_ksp_a;
		System.out.println("Number of paths: " + paths.size());

		for (int i = 0; i < paths.size(); i++) {

			System.out.println("path nr: " + i + " Path p: " + paths.get(i));

			Double fa_p = f(graph_a, paths.get(i), true);
			System.out.println("fa_p: " + fa_p);
			Double fb_p = f(graph_b, paths.get(i), false);
			System.out.println("fb_p: " + fb_p);

			if (!(fa_p <= fa_pb)) {
				System.out.println("!fa_p <= fa_pb -> stop");
				break;
			} else {
				if (fa_p == Ma) {
					System.out.println("fa_p = Ma");
					if (fb_p == mb) {
						System.out.println("fb_p = mb");
						potencial_paths.add(paths.get(i));
						System.out.println("p adicionado aos paths potenciais");
					} else {
						if (fb_p < mb) {
							System.out.println("fb_p < mb");
							potencial_paths = new ArrayList<PathUtil>();
							System.out.println("potencias reseted");
							potencial_paths.add(paths.get(i));
							System.out.println("p adicionado aos paths potenciais");
							mb = fb_p;
						}
					}
				} else {

					if (fb_p < mb) {
						System.out.println("fb_p < mb");
						// efficient_paths.add(paths.get(i));
						efficient_paths.addAll(potencial_paths);
						potencial_paths = new ArrayList<PathUtil>();
						System.out.println("potencial paths adicionados aos paths eficientes");
						mb = fb_p;
						// Ma = fa_p;
						System.out.println("mb = fb_p = " + mb);
						// System.out.println("Ma = fa_p = " + Ma);
					}
				}
			}
		}

		System.out.println("best paths");
		best_paths = efficient_paths;
		for (int i = 0; i < efficient_paths.size(); i++) {
			System.out.println(efficient_paths.get(i));
		}

		long timeFinish = System.currentTimeMillis();

		System.out.println("Operation took " + (timeFinish - timeStart) / 1000.0 + " seconds.");
	}

	public void start2(String node_source, String node_target) {
		System.out.println("Starting bicriteria...");
		long timeStart = System.currentTimeMillis();

		List<PathUtil> list_ksp_a = yen_a.ksp(graph_a, node_source, node_target, k);
		//list_ksp_a = apply_bonus(list_ksp_a);
		PathUtil a_path_great = list_ksp_a.get(0);
		System.out.println("melhor path a: " + a_path_great);
		aGreat = list_ksp_a.get(0).getTotalCost();

		List<PathUtil> list_ksp_b = yen_b.ksp(graph_b, node_source, node_target, 1);
		PathUtil b_path_great = list_ksp_b.get(0);
		System.out.println("melhor path b: " + b_path_great);
		bGreat = b_path_great.getTotalCost();

		// valores de cada criteria no melhor caminho da criteria oposto
		double fa_pb = f(graph_a, b_path_great, false);
		double fb_pa = f(graph_b, a_path_great, false);

		System.out.println("fa_pb: " + fa_pb);
		System.out.println("fb_pa: " + fb_pa);

		double Ma = aGreat;
		double mb = fb_pa;
		System.out.println("Ma: " + Ma);
		System.out.println("mb: " + mb);

		List<PathUtil> potencial_paths = new ArrayList<PathUtil>();
		List<PathUtil> efficient_paths = new ArrayList<PathUtil>();

		List<PathUtil> paths = list_ksp_a;
		System.out.println("Number of paths: " + paths.size());

		for (int i = 0; i < paths.size(); i++) {

			System.out.println("path nr: " + i + " Path p: " + paths.get(i));

			Double fa_p = f(graph_a, paths.get(i), false);
			System.out.println("fa_p: " + fa_p);
			Double fb_p = f(graph_b, paths.get(i), false);
			System.out.println("fb_p: " + fb_p);

			if (!(fa_p <= fa_pb)) {
				System.out.println("!fa_p <= fa_pb -> stop");
				break;
			} else {
				if (fa_p == Ma && fb_p == mb) {
					System.out.println("fa_p = Ma");
					System.out.println("fb_p = mb");
					potencial_paths.add(paths.get(i));
					System.out.println("p adicionado aos paths potenciais");
				} else {
					if (fb_p < mb) {
						System.out.println("fb_p < mb");
						potencial_paths.add(paths.get(i));
						System.out.println("p adicionado aos paths potenciais");
						mb = fb_p;
						System.out.println("mb = fb_p = " + mb);
					}
				}
			}
		}
		System.out.println("best paths");
		best_paths = potencial_paths;

		for (int i = 0; i < best_paths.size(); i++) {
			System.out.println("fa: " + best_paths.get(i).getTotalCost());
			System.out.println("fb: " + f(graph_b, best_paths.get(i), false));
			System.out.println(best_paths.get(i));
		}

		long timeFinish = System.currentTimeMillis();

		System.out.println("Operation took " + (timeFinish - timeStart) / 1000.0 + " seconds.");
	}

	public List<PathUtil> bestPaths() {
		return best_paths;
	}

	// calcula f(variavel) no p

	private double f(Graph graph, PathUtil p, boolean bonus) {
		double cost = -1;
		PathUtil new_path = new PathUtil();

		for (int i = 0; i < p.getEdges().size(); i++) {
			String node_source = p.getEdges().get(i).getFromNode();
			String node_target = p.getEdges().get(i).getToNode();

			double c = graph.getEdgeWeight(node_source, node_target);
			Edge edge = new Edge(node_source, node_target, c);
			new_path.add(edge);
		}
		if (bonus) {
			new_path = bonificate(new_path);
			System.out.println("oi");
		}
		cost = new_path.getTotalCost();

		return cost;
	}

	private PathUtil bonificate(PathUtil p) {
		int nPOI = p.nPOIs();
		if (nPOI == 1 || nPOI == 5) {
			double cost = p.getTotalCost();
			double new_cost = cost * 0.95;
			p.setTotalCost(new_cost);
		} else {
			if (nPOI == 2 || nPOI == 4) {
				double cost = p.getTotalCost();
				double new_cost = cost * 0.90;
				p.setTotalCost(new_cost);
			} else {
				if (nPOI == 3) {
					double cost = p.getTotalCost();
					double new_cost = cost * 0.90;
					p.setTotalCost(new_cost);
				}
			}
		}
		return p;
	}

	private List<PathUtil> apply_bonus(List<PathUtil> list) {
		List<PathUtil> new_list = new ArrayList<PathUtil>();
		for (PathUtil p : list) {
			PathUtil aux = bonificate(p);
			new_list.add(aux);
		}
		Collections.sort(new_list);
		return new_list;
	}

	public static void main(String[] args) {
		String file_criterio_a = "C:\\Users\\Rúben Beirão\\Desktop\\wsTESE\\wsTESE\\geo-graphs\\target\\classes\\edu\\ufl\\cise\\bsmock\\graph\\ksp\\test\\test_a.txt";
		String file_criterio_b = "C:\\Users\\Rúben Beirão\\Desktop\\wsTESE\\wsTESE\\geo-graphs\\target\\classes\\edu\\ufl\\cise\\bsmock\\graph\\ksp\\test\\test_b.txt";
		String node_source = "1";
		String node_target = "5";
		BiCriterio bicriterio = new BiCriterio(file_criterio_a, file_criterio_b);
		bicriterio.start2(node_source, node_target);
	}

}
