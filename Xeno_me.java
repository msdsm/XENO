/*サーバー側*/
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Random;

public class Xeno_me{
	private static final int BOY = 1;		//少年
	private static final int SOLDIER = 2;	//兵士
	private static final int PREDICTOR = 3;	//占い師
	private static final int MAIDEN = 4;	//乙女
	private static final int DEATH = 5;		//死神
	private static final int NOBLE = 6;		//貴族
	private static final int WISEMAN = 7; 	//賢者
	private static final int SPIRIT = 8;	//精霊
	private static final int EMPEROR = 9;	//皇帝
	private static final int HERO = 10;		//英雄
	private static final int INVALID = -1; //無効
	public static boolean boy_flag = false;
	public static boolean noble_flag = false;
	public static boolean wiseman_flag_me = false;
	public static boolean wiseman_flag_opposite = false;
	public static boolean maiden_flag_me = false;
	public static boolean maiden_flag_opposite = false;

	public static void main(String[] args)throws Exception{
		Scanner sc = new Scanner(System.in);
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(is);
		byte crlf [] = {13,10};//キャリッジリターン(CR),改行(LF)の並び で、送信時の区切り用
		int cnt = 0;
		int tmp = -1;
		int num = 0;
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
		Xeno_position s = new Xeno_position();

		try {
			//サーバー接続
			InetAddress local = InetAddress.getLocalHost();//このマシンの情報取得
			String localAdr = local.getHostAddress();
			System.out.println("このマシンのIPアドレス" + localAdr);	
			m.message("対戦相手を探しています...");
			
			//サーバー用ソケットをポート80で作成
			ServerSocket serverSock = new ServerSocket(80); 

			//クライアントからの接続を待ち、接続してきたら、
			//	そのクライアントと通信するソケットを取得する。
			Socket clientSock = serverSock.accept();
			serverSock.close();
			
			//クライアントからのリクエストメッセージ送信情報を受信して表示
			InputStream sok_in = clientSock.getInputStream();
			InputStreamReader sok_is = new InputStreamReader(sok_in);
			BufferedReader sok_br = new BufferedReader(sok_is);
			
			OutputStream os = clientSock.getOutputStream();
			
			m.message("対戦相手が見つかりました！");
			m.message2("名前を入力してください>>");
			String name = br.readLine();	//キー1行入力
			Xeno_player me = new Xeno_player(name);
			os.write(me.getname().getBytes());//送信
			os.write(crlf);
			String receive = sok_br.readLine();//受信データ取得
			m.message("対戦相手は、"+receive+"さんです！");
			Xeno_player opposite = new Xeno_player(receive);
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
			Xeno xeno = new Xeno(me,opposite);
			xeno.start();//山札完成、プレイヤーの最初の手札決定
			m.message("あなたの手札は"+s.toString(xeno.getcard(me))+"です");
			os.write(("あなたの手札は"+s.toString(xeno.getcard(opposite))+"です").getBytes());
			os.write(crlf);
			int discard=0;
			int card=0,card1=0,card2=0,card3=0;
			//xeno.debug();

			/*るーぷ*/
			while(true){
				/*meの処理*/
				if(xeno.empty()){//山札がないとき
					m.message("山札がなくなったため、互いの手札の数字が大きい方の勝利となります。あなたの手札は"+s.toString(xeno.getcard(me))+"であり相手の手札は"+s.toString(xeno.getcard(opposite))+"です");
					os.write("NODECK".getBytes());
					os.write(crlf);
					os.write(("山札がなくなったため、互いの手札の数字が大きい方の勝利となります。あなたの手札は"+s.toString(xeno.getcard(opposite))+"であり相手の手札は"+s.toString(xeno.getcard(me))+"です").getBytes());
					os.write(crlf);
					if(xeno.getcard(me)>xeno.getcard(opposite)){//meの勝利
						m.message("よって、"+xeno.getname(me)+"の勝利です。おめでとうございます！！");
						os.write(("よって、"+xeno.getname(me)+"の勝利です。残念ながら負けてしまいました...").getBytes());
						os.write(crlf);
						System.exit(0);
					}else if(xeno.getcard(me)<xeno.getcard(opposite)){//oppositeの勝利
						m.message("よって、"+xeno.getname(opposite)+"の勝利です。残念ながら負けてしまいました...");
						os.write(("よって、"+xeno.getname(opposite)+"の勝利です。おめでとうございます！！").getBytes());
						os.write(crlf);
						System.exit(0);
					}else{//引き分け
						m.message("よって、このゲームは引き分けで終了です。");
						os.write("よって、このゲームは引き分けで終了です。".getBytes());
						os.write(crlf);
						System.exit(0);
					}
				}
				card1 = xeno.getcard(me);//事前に持ってるカード
				if(wiseman_flag_me && xeno.size()>=3){//賢者の効果発動
					m.message("前のターンに捨てた"+s.toString(WISEMAN)+"の効果が発動します");
					wiseman_flag_me = false;
					card1 = xeno.drawcard();
					card2 = xeno.drawcard();
					card3 = xeno.drawcard();
					m.message(s.toString(card1)+"と"+s.toString(card2)+"と"+s.toString(card3)+"を引きました");
					cnt=0;
					do{
						if(cnt>0)
							m.message("入力が不適当です");
						m.message2("どれを手札に加えますか？>>");
						card = sc.nextInt();
						cnt++;
					}while(card!=card1&&card!=card2&&card!=card3);
					if(card==card1){
						xeno.returncard(card2);
						xeno.returncard(card3);
					}else if(card==card2){
						xeno.returncard(card1);
						xeno.returncard(card3);
					}else{
						xeno.returncard(card1);
						xeno.returncard(card2);
					}
					card2 = card;//引いたカードにセット
					card1 = xeno.getcard(me);//事前に持っているカードに戻す
				}else if(wiseman_flag_me){
					m.message("前のターンに"+s.toString(WISEMAN)+"を捨てていますが、山札の残り枚数が3枚もないので効果は発動されません");
					card2 = xeno.drawcard();//引いたカード
				}else{//普通に引く
					card2 = xeno.drawcard();//引いたカード
				}	
				//xeno.debug();
				if(card1==HERO||card2==HERO){
					discard = (card1==HERO)?card2:card1;
					m.message(s.toString(card2)+"を引きました。"+s.toString(HERO)+"を捨てることはできないので"+s.toString(discard)+"を捨てます");
				}else{
					m.selectcard(s.toString(card1),s.toString(card2));//カードを選ばせる
					cnt=0;
					do{
						if(cnt>0)
							m.message2("入力が不適当です。どちらを捨てますか？>>");
						discard = sc.nextInt();
						cnt++;
						//m.debug("discard",discard);
						//m.debug("card1",card1);
						//m.debug("card2",card2);
					}while(discard!=card1&&discard!=card2);//捨てるカード決定
				}
				m.message(s.toString(discard)+"が捨てられました");
				os.write(("相手は"+s.toString(discard)+"を捨てました").getBytes());//捨てたカード送信
				os.write(crlf);
				xeno.setcard(me,(discard==card1)?card2:card1);//捨てた結果持ち札の変更
				xeno.discard(discard);//墓地に捨てるカード追加
				m.message("現在の墓地の状態です");
				m.message(xeno.getGraveyard());
				os.write(xeno.getGraveyard().getBytes());//墓地の情報送信
				os.write(crlf);
				//xeno.debug();



				if(maiden_flag_opposite){//前の相手のターンに守護が発動した場合
					maiden_flag_opposite = false;
					if((discard==BOY&&boy_flag)||discard==SOLDIER||discard==PREDICTOR||
						discard==DEATH||discard==NOBLE||discard==SPIRIT||discard==EMPEROR)
						discard = INVALID;
				}
				switch(discard){
					case BOY://革命
					if(boy_flag){//2枚目の場合
						card1 = xeno.drawcard();
						card2 = xeno.getcard(opposite);
						if(card1!=-1){//山札が空でなければ
							cnt=0;
							m.message("2枚目の少年なので公開処刑が発動します");
							do{
								m.message("相手は"+s.toString(card1)+"と"+s.toString(card2)+"を持っています");
								if(cnt>0)
									m.message2("入力が不適当です。");
								m.message2("どちらを捨てさせますか>>");
								card = sc.nextInt();
								cnt++;
							}while(card!=card1&&card!=card2);
							xeno.setcard(opposite,(card==card1)?card2:card1);//捨てた結果持ち札の変更
							xeno.discard(card);//墓地に捨てるカード追加
							if(card==HERO){//英雄の場合は転生札で復活
								os.write("NOTEND".getBytes());
								os.write(crlf);
								os.write(("2枚目の"+s.toString(BOY)+"が捨てられて公開処刑が発動しました。あなたは"+s.toString(card1)+"を引いて"+s.toString(card)+"を捨てられました。"+s.toString(HERO)+"が捨てられたので転生札で復活します。"+"今持っている手札である"+s.toString(xeno.getcard(opposite))+"を捨てて転生札である"+s.toString(xeno.getreincarnation())+"が手札になりました").getBytes());
								os.write(crlf);
								m.message(s.toString(HERO)+"を捨てたので相手は転生札で復活しました");
								xeno.discard(xeno.getcard(opposite));//今の手札捨てる
								xeno.setcard(opposite,xeno.getreincarnation());;//転生札で復活
							}else{//英雄以外のカードが捨てられたときの情報を送信する
								os.write("NOTEND".getBytes());
								os.write(crlf);
								os.write(("2枚目の"+s.toString(BOY)+"が捨てられて公開処刑が発動しました。あなたは"+s.toString(card1)+"を引いて"+s.toString(card)+"を捨てられました").getBytes());
								os.write(crlf);
							}
						}else{//山札が空
							m.message("山札がもうないので公開処刑は発動されません。");
							os.write("NOTEND".getBytes());
							os.write(crlf);
							os.write(("2枚目の"+s.toString(BOY)+"が捨てられましたが山札がもうないので公開処刑は発動されません。").getBytes());
							os.write(crlf);
						}
					}else{//1枚目の場合
						boy_flag = true;
						os.write("NOTEND".getBytes());
						os.write(crlf);
						os.write(("1枚目の"+s.toString(BOY)+"が捨てられて何も起こりませんでした").getBytes());
						os.write(crlf);
					}
					break;

					case SOLDIER://捜査
					m.message2("相手のカードの数字は何だと思いますか？>>");
					card1 = sc.nextInt();
					if(card1==xeno.getcard(opposite)){//ゲーム終了
						m.message("当たりました。");
						m.message(xeno.getname(me)+"の勝ちです！おめでとうございます！");
						os.write("END".getBytes());
						os.write(crlf);
						os.write((s.toString(SOLDIER)+"が捨てられて捜査が発動しました。相手は"+s.toString(card1)+"と予想しあなたの手札は"+s.toString(xeno.getcard(opposite))+"なので相手は当てました。"+xeno.getname(me)+"の勝ちです。残念ながら負けてしまいました...").getBytes());
						os.write(crlf);
						System.exit(0);
						
					}else{
						m.message("違いました。");
						os.write("NOTEND".getBytes());
						os.write(crlf);
						os.write((s.toString(SOLDIER)+"が捨てられて捜査が発動しました。相手は"+s.toString(card1)+"と予想しましたがあなたの手札は"+s.toString(xeno.getcard(opposite))+"なので相手は外しました").getBytes());
						os.write(crlf);
					}
					break;

					case PREDICTOR://透視
					m.message("透視が発動します。相手の手札は"+s.toString(xeno.getcard(opposite))+"です。");
					os.write("NOTEND".getBytes());
					os.write(crlf);
					os.write((s.toString(PREDICTOR)+"が捨てられて透視が発動しました。相手はあなたの手札が"+s.toString(xeno.getcard(opposite))+"であることを知りました").getBytes());
					os.write(crlf);
					break;

					case MAIDEN://守護
					maiden_flag_me = true;
					os.write("NOTEND".getBytes());
					os.write(crlf);
					os.write((s.toString(MAIDEN)+"が捨てられて守護が発動しました。相手は次のあなたのターンで相手に対する効果を発動しても無効化されます").getBytes());
					os.write(crlf);
					break;

					case DEATH://疫病
					cnt=0;
					card1 = xeno.getcard(opposite);
					card2 = xeno.drawcard();
					card3 = card2;
					if(card2!=-1){//山札が空でなければ
						do{
							if(cnt>0)
								m.message("入力が不適当です");
							m.message2("相手の手札の1枚目、2枚目どちらを捨てさせますか？>>");
							card = sc.nextInt();
							cnt++;
						}while(card!=1&&card!=2);
						Random r = new Random();
						tmp = r.nextInt(2);
						if(tmp==0){//シャッフル
							int tmp2 = card1;
							card1 = card2;
							card2 = tmp2;
						}
						if(card==1)
							tmp = card1;
						else
							tmp = card2;
						xeno.setcard(opposite,(tmp==card1)?card2:card1);//捨てた結果持ち札の変更
						xeno.discard(tmp);//墓地に捨てるカード追加
						if(tmp==HERO){//英雄の場合は転生札で復活
							os.write("NOTEND".getBytes());
							os.write(crlf);
							os.write((s.toString(DEATH)+"が捨てられて疫病が発動しました。あなたは"+s.toString(card3)+"を引いて"+s.toString(HERO)+"を捨てられました。"+s.toString(HERO)+"が捨てられたので転生札で復活します。"+"今持っている手札である"+s.toString(xeno.getcard(opposite))+"を捨てて転生札である"+s.toString(xeno.getreincarnation())+"が手札になりました").getBytes());
							os.write(crlf);
							xeno.discard(xeno.getcard(opposite));//今の手札捨てる
							xeno.setcard(opposite,xeno.getreincarnation());;//転生札で復活
						}else{//英雄以外の場合に情報送信
							os.write("NOTEND".getBytes());
							os.write(crlf);
							os.write((s.toString(DEATH)+"が捨てられて疫病が発動しました。あなたは"+s.toString(card3)+"を引いて"+s.toString(tmp)+"を捨てられました").getBytes());
							os.write(crlf);
						}
					}else{//山札がない
						m.message("山札がもうないので疫病は発動されません。");
						os.write("NOTEND".getBytes());
						os.write(crlf);
						os.write((s.toString(DEATH)+"が捨てられましたが山札がもうないので疫病は発動されませんでした。").getBytes());
						os.write(crlf);
					}
					break;

					case NOBLE://対決
					if(noble_flag){//2回目は対決
						m.message("2枚目の"+s.toString(NOBLE)+"が捨てられました、対決が発動します");
						if(xeno.getcard(me)>xeno.getcard(opposite)){//ゲーム終了　自分の勝ち
							os.write("END".getBytes());
							os.write(crlf);
							os.write(("2枚目の"+s.toString(NOBLE)+"が捨てられて対決が発動しました。相手の手札は"+s.toString(xeno.getcard(me))+"でした。"+xeno.getname(me)+"の勝ちです。残念ながら負けてしまいました...").getBytes());
							os.write(crlf);
							m.message("相手の手札は"+s.toString(xeno.getcard(opposite))+"でした");
							m.message(xeno.getname(me)+"の勝ちです。おめでとうございます！");
							System.exit(0);
						}else if(xeno.getcard(me)<xeno.getcard(opposite)){//ゲーム終了　相手の勝ち
							os.write("END".getBytes());
							os.write(crlf);
							os.write(("2枚目の"+s.toString(NOBLE)+"が捨てられて対決が発動しました。相手の手札は"+s.toString(xeno.getcard(me))+"でした。"+xeno.getname(opposite)+"の勝ちです。おめでとうございます！").getBytes());
							os.write(crlf);
							m.message("相手の手札は"+s.toString(xeno.getcard(opposite))+"でした");
							m.message(xeno.getname(opposite)+"の勝ちです。残念ながら負けてしまいました...");
							System.exit(0);
						}else{//引き分け
							m.message("相手の手札は"+s.toString(xeno.getcard(opposite))+"です");
							m.message("引き分けなのでゲームは終了せず続行されます");
							os.write("NOTEND".getBytes());
							os.write(crlf);
							os.write(("2枚目の"+s.toString(NOBLE)+"が捨てられて対決が発動しました。相手の手札は"+s.toString(xeno.getcard(me))+"でした。引き分けなのでゲームは終了せず続行されます").getBytes());
							os.write(crlf);
						}
					}else{//1回目は札を見せ合うだけ
						m.message("1回目の"+s.toString(NOBLE)+"なので手札を見せ合います。相手の手札は"+s.toString(xeno.getcard(opposite))+"です");
						os.write("NOTEND".getBytes());
						os.write(crlf);
						os.write(("1枚目の"+s.toString(NOBLE)+"が捨てられたので互いに手札を見せ合います。"+"相手の手札は"+s.toString(xeno.getcard(me))+"です").getBytes());
						os.write(crlf);
					}
					break;

					case WISEMAN://選択
					wiseman_flag_me = true;
					os.write("NOTEND".getBytes());
					os.write(crlf);
					os.write((s.toString(WISEMAN)+"が捨てられて次の相手のターンに選択が発動します").getBytes());
					os.write(crlf);
					break;

					case SPIRIT://交換
					m.message(s.toString(xeno.getcard(me))+"を相手に渡しました");
					m.message(s.toString(xeno.getcard(opposite))+"を手に入れました");
					os.write("NOTEND".getBytes());
					os.write(crlf);
					os.write((s.toString(SPIRIT)+"が捨てられて交換が発動しました。"+s.toString(xeno.getcard(opposite))+"を相手に渡して"+s.toString(xeno.getcard(me))+"を手に入れました").getBytes());
					os.write(crlf);
					card1 = xeno.getcard(me);
					card2 = xeno.getcard(opposite);
					xeno.setcard(me,card2);
					xeno.setcard(opposite,card1);
					break;

					case EMPEROR://公開処刑
					card1 = xeno.getcard(opposite);
					card2 = xeno.drawcard();
					if(card2!=-1){//山札が空でなければ
						cnt=0;
						do{
							m.message("相手は"+s.toString(card1)+"と"+s.toString(card2)+"を持っています");
							if(cnt>0)
								m.message2("入力が不適当です。");
							m.message2("どちらを捨てさせますか>>");
							card = sc.nextInt();
							cnt++;
						}while(card!=card1&&card!=card2);
						xeno.setcard(opposite,(card==card1)?card2:card1);//捨てた結果持ち札の変更
						xeno.discard(card);//墓地に捨てるカード追加
						if(card==HERO){//英雄の場合はゲーム終了
							m.message("皇帝の効果で英雄を捨てたので"+xeno.getname(me)+"の勝ちです。おめでとうございます！");
							os.write("END".getBytes());
							os.write(crlf);
							os.write((s.toString(EMPEROR)+"が捨てられて公開処刑が発動しました。あなたは"+s.toString(card2)+"を引いて"+s.toString(HERO)+"を捨てられました。"+s.toString(HERO)+"が"+s.toString(EMPEROR)+"によって捨てられたのでゲームは終了です。"+xeno.getname(me)+"の勝ちです。残念ながら負けてしまいました...").getBytes());
							os.write(crlf);
							System.exit(0);
						}else{//英雄以外の場合の情報送信
							os.write("NOTEND".getBytes());
							os.write(crlf);
							os.write((s.toString(EMPEROR)+"が捨てられて公開処刑が発動しました。あなたは"+s.toString(card2)+"を引いて"+s.toString(card)+"を捨てられました").getBytes());
							os.write(crlf);
						}
					}else{//山札がない
						m.message("山札がもうないので公開処刑は発動されません。");
						os.write("NOTEND".getBytes());
						os.write(crlf);
						os.write((s.toString(EMPEROR)+"が捨てられましたが山札がもうないので公開処刑は発動されませんでした。").getBytes());
						os.write(crlf);
					}
					break;

					case HERO://潜伏・転生
					//英雄は捨てられないのでこの分岐にくることはありえない
					break;

					case INVALID://相手が守護を発動した場合
					m.message("相手が守護を発動していたので効果が無効化されました");
					os.write("NOTEND".getBytes());
					os.write(crlf);
					os.write(("前のターンであなたは"+s.toString(MAIDEN)+"を捨てていたので守護が発動して効果が無効化されました").getBytes());
					os.write(crlf);
					break;
				}
				/*meの処理終了*/
				m.message("相手のターン中です。しばらくお待ちください...");

				/*---------------------------------------------------------------------------------*/

				/*oppositeの処理*/
				if(xeno.empty()){//山札がないとき
					m.message("山札がなくなったため、互いの手札の数字が大きい方の勝利となります。あなたの手札は"+s.toString(xeno.getcard(me))+"であり相手の手札は"+s.toString(xeno.getcard(opposite))+"です");
					os.write("NODECK".getBytes());
					os.write(crlf);
					os.write(("山札がなくなったため、互いの手札の数字が大きい方の勝利となります。あなたの手札は"+s.toString(xeno.getcard(opposite))+"であり相手の手札は"+s.toString(xeno.getcard(me))+"です").getBytes());
					os.write(crlf);
					if(xeno.getcard(me)>xeno.getcard(opposite)){//meの勝利
						m.message("よって、"+xeno.getname(me)+"の勝利です。おめでとうございます！！");
						os.write(("よって、"+xeno.getname(me)+"の勝利です。残念ながら負けてしまいました...").getBytes());
						os.write(crlf);
						System.exit(0);
					}else if(xeno.getcard(me)<xeno.getcard(opposite)){//oppositeの勝利
						m.message("よって、"+xeno.getname(opposite)+"の勝利です。残念ながら負けてしまいました...");
						os.write(("よって、"+xeno.getname(opposite)+"の勝利です。おめでとうございます！！").getBytes());
						os.write(crlf);
						System.exit(0);
					}else{//引き分け
						m.message("よって、このゲームは引き分けで終了です。");
						os.write("よって、このゲームは引き分けで終了です。".getBytes());
						os.write(crlf);
						System.exit(0);
					}
				}
				card1 = xeno.getcard(opposite);//事前に持ってるカード
				if(wiseman_flag_opposite && xeno.size()>=3){//賢者の効果発動
					os.write("OUTPUT".getBytes());
					os.write(crlf);
					os.write(("前のターンに捨てた"+s.toString(WISEMAN)+"の効果が発動します").getBytes());
					os.write(crlf);
					wiseman_flag_opposite = false;
					card1 = xeno.drawcard();
					card2 = xeno.drawcard();
					card3 = xeno.drawcard();
					os.write("OUTPUT".getBytes());
					os.write(crlf);
					os.write((s.toString(card1)+"と"+s.toString(card2)+"と"+s.toString(card3)+"を引きました").getBytes());
					os.write(crlf);
					cnt=0;
					do{
						if(cnt>0){
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write(("入力が不適当です").getBytes());
							os.write(crlf);
						}
						os.write("INPUT".getBytes());
						os.write(crlf);
						os.write(("どれを手札に加えますか？>>").getBytes());
						os.write(crlf);
						receive = sok_br.readLine();
						num = Integer.parseInt(receive);
						card = num;
						cnt++;
					}while(card!=card1&&card!=card2&&card!=card3);
					if(card==card1){
						xeno.returncard(card2);
						xeno.returncard(card3);
					}else if(card==card2){
						xeno.returncard(card1);
						xeno.returncard(card3);
					}else{
						xeno.returncard(card1);
						xeno.returncard(card2);
					}
					card2 = card;//引いたカードにセット
					card1 = xeno.getcard(opposite);//事前に持っているカードに戻す
				}else if(wiseman_flag_opposite){//賢者の効果発動できない場合
					os.write("OUTPUT".getBytes());
					os.write(crlf);
					os.write(("前のターンに"+s.toString(WISEMAN)+"を捨てていますが、山札の残り枚数が3枚もないので効果は発動されません").getBytes());
					os.write(crlf);
					card2 = xeno.drawcard();//普通に引く
				}else{//普通に引く
					card2 = xeno.drawcard();//引いたカード
				}	
				if(card1==HERO||card2==HERO){
					discard = (card1==HERO)?card2:card1;
					os.write("OUTPUT".getBytes());
					os.write(crlf);
					os.write((s.toString(HERO)+"を捨てることはできないので"+s.toString(discard)+"を捨てます").getBytes());
					os.write(crlf);
				}else{
					os.write("OUTPUT".getBytes());
					os.write(crlf);
					os.write(("あなたは"+s.toString(card1)+"を持っていました。"+"新しく山札から"+s.toString(card2)+"を引きました。").getBytes());
					os.write(crlf);
					os.write("INPUT".getBytes());
					os.write(crlf);
					os.write(("どちらを捨てますか？>>").getBytes());
					os.write(crlf);
					cnt=0;
					do{
						if(cnt>0){
							os.write("INPUT".getBytes());
							os.write(crlf);
							os.write(("入力が不適当です。どちらを捨てますか？>>").getBytes());
							os.write(crlf);
						}
						receive = sok_br.readLine();
						num = Integer.parseInt(receive);
						discard = num;
						cnt++;
						//m.debug("discard",discard);
						//m.debug("card1",card1);
						//m.debug("card2",card2);
					}while(discard!=card1&&discard!=card2);//捨てるカード決定
				}
				os.write("OUTPUT".getBytes());
				os.write(crlf);
				os.write((s.toString(discard)+"が捨てられました").getBytes());
				os.write(crlf);
				m.message("相手は"+s.toString(discard)+"を捨てました");
				xeno.setcard(opposite,(discard==card1)?card2:card1);//捨てた結果持ち札の変更
				xeno.discard(discard);//墓地に捨てるカード追加
				m.message("現在の墓地の状態です");
				m.message(xeno.getGraveyard());
				os.write("GRAVE".getBytes());
				os.write(crlf);
				os.write(xeno.getGraveyard().getBytes());//墓地の情報送信
				os.write(crlf);
				/*カードを引いて捨てる処理終了*/
				/*カードの効果処理開始*/
				if(maiden_flag_me){//前の相手のターンに守護が発動した場合
					maiden_flag_me = false;
					if((discard==BOY&&boy_flag)||discard==SOLDIER||discard==PREDICTOR||
						discard==DEATH||discard==NOBLE||discard==SPIRIT||discard==EMPEROR)
						discard = INVALID;
				}
				switch(discard){
					case BOY://革命
					if(boy_flag){//2枚目の場合
						card1 = xeno.drawcard();
						card2 = xeno.getcard(me);
						if(card1!=-1){//山札が空でなければ
							cnt=0;
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write(("2枚目の少年なので公開処刑が発動します").getBytes());
							os.write(crlf);
							do{
								os.write("OUTPUT".getBytes());
								os.write(crlf);
								os.write(("相手は"+s.toString(card1)+"と"+s.toString(card2)+"を持っています").getBytes());
								os.write(crlf);
								if(cnt>0){
									os.write("OUTPUT".getBytes());
									os.write(crlf);
									os.write(("入力が不適当です。").getBytes());
									os.write(crlf);
								}
								os.write("INPUT".getBytes());
								os.write(crlf);
								os.write(("どちらを捨てさせますか>>").getBytes());
								os.write(crlf);
								receive = sok_br.readLine();
								num = Integer.parseInt(receive);
								card = num;
								cnt++;
							}while(card!=card1&&card!=card2);
							xeno.setcard(me,(card==card1)?card2:card1);//捨てた結果持ち札の変更
							xeno.discard(card);//墓地に捨てるカード追加
							if(card==HERO){//英雄の場合は転生札で復活
								m.message("2枚目の"+s.toString(BOY)+"が捨てられて公開処刑が発動しました。あなたは"+s.toString(card1)+"を引いて"+s.toString(card)+"を捨てられました。"+s.toString(HERO)+"が捨てられたので転生札で復活します。"+"今持っている手札である"+s.toString(xeno.getcard(me))+"を捨てて転生札である"+s.toString(xeno.getreincarnation())+"が手札になりました");
								os.write("OUTPUT".getBytes());
								os.write(crlf);
								os.write((s.toString(HERO)+"を捨てたので相手は転生札で復活しました").getBytes());
								os.write(crlf);
								xeno.discard(xeno.getcard(me));//今の手札捨てる
								xeno.setcard(me,xeno.getreincarnation());;//転生札で復活
							}else{//英雄以外のカードが捨てられたときの情報を出力
								m.message("2枚目の"+s.toString(BOY)+"が捨てられて公開処刑が発動しました。あなたは"+s.toString(card1)+"を引いて"+s.toString(card)+"を捨てられました");
							}
						}else{//山札がない場合
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write("山札がもうないので公開処刑は発動されません。".getBytes());
							os.write(crlf);
							m.message("2枚目の"+s.toString(BOY)+"が捨てられましたが山札がもうないので公開処刑は発動されませんでした。");
						}
					}else{//1枚目の場合
						boy_flag = true;
						m.message("1枚目の"+s.toString(BOY)+"が捨てられて何も起こりませんでした");
					}
					break;

					case SOLDIER://捜査
					os.write("INPUT".getBytes());
					os.write(crlf);
					os.write(("相手のカードの数字は何だと思いますか？>>").getBytes());
					os.write(crlf);
					receive = sok_br.readLine();
					num = Integer.parseInt(receive);
					card1 = num;
					if(card1==xeno.getcard(me)){//ゲーム終了
						os.write("OUTPUT".getBytes());
						os.write(crlf);
						os.write(("当たりました。").getBytes());
						os.write(crlf);
						os.write("OUTPUT".getBytes());
						os.write(crlf);
						os.write((xeno.getname(opposite)+"の勝ちです！おめでとうございます！").getBytes());
						os.write(crlf);
						m.message(s.toString(SOLDIER)+"が捨てられて捜査が発動しました。相手は"+s.toString(card1)+"と予想しあなたの手札は"+s.toString(xeno.getcard(me))+"なので相手は当てました。"+xeno.getname(opposite)+"の勝ちです。残念ながら負けてしまいました...");
						os.write("END".getBytes());
						os.write(crlf);
						System.exit(0);
					}else{
						os.write("OUTPUT".getBytes());
						os.write(crlf);
						os.write(("違いました。").getBytes());
						os.write(crlf);
						m.message(s.toString(SOLDIER)+"が捨てられて捜査が発動しました。相手は"+s.toString(card1)+"と予想しましたがあなたの手札は"+s.toString(xeno.getcard(me))+"なので相手は外しました");
					}
					break;

					case PREDICTOR://透視
					os.write("OUTPUT".getBytes());
					os.write(crlf);
					os.write(("透視が発動します。相手の手札は"+s.toString(xeno.getcard(me))+"です。").getBytes());
					os.write(crlf);
					m.message(s.toString(PREDICTOR)+"が捨てられて透視が発動しました。相手はあなたの手札が"+s.toString(xeno.getcard(me))+"であることを知りました");
					break;

					case MAIDEN://守護
					maiden_flag_opposite = true;
					m.message(s.toString(MAIDEN)+"が捨てられて守護が発動しました。相手は次のあなたのターンで相手に対する効果を発動しても無効化されます");
					break;

					case DEATH://疫病
					cnt=0;
					card1 = xeno.getcard(me);
					card2 = xeno.drawcard();
					card3 = card2;//シャッフル起きた場合の出力対策
					if(card2!=-1){//山札が空でなければ
						do{
							if(cnt>0){
								os.write("OUTPUT".getBytes());
								os.write(crlf);
								os.write(("入力が不適当です").getBytes());
								os.write(crlf);
							}
							os.write("INPUT".getBytes());
							os.write(crlf);
							os.write(("相手の手札の1枚目、2枚目どちらを捨てさせますか？>>").getBytes());
							os.write(crlf);
							receive = sok_br.readLine();
							num = Integer.parseInt(receive);
							card = num;
							cnt++;
						}while(card!=1&&card!=2);
						Random r = new Random();
						tmp = r.nextInt(2);
						if(tmp==0){//シャッフル
							int tmp2 = card1;
							card1 = card2;
							card2 = tmp2;
						}
						if(card==1)
							tmp = card1;
						else
							tmp = card2;
						xeno.setcard(me,(tmp==card1)?card2:card1);//捨てた結果持ち札の変更
						xeno.discard(tmp);//墓地に捨てるカード追加
						if(tmp==HERO){//英雄の場合は転生札で復活
							m.message(s.toString(DEATH)+"が捨てられて疫病が発動しました。あなたは"+s.toString(card3)+"を引いて"+s.toString(HERO)+"を捨てられました。"+s.toString(HERO)+"が捨てられたので転生札で復活します。"+"今持っている手札である"+s.toString(xeno.getcard(me))+"を捨てて転生札である"+s.toString(xeno.getreincarnation())+"が手札になりました");
							xeno.discard(xeno.getcard(me));//今の手札捨てる
							xeno.setcard(me,xeno.getreincarnation());;//転生札で復活
						}else{//英雄以外の場合の情報を出力
							m.message(s.toString(DEATH)+"が捨てられて疫病が発動しました。あなたは"+s.toString(card3)+"を引いて"+s.toString(tmp)+"を捨てられました");
						}
					}else{//山札がない場合
						os.write("OUTPUT".getBytes());
						os.write(crlf);
						os.write("山札がもうないので疫病は発動されません。".getBytes());
						os.write(crlf);
						m.message(s.toString(DEATH)+"が捨てられましたが山札がもうないので疫病は発動されませんでした。");
					}
					break;

					case NOBLE://対決
					if(noble_flag){//2回目は対決
						os.write("OUTPUT".getBytes());
						os.write(crlf);
						os.write(("2枚目の"+s.toString(NOBLE)+"が捨てられました、対決が発動します").getBytes());
						os.write(crlf);
						if(xeno.getcard(me)<xeno.getcard(opposite)){//ゲーム終了　相手の勝ち
							m.message("2枚目の"+s.toString(NOBLE)+"が捨てられて対決が発動しました。相手の手札は"+s.toString(xeno.getcard(opposite))+"でした。"+xeno.getname(opposite)+"の勝ちです。残念ながら負けてしまいました...");
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write(("相手の手札は"+s.toString(xeno.getcard(me))+"でした").getBytes());
							os.write(crlf);
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write((xeno.getname(opposite)+"の勝ちです。おめでとうございます！").getBytes());
							os.write(crlf);
							os.write("END".getBytes());
							System.exit(0);
						}else if(xeno.getcard(me)>xeno.getcard(opposite)){//ゲーム終了　自分の勝ち
							m.message("2枚目の"+s.toString(NOBLE)+"が捨てられて対決が発動しました。相手の手札は"+s.toString(xeno.getcard(opposite))+"でした。"+xeno.getname(me)+"の勝ちです。おめでとうございます！");
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write(("相手の手札は"+s.toString(xeno.getcard(me))+"でした").getBytes());
							os.write(crlf);
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write((xeno.getname(me)+"の勝ちです。残念ながら負けてしまいました...").getBytes());
							os.write(crlf);
							os.write("END".getBytes());
							os.write(crlf);
							System.exit(0);
						}else{//引き分け
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write(("相手の手札は"+s.toString(xeno.getcard(me))+"です").getBytes());
							os.write(crlf);
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write(("引き分けなのでゲームは終了せず続行されます").getBytes());
							os.write(crlf);
							m.message("2枚目の"+s.toString(NOBLE)+"が捨てられて対決が発動しました。相手の手札は"+s.toString(xeno.getcard(opposite))+"でした。引き分けなのでゲームは終了せず続行されます");
							
						}
					}else{//1回目は札を見せ合うだけ
						os.write("OUTPUT".getBytes());
						os.write(crlf);
						os.write(("1回目の"+s.toString(NOBLE)+"なので手札を見せ合います。相手の手札は"+s.toString(xeno.getcard(me))+"です").getBytes());
						os.write(crlf);
						m.message("1枚目の"+s.toString(NOBLE)+"が捨てられたので互いに手札を見せ合います。"+"相手の手札は"+s.toString(xeno.getcard(me))+"です");
					}
					break;

					case WISEMAN://選択
					wiseman_flag_opposite = true;
					m.message(s.toString(WISEMAN)+"が捨てられて次の相手のターンに選択が発動します");
					break;

					case SPIRIT://交換
					os.write("OUTPUT".getBytes());
					os.write(crlf);
					os.write((s.toString(xeno.getcard(opposite))+"を相手に渡しました").getBytes());
					os.write(crlf);
					os.write("OUTPUT".getBytes());
					os.write(crlf);
					os.write((s.toString(xeno.getcard(me))+"を手に入れました").getBytes());
					os.write(crlf);
					m.message(s.toString(SPIRIT)+"が捨てられて交換が発動しました。"+s.toString(xeno.getcard(me))+"を相手に渡して"+s.toString(xeno.getcard(opposite))+"を手に入れました");
					card1 = xeno.getcard(me);
					card2 = xeno.getcard(opposite);
					xeno.setcard(me,card2);
					xeno.setcard(opposite,card1);
					break;

					case EMPEROR://公開処刑
					card1 = xeno.getcard(me);
					card2 = xeno.drawcard();
					if(card2!=-1){//山札が空でなければ
						cnt=0;
						do{
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write(("相手は"+s.toString(card1)+"と"+s.toString(card2)+"を持っています").getBytes());
							os.write(crlf);
							if(cnt>0){
								os.write("OUTPUT".getBytes());
								os.write(crlf);
								os.write(("入力が不適当です。").getBytes());
								os.write(crlf);
							}
							os.write("INPUT".getBytes());
							os.write(crlf);
							os.write(("どちらを捨てさせますか>>").getBytes());
							os.write(crlf);
							receive = sok_br.readLine();
							num = Integer.parseInt(receive);
							card = num;
							cnt++;
						}while(card!=card1&&card!=card2);
						xeno.setcard(me,(card==card1)?card2:card1);//捨てた結果持ち札の変更
						xeno.discard(card);//墓地に捨てるカード追加
						if(card==HERO){//英雄の場合はゲーム終了
							os.write("OUTPUT".getBytes());
							os.write(crlf);
							os.write(("皇帝の効果で英雄を捨てたので"+xeno.getname(opposite)+"の勝ちです。おめでとうございます！").getBytes());
							os.write(crlf);
							m.message(s.toString(EMPEROR)+"が捨てられて公開処刑が発動しました。あなたは"+s.toString(card2)+"を引いて"+s.toString(HERO)+"を捨てられました。"+s.toString(HERO)+"が"+s.toString(EMPEROR)+"によって捨てられたのでゲームは終了です。"+xeno.getname(opposite)+"の勝ちです。残念ながら負けてしまいました...");
							os.write("END".getBytes());
							os.write(crlf);
							System.exit(0);
						}else{//英雄以外の場合の情報出力
							m.message(s.toString(EMPEROR)+"が捨てられて公開処刑が発動しました。あなたは"+s.toString(card2)+"を引いて"+s.toString(card)+"を捨てられました");
						}
					}else{//山札がない場合
						os.write("OUTPUT".getBytes());
						os.write(crlf);
						os.write("山札がもうないので公開処刑は発動されません。".getBytes());
						os.write(crlf);
						m.message(s.toString(EMPEROR)+"が捨てられましたが山札がもうないので公開処刑は発動されませんでした。");
					}
					break;

					case HERO://潜伏・転生
					//英雄は捨てられないのでこの分岐にくることはありえない
					break;

					case INVALID://自分が守護を発動した場合
					os.write("OUTPUT".getBytes());
					os.write(crlf);
					os.write(("相手が守護を発動していたので効果が無効化されました").getBytes());
					os.write(crlf);
					m.message("前のターンであなたは"+s.toString(MAIDEN)+"を捨てていたので守護が発動して効果が無効化されました");
					break;
				}
				/*カードの効果処理終了*/
				os.write("NEXT".getBytes());
				os.write(crlf);
				/*oppositeの処理終了*/
			}
			/*ゲーム終了*/
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		System.out.print("  Enterキーで終了");
		try{System.in.read();}catch(Exception e){}
	}
}