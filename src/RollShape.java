import java.awt.Point;
import java.net.URL;

public class RollShape extends Shape{

	public RollShape(URL imgURL, int margin, int steps, int xBoundary, int yBoundary) {
		super (imgURL, margin, steps, xBoundary, yBoundary);
		x=0;
		y=yBoundary;
	}
	// 하나의 점이 이 모양과 충돌하였는지 (모양의 margin 거리안에 있는지)를 판단하는 함수
	public boolean collide (Point p2) {				
		Point p = new Point(this.x, this.y); 		
		if (p.distance(p2) <= margin) return true;
		return false;
	}
	
	public void move() {
		x += steps;			//굴러오는 돌처럼 이동
	}
}
