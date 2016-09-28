/*
 * http://moten-util.googlecode.com/svn-history/r55/forq/trunk/src/main/java/moten/david/util/math/Varimax.java
 * and open the template in the editor.
 */
package trendanalisis.main.method;


import java.util.ArrayList;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class VarimaxForPCA {

    private RotationMethod method;
    private double[][] data;
    private double param;
    double[] fnorm;
    double[] kaiserNorm;
    private int maxIterations = 100;
    private double epsilon = 0.00001;
    private static boolean verbose = false;
    public boolean kaiserNormalisation = false;

    public boolean getKaiserNormalisation() {
        return kaiserNormalisation;
    }

    public void setKaiserNormalisation(boolean kaiserNormalisation) {
        this.kaiserNormalisation = kaiserNormalisation;
        if (kaiserNormalisation && kaiserNorm == null) {
            kaiserNorm = new double[data[0].length];
        }
        if (!kaiserNormalisation) {
            kaiserNorm = null;
        }
    }

    public VarimaxForPCA(RotationMethod method, double[][] data, double param) {
        super();
        this.method = method;
        this.data = data;
        this.param = param;
    }

    public enum RotationScratch {

        GFNORM, GKNORM, GLABELS, NTRASH
    }

    public static enum RotationMethod implements Serializable {

        VARIMAX("Varimax"), QUARTIMAX("Quartimax"), EQUIMAX("Equimax"), ORTHOMAX(
        "Orthomax"), OBLIMIN("Oblimin"), NMETHODS("N Methods"), NONE(
        "Unknown");
        private String name;

        private RotationMethod(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /*
     * Code acquired from Doug Hawkins, University of Minnesota, 12/93
     * 
     * Comments from Fortran original Routine to do a varimax rotation on the
     * real array aload(nv,nnf). If nnf is positive, the routine feels free to
     * reverse and reorder the factors;this is suppressed in nnf is entered as
     * the negative of its actual value. This suppression is desirable when
     * doing a q-mode analysis.
     * 
     * Translated to C by C. Bingham with the following modifications Argument
     * nnf is now nf and is assumed positive and additional argument fnorm
     * replaces the local variable fnorm. If fnorm == (float *) 0, reversing and
     * reordering the factors is suppressed
     * 
     * Also argument itmax has been added to control the maximum number of
     * iterations and argument eps provides a convergence limit (originally hard
     * wired as 1e-4).
     * 
     * Information on the iteration is printed if verbose != 0
     * 
     * varmx() returns a non-zero value if and only if fewer than itmax
     * iterations are required.
     * 
     * 960919 Modified code to make it easier to add new rotation methods.
     * except for slgihtly different error messages, it should have no effect on
     * what it does. 980303 Changed check before computation of rotaton angle to
     * avoid atan2 domain error
     * 
     * 010614 added argument lambda to implement orthomax; lambda = 1 is
     * varimax; lambda = 0 is quartimax Also computation of the criterion moved
     * to separate function compcrit and names of certain variables were changed
     */
    private double getCriterion(double[][] loadings, double lambda) {
        double crit = 0;
        int numFactors = loadings.length;
        int numVariables = loadings[0].length;
        int i, j;
        for (j = 0; j < numFactors; j++) {
            double s2 = 0;
            for (i = 0; i < numVariables; i++) {
                double sq = sqr(loadings[j][i]);
                s2 += sq;
                crit += sq * sq;
            }
            crit -= lambda * s2 * s2 / numVariables;
        }
        return crit;
    }

    private double sqr(double d) {
        return d * d;
    }

    /*
     * 010614 added arguments method and param and modified code so that it
     * finds optimal orthomax rotation with parameter lambda = params[0] lambda ==
     * 1 <==> varimax lambda == 2 <==> quartimax
     */
    private List<MatrixRotation> varimax(double data[][], double param) {
        List<MatrixRotation> rotations = new ArrayList<MatrixRotation>();
        int numFactors = data.length;
        int numVariables = data[0].length;
        double criterion, startingCriterion;
        double denominator, numerator, angle, trot;
        double eps1 = epsilon, eps2 = epsilon;
        double lambda = 0.0;
        int inoim = 0, iterationCount = 0, rotationCount = 0;
        int numFactorsMinusOne = numFactors - 1;
        boolean solutionFound = true;
        if (method.ordinal() < RotationMethod.ORTHOMAX.ordinal()) {
            lambda = param;
        }
        startingCriterion = criterion = getCriterion(data, lambda);
        do {
            double oldCrit = criterion;
            for (int j = 0; j < numFactorsMinusOne; j++) {
                for (int k = j + 1; k < numFactors; k++) {
                    double a = 0, b = 0, c = 0, d = 0, s = 0;
                    for (int i = 0; i < numVariables; i++) {
                        double c2 = sqr(data[j][i]) - sqr(data[k][i]);
                        double s2 = 2.0 * data[j][i] * data[k][i];
                        a += c2;
                        b += s2;
                        c += sqr(c2) - sqr(s2);
                        d += c2 * s2;
                    }
                    denominator = numVariables * c + lambda * (sqr(b) - sqr(a));
                    numerator = 2.0 * (numVariables * d - lambda * a * b);
                    if (Math.abs(numerator) > eps1 * Math.abs(denominator)) {
                        solutionFound = false;
                        rotationCount++;
                        angle = 0.25 * Math.atan2(numerator, denominator);
                        c = Math.cos(angle);
                        s = Math.sin(angle);
                        for (int i = 0; i < numVariables; i++) {
                            double dataj = data[j][i];
                            double datak = data[k][i];
                            data[j][i] = c * dataj + s * datak;
                            data[k][i] = -s * dataj + c * datak;
                        }
                        rotations.add(new MatrixRotation(j + 1, k + 1, -angle));
                    }
                }
            }
            iterationCount++;
            criterion = getCriterion(data, lambda);
            trot = (criterion > 0.0) ? (criterion - oldCrit) / criterion : 0.0;
            inoim++;
            if (trot > eps2) {
                inoim = 0;
            }
        } while (inoim < 2 && iterationCount < maxIterations && !solutionFound);
        if (fnorm != null && false)// not all zeroes
        {
            for (int j = 0; j < numFactors; j++) {
                double ssj = 0, sj = 0;
                for (int i = 0; i < numVariables; i++) {
                    sj += data[j][i];
                    ssj += sqr(data[j][i]);
                }
                fnorm[j] = ssj;
                if (sj <= 0.0) {
                    for (int i = 0; i < numVariables; i++) {
                        data[j][i] = -data[j][i];
                    }
                }
                for (int k = 0; k < j; k++) {
                    if (fnorm[k] < fnorm[j]) {
                        double t = fnorm[k];

                        fnorm[k] = fnorm[j];
                        fnorm[j] = t;
                        for (int i = 0; i < numVariables; i++) {
                            t = data[j][i];
                            data[j][i] = data[k][i];
                            data[k][i] = t;
                        }
                    }
                }
            }
        }

        if (verbose) {
            System.out.println("method=" + method);
            System.out.println("starting criterion=" + startingCriterion);
            System.out.println("final criterion=" + criterion);
            System.out.println(iterationCount + " iterations and "
                    + rotationCount + " rotations");
        }
        if (iterationCount < maxIterations) {
            return rotations;
        } else {
            return null;
        }
    }

    /*
     * 011125 added argument kaiserNorm. kaiserNorm != (float *) 0 signals
     * Kaiser normalization with kaiserNorm providing scratch for row norms
     */
    public List<MatrixRotation> rotate() {
        int numFactors = data.length;
        int numVariables = data[0].length;
        List<MatrixRotation> rotations = new ArrayList<MatrixRotation>();
        if (method.equals(RotationMethod.NONE)) {
            
            return rotations;
        }
        if (method.ordinal() <= RotationMethod.ORTHOMAX.ordinal()) {
            if (method.ordinal() < RotationMethod.ORTHOMAX.ordinal()) {
                if (method.ordinal() == RotationMethod.VARIMAX.ordinal()) {
                    param = 1.0;
                } else if (method.ordinal() == RotationMethod.QUARTIMAX.ordinal()) {
                    param = 0.0;
                } else {
                    /* equimax */
                    param = .5 * numFactors;
                }
            }
            if (kaiserNorm != null) {
                double s;
                for (int i = 0; i < numVariables; i++) {
                    s = 0.0;
                    for (int j = 0; j < numFactors; j++) {
                        s += data[j][i] * data[j][i];
                    }
                    kaiserNorm[i] = s = (s > 0) ? Math.sqrt(s) : 1.0;

                    for (int j = 0; j < numFactors; j++) {
                        data[j][i] /= s;
                    }
                }
            }
            
            rotations = varimax(data, param);
            if (kaiserNorm != null) {
                for (int j = 0; j < numFactors; j++) {
                    for (int i = 0; i < numVariables; i++) {
                        data[j][i] *= kaiserNorm[i];
                    }
                }
            }
        }
        return rotations;
    }

    private void printArray(double[][] data) {
        for (int j = 0; j < data[0].length; j++) {
            for (int i = 0; i < data.length; i++) {
                System.out.print(data[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public RotationMethod getMethod() {
        return method;
    }

    public void setMethod(RotationMethod method) {
        this.method = method;
    }

    public double getParam() {
        return param;
    }

    public void setParam(double param) {
        this.param = param;
    }

    public static boolean getVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean isVerbose) {
        verbose = isVerbose;
    }

    public double[] getKaiserNorm() {
        return kaiserNorm;
    }

    public class MatrixRotation implements Serializable {

        private int column1;
        private int column2;
        private double angle;

        public int getColumn1() {
            return column1;
        }

        public MatrixRotation(int column1, int column2, double angle) {
            super();
            this.column1 = column1;
            this.column2 = column2;
            this.angle = angle;
        }

        public MatrixRotation(int column1, int column2, int angleDegrees) {
            super();
            this.column1 = column1;
            this.column2 = column2;
            this.angle = Math.PI / 180 * angleDegrees;
        }

        public void setColumn1(int column1) {
            this.column1 = column1;
        }

        public int getColumn2() {
            return column2;
        }

        public void setColumn2(int column2) {
            this.column2 = column2;
        }

        public double getAngle() {
            return angle;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }

        public double getAngleDegrees() {
            return angle * 180 / Math.PI;
        }

        @Override
        public String toString() {
            return "(" + column1 + "," + column2 + ","
                    + new DecimalFormat("0.00000").format(getAngleDegrees()) + ")";
        }

        public void simplify(List<MatrixRotation> list) {
            int i = 0;
            while (list.size() > i + 1) {
                if (list.get(i).column1 == list.get(i + 1).column1
                        && list.get(i).column2 == list.get(i + 1).column2) {
                    list.get(i).setAngle(
                            list.get(i).getAngle() + list.get(i + 1).getAngle());
                    list.remove(i + 1);
                } else {
                    i++;
                }
            }
            for (i = list.size() - 1; i >= 0; i--) {
                if (Math.abs(list.get(i).getAngle()) < 0.000001) {
                    list.get(i).setAngle(0);
                }
                if (list.get(i).getAngle() == 0) {
                    list.remove(i);
                }
            }
        }
    }
    
    
}