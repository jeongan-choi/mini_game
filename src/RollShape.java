import java.awt.Point;
import java.net.URL;

public class RollShape extends Shape{

	public RollShape(URL imgURL, int margin, int steps, int xBoundary, int yBoundary) {
		super (imgURL, margin, steps, xBoundary, yBoundary);
		x=0;
		y=yBoundary;
	}
	// �ϳ��� ���� �� ���� �浹�Ͽ����� (����� margin �Ÿ��ȿ� �ִ���)�� �Ǵ��ϴ� �Լ�
	public boolean collide (Point p2) {				
		Point p = new Point(this.x, this.y); 		
		if (p.distance(p2) <= margin) return true;
		return false;
	}
	
	public void move() {
		x += steps;			//�������� ��ó�� �̵�
	}
}
