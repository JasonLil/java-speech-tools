/* Generated by TooT */

package uk.org.toot.misc;

public interface UndoableCommand  {

    boolean execute();
    boolean unexecute();
    boolean isStructural();
    String getName();
}
