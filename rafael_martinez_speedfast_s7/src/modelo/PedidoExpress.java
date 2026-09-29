package modelo;

public class PedidoExpress extends Pedido{

    private double distanciaKm;

    public PedidoExpress(int idPedido, String cliente, String direccionEntrega, double distanciaKm) {
        super(idPedido, cliente, direccionEntrega);
        this.distanciaKm = distanciaKm;
    }

    @Override
    public void calcularTiempoEntrega() {
        // 2 min/km
        tiempoEntregaEstimado = distanciaKm * 2;
    }

    @Override
    public void asignarRepartidor() {
        repartidorAsignado = "Repartidor prioritario más cercano (asignación automatica)";
        System.out.println("   [Express] Se asignó automáticamente el repartidor prioritario más cercano.");
    }

    @Override
    public String getTipo() {
        return "Express";
    }
}

