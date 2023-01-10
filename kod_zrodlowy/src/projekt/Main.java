/**
 * @author Daniel Thimm 179931
 */


package projekt;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.imageio.ImageIO;
/** Glowna klasa projektu **/
public class Main extends Canvas implements Runnable {
	
	/** deklarowanie wielkosci okna **/
    public static final int szer=1280, wys=1024;
    /** deklaracja watku **/
    private Thread watek;
    
    /** zmienna odpowiadajaca za pozostaly czas gry **/
    public long pczas = 0;
    /** zmienna odpowiadajaca za uplyniety czas w grze **/ 
    public long upczas = 0;
    
    /** Deklaracja tla **/
    private Image tlo;
    /** Deklaracja obrazu szkla **/
    private Image oszklo;
    /**  Deklaracja obrazu plastiku **/
    private Image oplastik;
    /**  Deklaracja obrazu papieru **/
    private Image opapier;
    /**  Deklaracja obrazu odpadow bio **/
    private Image obio;
    
    /** flaga usunieca smieci **/
    private boolean usunsmiecia = false;
    /** flaga konca gry **/
    private boolean jkoniec = false;
    /** flaga odpowiadajaca czy gra sie odbywa **/
    private boolean running = false;
    
  
    
    /**  zmiennea do zliczania punktow **/
    private int punkty = 0;
    /** obszaru do wyrzucania smieci **/
    private Rectangle smietnikPapier, smietnikPlastik, smietnikSzklo, smietnikBio;
    
    
    /** Klasa deklarujaca smiec **/
    private static class Smiec {
    	/** poczatkowe spó³rzêdne smiecia **/
    	public int x, y;
    	/** szerokoœæ smiecia **/
    	public int szer = 50;
    	
    	/** enum rodzaji smieci **/
    	public enum RodzajSmiecia {
    		PAPIER, PLASTIK, SZKLO, BIO
    	};
    	/** rodzaji smieci **/
    	public RodzajSmiecia rodzajSmiecia;
    	/** obrazu smiecia**/
    	public Image img;
    	
    	/** konstruktor odpadu 
    	 * @param x poczatkowa wspó³rzêdna x
    	 * @param y pocz¹tkowa wspó³rzêdna y
    	 * @param rodzajSmiecia rodzaj smiecie 
    	 * @param img obraz smiecia
    	 *  **/
    	public Smiec(int x, int y, RodzajSmiecia rodzajSmiecia, Image img) {
    		this.x = x;
    		this.y = y;
    		this.rodzajSmiecia = rodzajSmiecia;
    		this.img = img;
    	}
    	/** renderowanie grafiki 
    	 *  @param g 
    	**/
    	public void render(Graphics g) {
    		g.drawImage(img, x, y, szer, szer, null);  
    		
    	} 
    }
    
    /** deklaracja tabilcy smieci**/
    private ArrayList<Smiec> smieci;
    /** deklaracja atualnie poruszanego smiecia **/
    private Smiec aktulaniePoruszanySmiec;
    /** deklaracja zmiennej opisujacej czas wystartowania gry **/
    private long timerstart;
    
    /** glowna klasa gry **/
    public Main(){
        new Okno(szer, wys, "Sorter", this);
        timerstart = System.currentTimeMillis();
        pczas = 1;
        addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
				 
			}

			public void mouseExited(MouseEvent e) {
				  
			}

