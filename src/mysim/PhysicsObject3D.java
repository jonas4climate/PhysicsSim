package mysim;

/**
 * 3D object entity class
 */
public class PhysicsObject3D implements Cloneable {

   /**
    * ID used and incremented (to ensure uniqueness) when naming without a custom
    * name. Assuming users don't name the object after a number that then occurs as
    * ID iteration.
    * We are assuming that every object is a sphere to make hitbox operations easier
    */
   private static Integer ID = 1;

   /**
    * name given by user
    */
   public String name;

   /**
    * mass in kg
    */
   public double m;

   /**
    * radius in m
    */
   public double r;

   /**
    * position in m in format [x,y,z] to relative (0|0|0)
    */
   public Vector3D s = new Vector3D();

   /**
    * velocity in m/s in format [x,y,z]
    */
   public Vector3D v = new Vector3D();

   /**
    * acceleration in m/s^2 in format [x,z,y]
    */
   public Vector3D a = new Vector3D();

   /**
    * Most basic constructor
    */
   public PhysicsObject3D() {
      ID++;
   }

   /**
    * Constructor ignoring initial velocity
    * @param name name
    * @param m    mass
    * @param s    initial position
    */
   public PhysicsObject3D(String name, double r, double m, double[] s) {
      this.name = name;
      this.r = r;
      this.m = m;
      this.s.setVector(s);
   }

   /**
    * More advanced constructor allowing to declare velocity
    * @param name name
    * @param mass mass
    * @param s initial position
    * @param v initial velocity
    */
   public PhysicsObject3D(String name, double r, double m, double[] s, double[] v) {
      this.name = name;
      this.r = r;
      this.m = m;
      this.s.setVector(s);
      this.v.setVector(v);
   }

   /**
    * Advanced constructor to customize all variables but name
    * 
    * @param m mass
    * @param s position
    * @param v velocity
    */
   public PhysicsObject3D(double m, double r, double[] s, double[] v, double[] a) {
      name = ID.toString();
      ID++;
      this.r = r;
      this.m = m;
      this.s.setVector(s);
      this.v.setVector(v);
   }

   /**
    * Compact toString containing all relevant information
    */
   @Override
   public String toString() {
      return String.format("%10s: m=%6.3e, r=%6.3e, s=[%+6.2e,%+6.2e,%+6.2e], v=[%+6.2e,%+6.2e,%+6.2e], a=[%+6.2e,%+6.2e,%+6.2e]\n            |s|=%+6.2e  |v|=%+6.2e  |a|=%+6.2e",
            name, m, r, s.vector[0], s.vector[1], s.vector[2], v.vector[0], v.vector[1], v.vector[2], a.vector[0], a.vector[1], a.vector[2], s.length(), v.length(), a.length());
   }

   /**
    * deep-copy clone
    */
   @Override
   public PhysicsObject3D clone() {
      return new PhysicsObject3D(new String(name), m, r, s.vector.clone(), v.vector.clone());
   }
}