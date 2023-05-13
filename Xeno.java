/*ゲーム進行処理*/
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class Xeno{
	private Xeno_player p1,p2;
	private ArrayList<Integer> Deck = new ArrayList<>();//山札
	private ArrayList<Integer> Graveyard = new ArrayList<>();//墓地
	private int reincarnationcard;

	public Xeno(Xeno_player pp1,Xeno_player pp2){//第一引数が自分、第二引数が相手
		this.p1 = pp1;
		this.p2 = pp2;
	}

	public void start(){//ゲーム開始　山札シャッフルとプレイヤーにカード渡す
		for(int i = 0; i < 8; i++){
			/*1から8までは2枚ずつ*/
			this.Deck.add(i+1);
			this.Deck.add(i+1);
		}
		/*9と10は1枚ずつ*/
		this.Deck.add(9);
		this.Deck.add(10);
		//System.out.println(Deck);
		/*シャッフル*/
		Random r = new Random();
		for(int i = 0; i < 10000; i++){
			int tmp1 = r.nextInt(Deck.size());
			int tmp2 = r.nextInt(Deck.size());
			if(tmp1!=tmp2)
				Collections.swap(this.Deck,tmp1,tmp2);
		}
		//System.out.println(Deck);
		this.p1.setcard(Deck.get(0));
		this.Deck.remove(0);
		this.p2.setcard(Deck.get(0));
		this.Deck.remove(0);
		this.reincarnationcard = this.Deck.get(0);
		this.Deck.remove(0);
		//System.out.println(this.Deck);
	}

	public int drawcard(){//デッキからカードを1枚引く
		if(this.Deck.size()!=0){
			int card = this.Deck.get(0);
			this.Deck.remove(0);
			return card;
		}
		return -1;//デッキが空の場合
	}

	public void returncard(int x){//デッキにカードを1枚戻してシャッフル
		this.Deck.add(x);
		/*シャッフル*/
		Random r = new Random();
		for(int i = 0; i < 10000; i++){
			int tmp1 = r.nextInt(this.Deck.size());
			int tmp2 = r.nextInt(this.Deck.size());
			if(tmp1!=tmp2)
				Collections.swap(this.Deck,tmp1,tmp2);
		}
	}

	public String getname(Xeno_player p){//playerの名前取得
		return (p.getname()==this.p1.getname())?this.p1.getname():this.p2.getname();
	}

	public int getcard(Xeno_player p){//playerのカード取得
		return (p.getname()==this.p1.getname())?this.p1.getcard():this.p2.getcard();
	}

	public int getreincarnation(){//転生札取得
		return this.reincarnationcard;
	}

	public void setcard(Xeno_player p, int x){//プレイヤーのカードを変更
		if(p.getname()==this.p1.getname())
			this.p1.setcard(x);
		else
			this.p2.setcard(x);
	}

	public void discard(int x){//カードを墓地に追加
		Graveyard.add(x);
	}

	public int size(){//山札の残り枚数
		return this.Deck.size();
	}

	public boolean empty(){//山札が残っているかどうか
		if(this.Deck.size()==0)
			return true;
		else
			return false;
	}

	public String getGraveyard(){//墓地の情報取得
		return this.Graveyard.toString();
	}

	public void debug(){//動作確認のために使う
		System.out.println("debug start");
		System.out.println("山札の状態");
		System.out.println(Deck);
		System.out.println("墓地の状態");
		System.out.println(Graveyard);
		System.out.println("meのカード: "+this.getcard(p1));
		System.out.println("oppositeのカード: "+this.getcard(p2));
		System.out.println("debug end");
	}

}