/**
 * 
 * Calculate a new precision of WGS84 Coordinates. Uses the first geodetic problem to calculate a boundingbox
 * and cut the coordinate to a proper precision.
 * 
 * @author Sven Mattauch 
 */

import java.text.DecimalFormat;

public class CoordinateAdapter {
	// WGS84 constants
	static private final double c = 6399593.6258; // meters

	static private final double ep2 = 6.73949674228 * Math.pow(10, -3);
	static private final double rho = 180 / Math.PI;

	/**
	 * 
	 * This Method needs the Coordinate of the midpoint of a area and a distance
	 * to the north-east point. It solve the first geodetic problem to get the
	 * noth-east point and the south-west point of the boundingbox. With that it
	 * cuts digits from the midpoint to get a coordinate that describes the
	 * boundingbox and not only the midpoint. It uses the
	 * firstGeodeticProblem()-Method and the decreaseCoordinate()-Method.
	 * 
	 * @param latitude
	 *            the latitude of the midpoint of the area
	 * @param longitude
	 *            the longitude of the midpoint of the area
	 * @param distance
	 *            the distance from the midpoint to the north-east point of the
	 *            boundingbox area.
	 * @return returns the new calculated coordinate for the area as a String
	 */
	static public String coordinateAdapter(double latitude, double longitude,
			double distance) {
		// Use the firstGeodeticProblem-function to calculate the cornerpoints
		// of the Boundingbox
		// North-east point of the BB
		double[] NE = firstGeodeticProblem(latitude, longitude, distance, 45);
		// South-west point of the BB
		double[] SW = firstGeodeticProblem(latitude, longitude, distance, 225);

		// Use the decreaseCoordinte-function to set the result String.
		String coordinates = decreaseCoordinate(latitude, NE[0], SW[0]);
		coordinates += "N, " + decreaseCoordinate(longitude, NE[1], SW[1])
				+ " E";

		return coordinates;
	}

	/**
	 * This method solve the first geodetic problem and returns the latitude and
	 * the longitude in a double array. It does not calculate the azimut from
	 * the new point back to the old.
	 * 
	 * @param latitude
	 *            origin latitude
	 * @param longitude
	 *            origin longitude
	 * @param distance
	 *            distance to the new point
	 * @param azimut
	 *            azimut to the new point
	 * @return returns a double-array with the length of two. The first digit is
	 *         the latitude and the secound the longitude.
	 */
	static public double[] firstGeodeticProblem(double latitude,
			double longitude, double distance, double azimut) {

		// origin point
		double lat = latitude;
		double lng = longitude;

		// constans in dependent on the origin point
		double etap2 = ep2 * cosp2(lat);
		double bigVc = 1 + etap2;
		double t = tg(lat);
		double tp2 = Math.pow(tg(lat), 2);
		double lc = Math.cos(Math.toRadians(lat));

		// Series sequence for the latitude
		double b1 = rho / c * Math.pow(bigVc, 3);
		double b2 = (-1) * rho / (2 * Math.pow(c, 2)) * Math.pow(bigVc, 4) * t;
		double b3 = (-1) * (3 * rho) / (2 * Math.pow(c, 2))
				* Math.pow(bigVc, 4) * etap2 * t;
		double b4 = (-1) * rho / (6 * Math.pow(c, 3)) * Math.pow(bigVc, 5)
				* ((1 + 3 * tp2) + etap2 * (1 - 9 * tp2));
		double b5 = rho / (2 * Math.pow(c, 3)) * Math.pow(bigVc, 5) * etap2
				* (-1 + tp2);
		double b6 = rho / (24 * Math.pow(c, 4)) * Math.pow(bigVc, 6) * t
				* ((1 + 3 * tp2) + etap2 * (t - 9 * tp2));
		double b7 = (-1) * rho / (12 * Math.pow(c, 4)) * Math.pow(bigVc, 6) * t
				* ((4 + 6 * tp2) - etap2 * (13 + 9 * tp2));
		double b8 = rho / (120 * Math.pow(c, 5)) * Math.pow(bigVc, 7)
				* (1 + 30 * tp2 + 45 * Math.pow(t, 4));
		double b9 = (-1) * rho / (30 * Math.pow(c, 5)) * Math.pow(bigVc, 7)
				* (2 + 15 * tp2 + 15 * Math.pow(t, 4));

		// Series sequence for the longitude
		double l1 = rho / c * bigVc / lc;
		double l2 = rho / Math.pow(c, 2) * Math.pow(bigVc, 2) / lc * t;
		double l3 = (-1) * rho / (3 * Math.pow(c, 3)) * Math.pow(bigVc, 3) / lc
				* tp2;
		double l4 = rho / (3 * Math.pow(c, 3)) * Math.pow(bigVc, 3) / lc
				* (1 + 3 * tp2 + etap2);
		double l5 = (-1) * rho / (3 * Math.pow(c, 4)) * Math.pow(bigVc, 4) / lc
				* t * (1 + 3 * tp2);
		double l6 = rho / (3 * Math.pow(c, 4)) * Math.pow(bigVc, 4) / lc * t
				* (2 + 3 * tp2);
		double l7 = rho / (15 * Math.pow(c, 5)) * Math.pow(bigVc, 5) / lc * t
				* (1 + 3 * tp2);
		double l8 = (-1) * rho / (15 * Math.pow(c, 5)) * Math.pow(bigVc, 5)
				/ lc * (1 + 20 * tp2 + 30 * Math.pow(t, 4));
		double l9 = rho / (15 * Math.pow(c, 5)) * Math.pow(bigVc, 5) / lc
				* (2 + 15 * tp2 + 15 * Math.pow(t, 4));

		// proxy values
		double u = distance * Math.cos(Math.toRadians(azimut));
		double v = distance * Math.sin(Math.toRadians(azimut));

		// Calculation of the new latitude
		double lat2 = lat + b1 * u + b2 * Math.pow(v, 2) + b3 * Math.pow(u, 2)
				+ b4 * u * Math.pow(v, 2) + b5 * Math.pow(u, 3) + b6
				* Math.pow(v, 4) + b7 * Math.pow(u, 2) * Math.pow(v, 2) + b8
				* u * Math.pow(v, 4) + b9 * Math.pow(u, 3) * Math.pow(v, 2);

		// Calculation for the new longitude
		double lng2 = lng + l1 * v + l2 * u * v + l3 * Math.pow(v, 3) + l4
				* Math.pow(u, 2) * v + l5 * u * Math.pow(v, 3) + l6
				* Math.pow(u, 3) * v + l7 * Math.pow(v, 5) + l8
				* Math.pow(u, 2) * Math.pow(v, 3) + l9 * Math.pow(u, 4) * v;

		// Save the new values and return it as a array
		double[] result = { lat2, lng2 };
		return result;

	}

