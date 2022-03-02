import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Tiger .
 * Tigers  age, move, eat deers, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Tiger extends Animal
{
    // Characteristics shared by all Tigers  (class variables).
    
    // The food value of a single deer. In effect, this is the
    // number of steps a Tiger  can go before it has to eat again.
    private static final int DEER_FOOD_VALUE = 24;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
        // The age at which a Tiger can start to breed.
    protected static int BREEDING_AGE;
    // The age to which a Tiger can live.
    protected static int MAX_AGE;
    // The likelihood of a Tiger breeding.
    protected static double BREEDING_PROBABILITY;
    // The maximum number of births.
    protected static int MAX_LITTER_SIZE;

    // The Tiger's age.
    protected int age;
    // The Tiger's food level, which is increased by eating zebras.
    protected int foodLevel;
    // Minimum food level for Tiger to reproduce
    protected int breedingFoodLevel;

    /**
     * Create a Tiger . A Tiger  can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Tiger  will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tiger (boolean randomAge, Field field, Location location, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE)
    {

        super(field, location, 15, 150, 0.8, 1);
        //super(field, location, 15, 150, 0.4, 1);
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
     * This is what the Tiger  does most of the time: it hunts for
     * deers. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newTigers  A list to return newly born Tigers .
     */
    public void act(List<Actor> newTigers )
    {
        incrementAge();
        if (Simulator.getTime() >= 0 && Simulator.getTime() <= 14) {
            if(isAlive()) {
                if (foodLevel > breedingFoodLevel) {
                    giveBirth(newTigers ); 
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
                runInfection(newTigers);
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
                    //System.out.println("Tiger eats Deer");
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this Tiger  is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newTigers  A list to return newly born Tigers .
     */
    private void giveBirth(List<Actor> newTigers )
    {
        // New Tigers  are born into adjacent locations.
        // Get a list of adjacent free locations.
        //System.out.println("Tiger giving birth!");
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Tiger  young = new Tiger (false, field, loc, 15, 150, 0.7, 2);
            newTigers .add(young);
        }
    }
    
    /**
     * Increment the Tiger's hunger level. The tiger could die due to this.
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }
    



}
