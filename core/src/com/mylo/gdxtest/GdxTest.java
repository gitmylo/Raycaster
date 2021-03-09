package com.mylo.gdxtest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class GdxTest extends ApplicationAdapter {
	ShapeRenderer sr;

	float playerX = 100, playerY = 100, yaw = 0;

	int[] walls = new int[] {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	};
	int wallsHeight = 16, wallsWidth = 16, wallSize = 58;

	static final int FOV = 500;
	static final int FOVJUMP = 1;
	
	@Override
	public void create () {
		sr = new ShapeRenderer();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		RayCast();

		/*sr.setColor(Color.WHITE);
		sr.begin(ShapeRenderer.ShapeType.Line);
		sr.line(new Vector2(500, 0), new Vector2(500, 500));
		sr.end();
		 */
	}

	public void RayCast(){
		HandleMovement(Gdx.graphics.getDeltaTime());
		for (int x = 0; x < wallsWidth; x++){
			for (int y = 0; y < wallsHeight; y++){
				int index = x + (y*wallsWidth);
				//Render2d(index, x * wallSize, y * wallSize);
			}
		}
		RenderPlayer();
	}

	public void Render2d(int index, int x, int y){
		Color color = getWallColorForValue(walls[index]);
		sr.setColor(color);
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.rect(x, (wallSize * wallsHeight) - y, wallSize, -wallSize);
		sr.end();
	}

	public int getWallOnPos(int x, int y){
		if (walls.length >= x + (y*wallsWidth)){
			return walls[x + (y*wallsWidth)];
		}
		return 0;
	}

	public void HandleMovement(float deltaTime){
		float horizontal = 0;
		if (Gdx.input.isKeyPressed(Input.Keys.D)){//check D key
			horizontal++;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)){//check A key
			horizontal--;
		}

		float vertical = 0;
		if (Gdx.input.isKeyPressed(Input.Keys.W)){//check W key
			vertical++;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)){//check S key
			vertical--;
		}

		float yawChange = 0;
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){//check left arrow key
			yawChange++;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){//check right arrow key
			yawChange--;
		}

		horizontal *= deltaTime * 60;
		vertical *= deltaTime * 60;
		Vector2 forwards = rotationToPositionOffset(yaw);
		Vector2 sideways = rotationToPositionOffset(yaw - (565.2f));
		Vector2 outVec = forwards.scl(vertical).add(sideways.scl(horizontal));

		playerY += outVec.y;
		playerX += outVec.x;
		yaw = (yaw + (yawChange*deltaTime * 600));
	}

	public void RenderPlayer() {
		for (float i = 0; i < FOV; i+=FOVJUMP){
			float castYaw = i - FOV/2 + yaw;
			rayCastResult cast = RayCast(new Vector2(playerX, playerY), castYaw, 1000);

			//drawRayResult(cast);
			Render3d(cast, i/(double)FOV, 1/(double)(FOV+FOVJUMP), Math.abs(i - (FOV/2)));
		}

		//draw player itsself
		/*
		sr.setColor(Color.GREEN);
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.circle(playerX, playerY, 10);
		sr.end();
		 */
	}

	public void drawRayResult(rayCastResult result){
		if (!result.hit)
			return;
		Color c = getWallColorForValue(result.hitWallType);
		if (result.xAxisHit){
			c = new Color(c.r, c.g, c.b, c.a);
			c.mul(0.85f);
			c.add(0.15f, 0.15f, 0.15f, 0.15f);
		}
		sr.setColor(c);
		sr.begin(ShapeRenderer.ShapeType.Line);
		sr.line(result.startPos, result.hitWallPos);
		sr.end();
	}

	public Color getWallColorForValue(int value){
		Color color;
		switch (value){
			case 0:
				color = Color.GRAY;
				break;
			case 1:
				color = Color.BLACK;
				break;
			case 2:
				color = Color.RED;
				break;
			default:
				color = Color.PINK;
				break;
		}
		return color;
	}

	public Vector2 rotationToPositionOffset(float rotation){
		return new Vector2((float)Math.cos(rotation/360), (float)Math.sin(rotation/360));
	}

	public rayCastResult RayCast(Vector2 origin, float direction, float maxLenght){
		Vector2 directionVec = rotationToPositionOffset(direction);

		float hitX = origin.x;
		float hitY = origin.y;

		directionVec = directionVec.nor().scl(0.2f);

		boolean hitOnX = false;
		while (true){
			hitX += directionVec.x;

			int hitWallX = getWallOnPos((int) (hitX/wallSize),wallsHeight - 1 - ((int) (hitY/wallSize)));
			if (hitWallX > 0){
				return new rayCastResult(hitWallX, origin, new Vector2(hitX, hitY), true);
			}

			hitY += directionVec.y;
			int hitWallY = getWallOnPos((int) (hitX/wallSize),wallsHeight - 1 - ((int) (hitY/wallSize)));
			if (hitWallY > 0){
				return new rayCastResult(hitWallY, origin, new Vector2(hitX, hitY), false);
			}
			//if (new Vector2(hitX, hitY).len() > maxLenght){
			//	return new rayCastResult();
			//}
		}
	}

	public void Render3d(rayCastResult r, double screenPercent, double onePercent, float renderAngle){
		if (!r.hit)
			return;

		int startX = 0;//500;
		int endX = Gdx.graphics.getWidth();//1000;
		double diffX = endX - startX;
		double nextPercent = screenPercent + (onePercent * (FOVJUMP));

		double drawStartX = endX - (diffX * screenPercent);
		double drawEndX = endX - (diffX * nextPercent);

		float distance = r.startPos.sub(r.hitWallPos).len();

		Color c = getWallColorForValue(r.hitWallType);
		if (r.xAxisHit){
			c = new Color(c.r, c.g, c.b, c.a);
			c.mul(0.8f);
			c.add(0.2f, 0.2f, 0.2f, 0.2f);
		}
		renderWall3d((float) drawStartX, (float) drawEndX, distance, c, renderAngle);

	}

	public void renderWall3d(float x, float endX, float distance, Color c, float renderAngle){
		float maxSize = Gdx.graphics.getHeight();//500;
		float halfSize = maxSize/2;
		float distanceSize = (maxSize / (distance)) * 10;
		sr.setColor(c);
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.rect(x, halfSize - (distanceSize/2), endX - x, distanceSize);
		sr.end();
	}
	
	@Override
	public void dispose () {
		sr.dispose();
	}
}

class rayCastResult {
	public boolean hit;
	public int hitWallType;
	public Vector2 hitWallPos;
	public Vector2 startPos;
	public boolean xAxisHit;

	public rayCastResult(){
		hit = false;
	}

	public rayCastResult(int hitwall, Vector2 beginPos, Vector2 endPos, boolean hitXAxis){
		hitWallType = hitwall;
		hitWallPos = endPos;
		startPos = beginPos;
		hit = true;
		xAxisHit = hitXAxis;
	}
}
