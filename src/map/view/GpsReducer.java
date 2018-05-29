package map.view;

import gpx2.GpxDecoder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.model.GpxPoint;

public class GpsReducer {
	
	private static float superelevation = 1.0f;
	public static double EARTH_RADIUS = 6371.0008;
	private static final double PRECISION = 1e-6;
	
	private static int getBestSize( int original ) {
		if ( original > 1000 ) {
			return 999;
		}
		else {
			return original;
		}
	}
	
	public static List<GpxPoint> shrink( final List<GpxPoint> points) {
		
		int numpoints = getBestSize(points.size());
		
		List<GpxPoint> resPoints = new ArrayList<GpxPoint>();
		//Collections.copy(resPoints, points);
		int id = 0;
		for (GpxPoint point : points) {
			point.setId(id++);
			resPoints.add(point);
		}
		
		
        // calculate weight of all points
        Map<GpxPoint, Double> weights = new HashMap<GpxPoint, Double>();
		//List<Double> wL = new ArrayList<Double>();
        
        for (int i = 0; i < resPoints.size(); i++) {  
         	weights.put(resPoints.get(i), weight(resPoints, i));
        	double weight = weight(resPoints, i);
        	//wL.add(weight);
        	resPoints.get(i).setWeight(weight);
        }
        
        final List<Double> wL = new ArrayList<Double>(weights.values());
        int ntotal = wL.size();
//        int numiter = 1;
        while (ntotal > numpoints) {
            // calculate minimum weight of points to keep
        	//System.out.println("ntotal " + ntotal);
            Collections.sort(wL);
            int mindx = ntotal - 1 - numpoints;
            int ntrkpt = ntotal;
            double minWeight = wL.get(mindx);

            for (int i = resPoints.size() - 2; i > 0; i--) {
            	//System.out.println("i: " + i);
              	GpxPoint point = resPoints.get(i);
              	
              	Object obj = weights.get(point);
              	if ( obj == null ) {
              		dump(weights);
              	}
               	double w = weights.get(point);
            	//double w = resPoints.get(i).getWeight();
            	
                // is weight below threshold?
                if (w <= minWeight) {

                    // Look for neighbour with lower weight
                    for (double w2 = weights.get(resPoints.get(i - 1)); w2 < w;) {
                	//for (double w2 = resPoints.get(i - 1).getWeight(); w2 < w;) {
                        i--;
                        w = w2;
                        //w2 = weights.get(resPoints.get(i - 1));
                        w2 = resPoints.get(i - 1).getWeight();
                    }
                    

                    // remove trkpt and check success
                    GpxPoint removed = resPoints.get(i);
                    System.out.println("Remove : " + removed.getId());
 
                    ntrkpt--;
                    if (ntrkpt == numpoints) {
                        break;
                    }
                    
                    // update weights
                    double dd = weights.remove(removed).doubleValue();
                    
                    assert (w == dd);
                    if (i < resPoints.size() - 2) {
                        weights.put(resPoints.get(i + 1),
                                area(resPoints.get(i + 1), resPoints.get(i + 2), resPoints.get(i - 1)));
                    }
                    if (i > 1) {
                        weights.put(resPoints.get(i - 1),
                                area(resPoints.get(i - 1), resPoints.get(i + 1), resPoints.get(i - 2)));
                        
  
                    }
                    
                    resPoints.remove(i);

                    // skip next point lest we assign a weight to the
                    // point we have just removed when updating weights
                    i--;
               }
            }
  
            wL.clear();
            wL.addAll(weights.values());
            assert (ntrkpt == wL.size());
            if (ntrkpt >= ntotal) {
                String message = "ntrkpt: " + ntrkpt + "\n";
                message += "ntotal: " + ntotal + "\n";
                message += "minWeight: " + minWeight + "\n";
                message = "Not converging. This is a bug.\n" + message;
                throw new Error(message);
            }
            
            ntotal = ntrkpt;
        }
        
        return resPoints;
	}
	
    private static void dump(Map<GpxPoint, Double> weights) {
		for ( GpxPoint point : weights.keySet() ) {
			System.out.println("Dump " + point.getId() );
		}
		
	}

