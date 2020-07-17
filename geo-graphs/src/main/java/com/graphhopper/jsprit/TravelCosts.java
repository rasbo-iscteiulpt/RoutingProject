package com.graphhopper.jsprit;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.AbstractForwardVehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.driver.Driver;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.util.DistanceUnit;

public class TravelCosts extends AbstractForwardVehicleRoutingTransportCosts {

	private double speed = 1.;
	private double detour = 1.;

	private DistanceUnit distanceUnit = DistanceUnit.Kilometer;

	public TravelCosts() {
		super();
	}

	public TravelCosts(DistanceUnit distanceUnit) {
		super();
		this.distanceUnit = distanceUnit;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Sets the detour factor.
	 * <p>
	 * The distance is calculated by the great circle distance * detour factor.
	 * </p>
	 *
	 * @param detour
	 */
	public void setDetour(double detour) {
		this.detour = detour;
	}
	
	private double calculateDistance(Location fromLocation, Location toLocation) {
        Coordinate from = null;
        Coordinate to = null;
        if (fromLocation.getCoordinate() != null && toLocation.getCoordinate() != null) {
            from = fromLocation.getCoordinate();
            to = toLocation.getCoordinate();
        }
        if (from == null || to == null) throw new NullPointerException("either from or to location is null");
        return DistanceCalculator.calculateDistance(from, to, distanceUnit) * detour;
    }

	@Override
	public double getDistance(Location from, Location to, double departureTime, Vehicle vehicle) {
		double distance;
		try {
			distance = calculateDistance(from, to);
		} catch (NullPointerException e) {
			throw new NullPointerException(
					"cannot calculate euclidean distance. coordinates are missing. either add coordinates or use another transport-cost-calculator.");
		}
		double costs = distance;
		if (vehicle != null) {
			if (vehicle.getType() != null) {
				costs = distance * vehicle.getType().getVehicleCostParams().perDistanceUnit;
			}
		}
		return costs;
	}

	@Override
	public double getTransportTime(Location from, Location to, double departureTime, Driver driver, Vehicle vehicle) {
        return calculateDistance(from, to) / speed;
	}

	@Override
	public double getTransportCost(Location from, Location to, double departureTime, Driver driver, Vehicle vehicle) {
        return calculateDistance(from, to);
	}

}
