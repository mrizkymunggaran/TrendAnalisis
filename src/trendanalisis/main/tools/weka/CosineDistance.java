/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    EuclideanDistance.java
 *    Copyright (C) 1999-2007 University of Waikato, Hamilton, New Zealand
 *
 */

package trendanalisis.main.tools.weka;

import java.util.Enumeration;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.PerformanceStats;

/**
 <!-- globalinfo-start -->
 * Implementing Euclidean distance (or similarity) function.<br/>
 * <br/>
 * One object defines not one distance but the data model in which the distances between objects of that data model can be computed.<br/>
 * <br/>
 * Attention: For efficiency reasons the use of consistency checks (like are the data models of the two instances exactly the same), is low.<br/>
 * <br/>
 * For more information, see:<br/>
 * <br/>
 * Wikipedia. Euclidean distance. URL http://en.wikipedia.org/wiki/Euclidean_distance.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{missing_id,
 *    author = {Wikipedia},
 *    title = {Euclidean distance},
 *    URL = {http://en.wikipedia.org/wiki/Euclidean_distance}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -D
 *  Turns off the normalization of attribute 
 *  values in distance calculation.</pre>
 * 
 * <pre> -R &lt;col1,col2-col4,...&gt;
 *  Specifies list of columns to used in the calculation of the 
 *  distance. 'first' and 'last' are valid indices.
 *  (default: first-last)</pre>
 * 
 * <pre> -V
 *  Invert matching sense of column indices.</pre>
 * 
 <!-- options-end --> 
 *
 * @author Gabi Schmidberger (gabi@cs.waikato.ac.nz)
 * @author Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.13 $
 */
public class CosineDistance
  implements DistanceFunction {

        @Override
        public double distance(Instance first, Instance second) {
                double dotProduct = 0,
                           normFirstInstance = 0,
                           normSecondInstance = 0;
                
                for(int i=0;i<first.numAttributes();i++){
                        dotProduct                 += first.value(i)*second.value(i);
                        normFirstInstance  += first.value(i)*first.value(i);
                        normSecondInstance += second.value(i)*second.value(i);
                }
                normFirstInstance  =(Math.sqrt(normFirstInstance));
                normSecondInstance = (Math.sqrt(normSecondInstance));

                return ((double)dotProduct/(double)(normFirstInstance*normSecondInstance));
        }

        @Override
        public double distance(Instance first, Instance second,
                        PerformanceStats stats) throws Exception {
                return distance(first, second);
        }

        @Override
        public double distance(Instance first, Instance second, double cutOffValue) {
                return distance(first, second);
        }

        @Override
        public double distance(Instance first, Instance second, double cutOffValue,
                        PerformanceStats stats) {
                return distance(first, second);
        }

        @Override
        public String getAttributeIndices() {
                return null;
        }

        @Override
        public Instances getInstances() {
                return null;
        }

        @Override
        public boolean getInvertSelection() {
                return false;
        }

        @Override
        public void postProcessDistances(double[] distances) {
        }

        @Override
        public void setAttributeIndices(String value) {
        }

        @Override
        public void setInstances(Instances insts) {
        }

        @Override
        public void setInvertSelection(boolean value) {
        }

        @Override
        public void update(Instance ins) {
        }

        @Override
        public String[] getOptions() {
                return null;
        }

        @Override
        public Enumeration listOptions() {
                return null;
        }

        @Override
        public void setOptions(String[] options) throws Exception {
        }

    @Override
    public void clean() {
      
    }
}
