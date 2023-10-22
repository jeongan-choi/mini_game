import java.io.File;				//Java I/O ��Ű�� �ȿ��� ���� ����°� ���õ� Ŭ������
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio {
	private Clip clip;
	
	public void PlaySound(String PathName) {	//pathName:����� ������ ��θ�
		try {
			clip = AudioSystem.getClip();	//����� �����̳� ����� ��Ʈ���� ����� ����� �� �ִ� Ŭ���� ���
			File audioFile = new File(PathName);
			AudioInputStream audioStream =
				AudioSystem.getAudioInputStream(audioFile); //������ File�� ���� ����� �Է� ��Ʈ���� ���
			clip.open(audioStream);
			clip.setFramePosition(0);	//�̵���� ��ġ�� ���� �����Ӽ��� ����
			clip.start();
		}
		//�ڿ��� ������ ���ؼ� ������ ���� �ʴ� ���
		catch (LineUnavailableException e) { e.printStackTrace(); }	
		//�νĵ� �������� ���� ������ ��ȿ �����͸� �� ������ �������� �ʱ� ����(������) ������ ������ ��
		catch (UnsupportedAudioFileException e) { e.printStackTrace(); }
		//I/O ������ �߻��ϴ� ���
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
			clip.loop(Clip.LOOP_CONTINUOUSLY);	//������ �������ϰ� ����ϴ� ���� ��Ÿ��
		}
		catch (LineUnavailableException e) { e.printStackTrace(); }
		catch (UnsupportedAudioFileException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
}
