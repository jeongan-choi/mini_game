import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameSnowDay {
	private final int F_MARGIN=50;	//�����ڵ��� ũ��, ������ ������ �� �浹�� ������ �������� ��
	private final int S_MARGIN=60;	//�÷��̾��� ũ��, ������ ������ �� �浹�� ������ �������� ��
	private final int WIN_WIDTH = 1280; 		// ��ü frame�� ��
	private final int WIN_HEIGHT = 850; 	// ��ü frame�� ����
	private final int FIRE_INTERVAL= 1;		// ���� ��Ÿ���� �ֱ�
	private final int SNOW_INTERVAL= 1;		// ���� ��Ÿ���� �ֱ�
	private final int PRESENT_INTERVAL = 3; //������ ��Ÿ���� �ֱ�
	private final int BOMB_INTERVAL= 4;		//��ź�� ��Ÿ���� �ֱ�
	private final int LIKE_INTERVAL= 5;		// ������ ��Ÿ���� �ֱ�
	private final int STONE_INTERVAL= 7;	// ���� ��Ÿ���� �ֱ�
	private final int SPEED = 60;			// �ִϸ��̼��� �ӵ� (�и���)
	private final int STEPS = 30;			// �׸� ��ü���� �ѹ��� �����̴� �Ƚ� ��
	// ��ư ����� ���� ��Ʈ ���꿡 ���� �����
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	//�׸� res ����
	private final String FIRE_PIC="/res/fire.png";			//�� �׸� ����
	private final String BOMB_PIC="/res/bomb.png";			//��ź �׸� ����
	private final String SNOW_PIC="/res/snow.png";			//�� �׸� ����
	private final String PRESENT_PIC="/res/present.png";	//���� �׸� ����
	private final String PLAYER_PIC="/res/snowman.png";		//�����(�÷��̾�) �׸� ����
	private final String LIKE_PIC="/res/like.png";			//��Ʈ(����) �׸� ����
	private final String STONE_PIC="/res/stone.png";		//�� �׸� ����
	private final String MAIN_PIC = "/res/main.png";		//�ʱ� ȭ�� ��� ����
	private final String GAME_PIC = "/res/game.png";		//������ ����Ǵ� ���� �г� ��� ����
	private final String LOGO_PIC = "/res/logo.png";		//�ʱ� ȭ�鿡 ���� ���� �ΰ� ����
	private final String START_PIC = "/res/startbutton.png";//�ʱ� ȭ�鿡 ���� ���� ��ư ����

	JFrame frame=new JFrame();				// ��ü GUI�� ���� �����ӿ� ���� ���۷���
	int gamePanelWidth, gamePanelHeight;	// ���� ������ �̷���� ������ ũ�� 
	JPanel controlPanel=new JPanel();		// ���� ��Ʈ�Ѱ� �ð�, ����� ���÷��̰� �� �г�
	JButton start=new JButton("����");		// ���۹�ư
	JButton end=new JButton("����");			// �����ư
	JButton suspend=new JButton("�Ͻ�����");	// �Ͻ����� ��ư
	JButton cont=new JButton("���");			// ��� ��ư
	JLabel timing=new JLabel("Time : 0m 0s");// ���Ӱ�� �ð� ���÷��̸� ���� ��
	JPanel midPanel;						// �߾��� ������ �г�
	JPanel coverPanel;						// �ʱ�ȭ���� ��Ÿ�� �г�	
	ImageIcon startButtonImage = new ImageIcon((getClass().getResource(START_PIC)));	//���� ���� ��ư
	JButton startButton = new JButton(startButtonImage);	//��ư�� ���� ���� �̹��� �ֱ�
	GamePanel gamePanel;					// ������ �̷��� �г�
	Container container;					// ������ �̷���� �г��� pane�� ���� �����̳�
	CardLayout card;						// ������ �̷���� �гο� ȭ���� ������ ��ġ�� ���� Card ���̾�
	Timer goAnime;							// �׷��� ��ü�� �������� �����ϱ� ���� Ÿ�̸�
	Timer goClock;							// �ð豸���� ���� ���� Ÿ�̸�					
	ClockListener clockListener;			// �ð踦 �����ϱ� ���� ������
	
	ArrayList<Shape> fireList;			//���ӿ� ���Ǵ� �� ��ü�� ��� ����Ʈ
	ArrayList<Shape> fireList1;			//���ӿ� ���Ǵ� �� ��ü�� ��� ����Ʈ
	ArrayList<Shape> snowList;			//���ӿ� ���Ǵ� �� ��ü�� ��� ����Ʈ
	ArrayList<Shape> heartList;			//���ӿ� ���Ǵ� ���� ��ü�� ��� ����Ʈ
	ArrayList<Shape> bombList;			//���ӿ� ���Ǵ� ��ź ��ü�� ��� ����Ʈ
	ArrayList<Shape> presentList;		//���ӿ� ���Ǵ� ���� ��ü�� ��� ����Ʈ
	ArrayList<RollShape> stoneList;		//���ӿ� ���Ǵ� �� ��ü�� ��� ����Ʈ
	
	Shape player;							// Ű����� �����̴� Player ��ü
	DirectionListener keyListener;			// ȭ��ǥ �������� �����ϴ� ������
	Audio audio = new Audio();				//����� Ŭ���� ��ü ����
	
	boolean gameOver=false;			//gameOver=true�� ��� ������ ����,���� ǥ��
	int point=0, life=3;				//���ӿ� ��� �� ����, ����

	public static void main(String [] args) {
		new GameSnowDay().go();									// ������ �ʱ�ȭ
	}

	public void go() {
		//GUI����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ���� ���� ��ư �� ���÷��� �󺧵��� �� �г�
		controlPanel.add(start);
		controlPanel.add(suspend);
		controlPanel.add(cont);
		controlPanel.add(end);
		
		//���� ���� ��ư ������
		start.setContentAreaFilled(false);
		suspend.setContentAreaFilled(false);
		cont.setContentAreaFilled(false);
		end.setContentAreaFilled(false);

		// ������ ������ ���÷��� �� �г�
		gamePanel = new GamePanel();
		gamePanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
		gamePanel.add(timing);
		

		// �ʱ�ȭ���� ���� �г�
		coverPanel = new CoverPanel();
		coverPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		coverPanel.add(startButton);

		// �ʱ�ȭ��� ����ȭ���� ���̾�ȭ ��
		midPanel = new JPanel();
		card = new CardLayout();
		midPanel.setLayout(card);
		midPanel.add("1", coverPanel);	//�ʱ�ȭ��
		midPanel.add("2", gamePanel);	//����ȭ��

		// ��ü �����ӿ� ��ġ
		frame.add(BorderLayout.CENTER, midPanel);
		

		// ������ �̷���� �г��� ���� ���� ���� ���

		gamePanelWidth = gamePanel.getWidth() -70;
		gamePanelHeight = gamePanel.getHeight() -160;

		//��µ� ��ü���� �����Ͽ� attackerList�� �־� ��
		prepareAttackers();

		// Ű����� ������ player ��ü ����
		player = new Shape(getClass().getResource(PLAYER_PIC), S_MARGIN, gamePanelWidth, gamePanelHeight);

		// �ð� ���÷���, ��ü�� �������� �ڵ�ȭ �ϱ� ���� Ÿ�̸ӵ� 
		//public Timer(int delay, ActionListener listener)
		clockListener = new ClockListener();		
		goClock = new Timer(1000, clockListener);			// �ð��� �ʴ����� ��Ÿ���� ���� ������
		goAnime = new Timer(SPEED, new AnimeListener());	// �׸��� �̵��� ó���ϱ� ���� ������

		// Player�� Ű���� �������� ���� ��û��
		gamePanel.addKeyListener(new DirectionListener());	// Ű���� ������ ��ġ
		gamePanel.setFocusable(false);						// �ʱ⿡�� ��Ű�� �ȵǰ� ��(�� Ű �ȸ���)

		// ��ư  �������� ��ġ
		start.addActionListener(new StartListener());
		suspend.addActionListener(new SuspendListener());
		cont.addActionListener(new ContListener());
		end.addActionListener(new EndListener());

		startButton.addActionListener(new StartListener());	 //�ʱ�ȭ�� ���۹�ư����

		audio.PlayLoop("src/audio/start.wav");		//����� ���

		// ȭ���� Ȱ��ȭ
		buttonToggler(START);	// �ʱ⿡�� start��ư�� �� Ȱ��ȭ
		frame.setSize(WIN_WIDTH,WIN_HEIGHT);
		frame.setVisible(true);

	}

	// ��ư�� Ȱ�� ��Ȱ��ȭ�� ���� ��ƾ
	private void buttonToggler(int flags) {
		if ((flags & START) != 0)
			start.setEnabled(true);
		else
			start.setEnabled(false);
		if ((flags & SUSPEND) != 0)
			suspend.setEnabled(true);
		else
			suspend.setEnabled(false);
		if ((flags & CONT) != 0)
			cont.setEnabled(true);
		else
			cont.setEnabled(false);
		if ((flags & END) != 0)
			end.setEnabled(true);
		else
			end.setEnabled(false);
	}
	// ������ ���ۿ� ���� ��ü��
	private void prepareAttackers() {	
		fireList = new ArrayList<Shape>();	
		fireList1 = new ArrayList<Shape>();
		snowList = new ArrayList<Shape>();
		heartList = new ArrayList<Shape>();
		bombList = new ArrayList<Shape>();
		presentList = new ArrayList<Shape>();
		stoneList = new ArrayList<RollShape>();
	}

	// ������ ����� ó���ؾ� �� ����
	private void finishGame() {
		goClock.stop();						// �ð� ���ÿ��� ����
		goAnime.stop();						// �׸���ü ������ ����
		gamePanel.setFocusable(false);		// ��Ŀ�� �ȵǰ� ��(�� Ű �ȸ���)
		buttonToggler(START);				// Ȱ��ȭ ��ư�� ����
		gameOver=true;						// paintComponent���� ���ڻ���
		
	}


	// goAnime Ÿ�̸ӿ� ���� �ֱ������� ����� ����
	// ��ü�� ������, �浹�� ���� ����
	public class AnimeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// ���� �÷��̾�� �浹�Ͽ����� �浹�� ȿ���� ��Ÿ���� ������ ���ҽ�Ŵ 
			for(Shape s : fireList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/fire.wav");		//�� ȿ����
					life--;					//������
					fireList.remove(s);		//�÷��̾�� �浹�ϸ� �����
					if(life<=0) 			//��������� ��������
						finishGame();
					break;				
				}
			}
			for(Shape s : fireList1) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/fire.wav");		//�� ȿ����
					life--;					//������
					fireList1.remove(s);
					if(life<=0) 
						finishGame();
					break;				
				}
			}
			for(Shape s : bombList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/thunder.wav");		//��źȿ����
					life--;					//������
					point--;				//��������
					bombList.remove(s);
					if(life<=0) 			//��������� ��������
						finishGame();
					break;				
				}
			}
			for(RollShape s : stoneList) {
				if(s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/fire.wav");
					life--;					//������
					stoneList.remove(s);
					if(life<=0) 			//��������� ��������
						finishGame();
					break;	
				}
			}
			// ���� �÷��̾�� �浹�Ͽ����� ȿ���� ��Ÿ���� ������ ������Ŵ 
			for (Shape s : snowList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/point.wav");		//ȹ�� ȿ����
					point++;				// ���� 1�� ȹ��
					snowList.remove(s);		// ���� �ȿ��� list�� ��Ҹ� �����ϸ� ���� ����
					break;					// ���� loop���� �ٽ� ���ư����� �ٷ� ��������
				}				
			}
			for(Shape s : presentList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/point.wav");		//ȹ�� ȿ����
					point++;		//���� 3�� ȹ��
					point++;
					point++;
					presentList.remove(s);
					break;
				}
			}
			// ���� �÷��̾�� �浹�Ͽ����� ȿ���� ��Ÿ���� ������ ������Ŵ 
			for(Shape s : heartList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/point.wav");		//ȹ�� ȿ����
					if(life>=3) {		//������ �⺻ 3������ �������� �״�� 3��
						life=3;
					}
					else {
						life++;			//ȹ��� ���� �߰�
						heartList.remove(s);
						break;
					}
				}
			}
			// �׸� ��ü���� �̵���Ŵ
			for (Shape s : fireList) {
				s.move();
			}
			for (Shape s : fireList1) {
				s.move();
			}
			for (Shape s : bombList) {
				s.move();
			}
			for (Shape s : snowList) {
				s.move();
			}
			for (Shape s : presentList) {
				s.move();
			}
			for (Shape s : heartList) {
				s.move();
			}
			for (RollShape s : stoneList) {
				s.move();
			}

			// ȭ���� ��ü �� �ٽ� �׷���. 
			frame.repaint();								
		}
	}
	
	// ���� ��ư�� ��û��
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			audio.PlaySound("src/audio/point.wav");
			point=0;
			life=3;
			card.next(midPanel);							// gamePanel �� ������ ������ ��
			card.show(midPanel, "2");						// 2->gamePanel�� ������ ������ ��
			gamePanel.setFocusable(true);					// gamePanel�� ��Ŀ�̵� �� �ְ� ��
			gamePanel.requestFocus();						// ��Ŀ���� ������(�̰� �ݵ�� �ʿ�)

			goAnime.start();								// �׸���ü �������� ���� ����

			clockListener.reset();							// Ÿ�̸��� ���۰� �ʱ�ȭ
			timing.setText("Time : 0m 0s");
			timing.setFont(new Font("Arial",Font.BOLD,30));	//Ÿ�̸� ��Ʈ ����
			timing.setForeground(Color.white);				//��Ʈ ���� ����
			
			// �ð� ���÷��� Ÿ�̸ӽ���
			goClock.start();
			prepareAttackers();								// �ʱ� ������ �غ�

			buttonToggler(SUSPEND+END);						// Ȱ��ȭ�� ��ư�� ����
			gameOver=false;
			
			frame.add(BorderLayout.SOUTH, controlPanel);

		}
	}

	class SuspendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			audio.PlaySound("src/audio/point.wav");
			goClock.stop();
			goAnime.stop();
			gamePanel.setFocusable(false);					// ���� �����ӿ� Ű �ȸ԰� ��
			buttonToggler(CONT+END);						// Ȱ��ȭ ��ư�� ����
		}
	}

	class ContListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			audio.PlaySound("src/audio/point.wav");
			goClock.restart();
			goAnime.restart();
			gamePanel.setFocusable(true);					// ���� ������ Ű �԰� ��
			gamePanel.requestFocus();						// ��ü �����ֿ� ��Ŀ���ؼ� Ű �԰� ��
			buttonToggler(SUSPEND+END);						// Ȱ��ȭ ��ư�� ����
		}
	}

	// �����ư�� ���� ��û��
	class EndListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			audio.PlaySound("src/audio/point.wav");
			finishGame();
			System.exit(0);									//����
		}
	}


	// ������ ����Ǵ� ���� �г�
	class GamePanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0,0,this.getWidth(), this.getHeight());		// ȭ�� �����-> mainȭ�� �׸�ó�� ������
			Image image = new ImageIcon(getClass().getResource(GAME_PIC)).getImage(); 
			g.drawImage(image,0,0,this);

			
			for(int i=0;i<life;i++) {
				Image image3 = new ImageIcon(getClass().getResource(LIKE_PIC)).getImage(); 		//��Ʈ(����)�̹���
				g.drawImage(image3,30*i,0,50,50,this);											//ȭ�� ���� ��ܿ� ������ �ִ� ���� ������ŭ ǥ��
			}
			
			g.setFont(new Font("Arial",1,30));
			g.drawString("point : "+String.valueOf(point),1100,30);								//ȭ�� ������ ��ܿ� ȹ���� ���� ǥ��

			// ���ӿ� ���Ǵ� �׷��� ��ü�� ��� �׷���
			for (Shape s : fireList) {
				s.draw(g, this);
			}
			for (Shape s : fireList1) {
				s.draw(g, this);
			}
			for (Shape s : bombList) {
				s.draw(g, this);
			}
			for (Shape s : snowList) {
				s.draw(g, this);
			}
			for (Shape s : presentList) {
				s.draw(g, this);
			}
			for (Shape s : heartList) {
				s.draw(g, this);
			}
			for (RollShape s : stoneList) {
				s.draw(g, this);
			}
			player.draw(g, this);


			if(gameOver) {								//gameOver�� ��� ȹ���� ���� �˸�
				g.setFont(new Font("Arial",1,100));
				g.drawString("Game Over",380,250);

				g.setFont(new Font("Arial",1,100));
				g.drawString("point : "+String.valueOf(point),450, 400);
				
			}

		}

	}

	// �ʱ�ȭ���� ��Ÿ���� �г�
	class CoverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource(MAIN_PIC)).getImage(); 	//�ʱ�ȭ��
			Image logo = new ImageIcon(getClass().getResource(LOGO_PIC)).getImage(); 	//���ӷΰ�
			g.drawImage(image,0,0,this);
			g.drawImage(logo,450,30,this);

			startButton.setBounds(570, 670, 140, 40);		//���۹�ư ũ��� ��ġ ����
			startButton.setBorderPainted(false);			//��ư�� �ܰ��� ����
		}
	}
	
	// �ð� ���÷��̸� ���� ����ϴ� �ð�
	private class ClockListener implements ActionListener {
		int times=0;
		public void actionPerformed (ActionEvent event) {		
			times++;
			timing.setText("Time : "+times/60+"m "+times%60+"s");
			// ���� �ð��� ������ ���ο� ��ü���� ������
			if (times % FIRE_INTERVAL == 0)
				fireList.add(new Shape(getClass().getResource(FIRE_PIC), F_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
			if (times % FIRE_INTERVAL == 0)
				fireList1.add(new Shape(getClass().getResource(FIRE_PIC), F_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
			if (times % SNOW_INTERVAL == 0)
				snowList.add(new Shape(getClass().getResource(SNOW_PIC), F_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
			if (times % LIKE_INTERVAL == 0)
				heartList.add(new Shape(getClass().getResource(LIKE_PIC), F_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
			if (times % PRESENT_INTERVAL == 0) 
				presentList.add(new Shape(getClass().getResource(PRESENT_PIC), F_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
			if (times % BOMB_INTERVAL == 0)
				bombList.add(new Shape(getClass().getResource(BOMB_PIC), F_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
			// �����ð��� ������ ȭ�� ���� �ϴܿ��� �� ������
			if (times % STONE_INTERVAL == 0)
				stoneList.add(new RollShape(getClass().getResource(STONE_PIC), F_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		}
		public void reset() {
			times = 0;
		}
		public int getElaspedTime() {
			return times;
		}
	}

	// Ű���� �������� ��û�ϴ� ��û��
	class DirectionListener implements KeyListener {
		public void keyPressed (KeyEvent event) {
			switch (event.getKeyCode()){
			case KeyEvent.VK_LEFT:		//���� �̵�
				if (player.x >= 0)
					player.x -= STEPS;
				break;
			case KeyEvent.VK_RIGHT:		//������ �̵�
				if (player.x <= gamePanelWidth)
					player.x += STEPS;
				break;
			case KeyEvent.VK_SPACE:
				player.y = gamePanelHeight-100;			//�����̽��ٰ� ������ ��, ���� �̵�
				break;
			}
		}
		public void keyTyped (KeyEvent event) {}
		public void keyReleased (KeyEvent event) {
			if(event.getKeyCode()==KeyEvent.VK_SPACE) {
				player.y=gamePanelHeight;				//�����̽��ٸ� ���� ������� �̵�
			}
		}
	}

}