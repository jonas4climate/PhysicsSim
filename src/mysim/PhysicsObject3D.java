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
    * position in m in format [x,y,z] to  (0|0|0)
    */
   public Vector3D s = new Vector3D();

   /**
    * velocity in m/s in format [x,y,z]
    */
   public Vector3D v = new Vector3D();

   /**
    * acceleration due to all applied forces in m/s^2 in format [x,z,y]
    */
   public Vector3D a = new Vector3D();

   /**
    * 
    * @param name name
    * @param r radius
    * @param m mass
    * @param s position
    */
   public PhysicsObject3D(String name, double r, double m, double[] s) {
      this.name = name;
      this.r = r;
      this.m = m;
      this.s.setVector(s);
   }

   /**
    * 
    * @param name name
    * @param r radius
    * @param m mass
    * @param s position
    * @param v velocity
    */
   public PhysicsObject3D(String name, double r, double m, double[] s, double[] v) {
      this.name = name;
      this.r = r;
      this.m = m;
      this.s.setVector(s);
      this.v.setVector(v);
   }

   /**
    * 
    * @param m mass
    * @param r radius
    * @param s position
    * @param v velocity
    */
   public PhysicsObject3D(double r, double m, double[] s, double[] v) {
      name = ID.toString();
      this.r = r;
      this.m = m;
      this.s.setVector(s);
      this.v.setVector(v);
      ID++;
   }

   public double getVolume() {
      return (4/3) * Math.PI * Math.pow(r,2);
   }

   /**
    * Get current kinetic energy of the object
    * @return kinetic energy
    */
   public double getKineticEnergy() {
      return 0.5 * m * Math.pow(v.length(),2);
   }

   public double getDensity() {
      return getVolume()/m;
   }
   
   @Override
   public String toString() {
      return String.format("%10s: m=%6.3e, r=%6.3e, s=[%+6.2e,%+6.2e,%+6.2e], v=[%+6.2e,%+6.2e,%+6.2e], a=[%+6.2e,%+6.2e,%+6.2e]\n            |s|=%+6.2e  |v|=%+6.2e  |a|=%+6.2e",
            name, m, r, s.vector[0], s.vector[1], s.vector[2], v.vector[0], v.vector[1], v.vector[2], a.vector[0], a.vector[1], a.vector[2], s.length(), v.length(), a.length());
   }

   /**
    * Deep copy clone
    */
   @Override
   public PhysicsObject3D clone() {
      return new PhysicsObject3D(new String(name), r, m, s.vector.clone(), v.vector.clone());
   }
}