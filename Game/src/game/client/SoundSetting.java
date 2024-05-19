package game.client;

/**
 * This class holds the settings for a particular game instance such as game length and map size
 * It can  also be used to set the default values of these parameters.
 */
public class SoundSetting<T> {

    /**
     * The name of the Setting
     */
    private String name;

    /**
     * The associated value of the setting
     */
    private T val;

    /**
     * Constructor for the Setting class
     * @param name name of the setting
     * @param val value of the setting
     */
    public SoundSetting (String name, T val) {
        this.name = name;
        this.val = val;
    }

    /**
     *
     * @return the name of the setting
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return the current value of the setting
     */
    public T getVal() {
        return val;
    }

    /**
     *
     * @param val the new value of the setting
     */
    public void setVal(T val) {
        this.val = val;
    }

}