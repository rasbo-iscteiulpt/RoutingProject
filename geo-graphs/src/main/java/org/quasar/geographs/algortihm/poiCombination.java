package org.quasar.geographs.algortihm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.quasar.geographs.algortihm.Schedule.Days;

public class poiCombination {
	// a is the original array
	// k is the number of elements in each permutation
	public static LinkedList<LinkedList<PointOfInterest>> choose(LinkedList<PointOfInterest> original, int nElementsInPermutation) {
		LinkedList<LinkedList<PointOfInterest>> allPermutations = new LinkedList<LinkedList<PointOfInterest>>();
		enumerate(original, original.size(), nElementsInPermutation, allPermutations);
		return allPermutations;
	}

	// a is the original array
	// n is the array size
	// k is the number of elements in each permutation
	// allPermutations is all different permutations
	private static void enumerate(LinkedList<PointOfInterest> original, int originalSize, int nElementsInPermutation, LinkedList<LinkedList<PointOfInterest>> allPermutations) {
		if (nElementsInPermutation == 0) {
			LinkedList<PointOfInterest> singlePermutation = new LinkedList<PointOfInterest>();
			for (int i = originalSize; i < original.size(); i++) {
				singlePermutation.add(original.get(i));
			}
			allPermutations.add(singlePermutation);
			return;
		}

		for (int i = 0; i < originalSize; i++) {
			swap(original, i, originalSize - 1);
			enumerate(original, originalSize - 1, nElementsInPermutation - 1, allPermutations);
			swap(original, i, originalSize - 1);
		}
	}

	// helper function that swaps a.get(i) and a.get(j)
	public static void swap(LinkedList<PointOfInterest> a, int i, int j) {
		PointOfInterest temp = a.get(i);
		a.set(i, a.get(j));
		a.set(j, temp);
	}

	// sample client
	public static void main(String[] args) {

		// n is the end item of the array.
		// if n = 5, the array is [0, 1, 2, 3, 4, 5]
		// k is the number of elements of each permutation.		
		int n = 5;
		int k = 5;

		//Castelo de S. Jorge
		Schedule schedulePoi1 = new Schedule(10, 20, new ArrayList<Days> (Arrays.asList(Days.Monday, Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday, Days.Sunday)), 10);
		PointOfInterest poi1 = new PointOfInterest(1, -9.1334762, 38.7139092, new LinkedList<Schedule>(Arrays.asList(schedulePoi1)), 45);
		
		//Museu Arqueológico do Carmo
		Schedule schedulePoi2 = new Schedule(10, 19, new ArrayList<Days> (Arrays.asList(Days.Monday, Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday)), 5);
		PointOfInterest poi2 = new PointOfInterest(2, -9.14063627, 38.71190513, new LinkedList<Schedule>(Arrays.asList(schedulePoi2)), 70);
		
		//Elevador de Santa Justa
		Schedule schedulePoi3 = new Schedule(7.5, 23, new ArrayList<Days> (Arrays.asList(Days.Monday, Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday, Days.Sunday)), 5.15);
		PointOfInterest poi3 = new PointOfInterest(3, -9.1394235, 38.71212908, new LinkedList<Schedule>(Arrays.asList(schedulePoi3)), 30);

		//MUSEU NACIONAL DE ARTE CONTEMPORÂNEA DO CHIADO
		Schedule schedulePoi4 = new Schedule(10, 18, new ArrayList<Days> (Arrays.asList(Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday, Days.Sunday)), 4.5);
		PointOfInterest poi4 = new PointOfInterest(4, -9.14102261, 38.70968009, new LinkedList<Schedule>(Arrays.asList(schedulePoi4)), 70);
		
		//Sé de Lisboa
		Schedule schedulePoi5 = new Schedule(9, 19, new ArrayList<Days> (Arrays.asList(Days.Monday, Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday)), 0);
		Schedule schedule2Poi5 = new Schedule(9, 20, new ArrayList<Days> (Arrays.asList(Days.Sunday)), 0);
		PointOfInterest poi5 = new PointOfInterest(5, -9.13340813, 38.70980306, new LinkedList<Schedule>(Arrays.asList(schedulePoi5, schedule2Poi5)), 30);

		// create original array
		LinkedList<PointOfInterest> result = new LinkedList<PointOfInterest>();
		result.add(poi1);
		result.add(poi2);
		result.add(poi3);
		result.add(poi4);
		result.add(poi5);

		LinkedList<PointOfInterest> a = new LinkedList<>();
		for (int i = 0; i < n; i++) {
			a.add(result.get(i));
		}

		System.out.println(choose(a, k));
		System.out.println("Existem " + choose(a, k).size() + " alternativas");
	}
}
