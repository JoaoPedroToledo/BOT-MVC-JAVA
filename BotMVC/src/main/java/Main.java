
public class Main {


    private static Model model;

    public static void main(String[] args) throws Exception {

        model = Model.getInstance();

        View view = new View(model);
        model.registerObserver(view);
        view.receiveUsersMessages();
        
        
        
    }

}