package mysim;

/**
 * 3D object entity class
 */
public class PhysicsObject3D implements Cloneable {

   /**
    * ID used and incremented (to ensure uniqueness) when naming without a custom
    * name. Assuming users don't name the object after a number that then occurs as
    * ID iteration.
    */
   private static Integer ID = 1;

   /**
    * name given by user
    */
   public String name = ID.toString();

   /**
    * mass in kg
    */
   public double m = 0;

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
    * Basic constructor with renaming
    * 
    * @param name name
    */
   public PhysicsObject3D(String name) {
      this.name = name;
   }

   /**
    * Medium constructor when velocity and acceleration are neglectable
    * 
    * @param name name
    * @param m    mass
    * @param s    position
    */
   public PhysicsObject3D(String name, double m, double[] s) {
      this.name = name;
      this.m = m;
      this.s.setVector(s);
   }

   public PhysicsObject3D(String name, double m, double[] s, double[] v) {
      this.name = name;
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
    * @param a accelertion
    */
   public PhysicsObject3D(double m, double[] s, double[] v, double[] a) {
      ID++;
      this.m = m;
      this.s.setVector(s);
      this.v.setVector(v);
      this.a.setVector(a);
   }

   /**
    * Constructor to customize all variables
    * 
    * @param name name
    * @param m    mass
    * @param s    position
    * @param v    velocity
    * @param a    acceleration
    */
   public PhysicsObject3D(String name, double m, double[] s, double[] v, double[] a) {
      this.name = name;
      this.m = m;
      this.s.setVector(s);
      this.v.setVector(v);
      this.a.setVector(a);
   }

   /**
    * Informative but not pretty toString
    */
   @Override
   public String toString() {
      return String.format("PhysicsObject3D '%10s': m=%6.3e, s=[%.2f,%.2f,%.2f], v=[%.2f,%.2f,%.2f], a=[%.2f,%.2f,%.2f]",
            name, m, s.vector[0], s.vector[1], s.vector[2], v.vector[0], v.vector[1], v.vector[2], a.vector[0], a.vector[1], a.vector[2]);
   }

   /**
    * deep-copy clone
    */
   @Override
   public PhysicsObject3D clone() {
      return new PhysicsObject3D(new String(name), m, s.vector.clone(), v.vector.clone(), a.vector.clone());
   }
}