/*プレイヤーが行う処理をまとめたクラス*/
public class Xeno_player{
	private String name;
	private int card;

	Xeno_player(String n){
		this.name = n;
	}
	public String getname(){
		return this.name;
	}
	public int getcard(){
		return this.card;
	}
	public void setcard(int x){
		this.card = x;
	}
}