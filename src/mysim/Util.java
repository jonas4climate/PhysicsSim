package mysim;

/**
 * Helper class to set any physical constants used by the Simulator
 */
public abstract class Util {

   /**
    * Gravitational constant
    */
   protected static final double G_CONST = 6.67430 * Math.pow(10, -11);

   /**
    * Astronomic Unit - average distance between Earth and Sun in m
    */
   protected static final double AU = 149597870700d;

   /**
    * Speed of light in m/s
    */
   protected static final double C = 299792458d;

   protected static final double M_EARTH = 5.9722 * Math.pow(10, 24);

   /**
    * radius of earth at equator in m
    */
   protected static final double R_EARTH = 6378000d;

   /**
    * velocity of earth around the sun in m/s
    */
   protected static final double V_EARTH = 29780;

   /**
    * Closest approximation of orbital period around the sun in s
    */
   protected static final int ORBITAL_PERIOD_EARTH = 31558150;

   protected static final double M_SUN = 1.98847 * Math.pow(10,30);

   protected static final double R_SUN = 695700000d;

   protected static final double M_MOON = 7.246 * Math.pow(10,22);

   protected static final double R_MOON = 1737500d;

   protected static final double DISTANCE_EARTH_MOON = 384400000d;

   /* V_MOON = s / t where
         s = moon orbit around sun + 12 * moon orbit around earth
         t = +/- same orbital period as earth
   */
   protected static final double V_MOON = ((2 * Math.PI * (AU + DISTANCE_EARTH_MOON)) + (12 * 2 * Math.PI * DISTANCE_EARTH_MOON)) / ORBITAL_PERIOD_EARTH;

   protected static final double M_CHICXULUB = 4.6 * Math.pow(10, 17);

   protected static final double R_CHICXULUB = 40500d;

   /**
    * Solar system sun model
    */
   protected static PhysicsObject3D SUN = new PhysicsObject3D("Sun", R_SUN, M_SUN, new double[]{0,0,0});

   /**
    * Solar system earth model
    */
   protected static PhysicsObject3D EARTH = new PhysicsObject3D("Earth", R_EARTH, M_EARTH, new double[]{AU,0,0}, new double[]{0,V_EARTH,0});

   /**
    * Solar system moon model
    */
   protected static PhysicsObject3D MOON = new PhysicsObject3D("Moon", R_MOON, M_MOON, new double[]{AU + DISTANCE_EARTH_MOON,0,0}, new double[]{0,V_MOON,0});

   /**
    * Asteroid "Chicxulub incubator" that had supposingly hit earth and lead to the extinction of most dinosaurs
    */
   protected static PhysicsObject3D CHICXULUB = new PhysicsObject3D("Chicxulub", R_CHICXULUB, M_CHICXULUB, new double[]{AU,0,0});
}