package pocketknife.internal.codegen;

/**
 * Created by hansenji on 9/23/14.
 */
public class IllegalBundleTypeException extends Exception {
    public IllegalBundleTypeException(StoreFieldBinding storeFieldBinding) {
        super("Illegal bundle type (" + storeFieldBinding.getType() + ") for " + storeFieldBinding.getName());
    }
}
