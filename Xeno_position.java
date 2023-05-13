/*役職数値対応*/
public class Xeno_position{
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

	public String toString(int x){
		String s = "";
		switch(x){
			case BOY: s = "少年(1)";break;
			case SOLDIER: s = "兵士(2)";break;
			case PREDICTOR: s = "占い師(3)";break;
			case MAIDEN: s = "乙女(4)";break;
			case DEATH: s = "死神(5)";break;
			case NOBLE: s = "貴族(6)";break;
			case WISEMAN: s = "賢者(7)";break;
			case SPIRIT: s = "精霊(8)";break;
			case EMPEROR: s = "皇帝(9)";break;
			case HERO: s = "英雄(10)";break;
			case INVALID: s = "Error";break;
		}
		return s;
	}
}