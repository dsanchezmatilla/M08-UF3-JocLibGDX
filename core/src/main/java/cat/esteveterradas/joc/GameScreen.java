package cat.esteveterradas.joc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Pantalla de juego. Como en el tutorial Drop: render() = input() + logic() + draw().
 *
 * Mapa de salas (4 columnas x 2 filas):
 *
 *   [1] - [2] - [3] - [4]
 *    |           |     |
 *   [5] - [6] - [7] - [8]  (puerta)
 *
 * Llaves en las salas 3, 5 y 7.
 */
public class GameScreen implements Screen {

    static final int COLUMNES = 4;
    static final int FILES = 2;
    static final int TOTAL_CLAUS = 3;

    // Conexiones entre salas (índice 0..7). "dreta[i]" = la sala i conecta con la i+1.
    static final boolean[] CONNEXIO_DRETA = {true, true, true, false, true, true, true, false};
    // "avall[i]" = la sala i (fila de arriba) conecta con la i+4.
    static final boolean[] CONNEXIO_AVALL = {true, false, true, true};

    final Main game;

    Texture sheet;
    Texture pixel;
    Texture texClau;
    Texture texPorta;

    Sala[] sales;
    int salaActual = 0;
    Jugador jugador;
    int claus = 0;
    float temps = 0;

    String missatge = "";
    float tempsMissatge = 0;
    boolean acabat = false;

    // Joystick virtual: la pantalla dividida en tercios (apuntes "Controls d'entrada")
    Rectangle zonaEsquerra, zonaDreta, zonaAmunt, zonaAvall;
    Vector2 touchPos = new Vector2();

    public GameScreen(final Main game) {
        this.game = game;

        sheet = new Texture("personaje.png");
        crearTextures();
        crearSales();

        jugador = new Jugador(sheet, 3.7f, 2f);

        float w = game.viewport.getWorldWidth();
        float h = game.viewport.getWorldHeight();
        zonaEsquerra = new Rectangle(0, 0, w / 3, h);
        zonaDreta = new Rectangle(w * 2 / 3, 0, w / 3, h);
        zonaAvall = new Rectangle(0, 0, w, h / 3);
        zonaAmunt = new Rectangle(0, h * 2 / 3, w, h / 3);
    }

    /** Texturas dibujadas con Pixmap (apuntes "Matemàtiques i objectes"). */
    void crearTextures() {
        // Píxel blanco: se tiñe con batch.setColor para el suelo y las paredes
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();

        // Llave dorada
        pm = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pm.setColor(Color.GOLD);
        pm.fillCircle(9, 9, 8);
        pm.fillRectangle(14, 14, 16, 5);      // barra
        pm.fillRectangle(24, 18, 4, 8);       // diente
        pm.setBlending(Pixmap.Blending.None); // para poder "borrar" con color transparente
        pm.setColor(0, 0, 0, 0);
        pm.fillCircle(9, 9, 3);               // agujero
        texClau = new Texture(pm);
        pm.dispose();

        // Puerta marrón con pomo
        pm = new Pixmap(32, 80, Pixmap.Format.RGBA8888);
        pm.setColor(0.45f, 0.25f, 0.1f, 1f);
        pm.fill();
        pm.setColor(0.3f, 0.15f, 0.05f, 1f);
        pm.drawRectangle(0, 0, 32, 80);
        pm.drawRectangle(4, 6, 24, 30);
        pm.drawRectangle(4, 44, 24, 30);
        pm.setColor(Color.GOLD);
        pm.fillCircle(8, 40, 3);
        texPorta = new Texture(pm);
        pm.dispose();
    }

