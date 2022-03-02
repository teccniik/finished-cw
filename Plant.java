import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Plant implements Actor
{
    // Whether the plant is alive or not.
    protected boolean alive;
    // The plant's field.
    protected Field field;
    // The plant's position in the field.
    protected Location location;
    // The plant's age
    protected int age;
    // Maximum age of plant before it dies
    protected static int MAX_AGE;
    // Breeding age of plant
    protected static int BREEDING_AGE = 3;
    // The maximum number of births
    protected static int MAX_LITTER_SIZE = 10;
    // The likelihood of a plant reproducing.
    protected static double BREEDING_PROBABILITY = 0.6;
    
    // Random number generator
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new Plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Make this Plant act - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly born plants.
     */
    abstract public void act(List<Actor> newActors);
   
    /**
     * Check whether the plant is alive or not.
     * @return true if the plant is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Increments the age of the plant
     */
    public void incrementAge() {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Gives a number of children born to a plant
     * @return int births  The number of plants that a plant gives birth
     *  to.
     */
    protected int breed() {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
    
    /**
     * 
     */
    

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
     */
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the plant's field.
     * @return The plant's field.
     */
    public Field getField()
    {
        return field;
    }
}