	private static double weight(List<GpxPoint> points, int idx) {
        return (idx == 0 || idx == points.size() - 1) ? Double.POSITIVE_INFINITY : area(
        		points.get(idx), points.get(idx-1), points.get(idx+1));
    }
    
    private static double area(GpxPoint p1, GpxPoint p2, GpxPoint p3) {
        boolean threed = (p1.getAltitude() != null) && (p2.getAltitude() != null)
                && (p3.getAltitude() != null);
        double d12 = distance(p1, p2, threed);
        double d23 = distance(p2, p3, threed);
        double d13 = distance(p1, p3, threed);
        return triangleArea(d12, d23, d13);
    }
    
    /**
     * Calculates triangle area using the formula given in <a
     * href="http://http.cs.berkeley.edu/~wkahan/Triangle.pdf"
     * >http://http.cs.berkeley.edu/~wkahan/Triangle.pdf</a> page 4.
     */
    private static double triangleArea(double s1, double s2, double s3) {
        double[] cba = new double[] { Math.abs(s1), Math.abs(s2), Math.abs(s3) };
        Arrays.sort(cba);
        double a = cba[2];
        double b = cba[1];
        double c = cba[0];
        double diff = c - (a - b);
        if (diff < 0) {
            if (-diff / a < PRECISION) {
                // the minus sign is probably not significant
                // and we return 0
                return 0.0;
            }
            System.err.println(-diff / a);
            throw new IllegalArgumentException(
                    "triangleArea("
                            + s1
                            + ","
                            + s2
                            + ","
                            + s3
                            + ")\n"
                            + "The sum of the length of the two smaller "
                            + "sides of the triangle is not greater than the length of the "
                            + "third side. This is not a triangle!");
        }
        double sqrtarg = (a + (b + c)) * (c - (a - b)) * (c + (a - b))
                * (a + (b - c));
        return 0.25 * Math.sqrt(sqrtarg);
    }

    
    private static double distance(GpxPoint w1, GpxPoint w2, boolean threed) {
        // probably we should use straight line distance here, not geodetic
        // but then again the difference will usually not be large
        double dd = straightKmDistance(w1, w2);
        if (threed) {
            double dz = (w1.getAltitude().doubleValue() - w2.getAltitude().doubleValue())
                    * superelevation / 1000;
            dd = Math.sqrt(dd * dd + dz * dz);
        }
        return dd;
    }
    
    public static double kmDistance(GpxPoint a, GpxPoint b){
        return kmDistance(a.getLatitude(),
                          a.getLongitude(),
                          b.getLatitude(),
                          b.getLongitude());
    }
    
    public static double kmDistance(Number lat1, Number lon1, Number lat2, Number lon2){
        double phi1 = lat1.doubleValue() / 180 * Math.PI;
        double phi2 = lat2.doubleValue() / 180 * Math.PI;
        double deltaLambda = (lon1.doubleValue() - lon2.doubleValue())/180 * Math.PI;
        double cp1 = Math.cos(phi1);
        double cp2 = Math.cos(phi2);
        double sp1 = Math.sin(phi1);
        double sp2 = Math.sin(phi2);
        double sdl = Math.sin(deltaLambda);
        double cdl = Math.cos(deltaLambda);
        return EARTH_RADIUS * Math.atan2( Math.sqrt( Math.pow(cp2 * sdl, 2) + Math.pow(cp1 * sp2 - sp1 * cp2 * cdl, 2) ), sp1 * sp2 + cp1 * cp2 * cdl );
    }

    public static double straightKmDistance(GpxPoint a, GpxPoint b){
        double dGC = kmDistance(a, b);
        return 2 * EARTH_RADIUS * Math.sin(dGC/2/EARTH_RADIUS);
    }
    
    public static void main(String[] args) {
    	System.out.println("Start");
    	List<GpxPoint> gpsPoints = GpxDecoder.parse(new File("D:\\gps\\get301tracks\\Move_2014_11_08_12_46_34_Cyclisme.gpx"));
    	System.out.println("File : " + gpsPoints.size() );
    	
    	List<GpxPoint> reduced = shrink(gpsPoints);
    	System.out.println("File : " + reduced.size() );
    	
    	
    }


}
