import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Lion.
 * Lions age, move, eat zebras, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Lion extends Animal
{
    // Characteristics shared by all Lions (class variables).

    // The food value of a single zebra. In effect, this is the
    // number of steps a Lion can go before it has to eat again.
    private static final int ZEBRA_FOOD_VALUE = 20;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

        // The age at which a Zebra can start to breed.
    protected static int BREEDING_AGE;
    // The age to which a Zebra can live.
    protected static int MAX_AGE;
    // The likelihood of a Zebra breeding.
    protected static double BREEDING_PROBABILITY;
    // The maximum number of births.
    protected static int MAX_LITTER_SIZE;

    // The Zebra's age.
    protected int age;
    // The Zebra's food level, which is increased by eating zebras.
    protected int foodLevel;
    //
    protected int breedingFoodLevel;

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE)
    {
        super(field, location, 15, 150, 0.9, 3);
        breedingFoodLevel = 0;
        
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(ZEBRA_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = ZEBRA_FOOD_VALUE;
        }
    }

    /**
     * This is what the Lion does most of the time: it hunts for
     * zebras. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newLions A list to return newly born Lions.
     */
    public void act(List<Actor> newLions)
    {
        incrementAge();
        runInfection(newLions);
        if (Simulator.getTime() >= 8 && Simulator.getTime() <= 18) {
            if(isAlive()) {
                if (foodLevel >= breedingFoodLevel) {
                    giveBirth(newLions); 
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
                
            }
            incrementHunger();
        }
        else {
            Double randomVal = rand.nextDouble();
            if (randomVal < 0.125) {
                incrementHunger();
            }
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
            Object animal = field.getObjectAt(where);
            if(animal instanceof Zebra) {
                Zebra zebra = (Zebra) animal;
                if(zebra.isAlive()) { 
                    zebra.setDead();
                    foodLevel = ZEBRA_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this Lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLions A list to return newly born Lions.
     */
    private void giveBirth(List<Actor> newLions)
    {
        // New Lions are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Lion young = new Lion(false, field, loc, 15, 75, 0.9, 2);
            //Lion young = new Lion(false, field, loc, 15, 150, 0.35, 2);
            newLions.add(young);
        }
    }
    
    /**
     * Increment Lion's hunger. This could reduce in the Lion's death.
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }
    
    // public boolean findMate() {
        // Field field = getField();
        // List<Location> adjacent = field.adjacentLocations(this.getLocation());
        // for (Location where : adjacent) {
            // if (field.getObjectAt(where) != null) {
                // if (!(field.getObjectAt(where) instanceof Grass)) {
                    // Animal speciesInNextCell = (Animal) field.getObjectAt(where);
                    // if (speciesInNextCell != null && this.getClass().equals(speciesInNextCell.getClass()) && speciesInNextCell.isFemale() != this.isFemale() && speciesInNextCell.canBreed()) {
                        // return true;          
                    // }
                // }
            // }  
        // }
        // return false;
    // }

}
