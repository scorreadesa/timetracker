package Factories;

import javafx.fxml.FXMLLoader;
import java.util.TreeMap;

public class LoaderFactory {

    private TreeMap<String, FXMLLoader> controllers_;

    public LoaderFactory(){

        controllers_ = new TreeMap<>();
    }

    public void register(String path){

        String name = path.substring(path.lastIndexOf("/") + 1, path.indexOf("."));
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        controllers_.put(name, loader);
    }

    public FXMLLoader get(String view) throws NullPointerException{
        return controllers_.get(view);
    }

    @Override
    public String toString() {
        StringBuilder list = new StringBuilder();

        for(String view : this.controllers_.keySet()){
            list.append(view).append("\n");
        }

        return list.toString();
    }
}
