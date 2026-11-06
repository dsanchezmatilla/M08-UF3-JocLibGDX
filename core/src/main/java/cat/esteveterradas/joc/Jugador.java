package cat.esteveterradas.joc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Personaje controlado por el jugador.
 * El spritesheet tiene 8 filas (una por dirección) y 8 columnas (frames de caminar),
 * cada frame mide 60x110 píxeles.
 */
public class Jugador {

    // Tamaño de un frame dentro de la imagen (en píxeles)
    static final int FRAME_W = 60;
    static final int FRAME_H = 110;
    static final int COLUMNES = 8;

    // Tamaño del personaje en el mundo (unidades del viewport 8x5)
    static final float ALT = 1f;
    static final float AMPLE = ALT * FRAME_W / FRAME_H;
    static final float VELOCITAT = 2f; // unidades por segundo

    // Direcciones del juego -> fila del spritesheet.
    // Si alguna dirección se ve girada, basta con cambiar el número de fila aquí.
    static final int FILA_AVALL = 1;
    static final int FILA_AMUNT = 6;
    static final int FILA_ESQUERRA = 3;
    static final int FILA_DRETA = 4;
    static final int FILA_AVALL_ESQUERRA = 2;
    static final int FILA_AVALL_DRETA = 0;
    static final int FILA_AMUNT_ESQUERRA = 5;
    static final int FILA_AMUNT_DRETA = 7;

    // Una animación por fila del spritesheet
    Animation<TextureRegion>[] animacions;
    float stateTime = 0;
    int fila = FILA_AVALL;
    boolean caminant = false;

    // Posición (esquina inferior izquierda del dibujo)
    Vector2 posicio = new Vector2();
    // Rectángulo de colisión: solo los pies, para que pueda pasar "por detrás" de las cosas
    Rectangle peus = new Rectangle();

    @SuppressWarnings("unchecked")
    public Jugador(Texture sheet, float x, float y) {
        animacions = new Animation[8];
        for (int fila = 0; fila < 8; fila++) {
            TextureRegion[] frames = new TextureRegion[COLUMNES];
            for (int col = 0; col < COLUMNES; col++) {
                frames[col] = new TextureRegion(sheet, col * FRAME_W, fila * FRAME_H, FRAME_W, FRAME_H);
            }
            animacions[fila] = new Animation<TextureRegion>(0.1f, frames);
        }
        posicio.set(x, y);
        actualitzaPeus();
    }

    /**
     * Mueve el personaje según la dirección (dx, dy entre -1 y 1).
     * Primero se mueve en X y luego en Y; si choca con una pared se deshace ese eje.
     */
    public void moure(int dx, int dy, float delta, Sala sala) {
        caminant = (dx != 0 || dy != 0);
        if (!caminant) return;

        fila = filaPerDireccio(dx, dy);
        stateTime += delta;

        Vector2 vel = new Vector2(dx, dy).nor().scl(VELOCITAT * delta);

        posicio.x += vel.x;
        actualitzaPeus();
        if (sala.xocaAmbParet(peus)) {
            posicio.x -= vel.x;
            actualitzaPeus();
        }

        posicio.y += vel.y;
        actualitzaPeus();
        if (sala.xocaAmbParet(peus)) {
            posicio.y -= vel.y;
            actualitzaPeus();
        }
    }

    int filaPerDireccio(int dx, int dy) {
        if (dx == 0 && dy < 0) return FILA_AVALL;
        if (dx == 0 && dy > 0) return FILA_AMUNT;
        if (dx < 0 && dy == 0) return FILA_ESQUERRA;
        if (dx > 0 && dy == 0) return FILA_DRETA;
        if (dx < 0 && dy < 0) return FILA_AVALL_ESQUERRA;
        if (dx > 0 && dy < 0) return FILA_AVALL_DRETA;
        if (dx < 0) return FILA_AMUNT_ESQUERRA;
        return FILA_AMUNT_DRETA;
    }

    public void actualitzaPeus() {
        peus.set(posicio.x + AMPLE * 0.2f, posicio.y, AMPLE * 0.6f, ALT * 0.25f);
    }

    /** Centro de los pies, para saber si ha salido por un borde de la pantalla. */
    public float centreX() { return peus.x + peus.width / 2; }
    public float centreY() { return peus.y + peus.height / 2; }

    public void draw(SpriteBatch batch) {
        TextureRegion frame;
        if (caminant) {
            frame = animacions[fila].getKeyFrame(stateTime, true);
        } else {
            frame = animacions[fila].getKeyFrame(0); // quieto: primer frame
        }
        batch.draw(frame, posicio.x, posicio.y, AMPLE, ALT);
    }
}
