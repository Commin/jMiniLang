package priv.bajdcc.syntax.automata.nga;

/**
 * ��ȷ�����ķ��Զ���������
 * 
 * @author bajdcc
 *
 */
public enum NGAEdgeType {
	EPSILON("Epsilon��"), TOKEN("�ս��"), RULE("���ս��");

	private String name;

	NGAEdgeType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
