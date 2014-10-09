package pocketknife.internal.codegen;

/**
 * Created by hansenji on 9/23/14.
 */
public class IllegalBundleTypeException extends Exception {
    public IllegalBundleTypeException(BundleFieldBinding bundleFieldBinding) {
        super("Illegal bundle type (" + bundleFieldBinding.getType() + ") for " + bundleFieldBinding.getName());
    }
}
