package demo.domain.view;

/**
 * Created by nlabrot on 30/12/14.
 */
public class Views {
    public static class Public { }
    public static class ExtendedPublic extends Public { }
    public static class Internal extends ExtendedPublic { }
}