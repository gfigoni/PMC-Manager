package models.manialive;

/**
 * Map dans la base manialive
 * 
 * @author gehef
 */
public class MLMap {
    public String id;
    public String name;
    public String authorId;
    public String environment;
    
    @Override
    public boolean equals(Object o) {
        MLMap other = (MLMap) o;
        return this.id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
