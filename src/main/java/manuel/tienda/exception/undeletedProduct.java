package manuel.tienda.exception;

public class undeletedProduct extends RuntimeException {
    public undeletedProduct(Long id) {
        super("No se ha podido borrar el producto.. " + id);
    }
}