	/**
	 * Help Method for the firstGeodeticProblem Method. Calculate the Math.tan
	 * with degrees.
	 * 
	 * @param b
	 *            the angle in degree
	 * @return the Math.tan for b
	 */
	static private double tg(double b) {
		return Math.tan(Math.toRadians(b));
	}

	/**
	 * Help Method for the firstGeodeticProblem Method. Calculate the Math.cos²
	 * with degrees.
	 * 
	 * @param b
	 *            the angle in degree
	 * @return the Math.cos² for b
	 */
	static private double cosp2(double b) {
		return Math.pow(Math.cos(Math.toRadians(b)), 2);
	}

	/**
	 * Decrease a given Coordinate that is between the two others. Returns the
	 * lowest possible precision of the coordinate that still fit in the box.
	 * 
	 * @param x
	 *            coordinate in the box
	 * @param xNE
	 *            the bigger value
	 * @param xSW
	 *            the lower value
	 * @return return the new value as a String.
	 */
	static private String decreaseCoordinate(double x, double xNE, double xSW) {
		// proxy values
		String tmp = Double.toString(x);
		char[] number = tmp.toCharArray();
		int tmpNumber = (int) x;
		tmp = Integer.toString(tmpNumber) + ".";
		double result = tmpNumber;
		String coordinate;

		// i:= index value
		int i = 0;
		// k:= proxy value
		int k;

		// set i on the first decimal place
		for (k = 0; k < number.length; k++) {
			if (number[k] == '.') {
				i = k + 1;
				break;
			}
		}

		// another proxy value
		double numCheck = 0;

		// Break flag.
		boolean flag = true;
		// check if the the decimal digit is in the value range. if it is, save
		// the value, else check the next decimal digit.
		while (flag) {
			tmp += number[i];
			numCheck = Double.parseDouble(tmp);

			// If the lower value is in the range, then check the upper section
			if (numCheck < xNE && numCheck > xSW) {
				String tmp9 = "0.";
				double add;
				for (int j = 0; j < i - (k + 1); j++) {
					tmp9 += "0";
				}
				tmp9 += 1;
				add = Double.parseDouble(tmp9);
				numCheck += add;

				// Check the upper value
				if (numCheck < xNE && numCheck > xSW) {
					result = Double.parseDouble(tmp);
					flag = false;
					break;
				}
			}
			numCheck = 0;
			i++;

			// check whether there is another decimal value
			if (i == number.length) {
				result = Double.parseDouble(tmp);
				i--;
				break;
			}
		}

		// If the laste value is a zero, then make sure that it isnt cut
		String format = "#.";
		if (number[i] == '0') {
			for (int j = 0; j < i - k; j++) {
				format += 0;
			}
			DecimalFormat df = new DecimalFormat(format);
			coordinate = (df.format(result));
			return coordinate;
		}
		// If the last value isnt a zero just return the result
		coordinate = Double.toString(result);
		return coordinate;
	}

}
