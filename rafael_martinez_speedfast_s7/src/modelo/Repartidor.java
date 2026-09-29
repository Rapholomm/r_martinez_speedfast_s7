package modelo;

public class Repartidor {

    private int id;
    private final String nombre;

    public Repartidor(String nombre) {
        this.nombre = nombre;
    }

    public Repartidor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre; // así se ve bien dentro de un JComboBox o JOptionPane
    }
}