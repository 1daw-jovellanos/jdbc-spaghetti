package aplicacion;

import java.util.*;
import java.sql.*;

public class AppEmpleados {

    Scanner in;

    public AppEmpleados() {
        in = new Scanner(System.in);
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Oh cielos, no se que puñetas pasa con el Driver");
        }
    }

    void menuAnnadirEmpleado() {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "")) {
            System.out.print("Nombre: ");
            String nombre = in.nextLine();
            System.out.print("Apellido 1: ");
            String ape1 = in.nextLine();
            System.out.print("Apellido 2: ");
            String ape2 = in.nextLine();
            System.out.print("Ciudad: ");
            String ciudad = in.nextLine();
            System.out.print("Salario: ");
            double salario = Double.parseDouble(in.nextLine());
            String sql
                    = "INSERT INTO empleados(nombre, apellido1, apellido2, ciudad, salario) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, nombre);
                stmt.setString(2, ape1);
                stmt.setString(3, ape2);
                stmt.setString(4, ciudad);
                stmt.setDouble(5, salario);

                int cantidadFilas = stmt.executeUpdate();
                System.out.format("Filas afectadas %d%n", cantidadFilas);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void menuListarEmpleados() {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "")) {

            String sql = "SELECT nombre, apellido1, apellido2, ciudad, salario FROM empleados ";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {// CABECERA
                    System.out.format("%-15s%-15s%-15s%-20s%8s%n", "NOMBRE", "APELLIDO1",
                            "APELLIDO2", "CIUDAD", "SALARIO");
                    // Línea de guiones
                    for (int i = 0; i < (15 + 15 + 15 + 20 + 8); i++) {
                        System.out.print('-');
                    }
                    System.out.println();
                    // Imprimir Datos del resultSet tabulados
                    while (rs.next()) {
                        String nombre = rs.getString("nombre");
                        String ape1 = rs.getString("apellido1");
                        String ape2 = rs.getString("apellido2");
                        String ciudad = rs.getString("ciudad");
                        double salario = rs.getDouble("salario");
                        System.out.format("%-15s%-15s%-15s%-20s%8.2f%n", nombre, ape1,
                                ape2, ciudad, salario);
                    }
                }
            }
            // Línea en blanco, para separar un poco
            System.out.println();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void menuBorrarEmpleados() {
        String cadenaBusqueda;
        try (Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "")) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT count(*) cuenta FROM empleados "
                    + "WHERE lower(apellido1) LIKE ? OR lower(apellido2) LIKE ?"
            )) {
                System.out.print("Cadena de búsqueda: ");
                cadenaBusqueda = in.nextLine();
                stmt.setString(1, ("%" + cadenaBusqueda + "%").toLowerCase());
                stmt.setString(2, ("%" + cadenaBusqueda + "%").toLowerCase());
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    int cantidadEmpleados = rs.getInt("cuenta");
                    System.out.format("Se van a borrar %d empleados. ¿Estás seguro [s/N]?: ",
                            cantidadEmpleados);
                }
            }

            String respuesta = in.nextLine();
            if (respuesta.toLowerCase().equals("s")) {
                // borrar de verdad
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM empleados "
                        + "WHERE lower(apellido1) LIKE ? OR lower(apellido2) LIKE ?"
                )) {
                    stmt.setString(1, ("%" + cadenaBusqueda + "%").toLowerCase());
                    stmt.setString(2, ("%" + cadenaBusqueda + "%").toLowerCase());
                    int cantidadFilasAfectadas = stmt.executeUpdate();
                    System.out.format("Se borraron %d empleados. %n", cantidadFilasAfectadas);
                }
            } else {
                System.out.format("Operación cancelada.%n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void run() throws SQLException {
        int opcion;
        do {
            System.out.format(
                    "1.- Añadir Empleado.\n"
                    + "2.- Listar todos los empleados.\n"
                    + "3.- Borrar por apellido\n"
                    + "0.- Salir\n"
                    + "  -->"
            );
            opcion = Integer.parseInt(in.nextLine());
            switch (opcion) {
                case 1:
                    menuAnnadirEmpleado();
                    break;
                case 2:
                    menuListarEmpleados();
                    break;
                case 3:
                    menuBorrarEmpleados();
                    break;
            }
        } while (opcion != 0);
    }

    public static void main(String[] args) throws SQLException {
        new AppEmpleados().run();
    }

}
