import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Zebra.
 * Zebras age, move, eat zebras, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Zebra extends Animal
{
    // Characteristics shared by all Zebras (class variables).

    // The food value of a single zebra. In effect, this is the
    // number of steps a Zebra can go before it has to eat again.
    private static final int G_FOOD_VALUE = 9;
    private static final int GRASS_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
        // // The age at which a Zebra can start to breed.
    // private int BREEDING_AGE;
    // // The age to which a Zebra can live.
    // private int MAX_AGE;
    // // The likelihood of a Zebra breeding.
    // private double BREEDING_PROBABILITY;
    // // The maximum number of births.
    // private int MAX_LITTER_SIZE;

    // The Zebra's age.
    protected int age;
    // The Zebra's food level, which is increased by eating zebras.
    protected int foodLevel;
    // The Zebra's field
    private Field field;
    // Minimum food level where zebras can breed
    protected int breedingFoodLevel;


    /**
     * Create a Zebra. A Zebra can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Zebra will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Zebra(boolean randomAge, Field field, Location location, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE)
    {
        super(field, location, 15, 250, 0.8, 4);
        breedingFoodLevel = 0;
        
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GRASS_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = GRASS_FOOD_VALUE;
        }
    }
    

    /**
     * This is what the Zebra does most of the time: it hunts for
     * zebras. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newZebras A list to return newly born Zebras.
     */
    public void act(List<Actor> newZebras)
    {
        incrementAge();
        if (Simulator.getTime() >= 6 && Simulator.getTime() <= 18) {
            if(isAlive()) {
                if (foodLevel > breedingFoodLevel) {
                    giveBirth(newZebras); 
                }
                // Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
                runInfection(newZebras);
            }
            incrementHunger();
        }
        else {
            Double randomVal = rand.nextDouble();
            if (randomVal < 0.1) {
                incrementHunger();
            }
        }
    }
    
    /**
     * Increment the Zebra's hunger. The Zebra could die from this.
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();            
        }
    }


    /**
     * Look for zebra adjacent to the current location.
     * Only the first live zebra is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) { 
                    grass.setDead();
                    foodLevel = GRASS_FOOD_VALUE;                  
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this Zebra is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newZebras A list to return newly born Zebras.
     */
    private void giveBirth(List<Actor> newZebras) {
        // New Zebras are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Zebra young = new Zebra(false, field, loc, 15, 150, 0.6, 2);
            newZebras.add(young);
        }
    }

}
