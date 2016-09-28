/**
 * 
 */
package trendanalisis.main.method;

/**
 * calculates the euclidean distance of two vectors of equal length
 * @author Markus
 *
 */
public class Euclidean {
	
	public  double calculate (double[] vect1, double[] vect2){
		if(vect1.length != vect2.length){
			throw new NumberFormatException();
		}
		double result = 0;
		double d;
		for (int i = 0; i < vect1.length; i++) {
			d = (vect1[i] - vect2[i]);
			result += ( d * d ); //power of 2
		}
		return (double) Math.sqrt(result);
	}
	
    @Override
	public String toString(){
		return "Euclidean Distance";
	}

}
