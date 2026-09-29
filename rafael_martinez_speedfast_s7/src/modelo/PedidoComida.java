package modelo;

public class PedidoComida extends Pedido{

    private double distanciaKm;
    private boolean requierePreparacion;



    public PedidoComida(int idPedido, String cliente, String direccionEntrega, double distanciaKm, boolean requierePreparacion) {
        super(idPedido, cliente, direccionEntrega);
        this.distanciaKm = distanciaKm;
        this.requierePreparacion = requierePreparacion;
    }

    @Override
    public void calcularTiempoEntrega() {
        // Base: 4 min/km en moto, + 10 min si el aun se prepara
        tiempoEntregaEstimado = distanciaKm * 4;
        if (requierePreparacion) {
            tiempoEntregaEstimado += 10;
        }
    }

    @Override
    public void asignarRepartidor() {
        repartidorAsignado = "En moto (asignación automatica)";
        System.out.println("[Comida] Se asignó automáticamente un repartidor en moto.");
    }

    @Override
    public String getTipo() {
        return "Comida";
    }
}
