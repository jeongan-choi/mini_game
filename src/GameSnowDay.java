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
	private final int F_MARGIN=50;	//공격자들의 크기, 범위에 들어왔을 때 충돌로 결정할 것인지의 값
	private final int S_MARGIN=60;	//플레이어의 크기, 범위에 들어왔을 때 충돌로 결정할 것인지의 값
	private final int WIN_WIDTH = 1280; 		// 전체 frame의 폭
	private final int WIN_HEIGHT = 850; 	// 전체 frame의 높이
	private final int FIRE_INTERVAL= 1;		// 불이 나타나는 주기
	private final int SNOW_INTERVAL= 1;		// 눈이 나타나는 주기
	private final int PRESENT_INTERVAL = 3; //선물이 나타나는 주기
	private final int BOMB_INTERVAL= 4;		//폭탄이 나타나는 주기
	private final int LIKE_INTERVAL= 5;		// 생명이 나타나는 주기
	private final int STONE_INTERVAL= 7;	// 돌이 나타나는 주기
	private final int SPEED = 60;			// 애니매이션의 속도 (밀리초)
	private final int STEPS = 30;			// 그림 객체들이 한번에 움직이는 픽슬 수
	// 버튼 토글을 위한 비트 연산에 사용될 상수들
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	//그림 res 폴더
	private final String FIRE_PIC="/res/fire.png";			//불 그림 파일
	private final String BOMB_PIC="/res/bomb.png";			//폭탄 그림 파일
	private final String SNOW_PIC="/res/snow.png";			//눈 그림 파일
	private final String PRESENT_PIC="/res/present.png";	//선물 그림 파일
	private final String PLAYER_PIC="/res/snowman.png";		//눈사람(플레이어) 그림 파일
	private final String LIKE_PIC="/res/like.png";			//하트(생명) 그림 파일
	private final String STONE_PIC="/res/stone.png";		//돌 그림 파일
	private final String MAIN_PIC = "/res/main.png";		//초기 화면 배경 파일
	private final String GAME_PIC = "/res/game.png";		//게임이 진행되는 메인 패널 배경 파일
	private final String LOGO_PIC = "/res/logo.png";		//초기 화면에 들어가는 게임 로고 파일
	private final String START_PIC = "/res/startbutton.png";//초기 화면에 들어가는 시작 버튼 파일

	JFrame frame=new JFrame();				// 전체 GUI를 담을 프레임에 대한 레퍼런스
	int gamePanelWidth, gamePanelHeight;	// 실제 게임이 이루어질 영역의 크기 
	JPanel controlPanel=new JPanel();		// 게임 컨트롤과 시간, 사용자 디스플레이가 들어갈 패널
	JButton start=new JButton("시작");		// 시작버튼
	JButton end=new JButton("종료");			// 종료버튼
	JButton suspend=new JButton("일시중지");	// 일시중지 버튼
	JButton cont=new JButton("계속");			// 계속 버튼
	JLabel timing=new JLabel("Time : 0m 0s");// 게임경과 시간 디스플레이를 위한 라벨
	JPanel midPanel;						// 중앙을 차지할 패널
	JPanel coverPanel;						// 초기화면이 나타날 패널	
	ImageIcon startButtonImage = new ImageIcon((getClass().getResource(START_PIC)));	//게임 시작 버튼
	JButton startButton = new JButton(startButtonImage);	//버튼에 게임 시작 이미지 넣기
	GamePanel gamePanel;					// 게임이 이루질 패널
	Container container;					// 게임이 이루어질 패널의 pane을 가질 컨테이너
	CardLayout card;						// 게임이 이루어질 패널에 화면을 여러장 겹치기 위한 Card 레이어
	Timer goAnime;							// 그래픽 객체의 움직임을 관장하기 위한 타이머
	Timer goClock;							// 시계구현을 위한 위한 타이머					
	ClockListener clockListener;			// 시계를 구현하기 위한 리스너
	
	ArrayList<Shape> fireList;			//게임에 사용되는 불 객체를 담는 리스트
	ArrayList<Shape> fireList1;			//게임에 사용되는 불 객체를 담는 리스트
	ArrayList<Shape> snowList;			//게임에 사용되는 눈 객체를 담는 리스트
	ArrayList<Shape> heartList;			//게임에 사용되는 생명 객체를 담는 리스트
	ArrayList<Shape> bombList;			//게임에 사용되는 폭탄 객체를 담는 리스트
	ArrayList<Shape> presentList;		//게임에 사용되는 선물 객체를 담는 리스트
	ArrayList<RollShape> stoneList;		//게임에 사용되는 돌 객체를 담는 리스트
	
	Shape player;							// 키보드로 움직이는 Player 객체
	DirectionListener keyListener;			// 화살표 움직임을 감지하는 리스너
	Audio audio = new Audio();				//오디오 클래스 객체 생성
	
	boolean gameOver=false;			//gameOver=true일 경우 엔딩에 글자,점수 표시
	int point=0, life=3;				//게임에 사용 될 점수, 생명

	public static void main(String [] args) {
		new GameSnowDay().go();									// 게임의 초기화
	}

	public void go() {
		//GUI세팅
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 게임 조정 버튼 및 디스플레이 라벨들이 들어갈 패널
		controlPanel.add(start);
		controlPanel.add(suspend);
		controlPanel.add(cont);
		controlPanel.add(end);
		
		//게임 조정 버튼 디자인
		start.setContentAreaFilled(false);
		suspend.setContentAreaFilled(false);
		cont.setContentAreaFilled(false);
		end.setContentAreaFilled(false);

		// 게임의 진행이 디스플레이 될 패널
		gamePanel = new GamePanel();
		gamePanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
		gamePanel.add(timing);
		

		// 초기화면을 위한 패널
		coverPanel = new CoverPanel();
		coverPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		coverPanel.add(startButton);

		// 초기화면과 게임화면을 레이어화 함
		midPanel = new JPanel();
		card = new CardLayout();
		midPanel.setLayout(card);
		midPanel.add("1", coverPanel);	//초기화면
		midPanel.add("2", gamePanel);	//메인화면

		// 전체 프레임에 배치
		frame.add(BorderLayout.CENTER, midPanel);
		

		// 게임이 이루어질 패널의 실제 폭과 넓이 계산

		gamePanelWidth = gamePanel.getWidth() -70;
		gamePanelHeight = gamePanel.getHeight() -160;

		//출력될 객체들을 생성하여 attackerList에 넣어 줌
		prepareAttackers();

		// 키보드로 움직일 player 개체 생성
		player = new Shape(getClass().getResource(PLAYER_PIC), S_MARGIN, gamePanelWidth, gamePanelHeight);

		// 시간 디스플레이, 객체의 움직임을 자동화 하기 위한 타이머들 
		//public Timer(int delay, ActionListener listener)
		clockListener = new ClockListener();		
		goClock = new Timer(1000, clockListener);			// 시간을 초단위로 나타내기 위한 리스너
		goAnime = new Timer(SPEED, new AnimeListener());	// 그림의 이동을 처리하기 위한 리스너

		// Player의 키보드 움직임을 위한 감청자
		gamePanel.addKeyListener(new DirectionListener());	// 키보드 리스너 설치
		gamePanel.setFocusable(false);						// 초기에는 포키싱 안되게 함(즉 키 안먹음)

		// 버튼  리스너의 설치
		start.addActionListener(new StartListener());
		suspend.addActionListener(new SuspendListener());
		cont.addActionListener(new ContListener());
		end.addActionListener(new EndListener());

		startButton.addActionListener(new StartListener());	 //초기화면 시작버튼실행

		audio.PlayLoop("src/audio/start.wav");		//배경음 재생

		// 화면의 활성화
		buttonToggler(START);	// 초기에는 start버튼만 비 활성화
		frame.setSize(WIN_WIDTH,WIN_HEIGHT);
		frame.setVisible(true);

	}

	// 버튼의 활성 비활성화를 위한 루틴
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
	// 게임의 시작에 사용될 물체들
	private void prepareAttackers() {	
		fireList = new ArrayList<Shape>();	
		fireList1 = new ArrayList<Shape>();
		snowList = new ArrayList<Shape>();
		heartList = new ArrayList<Shape>();
		bombList = new ArrayList<Shape>();
		presentList = new ArrayList<Shape>();
		stoneList = new ArrayList<RollShape>();
	}

	// 게임의 종료시 처리해야 될 내용
	private void finishGame() {
		goClock.stop();						// 시간 디스플에이 멈춤
		goAnime.stop();						// 그림객체 움직임 멈춤
		gamePanel.setFocusable(false);		// 포커싱 안되게 함(즉 키 안먹음)
		buttonToggler(START);				// 활성화 버튼의 조정
		gameOver=true;						// paintComponent에서 글자삽입
		
	}


	// goAnime 타이머에 의해 주기적으로 실행될 내용
	// 객체의 움직임, 충돌의 논리를 구현
	public class AnimeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// 만약 플레이어와 충돌하였으면 충돌의 효과음 나타내고 생명을 감소시킴 
			for(Shape s : fireList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/fire.wav");		//불 효과음
					life--;					//생명감소
					fireList.remove(s);		//플레이어와 충돌하면 사라짐
					if(life<=0) 			//생명부족시 게임종료
						finishGame();
					break;				
				}
			}
			for(Shape s : fireList1) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/fire.wav");		//불 효과음
					life--;					//생명감소
					fireList1.remove(s);
					if(life<=0) 
						finishGame();
					break;				
				}
			}
			for(Shape s : bombList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/thunder.wav");		//폭탄효과음
					life--;					//생명감소
					point--;				//점수차감
					bombList.remove(s);
					if(life<=0) 			//생명부족시 게임종료
						finishGame();
					break;				
				}
			}
			for(RollShape s : stoneList) {
				if(s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/fire.wav");
					life--;					//생명감소
					stoneList.remove(s);
					if(life<=0) 			//생명부족시 게임종료
						finishGame();
					break;	
				}
			}
			// 만약 플레이어와 충돌하였으면 효과음 나타내고 점수를 증가시킴 
			for (Shape s : snowList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/point.wav");		//획득 효과음
					point++;				// 점수 1점 획득
					snowList.remove(s);		// 루프 안에서 list의 요소를 제거하면 문제 생김
					break;					// 따라서 loop으로 다시 돌아가기전 바로 빠져나옴
				}				
			}
			for(Shape s : presentList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/point.wav");		//획득 효과음
					point++;		//점수 3점 획득
					point++;
					point++;
					presentList.remove(s);
					break;
				}
			}
			// 만약 플레이어와 충돌하였으면 효과음 나타내고 생명을 증가시킴 
			for(Shape s : heartList) {
				if (s.collide(new Point(player.x, player.y))) {
					audio.PlaySound("src/audio/point.wav");		//획득 효과음
					if(life>=3) {		//생명이 기본 3개보다 많아지면 그대로 3개
						life=3;
					}
					else {
						life++;			//획득시 생명 추가
						heartList.remove(s);
						break;
					}
				}
			}
			// 그림 객체들을 이동시킴
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

			// 화면을 전체 다 다시 그려줌. 
			frame.repaint();								
		}
	}
	
	// 시작 버튼의 감청자
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			audio.PlaySound("src/audio/point.wav");
			point=0;
			life=3;
			card.next(midPanel);							// gamePanel 이 앞으로 나오게 함
			card.show(midPanel, "2");						// 2->gamePanel이 앞으로 나오게 함
			gamePanel.setFocusable(true);					// gamePanel이 포커싱될 수 있게 함
			gamePanel.requestFocus();						// 포커싱을 맞춰줌(이것 반드시 필요)

			goAnime.start();								// 그림객체 움직임을 위한 시작

			clockListener.reset();							// 타이머의 시작값 초기화
			timing.setText("Time : 0m 0s");
			timing.setFont(new Font("Arial",Font.BOLD,30));	//타이머 폰트 설정
			timing.setForeground(Color.white);				//폰트 색상 설정
			
			// 시간 디스플레이 타이머시작
			goClock.start();
			prepareAttackers();								// 초기 공격자 준비

			buttonToggler(SUSPEND+END);						// 활성화된 버튼의 조정
			gameOver=false;
			
			frame.add(BorderLayout.SOUTH, controlPanel);

		}
	}

	class SuspendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			audio.PlaySound("src/audio/point.wav");
			goClock.stop();
			goAnime.stop();
			gamePanel.setFocusable(false);					// 게임 프레임에 키 안먹게 함
			buttonToggler(CONT+END);						// 활성화 버튼의 조정
		}
	}

	class ContListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			audio.PlaySound("src/audio/point.wav");
			goClock.restart();
			goAnime.restart();
			gamePanel.setFocusable(true);					// 게임 프레임 키 먹게 함
			gamePanel.requestFocus();						// 전체 프레밍에 포커싱해서 키 먹게 함
			buttonToggler(SUSPEND+END);						// 활성화 버튼의 조정
		}
	}

	// 종료버튼을 위한 감청자
	class EndListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			audio.PlaySound("src/audio/point.wav");
			finishGame();
			System.exit(0);									//종료
		}
	}


	// 게임이 진행되는 메인 패널
	class GamePanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0,0,this.getWidth(), this.getHeight());		// 화면 지우기-> main화면 그림처럼 나오게
			Image image = new ImageIcon(getClass().getResource(GAME_PIC)).getImage(); 
			g.drawImage(image,0,0,this);

			
			for(int i=0;i<life;i++) {
				Image image3 = new ImageIcon(getClass().getResource(LIKE_PIC)).getImage(); 		//하트(생명)이미지
				g.drawImage(image3,30*i,0,50,50,this);											//화면 왼쪽 상단에 가지고 있는 생명 갯수만큼 표시
			}
			
			g.setFont(new Font("Arial",1,30));
			g.drawString("point : "+String.valueOf(point),1100,30);								//화면 오른쪽 상단에 획득한 점수 표시

			// 게임에 사용되는 그래픽 객체들 모두 그려줌
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


			if(gameOver) {								//gameOver일 경우 획득한 점수 알림
				g.setFont(new Font("Arial",1,100));
				g.drawString("Game Over",380,250);

				g.setFont(new Font("Arial",1,100));
				g.drawString("point : "+String.valueOf(point),450, 400);
				
			}

		}

	}

	// 초기화면을 나타내는 패널
	class CoverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource(MAIN_PIC)).getImage(); 	//초기화면
			Image logo = new ImageIcon(getClass().getResource(LOGO_PIC)).getImage(); 	//게임로고
			g.drawImage(image,0,0,this);
			g.drawImage(logo,450,30,this);

			startButton.setBounds(570, 670, 140, 40);		//시작버튼 크기와 위치 설정
			startButton.setBorderPainted(false);			//버튼의 외곽선 삭제
		}
	}
	
	// 시간 디스플레이를 위해 사용하는 시계
	private class ClockListener implements ActionListener {
		int times=0;
		public void actionPerformed (ActionEvent event) {		
			times++;
			timing.setText("Time : "+times/60+"m "+times%60+"s");
			// 일정 시간이 지나면 새로운 물체들이 떨어짐
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
			// 일정시간이 지나면 화면 왼쪽 하단에서 돌 굴러옴
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

	// 키보드 움직임을 감청하는 감청자
	class DirectionListener implements KeyListener {
		public void keyPressed (KeyEvent event) {
			switch (event.getKeyCode()){
			case KeyEvent.VK_LEFT:		//왼쪽 이동
				if (player.x >= 0)
					player.x -= STEPS;
				break;
			case KeyEvent.VK_RIGHT:		//오른쪽 이동
				if (player.x <= gamePanelWidth)
					player.x += STEPS;
				break;
			case KeyEvent.VK_SPACE:
				player.y = gamePanelHeight-100;			//스페이스바가 눌렸을 때, 위로 이동
				break;
			}
		}
		public void keyTyped (KeyEvent event) {}
		public void keyReleased (KeyEvent event) {
			if(event.getKeyCode()==KeyEvent.VK_SPACE) {
				player.y=gamePanelHeight;				//스페이스바를 떼면 원래대로 이동
			}
		}
	}

}