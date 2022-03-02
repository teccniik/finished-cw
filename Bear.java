import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Bear.
 * Bears age, move, eat deers, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Bear extends Animal
{
    // Characteristics shared by all Bears (class variables).
    
    // The food value of a single deer. In effect, this is the
    // number of steps a Bear can go before it has to eat again.
    private static final int DEER_FOOD_VALUE = 20;
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
    
    protected int breedingFoodLevel;

    /**
     * Create a Bear. A Bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE)
    {
        super(field, location, 15, 150, 0.8, 2);
        breedingFoodLevel = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEER_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = DEER_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the Bear does most of the time: it hunts for
     * deers. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newBears A list to return newly born Bears.
     */
    public void act(List<Actor> newBears)
    {
        incrementAge();
        if (Simulator.getTime() >= 5 && Simulator.getTime() <= 17) {
            if(isAlive()) {
                if (foodLevel > breedingFoodLevel) {
                    giveBirth(newBears);
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
                runInfection(newBears);
                incrementHunger();
            }
        }
        else {
            Double randomVal = rand.nextDouble();
            if (randomVal < 0.1) {
                incrementHunger();
            }
        }
    }
    
    /**
     * Look for deers adjacent to the current location.
     * Only the first live deer is eaten.
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
            if(animal instanceof Deer) {
                Deer deer = (Deer) animal;
                if(deer.isAlive()) { 
                    deer.setDead();
                    foodLevel = DEER_FOOD_VALUE;
                    //System.out.println("Bear eats Deer.");
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this Bear is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBears A list to return newly born Bears.
     */
    private void giveBirth(List<Actor> newbears)
    {
        // New bears are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Bear young = new Bear(false, field, loc, 15, 150, 0.7, 2);
            //Bear young = new Bear(false, field, loc, 15, 150, 0.35, 2);
            newbears.add(young);
        }
    }
    
    /**
     * Increment the Bear's hunger. This could cause the Bear to die.
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
