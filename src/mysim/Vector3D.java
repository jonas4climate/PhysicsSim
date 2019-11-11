package mysim;

/**
 * Class to model 3D vectors and their operations for information such as s, v, a.
 */
public class Vector3D {

   /**
    * 3D Vector
    */
   public double[] vector;

   /**
    * Default constructor
    */
   public Vector3D() {
      this.vector = new double[] {0,0,0};
   }

   /**
    * Second constructor
    * @param vector initial vector
    */
   public Vector3D(double[] vector) {
      this.vector = vector;
   }

   /**
    * Calculates distance between two vectors in 3D space. Order of vectors irrelevant.
    * @param v1 first vector
    * @param v2 second vector
    * @return distance
    */
    public static double distance(Vector3D v1, Vector3D v2) {
      return Math.sqrt(Math.pow(v1.vector[0] - v2.vector[0], 2) + Math.pow(v1.vector[1] - v2.vector[1], 2) + Math.pow(v1.vector[2] - v2.vector[2], 2));
   }

   public static Vector3D normDirV(Vector3D v1, Vector3D v2) {
      Vector3D result = new Vector3D(new double[] {v2.vector[0] - v1.vector[0], v2.vector[1] - v1.vector[1], v2.vector[2] - v1.vector[2]});
      result.normalize();
      return result;
   }

   /**
    * Normalizes the given position vector (length of vector is approx. 1)
    */
    public void normalize() {
      double length = length();
      for (int i = 0; i < 3; i++)
         vector[i] /= length;
   }

   /**
    * Determines length of a vector
    * @return length
    */
    public double length() {
      return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
   }


   /**
    * Reset vector with new values and array object
    * @param v new values
    */
   public void setVector(double[] v) {
      double[] newVector = new double[3];
      for (int i = 0; i < 3; i++)
         newVector[i] = v[i];
      vector = newVector;
   }
}