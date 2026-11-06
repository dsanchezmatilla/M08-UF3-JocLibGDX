package cat.esteveterradas.joc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

/** Pantalla de inicio: título e instrucciones. Al tocar empieza la partida. */
public class MainMenuScreen implements Screen {

    final Main game;

    public MainMenuScreen(final Main game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.font.draw(game.batch, "LA CASA DE LES 8 SALES", 1.5f, 4f);
        game.font.draw(game.batch, "Recull les 3 claus i obre la porta de la sala 8.", 1f, 3f);
        game.font.draw(game.batch, "Ordinador: fletxes / WASD", 1f, 2.5f);
        game.font.draw(game.batch, "Mobil: toca la pantalla cap on vulguis anar", 1f, 2.1f);
        game.font.draw(game.batch, "Toca la pantalla (o ENTER) per comencar", 1.5f, 1f);
        game.batch.end();

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override public void show() { }
    @Override public void hide() { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void dispose() { }
}