    void crearSales() {
        Color[] colors = {
            new Color(0.55f, 0.75f, 0.55f, 1f), new Color(0.75f, 0.7f, 0.5f, 1f),
            new Color(0.5f, 0.65f, 0.8f, 1f),  new Color(0.8f, 0.6f, 0.6f, 1f),
            new Color(0.65f, 0.55f, 0.8f, 1f), new Color(0.6f, 0.8f, 0.75f, 1f),
            new Color(0.8f, 0.75f, 0.45f, 1f), new Color(0.6f, 0.6f, 0.6f, 1f)
        };

        sales = new Sala[COLUMNES * FILES];
        for (int i = 0; i < sales.length; i++) {
            int col = i % COLUMNES;
            int fila = i / COLUMNES; // 0 = fila de arriba
            boolean dreta = CONNEXIO_DRETA[i];
            boolean esquerra = col > 0 && CONNEXIO_DRETA[i - 1];
            boolean avall = fila == 0 && CONNEXIO_AVALL[col];
            boolean amunt = fila == 1 && CONNEXIO_AVALL[col];
            sales[i] = new Sala(i + 1, colors[i], amunt, avall, esquerra, dreta);
        }

        // Obstáculos (siempre fuera de los pasillos centrales para no tapar salidas)
        sales[0].afegeixObstacle(1.0f, 3.6f, 1.2f, 0.5f);
        sales[0].afegeixObstacle(5.8f, 0.7f, 1.2f, 0.5f);

        sales[1].afegeixObstacle(1.0f, 0.7f, 0.6f, 0.8f);
        sales[1].afegeixObstacle(6.2f, 3.5f, 0.6f, 0.8f);

        sales[2].afegeixObstacle(1.2f, 3.5f, 1.6f, 0.4f);
        sales[2].afegeixObstacle(5.2f, 0.7f, 1.6f, 0.4f);
        sales[2].posaClau(6.6f, 4.0f);

        sales[3].afegeixObstacle(1.0f, 1.0f, 0.5f, 0.5f);
        sales[3].afegeixObstacle(2.0f, 3.7f, 0.5f, 0.5f);
        sales[3].afegeixObstacle(5.5f, 0.8f, 0.5f, 0.5f);
        sales[3].afegeixObstacle(6.5f, 3.7f, 0.5f, 0.5f);

        sales[4].afegeixObstacle(2.0f, 3.5f, 0.4f, 1.0f);
        sales[4].afegeixObstacle(5.5f, 0.6f, 1.5f, 0.4f);
        sales[4].posaClau(1.0f, 0.7f);

        sales[5].afegeixObstacle(1.0f, 3.6f, 2.0f, 0.4f);
        sales[5].afegeixObstacle(5.0f, 0.7f, 2.0f, 0.4f);

        sales[6].afegeixObstacle(5.5f, 0.5f, 0.4f, 1.2f);
        sales[6].afegeixObstacle(1.0f, 3.5f, 1.0f, 0.6f);
        sales[6].posaClau(7.0f, 0.6f);

        sales[7].afegeixObstacle(1.0f, 0.7f, 1.0f, 0.5f);
        sales[7].afegeixObstacle(5.0f, 3.7f, 1.0f, 0.5f);
        sales[7].posaPorta(7.6f, 2.0f);
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);
        if (acabat) return; // ya hemos cambiado de pantalla y liberado las texturas
        draw();
    }

    void input(float delta) {
        int dx = 0;
        int dy = 0;

        // Teclado (polling)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) dx = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) dx = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) dy = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) dy = -1;

        // Pantalla táctil: hasta 10 dedos, cada toque se pasa a coordenadas del mundo
        for (int i = 0; i < 10; i++) {
            if (Gdx.input.isTouched(i)) {
                touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i));
                game.viewport.unproject(touchPos);
                if (zonaEsquerra.contains(touchPos)) dx = -1;
                if (zonaDreta.contains(touchPos)) dx = 1;
                if (zonaAmunt.contains(touchPos)) dy = 1;
                if (zonaAvall.contains(touchPos)) dy = -1;
            }
        }

        jugador.moure(dx, dy, delta, sales[salaActual]);
    }

    void logic(float delta) {
        temps += delta;
        if (tempsMissatge > 0) tempsMissatge -= delta;

        Sala sala = sales[salaActual];

        // Recoger la llave al tocarla
        if (sala.teClauPerRecollir() && sala.clau.overlaps(jugador.peus)) {
            sala.clauRecollida = true;
            claus++;
            mostraMissatge("Clau recollida! (" + claus + "/" + TOTAL_CLAUS + ")");
        }

        // Puerta final
        if (sala.porta != null && sala.porta.overlaps(jugador.peus)) {
            if (claus == TOTAL_CLAUS) {
                acabat = true;
                game.setScreen(new GameOverScreen(game, temps));
                dispose();
                return;
            } else {
                mostraMissatge("Necessites " + TOTAL_CLAUS + " claus! Nomes en tens " + claus);
            }
        }

        canviDeSala();
    }

    /** Si el jugador sale por un borde, pasa a la sala vecina y aparece por el lado contrario. */
    void canviDeSala() {
        float x = jugador.centreX();
        float y = jugador.centreY();
        float w = game.viewport.getWorldWidth();
        float h = game.viewport.getWorldHeight();
        float marge = Sala.GRUIX + 0.1f;

        if (x < 0) {
            salaActual -= 1;
            jugador.posicio.x = w - marge - Jugador.AMPLE / 2;
        } else if (x > w) {
            salaActual += 1;
            jugador.posicio.x = marge - Jugador.AMPLE / 2;
        } else if (y > h) {
            salaActual -= COLUMNES;
            jugador.posicio.y = marge - jugador.peus.height / 2;
        } else if (y < 0) {
            salaActual += COLUMNES;
            jugador.posicio.y = h - marge - jugador.peus.height / 2;
        } else {
            return;
        }
        jugador.actualitzaPeus();
    }

    void mostraMissatge(String text) {
        missatge = text;
        tempsMissatge = 2f;
    }

    void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        float w = game.viewport.getWorldWidth();
        float h = game.viewport.getWorldHeight();

        game.batch.begin();
        sales[salaActual].draw(game.batch, pixel, texClau, texPorta);
        jugador.draw(game.batch);

        // HUD sobre la pared de arriba
        game.font.draw(game.batch, "Sala " + (salaActual + 1) + "/" + sales.length, 0.3f, h - 0.06f);
        game.font.draw(game.batch, "Claus: " + claus + "/" + TOTAL_CLAUS, w - 1.3f, h - 0.06f);
        if (tempsMissatge > 0) {
            game.font.draw(game.batch, missatge, 2.5f, 0.17f);
        }
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        sheet.dispose();
        pixel.dispose();
        texClau.dispose();
        texPorta.dispose();
    }

    @Override public void show() { }
    @Override public void hide() { }
    @Override public void pause() { }
    @Override public void resume() { }
}
