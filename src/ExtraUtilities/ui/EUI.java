package ExtraUtilities.ui;

public class EUI {
    public RogueLikeStart roguelike;
    public DDItemsList ddItemsList;

    public void init(){
        roguelike = new RogueLikeStart();
        ddItemsList = new DDItemsList();
    }
}
