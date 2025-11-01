import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.commander {
    exports net.thenextlvl.commander;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
    requires net.thenextlvl.binder;
}