package manuel.tienda.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app") // lee todas las propiedades que empiezan con app.
public class ConfigurationParameters {

    private String nombre;
    private String pais;
    private String author;
    private String lengua; // coincide con tu properties: app.legua

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLengua() {
        return lengua;
    }

    public void setLengua(String lengua) {
        this.lengua = lengua;
    }

    @Override
    public String toString() {
        return "ConfigurationParameters{" +
                "nombre='" + nombre + '\'' +
                ", pais='" + pais + '\'' +
                ", author='" + author + '\'' +
                ", lengua='" + lengua + '\'' +
                '}';
    }
}
