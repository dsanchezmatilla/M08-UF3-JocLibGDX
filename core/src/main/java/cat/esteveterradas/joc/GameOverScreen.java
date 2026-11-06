package cat.esteveterradas.joc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

/** Pantalla final: se muestra al abrir la puerta con las 3 llaves. */
public class GameOverScreen implements Screen {

    final Main game;
    final float temps;

    public GameOverScreen(final Main game, float temps) {
        this.game = game;
        this.temps = temps;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.3f, 0.1f, 1f);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.font.draw(game.batch, "HAS OBERT LA PORTA!  HAS GUANYAT!", 1.5f, 3.5f);
        game.font.draw(game.batch, "Temps: " + (int) temps + " segons", 2.5f, 2.7f);
        game.font.draw(game.batch, "Toca la pantalla (o ENTER) per tornar al menu", 1.2f, 1.5f);
        game.batch.end();

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MainMenuScreen(game));
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
