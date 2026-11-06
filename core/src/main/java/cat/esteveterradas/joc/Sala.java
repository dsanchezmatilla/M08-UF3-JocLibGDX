package cat.esteveterradas.joc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Una sala (pantalla) del juego: suelo, paredes con huecos hacia las salas
 * vecinas, obstáculos y, opcionalmente, una llave o la puerta final.
 */
public class Sala {

    // Medidas del mundo (iguales al FitViewport de Main)
    static final float W = 8f;
    static final float H = 5f;
    static final float GRUIX = 0.25f;  // grosor de las paredes
    static final float FORAT = 1.4f;   // ancho del hueco de salida

    int numero;
    Color colorTerra;
    Array<Rectangle> parets = new Array<Rectangle>();

    Rectangle clau;          // null si la sala no tiene llave
    boolean clauRecollida = false;
    Rectangle porta;         // null si la sala no tiene puerta

    public Sala(int numero, Color colorTerra,
                boolean sortidaAmunt, boolean sortidaAvall,
                boolean sortidaEsquerra, boolean sortidaDreta) {
        this.numero = numero;
        this.colorTerra = colorTerra;

        float meitatX = (W - FORAT) / 2;  // trozo de pared a cada lado del hueco (horizontal)
        float meitatY = (H - FORAT) / 2;  // trozo de pared a cada lado del hueco (vertical)

        // Pared de arriba
        if (sortidaAmunt) {
            parets.add(new Rectangle(0, H - GRUIX, meitatX, GRUIX));
            parets.add(new Rectangle(W - meitatX, H - GRUIX, meitatX, GRUIX));
        } else {
            parets.add(new Rectangle(0, H - GRUIX, W, GRUIX));
        }
        // Pared de abajo
        if (sortidaAvall) {
            parets.add(new Rectangle(0, 0, meitatX, GRUIX));
            parets.add(new Rectangle(W - meitatX, 0, meitatX, GRUIX));
        } else {
            parets.add(new Rectangle(0, 0, W, GRUIX));
        }
        // Pared izquierda
        if (sortidaEsquerra) {
            parets.add(new Rectangle(0, 0, GRUIX, meitatY));
            parets.add(new Rectangle(0, H - meitatY, GRUIX, meitatY));
        } else {
            parets.add(new Rectangle(0, 0, GRUIX, H));
        }
        // Pared derecha
        if (sortidaDreta) {
            parets.add(new Rectangle(W - GRUIX, 0, GRUIX, meitatY));
            parets.add(new Rectangle(W - GRUIX, H - meitatY, GRUIX, meitatY));
        } else {
            parets.add(new Rectangle(W - GRUIX, 0, GRUIX, H));
        }
    }

    public void afegeixObstacle(float x, float y, float w, float h) {
        parets.add(new Rectangle(x, y, w, h));
    }

    public void posaClau(float x, float y) {
        clau = new Rectangle(x, y, 0.4f, 0.4f);
    }

    public void posaPorta(float x, float y) {
        porta = new Rectangle(x, y, 0.4f, 1f);
    }

    public boolean xocaAmbParet(Rectangle r) {
        for (Rectangle paret : parets) {
            if (paret.overlaps(r)) return true;
        }
        return false;
    }

    public boolean teClauPerRecollir() {
        return clau != null && !clauRecollida;
    }

    /** Dibuja la sala. "pixel" es una textura blanca 1x1 que se tiñe con batch.setColor. */
    public void draw(SpriteBatch batch, Texture pixel, Texture texClau, Texture texPorta) {
        // Suelo
        batch.setColor(colorTerra);
        batch.draw(pixel, 0, 0, W, H);
        // Paredes y obstáculos
        batch.setColor(0.25f, 0.2f, 0.2f, 1f);
        for (Rectangle p : parets) {
            batch.draw(pixel, p.x, p.y, p.width, p.height);
        }
        batch.setColor(Color.WHITE);

        if (teClauPerRecollir()) {
            batch.draw(texClau, clau.x, clau.y, clau.width, clau.height);
        }
        if (porta != null) {
            batch.draw(texPorta, porta.x, porta.y, porta.width, porta.height);
        }
    }
}