			public void mousePressed(MouseEvent e) {
				if (jkoniec == true) {
					jkoniec = false; 
					punkty = 0;
					timerstart = System.currentTimeMillis();
					pczas = 1;
					aktulaniePoruszanySmiec = null;
					smieci.clear();
					spawnSmieci();
				}
				if(aktulaniePoruszanySmiec == null) {
					for(Smiec smiec : smieci) {
						if(smiec.x < e.getX() && smiec.x + smiec.szer > e.getX()
						&& smiec.y < e.getY() && smiec.y + smiec.szer > e.getY()) {
							aktulaniePoruszanySmiec = smiec;
							break;
						}
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				
				//spawdz czy spadnie na smietnik
				if(aktulaniePoruszanySmiec != null) {
					
					if(smietnikPlastik.contains(aktulaniePoruszanySmiec.x, aktulaniePoruszanySmiec.y, aktulaniePoruszanySmiec.szer, aktulaniePoruszanySmiec.szer)) {
						if(aktulaniePoruszanySmiec.rodzajSmiecia == Smiec.RodzajSmiecia.PLASTIK) {
							wywalSmiecia(aktulaniePoruszanySmiec);
							playSound(new File("src/res/plastik.wav"));
							timerstart = timerstart + 1000;
						} else {
							System.out.println("zly smietnik");
							punkty--;
							timerstart = timerstart - 3000;
						}
					}
					
					else if(smietnikPapier.contains(aktulaniePoruszanySmiec.x, aktulaniePoruszanySmiec.y, aktulaniePoruszanySmiec.szer, aktulaniePoruszanySmiec.szer)) {
						if(aktulaniePoruszanySmiec.rodzajSmiecia == Smiec.RodzajSmiecia.PAPIER) {
							wywalSmiecia(aktulaniePoruszanySmiec);
							timerstart = timerstart + 1000;
							playSound(new File("src/res/papier.wav"));
						} else {
							System.out.println("zly smietnik");
							punkty--;
							timerstart = timerstart - 3000;
						}
					}
					
					else if(smietnikSzklo.contains(aktulaniePoruszanySmiec.x, aktulaniePoruszanySmiec.y, aktulaniePoruszanySmiec.szer, aktulaniePoruszanySmiec.szer)) {
						if(aktulaniePoruszanySmiec.rodzajSmiecia == Smiec.RodzajSmiecia.SZKLO) {
							wywalSmiecia(aktulaniePoruszanySmiec);
							timerstart = timerstart + 1000;
							playSound(new File("src/res/szklo.wav"));
						} else {
							System.out.println("zly smietnik");
							punkty--;
							timerstart = timerstart - 3000;
						}
					}
					
					else if(smietnikBio.contains(aktulaniePoruszanySmiec.x, aktulaniePoruszanySmiec.y, aktulaniePoruszanySmiec.szer, aktulaniePoruszanySmiec.szer)) {
						if(aktulaniePoruszanySmiec.rodzajSmiecia == Smiec.RodzajSmiecia.BIO) {
							wywalSmiecia(aktulaniePoruszanySmiec);
							timerstart = timerstart + 1000;
							playSound(new File("src/res/bio.wav"));
						} else {
							System.out.println("zly smietnik");
							punkty--;
							timerstart = timerstart - 3000;
						}
					}
				}
				
				usunsmiecia =  true;
			}
        	
        });
    }
    
    /** uruchomienie watku **/
    public synchronized void start(){
        watek = new Thread(this);
        watek.start();
        running = true;
    }
    /** synchronizacja watku **/
    public synchronized void stop(){
        try{
            watek.join();
            running=false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        long lastTime = System.nanoTime();
        double amountOFTicks = 60.0;
        double ns = 1000000000 / amountOFTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();

        
        initGame();
        
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while( delta >= 1){
                cykl();
                delta--;
            }
            if(running)
                render();

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
            }
            
            try {
				watek.sleep(10);
			} catch (InterruptedException e) {  
				e.printStackTrace();
			}
        }
        stop();
    }
 
    /** wczytanie obrazow **/
    private void initGame() {
    	try {
			tlo = ImageIO.read(getClass().getResource("/res/tlo.png"));
			oplastik = ImageIO.read(getClass().getResource("/res/plastik.png"));
			obio = ImageIO.read(getClass().getResource("/res/ogryzek.png"));
			oszklo = ImageIO.read(getClass().getResource("/res/szklo.png"));
			opapier = ImageIO.read(getClass().getResource("/res/papier.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	smieci = new ArrayList<Smiec>(); 
    	
    	smietnikPlastik = new Rectangle(959, 69, 300, 205);
    	smietnikPapier = new Rectangle(51, 67, 250, 255);
    	smietnikSzklo = new Rectangle(56, 702, 250, 205);
    	smietnikBio = new Rectangle(959, 680, 300, 205);
    }
    /** Usuwanie smieci 
     * @param smiec jaki smiec **/
    private void wywalSmiecia(Smiec smiec) {
    	smieci.remove(aktulaniePoruszanySmiec);
    	
    	punkty++; 
    }
    
    /** tworzenie smieci **/
    private void spawnSmieci() {
    	int iloscSmieci = 10;
    	int obszarX = 400;
    	int obszarY = 200;
    	Random rand = new Random();
    	for(int i = 0; i < iloscSmieci; i++) {
    		int x = 375 + (int) (rand.nextFloat() * obszarX);
    		int y = 400 + (int) (rand.nextFloat() * obszarY);
    		 
    		Smiec.RodzajSmiecia rodzajSmiecia = Smiec.RodzajSmiecia.values()[rand.nextInt(Smiec.RodzajSmiecia.values().length)];
    		Image obrazekSmiecia = null;
    		switch(rodzajSmiecia) {
    		case PLASTIK:
    			obrazekSmiecia = oplastik;
    			break;
    		case SZKLO:
    			obrazekSmiecia = oszklo;
    			break;
    		case BIO:
    			obrazekSmiecia = obio;
    			break;
    		case PAPIER:
    			obrazekSmiecia = opapier;
    			break;
    		}
    		
    		Smiec smiec = new Smiec(x, y, rodzajSmiecia, obrazekSmiecia);
    		smieci.add(smiec); 
    	}
    	
    }
    /**   **/
    private void cykl(){
    	if(pczas <= 0) {
    		jkoniec = true;
    	}
    		
    	if(usunsmiecia == true) {
    		aktulaniePoruszanySmiec = null;
    		usunsmiecia = false;
    	}
    		
    	if(aktulaniePoruszanySmiec != null) {
    		Point m = MouseInfo.getPointerInfo().getLocation();
    		aktulaniePoruszanySmiec.x = m.x - getLocationOnScreen().x - aktulaniePoruszanySmiec.szer/2;
    		aktulaniePoruszanySmiec.y = m.y - getLocationOnScreen().y - aktulaniePoruszanySmiec.szer/2;;
    	}
    	
    	
    	//spoawnowanie smieci w nieskononosc
    	if(smieci.size() <= 0)
    		spawnSmieci();
    }
    /** Renderowanie obrazu w czasie gry **/
    private void render(){

        
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.drawImage(tlo, 0, 0, szer, wys, null);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
        g.setColor(Color.WHITE);

        if (jkoniec == true) {
        	g.drawString("Punkty: "+ punkty, 0 , 970);
        	g.drawString("Pozosta³y czas: "+ upczas, 300, 970);
            g.drawString("Nacisnij ¿eby zagraæ ponownie", 220, 500);
            
        	
        }else {
        //tlo
        g.drawImage(tlo, 0, 0, szer, wys, null);
        
        //wszystkie smieci
        for(Smiec smiec : smieci) {
        	smiec.render(g);
        }
        //ustawienia czcionki

        g.setColor(Color.WHITE);
        //wysiwetl punkty
        g.drawString("Punkty: "+ punkty, 0, 970);
        
        upczas = (System.currentTimeMillis() - timerstart) / 1000;
        //wyswietlanie czasu
        pczas = 120 - upczas; 
        g.drawString("Pozosta³y czas: "+ pczas, 300, 970);
        }
        g.dispose();
        bs.show();
    }
    
    /** klasa odpowiadajaca za odtwarzanie dzwiekow 
     * @param f = plik audio **/
    
    public static void playSound(final File f) {
        new Thread(new Runnable() {
          public void run() {
            try {
              Clip clip = AudioSystem.getClip();
              AudioInputStream inputStream = AudioSystem.getAudioInputStream(f);
              clip.open(inputStream);
              clip.start(); 
            } catch (Exception e) {
            }
          }
        }).start();
    }
    /** main gry **/
    public static void main(String[] args) {
        new Main();
    }
}
