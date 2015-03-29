package priv.bajdcc.syntax.automata.npa;

/**
 * <p>
 * ��ȷ���������Զ���������
 * </p>
 * Move ------------ (Start,Epsilon,[Token]) ----> (End,Epsilon)<br/>
 * Shift ----------- (Start,Epsilon,Epsilon) ----> (End,Start)<br/>
 * Reduce ---------- (Start,[Status],Epsilon) ---> (End,Epsilon)<br/>
 * Left Recursion -- (Start,Epsilon,Epsilon) ----> (End,Epsilon)<br/>
 * Finish ---------- (Start,Epsilon,Epsilon) ----> (Epsilon,Epsilon) <br/>
 * 
 * @author bajdcc
 *
 */
public enum NPAEdgeType {
	MOVE("ת��"), SHIFT("�ƽ�"), REDUCE("��Լ"), LEFT_RECURSION("��ݹ�"), FINISH("����");

	private String name;

	NPAEdgeType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
