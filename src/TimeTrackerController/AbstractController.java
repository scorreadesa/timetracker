package TimeTrackerController;


public abstract class AbstractController {

    protected Controller controller_;

    public AbstractController(){

    }

    public void registerController(Controller controller){
        controller_ = controller;
    }
}
