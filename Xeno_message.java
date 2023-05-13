/*出力する文字列の表示を工夫するクラス*/
public class Xeno_message{
	private int mode;
	public Xeno_message(int x){
		if(x==0)//おそい
			this.mode = 200;
		else if(x==1)//普通
			this.mode = 100;
		else//はやい
			this.mode = 50;
	}
	public void message(String s) throws InterruptedException{//改行する
		for(int i = 0; i < s.length(); i++){
			try{
				Thread.sleep(mode);
			}catch(InterruptedException e){
				System.out.println(e.toString());
			}
			if(i!=s.length()-1)
				System.out.print(s.charAt(i));
			else 
				System.out.println(s.charAt(i));
		}
	}
	public void message2(String s)throws InterruptedException{//改行しない
		for(int i = 0; i < s.length(); i++){
			try{
				Thread.sleep(mode);
			}catch(InterruptedException e){
				System.out.println(e.toString());
			}
			System.out.print(s.charAt(i));
		}
	}
	public void selectcard(String card1,String card2)throws Exception{
		/*第1引数が事前に持ってるカードで第2引数が引いたカード*/
		this.message("あなたは"+card1+"を持っていました。"+"新しく山札から"+card2+"を引きました。");
		this.message2("どちらを捨てますか？>>");
	}

	public void debug(String s,int x){//動作確認のために使った
		System.out.println("debug start");
		System.out.println(s+": "+x);
		System.out.println("debug end");
	}
}