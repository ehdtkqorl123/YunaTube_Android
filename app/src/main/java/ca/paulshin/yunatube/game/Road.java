package ca.paulshin.yunatube.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Road {

	public static final float h = 20;

	Game game;
	float y;
	Paint paint;
		
	public Road(Game game) {
		this.game = game;
		y = game.groundY;

		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setAntiAlias(true);
	}

	public void draw(Canvas canvas) {
		canvas.drawRect(0, y, canvas.getWidth(), y + h, paint);
	}
}
