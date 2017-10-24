package cs.cooble.graphics;

/**
 * Interface which take care of some things
 */
public interface CollectionManager<T> {

    /**
     * adds thing to look after
     *
     * @param thing
     */
    void register(T thing);

    /**
     * remove thing from looking after
     *
     * @param thing
     * @return true if success
     */
    boolean remove(T thing);

    /**
     * clear every thing
     */
    default void clear() {
        Exception e = new UnsupportedOperationException("Calling clear() method which is not overridden!");
        e.printStackTrace();
    }
}
