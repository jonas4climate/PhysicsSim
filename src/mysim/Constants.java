package mysim;

/**
 * Helper class to set any physical constants used by the Simulator
 */
public abstract class Constants {

   /**
    * Gravitational constant
    */
   protected static final double G_CONST = 6.67430 * Math.pow(10, -11);

   /**
    * gravitational acceleration on earth in m/s^2
    */
    protected static final double G_ACC = 9.80665;

   /**
    * mass of earth in kg
    */
   protected static final double M_EARTH = 5.9736 * Math.pow(10, 24);

   /**
    * radius of earth at equator in m
    */
   protected static final double R_EARTH = 6378000;

   protected static 


   /**
    * Earth as PhysicsObject3D
    */
   protected static PhysicsObject3D EARTH = new PhysicsObject3D("Earth", M_EARTH, new double[]{0,0,-R_EARTH});

}