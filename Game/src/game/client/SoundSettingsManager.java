package game.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This class holds the settings for a particular game instance such as game length and map size
 * It can  also be used to set the default values of these parameters.
 */
public class SoundSettingsManager {

    /**
     * Constructor for the ServerSettings class
     * auto sets default values
     */
    public SoundSettingsManager() {
        setDefaultValues();
    }

    /**
     * sets the default values for all the settings
     */
    private void setDefaultValues() {

    }

    /**
     * sets a field to a new value based on the name of the field.
     * @param variable the name of the variable that has to be changed
     * @param newValue the Value to which the variable has to be changed to
     */
    public void setValue(String variable, Object newValue) {
        String variableName = getSimilarNameToVariable(variable, "", this.getClass().getDeclaredFields());
        Object obj;
        try {
            obj = this.getClass().getField(variableName).get(this);
        } catch (Exception e) {
            System.out.println("no field by the name \"" + variable + "\" exists");
            return;
        }
        if (!(obj instanceof SoundSetting)) {
            return;
        }
        SoundSetting setting = (SoundSetting<?>) obj;
        if(setting.getVal() instanceof Integer) {
            setting.setVal(newValue);
        }

    }

    /**
     * matches any case to camelCase for all the fields
     * @param similarName the variable name in any case matched to camelCase
     * @param preFix whether the variable has a prefix or not (not used in here)
     * @param fields all the fields where the variable name should be searched
     * @return the camelCase of a variableName
     */
    private String getSimilarNameToVariable(String similarName, String preFix, Field[] fields) {
        for (Field f : fields) {
            if (f.getName().toLowerCase(Locale.ROOT).matches(preFix + similarName.toLowerCase(Locale.ROOT))) {
                return f.getName();
            }
        }
        return "NONE";
    }

}