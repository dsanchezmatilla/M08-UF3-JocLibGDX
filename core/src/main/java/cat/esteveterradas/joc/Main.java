package cat.esteveterradas.joc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Clase principal del juego (igual que "Drop" del tutorial extendido).
 * Guarda los objetos compartidos por todas las pantallas.
 */
public class Main extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Mundo virtual de 8x5 unidades (apuntes libgdx_viewport)
        viewport = new FitViewport(8, 5);

        // La fuente por defecto se escala a las unidades del viewport
        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // imprescindible para que se dibuje la Screen activa
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
