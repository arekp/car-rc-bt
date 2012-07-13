package pl.arekp.car_control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawView extends View {
	 
	public DrawView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	Paint paint;
	Path path;
	 
	
	private void init(){
	 paint = new Paint();
	 paint.setColor(Color.BLUE);
	 paint.setStrokeWidth(10);
	 paint.setStyle(Paint.Style.STROKE);
	 
	}
	 
	@Override
	protected void onDraw(Canvas canvas) {
	 // TODO Auto-generated method stub
	 super.onDraw(canvas);
	 
	 paint.setStyle(Paint.Style.STROKE);
	 canvas.drawCircle(50, 50, 30, paint);
	 
	 paint.setStyle(Paint.Style.FILL);
	 canvas.drawCircle(300, 300, 200, paint);
	 //drawCircle(cx, cy, radius, paint)
	 
	}
	 
	}