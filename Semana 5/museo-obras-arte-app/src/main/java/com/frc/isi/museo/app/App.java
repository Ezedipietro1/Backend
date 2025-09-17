package com.frc.isi.museo.app;

import java.net.URL;

import com.frc.isi.museo.menu.ApplicationContext;
import com.frc.isi.museo.menu.ItemMenu;
import com.frc.isi.museo.menu.Menu;

public class App {
    public static void main(String[] args) {
        var ctx = ApplicationContext.getInstance();
        Menu menu = new Menu();
        menu.setTitulo("\nMenu de Opciones para Museo");

        Acciones acciones = new Acciones();

        URL folderPath = App.class.getResource("/files");
        ctx.put("path", folderPath);

        //Listado de opciones:
        menu.addOpcion(new ItemMenu(1, "Cargar Obras Arstisticas", acciones::importarObras));
        //menu.addOpcion(new ItemMenu(0, "Salir", p -> System.exit(1)));

        menu.ejecutar(ctx);
    }
}
