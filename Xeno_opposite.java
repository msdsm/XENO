/*クライアント側*/
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Xeno_opposite{
	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(is);
		byte crlf [] = {13,10};//キャリッジリターン(CR),改行(LF)の並び で、送信時の区切り用

		Socket socket;//ソケット

		int cnt = 0;
		int tmp = -1;
		String send = "";
		String receive = "";
		do{
			if(cnt==0)
				System.out.println("文字を表示するはやさを決めてください");
			else
				System.out.println("入力が不適当です");
			cnt++;
			System.out.println("遅い...0   普通...1   速い...2");
			tmp = sc.nextInt();
		}while(tmp<0||tmp>2);

		Xeno_message m = new Xeno_message(tmp);
		
		try {
			m.message("対戦相手のIPアドレスを入力してください");
			m.message2("接続するサーバーのIPアドレス入力>"); //追加
			String IPAddress = br.readLine(); //キー1行入力
			socket = new Socket( IPAddress ,  80); //接続

			OutputStream os = socket.getOutputStream();
			
			InputStream sok_in = socket.getInputStream();
			InputStreamReader sok_isr = new InputStreamReader(sok_in);
			BufferedReader sok_br = new BufferedReader(sok_isr);

			m.message("対戦相手が見つかりました！");
			m.message2("名前を入力してください>>");
			String name = br.readLine();	//キー1行入力
			Xeno_player opposite = new Xeno_player(name);
			receive = sok_br.readLine();//受信データ取得
			m.message("対戦相手は、"+receive+"さんです！");
			os.write(opposite.getname().getBytes());//送信
			os.write(crlf);
			/*ルール確認*/
			cnt=0;
			do{
				if(cnt>0)
					m.message("入力が不適当です");
				m.message2("ルール説明は必要ですか？(必要...1/不必要...0)>>");
				tmp = sc.nextInt();
				cnt++;
			}while(tmp!=0&&tmp!=1);
			Xeno_rule rule = new Xeno_rule(tmp);
			m.message(rule.rule());
			/*ルール確認終了*/

			/*ゲーム開始*/
			m.message("それではゲームを開始します!");
			receive = sok_br.readLine();//手札の情報取得
			m.message(receive);
			while(true){
				/*相手のターンの処理を受け取る*/
				m.message("相手のターン中です。しばらくお待ちください...");
				receive = sok_br.readLine();//相手が捨てたカードの情報取得
				if(receive.equals("NODECK")){//山札がなくなった場合
					receive = sok_br.readLine();//この後の処理の説明
					m.message(receive);
					receive = sok_br.readLine();//結果取得
					m.message(receive);
					System.exit(0);
				}
				m.message(receive);//相手が捨てたカードを表示
				receive = sok_br.readLine();//墓地の情報取得
				m.message("現在の墓地の状態です");
				m.message(receive);//墓地出力
				receive = sok_br.readLine();//カードの効果の結果、ゲームが終わるかどうか取得
				if(receive.equals("END")){//ゲーム終了
					receive = sok_br.readLine();//結果取得
					m.message(receive);
					System.exit(0);
				}else{//終了しない場合
					receive = sok_br.readLine();//結果取得
					m.message(receive);//相手が捨てたカードの効果を表示
				}
				/*相手のターンの情報出力終了*/

				/*自分のターンの処理開始*/
				while(true){
					receive = sok_br.readLine();
					if(receive.equals("OUTPUT")){
						receive = sok_br.readLine();
						m.message(receive);
					}
					if(receive.equals("INPUT")){
						receive = sok_br.readLine();
						m.message2(receive);
						send = br.readLine();
						os.write(send.getBytes());
						os.write(crlf);
					}
					if(receive.equals("GRAVE")){
						receive = sok_br.readLine();
						m.message("現在の墓場の状態です");
						m.message(receive);
					}
					if(receive.equals("NEXT")){//自分のターン終了
						break;
					}
					if(receive.equals("END")){//ゲーム終了
						System.exit(0);
					}
					if(receive.equals("NODECK")){//デッキなくなったとき
						receive = sok_br.readLine();//この後の処理の説明
						m.message(receive);
						receive = sok_br.readLine();//結果取得
						m.message(receive);
						System.exit(0);
					}
				}
				/*自分のターン終了*/
			}
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		System.out.print("  Enterキーで終了");
		try{System.in.read();}catch(Exception e){}
	}
}