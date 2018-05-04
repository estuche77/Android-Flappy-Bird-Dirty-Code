package cr.ac.itcr.andreifuentes.flappybirdclase;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	Texture background;
	Texture topTube;
	Texture bottomTube;
	Texture[] birds;
	Texture gameOver;
	int birdState;
	float birdRotation;
	float gap;
	float birdY;
	float velocity;
	float gravity;
	int numberOfPipes = 4;
	float pipeX[] = new float[numberOfPipes];
	float pipeYOffset[] = new float[numberOfPipes];
	float distance;
	float pipeVelocity = 5;
	Random random;
	float maxLine;
	float minLine;
	int score;
	int pipeActivo;
	BitmapFont font;
	int game_state;
	int birdSize;

	Music music;
	Sound dieSound;
	Sound jumpSound;

	Circle birdCircle;
	Rectangle[] topPipes;
	Rectangle[] bottomPipes;

	@Override
	public void create () {

		music = Gdx.audio.newMusic(Gdx.files.internal("mario-sountrack.mp3"));

		music.setVolume(0.5f);
		music.setLooping(true);

		dieSound = Gdx.audio.newSound(Gdx.files.internal("mario-die.wav"));
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("mario-jump.mp3"));

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		gameOver = new Texture("gameOverOriginal.png");

		birdCircle = new Circle();
		topPipes = new Rectangle[numberOfPipes];
		bottomPipes = new Rectangle[numberOfPipes];

		birdRotation = 0;

		birdState = 0;
		game_state = 0;
		gap = 500;
		velocity = 0;
		gravity = 0.5f;
		random = new Random();
		distance = Gdx.graphics.getWidth() * 3/5;
		maxLine = Gdx.graphics.getHeight()* 3/4;
		minLine = Gdx.graphics.getHeight()* 1/4;
		score = 0;
		pipeActivo = 0;
		birdSize = 0;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - birds[birdState].getHeight()/2;
		for (int i = 0; i<numberOfPipes; i++){
			pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
			pipeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth() + Gdx.graphics.getWidth() + distance*i;

			// inicializamos cada uno de los Shapes
			topPipes[i] = new Rectangle();
			bottomPipes[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		// no iniciado
		if (game_state == 0){

			float width = Gdx.graphics.getWidth() / 3;
			float length = (Gdx.graphics.getWidth() - 200) / 3;

			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.rect(50,300,length,300);
			shapeRenderer.setColor(Color.YELLOW);
			shapeRenderer.rect(width*1+50,300,length,300);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(width*2+50,300,length,300);
			shapeRenderer.end();

			if (Gdx.input.justTouched()){

				if (Gdx.input.getX() > 50 && Gdx.input.getX() < 50 + 300) {
					gravity = 0.3f;
					gap = 1000;
					maxLine = Gdx.graphics.getHeight()* 4/10;
					minLine = Gdx.graphics.getHeight()* 6/10;
				}
				else if (Gdx.input.getX() > width*1+50 && Gdx.input.getX() < width*1+50+300) {
					gravity = 0.5f;
					gap = 500;
					maxLine = Gdx.graphics.getHeight()* 3/4;
					minLine = Gdx.graphics.getHeight()* 1/4;
				}
				else if (Gdx.input.getX() > width*2+50 && Gdx.input.getX() < width*2+50+300) {
					gravity = 0.8f;
					gap = 380;
					maxLine = Gdx.graphics.getHeight()* 3/4;
					minLine = Gdx.graphics.getHeight()* 1/4;
				}

				game_state = 1;
				music.play();
			}
		}
		// jugando
		else if (game_state == 1){
			if (pipeX[pipeActivo] < Gdx.graphics.getWidth()/2 - topTube.getWidth()){
				score++;

				if (pipeActivo < numberOfPipes - 1){
					pipeActivo++;
				}
				else {
					pipeActivo = 0;
				}

				Gdx.app.log("score", Integer.toString(score));
			}


			birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[birdState].getHeight()/2, birds[birdState].getWidth()/2);

//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//			shapeRenderer.setColor(Color.MAGENTA);
//			shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
//

			// Posicionamiento de los pipes
			for (int i = 0; i<numberOfPipes; i++) {

				if (pipeX[i] < -topTube.getWidth()){
					pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
					pipeX[i] += distance*(numberOfPipes);
				}
				else {
					pipeX[i] = pipeX[i] - pipeVelocity;
				}

				batch.draw(topTube,
						pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				batch.draw(bottomTube,
						pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

				topPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				bottomPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

//				shapeRenderer.rect(topPipes[i].x, topPipes[i].y, topTube.getWidth(),
//						topTube.getHeight());
//				shapeRenderer.rect(bottomPipes[i].x, bottomPipes[i].y, bottomTube.getWidth(),
//						bottomTube.getHeight());

				if (Intersector.overlaps(birdCircle, topPipes[i])){
					Gdx.app.log("Intersector", "top pipe overlap");
					game_state = 2;
					music.stop();
					dieSound.play();
				}
				else if (Intersector.overlaps(birdCircle, bottomPipes[i])){
					Gdx.app.log("Intersector", "bottom pipe overlap");
					game_state = 2;
					music.stop();
					dieSound.play();
				}
			}

			if (Gdx.input.justTouched()){
				//velocity = velocity - 30;
				velocity = -15;
				birdRotation = 45;
				jumpSound.play();
			}

			birdState = birdState == 0 ? 1 : 0;


			velocity = velocity + gravity;

			if (birdRotation > -45) {
				birdRotation = birdRotation - gravity*2;
			}


			if (birdY < 0){
				music.stop();
				dieSound.play();
				game_state = 2;
			}
			else {
				birdY = birdY - velocity;
			}

//			shapeRenderer.end();


		}
		// game over
		else if (game_state == 2){

			float width = Gdx.graphics.getWidth() / 3;
			float length = (Gdx.graphics.getWidth() - 200) / 3;

			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.rect(50,300,length,300);
			shapeRenderer.setColor(Color.YELLOW);
			shapeRenderer.rect(width*1+50,300,length,300);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(width*2+50,300,length,300);
			shapeRenderer.end();

			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);

			if (Gdx.input.justTouched()) {

				if (Gdx.input.getX() > 50 && Gdx.input.getX() < 50 + 300) {
					gravity = 0.3f;
					gap = 1000;
					maxLine = Gdx.graphics.getHeight()* 4/10;
					minLine = Gdx.graphics.getHeight()* 6/10;
				}
				else if (Gdx.input.getX() > width*1+50 && Gdx.input.getX() < width*1+50+300) {
					gravity = 0.5f;
					gap = 500;
					maxLine = Gdx.graphics.getHeight()* 3/4;
					minLine = Gdx.graphics.getHeight()* 1/4;
				}
				else if (Gdx.input.getX() > width*2+50 && Gdx.input.getX() < width*2+50+300) {
					gravity = 0.8f;
					gap = 380;
					maxLine = Gdx.graphics.getHeight()* 3/4;
					minLine = Gdx.graphics.getHeight()* 1/4;
				}

				dieSound.stop();
				music.play();
				game_state = 1;
				score = 0;
				pipeActivo = 0;
				velocity = 0;
				startGame();
			}
		}

		//batch.draw(birds[birdState], Gdx.graphics.getWidth() / 2 - birds[birdState].getWidth()/2,  birdY,  birds[birdState].getWidth() + birdSize, birds[birdState].getHeight() + birdSize);
		TextureRegion textureRegion = new TextureRegion(birds[birdState]);
		batch.draw(textureRegion.getTexture(), Gdx.graphics.getWidth() / 2 - birds[birdState].getWidth()/2, birdY, 1, 1, birds[birdState].getWidth() + birdSize, birds[birdState].getHeight() + birdSize, 1.0f, 1.0f, birdRotation, textureRegion.getRegionX(), textureRegion.getRegionY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), false, false);
		font.draw(batch, Integer.toString(score), Gdx.graphics.getWidth()*1/8, Gdx.graphics.getHeight()*9/10);
		//birdSize += 1;
		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		music.dispose();
		jumpSound.dispose();
		dieSound.dispose();
		background.dispose();
	}
}
