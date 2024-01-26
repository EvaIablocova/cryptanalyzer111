package com.javarush.cryptanalyzerGirls.iablocova;

import com.javarush.cryptanalyzerGirls.iablocova.app.Application;
import com.javarush.cryptanalyzerGirls.iablocova.controller.MainController;
import com.javarush.cryptanalyzerGirls.iablocova.entity.Result;
import com.javarush.cryptanalyzerGirls.iablocova.view.ConsoleView;
import com.javarush.cryptanalyzerGirls.iablocova.view.GUIView;
import com.javarush.cryptanalyzerGirls.iablocova.view.View;


public class EntryPoint {
    public static void main (String[] args){
            //спрашивать в начале какой view -> создаем view
            View view = new GUIView();
            MainController mainController = new MainController(view);
            Application application = new Application(mainController);
/*
        boolean t = true;
        while (t) {
            Result result = application.run();
            application.printResult(result);

            t = view.repeat();
        }
 */
    }
}