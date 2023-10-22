import java.io.File;				//Java I/O 패키지 안에는 파일 입출력과 관련된 클래스들
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio {
	private Clip clip;
	
	public void PlaySound(String PathName) {	//pathName:오디오 파일의 경로명
		try {
			clip = AudioSystem.getClip();	//오디오 파일이나 오디오 스트림의 재생에 사용할 수 있는 클립을 취득
			File audioFile = new File(PathName);
			AudioInputStream audioStream =
				AudioSystem.getAudioInputStream(audioFile); //지정된 File로 부터 오디오 입력 스트림을 취득
			clip.open(audioStream);
			clip.setFramePosition(0);	//미디어의 위치를 샘플 프레임수로 설정
			clip.start();
		}
		//자원의 제약을 위해서 라인을 열지 않는 경우
		catch (LineUnavailableException e) { e.printStackTrace(); }	
		//인식된 파일형과 파일 형식의 유효 데이터를 그 파일이 포함하지 않기 위해(때문에) 조작이 실패한 것
		catch (UnsupportedAudioFileException e) { e.printStackTrace(); }
		//I/O 오류가 발생하는 경우
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void PlayLoop(String PathName) {
		try {
			clip = AudioSystem.getClip();
			File audioFile = new File(PathName);
			AudioInputStream audioStream =
				AudioSystem.getAudioInputStream(audioFile);
			clip.open(audioStream);
			clip.setFramePosition(0);
			clip.loop(Clip.LOOP_CONTINUOUSLY);	//루프를 무제한하게 계속하는 것을 나타냄
		}
		catch (LineUnavailableException e) { e.printStackTrace(); }
		catch (UnsupportedAudioFileException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
}
