package modelo;

import interfaces.Cancelable;
import interfaces.Despachable;

public abstract class Pedido implements Cancelable, Despachable {


    protected int idPedido;
    protected String cliente;
    protected String direccionEntrega;
    protected String repartidorAsignado;
    protected double tiempoEntregaEstimado;
    protected EstadoPedido estadoPedido;


    public Pedido(int idPedido, String cliente, String direccionEntrega) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.direccionEntrega = direccionEntrega;
        this.repartidorAsignado = "Sin asignar";
        this.estadoPedido = EstadoPedido.PENDIENTE;
    }

    public abstract void calcularTiempoEntrega();

    public void calcularTiempoEntrega(boolean muchoTrafico) {
        calcularTiempoEntrega();
        if (muchoTrafico) {
            tiempoEntregaEstimado *= 1.4;
            System.out.println("(Se aplicó una tarifa adicional por exceso de tráfico: +40%)");
        }
    }

    public abstract void asignarRepartidor();


    public abstract String getTipo();


    public void asignarRepartidor(String nombreRepartidor) {
        this.repartidorAsignado = nombreRepartidor;
        System.out.println("   Repartidor asignado manualmente: " + nombreRepartidor);
    }


    public void mostrarResumen() {
        System.out.println("---- Resumen del pedido #" + idPedido + " ----");
        System.out.println("Cliente: " + cliente);
        System.out.println("Dirección: " + direccionEntrega);
        System.out.println("Repartidor: " + repartidorAsignado);
        System.out.println("Tiempo estimado: " + String.format("%.2f", tiempoEntregaEstimado) + " min");
        System.out.println("Estado: " + estadoPedido);
    }


    @Override
    public void despachar() {
        if (estadoPedido == EstadoPedido.CANCELADO) {
            System.out.println("No se puede despachar: el pedido #" + idPedido + " está cancelado.");
            return;
        }
        estadoPedido = EstadoPedido.ENTREGADO;
        System.out.println("Pedido #" + idPedido + " despachado con " + repartidorAsignado + ".");
    }

    @Override
    public void cancelar() {
        estadoPedido = EstadoPedido.CANCELADO;
        System.out.println("   Pedido #" + idPedido + " ha sido cancelado.");
    }


    // Se apoya en el enum EstadoPedido para no aceptar estados que no existan.
    public void setEstado(String nuevoEstado) {
        this.estadoPedido = EstadoPedido.valueOf(nuevoEstado);
    }

    // Getters usados por el controlador y por Main
    public int getIdPedido() {
        return idPedido;
    }

    // Lo usa PedidoDAO para guardar el id que MySQL genera automáticamente (AUTO_INCREMENT)
    // apenas se inserta el pedido, ya que antes de guardarlo todavía no tiene un id real.
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public String getCliente() {
        return cliente;
    }

    public String getRepartidorAsignado() {
        return repartidorAsignado;
    }

    public String getEstado() {
        return estadoPedido.name();
    }

    public double getTiempoEntregaEstimado() {
        return tiempoEntregaEstimado;
    }

}
