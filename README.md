# M08 UF3 – Minijoc amb libGDX

**Alumne:** David Sánchez Matilla
**Professor:** Enric Mieza – Institut Esteve Terradas i Illa

Minijoc 2D fet amb [libGDX](https://libgdx.com/) per a Android i escriptori, seguint l'**opció 1** de l'enunciat: un personatge que es mou per la pantalla i canvia de pantalla quan arriba al límit.

## El joc

- **8 pantalles (sales)** connectades en una quadrícula de 4x2. Quan el personatge surt per l'obertura d'una paret, passa a la sala del costat i apareix pel costat contrari.
- **3 claus** a les sales 3, 5 i 7. S'agafen en tocar-les.
- **Porta final** a la sala 8: només s'obre amb les 3 claus. Si en falten, surt l'avís "Necessites 3 claus". En obrir-la, apareix la pantalla final amb el temps de la partida.
- **Personatge animat en 8 direccions** amb un spritesheet de 8x8 frames.

```
[1] - [2] - [3] - [4]
 |           |     |
[5] - [6] - [7] - [8]   (porta)

Claus: sales 3, 5 i 7
```

## Controls

| Plataforma | Control |
| --- | --- |
| Ordinador | Fletxes o WASD. ENTER o clic per passar el menú i la pantalla final. |
| Mòbil | Tocar el terç esquerre, dret, superior o inferior de la pantalla. Les cantonades mouen en diagonal. |

## Estructura del codi

Tot el codi del joc és al mòdul `core` (`core/src/main/java/cat/esteveterradas/joc/`):

| Classe | Funció |
| --- | --- |
| `Main` | `Game` principal. Guarda el `SpriteBatch`, la `BitmapFont` i el `FitViewport(8, 5)` compartits. |
| `MainMenuScreen` | Pantalla d'inici amb les instruccions. |
| `GameScreen` | Partida: `input()` → `logic()` → `draw()`, canvi de sala, claus, porta i HUD. |
| `GameOverScreen` | Pantalla de victòria amb el temps. |
| `Jugador` | Retalla l'spritesheet amb `TextureRegion`, crea una `Animation` per direcció i gestiona el moviment amb col·lisions. |
| `Sala` | Parets i obstacles (`Rectangle`), clau i porta. |

### Conceptes dels apunts aplicats

- Estructura `Game` + `Screen`, com al tutorial *Drop* ampliat.
- `FitViewport` de 8x5 unitats i `resize()` a cada pantalla.
- Animació amb `Animation<TextureRegion>` a partir d'un spritesheet.
- Col·lisions amb `Rectangle.overlaps`.
- Objectes dibuixats amb `Pixmap`: terra, parets, claus i porta.
- Joystick virtual: pantalla dividida en terços, amb `viewport.unproject` i `Rectangle.contains`, fins a 10 dits.

## Com executar-lo

1. Obrir la carpeta del projecte amb **Android Studio** i esperar que acabi el *Gradle Sync*.
2. **Escriptori:** executar `Lwjgl3Launcher` (mòdul `lwjgl3`) o, des de la terminal, `gradlew lwjgl3:run`.
3. **Android:** triar la configuració `android`, connectar un mòbil amb la depuració USB activada (o un emulador) i prémer ▶.

Provat a Windows (escriptori) i en un Xiaomi Redmi Note 11.

## Recursos

- Projecte generat amb [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).
- Spritesheet del personatge: `assets/personaje.png`, aprovat pel professor abans de començar.
