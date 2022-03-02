import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Grass.
 * Grass age, grow, reproduce and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Grass extends Plant
{
    // Characteristics shared by all Grass (class variables).
    
    // The age at which a Grass can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a Grass can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a Grass breeding.
    private static double BREEDING_PROBABILITY = 0.75;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The Grass's age.
    private int age;
    // The Grass's food level, which is increased by eating zebras.
 

    /**
     * Create a Grass. A Grass can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Grass will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the Grass does most of the time: it hunts for
     * zebras. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newGrass A list to return newly born Grass.
     */
    public void act(List<Actor> newActors)
    {
        incrementAge();
        if (Simulator.getTime() >= 6 && Simulator.getTime() <= 20) {
            if(isAlive()) {
                giveBirth(newActors);           
            }
        }
        if (Simulator.getRain()) {
            BREEDING_PROBABILITY = 1.0;
        }
        else if (Simulator.rainedThisSeason()) {
            BREEDING_PROBABILITY = 0.7;
        }
        else {
            BREEDING_PROBABILITY = 0.5;
        }
    }

    /**
     * Increase the age. This could result in the Grass's death.
     */
    public void incrementAge() {
        age++;
        if (age > MAX_AGE) {
            setDead();
        }
    }
    
    
    /**
     * Check whether or not this Grass is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGrass A list to return newly born Grass.
     */
    private void giveBirth(List<Actor> newGrass)
    {
        // New Grass are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Grass young = new Grass(false, field, loc);
            newGrass.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    // private int breed()
    // {
        // int births = 0;
        // if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            // births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        // }
        // return births;
    // }

    /**
     * A Grass can breed if it has reached the breeding age.
     */
    public boolean canBreed() {
        return age >= BREEDING_AGE;
    }
    
    /**
     * PLace the Grass object in a Location within the field.
     */
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
}
