import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal implements Actor
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's gender
    private int female;

    // The age at which an Animal can start to breed.
    private int BREEDING_AGE;
    // The age to which an Animal can live.
    protected int MAX_AGE;
    // The likelihood of an Animal breeding.
    private double BREEDING_PROBABILITY;
    // The maximum number of births.
    private int MAX_LITTER_SIZE;

    // The Animal's age.
    protected int age;
    // The Animal's food level, which is increased by eating zebras.
    protected int foodLevel;
    // Animal's infection level
    private Double infection;

    // The animal's position in the field.
    private Location location;
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location, int breedingAge, int maxAge, double breedingProbability, int maxLitterSize)
    {
        alive = true;
        this.BREEDING_AGE = breedingAge;
        this.MAX_AGE = maxAge;
        this.BREEDING_PROBABILITY = breedingProbability;
        this.MAX_LITTER_SIZE = maxLitterSize;
        infection = rand.nextDouble();
        this.age = age;
        this.foodLevel = foodLevel;
        female = rand.nextInt(2);
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Whether the Animal has been infected or not
     * @return Double infection  True for an infected animal.
     */
    public Double isInfected() {
        return infection;
    }
    
    /**
     * Set the Animal's infection level to 0.8
     */
    public void setInfection() {
        this.infection = 0.8;
    }
    
    /**
    * Make this animal act - that is: make it do
    * whatever it wants/needs to do.
    * @param newAnimals A list to receive newly born animals.
    */
    abstract public void act(List<Actor> newAnimals);
    
    /**
     * Run the infection status of Animals.
     * Generates a random number, then if location isn't null then it checks adjacent locations.
     * For all adjacent locations, it checks for surrounding Animals, and for any alive Animals there's a random chance that they
     * will be infected.
     */
    public void runInfection(List<Actor> newAnimals) {
        Field field = getField();
        Double random = rand.nextDouble();
        if (!(this.location==null)) {
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if (animal instanceof Animal) {
                    Animal animalToInfect = (Animal) animal;
                    if (animalToInfect.isAlive()) {
                        if (random > 0.75) {
                            animalToInfect.setInfection();
                        }
                            
                    }
                }
            }
            //if (random > 0.99) {
            if (random > 0.99) {
                setDead();
            }
            
        }
    }
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Check whether or not the Animal is female.
     */
    protected int isFemale()
    {
        return female;
    }

    /**
     * Indicate that the animal is no longer alive, setting alive to false.
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
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
     * @param Location newLocation  The animal's new location.
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
     * Return the animal's field.
     * @return Field  The animal's field.
     */
    public Field getField()
    {
        return field;
    }

    /**
     * Make this Animal more hungry. This could result in the Zebra's death if its foodLevel is <= 0.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
            
        }
    }

    /**
     * Increase the age. This could result in the Animal's death, if age goes above the Animal's maximum age.
     */
    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return int  The number of births 
     */
    public int breed()
    {
        if (!(findMate())) {
            return 0;
        }
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) { // checks if animal can breed, and that a random number is less than the animal's breeding probability.
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A Zebra can breed if it has reached the breeding age.
     * @return boolean  true if age of animal is greater than its breeding age.
     */
    public boolean canBreed()
    {
        return (age >= BREEDING_AGE);
    }
    
    /**
     * A method that allows Animals to find mates. 
     * Creates a list of adjacent grid Locations, and checks them to see if there's a non-plant, Animal entity.
     * If the species is not null, the classes are equal, the gender is opposite to the gender of the Animal this method is called upon,
     * and the other Animal can breed, then a boolean value of true is returned. Otherwise, false is returned.
     * @return boolean  Value to confirm whether or not the Animal could find a viable mate.
     */
    public boolean findMate() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(this.getLocation());
        for (Location where : adjacent) { // Running through all Locations in the List adjacent.
            if (field.getObjectAt(where) != null) { // Checks that grid Location is not empty
                if (!(field.getObjectAt(where) instanceof Grass)) { // Checks that other entity is not Grass
                    Animal speciesInNextCell = (Animal) field.getObjectAt(where);
                    if (speciesInNextCell != null && this.getClass().equals(speciesInNextCell.getClass()) && speciesInNextCell.isFemale() != this.isFemale() && speciesInNextCell.canBreed()) { // Checks that there's an Animal in the next cell. Also checks: That the two Animals are the same class, the genders of the two are different and that the other Animal can breed.
                        return true;          
                    }
                }
            }  
        }
        return false;
    }
    
}
